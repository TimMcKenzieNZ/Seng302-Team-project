package seng302.model.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * Gets the token from an incoming request and attempts to authenticate using AuthenticationProviderImpl.
 */
public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {


    /**
     * Tokens are stored in this header.
     */
    public static final String HEADER = "x-auth-token";


    /**
     * Constructor
     * @param matcher Path matcher for endpoints
     */
    protected AuthenticationProcessingFilter(RequestMatcher matcher) {
        super(matcher);
    }


    /**
     * Required by AbstractAuthenticationProcessingFilter
     * @param request http request
     * @param response http response
     * @param chain pass the request on
     * @throws IOException exception
     * @throws ServletException exception
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {

        // Get the token from header
        String token = ((HttpServletRequest) request).getHeader(HEADER);

        // No token to speak of
        if (StringUtils.isEmpty(token)) {
            chain.doFilter(request, response);
            return;
        }

        this.setAuthenticationSuccessHandler((request1, response1, authentication) -> {
            chain.doFilter(request1, response1);
        });

        super.doFilter(request, response, chain);
    }


    /**
     * Required by AbstractAuthenticationProcessingFilter
     * @param request http request
     * @param response http response
     * @return authenticates using AuthenticationProviderImpl
     * @throws AuthenticationException exception
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // Get the token from header
        String token = request.getHeader(HEADER);
        // No token to speak of
        if(StringUtils.isEmpty(token)){
            return null;
        }
        // Create an AuthenticationToken instance and attempt to authenticate
        AuthenticationToken authenticationToken = new AuthenticationToken(token);
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}
