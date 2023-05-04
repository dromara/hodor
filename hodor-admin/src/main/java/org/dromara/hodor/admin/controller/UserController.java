package org.dromara.hodor.admin.controller;

import cn.hutool.json.JSONUtil;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.domain.UserFeedback;
import org.dromara.hodor.admin.service.impl.RoleService;
import org.dromara.hodor.admin.service.impl.UserService;
import org.dromara.hodor.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/service/user")
public class UserController {

//    @Autowired
//    @Qualifier("dbUserActionRecordService")
//    DBUserActionRecordService userActionRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // limit:10
    // offset:5
//    @RequestMapping("/userActionList")
//    public PageResult userActionList(@RequestParam(value = "limit", required = true) int limit,
//                                        @RequestParam(value = "offset", required = true) int offset,
//                                        @RequestParam(value = "search", required = false) String search,
//                                        HttpSession session) {
//        if (search != null)
//            search = search.trim();
//        final DBUser user = (DBUser) session.getAttribute(SystemDefault.USER_SESSION_KEY);
//        if (PermitUtil.isAdmin(user.getUsername())) {
//            PageResult pageResult = new PageResult();
//            pageResult.setRows(userActionRecordService.selectUserActionRecordList(limit, offset, search, null));
//            pageResult.setTotal(userActionRecordService.selectUserActionRecordCount(search, null));
//            return pageResult;
//        }
//
//        PageResult pageResult = new PageResult();
//        pageResult.setRows(userActionRecordService.selectUserActionRecordList(limit, offset, search, user.getUsername()));
//        pageResult.setTotal(userActionRecordService.selectUserActionRecordCount(search, user.getUsername()));
//        return pageResult;
//    }

    @RequestMapping("/listUser")
    public Result<PageInfo<User>> listUser(@RequestParam(value = "limit", required = true) int pageSize,
                                           @RequestParam(value = "offset", required = true) int start,
                                           @RequestParam(value = "search", required = false) String userName) {
        int pageNum = start / pageSize + 1;
        PageInfo<User> pageInfo = userService.queryUser(userName, pageNum, pageSize);
        return Result.success(pageInfo);

    }

    @RequestMapping("/saveUser")
    public Result saveUser(@RequestParam(value = "user", required = true) String userStr) {
        User user = JSONUtil.toBean(userStr, User.class);
        if (user.getRoleId() != null) {
            user.setRoleName(roleService.getRoleByIdOrName(user.getRoleId(), "").getRoleName());
        }
        boolean flag = userService.saveUser(user);
        return Result.success(flag);

    }

    @RequestMapping("/updateUser")
    public Result updateUser(@RequestParam(value = "user", required = true) String userStr) {
        User user = JSONUtil.toBean(userStr, User.class);
        if (user.getRoleId() != null) {
            user.setRoleName(roleService.getRoleByIdOrName(user.getRoleId(), "").getRoleName());
        }
        boolean flag = userService.updateUser(user);
        return Result.success(flag);

    }

    @RequestMapping("/checkUserName")
    public Result checkUserName(@RequestParam(value = "username", required = true) String userName) {
        boolean flag = userService.checkUserName(userName);
        return Result.success(flag);
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public Result addRecommend(@RequestParam(value = "username") String userName, @RequestParam(value = "content") String content) {
        if (StringUtils.isBlank(userName)) {
            return new Result(false, "用户不能为空");
        }
        boolean flag = userService.addRecommend(userName, content);
        return new Result(flag, flag ? "添加成功" : "添加失败");
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
        return Result.success(pageInfo);
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.DELETE)
    public Result deleteRecommend(@RequestParam(value = "id") int id, @RequestParam(value = "username") String userName) {
        boolean result = userService.deleteRecommend(id, userName);
        return new Result(result, result ? "删除成功" : "删除失败");
    }
}
