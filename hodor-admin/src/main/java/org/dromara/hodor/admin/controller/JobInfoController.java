package org.dromara.hodor.admin.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.springframework.web.bind.annotation.*;

/**
 * JobInfoController
 *
 * @author tomgs
 * @since 1.0
 */
@RestController
@RequestMapping("jobInfo")
@RequiredArgsConstructor
public class JobInfoController {

    private final JobInfoService jobInfoService;

    /**
     * 分页查询
     *
     * @param jobInfo 筛选条件
     * @param pageNo      第几页
     * @param pageSize    分页大小
     * @return 查询结果
     */
    @GetMapping
    public Result<PageInfo<JobInfo>> queryByPage(@RequestBody JobInfo jobInfo,
                                                    @RequestParam(value = "pageNo") Integer pageNo,
                                                    @RequestParam(value = "pageSize") Integer pageSize) {
		PageInfo<JobInfo> pageInfo = jobInfoService.queryByPage(jobInfo, pageNo, pageSize);
		return ResultUtil.success(pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<JobInfo> queryById(@PathVariable("id") Long id) {
		return ResultUtil.success(jobInfoService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param jobInfo 实体
     * @return 新增结果
     */
    @PostMapping
    public Result<JobInfo> add(@RequestBody JobInfo jobInfo) {
        return ResultUtil.success(jobInfoService.addJob(jobInfo));
    }

    /**
     * 编辑数据
     *
     * @param jobInfo 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<JobInfo> update(@RequestBody JobInfo jobInfo) {
        return ResultUtil.success(jobInfoService.update(jobInfo));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public Result<Boolean> deleteById(Long id) {
        return ResultUtil.success(jobInfoService.deleteById(id));
    }

}

