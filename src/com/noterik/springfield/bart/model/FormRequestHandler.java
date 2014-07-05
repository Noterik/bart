package com.noterik.springfield.bart.model;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.noterik.springfield.bart.authentication.CommandAuthentication;
import com.noterik.springfield.bart.authentication.UserAuthentication;
import com.noterik.springfield.bart.transform.FormTransformer;
import com.noterik.springfield.tools.fs.URIParser;

public class FormRequestHandler extends RequestHandler {
	/**
	 * Contains the form data
	 */
	private Form data;
	
	/**
	 * The method to call
	 */
	private String method;
	
	/**
	 * Mimetype
	 */
	private String mimetype;
	
	public FormRequestHandler(Request request) {
		super(request);
		
	
		// get data
		data = request.getEntityAsForm();
		
		// determine method
		method = data.getFirstValue("method", "get").toLowerCase();
		
		// determine mimetype 
		mimetype = data.getFirstValue("mimetype", "").toLowerCase();
	}	

	@Override
	public Representation get() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "GET", fsxml,cookies);
		return new StringRepresentation(response,MediaType.TEXT_XML);
	}

	@Override
	public Representation put() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "PUT", fsxml,cookies);
		return new StringRepresentation(response,MediaType.TEXT_XML);
	}
	
	@Override
	public Representation post() {
		
		/* ----- default handle ----- */		
		
		if(method.equals("put")) {
			return put();
		} 
		else if(method.equals("get")) {
			return get();
		}
		else if(method.equals("delete")) {
			return delete();
		}
		
		/* ----- end default handle ----- */		
		
		// POST
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "POST", fsxml,cookies);
		return new StringRepresentation(response,MediaType.TEXT_XML);
	}
	
	@Override
	public Representation delete() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "DELETE", fsxml,cookies);
		return new StringRepresentation(response,MediaType.TEXT_XML);
	}

	@Override
	public boolean authenticate() {
		// get user and ticket
		String user = data.getFirstValue("user","");
		String ticket = data.getFirstValue("ticket","");
		
		// validate
		return UserAuthentication.validateTicket(user, ticket, URIParser.getDomainIdFromUri(uri));
	}
	
	@Override
	public boolean authorize(String dummy) {
		// get is always allowed
		if(method.toLowerCase().equals("get")) {
			return true;
		}
				
		// authenticate user
		if(authenticate()) {
			// get user and domain
			String userID = data.getFirstValue("user","");
			String domainID = URIParser.getDomainIdFromUri(uri);
			
			// command check
			if(method.toLowerCase().equals("post") && mimetype.equals(MIMETYPE_COMMAND)) {
				String commandID = data.getFirstValue("id");
				return CommandAuthentication.validateCommand(domainID, userID, commandID);
			}
			
			// parse uri 
			if( uri.startsWith("/domain/"+domainID+"/user/"+userID+"/") ) {
				return true;
			}
		}
		// TODO: fix this hack		
		else if(method.toLowerCase().equals("post") && mimetype.toLowerCase().equals(MIMETYPE_COMMAND)) {
			String commandID = data.getFirstValue("id");
			return CommandAuthentication.validateCommand("dummy", "dummy", commandID);
		}
		
		// default false
		return true;
	}
}
