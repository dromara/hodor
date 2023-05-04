package org.dromara.hodor.admin.core.cron;

public class CronResult {

	private String crontab;
	
	private String quartzCronExpression;
	
	private boolean isTransferSuccess;

	private String message;
	
	public String getCrontab() {
		return crontab;
	}

	public void setCrontab(String crontab) {
		this.crontab = crontab;
	}

	public String getQuartzCronExpression() {
		return quartzCronExpression;
	}

	public void setQuartzCronExpression(String quartzCronExpression) {
		this.quartzCronExpression = quartzCronExpression;
	}

	public boolean isTransferSuccess() {
		return isTransferSuccess;
	}

	public void setTransferSuccess(boolean isTransferSuccess) {
		this.isTransferSuccess = isTransferSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CronResult{" +
				"crontab='" + crontab + '\'' +
				", quartzCronExpression='" + quartzCronExpression + '\'' +
				", isTransferSuccess=" + isTransferSuccess +
				", message='" + message + '\'' +
				'}';
	}
}
