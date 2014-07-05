package com.noterik.springfield.bart.model;

import org.restlet.Request;
import org.restlet.data.Cookie;
import org.restlet.representation.Representation;

/**
 * Default request handler
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.model
 * @access private
 * @version $Id: RequestHandler.java,v 1.10 2011-11-15 13:22:28 derk Exp $
 *
 */
public abstract class RequestHandler {
	private static final String BART_COOKIE="bart_request";
	protected static final String MIMETYPE_COMMAND = "application/fscommand";
	
	protected static final boolean LOGGING = false; 
	
	/**
	 * uri
	 */
	protected String uri;
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return this.uri;
	}
	
	protected String cookies;
	/**
	 * Request object
	 */
	protected Request request;
	
	public RequestHandler(Request request) {
		this.request = request;
		this.uri = getRequestUri(request.getResourceRef().getPath());
		cookies="bart_request=1";
		for (Cookie cookie : request.getCookies()){
			if (cookie.getName().toLowerCase().equals(BART_COOKIE)) continue;
			cookies += ";" + cookie.getName() + "=" + cookie.getValue();
		}
	}
	
	/**
	 * Get the request uri (inside the cloud)
	 * @return
	 */
	public static String getRequestUri(String uri) {
		String url = uri;		
		
		// remove first part (/bart)
		int index;
		if((index = uri.indexOf("/",1))!=-1) {
			url = url.substring(index);
		}

		// if the url ends in a /, remove it
		if (url.lastIndexOf("/") == url.length() - 1) {
			url = url.substring(0, url.lastIndexOf("/"));
		}

		return url;
	}
	
	/**
	 * GET
	 * @return
	 */
	public abstract Representation get();
	
	/**
	 * PUT
	 * @return
	 */
	public abstract Representation put();
	
	/**
	 * POST
	 * @return
	 */
	public abstract Representation post();
	
	/**
	 * DELETE
	 * @return
	 */
	public abstract Representation delete();
	
	/**
	 * Validate user/ticket pair
	 * @return
	 */
	public abstract boolean authenticate();
	
	/**
	 * Authorize action
	 * @return
	 */
	public abstract boolean authorize(String method);

}
