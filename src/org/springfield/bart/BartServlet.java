/* 
* BartServlet.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
*
* Bart is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Bart is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.bart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;
import org.springfield.bart.homer.LazyHomer;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.security.utils.Base64;



/**
 * Servlet implementation class ServletResource
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.servlet
 */
@WebServlet("/BartServlet")
public class BartServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(BartServlet.class);
	private static final long serialVersionUID = 42L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public  BartServlet() {
        super();
        System.out.println("Bart servlet object created");
        // TODO Auto-generated constructor stub
    }
    
  
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String account = null;
		String password = null;
		
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		
		String authorization = request.getHeader("Authorization");
	    if (authorization!=null && authorization.startsWith("Basic")) {
	    	try {
	    		String base64 = authorization.substring("Basic".length()).trim();
	    		String values = new String(Base64.decode(base64),Charset.forName("UTF-8"));
	    		final String[] p = values.split(":",2);
	    		account = p[0];
	    		password = p[1];
	    	} catch(Exception e) {
	    		e.printStackTrace();
				response.setContentType("text/xml; charset=UTF-8");
				response.setHeader("WWW-Authenticate", "BASIC realm=\"springfield\""); 
				response.setStatus(401);
				return;
	    	}
	    } else {
			response.setContentType("text/xml; charset=UTF-8");
			response.setHeader("WWW-Authenticate", "BASIC realm=\"springfield\""); 
			response.setStatus(401);
			return;
	    }
	    
		String uri = request.getRequestURI();
				
		response.setContentType("text/xml; charset=UTF-8");
		OutputStream out = response.getOutputStream();
		ServiceInterface smithers = ServiceManager.getService("smithers");
		String body ="";
		if (smithers==null) {
			body = createFsxmlError("no active smithers",500);
		} else if (getDomain(uri).equals("internal")) {
			body = createFsxmlError("internal domain is never allowed from bart",401);
		} else {
			String ds = request.getParameter("depth");

			// some checks on the depth setting
			int depth = 0;
			try {
				depth = Integer.parseInt(ds); // parse it to get in secure
			} catch(Exception e) { depth = 0; }
			if (depth<0 || depth>10) depth = 0;

			ServiceInterface barney = ServiceManager.getService("barney");
			if (barney!=null) {
				// perform a security check can we read the url including the depth ?
				String duri = getDomainString(uri);
				String allowed = barney.get("allowed(read,"+duri+","+depth+","+account+","+password+")",null,null);
				if (allowed!=null && allowed.equals("true")) {
					String xml = "<fsxml><properties><depth>"+depth+"</depth></properties></fsxml>";
					body = smithers.get(duri,xml,"text/xml");
				} else {
					//body = createFsxmlError("not allowed to read that uri or depth",401);
					response.setContentType("text/xml; charset=UTF-8");
					response.setHeader("WWW-Authenticate", "BASIC realm=\"springfield\""); 
					response.setStatus(401);
				}
			} else {
				body = createFsxmlError("can't reach barney to check security",500);
			}
			out.write(body.getBytes());
			out.close();
		}
		out.write(body.getBytes());
		out.flush();
		out.close();
		return;
	}
	
	/**
	 * Post request handles mainly external requests
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		
		String uri = request.getRequestURI();
		return;
	}
	
	public String getDomain(String uri) {
		String result = uri.substring(uri.indexOf("/domain/")+8);
		result = result.substring(0,result.indexOf('/'));
		return result;
	}
	
	public String getDomainString(String uri) {
		String result = uri.substring(uri.indexOf("/domain/"));
		return result;
	}
	
	private String createFsxmlError(String reason,int code) {
		return "<fsxml><error><properties><code>"+code+"</code><reason>"+reason+"</reason></properties></error></fsxml>";	
	}
	
}
