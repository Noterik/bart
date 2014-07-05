package com.noterik.springfield.bart.model;

import java.util.Iterator;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Encoding;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Preference;
import org.restlet.engine.application.EncodeRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.util.Series;

import com.noterik.springfield.bart.authentication.CommandAuthentication;
import com.noterik.springfield.bart.authentication.UserAuthentication;
import com.noterik.springfield.bart.encryption.SimpleEncryption;
import com.noterik.springfield.bart.session.Session;
import com.noterik.springfield.bart.session.SessionManager;
import com.noterik.springfield.bart.transform.FormTransformer;
import com.noterik.springfield.tools.fs.FSXMLBuilder;
import com.noterik.springfield.tools.fs.URIParser;

/**
 * Handler for query data requests
 * 
 * This class handles the requests that have their information
 * contained in the query parameters.
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.model
 * @access private
 * @version $Id: QueryRequestHandler.java,v 1.25 2011-11-16 07:05:28 pieter Exp $
 *
 */
public class QueryRequestHandler extends RequestHandler {

	/**
	 * Contains the query data
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
	
	/**
	 * Deflate standard disabled
	 */
	private boolean deflate = false;
	private String reqIP = "";
	private String reqAgent = "";
	
	private String sid = null;
	
	public QueryRequestHandler(Request request) {
		super(request);
		// get data
		data = request.getResourceRef().getQueryAsForm(CharacterSet.ISO_8859_1);
		//data = request.getResourceRef().getQueryAsForm();
		// determine method
		method = data.getFirstValue("method", "get").toLowerCase();
		//get bsid
		sid = data.getFirstValue("bsid");
		
		// get accepted encodings
        List<Preference<Encoding>> acceptedEncodings =  request.getClientInfo().getAcceptedEncodings();
        
        /* get forwarded ip since this requests always comes from our local apache */
		Series<Parameter> headers = (Series<Parameter>) request.getAttributes().get("org.restlet.http.headers");
        reqIP = headers.getFirstValue("x-forwarded-for",true);
        reqAgent = request.getClientInfo().getAgent();
        
        // determine mimetype 
		mimetype = data.getFirstValue("mimetype", "").toLowerCase();
        
        // check if deflate can be served
        /*
        if (acceptedEncodings != null) {
        	for (Preference<Encoding> encoding : acceptedEncodings) {  
        		if (encoding.getMetadata().equals(Encoding.DEFLATE)) {
        			deflate = true;
        			break;
        		}
        	}
        }
        */
	}

	@Override
	public Representation get() {
		
		/* ----- default handle ----- */
		
		// handle request
		if(method.equals("put")) {
			return put();
		} 
		else if(method.equals("post")) {
			return post();
		}
		else if(method.equals("delete")) {
			return delete();
		}
		
		/* ----- end default handle ----- */
		
		// GET
		if(data.getFirst("depth")==null) {
			String[] parts = uri.split("/");
			if(parts.length<4 || (parts.length<7 && uri.contains("/user"))) {
				// add default depth with less than 7 uri parts
				if (uri.contains("at5")) {
					System.out.println("AT5-CRASH-URI requesting "+uri+" from "+reqIP+" ("+reqAgent+")");
				}
				data.add(new Parameter("depth","0"));
				data.add(new Parameter("limit", "1000"));
			}
		}
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "GET", fsxml, cookies);

		if (sid != null && !sid.equals("")) {
			SimpleEncryption senc = new SimpleEncryption();
			String encrypted = "";
			
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if(currentSession!=null) {
				encrypted = senc.Encrypt(currentSession.getEncryptionKey(), response);
				response = encrypted;
			} else {
				response = FSXMLBuilder.getErrorMessage("401", 
						"Unauthorized", 
						"Session not found, unauthorized to send this request without a session", 
						"http://blackboots.noterik.com/team");;
			}
		}

		Representation entity = new StringRepresentation(response,MediaType.TEXT_XML);
		if (deflate) {
			entity = new EncodeRepresentation(Encoding.DEFLATE, entity);
		}		
		return entity;
	}

	@Override
	public Representation put() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "PUT", fsxml,cookies);
		
		if (sid != null && !sid.equals("")) {
			SimpleEncryption senc = new SimpleEncryption();
			String encrypted = "";
			
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if(currentSession!=null) {
				encrypted = senc.Encrypt(currentSession.getEncryptionKey(), response);
				response = encrypted;
			} else {
				response = FSXMLBuilder.getErrorMessage("401", 
						"Unauthorized", 
						"Session not found, unauthorized to send this request without a session", 
						"http://blackboots.noterik.com/team");;
			}
		}
		
		Representation entity = new StringRepresentation(response,MediaType.TEXT_XML);
		if (deflate) {
			entity = new EncodeRepresentation(Encoding.DEFLATE, entity);
		} 
		return entity; 
	}
	
	@Override
	public Representation post() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "POST", fsxml,cookies);
		
		if (sid != null && !sid.equals("")) {
			SimpleEncryption senc = new SimpleEncryption();
			String encrypted = "";
			
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if(currentSession!=null) {
				encrypted = senc.Encrypt(currentSession.getEncryptionKey(), response);
				response = encrypted;
			} else {
				response = FSXMLBuilder.getErrorMessage("401", 
						"Unauthorized", 
						"Session not found, unauthorized to send this request without a session", 
						"http://blackboots.noterik.com/team");;
			}
		}
		
		Representation entity = new StringRepresentation(response,MediaType.TEXT_XML);
		if (deflate) {
			entity = new EncodeRepresentation(Encoding.DEFLATE, entity);
		}
		return entity; 
	}
	
	@Override
	public Representation delete() {
		String fsxml = FormTransformer.transform(data);
		String response = ForwardManager.forwardToSmithers(uri, "DELETE", fsxml,cookies);
		
		if (sid != null && !sid.equals("")) {
			SimpleEncryption senc = new SimpleEncryption();
			String encrypted = "";
			
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if(currentSession!=null) {
				encrypted = senc.Encrypt(currentSession.getEncryptionKey(), response);
				response = encrypted;
			} else {
				response = FSXMLBuilder.getErrorMessage("401", 
						"Unauthorized", 
						"Session not found, unauthorized to send this request without a session", 
						"http://blackboots.noterik.com/team");;
			}
		}
		
		Representation entity = new StringRepresentation(response,MediaType.TEXT_XML);
		if (deflate) {
			entity = new EncodeRepresentation(Encoding.DEFLATE, entity);
		}
		return entity;
	}

	@Override
	public boolean authenticate() {
		// get user and ticket
		String user = data.getFirstValue("user","");
		String ticket = data.getFirstValue("ticket","");
		
		// validate
		return UserAuthentication.validateTicket(user, ticket, URIParser.getDomainFromUri(uri));
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
			if(method.toLowerCase().equals("post") && mimetype.toLowerCase().equals(MIMETYPE_COMMAND)) {
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
