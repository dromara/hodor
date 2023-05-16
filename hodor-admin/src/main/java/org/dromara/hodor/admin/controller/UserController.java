package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 *
 * @author tomgs
 * @since 1.0
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询
     *
     * @param user     筛选条件
     * @param pageNo   第几页
     * @param pageSize 分页大小
     * @return 查询结果
     */
    @GetMapping
    public Result<PageInfo<User>> queryByPage(@RequestBody User user,
                                              @RequestParam(value = "pageNo") Integer pageNo,
                                              @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<User> pageInfo = userService.queryByPage(user, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<User> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(userService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param user 实体
     * @return 新增结果
     */
    @PostMapping
    public Result<User> add(@RequestBody User user) {
        return ResultUtil.success(userService.insert(user));
    }

    /**
     * 编辑数据
     *
     * @param user 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<User> update(@RequestBody User user) {
        return ResultUtil.success(userService.update(user));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(userService.deleteById(id));
    }

}

