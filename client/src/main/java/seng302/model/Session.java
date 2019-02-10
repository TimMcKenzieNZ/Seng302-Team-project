package seng302.model;

import seng302.exceptions.InvalidCredentialsException;

/**
 * For storing the username, user type and auth token of the currently authenticated user.
 * The values for these should not be copied but accessed when needed.
 */
public class Session {
    private LoginResult currentUser;
    private boolean donorIsDead = false;

    public void setSession(LoginResult currentUser) throws InvalidCredentialsException {
        if (currentUser.getUsername() == null || currentUser.getUsername().isEmpty()) {
            throw new InvalidCredentialsException("Missing username.");
        } else if (currentUser.getCredentials() == null || currentUser.getCredentials().isEmpty()) {
            throw new InvalidCredentialsException("Missing user type.");
        } else if (currentUser.getToken() == null || currentUser.getToken().isEmpty()) {
            throw new InvalidCredentialsException("Missing auth token.");
        } else {
            this.currentUser = currentUser;
        }
    }

    public LoginResult getSession(){
        return this.currentUser;
    }

    public String getToken(){
        return this.currentUser.getToken();
    }
    public String getUserType(){
        return this.currentUser.getCredentials();
    }
    public String getUsername(){
        return this.currentUser.getUsername();
    }
    public void setDonorIsDead(boolean dead) { this.donorIsDead = dead; }
    public boolean getDonorIsDead() { return donorIsDead; }
}
