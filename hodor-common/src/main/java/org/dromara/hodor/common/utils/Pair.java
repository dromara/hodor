package org.dromara.hodor.common.utils;

import cn.hutool.core.lang.Tuple;

/**
 * Tuple 2
 *
 * @author tomgs
 * @since 2021/8/16
 */
public class Pair<F, S> extends Tuple {

    private static final long serialVersionUID = -39004049673534799L;

    private final F first;

    private final S second;

    public Pair(F first, S second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

}
