package org.dromara.hodor.admin.domain;

import java.io.Serializable;

/**
 * ZooKeeper上job属性实体映射
 *
 * @author tomgs
 * @since 1.0
 */
public class Job implements Serializable {

    private static final long serialVersionUID = -7532210092518686666L;

    private String groupName;

    private String jobName;

    private String cronExpression;

    private String jobPath;

    private String parameters = "";

    private int shardingCount;

    private int fetchDataCount;

    private String shardingItemParameters;

    private boolean failover;

    private boolean misfire;

    /**
     * 是否马上开始执行（比如设置时间是5分钟，那么5分钟后才执行），如果设置为true，则现在执行一次
     */
    private boolean fireNow;

    /**
     * 任务状态0未激活 1可运行 2正在运行 3暂停
     */
    private int jobStatus = 0;

    private String desc = "";

    /**
     * 执行情况记录
     */
    private JobExecution exe;

    /**
     * 该job被分配到slave的IP
     */
    private String slaveIp;

    /**
     * 任务上次执行时间
     */
    private String lastFireTime;

    /**
     * 任务结束时间
     */
    private String endTime;

    /**
     * 是否并行,false:否,true:是
     */
    private boolean ifParallel;

    /**
     * 超时时间,单位是s
     */
    private long timeOut;

    /**
     * 对group下的job进行分类
     */
    private String category = "";

    /**
     * 主机ip
     */
    private String host;

    /**
     * 主机端口
     */
    private int port;


    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;


    /**
     * 是否广播模式
     */
    private boolean ifBroadcast = false;

    public long getTimeOut() {
        // 如果没有设置超时时间,则使用默认超时时间10分钟
        if (timeOut == 0) {
            return 600;
        }
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public String getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(String lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobPath() {
        return jobPath;
    }

    public void setJobPath(String jobPath) {
        this.jobPath = jobPath;
    }

    public String getParameters() {
        return parameters == null ? "" : parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getShardingCount() {
        return shardingCount;
    }

    public void setShardingCount(int shardingCount) {
        this.shardingCount = shardingCount;
    }

    public int getFetchDataCount() {
        return fetchDataCount;
    }

    public void setFetchDataCount(int fetchDataCount) {
        this.fetchDataCount = fetchDataCount;
    }

    public String getShardingItemParameters() {
        return shardingItemParameters;
    }

    public void setShardingItemParameters(String shardingItemParameters) {
        this.shardingItemParameters = shardingItemParameters;
    }

    public boolean isFailover() {
        return failover;
    }

    public void setFailover(boolean failover) {
        this.failover = failover;
    }

    public boolean isMisfire() {
        return misfire;
    }

    public void setMisfire(boolean misfire) {
        this.misfire = misfire;
    }

    public boolean isFireNow() {
        return fireNow;
    }

    public void setFireNow(boolean fireNow) {
        this.fireNow = fireNow;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getDesc() {
        return desc == null ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public JobExecution getExe() {
        return exe;
    }

    public void setExe(JobExecution exe) {
        this.exe = exe;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Job() {

    }

    public Job(String host, int port, String groupName,
               String jobName, String filePath, String className,
               String methodName, String cronExpression) {
        super();
        this.host = host;
        this.port = port;
        this.groupName = groupName;
        this.jobName = jobName;
        this.filePath = filePath;
        this.className = className;
        this.methodName = methodName;
        this.cronExpression = cronExpression;


    }

    public Job(String groupName, String jobName) {
        super();
        this.groupName = groupName;
        this.jobName = jobName;
    }

    /**
     * group和jobname唯一确定一个job
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
        return result;
    }

    // 根据group和jobname唯一确定一个job,故equals和hashcode必须以这两个值重写
    @Override
    public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
        Job other = (Job) obj;
        if (groupName == null) {
			if (other.groupName != null) {
				return false;
			}
        } else if (!groupName.equals(other.groupName)) {
			return false;
		}
        if (jobName == null) {
			if (other.jobName != null) {
				return false;
			}
        } else if (!jobName.equals(other.jobName)) {
			return false;
		}
        return true;
    }

    public String getSlaveIp() {
        return slaveIp;
    }

    public void setSlaveIp(String slaveIp) {
        this.slaveIp = slaveIp;
    }

    public boolean isIfParallel() {
        return ifParallel;
    }

    public void setIfParallel(boolean ifParallel) {
        this.ifParallel = ifParallel;
    }

    public boolean isIfBroadcast() {
        return ifBroadcast;
    }

    public void setIfBroadcast(boolean ifBroadcast) {
        this.ifBroadcast = ifBroadcast;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Job [groupName=" + groupName + ", jobName=" + jobName + ", cronExpression=" + cronExpression
            + ", jobPath=" + jobPath + ", parameters=" + parameters + ", shardingCount=" + shardingCount
            + ", fetchDataCount=" + fetchDataCount + ", shardingItemParameters=" + shardingItemParameters
            + ", failover=" + failover + ", misfire=" + misfire + ", fireNow=" + fireNow + ", jobStatus="
            + jobStatus + ", desc=" + desc + ", exe=" + exe + ", slaveIp=" + slaveIp + ", lastFireTime="
            + lastFireTime + ", endTime=" + endTime + ", ifParallel=" + ifParallel + ", timeOut=" + timeOut
            + ", category=" + category + ", host=" + host + ", port=" + port + ", filePath=" + filePath
            + ", className=" + className + ", methodName=" + methodName + ", ifBroadcast=" + ifBroadcast + "]";
    }

}
