package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.entity.Tenant;
import org.dromara.hodor.admin.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.web.bind.annotation.*;

/**
 * TenantController
 *
 * @author tomgs
 * @since 1.0
 */
@RestController
@RequestMapping("tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 分页查询
     *
     * @param tenant 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    @GetMapping
    public Result<PageInfo<Tenant>> queryByPage(@RequestBody Tenant tenant,
                                                    @RequestParam(value = "pageNo") Integer pageNo,
                                                    @RequestParam(value = "pageSize") Integer pageSize) {
		PageInfo<Tenant> pageInfo = tenantService.queryByPage(tenant, pageNo, pageSize);
		return ResultUtil.success(pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<Tenant> queryById(@PathVariable("id") Long id) {
		return ResultUtil.success(tenantService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param tenant 实体
     * @return 新增结果
     */
    @PostMapping
    public Result<Tenant> add(@RequestBody Tenant tenant) {
        return ResultUtil.success(tenantService.insert(tenant));
    }

    /**
     * 编辑数据
     *
     * @param tenant 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<Tenant> update(@RequestBody Tenant tenant) {
        return ResultUtil.success(tenantService.update(tenant));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(tenantService.deleteById(id));
    }

}

