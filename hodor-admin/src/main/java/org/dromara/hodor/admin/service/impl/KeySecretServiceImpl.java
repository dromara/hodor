package org.dromara.hodor.admin.service.impl;

import org.dromara.hodor.admin.domain.KeySecret;
import org.dromara.hodor.admin.mapper.KeySecretMapper;
import javax.annotation.Resource;
import org.dromara.hodor.admin.service.KeySecretService;
import org.springframework.stereotype.Service;
@Service("keySecretService")
public class KeySecretServiceImpl implements KeySecretService {
	@Resource
	private KeySecretMapper dbKeySecretMapper;

	@Override
	public KeySecret selectByKey(String key) {
		// TODO Auto-generated method stub
		return dbKeySecretMapper.selectByKey(key);
	}

}
