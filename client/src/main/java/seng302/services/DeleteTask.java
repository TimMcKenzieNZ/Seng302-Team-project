package seng302.services;

import org.springframework.http.HttpMethod;

/**
 * DeleteTask is used for making a DELETE request. It extends Task which is the JavaFX way of doing work off the main
 * gui thread. To use DeleteTask first construct it with  the server address, the endpoint, and if required the
 * authentication token. Then set onSucceeded and onFailed (see loginController for an example).
 * onFailed should handle IOException, which is a general failure and FailedServerRequestException which is a custom exception
 * with the response code and message and is thrown if the response code is not 200.
 * Then run the task.
 */
public class DeleteTask extends RequestTask {

    /**
     * Constructor with authentication token.
     *
     * @param server The address of the server as a string.
     * @param endpoint The endpoint as a string.
     * @param authenticationToken The authentication token of the current session as a string.
     */
    public DeleteTask(String server, String endpoint, String authenticationToken) {
        this.method = HttpMethod.DELETE;
        this.url = server + endpoint;
        this.authenticationToken = authenticationToken;
    }

    public DeleteTask(String server, String endpoint, Object body, String authenticationToken) {
        this.method = HttpMethod.DELETE;
        this.url = server + endpoint;
        this.body = body;
        this.authenticationToken = authenticationToken;
    }


    /**
     * Constructor without an authentication token.
     *
     * @param server The address of the server as a string.
     * @param endpoint The endpoint as a string.
     */
    public DeleteTask(String server, String endpoint) {
        this.method = HttpMethod.DELETE;
        this.url = server + endpoint;
    }
}


