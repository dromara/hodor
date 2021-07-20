package org.dromara.hodor.common.concurrent;

/**
 * future callback
 *
 * @author tomgs
 * @date 2021/7/20 20:01
 * @since 1.0
 */
public interface FutureCallback<T> {

    /**
     * success callback
     * @param result callback result
     */
    void onSuccess(T result);

    /**
     * failure callback
     * @param cause throwable
     */
    void onFailure(Throwable cause);
}
