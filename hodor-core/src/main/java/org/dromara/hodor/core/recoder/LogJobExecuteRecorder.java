package org.dromara.hodor.core.recoder;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.dromara.hodor.model.job.JobKey;

/**
 * LogJobExecuteRecord
 *
 * @author tomgs
 * @since 2021/8/11
 */
@Slf4j
public class LogJobExecuteRecorder implements JobExecuteRecorder {

    private final Logger logger;

    private final HodorCacheSource hodorCacheSource;

    private final File logsDir;

    private final File backUpDir;

    private final JobExecDetailService jobExecDetailService;

    private final String loggerName = "hodor-job-execute-record.log";

    private final int interval = 3;

    public LogJobExecuteRecorder(final String logDir,
                                 final JobExecDetailService jobExecDetailService,
                                 final HodorCacheSource hodorCacheSource) {
        String logLayout = "%msg%n";
        this.logsDir = new File((StringUtils.isBlank(logDir) ? System.getProperty("user.dir") : logDir) + "/logs/");
        this.backUpDir = new File((StringUtils.isBlank(logDir) ? System.getProperty("user.dir") : logDir) + "/backup/");
        this.logger = LogUtil.getInstance().createRollingLogger(loggerName, new File(logsDir, loggerName), logLayout, interval);
        this.hodorCacheSource = hodorCacheSource;
        this.jobExecDetailService = jobExecDetailService;

    }

    public void recordJobExecDetail(String op, JobExecDetail jobExecDetail) {
        logger.info(toDetailString(op, jobExecDetail));
    }

    @Override
    public JobExecDetail getJobExecDetail(JobKey jobKey) {
        return hodorCacheSource.<JobKey, JobExecDetail>getCacheSource().get(jobKey);
    }

    @Override
    public void removeRunningJob(JobKey jobKey) {
        hodorCacheSource.<JobKey, JobExecDetail>getCacheSource().remove(jobKey);
    }

    @Override
    public void addSchedulerRunningJob(JobExecDetail jobExecDetail) {
        hodorCacheSource.<JobKey, JobExecDetail>getCacheSource()
            .put(JobKey.of(jobExecDetail.getGroupName(), jobExecDetail.getJobName()), jobExecDetail);
    }

    @Override
    public void startReporterJobExecDetail() {
        Thread reporterThread = new Thread(() -> {
            while (true) {
                try {
                    //Map<Long, JobExecDetail> insertMap = Maps.newHashMap();
                    //Map<Long, JobExecDetail> updateMap = Maps.newHashMap();
                    if (!logsDir.exists() || !logsDir.isDirectory()) {
                        return;
                    }
                    File[] files = logsDir.listFiles((FileFilter) new NotFileFilter(new NameFileFilter(loggerName)));
                    for (File file : Objects.requireNonNull(files)) {
                        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                        lines.forEach(line -> {
                            JobExecDetail jobExecDetail = toRawJobExecDetail(line);
                            if (line.startsWith(OP_INSERT)) {
                                jobExecDetailService.create(jobExecDetail);
                            }
                            if (line.startsWith(OP_UPDATE)) {
                                jobExecDetailService.update(jobExecDetail);
                            }
                        });
                        FileUtils.moveFile(file, new File(backUpDir, file.getName()));
                    }
                } catch (Exception e) {
                    log.error("reporter job recorder file error, msg: {}", e.getMessage(), e);
                }
                ThreadUtils.sleep(TimeUnit.SECONDS, interval);
            }
        }, "job-exec-detail-reporter");
        reporterThread.setDaemon(true);
        reporterThread.start();
    }

}
