package org.dromara.hodor.admin.domain;

public class RolePermit {

	private String id;
	
	private String roleId ;
	
	private String permitItem;
	
	public RolePermit(String roleId, String permititem) {
		this.roleId = roleId;
		this.permitItem = permititem;
	}

	public RolePermit() {
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getPermitIterm() {
		return permitItem;
	}

	public void setPermitIterm(String permitIterm) {
		this.permitItem = permitIterm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DBRolePermit [id=" + id + ", roleId=" + roleId + ", permitItem=" + permitItem + "]";
	}
	
}
