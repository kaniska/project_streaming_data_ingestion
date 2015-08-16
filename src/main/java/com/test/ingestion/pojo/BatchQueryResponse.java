package com.test.ingestion.pojo;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This Http Response Payload embodies the required list of column-values;
 * so that UI can easily iterate through the list and show the data in a table.
 *  
 * @author Kaniska_Mandal
 *
 */
@XmlRootElement(name="ResponseDataForReadRequest")
public class BatchQueryResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<GenericEntity> entityList = new ArrayList<GenericEntity>();

	@XmlElement
	@XmlElementWrapper(name = "entityList")
	public ArrayList<GenericEntity> getEntityList() {
		return entityList;
	}
}
