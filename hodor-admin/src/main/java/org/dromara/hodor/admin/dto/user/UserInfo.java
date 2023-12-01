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

package org.dromara.hodor.admin.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * UserInfo
 *
 * @author tomgs
 * @since 1.0
 */
@Schema(title = "user info")
@Data
@Accessors(chain = true)
public class UserInfo {

    @Schema(name = "user id")
    private Long id;

    /**
     * 用户名
     */
    @Schema(name = "user name")
    private String username;
    /**
     * 联系邮箱
     */
    @Schema(name = "user email")
    private String email;
    /**
     * 联系电话
     */
    @Schema(name = "user phone")
    private String phone;
    /**
     * 租户id
     */
    @Schema(name = "user tenantId")
    private Long tenantId;
    /**
     * 创建时间
     */
    @Schema(name = "user create time")
    private Date createdAt;

    @Schema(name = "token")
    private String token;

    //@Schema(name = "permission info list")
    //private List<PermissionInfo> permissionInfoList;

}
