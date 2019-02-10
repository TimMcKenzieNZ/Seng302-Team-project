package seng302.model.security;

import java.io.Serializable;

/**
 * LoginResult stores the result of a login attempt to be returned by /login endpoint.
 * If attempt is not successful then token and credentials will be empty strings.
 */
public class LoginResult implements Serializable{
    private String username;
    private String token;
    private String credentials;


    public LoginResult() {
        token = "";
        credentials = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

}
