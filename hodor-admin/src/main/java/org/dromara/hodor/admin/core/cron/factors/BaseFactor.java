package org.dromara.hodor.admin.core.cron.factors;

public class BaseFactor {

	public static final String allspecString = "*";
	
	protected static final String timedefault = allspecString;
	
	private static final String nospecString = "?";
	
	private boolean nospec = false;

	private boolean allspec = false;

	public boolean isNospec() {
		return nospec;
	}

	public void setNospec(boolean nospec) {
		this.nospec = nospec;
	}

	public boolean isAllspec() {
		return allspec;
	}

	public void setAllspec(boolean allspec) {
		this.allspec = allspec;
	}
	
	public String join(String... rangeString) {
		StringBuffer buffer = new StringBuffer();
		for(String str : rangeString){
			if(str == null){
				continue;
			}
			if(str.trim().length() != 0){
				if(buffer.length() != 0){
					buffer.append(",");
				}
				buffer.append(str);
			}
		}
		if(buffer.length() == 0){
			return null;
		}
		return buffer.toString();
	}
	
	public String build(){
		String baseFactorCron = null;
		if(isAllspec()){
			baseFactorCron = allspecString;
		}
		if(isNospec()){
			baseFactorCron = nospecString;
		}
		return baseFactorCron;
	}
}
