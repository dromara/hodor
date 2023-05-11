package org.dromara.hodor.admin.service.impl;

import javax.annotation.Resource;
import org.dromara.hodor.admin.domain.KeySecret;
import org.dromara.hodor.admin.mapper.KeySecretMapper;
import org.dromara.hodor.admin.service.SecretService;
import org.springframework.stereotype.Service;

@Service
public class SecretServiceImpl implements SecretService {
    @Resource
    private KeySecretMapper dbKeySecretMapper;

    @Override
    public KeySecret selectByKey(String key) {
        return dbKeySecretMapper.selectByKey(key);
    }

}
