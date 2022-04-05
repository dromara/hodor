package org.dromara.hodor.actuator.bigdata.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

public class ConfigException extends NestableRuntimeException {

    private static final long serialVersionUID = -7288830284122672209L;

    private String errorCode;
    private String errorDesc;

    public ConfigException(String errorCode) {
        super(errorCode);
    }

    public ConfigException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ConfigException(String errorCode, String errorDesc) {
        super(errorCode + ":" + errorDesc);
    }

    public ConfigException(String errorCode, String errorDesc, Throwable cause) {
        super(errorCode + ":" + errorDesc, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
