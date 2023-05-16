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

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

/**
 * Exception Handler
 */
@RestControllerAdvice
@ResponseBody
@Slf4j
public class AdminExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result<Object> exceptionHandler(ServiceException e, HandlerMethod hm) {
        log.error("ServiceException: {}", e.getMessage(), e);
        return ResultUtil.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public Result<Object> badRequestExceptionHandler(Exception e, HandlerMethod hm) {
        log.error("BadRequestException: {}", e.getMessage(), e);
        return ResultUtil.errorWithArgs(MsgCode.REQUEST_BAD, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception e, HandlerMethod hm) {
        log.error("Exception: {}", e.getMessage(), e);
        return ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
