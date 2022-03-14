package org.dromara.hodor.actuator.bigdata.core;

import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.config.HodorActuatorBigdataProperties;
import org.dromara.hodor.actuator.bigdata.executor.CommonJobProperties;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.bigdata.executor.NoopJob;
import org.dromara.hodor.actuator.bigdata.register.BigdataJobRegister;
import org.dromara.hodor.actuator.common.ExecutableJob;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.utils.Props;
import org.dromara.hodor.common.storage.filesystem.FileStorage;
import org.dromara.hodor.common.storage.filesystem.LocalFileStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class BigdataExecutableJobTest {

    private JobTypeManager jobtypeManager;

    private FileStorage fileStorage;

    private final Logger logger = Logger.getLogger("BigdataExecutableJobTest");

    @Before
    public void setup() {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        jobtypeManager = new JobTypeManager(null, null, getClass().getClassLoader());
        fileStorage = new LocalFileStorage();
    }

    @Test
    public void testBase() throws Exception {
        String jobKey = "testJob";
        Props props = new Props();
        props.put(CommonJobProperties.JOB_TYPE, "noop");
        Job job = jobtypeManager.buildJobExecutor(jobKey, props, logger);
        Assert.assertTrue(job instanceof NoopJob);
    }

    @Test
    public void testJobExecute() throws Exception {
        HodorActuatorBigdataProperties properties = Mockito.mock(HodorActuatorBigdataProperties.class);
        BigdataJobRegister register = new BigdataJobRegister(properties);

        ExecutableJobContext context = Mockito.mock(ExecutableJobContext.class);
        ExecutableJob executableJob = register.provideExecutableJob(context);
        executableJob.execute(context);
    }


}
