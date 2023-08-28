package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.dto.user.UserInfo;
import org.dromara.hodor.admin.entity.User;
import org.dromara.hodor.admin.service.UserService;
import org.dromara.hodor.common.utils.Utils.Beans;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录接口管理")
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<UserInfo> login(
        @RequestParam(value = "username") String username,
        @RequestParam(value = "password") String password,
        HttpSession session) {
        User user = userService.findUser(username, password);
        if (user != null) {
            UserInfo userInfo = new UserInfo();
            Beans.copyProperties(user, userInfo);
            session.setAttribute(ServerConfigKeys.USER_SESSION, userInfo);
            return ResultUtil.success(userInfo);
        } else {
            return ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, "用户名密码不匹配");
        }
    }

    @Operation(summary = "登出接口")
    @PostMapping("/logout")
    @ResponseBody
    public Result<Void> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResultUtil.success();
    }

}
