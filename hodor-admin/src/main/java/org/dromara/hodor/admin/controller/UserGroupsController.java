package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.domain.UserGroups;
import org.dromara.hodor.admin.service.UserGroupsService;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.web.bind.annotation.*;

/**
 * UserGroupsController
 *
 * @author tomgs
 * @since 1.0
 */
@RestController
@RequestMapping("/userGroups")
@RequiredArgsConstructor
public class UserGroupsController {

    private final UserGroupsService userGroupsService;

    /**
     * 分页查询
     *
     * @param userGroups 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    @GetMapping
    public Result<PageInfo<UserGroups>> queryByPage(UserGroups userGroups, 
                                                    @RequestParam(value = "pageNo") Integer pageNo,
                                                    @RequestParam(value = "pageSize") Integer pageSize) {
		PageInfo<UserGroups> pageInfo = userGroupsService.queryByPage(userGroups, pageNo, pageSize);
		return ResultUtil.success(pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public Result<UserGroups> queryById(@PathVariable("id") Long id) {
		return ResultUtil.success(userGroupsService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param userGroups 实体
     * @return 新增结果
     */
    @PostMapping
    public Result<UserGroups> add(UserGroups userGroups) {
        return ResultUtil.success(userGroupsService.insert(userGroups));
    }

    /**
     * 编辑数据
     *
     * @param userGroups 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<UserGroups> update(UserGroups userGroups) {
        return ResultUtil.success(userGroupsService.update(userGroups));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(userGroupsService.deleteById(id));
    }

}

