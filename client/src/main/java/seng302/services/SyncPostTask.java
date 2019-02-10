package seng302.services;

import org.springframework.http.HttpMethod;

public class SyncPostTask extends SyncRequestTask {

  /**
   * Constructor with authentication token.
   *
   * @param classOfObject       Class of object in body of response.
   * @param server              Address of the server.
   * @param endpoint            The endpoint.
   * @param body                Object to go in body of request.
   * @param authenticationToken The authentication token.
   */
  public SyncPostTask(Class<?> classOfObject, String server, String endpoint, Object body, String authenticationToken) {
    this.method = HttpMethod.POST;
    this.classOfObject = classOfObject;
    this.url = server + endpoint;
    this.body = body;
    this.authenticationToken = authenticationToken;
  }
}
