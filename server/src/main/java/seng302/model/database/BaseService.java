package seng302.model.database;

import org.springframework.beans.factory.annotation.Autowired;
import seng302.model.security.AuthenticationTokenStore;


public abstract class BaseService {


    public static AuthenticationTokenStore authenticationTokenStore;


    @Autowired
    public BaseService(AuthenticationTokenStore authenticationTokenStore) {
        BaseService.authenticationTokenStore = authenticationTokenStore;
    }



    /**
     * Gets the username associated with the token.
     * @param token string
     * @return username
     */
    public String getUsername(String token){
        return authenticationTokenStore.getUsername(token);
    }
}
