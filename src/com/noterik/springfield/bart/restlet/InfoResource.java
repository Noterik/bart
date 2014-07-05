package com.noterik.springfield.bart.restlet;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

public class InfoResource extends ServerResource {
	
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
		
		Request request = getRequest();

		/* get forwarded ip since this requests always comes from our local apache */
		Series<Parameter> headers = (Series<Parameter>) request.getAttributes().get("org.restlet.http.headers");
		
		SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
		String time = simple.format(new Date().getTime());
		
		String fsxml = "<fsxml id=\"200\"><properties>";
		fsxml += "<ip>"+headers.getFirstValue("x-forwarded-for",true)+"</ip>";			
		fsxml += "<time>"+time+"</time></properties></fsxml>";
		
		rep = new StringRepresentation(fsxml,MediaType.TEXT_XML);
		
		return rep; 
	}
}
