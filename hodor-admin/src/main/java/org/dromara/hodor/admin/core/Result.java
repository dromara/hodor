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

package org.dromara.hodor.admin.core;

import java.io.Serializable;
import lombok.Data;

/**
 * result
 *
 * @param <T> T
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -6567814428394143641L;

    /**
     * successful
     */
    private boolean successful;

    /**
     * status
     */
    private Integer code;

    /**
     * message
     */
    private String msg;

    /**
     * data
     */
    private T data;

    public Result() {
    }

    public Result(Boolean successful, Integer code, String msg) {
        this.successful = successful;
        this.code = code;
        this.msg = msg;
    }

    public Result(Boolean successful, MsgCode msgCode) {
        this.successful = successful;
        if (msgCode != null) {
            this.code = msgCode.getCode();
            this.msg = msgCode.getMsg();
        }
    }

    public Result(Boolean successful, Integer code, String msg, T data) {
        this.successful = successful;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
