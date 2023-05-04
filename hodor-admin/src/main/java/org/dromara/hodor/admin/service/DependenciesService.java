package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.JobDependencies;
import org.dromara.hodor.admin.domain.WorkNode;
import org.dromara.hodor.admin.dto.GraEntry;
import java.util.List;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/

public interface DependenciesService {

    JobDependencies selectByGroupAndJobName(String groupName, String jobName);

    void updateByGroupAndJobName(JobDependencies jobDependencies);

    void insert(JobDependencies jobDependencies);

    void delete(String groupName,String jobName);

    void getRelyTree(List<String> treeNode, List<GraEntry> entries,List<String> disabled, String groupName, String jobName);

    List<WorkNode> getWorkNode(String groupName, String jobName, String flowId);

}
