package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service/job/cron")
public class CronController {

    @RequestMapping("/transferFromCron")
    @ResponseBody
    public Result<Void> transferFromCron(@RequestParam(value = "cronExpression", required = true) String cronExpression) {
        return ResultUtil.success();
    }
}
