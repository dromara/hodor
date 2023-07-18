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

package org.dromara.hodor.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dromara.hodor.admin.config.AdminProperties;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.UserInfo;
import org.dromara.hodor.admin.entity.User;
import org.dromara.hodor.admin.service.UserService;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Beans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * LoginInterceptor
 *
 * @author tomgs
 * @since 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    private final AdminProperties adminProperties;

    public LoginInterceptor(final AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // check api-key
        final String key = request.getHeader("API-KEY");
        if (StringUtils.equals(adminProperties.getApiKey(), key)) {
            final User user = userService.findUser(adminProperties.getRole());
            final UserInfo userInfo = Beans.copyProperties(user, UserInfo.class);
            UserContext.setUser(userInfo);
            return true;
        }

        // check session
        Object userSession = request.getSession().getAttribute(ServerConfigKeys.USER_SESSION);
        if (userSession == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter()
                .print("API Unauthorized");
            return false;
        }

        UserContext.setUser((UserInfo) userSession);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.removeUser();
    }
}
