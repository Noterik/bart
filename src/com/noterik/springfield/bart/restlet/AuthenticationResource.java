package com.noterik.springfield.bart.restlet;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.noterik.springfield.bart.authentication.User;
import com.noterik.springfield.bart.authentication.UserAuthentication;
import com.noterik.springfield.tools.fs.FSXMLBuilder;
import com.noterik.springfield.tools.fs.URIParser;

/**
 * Authentication
 * 
 * Handles the login/logout of the proxy
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.restlet
 * @access private
 * @version $Id: AuthenticationResource.java,v 1.9 2011-11-15 13:22:28 derk Exp $
 *
 */
public class AuthenticationResource extends ServerResource {
	
	private String uri;
	
	// allowed actions: POST, GET
	public boolean allowPut() {return false;}
	public boolean allowPost() {return true;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return false;}
	
	/**
	 * Called right after constructor of this resource (every request)
	 */
	@Override
	public void doInit() {
		uri = getRequestUrl();
	}
	
	/**
	 * GET
	 * 
	 * - login
	 * - logout
	 * - validate
	 */
	@Get
	public Representation doGet() {
		String username, pass, ticket, domain;
	    
		//System.out.println("request:: " + getRequest().toString());
        // parse parameters
        Form postdata = getRequest().getResourceRef().getQueryAsForm();
        username = postdata.getFirstValue("user", "");
        pass = postdata.getFirstValue("pass", "");
        ticket = postdata.getFirstValue("ticket", "");
        domain = URIParser.getDomainIdFromUri(uri);
        
        // do stuff
        String response = null;
        if(uri.indexOf("login") != -1) {
        	//System.out.println("LOG IN:::");
        	response = login(username, pass, domain);
        } else if(uri.indexOf("validate") != -1) {
        	//System.out.println("VALIDATE:::");
        	response = authenticate(username, ticket, domain);
        } else {
        	//System.out.println("LOG OUT:::");
        	response = logout(username, ticket, domain);
        }
        System.out.println("response:: " + response);

        // return
        return new StringRepresentation(response,MediaType.TEXT_XML);
	}
	
	/**
	 * POST
	 * 
	 * - login
	 * - logout
	 */
	@Post
	public void doPost(Representation representation) {
		String username, pass, ticket, domain;
	    User user = null;
	    System.out.println("got post method for login");
        // parse parameters
        Form postdata = getRequest().getEntityAsForm();
        username = postdata.getFirstValue("user", "");
        pass = postdata.getFirstValue("pass", "");
        ticket = postdata.getFirstValue("ticket", "");
        domain = URIParser.getDomainIdFromUri(uri);
        
        // do stuff
        String response = null;
        if(uri.indexOf("login") != -1) {
        	System.out.println("LOG IN:::");
        	response = login(username, pass, domain);
        } else if(uri.indexOf("validate") != -1) {
        	System.out.println("VALIDATE:::");
        	response = authenticate(username, ticket, domain);
        } else {
        	System.out.println("LOGOUT");
        	response = logout(username,ticket,domain);
        }		
        
        System.out.println("response:: " + response);
        // set response
		getResponse().setEntity(new StringRepresentation(response,MediaType.TEXT_XML));
	}
	
	/**
	 * This function gets the url of the request eg
	 * domain/noterik/user/levi/video/1
	 *
	 * @return
	 */
	protected final String getRequestUrl() {
		String url = "";
		String uri = getRequest().getResourceRef().getPath();
		String uri2 = uri.substring(2);
		url = uri2.substring(uri2.indexOf("/"));

		// if the url ends in a /, remove it
		if (url.lastIndexOf("/") == url.length() - 1) {
			url = url.substring(0, url.lastIndexOf("/"));
		}

		return url;
	}
	
	/**
	 * Login user and return user information or error xml.
	 * 
	 * @param username
	 * @param pass
	 * @param domain
	 * @return
	 */
	private String login(String username, String pass, String domain) {
		String response = null;
		User user = UserAuthentication.login(username,pass,domain);
		if(user==null) {
			response = FSXMLBuilder.getErrorMessage("","Could not login", "", "http://blackboots.noterik.com/team/");
        }
        else if(user.hasErrors()) {
        	response = FSXMLBuilder.getErrorMessage("","Could not login", "", "http://blackboots.noterik.com/team/");
        }
        else {
        	response = user.asXML();
        }
		return response;
	}
	
	/**
	 * Logout user.
	 * 
	 * @param username
	 * @param ticket
	 * @param domain
	 * @return
	 */
	private String logout(String username, String ticket, String domain) {
		// validate user
		boolean validated = UserAuthentication.validateTicket(username, ticket, domain);
		if(!validated) {
			return FSXMLBuilder.getErrorMessage("401", "Unauthorized", "Ticket could not be validated", "");
		}
		// logout user
		boolean loggedout = UserAuthentication.logout(domain, username);
		if(!loggedout) {
			return FSXMLBuilder.getErrorMessage("410", "Gone", "User could not be logged out", "");
		}
		return FSXMLBuilder.getErrorMessage("200", "OK", "User was successfully logged out", "");
	}
	
	/**
	 * Authenticate user.
	 * 
	 * @param username
	 * @param ticket
	 * @param domain
	 * @return
	 */
	private String authenticate(String username, String ticket, String domain) {
		String response = null;
		boolean authenticated = UserAuthentication.validateTicket(username, ticket, domain);
		if(authenticated) {
			response = "<validTicket>true</validTicket>";
        } else {
        	response ="<validTicket>false</validTicket>";
        }
		return response;
	}
}
