package seng302.model.databaseTests.sprintBootTests;


import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import seng302.model.database.DBCConnection;
import static seng302.model.databaseTests.testingUtilities.
        EndpointInteractionUtilities.createHeaders;
import static seng302.model.databaseTests.testingUtilities.EndpointInteractionUtilities.login;


/**
 * This class contains a series of tests for the DELETE clinician endpoint and
 * uses Spring Boot's testing utilities alongside JUnit.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteClinicianTest {


  // Class attributes.
  @Autowired
  private TestRestTemplate template;
  private static final String TEST_CLINICIAN_ID = "11";
  private static final String DEFAULT_ADMIN_USERNAME = "Sudo";
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
   * Confirms that the DELETE endpoint for clinicians will succeed when a valid
   * staff ID is provided alongside clinician authorisation. This should fail,
   * and the server should respond with status code 403 (FORBIDDEN)
   */
  @Ignore
  @Test
  public void clinicianDeleteEndpointTestWithValidClinicianAuthorisationAndID()
          throws JSONException {

    // Create test clinician.
    String token = login("5", DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.CREATED, createTestClinician(token));

    // Attempt to delete clinician as clinician.
    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteClinician(token,
            TEST_CLINICIAN_ID));
    Assert.assertTrue(checkClinicianExists(token, TEST_CLINICIAN_ID));

    // Delete as admin (test cleanup).
    token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    deleteClinician(token, TEST_CLINICIAN_ID);

  }


  /**
   * Attempts to delete a clinician using the DELETE endpoint with donor-
   * receiver authorisation and a valid staff ID. This operation should fail,
   * and the server should respond with status code 403 (FORBIDDEN).
   */
  @Ignore
  @Test
  public void clinicianDeleteEndpointTestWithValidDonorReceiverAuthorisationAndID()
          throws JSONException {

    // Create test clinician.
    String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.CREATED, createTestClinician(token));

    // Attempt to delete clinician as donor-receiver.
    token = login("AAA9999", DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteClinician(token,
            TEST_CLINICIAN_ID));

    // Confirm account still exists and then delete as admin (test cleanup).
    token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertTrue(checkClinicianExists(token, TEST_CLINICIAN_ID));
    deleteClinician(token, TEST_CLINICIAN_ID);

  }


  /**
   * Attempts to delete a clinician using the DELETE endpoint with admin
   * authorisation and a valid staff ID. This operation should succeed and the
   * server should respond with status code 200 (OK).
   */
  @Ignore
  @Test
  public void clinicianDeleteEndpointTestWithValidAdminAuthorisationAndID()
          throws JSONException{

    // Create test clinician and confirm their existence.
    String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.CREATED, createTestClinician(token));
    Assert.assertTrue(checkClinicianExists(token, TEST_CLINICIAN_ID));

    // Attempt donor deletion, and confirm deletion.
    Assert.assertEquals(HttpStatus.ACCEPTED, deleteClinician(token, TEST_CLINICIAN_ID));
    Assert.assertFalse(checkClinicianExists(token, TEST_CLINICIAN_ID));

  }


  /**
   * Will endeavour to delete a clinician with the DELETE endpoint with no
   * authorisation. This should fail and cause the server to respond with
   * status code 403 (FORBIDDEN).
   */
  @Test
  public void clinicianDeleteEndpointTestWithNoAuthorisation() {

    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteClinician("", TEST_CLINICIAN_ID));

  }


  /**
   * Tries to delete a clinician with the DELETE endpoint using an invalid staff
   * ID (i.e. one that does not exist). The server should respond with status
   * code 404 (NOT FOUND) indicating that no staff member was found.
   */
  @Test
  public void clinicianDeleteEndpointTestWithInvalidID() throws JSONException {

    String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.NOT_FOUND, deleteClinician(token,
            "9999"));

  }


  /**
   * Attempts to delete the default clinician with valid admin authorisation.
   * This should result in failure and no action should be executed on the
   * database.
   * @throws JSONException
   */
  @Test
  public void clinicianDeleteEndpointTestWithValidAuthorisationAndDefaultID()
          throws JSONException {

    String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
    Assert.assertEquals(HttpStatus.FORBIDDEN, deleteClinician(token, "0"));
    boolean value = checkClinicianExists(token, "0");
    Assert.assertTrue(checkClinicianExists(token, "0"));

  }


  /**
   * Helper method which returns true if the test clinician exists. If the test
   * clinician is not found, the method will return false.
   * @param token A login token issued by the server for authorisation.
   * @param checkID The ID of the staff member being checked.
   * @return True if the clinician exists, false otherwise.
   */
  private boolean checkClinicianExists(String token, String checkID) {

    HttpEntity<String> request = new HttpEntity<String>("", createHeaders(token));
    ResponseEntity response = template.exchange("/api/v1/clinicians/" +
            checkID, HttpMethod.GET, request, String.class);

    try {

      String staffID = new JSONObject((String) response.getBody()).
              getString("userName");
      return staffID.equals(checkID);

    } catch (Exception exception) {

      return false;

    }

  }


  /**
   * Helper method which creates a test clinician.
   * @return A status code indicating the result of the operation.
   */
  private HttpStatus createTestClinician(String token) throws JSONException {

    JSONObject clinician = new JSONObject();
    clinician.put("username", TEST_CLINICIAN_ID);
    clinician.put("givenName", "Lara");
    clinician.put("lastName", "Croft");
    clinician.put("password", DEFAULT_PASSWORD);
    clinician.put("streetAddress", "Croft Manor");
    clinician.put("region", "Abbingdon");

    HttpEntity<String> request = new HttpEntity<String>(clinician.toString(),
            createHeaders(token));
    ResponseEntity response = template.exchange("/api/v1/clinicians",
            HttpMethod.POST, request, String.class);
    return response.getStatusCode();

  }


  /**
   * Helper method which deletes the specified clinician.
   * @param token A login token issued by the server for authorisation.
   * @param staffID The staff ID of the clinician to delete.
   * @return A status code indicating the result of the operation.
   */
  private HttpStatus deleteClinician(String token, String staffID) {

    HttpEntity<String> request = new HttpEntity<String>("", createHeaders(token));
    ResponseEntity response = template.exchange("/api/v1/clinicians/" +
            staffID, HttpMethod.DELETE, request, String.class);
    return response.getStatusCode();

  }


}
