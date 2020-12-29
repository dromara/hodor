package org.dromara.hodor.common.loadbalance;

/**
 * load balance factory
 *
 * @author tomgs
 */
public final class LoadBalanceFactory {

    public static LoadBalance getLoadBalance(String type) {
        LoadBalanceEnum lb = getLoadBalanceEnum(type);
        if (lb == LoadBalanceEnum.RANDOM) {
            return new RandomLoadBalance();
        }
        return new RoundRobinLoadBalance();
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
