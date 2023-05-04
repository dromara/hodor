package org.dromara.hodor.admin.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.RolePermit;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.impl.UserService;
import org.dromara.hodor.admin.service.impl.PermitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AccountController {

    private final UserService userService;

    private final PermitService permitService;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(ServerConfigKeys.USER_SESSION);
        if (user == null) {
            return "login";
        }

        return "main";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(
        @RequestParam(value = "username", required = true) String username,
        @RequestParam(value = "password", required = true) String password,
        HttpSession session) {
        User user = userService.findUser(username, password);
        if (user != null) {
            log.info(String.format("【web信息】AccountController { %s } login success", user));
            session.setAttribute(ServerConfigKeys.USER_SESSION, user);
            List<RolePermit> list = permitService.getPermitListByRoleId(user.getRoleId());
            List<String> items = new ArrayList<>();
            for (RolePermit rolePermit : list) {
                items.add(rolePermit.getPermitIterm());
            }
            user.setPermitItems((items.toArray(new String[items.size()])));
            return Result.success(user);
        } else {
            return Result.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, "用户名密码不匹配");
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Result logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return Result.success();
    }

    @RequestMapping("checkSession")
    @ResponseBody
    public Result checkSession(HttpSession session) {
        if (session.getAttribute(ServerConfigKeys.USER_SESSION) != null) {
            return Result.success();
        }
        return Result.success(false);
    }
}
