package com.noterik.springfield.bart.common;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import org.restlet.data.Form;
import org.restlet.data.Parameter;

//import com.noterik.bart.marge.model.Service;
//import com.noterik.bart.marge.server.MargeServer;

public class HelperFunctions {	
	/**
	 * Gets usermanager service
	 * @return
	 */
	/*
	public static Service getService(String domain, String serviceName) {
		//MargeServer marge = MargeServer.getInstance();
		MargeServer marge = new MargeServer();		
		HashMap<Integer, Service> map = marge.getServices(serviceName, domain);
		Service service =  null;
		
		// get current host
    	String hostIP = "UNKNOWN";
    	try {
    		hostIP = java.net.InetAddress.getLocalHost().getHostAddress(); // get this ip
		} catch (UnknownHostException e) {}
		
		// determine user manager on the same host
		Integer key;
		String serviceHost = null;
		String serviceHostIP = null;
		if(map!=null) {
			for(Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
				key = (Integer)iter.next();
				service = map.get(key);
				try {
					serviceHost = service.app_host();
					serviceHostIP = java.net.InetAddress.getByName(serviceHost).getHostAddress();
				} catch (UnknownHostException e) {
					System.out.println("Could not determine host address");
				}
				
				// preferably this server
				if(serviceHostIP!=null) {
					if(serviceHostIP.equals(hostIP)) {
						break;
					} 
				}
			}
		} 
		
		// check if service not is null
		if(service == null) {
			service = marge.getService(serviceName, domain);
		}
		
		return service;
	}
	*/
	
	public static String form2str(Form form) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		// loop through form data
		Parameter param;
		String key, value;
		boolean first = true;
		for(Iterator<Parameter> iter = form.iterator(); iter.hasNext(); ) {
			if(!first)
				sb.append(", ");
			param = iter.next();
			key = param.getName();
			value = param.getValue();
			sb.append("{"+key+":"+value+"}");
			first = false;
		}
		
		sb.append("]");
		return sb.toString();
	}
}
