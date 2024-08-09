package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.UserEndPoints;
import api.payload.User;
import api.utilities.ExtentManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserTests {
	
	 @BeforeSuite
	  public void beforeSuite() {
	    ExtentManager.setExtent();
	  }
	
	Faker faker;
	User userPayload;
	
	public Logger logger;
	
    @BeforeClass
    public void setupData() 
    {
    	faker = new Faker();
    	userPayload = new User();
    	
    	userPayload.setId(faker.idNumber().hashCode());
    	userPayload.setUsername(faker.name().username());
    	userPayload.setFirstName(faker.name().firstName());
    	userPayload.setLastName(faker.name().lastName());
    	userPayload.setEmail(faker.internet().safeEmailAddress());
    	userPayload.setPassword(faker.internet().password(5, 10));
    	userPayload.setPhone(faker.phoneNumber().cellPhone());	
    	
    	//Logs
    	logger = LogManager.getLogger(this.getClass());
    }
    
    @Test(priority=1)
    public void testPostUser()
    {
    	
    	 logger.info("********** Creating User **********");

    	Response response = UserEndPoints.createUser(userPayload);
    	response.then().log().all();
    	Assert.assertEquals(response.getStatusCode(), 200);
    	
    	logger.info("********** User is created Successfully **********");
    }
    
    @Test(priority=2)
    public void testGetUserByName()
    
    {
    	 logger.info("********** Reading User Info **********");
    	 
    	Response response = UserEndPoints.readUser(this.userPayload.getUsername());
    	response.then().log().all();
    	Assert.assertEquals(response.getStatusCode(), 200);
    	
    	logger.info("********** User Info is displayed Successfully **********");
    	
    }
    
    @Test(priority=3)
    public void testUpdateUserByName()
    {
    	
    	logger.info("********** Updating User **********");
   
    	userPayload.setUsername(faker.name().username());
    	userPayload.setFirstName(faker.name().firstName());
    	userPayload.setLastName(faker.name().lastName());
    	userPayload.setEmail(faker.internet().safeEmailAddress());
    	
    	Response response = UserEndPoints.updateUser(this.userPayload.getUsername(), userPayload);
    	response.then().log().all().extract().response().asString();
    	Assert.assertEquals(response.getStatusCode(), 200);
    	
    	logger.info("********** User is Updated Successfully **********");

        // Checking data after update
    	
    	Response responseAfterUpdate =  UserEndPoints.readUser(this.userPayload.getUsername());
    	String verifyResponse = responseAfterUpdate.then().log().body().extract().asString();
    	Assert.assertEquals(responseAfterUpdate.getStatusCode(), 200);
    	JsonPath js = new JsonPath(verifyResponse);
    	String username =js.get("username");
    	Assert.assertEquals(userPayload.getUsername(),username);
    }
    
   

	@Test(priority=4)
    public void testDeleteUserByName()
    {
		logger.info("********** Deleting User **********");
		
    	Response response = UserEndPoints.DeleteUser(this.userPayload.getUsername());
    	response.then().log().all();
    	Assert.assertEquals(response.getStatusCode(), 200);
    	
    	logger.info("********** User is Deleted Successfully **********");
    	
    }
    
    @AfterSuite
    public void afterSuite() {
      ExtentManager.endReport();
    }
    
}
