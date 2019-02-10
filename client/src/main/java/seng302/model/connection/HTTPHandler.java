package seng302.model.connection;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Serves as a single class that provides HTTP methods to allow the client to interact with the server
 * Currently ignoring the url setting since it complicates query parameters.
 * Ideally, we keep the URL static and within this class so we don't have to type in "localhost:8080/api/v1"
 * for all endpoints.
 */
public class HTTPHandler {

  private String url;
  private String localString = "http://localhost:8080/api/v1";
  private static HTTPHandler handler;

  private HTTPHandler() {
    this.url = localString;
  }

  public static HTTPHandler getHandler() {
    if(handler == null) {
      handler = new HTTPHandler();
    }
    return handler;
  }


  /**
   * Return a ResponseEntity for a request made to a URL
   * @param endpoint The endpoint URL (eg: /donors)
   * @param method The HttpMethod we're performing the request with
   * @param headerBody The header and body as a single HttpEntity
   *
   * The ResponseEntity returns the response as a string, so it will need to be further converted to a JSON since various
   * endpoints return string as well
   * @return A ResponseEntity with all information about the response received from the server
   */
  public static ResponseEntity getResponse(String endpoint, HttpMethod method, HttpEntity headerBody) {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate.exchange(endpoint, method, headerBody, String.class);
  }

  public HttpHeaders getHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    headers.add("x-auth-token", token);
    headers.add("Content-Type", "application/json");
    return headers;
  }
}
