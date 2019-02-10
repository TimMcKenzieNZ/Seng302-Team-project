package seng302.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javafx.concurrent.Task;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import seng302.exceptions.FailedServerRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * PostTask is used for making a POST request. It extends Task which is the JavaFX way of doing work off the main
 * gui thread. To use PostTask first construct it with the object to send, server address, the endpoint, and if required the
 * authentication token and class of return object. Then set onSucceeded and onFailed (see loginController for an example).
 * onFailed should handle IOException, which is a general failure and FailedServerRequestException which is a custom exception
 * with the response code and message and is thrown if the response code is not 201.
 * Note that if no class of return object is specified it will return true on 201
 * Then run the task.
 */
public class PostTask extends RequestTask {

    /**
     * Constructor without authentication token.
     *
     * @param classOfObject Class of object in body of response.
     * @param server        Address of the server.
     * @param endpoint      The endpoint.
     * @param body          Object to go in body of request.
     */
    public PostTask(Class<?> classOfObject, String server, String endpoint, Object body) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
        this.body = body;
    }


    /**
     * Constructor with authentication token.
     *
     * @param classOfObject       Class of object in body of response.
     * @param server              Address of the server.
     * @param endpoint            The endpoint.
     * @param body                Object to go in body of request.
     * @param authenticationToken The authentication token.
     */
    public PostTask(Class<?> classOfObject, String server, String endpoint, Object body, String authenticationToken) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
        this.body = body;
        this.authenticationToken = authenticationToken;
    }

    /**
     * Constructor with authentication token and custom headers.
     *
     * @param classOfObject       Class of object in body of response.
     * @param server              Address of the server.
     * @param endpoint            The endpoint.
     * @param body                Object to go in body of request.
     * @param authenticationToken The authentication token.
     */
    public PostTask(Class<?> classOfObject, String server, String endpoint, Object body, String authenticationToken, Map<String, String> customHeaders) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint;
        this.body = body;
        this.authenticationToken = authenticationToken;
        this.customHeaders = customHeaders;
    }


    /**
     * Constructor with authentication token and without return object.
     *
     * @param server              Address of the server.
     * @param endpoint            The endpoint.
     * @param body                Object to go in body of request.
     * @param authenticationToken The authentication token.
     */
    public PostTask(String server, String endpoint, Object body, String authenticationToken) {
        this.method = HttpMethod.POST;
        this.url = server + endpoint;
        this.body = body;
        this.authenticationToken = authenticationToken;
    }


    /**
     * Constructor without authentication token and return object.
     *
     * @param server   Address of the server.
     * @param endpoint The endpoint.
     * @param body     Object to go in body of request.
     */
    public PostTask(String server, String endpoint, Object body) {
        this.method = HttpMethod.POST;
        this.url = server + endpoint;
        this.body = body;
    }

    /**
     * Constructor with authentication token and without return object.
     *
     * @param server              Address of the server.
     * @param endpoint            The endpoint.
     * @param authenticationToken The authentication token.
     */
    public PostTask(String server, String endpoint, String authenticationToken) {
        this.method = HttpMethod.POST;
        this.url = server + endpoint;
        this.authenticationToken = authenticationToken;
    }


//    /**
//     * Constructor with custom headers
//     * @param classOfObject Class of object
//     * @param server server url
//     * @param endpoint endpoint uri
//     * @param body body of the post request
//     * @param authenticationToken token of the session
//     * @param customHeaders Custom headers to be added
//     */
//    public PostTask(Class<?> classOfObject, String server, String endpoint, Object body, String authenticationToken, Map<String, String> customHeaders) {
//        this.method = HttpMethod.POST;
//        this.classOfObject = classOfObject;
//        this.url = server + endpoint;
//        this.body = body;
//        this.authenticationToken = authenticationToken;
//        this.customHeaders = customHeaders;
//    }

}


