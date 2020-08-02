package org.dromara.hodor.core;

import java.util.Map;
import org.dromara.hodor.core.enums.Priority;

public interface JobInfo {

  String getJobKey();

  String getGroupName();

  String getJobName();

  int getType();

  Priority getPriority();

  Map<String, Object> getJobData();

  String getCron();

}
