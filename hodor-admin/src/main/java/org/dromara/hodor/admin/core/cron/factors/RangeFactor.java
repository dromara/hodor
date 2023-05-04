package org.dromara.hodor.admin.core.cron.factors;

import org.dromara.hodor.admin.core.cron.BuildInterface;

public class RangeFactor extends BaseFactor implements BuildInterface{

	private static final String MIDDLE_LINE = "-";
	private static final String SLASH = "/";
	
	private int interval;
	private int startAt;
	private int endAt;
	
	public RangeFactor(int start, int interval2, int end) {
		this.interval = interval2;
		this.startAt = start;
		this.endAt = end;
	}

	@Override
	public String build() {
		String factorRangeCron = null;
		if(super.build() == null){
			factorRangeCron = buildRangeFactor();
		}
		return factorRangeCron;
	}

	private String buildRangeFactor() {
		if (interval == 1 || interval == 0) {
			return new StringBuffer().append(startAt).append(MIDDLE_LINE).append(endAt)
					.toString();
		} else {
			return new StringBuffer().append(startAt).append(MIDDLE_LINE).append(endAt)
					.append(SLASH).append(interval).toString();
		}
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getStartAt() {
		return startAt;
	}

	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public int getEndAt() {
		return endAt;
	}

	public void setEndAt(int endAt) {
		this.endAt = endAt;
	}
	
}
