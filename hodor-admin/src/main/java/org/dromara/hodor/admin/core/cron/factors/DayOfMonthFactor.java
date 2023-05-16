package org.dromara.hodor.admin.core.cron.factors;

public class DayOfMonthFactor extends TimeFactor {

	/**
	 * 倒数第几天
	 */
	Integer lastIndexDay;
	
	/**
	 * 几号最近的工作日
	 */
	Integer nearestWeekday;
	
	
	public Integer getLastIndexDay() {
		return lastIndexDay;
	}

	public void setLastIndexDay(Integer lastIndexDay) {
		this.lastIndexDay = lastIndexDay;
	}

	public Integer getNearestWeekday() {
		return nearestWeekday;
	}

	public void setNearestWeekday(Integer nearestWeekday) {
		this.nearestWeekday = nearestWeekday;
	}

	public String build(){
		String superbuild = super.build();
		String dayOfMonthFactorCron = null;
		if(superbuild != null){
			dayOfMonthFactorCron = superbuild;
			return dayOfMonthFactorCron;
		}else if(super.specifyFactor != null){
			dayOfMonthFactorCron = specifyFactor.build();
		}
		dayOfMonthFactorCron = super.join(dayOfMonthFactorCron, buildlastIndexDay(), buildnearestweekday(), buildRangeFactor());
		if(dayOfMonthFactorCron == null){
			dayOfMonthFactorCron = "*";
		}
		return dayOfMonthFactorCron;
	}

	private String buildRangeFactor() {
		if(super.rangeFactor == null){
			return null;			
		}else{
			return rangeFactor.build();
		}
	}

	private String buildnearestweekday() {
		if(nearestWeekday != null){
			return nearestWeekday + "W";
		}
		return null;
	}

	private String buildlastIndexDay() {
		if(lastIndexDay != null){
			return "L-" + lastIndexDay;
		}else{
			return null;
		}
	}

	public void setRangeFactor(int startAt, int interval, int endAt) {
		rangeFactor = new RangeFactor(startAt, interval, endAt);
	}
	
	public DayOfMonthFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
}
