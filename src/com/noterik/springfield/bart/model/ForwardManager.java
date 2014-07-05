package com.noterik.springfield.bart.model;

import java.io.File;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jdom.*;
import org.jdom.input.*;
import org.w3c.dom.Node;

//import com.noterik.bart.marge.model.Service;
import com.noterik.springfield.bart.common.HelperFunctions;
import com.noterik.springfield.bart.homer.LazyHomer;
import com.noterik.springfield.tools.HttpHelper;
import com.noterik.springfield.tools.fs.FSXMLBuilder;
import com.noterik.springfield.tools.fs.URIParser;

public class ForwardManager {
	
	private static Logger LOG = Logger.getLogger(ForwardManager.class);
	
	/**
	 * This function finally forwards the request coming from the client to smithers. Homer is
	 * used to see which smithers to send the request to.
	 * @param uri
	 * @param method
	 * @param xml
	 * @param cookies
	 * @return
	 */
	public static String forwardToSmithers(String uri, String method, String xml,String cookies) {
		// are we running ?
		if(uri != null && (uri.equals("/") || uri.equals(""))) {
			return "<fsxml><error id=\"500\"><properties><message>No domain defined in the uri</message></properties></error></fsxml>";
		}
		
		
		if (LazyHomer.isRunning()) {
			LOG.debug("Forward: "+uri);
			String response = LazyHomer.sendRequest(method, uri, xml,"text/xml",cookies);
			return response;
		} else {
			return "<fsxml><error id=\"500\"><properties><message>This bart is not running at the moment</message></properties></error></fsxml>";
		}
	}
	
	
	
}