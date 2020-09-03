package org.dromara.hodor.core;
import java.util.List;
import org.mapstruct.Mapper;
import org.dromara.hodor.core.entity.JobInfo;

/**
 * job info convert
 *
 * @author tomgs
 * @since 2020/8/28
 */
@Mapper
public interface JobDescConvert {

    JobDesc convert(final JobInfo jobInfo);

    List<JobDesc> convertList(final List<JobInfo> jobInfoList);

}
