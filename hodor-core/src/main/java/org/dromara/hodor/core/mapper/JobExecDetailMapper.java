package org.dromara.hodor.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.hodor.core.entity.JobExecDetail;

/**
 * JobExecDetailMapper
 *
 * @author tomgs
 * @since 1.0
 */
public interface JobExecDetailMapper extends BaseMapper<JobExecDetail> {

    void insertIgnore(JobExecDetail jobExecDetail);

}
