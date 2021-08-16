package org.dromara.hodor.common;

import cn.hutool.core.lang.Tuple;

/**
 * Tuple 2
 *
 * @author tomgs
 * @since 2021/8/16
 */
public class Tuple2<T1, T2> extends Tuple {

    private static final long serialVersionUID = -39004049673534799L;

    private final T1 first;

    private final T2 second;

    public Tuple2(T1 first, T2 second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

}
