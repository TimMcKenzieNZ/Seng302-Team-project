package seng302.controller;

import org.springframework.web.bind.annotation.RestController;
import seng302.model.database.DBCConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public abstract class BaseController {

  static DBCConnection dbcConnection;


  public BaseController() {
    // Insert database connection and other things related to setting up the class here
    // Use the constructor defined in the subclasses to just access this. Refer to team 100s code for a good way to do this but we will changing it since our modules are more specific.
    try {
      dbcConnection = new DBCConnection();
    } catch (SQLException e) {
      System.err.println("Something went wrong");
    }
  }

  /**
   * Checks that the given version is valid
   * @param username username of user to check
   * @param checkVersion version number to check
   * @return int An indicator of whether the version is valid or not.
   * 1 means the version of the user is greater than the existing copy on the database
   * 0 means the version of the user is the same as the copy on the database
   * -1 means the version of the user is less that the copy on the database
   */
  public static int checkVersionNumber(String username, int checkVersion) {

    int version = getVersionNumber(username);
    if (checkVersion > version) {
      return 1;
    }
    else if (checkVersion == version) {
      return 0;
    }
    return -1;
  }

  /**
   * Gets the version for a user
   * @param username The username of the user we want to check the version for
   * @return int The version number.
   */
  private static int getVersionNumber(String username) {

    String sql = String.format("SELECT username, version from `Users` WHERE username=\"%s\"", username);
    try {
      ResultSet resultSet = dbcConnection.executeStatement(sql);
      if(resultSet.next()) {
        resultSet.getString("username"); // Check that the username is in the resultSet
        return resultSet.getInt("version");
      }
      return -1;
    }
    catch (SQLException e) {
      return -1;
    }
  }

  /**
   * A static way to get the DBCConnection for classes that don't inherit BaseController.
   * Primarily used in ServerUserValidator to check if the username is taken.
   * @return The database connection.
   */
  public static DBCConnection getDBCConnection() {
    return dbcConnection;
  }
}
