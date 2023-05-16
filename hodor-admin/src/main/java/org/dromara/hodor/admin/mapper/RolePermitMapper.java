package org.dromara.hodor.admin.mapper;

import org.dromara.hodor.admin.domain.RolePermit;
import java.util.List;

public interface RolePermitMapper {

	void insert(RolePermit rolePermit);

	void delete(RolePermit permit);

	void deleteByRoleId(RolePermit permit);
	
	RolePermit select(RolePermit permit);

	List<RolePermit> getListByRoleId(RolePermit permit);
}
