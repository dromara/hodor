package org.dromara.hodor.admin.controller;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        User user = (User) session.getAttribute(ServerConfigKeys.USER_SESSION);
        if (user == null) {
            return "login";
        }

        return "main";
    }

    @PostMapping("/login")
    public Result<User> login(
        @RequestParam(value = "username") String username,
        @RequestParam(value = "password") String password,
        HttpSession session) {
        User user = userService.findUser(username, password);
        if (user != null) {
            session.setAttribute(ServerConfigKeys.USER_SESSION, user);
            return ResultUtil.success(user);
        } else {
            return ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, "用户名密码不匹配");
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Result<Void> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResultUtil.success();
    }

    @RequestMapping("checkSession")
    @ResponseBody
    public Result<Boolean> checkSession(HttpSession session) {
        if (session.getAttribute(ServerConfigKeys.USER_SESSION) != null) {
            return ResultUtil.success();
        }
        return ResultUtil.success(false);
    }
}
