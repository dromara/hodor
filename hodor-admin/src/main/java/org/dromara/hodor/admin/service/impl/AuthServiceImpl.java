package org.dromara.hodor.admin.service.impl;

import java.util.List;
import org.dromara.hodor.admin.service.AuthService;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements AuthService {

	@Override
	public void assign(String username, String group, String type) {
	}

	@Override
	public void unAssign(String username, String group, String type) {
	}

	@Override
	public List<String> getAuthAvailableJobGroup() {
		return null;
	}
	
	@Override
	public boolean[] getAuthByUsernameAndJobGroup(String username, String jobGroup) {
		return null;
	}
}
