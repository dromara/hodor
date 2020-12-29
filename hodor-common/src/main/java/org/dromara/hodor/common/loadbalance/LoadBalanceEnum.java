package org.dromara.hodor.common.loadbalance;

/**
 * load balance enum
 *
 * @author tomgs
 */
public enum LoadBalanceEnum {

    RANDOM("随机"),

    ROUND_ROBIN("轮询"),

	WEIGHT_ROUND_ROBIN("加权轮询");

    String desc;

    LoadBalanceEnum(String desc) {
        this.desc = desc;
    }

}

