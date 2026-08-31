package org.practice.Junit5;

import org.junit.Test;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GetIdentityTest {
	
	@Test
	public void getIdentity() {
	   
		 String baseURL = ConfigReader.getBaseURL();
	        String username = ConfigReader.getUsername();     
	        String password = ConfigReader.getPassword();
	    String userId = "0af11810a037113d81a03d9a2e06695e";
	    
	    String fullURL = baseURL + "/identityiq/scim/v2/Users/" + userId;
	    
	    Response response = 
	        given()
	            .relaxedHTTPSValidation()
	            .auth().basic(username, password)
	            .header("Content-Type", "application/json")
	        
	        .when()
	            .get(fullURL);
	    
	    System.out.println("Status Code: " + response.getStatusCode());
	    System.out.println("Response: " + response.getBody().asString());
	    
	    response.then()
	        .statusCode(200);
	    
	    
	}

}
