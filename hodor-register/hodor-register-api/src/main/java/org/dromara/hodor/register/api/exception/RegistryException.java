package org.dromara.hodor.register.api.exception;

/**
 * 注册中心异常
 *
 * @author tomgs
 * @since 2020/6/28
 */
public class RegistryException extends RuntimeException {

    public RegistryException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public RegistryException(Exception cause) {
        super(cause);
    }

}
