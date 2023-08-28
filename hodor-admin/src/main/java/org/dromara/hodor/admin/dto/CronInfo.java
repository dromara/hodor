package org.dromara.hodor.admin.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CronInfo implements Serializable {

    public static final String defaultSpecial = "hourly";

    private static final long serialVersionUID = 1L;

    private boolean allSeconds = true;

    private boolean allDays = true;

    private boolean allHours = true;

    private boolean allMins = true;

    private boolean allMonths = true;

    private boolean allWeekdays = true;

    private String chooseSpecial = "";

    private String customizeVal = "";

    private String special;

    private String[] days;

    private String[] mins;

    private String[] hours;

    private String[] months;

    private String[] weekdays;

    private String[] seconds;

    public static final CronInfo defaultValue() {
        CronInfo cronInfo = new CronInfo();
        cronInfo.setChooseSpecial("true");
        cronInfo.setSpecial(defaultSpecial);
        return cronInfo;
    }
}
