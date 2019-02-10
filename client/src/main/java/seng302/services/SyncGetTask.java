package seng302.services;

import org.springframework.http.HttpMethod;

public class SyncGetTask extends SyncRequestTask{
  /**
   * Constructor with authentication token.
   *
   * @param classOfObject The class of the object that will be returned in the body of the response.
   * @param server The address of the server as a string.
   * @param endpoint The endpoint as a string.
   * @param authenticationToken The authentication token of the current session as a string.
   */
  public SyncGetTask(Class<?> classOfObject, String server, String endpoint, String authenticationToken) {
    this.method = HttpMethod.GET;
    this.classOfObject = classOfObject;
    this.url = server + endpoint;
    this.authenticationToken = authenticationToken;
  }
}
