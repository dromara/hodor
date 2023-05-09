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
        return new Result<>(Status.SUCCESS.getCode(), Status.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg);
    }
    /**
     * Call this function if there is any error
     *
     * @param status status
     * @return result
     */
    public static <T> Result<T> error(Status status) {
        return new Result<>(status);
    }

    /**
     * Call this function if there is any error
     *
     * @param status status
     * @param args   args
     * @return result
     */
    public static <T> Result<T> errorWithArgs(Status status, Object... args) {
        return new Result<>(status.getCode(), MessageFormat.format(status.getMsg(), args));
    }

}
