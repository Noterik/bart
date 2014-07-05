package com.noterik.springfield.bart.restlet;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.noterik.springfield.bart.transform.FormTransformer;

/**
 * Resource used for testing
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.bart.test
 * @access private
 * @version $Id: TestResource.java,v 1.2 2011-11-15 13:22:28 derk Exp $
 *
 */
public class TestResource extends ServerResource {
	
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
		// get testcase
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String testcase = form.getFirstValue("testcase", "default");
		form.removeFirst("testcase");
		
		// get representation
		Representation rep = null;
		
		// transformer test
		if(testcase.equals("transform")) {
			String fsxml = FormTransformer.transform(form);
			rep = new StringRepresentation(fsxml,MediaType.TEXT_XML);
		} 
		// default
		else {
			rep = new StringRepresentation("It works!");
		}
		
		return rep;
	}
}
