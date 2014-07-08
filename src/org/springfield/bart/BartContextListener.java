package org.springfield.bart;

import java.net.InetAddress;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.restlet.Context;
import org.springfield.bart.homer.*;

public class BartContextListener implements ServletContextListener {

	private static LazyHomer lh = null; 
	
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("Bart: context initialized");
		ServletContext servletContext = event.getServletContext();
		
		// turn logging off
		Context.getCurrentLogger().setLevel(Level.SEVERE);
		Logger.getLogger("").setLevel(Level.SEVERE);
		
 		LazyHomer lh = new LazyHomer();

		lh.init(servletContext.getRealPath("/"));
		
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("Bart: context destroyed");
		
		
	}

}
