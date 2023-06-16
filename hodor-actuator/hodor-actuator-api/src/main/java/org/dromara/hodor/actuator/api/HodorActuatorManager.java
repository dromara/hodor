package org.dromara.hodor.actuator.api;

import java.sql.SQLException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.core.HodorDatabaseSetup;
import org.dromara.hodor.actuator.api.core.NodeManager;
import org.dromara.hodor.actuator.api.executor.ClientChannelManager;
import org.dromara.hodor.actuator.api.executor.ExecutorManager;
import org.dromara.hodor.actuator.api.executor.ExecutorServer;
import org.dromara.hodor.actuator.api.executor.MsgSender;
import org.dromara.hodor.actuator.api.executor.RequestHandleManager;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.storage.db.HodorDataSource;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 * hodor client init
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorActuatorManager {

    private final String interval;

    private final HodorProperties properties;

    private final JobRegister jobRegister;

    private final DBOperator dbOperator;

    private final HodorApiClient hodorApiClient;

    private final ExecutorManager executorManager;

    private final RequestHandleManager requestHandleManager;

    private final RemotingMessageSerializer remotingMessageSerializer;

    private MsgSender msgSender;

    private ExecutorServer executorServer;

    private HodorDatabaseSetup hodorDatabaseSetup;

    private ScheduledThreadPoolExecutor heartbeatSenderService;

    public HodorActuatorManager(final HodorProperties properties,
                                final JobRegister jobRegister) {
        Assert.notNull(properties, "properties is required; it must not be null");
        Assert.notNull(jobRegister, "jobRegistrar is required; it must not be null");

        this.interval = System.getProperty("hodor.heartbeat.interval", "5000");
        this.properties = properties;
        this.jobRegister = jobRegister;
        this.dbOperator = dbOperator();
        this.executorManager = new ExecutorManager(properties);
        this.hodorApiClient = new HodorApiClient(properties);
        this.requestHandleManager = new RequestHandleManager(properties, jobRegister, executorManager,
            ClientChannelManager.getInstance(), dbOperator);
        this.remotingMessageSerializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        init();
    }

    private void init() {
        final NodeManager nodeManager = new NodeManager(properties, executorManager);
        this.executorServer = new ExecutorServer(requestHandleManager, remotingMessageSerializer, properties);
        this.msgSender = new MsgSender(hodorApiClient, nodeManager, jobRegister);
        this.hodorDatabaseSetup = new HodorDatabaseSetup(dbOperator);
        this.heartbeatSenderService = new ScheduledThreadPoolExecutor(2,
            HodorThreadFactory.create("hodor-heartbeat-sender", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private DBOperator dbOperator() {
        DataSourceConfig dataSourceConfig = properties.getDataSourceConfig();
        HodorDataSource datasource = ExtensionLoader.getExtensionLoader(HodorDataSource.class, DataSourceConfig.class)
            .getProtoJoin(dataSourceConfig.getType(), dataSourceConfig);
        return new DBOperator(datasource.getDataSource());
    }

    public void start() throws Exception {
        // init data
        initHodorClientData();
        // start executor server
        log.info("Hodor actuator starting executor server...");
        startExecutorServer();
        // start heartbeat sender server
        log.info("Hodor actuator starting heartbeat sender server...");
        startHeartbeatSender();
        // start register jobs after executor server start success
        log.info("Hodor actuator starting register jobs...");
        registerJobs();
    }

    public void registerJobs() throws Exception {
        hodorApiClient.registerJobTypeName(jobRegister.registerJobType());
        hodorApiClient.registerJobs(jobRegister.registerJobs());
    }

    private void initHodorClientData() throws SQLException {
        hodorDatabaseSetup.initTables();
        hodorDatabaseSetup.initData();
    }

    private void startExecutorServer() {
        Thread executorServerThread = new Thread(executorServer::start, "hodor-scheduler-executor-server");
        executorServerThread.setDaemon(true);
        executorServerThread.start();
    }

    private void startHeartbeatSender() {
        // first run for init
        MsgSender.HeartbeatSender heartbeatSender = msgSender.getHeartbeatSender();
        heartbeatSender.run();
        heartbeatSenderService.scheduleWithFixedDelay(() -> {
            while (heartbeatSenderService.getQueue().size() > 1) {
                heartbeatSenderService.getQueue().poll();
            }
            heartbeatSender.run();
        }, 3_000, Integer.parseInt(interval), TimeUnit.MILLISECONDS);
    }

    public void close() {
        // send offline notify
        log.info("Hodor actuator server shutdown ...");
        msgSender.getNodeOfflineSender().run();
        executorServer.close();
        heartbeatSenderService.shutdown();
        jobRegister.clear();
    }

}
