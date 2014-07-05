package com.noterik.springfield.bart.transform;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Parameter;

/**
 * Transforms FORM-data to fsxml
 *
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.bart.ps.transform
 * @access private
 * @version $Id: FormTransformer.java,v 1.16 2011-01-05 08:40:59 pieter Exp $
 *
 */
public class FormTransformer {
	
	/**
	 * reserved names
	 */
	public static final List<String> reserved = Arrays.asList(new String[]{"method","command","id","referid","mimetype","user","ticket","datatype", "bsid"});
	
	/**
	 * attribute reserved names
	 */
	public static final List<String> attributes = Arrays.asList(new String[]{"id","referid","mimetype"});
	
	/**
	 * fscommand mimetype
	 */
	public static final String FSCOMMAND_MIMETYPE = "application/fscommand";
	
	/**
	 * Transforms form data to fsxml
	 * @param form
	 * @return
	 */
	public static String transform(Form form) {		
		return transform2FsXML(form);
	}
	
	/**
	 * Transform to fsxml
	 * 
	 * @param form
	 * @return
	 */
	private static String transform2FsXML(Form form) {
		StringBuffer sb = new StringBuffer();
		StringBuffer attr = new StringBuffer();
		StringBuffer attrXml = new StringBuffer();
		StringBuffer props = new StringBuffer();
				
		// determine the the type of the data
		String datatype = form.getFirstValue("datatype", "").toLowerCase();
		
		// loop through form data
		Parameter param;
		String key = "", value, valueSingle = "";
		for(Iterator<Parameter> iter = form.iterator(); iter.hasNext(); ) {
			param = iter.next();
			key = param.getName();
			value = param.getValue();
			if(value==null) {
				value="";
			}
			
			// check if key is an attribute and not a single value
			if(!reserved.contains(key) && !datatype.equals("value")) {
				props.append("<"+key+">"+value+"</"+key+">");
			} 
			else if(attributes.contains(key) && !datatype.equals("value")) { 
				attr.append(key+"='"+value+"' ");
				attrXml.append("<"+key+">"+value+"</"+key+">");
			}
			else {
				valueSingle = key;
				if(valueSingle.equals("datatype")) {
					//valueSingle = "";
				}
			}
		}
		
		// build complete
		if (datatype.equals("value")) {
			sb.append(valueSingle);
		} else if (datatype.equals("attributes")) {
			sb.append("<fsxml>");
			sb.append("<attributes>"+attrXml+"</attributes>");
			sb.append("</fsxml>");
		} else {
			sb.append("<fsxml " + attr + ">");
			sb.append("<properties>"+props+"</properties>");
			sb.append("</fsxml>");
		}
		
		return sb.toString();
	}
}
