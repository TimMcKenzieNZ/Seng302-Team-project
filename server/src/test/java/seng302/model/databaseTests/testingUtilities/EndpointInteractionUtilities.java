package seng302.model.databaseTests.testingUtilities;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


/**
 * This class provides a set of common methods which are used in multiple Spring
 * tests including those in DeleteDonorReceiverTest and DeleteClinicianTest.
 * They are provided as public static methods.
 */
public class EndpointInteractionUtilities {


  /**
   * Private constructor for the EndpointInteractionUtilities class. It serves
   * no purpose other than to prevent instantiation with a default constructor.
   */
  private EndpointInteractionUtilities() {

    // This method has no function.

  }


  /**
   * Attempts to perform a login operation by sending a POST request to the
   * server's login endpoint with the username and password specified as the
   * input parameters username and password.
   * @param username The account to login as a String object.
   * @param password The password of the account as a String object.
   * @param template The TestRestTemplate used to make the request.
   * @return The session token returned by the server as a String object.
   * @throws JSONException If a problem occurred processing JSON objects.
   */
  public static String login(String username, String password, TestRestTemplate template) throws JSONException {

    JSONObject credentials = new JSONObject();
    credentials.put("username", username);
    credentials.put("password", password);

    HttpEntity<String> requestEntity = new HttpEntity<>(credentials.toString(), createHeaders());
    ResponseEntity response = template.exchange("/api/v1/login", HttpMethod.POST, requestEntity, String.class);
    return new JSONObject((String) response.getBody()).getString("token");

  }


  /**
   * Creates a new HttpHeaders instance with content type set to JSON.
   * @return The created object.
   */
  public static HttpHeaders createHeaders() {

    return createHeaders(null);

  }


  /**
   * Creates a new HttpHeaders object with the content type set to JSON.
   * @param token The current session ID as a String object.
   * @return The new HttpHeaders instance.
   */
  public static HttpHeaders createHeaders(String token) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    if (token != null) {
      httpHeaders.add("x-auth-token", token);
    }
    return httpHeaders;

  }


}
