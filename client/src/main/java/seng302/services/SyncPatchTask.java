package seng302.services;

import org.springframework.http.HttpMethod;

public class SyncPatchTask extends SyncRequestTask {

  public SyncPatchTask(String server, String endpoint, Object body, String authenticationToken) {
    this.method = HttpMethod.POST;
    this.url = server + endpoint + "?_method=patch";
    this.body = body;
    this.authenticationToken = authenticationToken;
  }
}
