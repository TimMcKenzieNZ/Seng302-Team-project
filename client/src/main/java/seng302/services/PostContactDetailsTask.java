package seng302.services;


import java.util.LinkedHashMap;
import java.util.Map;


/**
 * PostContactDetailsTask is an extension of PostTask dedicated to handling POST requests to the
 * server where a boolean describing emergency is required.
 */
public class PostContactDetailsTask extends PostTask {


  // Class attributes.
  boolean emergency;


  /**
   * Constructor for the PostContactDetailsTask. This is identical to a constructor found in post
   * task, with the exception of an added boolean variable. This is used to specify if the contact
   * details to be created are emergency contact details.
   * @param classOfObject Class of object contained in the response body.
   * @param server Address of the server.
   * @param endpoint The endpoint.
   * @param body Object to go in body of request.
   * @param emergency True if the contact details to be created are emergency contact details.
   */
  public PostContactDetailsTask(Class<?> classOfObject, String server, String endpoint, Object body,
      boolean emergency) {

    super(classOfObject, server, endpoint, body);
    this.emergency = emergency;

  }


  /**
   * Sends a POST request to the server with emergency set as a variable in the header.
   * @return The result of the call operation.
   */
  @Override
  protected Object call(){

    Map<String, String> headers = new LinkedHashMap<>(); // Regular HashMap will cause exception.
    headers.put("emergency", Boolean.toString(emergency));
    return RequestMethods.makeRequest(authenticationToken, body, url, method, classOfObject, headers);

  }


}
