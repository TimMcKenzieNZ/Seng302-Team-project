package seng302.model.security;


import java.io.Serializable;

/**
 * LoginAttempt stores a login attempt made to /login endpoint.
 */
public class LoginAttempt implements Serializable{
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
