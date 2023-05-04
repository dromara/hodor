package org.dromara.hodor.admin.domain;

import java.io.Serializable;
import java.util.Arrays;

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

    public boolean isAllDays() {
        return allDays;
    }

    public void setAllDays(boolean allDays) {
        this.allDays = allDays;
    }

    public boolean isAllHours() {
        return allHours;
    }

    public void setAllHours(boolean allHours) {
        this.allHours = allHours;
    }

    public boolean isAllMins() {
        return allMins;
    }

    public void setAllMins(boolean allMins) {
        this.allMins = allMins;
    }

    public boolean isAllMonths() {
        return allMonths;
    }

    public void setAllMonths(boolean allMonths) {
        this.allMonths = allMonths;
    }

    public boolean isAllWeekdays() {
        return allWeekdays;
    }

    public void setAllWeekdays(boolean allWeekdays) {
        this.allWeekdays = allWeekdays;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public boolean isAllSeconds() {
        return allSeconds;
    }

    public void setAllSeconds(boolean allSeconds) {
        this.allSeconds = allSeconds;
    }


    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
    }

    public String[] getMins() {
        return mins;
    }

    public void setMins(String[] mins) {
        this.mins = mins;
    }

    public String[] getHours() {
        return hours;
    }

    public void setHours(String[] hours) {
        this.hours = hours;
    }

    public String[] getMonths() {
        return months;
    }

    public void setMonths(String[] months) {
        this.months = months;
    }

    public String[] getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String[] weekdays) {
        this.weekdays = weekdays;
    }

    public String[] getSeconds() {
        return seconds;
    }

    public void setSeconds(String[] seconds) {
        this.seconds = seconds;
    }

    public String getChooseSpecial() {
        return chooseSpecial;
    }

    public String getCustomizeVal() {
        return customizeVal;
    }

    public void setCustomizeVal(String customizeVal) {
        this.customizeVal = customizeVal;
    }

    public void setChooseSpecial(String chooseSpecial) {
        this.chooseSpecial = chooseSpecial;
    }

    @Override
    public String toString() {
        return "CronInfo [allSeconds=" + allSeconds + ", allDays=" + allDays + ", allHours=" + allHours + ", allMins="
            + allMins + ", allMonths=" + allMonths + ", allWeekdays=" + allWeekdays + ", chooseSpecial="
            + chooseSpecial + ", special=" + special + ", days=" + Arrays.toString(days) + ", mins="
            + Arrays.toString(mins) + ", hours=" + Arrays.toString(hours) + ", months=" + Arrays.toString(months)
            + ", weekdays=" + Arrays.toString(weekdays) + ", seconds=" + Arrays.toString(seconds) + "]";
    }

    public static final CronInfo defaultValue() {
        CronInfo cronInfo = new CronInfo();
        cronInfo.setChooseSpecial("true");
        cronInfo.setSpecial(defaultSpecial);
        return cronInfo;
    }
}
