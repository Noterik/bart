package com.noterik.springfield.bart.encryption;

public class Domain {

	private String domain = "";
	private boolean enabled = false;
	
	public Domain(String domain, boolean enabled) {
		this.domain = domain;
		this.enabled = enabled;
	}
	
	public boolean securityEnabled() {
		return this.enabled;
	}
	
	public void setSecurityEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
