package com.noterik.springfield.bart.encryption;

import java.util.HashMap;

public class DomainList {
	
	private static DomainList instance;
	private HashMap<String, Domain> domains;
	
	public DomainList() {
		domains = new HashMap<String, Domain>(512);
	}
	
	public static DomainList instance() {
		if (instance == null) {
			instance = new DomainList();
		}
		return instance;
	}
	
	public void updateDomain(String domain, boolean enabled) {
		if (domains.containsKey(domain)) {
			Domain d = domains.get(domain);
			d.setSecurityEnabled(enabled);
		} else {
			Domain d = new Domain(domain, enabled);
			domains.put(domain, d);
		}
	}
	
	public boolean getDomainSecurityEnabled(String domain) {	
		if (domains.containsKey(domain)) {
			return domains.get(domain).securityEnabled();
		}
		return false;
	}	
}
