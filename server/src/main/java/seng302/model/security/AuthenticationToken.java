package seng302.model.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.time.LocalDateTime;
import java.util.Random;


/**
 * AuthenticationToken stores username, type, and session token for authentication of users.
 */
public class AuthenticationToken extends AbstractAuthenticationToken {


    private static final int TOKEN_LENGTH = 40;
    private static final long VALID_TOKEN_TTL= 600;
    private static final long INVALID_TOKEN_TTL = 15;
    private static final String CHARACTER_SET = "123456789abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ";
    private static Random rnd = new Random();

    private final String token;
    private String username;
    private String userType;
    private LocalDateTime expiryTime;


    /**
     * AuthenticationToken constructor
     * @param username Username of the donor/clinician/admin
     * @param userType Type of the user (donor/clinician/admin)
     */
    public AuthenticationToken(String username, String userType) {
        super(GrantedAuthorityImpl.valueOfList(userType));
        this.username = username;
        this.userType = userType;
        this.token = newToken();
        this.expiryTime = LocalDateTime.now().plusMinutes(VALID_TOKEN_TTL);
        setAuthenticated(true);
    }


    /**
     * Creates an AuthenticationToken with no GrantedAuthority.
     * @param token token string
     */
    public AuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.expiryTime = LocalDateTime.now().plusMinutes(INVALID_TOKEN_TTL);
        setAuthenticated(false);
    }


    /**
     * Generates a new random token.
     * @return String representation of the token.
     */
    public static String newToken(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < TOKEN_LENGTH; i++){
            builder.append(CHARACTER_SET.charAt(rnd.nextInt(CHARACTER_SET.length())));
        }
        return builder.toString();
    }


    /**
     * Extend the expiry time of the token.
     */
    void renew(){
        this.expiryTime = LocalDateTime.now().plusMinutes(VALID_TOKEN_TTL);
    }


    /**
     * Check if the token is expired
     * @return True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(){
        return LocalDateTime.now().isAfter(expiryTime);
    }


    /**
     * Required method of AbstractAuthenticationToken, returns token associated with AuthenticationToken.
     * @return Token (string of alphanumeric characters)
     */
    @Override
    public Object getCredentials() {
        return token;
    }


    /**
     * Required method of AbstractAuthenticationToken, returns username associated with the token.
     * @return Username of the user associated with the token.
     */
    @Override
    public Object getPrincipal() {
        return username;
    }
}
