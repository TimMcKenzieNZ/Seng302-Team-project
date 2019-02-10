package seng302.model.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Implementation of AuthenticationProvider. Authenticates the token provided by the
 * AuthenticationProcessingFilter against the AuthenticationTokenStore.
 */
public class AuthenticationProviderImpl implements AuthenticationProvider {

    @Autowired
    AuthenticationTokenStore authenticationTokenStore;


    /**
     * Authenticates the authentication instance (token string) provided
     * by authentication filter.
     * @param authentication as returned by authentication filter.
     * @return AuthenticationToken
     * @throws AuthenticationException Bad or expired credentials.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        final String token = authenticationToken.getCredentials().toString();

        if (!authenticationTokenStore.contains(token)) {
            throw new BadCredentialsException("UNAUTHORIZED");
        } else if (authenticationTokenStore.get(token).isTokenExpired()) {
            authenticationTokenStore.remove(token);
            throw new BadCredentialsException("SESSION EXPIRED");
        } else {
            authenticationTokenStore.get(token).renew();
        }

        return authenticationTokenStore.get(token);
    }


    /**
     * Checks if a class may be (true does not guarantee it will work) supported
     * by authentication provider.
     * @param authentication Class to check for support
     * @return True if supported by AuthenticationProvider, false otherwise.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
