package org.dromara.hodor.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class UserActionRecord {

	private String id;
	
	private String username;
	
	private String permitItem;
	
	private ActionResult result;
	
	private Date actionDate;

	private String actionParam;
	
	private String host;
	
	private String addr;
	
	public UserActionRecord(){
		
	}
	
	public UserActionRecord(String username, String permitItem, String param) {
		this.username = username;
		this.permitItem = permitItem;
		this.actionParam = param;
	}
	
	public UserActionRecord(String username, String permitItem) {
		this.username = username;
		this.permitItem = permitItem;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPermitItem() {
		return permitItem;
	}

	public void setPermitItem(String permitItem) {
		this.permitItem = permitItem;
	}

	public ActionResult getResult() {
		return result;
	}

	public void setResult(ActionResult result) {
		this.result = result;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Shanghai")  
	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public String getActionParam() {
		return actionParam;
	}

	public void setActionParam(String actionParam) {
		this.actionParam = actionParam;
	}

	public static enum ActionResult{
		SUCCESS("success"),
		PERMIT_FAILURE("permit_failure"),
		EXCEPTION_FAILURE("exception_failure");
		private String name;
		private ActionResult(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Override
	public String toString() {
		return "DBUserActionRecord [id=" + id + ", username=" + username + ", permitItem=" + permitItem + ", result="
				+ result + ", actionDate=" + actionDate + ", actionParam=" + actionParam + ", host=" + host + ", addr="
				+ addr + "]";
	}

}
