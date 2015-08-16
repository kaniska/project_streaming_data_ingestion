package com.test.ingestion.rest;

import java.sql.SQLException;
import java.util.Iterator;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.test.ingestion.pojo.BatchRequestContext;
import com.Test.ca.metadata.service.BusinessObjectMetadataService;
import com.Test.Test.core.error.ResourceError;
import com.Test.Test.util.TestLogger;
import com.Test.Test.util.StringUtil;

/**
 * Data Collection Webservice Provider
 * @author Kaniska
 */
@Controller
@RequestMapping("/entities/template")
public class DatasetTemplateRestController {

	private static final TestLogger log = new TestLogger(DatasetTemplateRestController.class);

	@Autowired
	private BusinessObjectMetadataService businessObjMetadataService;
	
	// http://localhost:9080/test/api/entities/batch/initialize

	private static HttpHeaders createCommonHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.set("Pragma", "no-cache");
		headers.set("Expires", "0");
		headers.set("Content-Type", "text/xml");
		
		return headers;
	}

	
	// http://localhost:9080/test/api/entities/batch/add
	

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="text/plain", produces="text/xml")
	@RolesAllowed({com.Test.Test.core.TestConstants.Test_ANALYTICS_TENANTADMIN, com.Test.Test.core.TestConstants.Test_ANALYTICS_INTEGRETION_MANAGER})
	
	public ResponseEntity<String> addDatasetTemplate(@RequestBody String jsonTemplate,  @RequestParam("dsname") String dsName,
	        @RequestParam("appid") String appId)  {
		ResponseEntity<String> response = new ResponseEntity<String>(createCommonHeader(), org.springframework.http.HttpStatus.OK );
		try {
			// TODO we just need the dsname. Since the seeded dsname is not defined like sfdc-ee, sfdc-pe, i am passing
			// the appid for now, which are fixed. In the runtime, we will fetch the tenant appds id for the given tenant id and app id.
	        if(StringUtil.isEmpty(dsName)) {
	            throw new Exception("Datasource name is empty in the queryparam");
	        }
	        if(StringUtil.isEmpty(appId)) {
	            throw new Exception("App Id is empty in the queryparam");
	        }
			
			businessObjMetadataService.createTablesAndBusinessObjectDef(jsonTemplate, Integer.parseInt(appId), dsName);
			//TODO send a custom Response XML			
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<String> (org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
		}
		
		return response;
	}
	
	
	
	/**
	 * Playing a trick here : Do not set the server error code in Http header.
	 * Then the TEST Http Client will error out and exit the TEST process immediately.
	 * Set a normal code (202) as Response header and set the actual server error code 
	 * along with Error log inside the Response Data
	 * 
	 * @param e
	 * @param requestContext
	 * @return
	 */
	private String logError(Exception e, BatchRequestContext requestContext) {
		int errorCode = 0;
		SQLException sqlEx = null;
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(e.getMessage());
		if (e instanceof SQLException) {
			sqlEx = (SQLException)e;
			errorCode = ((SQLException) e).getErrorCode();
		    Iterator<Throwable> iter = sqlEx.iterator();
		    int i = 1;
		    while(iter.hasNext()) {
		    	sqlEx = (SQLException) iter.next();
		    	errorMessage.append("\n Message ");
		    	errorMessage.append(i++);
		    	errorMessage.append(" -- ");
		    	errorMessage.append(iter.next());
		    	
		    	break;
		    }
		}
		try {
			/*boMetadataService.logInProcessError(requestContext, errorCode
					+ "", errorMessage.toString());*/
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			requestContext.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return e.getMessage();
	}

}
