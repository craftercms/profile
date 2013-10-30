package org.craftercms.profile.impl.domain;

import java.io.Serializable;

public class Target implements Serializable {
	private String targetId;
	private String targetDescription;
	private String targetUrl;
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getTargetDescription() {
		return targetDescription;
	}
	public void setTargetDescription(String targetDescription) {
		this.targetDescription = targetDescription;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	

}
