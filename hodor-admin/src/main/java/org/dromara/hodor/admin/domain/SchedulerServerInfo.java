package org.dromara.hodor.admin.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SchedulerServerInfo implements Serializable, Comparable<SchedulerServerInfo> {

    private static final long serialVersionUID = 2652462087308053893L;

    private String ip;

    private String hostName;

    private int weight;

    private int effectiveWeight;

    private int currentWeight;

    private boolean isMaster;

    private boolean isActive;

    private boolean down = false;

    private Date checkedDate;

    private Map<String, List<String>> groupJobMap;

    public SchedulerServerInfo(String ip) {
        this.ip = ip;
    }

    public SchedulerServerInfo(String ip, int weight) {
        super();
        this.ip = ip;
        this.weight = weight;
        this.effectiveWeight = this.weight;
        this.currentWeight = 0;
        if (this.weight < 0) {
            this.down = true;
        } else {
            this.down = false;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getEffectiveWeight() {
        return effectiveWeight;
    }

    public void setEffectiveWeight(int effectiveWeight) {
        this.effectiveWeight = effectiveWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }

    public Map<String, List<String>> getGroupJobMap() {
        return groupJobMap;
    }

    public void setGroupJobMap(Map<String, List<String>> groupJobMap) {
        this.groupJobMap = groupJobMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        return result;
    }

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
        SchedulerServerInfo other = (SchedulerServerInfo) obj;
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!ip.equals(other.ip)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(SchedulerServerInfo o) {
        if (this.ip.hashCode() < o.ip.hashCode()) {
            return -1;
        }
        if (this.ip.hashCode() > o.ip.hashCode()) {
            return 1;
        }
        return 0;
    }

}

