package seng302.services;

import org.springframework.http.HttpMethod;

/**
 * GetTask is used for making a GET request that returns one instance a class ie one DonorReceiver. It extends Task
 * which is JavaFX way of doing work off the main gui thread.
 * To use GetTask first construct it with the class of object to be returned, the server address, the endpoint, and if
 * required the authentication token.
 * Then set onSucceeded and onFailed (see loginController for an example).
 * onFailed should handle IOException, which is a general failure and FailedServerRequestException which is a custom exception
 * with the response code and message and is thrown if the response code is not 200.
 * Then run the task.
 */
public class GetTask extends RequestTask {

    /**
     * Constructor with authentication token.
     *
     * @param classOfObject The class of the object that will be returned in the body of the response.
     * @param server The address of the server as a string.
     * @param endpoint The endpoint as a string.
     * @param authenticationToken The authentication token of the current session as a string.
     */
    public GetTask(Class<?> classOfObject, String server, String endpoint, String authenticationToken) {
        this.method = HttpMethod.GET;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
        this.authenticationToken = authenticationToken;

    }


    /**
     * Constructor without an authentication token.
     *
     * @param classOfObject The class of the object that will be returned in the body of the response.
     * @param server The address of the server as a string.
     * @param endpoint The endpoint as a string.
     */
    public GetTask(Class<?> classOfObject, String server, String endpoint) {
        this.method = HttpMethod.GET;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
    }


    /**
     * Constructor with authentication token.
     *
     * @param classOfObject The class of the object that will be returned in the body of the response.
     * @param server The address of the server as a string.
     * @param endpoint The endpoint as a string.
     * @param authenticationToken The authentication token of the current session as a string.
     */
    public GetTask(Class<?> classOfObject, String server, String endpoint, String authenticationToken, boolean returnResponseEntity) {
        this.method = HttpMethod.GET;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
        this.authenticationToken = authenticationToken;
        this.returnResponseEntity = returnResponseEntity;

    }

}


