package org.dromara.hodor.admin.service;

import java.util.List;

public interface AuthService {

	void assign(String username, String group, String type);

	void unAssign(String username, String group, String type);
	
	List<String> getAuthAvailableJobGroup();

	boolean[] getAuthByUsernameAndJobGroup(String username, String jobGroup);
}