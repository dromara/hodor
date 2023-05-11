/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.hodor.admin.exception;

import java.text.MessageFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.hodor.admin.core.MsgCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -7341723939198986717L;

    private int code;

    public ServiceException() {
        this(MsgCode.INTERNAL_SERVER_ERROR);
    }

    public ServiceException(MsgCode msgCode) {
        this(msgCode.getCode(), msgCode.getMsg());
    }

    public ServiceException(MsgCode msgCode, Object... formatter) {
        this(msgCode.getCode(), MessageFormat.format(msgCode.getMsg(), formatter));
    }

    public ServiceException(String message) {
        this(MsgCode.INTERNAL_SERVER_ERROR, message);
    }

    public ServiceException(int code, String message) {
        this(code, message, null);
    }

    public ServiceException(int code, String message, Exception cause) {
        super(message, cause);
        this.code = code;
    }

}
