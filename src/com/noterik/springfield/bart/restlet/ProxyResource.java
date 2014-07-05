package com.noterik.springfield.bart.restlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Options;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.noterik.springfield.bart.encryption.DomainList;
import com.noterik.springfield.bart.model.FormRequestHandler;
import com.noterik.springfield.bart.model.QueryRequestHandler;
import com.noterik.springfield.bart.model.RequestHandler;
import com.noterik.springfield.bart.model.XMLRequestHandler;
import com.noterik.springfield.bart.session.Session;
import com.noterik.springfield.bart.session.SessionManager;
import com.noterik.springfield.tools.fs.FSXMLBuilder;
import com.noterik.springfield.tools.fs.URIParser;

/**
 * Implements the business logic of the WebTV2 platform
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.bart.fs.proxy
 * @access private
 * @project Proxy Service
 * 
 */
public class ProxyResource extends ServerResource {
	
	/**
	 * Handler of the requests
	 */
	protected RequestHandler rHandler;
	
	private String sid = null;
	private String domainID = null;
	private String useragent = "";
	
	/**
	 * Called right after constructor of this resource (every request)
	 */
	@Override
	public void doInit() {
        Form data = getRequest().getResourceRef().getQueryAsForm(CharacterSet.ISO_8859_1);
        sid = data.getFirstValue("bsid");
		useragent = getRequest().getClientInfo().getAgent() == null ? "" : getRequest().getClientInfo().getAgent();
        String uri = getRequest().getResourceRef().getPath();
        domainID = URIParser.getDomainIdFromUri(uri);
        rHandler = getRequestHandler();
	}
	
	// allowed actions: GET, PUT, POST, DELETE, OPTIONS
	public boolean allowPut() {return true;}
	public boolean allowPost() {return true;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return true;}
	public boolean allowOptions() {return true;}
	
	/**
	 * GET
	 */
	@Get
	public Representation doGet() {
		Representation rep;

		String error = FSXMLBuilder.getErrorMessage("403", "Forbidden", "Request is forbidden", "http://blackboots.noterik.com/team");
		rep = new StringRepresentation(error,MediaType.TEXT_XML);
		rep.setCharacterSet(CharacterSet.UTF_8);
		
		if (!useragent.equals("Noterik WebTV 2.0") && DomainList.instance().getDomainSecurityEnabled(domainID)) {
			if (sid != null && !sid.equals("")) {
				Session currentSession = SessionManager.instance().getSession(sid, false);
				if (currentSession == null) {
					return rep;
				}
			} else {
				return rep;
			}
		} else if (sid != null && !sid.equals("")) {
			sid = null;
		}
		
		// authentication / authorization
		if(rHandler.authorize("GET")) { // needed here, since PUT/POST/DELETE request can also be made through here
			rep = rHandler.get();
		} else {
			error = FSXMLBuilder.getErrorMessage("401", "Request is not authorized", "Request is not authorized", "http://blackboots.noterik.com/team");
			rep = new StringRepresentation(error,MediaType.TEXT_XML);
		}
		rep.setCharacterSet(CharacterSet.UTF_8);
		
		//set access control headers to allow cross domain communication
		Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
		if (responseHeaders == null)  
		{  
			responseHeaders = new Form();  
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
		}  
		responseHeaders.add("Access-Control-Allow-Origin", "*");  
		responseHeaders.add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
		return rep;
	}
	
	/**
	 * PUT
	 */
	@Put
	public void doPut(Representation representation) {
		Representation rep = null;
		String error = FSXMLBuilder.getErrorMessage("403", "Forbidden", "Request is forbidden", "http://blackboots.noterik.com/team");
		if (!useragent.equals("Noterik WebTV 2.0") && DomainList.instance().getDomainSecurityEnabled(domainID)) {
			if (sid != null && !sid.equals("")) {
				Session currentSession = SessionManager.instance().getSession(sid, false);
				if (currentSession == null) {
					rep = new StringRepresentation(error,MediaType.TEXT_XML);
					rep.setCharacterSet(CharacterSet.UTF_8);
				}
			} else {
				rep = new StringRepresentation(error,MediaType.TEXT_XML);
				rep.setCharacterSet(CharacterSet.UTF_8);
			}
		} else if (sid != null && !sid.equals("")) {
			sid = null;
		}
		
		if (rep == null) {
			// authentication / authorization
			if(rHandler.authorize("PUT")) {
				rep = rHandler.put();
			} else {
				error = FSXMLBuilder.getErrorMessage("401", "Request is not authorized", "Request is not authorized", "http://blackboots.noterik.com/team");
				rep = new StringRepresentation(error,MediaType.TEXT_XML);
			}
			rep.setCharacterSet(CharacterSet.UTF_8);
		}

		// set representation
		getResponse().setEntity(rep);
		
		//set access control headers to allow cross domain communication
		Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
		if (responseHeaders == null)  
		{  
			responseHeaders = new Form();  
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
		}  
		responseHeaders.add("Access-Control-Allow-Origin", "*");  
		responseHeaders.add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
	}
	
