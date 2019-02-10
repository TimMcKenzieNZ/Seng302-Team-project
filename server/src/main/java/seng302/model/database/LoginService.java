package seng302.model.database;

import seng302.model.security.AuthenticationToken;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.LoginAttempt;
import seng302.model.security.LoginResult;

import java.sql.*;

/**
 * LoginService provides methods for authenticating login and logout requests.
 */
public class LoginService {

    /**
     * Attempts to login a user and generate an AuthenticationToken for the session. If successful the AuthenticationToken
     * is added to the AuthenticationTokenStore and the returned LoginResult contains the users credentials (donor, clinician,
     * admin) and the string representation of the token. If unsuccessful then token and credentials will be empty strings.
     * @param loginAttempt Object that stores the username and password when a login attempt is made.
     * @param connection Connection to the database where user details are stored.
     * @param authenticationTokenStore Stores tokens for all currently authenticated users.
     * @return LoginResult To be returned to user. If unsuccessful token and credentials will be empty string.
     */
    public LoginResult login(LoginAttempt loginAttempt, Connection connection, AuthenticationTokenStore authenticationTokenStore) throws SQLException {
        LoginResult loginResult = new LoginResult();
        loginResult.setUsername(loginAttempt.getUsername());
        try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM `Users` WHERE Users.username= ? AND Users.password = ?")){

            statement.setString(1, loginAttempt.getUsername());
            statement.setString(2, loginAttempt.getPassword());
            try (ResultSet resultSet = statement.executeQuery()) {
                connection.commit();
                resultSet.next();
                String existingToken = authenticationTokenStore.getExistingToken(resultSet.getString("username"));
                if (!existingToken.isEmpty()){
                    loginResult.setToken(existingToken);
                    loginResult.setCredentials(resultSet.getString("userType"));
                } else {
                    loginResult.setCredentials(resultSet.getString("userType"));
                    AuthenticationToken token = new AuthenticationToken(loginResult.getUsername(), loginResult.getCredentials());
                    loginResult.setToken(token.getCredentials().toString());
                    authenticationTokenStore.add(token);
                }
            }
        }
        return loginResult;
    }


    /**
     * Removes the token from the token store. This logs out the user associated with the token.
     * @param token AuthenticationToken to remove from store.
     * @param authenticationTokenStore AuthenticationTokenStore that contains token.
     * @return True if token existed and has been removed. False otherwise.
     */
    public boolean logout(String token, AuthenticationTokenStore authenticationTokenStore){
        return authenticationTokenStore.remove(token);
    }
}

