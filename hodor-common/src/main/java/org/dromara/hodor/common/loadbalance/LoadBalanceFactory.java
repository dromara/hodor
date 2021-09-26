package org.dromara.hodor.common.loadbalance;

import java.util.HashMap;
import java.util.Map;

/**
 * load balance factory
 *
 * @author tomgs
 */
public final class LoadBalanceFactory {

    private static final Map<String, LoadBalance> loadBalanceMap = new HashMap<>();

    public static LoadBalance getLoadBalance(String type) {
        return loadBalanceMap.computeIfAbsent(type, k -> {
            LoadBalanceEnum lb = getLoadBalanceEnum(type);
            if (lb == LoadBalanceEnum.RANDOM) {
                return new RandomLoadBalance();
            }
            return new RoundRobinLoadBalance();
        });
    }

    /**
     * 默认为轮询方式
     */
    private static LoadBalanceEnum getLoadBalanceEnum(String type) {
        try {
            return LoadBalanceEnum.valueOf(type);
        } catch (Exception e) {
            return LoadBalanceEnum.ROUND_ROBIN;
        }
    }

}
