package seng302.services;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Performs a synchronous request on the main thread so we can handle responses from the server
 * in cases where we need to perform
 */
public class SyncRequestTask {
  Class classOfObject;
  protected String url;
  Object body;
  String authenticationToken;
  HttpMethod method;
  public HttpStatus responseStatus;
  public Object responseBody;

  public Object makeRequest() throws HttpClientErrorException {
    ResponseEntity responseEntity = RequestMethods.makeRequest(authenticationToken, body, url, method, classOfObject);
    responseStatus = responseEntity.getStatusCode();
    responseBody = responseEntity.getBody();
    return responseBody;
  }
}
