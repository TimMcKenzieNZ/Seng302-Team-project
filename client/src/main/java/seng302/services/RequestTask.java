package seng302.services;

import javafx.concurrent.Task;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.model.enums.STATUS;

import java.util.Map;

public class RequestTask extends Task<Object> {
  Class classOfObject;
  protected String url;
  Object body;
  String authenticationToken;
  HttpMethod method;
  private volatile STATUS recentStatus = STATUS.NONE; // Depreciated but might be useful later
  public HttpStatus responseStatus;
  public Object responseBody;
  public Map<String, String> customHeaders;
  public boolean returnResponseEntity = false;

  RequestTask(){}

  @Override
  protected Object call(){
    ResponseEntity responseEntity = RequestMethods.makeRequest(authenticationToken, body, url, method, classOfObject, customHeaders);
    responseStatus = responseEntity.getStatusCode();
    responseBody = responseEntity.getBody();
    if(returnResponseEntity){
      return responseEntity;
    } else {
      return responseBody;
    }
  }

  @Override
  protected void cancelled() {
    super.cancelled();
    recentStatus = STATUS.CANCELLED;
    updateMessage("The task was cancelled.");
  }

  @Override
  protected void failed() {
    super.failed();
    recentStatus = STATUS.FAILED;
    updateMessage("The task failed.");
  }

  @Override
  public void succeeded() {
    super.succeeded();
    recentStatus = STATUS.SUCCEEDED;
    updateMessage("The task finished successfully.");
  }

  public void resetStatus() {
    recentStatus = STATUS.NONE;
  }

  public STATUS getRecentStatus() {
    return recentStatus;
  }
}
