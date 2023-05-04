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

import java.util.Locale;
import java.util.Optional;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * status enum
 */
public enum Status {

    SUCCESS(0, "success", "成功"),
    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "request parameter {0} is not valid", "请求参数[{0}]无效"),
    REQUEST_BAD(10002, "request bad: {0}", "请求错误：[{0}]"),
    USER_NAME_EXIST(10003, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10004, "user name is null", "用户名不能为空"),
    RECEIVER_DATA_ERROR(10005, "data receive exception", "接收数据失败"),
    SEARCH_DATA_ERROR(10006, "search data failed, type {0}, message {1}", "搜索数据失败，原因：{0}，{1}"),
    CREATE_INDEX_ERROR(10007, "create index error, type {0}, message {1}", "创建索引失败，原因：{0}，{1}"),
    DELETE_INDEX_ERROR(10008, "delete index error, type {0}, message {1}", "创建索引失败，原因：{0}，{1}"),
    EXIST_INDEX_ERROR(10012, "exist index error, type {0}, message {1}", "获取文档失败，原因：{0}，{1}"),
    CREATE_INDEX_DOC_ERROR(10009, "create index doc error, type {0}, message {1}", "创建文档失败，原因：{0}，{1}"),
    GET_INDEX_DOC_ERROR(10010, "get index doc error, type {0}, message {1}", "获取文档失败，原因：{0}，{1}"),
    DELETE_INDEX_DOC_ERROR(10011, "delete index doc error, type {0}, message {1}", "获取文档失败，原因：{0}，{1}"),
    ;

    private final int code;
    private final String enMsg;
    private final String zhMsg;

    Status(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

    /**
     * Retrieve Status enum entity by status code.
     */
    public static Optional<Status> findStatusBy(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
