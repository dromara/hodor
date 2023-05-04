package org.dromara.hodor.admin.core;

import java.io.Serializable;


public class OpenApiResult implements Serializable {

    private static final long serialVersionUID = -145962211279772471L;

    private boolean isSuccess;
    private MsgDetail msg;
    private Object data;

    public OpenApiResult() {
        isSuccess = true;
    }

    public OpenApiResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public OpenApiResult(MsgCodeEnum msgCode, String errorDetails) {
        isSuccess = false;
        setMsg(msgCode, errorDetails);
    }

    public OpenApiResult(MsgCodeEnum msgCode, boolean isSuccess) {
        this.isSuccess = isSuccess;
        setMsg(msgCode, "");
    }

    public OpenApiResult(MsgCodeEnum msgCode) {
        this.isSuccess = false;
        setMsg(msgCode, "");
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public MsgDetail getMsg() {
        return msg;
    }

    public void setMsg(MsgDetail msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setMsg(MsgCodeEnum msgCode, String errorDetails) {
        msg = new MsgDetail(msgCode.code, msgCode.desc, errorDetails);
    }

    public void setMsg(MsgCodeEnum msgCode) {
        msg = new MsgDetail(msgCode.code, msgCode.desc, "");
    }

    @Override
    public String toString() {
        return "OpenApiResult [isSuccess=" + isSuccess + ", msg=" + msg
                + ", data=" + data + "]";
    }

    public class MsgDetail implements Serializable {

        /**
         * Comment for &lt;code&gt;serialVersionUID&lt;/code&gt;
         */
        private static final long serialVersionUID = -3834888530426845667L;
        private int code;
        private String desc;
        private String errorDetails;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getErrorDetails() {
            return errorDetails;
        }

        public void setErrorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
        }

        /**
         * MsgDetail Constructor.
         *
         * @param code
         * @param desc
         * @param errorDetails
         */
        public MsgDetail(int code, String desc, String errorDetails) {
            super();
            this.code = code;
            this.desc = desc;
            this.errorDetails = errorDetails;
        }

        /**
         * MsgDetail Constructor.
         */
        public MsgDetail() {
            super();
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            return "MsgDetail [code=" + code + ", desc=" + desc + ", errorDetails="
                    + errorDetails + "]";
        }


    }
}

