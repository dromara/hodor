package org.dromara.hodor.common.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utils
 *
 * @author tomgs
 * @version 1.0
 */
public class DateUtils extends DateUtil {

    public static String PATTEN = "yyyy-MM-dd HH:mm:ss";

    public static String SIMPLE_PATTEN = "yyyyMMddHHmmss";

    public static long betweenMs(String startTime, String endTime) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(endTime) || org.apache.commons.lang3.StringUtils.isEmpty(startTime)) {
            return 0;
        }
        if (endTime.equals(startTime)) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(PATTEN);
        long processTime = 0;
        if (!(org.apache.commons.lang3.StringUtils.isNotEmpty(endTime) && StringUtils.isNotEmpty(startTime))) {
            return processTime;
        }
        try {
            java.util.Date end = sdf.parse(endTime);
            java.util.Date start = sdf.parse(startTime);
            processTime = (end.getTime() - start.getTime()) / 1000;
        } catch (ParseException e) {

        }
        if (processTime > 1400000000) {
            processTime = 0;
        }
        return processTime;
    }

    public static Date parseByYYYYMMDDPatten(String date) throws ParseException {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(SIMPLE_PATTEN);
        return df.parse(date);
    }
}
