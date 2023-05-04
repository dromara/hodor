package org.dromara.hodor.admin.core.cron.factors;

public class DayOfWeekFactor extends TimeFactor{

	/**
	 * 第几个周几
	 */
	NthWeekdayFactor nthWeekday;
	
	Integer lastweekdayindex;

	public NthWeekdayFactor getNthWeekday() {
		return nthWeekday;
	}

	public void setNthWeekday(NthWeekdayFactor nthWeekday) {
		this.nthWeekday = nthWeekday;
	}

	public Integer getLastweekdayindex() {
		return lastweekdayindex;
	}

	public void setLastweekdayindex(Integer lastweekdayindex) {
		this.lastweekdayindex = lastweekdayindex;
	}

	public String build(){
		String timeCron = super.build();
		String lastweekdayindexCron = null;
		if(lastweekdayindex != null){
			lastweekdayindexCron = lastweekdayindex + "L";
		}
		timeCron = super.join(timeCron, lastweekdayindexCron);
		if(nthWeekday != null){
			timeCron = super.join(timeCron,nthWeekday.build());
		}
		if(timeCron == null){
			timeCron = timedefault;
		}
		return timeCron;
	}
	
	public DayOfWeekFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	
}
