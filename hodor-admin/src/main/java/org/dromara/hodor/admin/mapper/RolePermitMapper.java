package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.hodor.admin.dto.RolePermit;
import java.util.List;

@Mapper
public interface RolePermitMapper {

	void insert(RolePermit rolePermit);

	void delete(RolePermit permit);

	void deleteByRoleId(RolePermit permit);

	RolePermit select(RolePermit permit);

	List<RolePermit> getListByRoleId(RolePermit permit);
}
