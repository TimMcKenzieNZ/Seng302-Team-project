package seng302.services;

import org.springframework.http.HttpMethod;

public class SyncDeleteTask extends SyncRequestTask {
  /**
   * Constructor with authentication token.
   *
   * @param server The address of the server as a string.
   * @param endpoint The endpoint as a string.
   * @param authenticationToken The authentication token of the current session as a string.
   */
  public SyncDeleteTask(String server, String endpoint, String authenticationToken) {
    this.method = HttpMethod.DELETE;
    this.url = server + endpoint;
    this.authenticationToken = authenticationToken;
  }

}
