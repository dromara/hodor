package org.dromara.hodor.scheduler.api.config;

import lombok.Builder;
import lombok.Getter;

/**
 * scheduler config
 * @author tangzy
 */
@Builder
@Getter
public class SchedulerConfig {

  private String schedulerName;
  private int threadCount;
  private int misfireThreshold;

}
