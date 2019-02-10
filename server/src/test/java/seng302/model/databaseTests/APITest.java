package seng302.model.databaseTests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.LoginController;
import seng302.model.database.DBCConnection;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.LoginAttempt;
import seng302.model.security.LoginResult;

abstract class APITest {

  static Boolean canRunTests;
  static DBCConnection connection;
  private static final String testURL = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-test";
  private static final String prodURL = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-prod";
  static String token;
  static AuthenticationTokenStore authenticationTokenStore = new AuthenticationTokenStore();
  private static LoginController loginController = new LoginController(authenticationTokenStore);
//
//
//  public APITest() {
//    if(authenticationTokenStore == null) {
//      authenticationTokenStore = new AuthenticationTokenStore();
//      loginController = new LoginController(authenticationTokenStore);
//    }
//    else {
//      if(loginController == null) {
//        loginController = new LoginController(authenticationTokenStore);
//      }
//    }
//  }

  /**
   * Logs in as the default administrator
   */
  static void adminLogin(String database) {
    String url = testURL;
    if (database.equals("prod")) {
      url = prodURL;
    }
    if (canRunTests) {
      try {
        connection.setConnection(url);
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUsername("Sudo");
        loginAttempt.setPassword("password");
        ResponseEntity responseEntity = loginController.login(loginAttempt);
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
          canRunTests = false;
        } else {
          LoginResult loginResult = (LoginResult) responseEntity.getBody();
          token = loginResult.getToken();
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
  * Logs in a pre-made clinician
  */
  static void clinicianLogin(String database) {
    String url = testURL;
    if (database.equals("prod")) {
      url = prodURL;
    }
    if (canRunTests) {
      connection.setConnection(url);
      LoginAttempt loginAttempt = new LoginAttempt();
      loginAttempt.setUsername("0");
      loginAttempt.setPassword("password");
      ResponseEntity responseEntity = loginController.login(loginAttempt);
      if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        canRunTests = false;
      } else {
        LoginResult loginResult = (LoginResult) responseEntity.getBody();
        token = loginResult.getToken();
      }
    }
  }


  /**
   * Creates a new connection to the database
   */
  static void connectToDatabase() {
    try {
      connection = new DBCConnection();
      canRunTests = true;
    } catch (SQLException e) {
      canRunTests = false;
    }
  }

  /**
   * Reduce the version of an account to prevent it from increasing to large amounts and to
   * ensure that tests work without requiring modifying the database
   * @param connection The connection to the database
   * @param username The username of the User we want to reduce the version for
   * @return boolean Whether the version was reduced or not
   */
  static boolean reduceVersion(Connection connection, String username) {
    String updateQuery = String.format("UPDATE `Users` SET version = version - 1 WHERE username = \"%s\" AND version > 1",username);
    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
      Integer updated = preparedStatement.executeUpdate();
      connection.commit();
      return updated > 0;
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
