package org.dromara.hodor.admin.core.cron;

import org.dromara.hodor.admin.core.cron.factors.TimeFactor;

public class YearFactor extends TimeFactor {

	public YearFactor setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
}
