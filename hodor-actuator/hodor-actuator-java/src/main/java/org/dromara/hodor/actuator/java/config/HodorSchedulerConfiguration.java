package org.dromara.hodor.actuator.java.config;

import org.dromara.hodor.actuator.common.HodorActuatorManager;
import org.dromara.hodor.actuator.common.HodorApiClient;
import org.dromara.hodor.actuator.common.JobRegistrar;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.NodeManager;
import org.dromara.hodor.actuator.common.executor.ClientChannelManager;
import org.dromara.hodor.actuator.common.executor.ExecutorManager;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.actuator.java.HodorJavaActuatorInit;
import org.dromara.hodor.actuator.java.ServiceProvider;
import org.dromara.hodor.actuator.java.annotation.HodorSchedulerAnnotationBeanPostProcessor;
import org.dromara.hodor.actuator.java.core.JavaJobRegistrar;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.storage.db.HodorDataSource;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor scheduler configuration
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Configuration
@EnableConfigurationProperties(HodorActuatorJavaProperties.class)
public class HodorSchedulerConfiguration {

    private final HodorProperties properties;

    public HodorSchedulerConfiguration(final HodorActuatorJavaProperties hodorActuatorJavaProperties, final ApplicationContext applicationContext) {
        this.properties = hodorActuatorJavaProperties.getProperties();
        ServiceProvider.getInstance().setApplicationContext(applicationContext);
    }

    @Bean
    public HodorSchedulerAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new HodorSchedulerAnnotationBeanPostProcessor(jobRegistrar());
    }

    @Bean
    public HodorApiClient hodorApiClient() {
        return new HodorApiClient(properties);
    }

    @Bean
    public JobRegistrar jobRegistrar() {
        return new JavaJobRegistrar(hodorApiClient());
    }

    @Bean
    public DBOperator dbOperator() {
        DataSourceConfig dataSourceConfig = properties.getDataSourceConfig();
        HodorDataSource datasource = ExtensionLoader.getExtensionLoader(HodorDataSource.class, DataSourceConfig.class)
            .getProtoJoin("datasource", dataSourceConfig);
        return new DBOperator(datasource.getDataSource());
    }

    @Bean
    public RequestHandleManager requestHandleManger() {
        return new RequestHandleManager(properties, ExecutorManager.getInstance(), ClientChannelManager.getInstance(), dbOperator());
    }

    @Bean
    public JobExecutionPersistence jobExecutionPersistence() {
        return new JobExecutionPersistence(dbOperator());
    }

    @Bean
    public RemotingMessageSerializer remotingMessageSerializer() {
        return ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
    }

    @Bean
    public NodeManager nodeManager() {
        return new NodeManager(properties, ExecutorManager.getInstance());
    }

    @Bean
    public HodorActuatorManager hodorActuatorManager() {
        return new HodorActuatorManager(dbOperator(), requestHandleManger(), null, properties, hodorApiClient(), nodeManager(), jobRegistrar());
    }

    @Bean
    public HodorJavaActuatorInit hodorClientInit() {
        return new HodorJavaActuatorInit(jobRegistrar(), hodorActuatorManager());
    }

}
