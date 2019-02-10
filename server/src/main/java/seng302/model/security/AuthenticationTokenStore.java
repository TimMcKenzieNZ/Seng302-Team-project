package seng302.model.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Stores all the AuthenticationTokens for currently authenticated users. Is purged every 30min to remove
 * expired tokens.
 */
@Component
public class AuthenticationTokenStore {

    private static final long HALF_AN_HOUR_IN_MS = 1800000;

    private Set<AuthenticationToken> currentTokens;

    @Autowired
    public AuthenticationTokenStore() {
        currentTokens = new HashSet<>();
    }


    /**
     * Scheduled service that runs every 30 minutes and removes expired tokens.
     */
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MS)
    public void evictExpiredTokens() {

        ArrayList<String> tokensToRemove = new ArrayList<>();
        for (AuthenticationToken token : currentTokens){
            if (token.isTokenExpired()){
                tokensToRemove.add(token.getCredentials().toString());
            }
        }
        for (String token : tokensToRemove){
            this.remove(token);
        }
        tokensToRemove.clear();
    }


    /**
     * Get the AuthenticationToken associated with token string if it exists in token store.
     * @param token string
     * @return AuthenticationToken or null
     */
    AuthenticationToken get(String token) {
        for (AuthenticationToken authenticationToken : currentTokens) {
            if (authenticationToken.getCredentials().toString().equals(token)){
                return authenticationToken;
            }
        }
        return null;
    }

    /**
     *  Returns the username associated with the given token.
     * @param token string token
     * @return
     */
    public String getUsername(String token) {
       AuthenticationToken authenticationToken = get(token);
       return (String) authenticationToken.getPrincipal();
    }

    /**
     * Adds the given AuthenticationToken to the store.
     * @param authenticationToken to add.
     * @return True if added, false otherwise.
     */
    public boolean add(AuthenticationToken authenticationToken) {
        return currentTokens.add(authenticationToken);
    }


    /**
     * Checks if the store contains an AuthenticationToken associated with the given token string.
     * @param token string
     * @return True if contains token. False otherwise.
     */
    boolean contains(String token) {
        return (get(token) != null);
    }


    /**
     * Remove the token associated with the token string
     * @param token string
     * @return
     */
    public boolean remove(String token) {
        AuthenticationToken authenticationToken = get(token);
        return currentTokens.remove(authenticationToken);
    }

    /**
     * Checks if any other token exists for the user if yes renews the expiry and
     * returns the token string. Else returns empty string.
     */
    public String getExistingToken(String username){
        for (AuthenticationToken authenticationToken : currentTokens) {
            if (authenticationToken.getPrincipal().toString().equals(username)){
                authenticationToken.renew();
                return authenticationToken.getCredentials().toString();
            }
        }
        return "";
    }

}
