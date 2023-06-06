package org.dromara.hodor.admin.controller;

import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/cron")
public class CronController {

    @RequestMapping("/convertCron")
    @ResponseBody
    public Result<Void> convertCron(@RequestParam(value = "cronExpr", required = true) String cronExpression) {
        return ResultUtil.success();
    }
}
