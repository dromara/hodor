package org.dromara.hodor.admin.domain;

import java.io.Serializable;

/**
 * job检查报警配置信息
 * @author tomgs
 *
 */
public class AlarmCheckJob implements Serializable {

	private static final long serialVersionUID = 1361087100065827442L;
	
	private int id;
	
	/** job的组名 */
	private String groupName;
	
	/** 报警阀值 */
	private int threshold;
	
	/** 是否开启报警：1开启，0关闭*/
	private boolean enabled;
	
	/** 备注 */
	private String comment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "DBAlarmCheckJob{" +
				"id=" + id +
				", groupName='" + groupName + '\'' +
				", threshold=" + threshold +
				", enabled=" + enabled +
				", comment='" + comment + '\'' +
				'}';
	}

}
