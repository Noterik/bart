package com.noterik.springfield.bart.restlet;

import org.restlet.Context;
import org.restlet.routing.Router;

/**
 * Defines the uri mapping to the resources
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.restlet
 * @access private
 * @version $Id: ProxyRestlet.java,v 1.16 2011-11-15 13:47:36 derk Exp $
 *
 */
public class ProxyRestlet extends Router {	
	public ProxyRestlet(Context cx) {
		super(cx);
		
		// set routing mode
		this.setRoutingMode(MODE_BEST_MATCH);
		
		// authentication
		this.attach("/domain/{domainid}/login",AuthenticationResource.class);
		this.attach("/domain/{domainid}/logout",AuthenticationResource.class);
		this.attach("/domain/{domainid}/validate",AuthenticationResource.class);
		
		// testing
		this.attach("/test",TestResource.class);
		
		// session
		this.attach("/session", SessionResource.class);
		
		// domain session
		this.attach("/domain/{domainid}/session", DomainSessionResource.class);
		
		// info
		this.attach("/info", InfoResource.class);
		
		// logging
		this.attach("/logging", LoggingResource.class);
		
		// rest
		this.attachDefault(ProxyResource.class);
	}
}
