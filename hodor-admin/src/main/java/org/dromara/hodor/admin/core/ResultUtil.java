/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.admin.core;

import java.text.MessageFormat;

/**
 * ResultUtil
 *
 * @author tomgs
 * @since 1.0
 */
public class ResultUtil {

    private ResultUtil() {}

    public static <T> Result<T> success(T data) {
        return new Result<>(true, MsgCode.SUCCESS.getCode(), MsgCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(false, code, msg);
    }
    /**
     * Call this function if there is any error
     *
     * @param msgCode status
     * @return result
     */
    public static <T> Result<T> error(MsgCode msgCode) {
        return new Result<>(false, msgCode);
    }

    /**
     * Call this function if there is any error
     *
     * @param msgCode status
     * @param args   args
     * @return result
     */
    public static <T> Result<T> errorWithArgs(MsgCode msgCode, Object... args) {
        return new Result<>(false, msgCode.getCode(), MessageFormat.format(msgCode.getMsg(), args));
    }

}
