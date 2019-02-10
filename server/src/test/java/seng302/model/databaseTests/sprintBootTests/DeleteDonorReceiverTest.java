package seng302.model.databaseTests.sprintBootTests;


import org.json.JSONException;
import org.json.JSONObject;

import org.junit.*;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import seng302.model.database.DBCConnection;
import static seng302.model.databaseTests.testingUtilities.EndpointInteractionUtilities.createHeaders;
import static seng302.model.databaseTests.testingUtilities.EndpointInteractionUtilities.login;


/**
 * This class contains a series of tests for the DELETE donor-receivere
 * endpoint and uses Spring Boot's testing utilities alongside JUnit.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteDonorReceiverTest {


  // Class attributes.
  @Autowired private TestRestTemplate template;
  private static final String CLINICIAN_USERNAME = "5";
  private static final String DEFAULT_PASSWORD = "password";

  /**
   * Configures DBCConnection to use the test database instead of production.
   * This is called before the server is run.
   */
  @BeforeClass
  public static void setUp() {

    DBCConnection.setTestDatabase(true);

  }


  /**
   * Configures DBCConnection to use its default database, which some tests
   * expect and would break without.
   */
  @AfterClass
  public static void tearDown() {

    DBCConnection.setTestDatabase(false);

  }


  /**
   * Sends a DELETE request to the donor-receiver endpoint with authorisation
   * and an valid NHI. This should delete the donor-receiver account and return
   * a status code of 200 indicating a successful operation.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  @Ignore
  @Test
  public void deleteEndpointTestWithClinicianAuthorisationAndValidNHI() throws JSONException {

    // Create test donor and confirm their existence.
    Assert.assertEquals(HttpStatus.CREATED, createTestDonorReceiver(CLINICIAN_USERNAME));
    String token = login(CLINICIAN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertTrue(checkTestDonorExists(token));

    // Delete donor, and confirm deletion.
    Assert.assertEquals(HttpStatus.ACCEPTED, deleteTestDonorReceiver(token, "HSH1859"));
    Assert.assertFalse(checkTestDonorExists(token));

  }


  /**
   * Performs the same operation as deleteEndpointTestWithClinicianAuthorisationAndValidNHI,
   * except admin authorisation is used instead.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  @Ignore
  @Test
  public void deleteEndpointTestWithAdministratorAuthorisationAndValidNHI() throws JSONException {


    // Create test donor and confirm their existence.
    Assert.assertEquals(HttpStatus.CREATED, createTestDonorReceiver(CLINICIAN_USERNAME));
    String token = login("Sudo", DEFAULT_PASSWORD, template);
    Assert.assertTrue(checkTestDonorExists(token));

    // Delete donor, and confirm deletion.
    Assert.assertEquals(HttpStatus.ACCEPTED, deleteTestDonorReceiver(token, "HSH1859"));
    Assert.assertFalse(checkTestDonorExists(token));

  }


  /**
   * Sends a DELETE request to the donor-receiver endpoint with donor-receiver
   * authorisation. This should fail, generating a response with status 403.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  @Ignore
  @Test
  public void deleteEndpointTestWithDonorReceiverAuthorisationAndValidNHI() throws JSONException {

    // Attempt to delete as donor-receiver.
    Assert.assertEquals(HttpStatus.CREATED, createTestDonorReceiver("HSH1859"));
    String token = login("HSH1859", DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteTestDonorReceiver(token, "HSH1859"));
    Assert.assertTrue(checkTestDonorExists(token));

    // Delete as clinician (test cleanup).
    token = login(CLINICIAN_USERNAME, DEFAULT_PASSWORD, template);
    deleteTestDonorReceiver(token, "HSH1859");

  }


  /**
   * Sends a DELETE request to the donor-receiver endpoint with no authorisation
   * and a valid NHI. This should return a 400 unauthorised status code.
   */
  @Test
  public void deleteEndpointTestWithNoAuthorisationAndValidNHI() {

    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteTestDonorReceiver("", "HSH1859"));

  }


  /**
   * Sends a DELETE request to the donor-receiver endpoint with authorisation
   * and no NHI. The returning status code should be 405, indicating that no
   * endpoint exists deleting a user without specifying their NHI.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  @Ignore
  @Test
  public void deleteEndpointTestWithClinicianAuthorisationAndEmptyNHI() throws JSONException {

    String token = login(CLINICIAN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, deleteTestDonorReceiver(token, ""));

  }


  /**
   * Sends a DELETE request to the donor-receiver endpoint with authorisation
   * and an invalid NHI. The returning status code should be 400, indicating
   * that the user's original input was incorrect.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  @Ignore
  @Test
  public void deleteEndpointTestWithClinicianAuthorisationAndInvalidNHI() throws JSONException {
    String token = login(CLINICIAN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.BAD_REQUEST, deleteTestDonorReceiver(token, "III0000"));
  }





  /**
   * Deletes the test donor-receiver.
   * @param token The current session token as a String object.
   * @return The HTTP status code received in response as an HttpStatus object.
   */
  private HttpStatus deleteTestDonorReceiver(String token, String nhi) {

    HttpEntity<String> request = new HttpEntity<String>("", createHeaders(token));
    ResponseEntity response = template.exchange("/api/v1/donors/" + nhi, HttpMethod.DELETE, request, String.class);
    return response.getStatusCode();

  }

  /**
   * Creates test donor-receiver account HSH1859 used in deletion tests.
   * @param modifyingAccount The username of the account creating the test
   *                         donor-receiver as a String object.
   * @return The status code of the response as an HttpStatus object.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  private HttpStatus createTestDonorReceiver(String modifyingAccount) throws JSONException{

    JSONObject donorReceiver = new JSONObject();
    donorReceiver.put("nhi", "HSH1859");
    donorReceiver.put("givenName", "Honor");
    donorReceiver.put("middleName", "Stephanie");
    donorReceiver.put("lastName", "Harrington");
    donorReceiver.put("dateOfBirth", "1859-10-01");
    donorReceiver.put("password", "password");
    donorReceiver.put("modifyingAccount", modifyingAccount);

    HttpEntity<String> request = new HttpEntity<String>(donorReceiver.toString(), createHeaders());
    ResponseEntity response = template.exchange("/api/v1/donors", HttpMethod.POST, request, String.class);
    return response.getStatusCode();

  }


  /**
   * Confirms that the test donor exists in the database.
   * @return True if the test donor exists, and false if it does not.
   * @throws JSONException If a problem occurs processing JSON objects.
   */
  private boolean checkTestDonorExists(String token) throws JSONException {

    HttpEntity<String> request = new HttpEntity<String>("", createHeaders(token));
    ResponseEntity response = template.exchange("/api/v1/donors/HSH1859", HttpMethod.GET, request, String.class);

    try {

      String nhi = new JSONObject((String) response.getBody()).getString("userName");
      return nhi.equals("HSH1859");

    } catch (Exception exception) {

      return false;

    }

  }


}
