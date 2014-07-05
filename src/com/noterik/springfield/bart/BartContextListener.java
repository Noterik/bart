package com.noterik.springfield.bart;

import java.net.InetAddress;
import com.noterik.springfield.bart.homer.*;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.restlet.Context;

import com.noterik.springfield.bart.encryption.DomainUpdater;
import com.noterik.springfield.bart.homer.LazyHomer;

public class BartContextListener implements ServletContextListener {

	private static final int RETRY = 5 * 60 * 1000;
	private Timer timer = new Timer();
	private static LazyHomer lh = null; 
	
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("Bart: context initialized");
		ServletContext servletContext = event.getServletContext();
		
		// turn logging off
		Context.getCurrentLogger().setLevel(Level.SEVERE);
		Logger.getLogger("").setLevel(Level.SEVERE);
		
		// start timer
		timer.schedule(new DomainUpdater(), 0, RETRY);
		
 		LazyHomer lh = new LazyHomer();

		lh.init(servletContext.getRealPath("/"));
		
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("Bart: context destroyed");
		
		// stop timer
		timer.cancel();
		
	}

}
