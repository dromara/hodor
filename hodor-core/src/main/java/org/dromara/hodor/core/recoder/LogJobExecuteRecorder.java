package org.dromara.hodor.core.recoder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
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
import org.dromara.hodor.common.storage.cache.CacheClient;
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

    private final Logger jobExecuteDetailLogger;

    private final CacheClient<JobKey, JobExecDetail> cacheClient;

    private final File logsDir;

    private final File backUpDir;

    private final JobExecDetailService jobExecDetailService;

    private final String loggerName = "hodor-job-execute-record.log";

    private final int interval = 3;

    private volatile boolean isStart = false;

    public LogJobExecuteRecorder(final String logDir,
                                 final JobExecDetailService jobExecDetailService,
                                 final HodorCacheSource hodorCacheSource) {
        Assert.notNull(jobExecDetailService, "jobExecDetailService must be not null.");
        Assert.notNull(hodorCacheSource, "hodorCacheSource must be not null.");

        String logLayout = "%msg%n";
        this.logsDir = new File((StringUtils.isBlank(logDir) ? System.getProperty("user.dir") : logDir) + "/logs/");
        this.backUpDir = new File((StringUtils.isBlank(logDir) ? System.getProperty("user.dir") : logDir) + "/backup/");
        this.jobExecuteDetailLogger = LogUtil.getInstance().createRollingLogger(loggerName, new File(logsDir, loggerName), logLayout, interval);
        this.cacheClient = hodorCacheSource.getCacheClient("job-execute-recorder");
        this.jobExecDetailService = jobExecDetailService;
    }

    public void recordJobExecDetail(String op, JobExecDetail jobExecDetail) {
        jobExecuteDetailLogger.info(toDetailString(op, jobExecDetail));
    }

    @Override
    public JobExecDetail getJobExecDetail(JobKey jobKey) {
        return cacheClient.get(jobKey);
    }

    @Override
    public void removeJobExecDetail(JobKey jobKey) {
        cacheClient.remove(jobKey);
    }

    @Override
    public void addJobExecDetail(JobExecDetail jobExecDetail) {
        cacheClient.put(JobKey.of(jobExecDetail.getGroupName(), jobExecDetail.getJobName()), jobExecDetail);
    }

    @Override
    public void startReporterJobExecDetail() {
        isStart = true;
        final FileFilter fileFilter = new NotFileFilter(new NameFileFilter(loggerName));
        final Thread reporterThread = new Thread(() -> {
            while (isStart) {
                try {
                    if (!logsDir.exists() || !logsDir.isDirectory()) {
                        return;
                    }
                    File[] files = logsDir.listFiles(fileFilter);
                    if (files != null && files.length == 0) {
                        File loggerFile = new File(logsDir, loggerName);
                        long length = loggerFile.length();
                        if (length > 2) {
                            jobExecuteDetailLogger.info("");
                        }
                    }
                    File backUpCurrentDayRecordFile = new File(backUpDir, DateUtil.today());
                    for (File file : Objects.requireNonNull(files)) {
                        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                        lines.stream()
                            .filter(StringUtils::isNotBlank)
                            .forEach(line -> {
                                if (line.startsWith(OP_INSERT)) {
                                    JobExecDetail jobExecDetail = toRawJobExecDetail(line);
                                    jobExecDetailService.createIfAbsent(jobExecDetail);
                                    return;
                                }
                                if (line.startsWith(OP_UPDATE)) {
                                    JobExecDetail jobExecDetail = toRawJobExecDetail(line);
                                    jobExecDetailService.update(jobExecDetail);
                                }
                            });
                        FileUtils.moveFile(file, new File(backUpCurrentDayRecordFile, file.getName()));
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

    @Override
    public void stopReporterJobExecDetail() {
        log.info("stop reporter job exec detail record ....");
        this.isStart = false;
    }

}
