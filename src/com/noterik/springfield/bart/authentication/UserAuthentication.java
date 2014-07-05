package com.noterik.springfield.bart.authentication;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

//import com.noterik.bart.marge.model.Service;
import com.noterik.springfield.bart.common.HelperFunctions;
import com.noterik.springfield.tools.HttpHelper;

/**
 * User validation through communication with the user manager
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.authentication
 * @access private
 * @version $Id: UserAuthentication.java,v 1.15 2011-01-20 12:20:35 derk Exp $
 *
 */
public class UserAuthentication {
	/** The UserAuthentication's log4j Logger */
	private static Logger logger = Logger.getLogger(UserAuthentication.class);
	
	/**
	 * validation uri
	 */
	private static final String VALIDATION_URI = "/domain/{domainid}/user/{userid}/validate";
	
	/**
	 * login uri
	 */
	private static final String LOGIN_URI = "/domain/{domainid}/user/{userid}/login";
	
	/**
	 * 
	 */
	private static final String USER_URI = "/domain/{domainid}/user/{userid}";
	
	/**
	 * barney (user manager) ip
	 */
	private static final String barneyURL = "http://localhost:8080/barney/restlet";
	
	/**
	 * Validate a user using a ticket
	 * 
	 * @param user
	 * @param ticket
	 * @param domain
	 * @return
	 */
	public static boolean validateTicket(String user, String ticket, String domain) {
		logger.debug("validateTicket");
		System.out.println("vlidate ticket");
		String url, method, body, contentType, response;
		
		// check parameters
		if(user==null || ticket==null || domain==null || user.equals("") || ticket.equals("") || domain.equals("")) {
			logger.debug("validateTicket: some parameters where null");
			System.out.println("user: " + user);
			System.out.println("ticket: " + ticket);
			System.out.println("domain: " + domain);
			System.out.println("validateTicket: some parameters where null");
			return false;
		}
		
	
		url = barneyURL + VALIDATION_URI.replace("{domainid}", domain).replace("{userid}", user);
		method = "POST";
		body = "ticket="+ticket;
		contentType = "application/x-www-form-urlencoded";
		
		// send request to usermanager
		response = HttpHelper.sendRequest(method, url, body, contentType);
		System.out.println("BART:: response - " + response);
		// parse
		return response.indexOf("true")!=-1;
	}
		// parse response
		/* deprecated: SLOW
		try {
			Document doc = DocumentHelper.parseText(response);
			String validTicket = doc.getRootElement().getText();
			return validTicket.equals("true");
		} catch (DocumentException e) {
			System.out.println("Could not parse response from usermanager");
			e.printStackTrace();
			
			// TODO: error response, throw Exception?
		}		
		return false;
		
		return false;
	}
	
	/**
	 * Login a user and return his ticket
	 * 
	 * TODO: return User object containing all information
	 * 
	 * @param user
	 * @param pass
	 * @param domain
	 * @return ticket
	 */
	public static User login(String username, String pass, String domain) {
		logger.debug("login");
		String url, method, body, contentType, response;
		
		// check parameters
		if(username==null || pass==null || domain==null || username.equals("") || pass.equals("") || domain.equals("")) {
			logger.debug("login: some parameters where null");
			return null;
		}
		
		
		url = barneyURL + LOGIN_URI.replace("{domainid}", domain).replace("{userid}", username);
		method = "POST";
		body = "password="+pass;
		contentType = "application/x-www-form-urlencoded";
		
		// send request to usermanager
		response = HttpHelper.sendRequest(method, url, body, contentType);
		
		// parse response
		User user = null;
		try {
			user = new User(response);
		} catch (DocumentException e) {
			System.out.println("Could not parse response from usermanager");
			e.printStackTrace();
			
			// TODO: error response, throw Exception?
		}
		
		//get groups, check for admin group
		url = barneyURL + USER_URI.replace("{domainid}", domain).replace("{userid}", username);
		method = "GET";
		
		// send request to usermanager for user data
		user.setUserData(HttpHelper.sendRequest(method, url, "", contentType));
		
		return user;
		
	}
	
	/**
	 * Logout a user
	 * 
	 * @param domain
	 * @param username
	 * @return
	 */
	public static boolean logout(String domain, String username) {
		// TODO: implement logout in usermanager
		return false;
	}
}