	/**
	 * POST
	 */
	@Post
	public void doPost(Representation representation) {
		Representation rep = null;
		String error = FSXMLBuilder.getErrorMessage("403", "Forbidden", "Request is forbidden", "http://blackboots.noterik.com/team");
		if (!useragent.equals("Noterik WebTV 2.0") && DomainList.instance().getDomainSecurityEnabled(domainID)) {
			if (sid != null && !sid.equals("")) {
				Session currentSession = SessionManager.instance().getSession(sid, false);
				if (currentSession == null) {
					rep = new StringRepresentation(error,MediaType.TEXT_XML);
					rep.setCharacterSet(CharacterSet.UTF_8);
				}
			} else {
				rep = new StringRepresentation(error,MediaType.TEXT_XML);
				rep.setCharacterSet(CharacterSet.UTF_8);
			}
		} else if (sid != null && !sid.equals("")) {
			sid = null;
		}
		
		if (rep == null) {
			// authentication / authorization
			if(rHandler.authorize("POST")) {
				rep = rHandler.post();
			} else {
				error = FSXMLBuilder.getErrorMessage("401", "Request is not authorized", "Request is not authorized", "http://blackboots.noterik.com/team");
				rep = new StringRepresentation(error,MediaType.TEXT_XML);
			}
			rep.setCharacterSet(CharacterSet.UTF_8);
		}
		// set response
		getResponse().setEntity(rep);
		
		//set access control headers to allow cross domain communication
		Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
		if (responseHeaders == null)  
		{  
			responseHeaders = new Form();  
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
		}  
		responseHeaders.add("Access-Control-Allow-Origin", "*");  
		responseHeaders.add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
	}
	
	/**
	 * DELETE
	 */
	@Delete
	public void doDelete() {
		Representation rep;
		String error = FSXMLBuilder.getErrorMessage("403", "Forbidden", "Request is forbidden", "http://blackboots.noterik.com/team");
		if (!useragent.equals("Noterik WebTV 2.0") && DomainList.instance().getDomainSecurityEnabled(domainID)) {
			if (sid != null && !sid.equals("")) {
				Session currentSession = SessionManager.instance().getSession(sid, false);
				if (currentSession == null) {
					rep = new StringRepresentation(error,MediaType.TEXT_XML);
					rep.setCharacterSet(CharacterSet.UTF_8);
				}
			} else {
				rep = new StringRepresentation(error,MediaType.TEXT_XML);
				rep.setCharacterSet(CharacterSet.UTF_8);
			}
		} else if (sid != null && !sid.equals("")) {
			sid = null;
		}
		
		// authentication / authorization
		if(rHandler.authorize("DELETE")) {
			rep = rHandler.delete();
		} else {
			error = FSXMLBuilder.getErrorMessage("401", "Request is not authorized", "Request is not authorized", "http://blackboots.noterik.com/team");
			rep = new StringRepresentation(error,MediaType.TEXT_XML);
		}
		rep.setCharacterSet(CharacterSet.UTF_8);
		// set response
		getResponse().setEntity(rep);
		
		//set access control headers to allow cross domain communication
		Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
		if (responseHeaders == null)  
		{  
			responseHeaders = new Form();  
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
		}  
		responseHeaders.add("Access-Control-Allow-Origin", "*");  
		responseHeaders.add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
	}
	
	/**
	 * OPTIONS
	 */
	@Options
	public void doOptions(Representation entity) {
		//set access control headers to allow cross domain communication
		Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
	    if (responseHeaders == null) {
	        responseHeaders = new Form();
	        getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
	    }
	    responseHeaders.add("Access-Control-Allow-Origin", "*");
	    responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
	    responseHeaders.add("Access-Control-Max-Age", "60");
	}
	
	/**
	 * Get the correct request handler
	 * @return
	 */
	protected RequestHandler getRequestHandler() {
		RequestHandler rHandler = null;		
		// check type of request
		Request request = getRequest();		   				
		MediaType reqType = request.getEntity().getMediaType();
		if(reqType == null) {			
			rHandler = new QueryRequestHandler(request);
		}
		else if(reqType.equals(MediaType.TEXT_XML)) {			
			rHandler = new XMLRequestHandler(request);
		}
		else if(reqType.equals(MediaType.APPLICATION_WWW_FORM)) {			
			rHandler = new FormRequestHandler(request);
		}
		else {			
			rHandler = new QueryRequestHandler(request);
		}
		
		return rHandler;
	}
}
