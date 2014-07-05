package com.noterik.springfield.bart.encryption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

//import com.noterik.bart.marge.model.Service;
//import com.noterik.bart.marge.server.MargeServer;
import com.noterik.springfield.bart.common.HelperFunctions;
import com.noterik.springfield.tools.HttpHelper;
import com.noterik.springfield.bart.model.*;

public class DomainUpdater extends TimerTask {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(DomainUpdater.class);


	private static final String CHECK_URI = "/domain/{domain}/config/security/properties";
	
	//private static final Boolean localmode = true;
	
	public void run() {
		// get domains
		List<String> domains = getDomains();
		
		// loop over all domains
		for(String domain : domains) {
			boolean enabled = false;
			if (1==2) {
				enabled = securityEnabled(domain);
			}
			DomainList.instance().updateDomain(domain, enabled);
		}
	}
	
	/**
	 * Returns the domains gotten from homer.
	 * 
	 * @return The domains gotten from homer
	 */
	private List<String> getDomains() {
		List<String> domains = new ArrayList<String>();
		/*
		try {
			if (1==2) {
				// get homer
				MargeServer marge = new MargeServer();
				Service homer = marge.getHomer();
				if(homer == null) {
					return null;
				}
			
				// request domains
				String response = HttpHelper.sendRequest("GET", homer.getUrl() + "/xml/domain", null, null);
			
				// parse response
				Document doc = DocumentHelper.parseText(response);
				List nodes = doc.selectNodes("//domain");
				Element elem;
				for(Iterator iter = nodes.iterator(); iter.hasNext(); ) {
					elem = (Element)iter.next();
					domains.add(elem.valueOf("name"));
				}
			}
			
		} catch (Exception e) {
			return null;
		}
		*/
		return domains;
	}
	
	private boolean securityEnabled(String domain) {
		boolean enabled = false;
		

		
		//Service service = HelperFunctions.getService(domain, "filesystemmanager");
		/*
		if(service!=null) {
			String response = HttpHelper.sendRequest("GET", service.getUrl()+CHECK_URI.replace("{domain}", domain), null, null);
			try {
				Document doc = DocumentHelper.parseText(response);
				enabled = doc.selectSingleNode("//properties/enabled") == null ? false : Boolean.parseBoolean(doc.selectSingleNode("//properties/enabled").getText());
			} catch (DocumentException e) {} 
		}
		*/			
		return enabled;
	}
	
}
