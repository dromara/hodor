package org.dromara.hodor.core.service.impl;

import java.util.List;
import org.dromara.hodor.core.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.springframework.stereotype.Service;

/**
 * @author tomgs
 * @since 2020/6/30
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

    @Override
    public Integer queryAssignableJobCount() {
        return null;
    }

    @Override
    public Integer queryJobHashIdByOffset(Integer offset) {
        return null;
    }

    @Override
    public Integer queryJobIdByOffset(Integer offset) {
        return null;
    }

    @Override
    public List<JobInfo> queryJobInfoByOffset(Integer start, Integer end) {
        return null;
    }

}
