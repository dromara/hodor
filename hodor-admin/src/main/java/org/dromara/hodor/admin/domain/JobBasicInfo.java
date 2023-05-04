package org.dromara.hodor.admin.domain;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;
import org.dromara.hodor.admin.dto.GraEntry;
import org.dromara.hodor.admin.enums.JobBasicStatus;
import org.dromara.hodor.common.utils.DateUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Data
public class JobBasicInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String uuid;//uuid，任务唯一标识
    private String groupName;//任务组名称
    private String jobName;//任务名称
    private String createdAt;//创建的时间
    private String updatedAt;//最后更新时间
    private String scheduleSip;//调度服务器IP地址
    private String businessSip;//业务服务器IP地址
    private String scheduleStart;//调度开始时间
    private String scheduleEnd;//调度结束时间
    private String executeStart;//执行开始时间
    private String executeEnd;//执行结束时间
    private long timeConsuming;//耗时
    private char currentStatus = JobBasicStatus.RUNNING.get();//当前状态
    private String errorLocation;//异常位置
    private String errorType;//异常类型
    private String errorReason;//异常原因
    private long timeOut;//超时
    private int isTimeout;//是否已经超时

    private String jobRecvTime;//rpc服务端接收任务的时间
    private int killprocess;//终止job标记,0:否,1:终止
    private String sharingRequestId;//分片job的requestId
    private String parentRequestId; //依赖执行时前job执行的requestId

    private String instanceId;
    private int encType;
    private byte[] detailedLog;
    private byte[] jobExeData;

    public JobBasicInfo() {
    }

    public JobBasicInfo(String uuid, String executeStart, String executeEnd, char currentStatus,
                        String errorReason, String errorType, String jobRecvTime, String sharingRequestId) throws ParseException {
        this.uuid = uuid;
        this.executeStart = executeStart;
        this.executeEnd = executeEnd;
        this.errorReason = errorReason;
        if (currentStatus != 'v') {
            this.currentStatus = currentStatus;
        }
        this.errorType = errorType;
        this.timeConsuming = DateUtils.betweenMs(executeStart, executeEnd);
        this.jobRecvTime = jobRecvTime;
        this.sharingRequestId = sharingRequestId;
    }

    public byte[] buildJobDependExeData(Map<String, List<GraEntry>> dependBasicJob) {
        JSONArray root = new JSONArray();
        for (Map.Entry<String, List<GraEntry>> entry : dependBasicJob.entrySet()) {
            JSONObject nodes = new JSONObject();
            nodes.putOnce("requestId", entry.getKey())
                    .putOnce("node", entry.getValue());
            root.add(nodes);
        }
        try {
            return root.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

}