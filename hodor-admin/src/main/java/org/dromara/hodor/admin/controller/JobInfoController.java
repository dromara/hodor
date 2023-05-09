package org.dromara.hodor.admin.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JobInfoController
 *
 * @author tomgs
 * @version 1.0
 */
@RestController
@RequestMapping("/jobInfo")
@RequiredArgsConstructor
public class JobInfoController {

    private final JobInfoService jobInfoService;

    @PostMapping("/createJob")
    public Result<Void> createJob(@RequestBody JobInfo jobInfo) {
        jobInfoService.addJob(jobInfo);
        return ResultUtil.success();
    }

}
