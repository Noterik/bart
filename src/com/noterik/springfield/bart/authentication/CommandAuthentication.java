package com.noterik.springfield.bart.authentication;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

//import com.noterik.bart.marge.model.Service;
import com.noterik.springfield.bart.common.HelperFunctions;
import com.noterik.springfield.tools.HttpHelper;

public class CommandAuthentication {
	/** The CommandAuthentication's log4j Logger */
	private static Logger logger = Logger.getLogger(CommandAuthentication.class);
	
	/**
	 * user uri
	 */
	private static final String USER_URI = "/domain/{domainid}/user/{userid}";
	
	/**
	 * Set of commands reserved for administrators
	 */
	private static Set<String> adminCommands = new HashSet<String>();
	static {
		//adminCommands.add("users");
		adminCommands.add("useradd");
		adminCommands.add("usermod");
		adminCommands.add("userdel");
		adminCommands.add("passwd");
		//adminCommands.add("groups");
		adminCommands.add("groupadd");
		adminCommands.add("groupmod");
		adminCommands.add("groupdel");
	}
	
	/**
	 * Validate a command request
	 * 
	 * @param domain
	 * @param username
	 * @param command
	 * @return
	 */
	public static boolean validateCommand(String domain, String username, String command) {		
		// not an admin command
		if(!adminCommands.contains(command)) {
			return true;
		}
		// check if user is admin
		if(isAdmin(domain,username)) {
			return true;
		}		
		return false;
	}
	
	/**
	 * Determine if a user is part of the 'admin' group
	 * 
	 * @param domain
	 * @param username
	 * @return
	 */
	private static boolean isAdmin(String domain, String username) {
		String url, method, response;
		
		// get service
		/*
		Service service = HelperFunctions.getService(domain,"usermanager");
		if(service==null) {
			return false;
		}
		*/
		return false;
		/*
		
		url = service.getUrl() + USER_URI.replace("{domainid}", domain).replace("{userid}", username);
		method = "GET";
		
		// send request to usermanager
		response = HttpHelper.sendRequest(method, url, null, null);
		
		// parse
		try {
			Document doc = DocumentHelper.parseText(response);
			Node node = doc.selectSingleNode("//groups/group[@id='admin']");
			if(node!=null) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
	}
}
