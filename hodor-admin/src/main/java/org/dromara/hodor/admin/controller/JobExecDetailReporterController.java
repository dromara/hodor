package org.dromara.hodor.admin.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * job exec detail reporter controller
 *
 * @author tomgs
 * @since 1.0
 */
@RestController
@RequestMapping("jobExecDetail")
@RequiredArgsConstructor
public class JobExecDetailReporterController {

    private final JobExecDetailService jobExecDetailService;

    /**
     * 分页查询
     *
     * @param jobExecDetail 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    @GetMapping
    public Result<PageInfo<JobExecDetail>> queryByPage(@RequestBody JobExecDetail jobExecDetail,
                                                 @RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<JobExecDetail> pageInfo = jobExecDetailService.queryByPage(jobExecDetail, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<JobExecDetail> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobExecDetailService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param jobExecDetail 实体
     * @return 新增结果
     */
    @PostMapping
    public Result<JobExecDetail> add(@RequestBody JobExecDetail jobExecDetail) {
        jobExecDetailService.create(jobExecDetail);
        return ResultUtil.success();
    }

    /**
     * 编辑数据
     *
     * @param jobExecDetail 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<JobExecDetail> update(@RequestBody JobExecDetail jobExecDetail) {
        jobExecDetailService.update(jobExecDetail);
        return ResultUtil.success();
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(jobExecDetailService.deleteById(id));
    }

}
