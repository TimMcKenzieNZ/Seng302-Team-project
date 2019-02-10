package seng302.services;


import com.fasterxml.jackson.databind.ObjectMapper;
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
 * For making API calls
 */
public class PatchTask extends RequestTask {

    /**
     * Constructor without authentication token.
     *
     * @param classOfObject
     * @param body
     */
    public PatchTask(Class<?> classOfObject, String server, String endpoint, Object body) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint + "?_method=patch";
        this.body = body;

    }

    /**
     * Constructor with authentication token.
     *
     * @param classOfObject
     * @param body
     * @param authenticationToken
     */
    public PatchTask(Class<?> classOfObject,  String server, String endpoint, Object body, String authenticationToken) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint + "?_method=patch";
        this.body = body;
        this.authenticationToken = authenticationToken;



    }

    /**
     * Constructor with custom headers
     * @param classOfObject Class of object
     * @param server server url
     * @param endpoint endpoint uri
     * @param body body the patch request
     * @param authenticationToken token of the session
     * @param customHeaders Custom headers to be added
     */
    public PatchTask(Class<?> classOfObject, String server, String endpoint, Object body, String authenticationToken, Map<String, String> customHeaders) {
        this.method = HttpMethod.POST;
        this.classOfObject = classOfObject;
        this.url = server + endpoint + "?_method=patch";
        this.body = body;
        this.authenticationToken = authenticationToken;
        this.customHeaders = customHeaders;
    }

}


