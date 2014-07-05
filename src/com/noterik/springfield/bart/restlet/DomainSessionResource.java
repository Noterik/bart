package com.noterik.springfield.bart.restlet;

import java.util.UUID;

import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.noterik.springfield.bart.session.SessionManager;

public class DomainSessionResource extends ServerResource {
	/** session id */
	private UUID sid = null;
	
	/**
	 * Called right after constructor of this resource (every request)
	 */
	@Override
	public void doInit() {
		sid = SessionManager.instance().createSession();
	}
	
	// allowed actions: GET
	public boolean allowPut() {return false;}
	public boolean allowPost() {return false;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return false;}
	
	/**
	 * GET
	 */
	@Get
	public Representation doGet() {
		Representation rep;
		
		rep = new StringRepresentation("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><fsxml><session id=\""+sid.toString()+"\"/></fsxml>",MediaType.TEXT_XML);
		rep.setCharacterSet(CharacterSet.UTF_8);
		
		return rep;		
	}
	
}
