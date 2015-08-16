/**
 * 
 */
package com.test.ingestion.pojo;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Kaniska_Mandal
 * This is used by Query Module.
 * All the incoming objects are treated as Generic Entity
 */
@XmlType(name = "GenericEntity") 
public class GenericEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> keyValueList = new ArrayList<String>();

	/**
	 * @return the keyValueList
	 */
	public ArrayList<String> getKeyValueList() {
		return keyValueList;
	}

}
