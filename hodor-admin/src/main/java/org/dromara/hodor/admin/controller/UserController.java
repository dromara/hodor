package org.dromara.hodor.admin.controller;

import cn.hutool.json.JSONUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.domain.UserFeedback;
import org.dromara.hodor.admin.service.impl.RoleService;
import org.dromara.hodor.admin.service.impl.UserService;
import org.dromara.hodor.common.utils.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    @RequestMapping("/listUser")
    public Result<PageInfo<User>> listUser(@RequestParam(value = "limit", required = true) int pageSize,
                                           @RequestParam(value = "offset", required = true) int start,
                                           @RequestParam(value = "search", required = false) String userName) {
        int pageNum = start / pageSize + 1;
        PageInfo<User> pageInfo = userService.queryUser(userName, pageNum, pageSize);
        return ResultUtil.success(pageInfo);

    }

    @RequestMapping("/saveUser")
    public Result<Boolean> saveUser(@RequestParam(value = "user", required = true) String userStr) {
        User user = JSONUtil.toBean(userStr, User.class);
        if (user.getRoleId() != null) {
            user.setRoleName(roleService.getRoleByIdOrName(user.getRoleId(), "").getRoleName());
        }
        boolean flag = userService.saveUser(user);
        return ResultUtil.success(flag);

    }

    @RequestMapping("/updateUser")
    public Result<Boolean> updateUser(@RequestParam(value = "user", required = true) String userStr) {
        User user = JSONUtil.toBean(userStr, User.class);
        if (user.getRoleId() != null) {
            user.setRoleName(roleService.getRoleByIdOrName(user.getRoleId(), "").getRoleName());
        }
        boolean flag = userService.updateUser(user);
        return ResultUtil.success(flag);

    }

    @RequestMapping("/checkUserName")
    public Result<Boolean> checkUserName(@RequestParam(value = "username", required = true) String userName) {
        boolean flag = userService.checkUserName(userName);
        return ResultUtil.success(flag);
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public Result<Boolean> addRecommend(@RequestParam(value = "username") String userName, @RequestParam(value = "content") String content) {
        if (StringUtils.isBlank(userName)) {
            return ResultUtil.errorWithArgs(Status.REQUEST_PARAMS_NOT_VALID_ERROR, "用户不能为空");
        }
        boolean flag = userService.addRecommend(userName, content);
        return ResultUtil.success(flag);
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public Result<PageInfo<UserFeedback>> listRecommend(@RequestParam(value = "offset") int start,
                                                        @RequestParam(value = "limit") int pageSize,
                                                        @RequestParam(value = "username", required = false) String userName) {
        List<User> user = userService.findUser(userName);
        if (user != null && user.size() > 0) {
            User dbUser = user.get(0);
            String roleName = dbUser.getRoleName();
            if (ServerConfigKeys.ADMINISTRATOR_ROLE.equalsIgnoreCase(roleName)) {
                userName = null;//userName==nul查询所有
            }
        }
        PageInfo<UserFeedback> pageInfo = new PageInfo<>();
        long total = userService.getRecommendSizeByUserName(userName);//统计
        List<UserFeedback> userFeedbacks = userService.listRecommend(userName, start, pageSize);
        pageInfo.setTotalPage((int) total);
        pageInfo.setTotalList(userFeedbacks);
        return ResultUtil.success(pageInfo);
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.DELETE)
    public Result<Boolean> deleteRecommend(@RequestParam(value = "id") int id, @RequestParam(value = "username") String userName) {
        boolean result = userService.deleteRecommend(id, userName);
        return ResultUtil.success(result);
    }
}
