package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.KeySecret;

public interface KeySecretService {
	KeySecret selectByKey(String key);
}
