package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.KeySecret;

/**
 * 密钥服务
 * 
 * @author tomgs
 * @since 1.0
 */
public interface SecretService {

    KeySecret selectByKey(String key);
}
