package org.dromara.hodor.admin.core.cron.factors;

import org.dromara.hodor.admin.core.cron.BuildInterface;

public class SpecifyFactor implements BuildInterface{

	private int[] specify;
	
	private BuildInterface buildInterface;
	
	public SpecifyFactor(int[] specify0){
		this.specify = specify0;
	}
	
	public SpecifyFactor(String[] specify0){
		int length = specify0.length;
		specify = new int[length];
		for(int i = 0; i < length; i++ ){
			specify[i] = Integer.parseInt(specify0[i]);
		}
	}
	
	public int[] getSpecify() {
		return specify;
	}

	public void setSpecify(int[] specify) {
		this.specify = specify;
	}

	public void setBuild(BuildInterface buildInterface){
		this.buildInterface = buildInterface;
	}
	
	@Override
	public String build() {
		if(buildInterface != null){
			return buildInterface.build();
		}
		
		StringBuffer buffer = new StringBuffer();
		for(int i : specify){
			if(buffer.length() != 0){
				buffer.append(",");
			}
			buffer.append(i);
		}
		return buffer.toString();  
	}

}
