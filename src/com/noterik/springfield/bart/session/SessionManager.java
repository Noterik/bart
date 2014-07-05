package com.noterik.springfield.bart.session;

import java.util.Date;
import java.util.UUID;

import java.util.HashMap;

public class SessionManager {

	// Session expiry 30 minutes
	public static final int EXPIRY = 30 * 60 * 1000;
	public static final int SIZE = 4096;
	private static SessionManager instance;
	private HashMap<UUID, Session> sessions;
	
	private SessionManager() {
		sessions = new HashMap<UUID, Session>(SIZE);
	}
	
	public static SessionManager instance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	public UUID createSession() {
		UUID id = UUID.randomUUID();
		
		Session session = new Session(id);
		sessions.put(id, session);	
		return id;
	}
	
	public Session getSession(String id, boolean update) {
		if (!id.matches("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}")) {
			return null;
		}
		
		UUID uuid = UUID.fromString(id);
		
		if (sessions.containsKey(uuid)) {		
			Session session = sessions.get(uuid);

			if (session.getLastAccess() > new Date().getTime()-EXPIRY) {
				if (update) {
					session.setLastAccess();
				}
				return session;
			} else {
				if (update) {
					sessions.remove(UUID.fromString(id));
				}
			}
		}
		return null;
	}
	
	public int size() {
		return sessions.size();
	}
}
