package org.practice.Junit5;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import io.restassured.response.Response;

public class GetRoleTest {
	
	@Test
	public void getIdentity() {
		 String baseURL = ConfigReader.getBaseURL();
	        String username = ConfigReader.getUsername();     
	        String password = ConfigReader.getPassword();
	    String roleId = "0af116358d1d10dc818d20eeefac257f";
	    
	    String fullURL = baseURL + "/identityiq/scim/v2/Roles/" + roleId;
	    
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
