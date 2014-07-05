package com.noterik.springfield.bart.session;

import java.util.Date;
import java.util.UUID;

public class Session {
	private UUID id;
	private String user;
	private String domain;
	private boolean admin;
	private long created;
	private long lastAccess;
	private String encryptionKey;
	
	public Session(UUID id) {
		this(id, "", "");
	}
	
	public Session(UUID id, String user, String domain) {
		this(id,user,domain,false, id.toString());
	}
	
	public Session(UUID id, String user, String domain, boolean admin, String key) {
		this.id = id;
		this.user = user;
		this.domain = domain;
		this.admin = admin;
		this.encryptionKey = key;
		
		created = new Date().getTime();
		lastAccess = created;
	}
	
	public UUID getId() {
		return this.id;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public void setLastAccess() {
		lastAccess =  new Date().getTime();
	}
	
	public void setEncryptionKey(String key) {
		this.encryptionKey = key;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public long getLastAccess() {
		return lastAccess;
	}
	
	public String getEncryptionKey() {
		return encryptionKey;
	}
}
