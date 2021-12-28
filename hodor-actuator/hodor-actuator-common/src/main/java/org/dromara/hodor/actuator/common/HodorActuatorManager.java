package org.dromara.hodor.actuator.common;

import cn.hutool.core.lang.Assert;
import java.sql.SQLException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.HodorDatabaseSetup;
import org.dromara.hodor.actuator.common.core.NodeManager;
import org.dromara.hodor.actuator.common.executor.ClientChannelManager;
import org.dromara.hodor.actuator.common.executor.ExecutorManager;
import org.dromara.hodor.actuator.common.executor.ExecutorServer;
import org.dromara.hodor.actuator.common.executor.MsgSender;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.storage.db.HodorDataSource;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 * hodor client init
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Slf4j
public class HodorActuatorManager {

    private final String interval;

    private ExecutorServer executorServer;

    private final JobRegistrar jobRegistrar;

    private final HodorProperties properties;

    private final RequestHandleManager requestHandleManager;

    private final RemotingMessageSerializer remotingMessageSerializer;

    private final HodorApiClient hodorApiClient;

    private final DBOperator dbOperator;

    private MsgSender msgSender;

    private ScheduledThreadPoolExecutor heartbeatSenderService;

    private HodorDatabaseSetup hodorDatabaseSetup;

    public HodorActuatorManager(final HodorProperties properties,
                                final JobRegistrar jobRegistrar) {
        Assert.notNull(properties, "properties is required; it must not be null");
        Assert.notNull(jobRegistrar, "jobRegistrar is required; it must not be null");

        this.interval = System.getProperty("hodor.heartbeat.interval", "5000");
        this.properties = properties;
        this.jobRegistrar = jobRegistrar;
        this.dbOperator = dbOperator();
        this.hodorApiClient = new HodorApiClient(properties);
        this.requestHandleManager = new RequestHandleManager(properties, jobRegistrar, ExecutorManager.getInstance(),
            ClientChannelManager.getInstance(), dbOperator);
        this.remotingMessageSerializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        init();
    }

    private void init() {
        final NodeManager nodeManager = new NodeManager(properties, ExecutorManager.getInstance());
        this.executorServer = new ExecutorServer(requestHandleManager, remotingMessageSerializer, properties);
        this.msgSender = new MsgSender(hodorApiClient, nodeManager, jobRegistrar);
        this.hodorDatabaseSetup = new HodorDatabaseSetup(dbOperator);
        this.heartbeatSenderService = new ScheduledThreadPoolExecutor(2,
            HodorThreadFactory.create("hodor-heartbeat-sender", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private DBOperator dbOperator() {
        DataSourceConfig dataSourceConfig = properties.getDataSourceConfig();
        HodorDataSource datasource = ExtensionLoader.getExtensionLoader(HodorDataSource.class, DataSourceConfig.class)
            .getProtoJoin("datasource", dataSourceConfig);
        return new DBOperator(datasource.getDataSource());
    }

    public void start() throws Exception {
        // init data
        initHodorClientData();
        // start executor server
        log.info("HodorClient starting executor server...");
        startExecutorServer();
        // start heartbeat sender server
        log.info("HodorClient starting heartbeat sender server...");
        startHeartbeatSender();
        // start register jobs after executor server start success
        log.info("HodorClient starting register jobs...");
        registerJobs();
        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void registerJobs() throws Exception {
        hodorApiClient.registerJobs(jobRegistrar.registerJobs());
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
        // 第一次初始化
        MsgSender.HeartbeatSender heartbeatSender = msgSender.getHeartbeatSender();
        heartbeatSender.run();
        heartbeatSenderService.scheduleWithFixedDelay(() -> {
            // 可能会出现大量的不必要任务，产生心跳风暴，直接丢弃
            while (heartbeatSenderService.getQueue().size() > 1) {
                heartbeatSenderService.getQueue().poll();
            }
            heartbeatSender.run();
        }, 3_000, Integer.parseInt(interval), TimeUnit.MILLISECONDS);
    }

    public void close() {
        log.info("Shutdown server ...");
        // 发送下线通知
        msgSender.getNodeOfflineSender().run();
        // 关闭相应服务
        executorServer.close();
        heartbeatSenderService.shutdown();
        jobRegistrar.clear();
    }

}
