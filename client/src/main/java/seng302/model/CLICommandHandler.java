package seng302.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import seng302.model.Database.DBCConnection;
import seng302.model.Database.CLIToSQLCommandSanitizer;


/**
 * The CLICommandHandler class handles all commands sent by the GUI and lets the separate Account
 * Type class handles like CLIAdminCommandHandler handle the implementation by giving it the
 * necessary information
 *
 * Refer to the eng-git wiki for how the commands should be structured
 */
public class CLICommandHandler {

  private boolean loggedIn;

  private String username = "seng302-team600";

  private String databaseAccess = "TailspinElla4435";

  private static final String url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-prod";
  private DBCConnection dbcConnection;
  private CLIToSQLCommandSanitizer commandSanitizer = new CLIToSQLCommandSanitizer();

  /**
   * Initialises the command handler so that the user is not logged in.
   */
  public CLICommandHandler() {
    loggedIn = false;
  }


  /**
   * Attempts to match the target string, starting at the beginning of the string, against the
   * regex.
   *
   * @param target The string being inspected for a pattern.
   * @param regex The regular expression to search for in the string.
   * @return True if the regular expression is matched, False if it is not matched.
   */
  private boolean isLookingAtPattern(String target, String regex) {
    return Pattern.compile(regex).matcher(target).lookingAt();
  }

  /**
   * Routes the commands to the appropriate handlers.
   *
   * @param command Command entered by user
   * @return Returns a list of messages that are the result of the command. Message are in order of
   * occurrence i.e. the message that was added first is first in the list.
   */
  public ArrayList<String> commandControl(String command){
    ArrayList<String> messages = new ArrayList<>();
    String helpRegex = "^(?i)help";
    String loginRegex = "^(?i)login";
    String logoutRegex = "^(?i)logout";
    String sqlRegex = "^(?i)sql";

    if (isLookingAtPattern(command, helpRegex)) {
      messages.add(help());
    }
    // SQL command parsing logic
    else if (isLookingAtPattern(command, sqlRegex)) {
      if (loggedIn) {
        messages.addAll(handleSQLCommand(command));
      } else {
        messages
            .add("ERROR: You need to login to the database in order to execute an SQL statement.");
      }
    }
    // Login command parsing logic
    else if (isLookingAtPattern(command, loginRegex)) {
      if (loggedIn) {
        messages.add("Error: already logged in.");
      } else {
        messages.addAll(login(command));
      }
      // Logout command parsing logic
    } else if (isLookingAtPattern(command, logoutRegex)) {
      if (!loggedIn) {
        messages.add("Error: Not logged in.");
      } else {
        messages.addAll(logout());
      }
    }
    else {
      messages.add("Invalid command. Please try again.");
    }
    return messages;
  }


  /**
   * Takes the sql command entered by the user, and executes it. There is validation included to
   * ensure that the command cannot modify the data.
   * @param command The sql command to execute (including the 'sql' at the front of the string).
   * @return The messages resulting from the command (e.g. the result of a select statement, an error
   * message if the command attempts to modify the data)
   */
  private ArrayList<String> handleSQLCommand(String command) {
    ArrayList<String> messages = new ArrayList<>();
    try {
      boolean connected = dbcConnection.connectToDatabase();
      if (connected) {
        String[] fullCommand = command.split(" ");
        String[] sql = Arrays.copyOfRange(fullCommand, 1, fullCommand.length);
        String sqlQuery = String.join(" ", sql);
        try {
          commandSanitizer.validateSQL(sqlQuery);
          if (sqlQuery.equals("get tables") || sqlQuery.equals("get tables;")) {
            sqlQuery = "SELECT table_name FROM information_schema.tables WHERE table_type = 'base table' AND table_schema ='seng302-2018-team600-prod';";
          }
          try {
            messages = dbcConnection.executeStatement(sqlQuery);
            dbcConnection.terminate();
          } catch (Exception e) {
            messages.add(
                "ERROR: Something went wrong while terminating the connection, please reopen the CLI");
          }
        } catch (IllegalArgumentException e) {
          messages.add("ERROR: " + e.getMessage());
        }
      } else { // could not connect to DB
        messages.add("ERROR: could not connect to database with given username and password.");
      }
    } catch (SQLException e) {
      messages.add(
          "ERROR: An SQL error occurred when connection to database with given username and password.");
    }
    return messages;
  }


  /**
   * Logs the user out of the database.
   * @return The messages that the user will be shown e.g. logged out, unable to disconnect etc.
   */
  private ArrayList<String> logout() {
    ArrayList<String> messages = new ArrayList<>();
    try {
      dbcConnection.terminateConnection();
      dbcConnection = null;
      loggedIn = false;
      messages.add("Logged out and disestablished database connection.");
    } catch (SQLException e) {
      messages.add("Unable to disconnect from Database. Please try again");
    } catch (NullPointerException e) {
      e.printStackTrace();
      messages.add("ERROR: Not logged in.");
    }
    return messages;
  }


  /**
   * Logs the user in to the database.
   * @param command the login command (should just be the word 'login').
   * @return The messages resulting from the command (e.g. logged in, could not log in)
   */
  private ArrayList<String> login(String command) {
    ArrayList<String> messages = new ArrayList<>();
    if (!loggedIn) {
      String[] words = command.split(" ");
      if (words.length != 1) {
        messages.add(String.format("Invalid command '%s'. Login command should be: " +
            "'login", command));
      } else {
        try {
          dbcConnection = new DBCConnection(url, username, databaseAccess);
          if (dbcConnection.isConnected()) {
            loggedIn = true;
            messages.add(String
                .format("Successfully logged in for user %s. Ready for SQL commands", username));
          } else {
            messages.add("ERROR: Database connection was not established. Please try again.");
          }
        } catch (SQLException e) {
          messages.add(String.format(
              "ERROR: Database connection was not established. Please try again.",
              command));
        }
      }
    } else {
      messages.add("ERROR: You are already logged in.");
    }
    return messages;
  }


  /**
   * The help command that returns all commands
   *
   * @return A string of all commands and how to use them
   */
  public String help() {
    String divider = "============================================================================================\n";
    String message = "";
    message += "Login to database\n";
    message += "To login to the database, use the command: login\n";
    message += divider;
    message += "Logout of the database\n";
    message += "To logout of the database, use the command: logout\n";
    message += divider;
    message += "Database queries\n";
    message += "To query the database, use the command: sql <query>;\n";
    message += "Where <query> is the sql query\n";
    message += "Queries must be non-modifying, you cannot use queries like 'create table', 'drop table', or 'insert'\n";
    message += "To find out the names of the tables, use command: sql get tables\n";
    message += divider;
    message += ("For more information regarding commands for the system, please consult the user_manual\n");
    return message;
  }
}
