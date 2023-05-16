package org.dromara.hodor.admin.core.cron.factors;

import org.dromara.hodor.admin.core.cron.BuildInterface;

public class TimeFactor extends BaseFactor implements BuildInterface{

	protected RangeFactor rangeFactor;
	
	protected SpecifyFactor specifyFactor;
	
	protected String defaultValue;
	public TimeFactor(){}
	public TimeFactor(String dv){
		this.defaultValue = dv;
	}
	public TimeFactor(RangeFactor rf, SpecifyFactor sf){
		this.rangeFactor = rf;
		this.specifyFactor = sf;
	}
	
	public RangeFactor getRangeFactor() {
		return rangeFactor;
	}

	public void setRangeFactor(RangeFactor rf) {
		this.rangeFactor = rf;
	}

	public SpecifyFactor getSpecifyFactor() {
		return specifyFactor;
	}

	public void setSpecifyFactor(SpecifyFactor specifyFactor) {
		this.specifyFactor = specifyFactor;
	}

	public String build(){
		String superbuild = super.build();
		if(superbuild != null){
			return superbuild;
		}
		return join(rangeFactor, specifyFactor);
	}
	
	public String setDefault(String timeCron){
		if(timeCron == null){
			timeCron = timedefault;
		}
		return timeCron;
	}
	
	public String join(BuildInterface ...buildInterfaces){
		String buidCron = null;
		for(BuildInterface buildInterface : buildInterfaces){
			if(buildInterface != null){
				buidCron = super.join(buidCron, buildInterface.build());
			}
		}
		if(buidCron == null){
			return defaultValue;
		}
		return buidCron;
		
	}
	
	public TimeFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	
}