package org.dromara.hodor.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.mapper.JobExecDetailMapper;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author tomgs
 * @since 2020/8/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecDetailServiceImpl implements JobExecDetailService {

    private final @NonNull JobExecDetailMapper jobExecDetailMapper;

}
