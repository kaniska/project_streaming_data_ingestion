package com.test.ingestion.rest;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * http://<ip>:<port>/test/ping
 * @author Kaniska
 */
@Controller
@RequestMapping("/")
@Scope("singleton")
public class HealthCheckController {

	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces="text/plain")
	public ResponseEntity<String>  ping() {
		return new ResponseEntity<String>("pong",createCommonHeader(), org.springframework.http.HttpStatus.OK );
	}

	
	private static HttpHeaders createCommonHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.set("Pragma", "no-cache");
		headers.set("Expires", "0");
		headers.set("Content-Type", "text/xml");
		
		return headers;
	}
	
}
