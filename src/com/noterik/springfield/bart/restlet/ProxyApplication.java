package com.noterik.springfield.bart.restlet;

import org.restlet.Application;
import org.restlet.Restlet;

import javax.servlet.ServletContext;


public class ProxyApplication extends Application {
	
    @Override
    public Restlet createRoot() {  
    	System.out.println("STARTING BART SERVLET");
		return new ProxyRestlet(super.getContext());
    }
}
