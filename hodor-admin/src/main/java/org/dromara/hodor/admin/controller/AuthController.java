package org.dromara.hodor.admin.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.service.AuthService;
import org.dromara.hodor.admin.service.impl.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/service/auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @RequestMapping("/auth")
    public Result<Void> auth(@RequestParam(value = "jobgroup", required = true) String groupName,
                       @RequestParam(value = "username", required = true) String username,
                       @RequestParam(value = "authtype", required = true) String authtype) throws Exception {
        authService.assign(username, groupName, authtype);
        return Result.success();
    }

    @RequestMapping("/unAuth")
    public Result unAuth(@RequestParam(value = "jobgroup", required = true) String groupName,
                         @RequestParam(value = "username", required = true) String username,
                         @RequestParam(value = "authtype", required = true) String authtype) {
        authService.unAssign(username, groupName, authtype);
        return new Result();
    }

    @RequestMapping("/getUserList")
    public List<String> getUserList() {
        return userService.getAllUsername();
    }

    @RequestMapping("/getJobgroup")
    public List<String> getJobgroup() {
        return authService.getAuthAvailableJobGroup();
    }

    @RequestMapping("/getAuthInfos")
    public boolean[] getAuthInfos(@RequestParam(value = "jobgroup", required = true) String jobgroup,
                                  @RequestParam(value = "username", required = true) String username) {
        return authService.getAuthByUsernameAndJobGroup(username, jobgroup);
    }
}
