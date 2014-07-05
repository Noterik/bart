package com.noterik.springfield.bart.authentication;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * Container for response of the usermanager
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.authentication
 * @access private
 * @version $Id: User.java,v 1.3 2011-01-05 08:40:38 pieter Exp $
 *
 */
public class User {
	/**
	 * Response of usermanager
	 */
	private Document response;
	
	
	private Document userdata;
	
	public User(Document response) {
		this.response = response;
	}
	public User(String response) throws DocumentException {
		this.response = DocumentHelper.parseText(response);
	}
	
	/**
	 * Getters
	 */
	public String getTicket() {
		String ticket = null;
		Node node = response.selectSingleNode("//ticket/ticketValue");
		if(node!=null) {
			ticket = node.getText();
		}
		return ticket;
	}
	
	public String getUserName() {
		String username = null;
		Node node = response.selectSingleNode("//user/@id");
		if(node!=null) {
			username = node.getText();
		}
		return username;
	}
	
	/**
	 * Does the response contain errors
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		Node node = response.selectSingleNode("//root/error");
		if(node!=null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Convert to fsxml
	 * @return
	 */
	public String asXML() {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<user id=\""+getUserName()+"\">");
		xml.append("<properties>");
		xml.append("<ticket>" + getTicket() + "</ticket>");
		xml.append("</properties>");
		xml.append("</user>");
		return xml.toString();
	}
	
	
	public void setUserData(String response) {
		System.out.println(response);
		try {
			this.userdata = DocumentHelper.parseText(response);
		} catch (DocumentException e) { }
	}
	
	public boolean isAdmin() {
		Node node = userdata.selectSingleNode("//group[@id='admin']");
		if (node!=null) {
			System.out.println("is admin");
			return true;
		}
		System.out.println("no admin");
		return false;
	}
}
