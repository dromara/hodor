package org.dromara.hodor.common.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * lock util
 *
 * @author tomgs
 * @since 1.0
 */
public class LockUtil {

    /**
     * lock method
     *
     * @param rawLock 原始的锁
     * @param function 需要被锁函数
     * @param t 函数参数
     * @param <T> 函数参数类型
     * @param <R> 函数响应类型
     * @return 函数执行响应结果
     */
    public static  <T, R> R lockMethod(Lock rawLock, Function<T, R> function, T t) {
        rawLock.lock();
        try {
            return function.apply(t);
        } finally {
            rawLock.unlock();
        }
    }

    public static  <T1, T2, R> R lockMethod(Lock rawLock, BiFunction<T1, T2, R> function, T1 t1, T2 t2) {
        rawLock.lock();
        try {
            return function.apply(t1, t2);
        } finally {
            rawLock.unlock();
        }
    }

}
