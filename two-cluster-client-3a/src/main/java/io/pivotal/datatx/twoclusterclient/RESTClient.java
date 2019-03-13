package io.pivotal.datatx.twoclusterclient;

import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RESTClient {
	
	private static Logger log = LogManager.getLogger(RESTClient.class);
	
	private String baseURL;
	
	public void setBaseURL(String url){
		baseURL = url;
	}
	
	public void put(String region, Object key, PdxInstance value){
		String postURL = String.format("%s/v1/%s", baseURL,region);
		log.info("POST TO " + postURL);
		
		TypePreservingPdxToJSON converter = new TypePreservingPdxToJSON(value);
		String body = converter.getJSON();
		log.info("BODY: " + body);
		
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.post(postURL)
					  .header("accept", "application/json")
					  .header("Content-Type", "application/json")
					  .queryString("key", key.toString())
					  .body(body)
					  .asJson();

			int rc = jsonResponse.getStatus();
			if (rc != 201)
				throw new RuntimeException("That didn't work.  rc was " + rc);
				
		} catch (UnirestException e) {
			log.error("An error occurred while accessing the REST API", e);
			throw new RuntimeException("That didn't work");
		}
	}
}
