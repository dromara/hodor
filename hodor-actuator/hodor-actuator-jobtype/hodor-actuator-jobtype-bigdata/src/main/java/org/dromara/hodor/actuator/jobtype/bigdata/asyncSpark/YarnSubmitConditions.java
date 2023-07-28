package org.dromara.hodor.actuator.jobtype.bigdata.asyncSpark;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 提交到yarn的实体类
 * @since 1.0
 **/
public class YarnSubmitConditions {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务运行主类
     */
    private String mainClass;

    /**
     * 应用jar包
     */
    private String applicationJar;

    /**
     * 应用依赖jar包
     */
    private String[] dependJars;

    /**
     * yarn 依赖jar包
     */
    private String yarnJars;

    /**
     * yarn resource manager address
     */
    private String yarnResourcemanagerAddress;

    /**
     * 任务队列
     */
    private String queue;

    /**
     * namenode 地址
     */
    private String defaultFS;

    /**
     * hdfs nameservices
     */
    private String nameservices;

    /**
     * eg: nn1,nn2
     */
    private String namenodes;

    /**
     * eg: nn1.hadoop:9000,nn2.hadoop:9000
     */
    private String namenodeRpcAddress;

    /**
     * spark或者mr依赖的文件
     */
    private String[] files;

    /**
     * 应用参数
     */
    private List<String> otherArgs;

    /**
     * 其他属性
     */
    private Properties properties;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getApplicationJar() {
        return applicationJar;
    }

    public void setApplicationJar(String applicationJar) {
        this.applicationJar = applicationJar;
    }

    public String[] getDependJars() {
        return dependJars;
    }

    public void setDependJars(String[] dependJars) {
        this.dependJars = dependJars;
    }

    public String getYarnJars() {
        return yarnJars;
    }

    public void setYarnJars(String yarnJars) {
        this.yarnJars = yarnJars;
    }

    public String getYarnResourcemanagerAddress() {
        return yarnResourcemanagerAddress;
    }

    public void setYarnResourcemanagerAddress(String yarnResourcemanagerAddress) {
        this.yarnResourcemanagerAddress = yarnResourcemanagerAddress;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }

    public String getNameservices() {
        return nameservices;
    }

    public void setNameservices(String nameservices) {
        this.nameservices = nameservices;
    }

    public String getNamenodes() {
        return namenodes;
    }

    public void setNamenodes(String namenodes) {
        this.namenodes = namenodes;
    }

    public String getNamenodeRpcAddress() {
        return namenodeRpcAddress;
    }

    public void setNamenodeRpcAddress(String namenodeRpcAddress) {
        this.namenodeRpcAddress = namenodeRpcAddress;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public List<String> getOtherArgs() {
        return otherArgs;
    }

    public void setOtherArgs(List<String> otherArgs) {
        this.otherArgs = otherArgs;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        YarnSubmitConditions that = (YarnSubmitConditions) o;
        return Objects.equals(jobName, that.jobName) && Objects.equals(mainClass, that.mainClass) && Objects.equals(applicationJar, that.applicationJar) && Arrays.equals(dependJars, that.dependJars) && Objects.equals(yarnJars, that.yarnJars) && Objects.equals(yarnResourcemanagerAddress, that.yarnResourcemanagerAddress) && Objects.equals(queue, that.queue) && Objects.equals(defaultFS, that.defaultFS) && Objects.equals(nameservices, that.nameservices) && Objects.equals(namenodes, that.namenodes) && Objects.equals(namenodeRpcAddress, that.namenodeRpcAddress) && Arrays.equals(files, that.files) && Objects.equals(otherArgs, that.otherArgs) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(jobName, mainClass, applicationJar, yarnJars, yarnResourcemanagerAddress, queue, defaultFS, nameservices, namenodes, namenodeRpcAddress, otherArgs, properties);
        result = 31 * result + Arrays.hashCode(dependJars);
        result = 31 * result + Arrays.hashCode(files);
        return result;
    }
}
