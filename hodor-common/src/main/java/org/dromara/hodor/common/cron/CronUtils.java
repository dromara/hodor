package org.dromara.hodor.common.cron;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.CronPattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.dromara.hodor.common.utils.Utils.Assert;

/**
 * cron utils
 *
 * @author tomgs
 * @since 1.0
 */
public class CronUtils {

    /**
     * cron disabled这个用于不需要调度，但是需要注册的任务，可能用于任务的关联任务
     */
    public static final String CRON_DISABLED = "-";

    public static void assertValidCron(String cron, String errorMsgTemplate, Object... params) {
        Assert.notBlank(cron, "cron {} must be not null.", cron);
        if (isDisabledCron(cron)) {
            return;
        }
        if (!isValidCron(cron)) {
            throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
        }
    }

    public static boolean isValidCron(String cron) {
        try {
            new CronPattern(cron);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDisabledCron(String cron) {
        return CRON_DISABLED.equals(cron);
    }

    public static String parseCron(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }
}
