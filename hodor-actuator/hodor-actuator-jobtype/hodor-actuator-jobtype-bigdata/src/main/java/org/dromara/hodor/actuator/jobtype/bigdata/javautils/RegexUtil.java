package org.dromara.hodor.actuator.jobtype.bigdata.javautils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author tomgs
 * @since 1.0
 **/
public class RegexUtil {

    /**
     * 返回带端口号的ResourcemanagerAddress
     *
     * @param yarnResourcemanagerAddress ResourcemanagerAddress地址 eg:0.0.0.0:123, 0.0.0.1:123
     * @return 返回地址数组 eg:[0.0.0.0:123, 0.0.0.1:123]
     */
    public static String[] getResourceHostPort(String yarnResourcemanagerAddress) {
        if (StringUtils.isBlank(yarnResourcemanagerAddress)) {
            return null;
        }
        String[] RMAddress = yarnResourcemanagerAddress.split(",");
        return RMAddress;
    }

    /**
     * 返回不带端口号的ResourcemanagerAddress
     *
     * @param yarnResourcemanagerAddress 0.0.0.0:123, 0.0.0.1:123
     * @return 返回地址列表 [0.0.0.0, 0.0.0.1]
     */
    public static List<String> getResourceHost(String yarnResourcemanagerAddress) {
        List<String> hosts = new ArrayList<>();

        if (StringUtils.isBlank(yarnResourcemanagerAddress)) {
            return hosts;
        }
        String[] RMAddress = yarnResourcemanagerAddress.split(",");
        for (String rmAddress : RMAddress) {
            String host = rmAddress.substring(0, rmAddress.indexOf(":"));
            hosts.add(host);
        }
        return hosts;
    }

}
