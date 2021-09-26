package org.dromara.hodor.model.common;

import lombok.Data;

/**
 * hodor result
 *
 * @author tomgs
 */
@Data
public class HodorResult<R> {

    private boolean success;

    private String msg;

    private R data;

    public HodorResult(boolean success, String msg, R data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public static <R> HodorResult<R> success(String msg, R result) {
        return new HodorResult<>(true, msg, result);
    }

    public static <R> HodorResult<R> success(String msg) {
        return success(msg, null);
    }

    public static <R> HodorResult<R> failure(String msg) {
        return new HodorResult<>(false, msg, null);
    }

}
