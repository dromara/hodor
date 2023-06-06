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
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * MsgCode enum
 * <p>
 * 10 客户、服务端异常、参数解析失败
 * 20 任务相关异常
 * 30 openapi相关异常
 *
 * @author tomgs
 * @since 1.0
 */
public enum MsgCode {

    SUCCESS(0, "success", "成功"),
    // 服务端
    INTERNAL_SERVER_ERROR(10000, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "request parameter {0} is not valid", "请求参数[{0}]无效"),
    REQUEST_BAD(10002, "request bad: {0}", "请求错误：[{0}]"),
    USER_NAME_EXIST(10003, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10004, "user name is null", "用户名不能为空"),
    NO_OPERATION_PERMISSION(10005, "no operation permission", "没有操作权限"),
    LOGIN_EXPIRED(10006, "login expired", "登陆已失效"),
    USER_NOT_LOGIN(10007, "user not login", "用户未登录"),
    JSON_PARSE_ERROR(10008, "json parsing failed", "JSON解析失败"),

    // 任务相关
    INVALID_JOB(20001, "invalid job", "非法任务"),
    INVALID_JOB_ID(20002, "invalid job id {0}", "非法任务id {0}"),
    JOB_PARSE_ERROR(20003, "job parsing failed", "任务解析失败"),
    EMPTY_JOB(20004, "jobs is empty", "任务为空"),
    EMPTY_CRON(20005, "cron is empty", "Cron表达式为空"),
    INVALID_CRON(20006, "Invalid cron", "非法Cron表达式"),
    FEWER_FIELDS(20007, "groupName, jobName, jobUser can not be empty", "groupName, jobName, jobUser 不能为空"),
    CREATE_JOB_ERROR(20008, "create job error, msg {0}", "创建任务失败，信息 {0}"),
    UPDATE_JOB_ERROR(20009, "update job error, msg {0}", "更新任务失败，信息 {0}"),
    DELETE_JOB_ERROR(20010, "delete job error, msg {0}", "更新任务失败，信息 {0}"),
    PAUSE_JOB_ERROR(20011, "pause job error, msg {0}", "暂停任务失败，信息 {0}"),
    STOP_JOB_ERROR(20012, "stop job error, msg {0}", "停止任务失败，信息 {0}"),
    RESUME_JOB_ERROR(20013, "resume job error, msg {0}", "恢复任务失败，信息 {0}"),
    EXECUTE_JOB_ERROR(20013, "execute job error, msg {0}", "执行任务失败，信息 {0}"),

    // openapi
    INVALID_SIGN(30001, "the signature is invalid", "非法签名"),
    REQUEST_TIMEOUT(30002, "request timed out", "请求超时"),
    INVALID_APP_KEY(30003, "appKey is invalid", "非法appKey"),
    EMPTY_PARAM(30004, "parameter {0} is empty", "参数[{0}]为空");

    private final int code;

    private final String enMsg;

    private final String zhMsg;

    MsgCode(int code, String enMsg, String zhMsg) {
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
}
