package org.dromara.hodor.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.service.JobOperatorService;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobInfo;
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
 * JobOperatorController
 *
 * @author tomgs
 * @since 1.0
 */
@Tag(name = "任务操作管理")
@RestController
@RequestMapping("jobOperator")
@RequiredArgsConstructor
public class JobOperatorController {

    private final JobOperatorService jobOperatorService;

    @Operation(summary = "分页查询任务列表",
        parameters = {
            @Parameter(name = "jobInfo", description = "任务筛选条件"),
            @Parameter(name = "pageNo", description = "第几页"),
            @Parameter(name = "pageSize", description = "分页大小")
        })
    @GetMapping
    public Result<PageInfo<JobInfo>> queryByPage(JobInfo jobInfo,
                                                 @RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize) {
        PageInfo<JobInfo> pageInfo = jobOperatorService.queryByPage(jobInfo, pageNo, pageSize);
        return ResultUtil.success(pageInfo);
    }

    @Operation(summary = "通过任务Id查询任务信息")
    @GetMapping("{id}")
    public Result<JobInfo> queryById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobOperatorService.queryById(id));
    }

    @Operation(summary = "添加任务")
    @PostMapping
    public Result<JobInfo> add(@RequestBody JobInfo jobInfo) {
        return ResultUtil.success(jobOperatorService.addJob(jobInfo));
    }

    @Operation(summary = "更新任务")
    @PutMapping
    public Result<JobInfo> updateById(@RequestBody JobInfo jobInfo) {
        return ResultUtil.success(jobOperatorService.updateById(jobInfo));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobOperatorService.deleteById(id));
    }

    @Operation(summary = "执行任务")
    @PostMapping("/execute/{id}")
    public Result<Boolean> executeById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobOperatorService.executeById(id));
    }

    @Operation(summary = "停止任务")
    @PostMapping("/stop/{id}")
    public Result<Boolean> stopById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobOperatorService.stopById(id));
    }

    @Operation(summary = "恢复任务")
    @PostMapping("/resume/{id}")
    public Result<Boolean> resumeById(@PathVariable("id") Long id) {
        return ResultUtil.success(jobOperatorService.resumeById(id));
    }

}

