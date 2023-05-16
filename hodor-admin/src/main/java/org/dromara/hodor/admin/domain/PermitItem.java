package org.dromara.hodor.admin.domain;

public class PermitItem {

	private String id;
	
	private String url;
	
	private String description;

	private String itemType;
	
	private String operateType;
	
	private boolean enabled;
	
	private String parentId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDesciption() {
		return description;
	}

	public void setDesciption(String desciption) {
		this.description = desciption;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "DBPermitIterm [id=" + id + ", url=" + url + ", description=" + description + ", itemType=" + itemType
				+ ", operateType=" + operateType + ", enabled=" + enabled + ", parentId=" + parentId + "]";
	}
	
}
