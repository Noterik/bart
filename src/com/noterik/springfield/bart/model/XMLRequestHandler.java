package com.noterik.springfield.bart.model;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.noterik.springfield.bart.authentication.CommandAuthentication;
import com.noterik.springfield.bart.authentication.UserAuthentication;
import com.noterik.springfield.bart.encryption.SimpleEncryption;
import com.noterik.springfield.bart.session.Session;
import com.noterik.springfield.bart.session.SessionManager;
import com.noterik.springfield.bart.transform.FormTransformer;
import com.noterik.springfield.tools.fs.FSXMLBuilder;
import com.noterik.springfield.tools.fs.URIParser;

/**
 * Handler for xml requests
 * 
 * This class handles the xml requests. It only needs to validate
 * the user if a username and ticket is supplied.
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.model
 * @access private
 * @version $Id: XMLRequestHandler.java,v 1.15 2011-11-16 07:05:28 pieter Exp $
 *
 */
public class XMLRequestHandler extends RequestHandler {

	/**
	 * The original xml data
	 */
	private String fsxml = null;
	
	/**
	 * Contains the query data
	 */
	private Form data;
	
	private String sid = null;

	public XMLRequestHandler(Request request) {
		super(request);
		
		// get (query) data
		data = request.getResourceRef().getQueryAsForm(CharacterSet.ISO_8859_1);
		
		// get sid
		sid = data.getFirstValue("bsid");
		
		// get xml from representation
		try {
			request.getEntity().setCharacterSet(CharacterSet.UTF_8);
			fsxml = request.getEntity().getText();
		} catch (IOException e) {}
	}

	@Override
	public Representation delete() {
		return handle("DELETE");
	}

	@Override
	public Representation get() {
		return handle("GET");
	}

	@Override
	public Representation post() {
		return handle("POST");
	}

	@Override
	public Representation put() {
		return handle("PUT");
	}
	
	/**
	 * Basic handler
	 * 
	 * @param method
	 * @return
	 */
	private Representation handle(String method) {
		// TODO: validation		
		method = method.toUpperCase();
		
		SimpleEncryption enc = new SimpleEncryption();
		String error = FSXMLBuilder.getErrorMessage("", 
				"Request body was empty", 
				"Request body was empty", 
				"http://blackboots.noterik.com/team");
		
		// check input
		if(fsxml == null) {
			return new StringRepresentation(error,MediaType.TEXT_XML);
		}
		
		if (sid != null && !sid.equals("")) {
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if (currentSession != null) {
				//get token and decrypt it to check for valid request
				System.out.println("encrypted received = "+fsxml);				
				fsxml = enc.Decrypt(currentSession.getEncryptionKey(), fsxml);					
				System.out.println("decrypted with "+sid+" = "+fsxml);
				
				//check delete body for matching uri
				if (method.equals("PUT") || method.equals("POST")) {
					if (validateXml()) {	
						//check data ok, so forward data from ok
						fsxml = FormTransformer.transform(data);
						System.out.println("fsxml transformed to "+fsxml);
					}
				} else if (method.equals("DELETE")) {					
					if (!validateXml()) {
						return new StringRepresentation(error, MediaType.TEXT_XML);
					}
				}
			}
		}			
		String response = ForwardManager.forwardToSmithers(uri, method, fsxml,cookies);
		
		if (sid != null && !sid.equals("")) {
			Session currentSession = SessionManager.instance().getSession(sid, false);
			if (currentSession != null) {			
				response = enc.Encrypt(currentSession.getEncryptionKey(), response);	
			}
		}		
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
	public boolean authorize(String method) {
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
			if(method.toLowerCase().equals("post")) {
				String mimetype = getMimetype();
				if(mimetype.equals(MIMETYPE_COMMAND)) {
					String commandID = getCommandID();
					return CommandAuthentication.validateCommand(domainID, userID, commandID);
				}
			}
			
			// parse uri 
			if( uri.startsWith("/domain/"+domainID+"/user/"+userID+"/") ) {
				return true;
			}
		} 
		// TODO: fix this hack
		else if(method.toLowerCase().equals("post")) {
			String mimetype = getMimetype();
			if(mimetype.toLowerCase().equals(MIMETYPE_COMMAND)) {
				String commandID = getCommandID();
				return CommandAuthentication.validateCommand("dummy", "dummy", commandID);
			}
		}
		// default false
		return true;
	}
	
	/**
	 * Extract mimetype from XML
	 * 
	 * @return
	 */
	private String getMimetype() {
		String mimetype = "";
		try {
			Document doc = DocumentHelper.parseText(fsxml);
			Element root = doc.getRootElement();
			Node node = root.selectSingleNode("@mimetype");
			if(node!=null) {
				mimetype = node.getText();
			}
		} catch(Exception e) {}
		return mimetype;
	}

	/**
	 * Extract command id from XML
	 * 
	 * @return
	 */
	private String getCommandID() {
		String id = "";
		try {
			Document doc = DocumentHelper.parseText(fsxml);
			Element root = doc.getRootElement();
			Node node = root.selectSingleNode("@id");
			if(node!=null) {
				id = node.getText();
			}
		} catch(Exception e) {}
		return id;
	}
		
	private boolean validateXml() {
		Document doc = null;

		try {
			doc = DocumentHelper.parseText(fsxml);
		} catch (DocumentException e) {
			return false;
		}
		
		Node nuri = doc.selectSingleNode("//properties/uri");		
		String correctUri = nuri == null ? "" : nuri.getText();
		
		System.out.println("uri send = "+correctUri);
		System.out.println("uri requested = "+ uri);
		
		Node random = doc.selectSingleNode("//properties/random");
		
		if (random != null ) {
			System.out.println("random not empty");
			if (uri.equals(correctUri)) {
				doc.remove(nuri);
				doc.remove(random);
				
				fsxml = doc.asXML();
				return true;
			}			
		}	
		return false;
	}
}
