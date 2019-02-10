package seng302.model.database;

import javafx.concurrent.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public class NonBlockingSQLTask extends Task<ArrayList<String>> {
  private String username;
  private String password;
  private String query;
  private String url;
  //private DBCConnection dbcConnection;

  public NonBlockingSQLTask(String url, String username, String password, String query) {
    this.username = username;
    this.password = password;
    this.query = query;
    this.url = url;

  }

  // The task implementation
  @Override
  protected ArrayList<String> call() {
    ArrayList<String> results = new ArrayList<>();
    try {
      DBCConnection dbcConnection = new DBCConnection(url, username, password);
      boolean connected = dbcConnection.connectToDatabase();
      if (connected) {
        results = dbcConnection.executeSanatizedStatement(query);
        dbcConnection.terminate();
      } else {
        results.add("ERROR: could not connect to database with given username and password.");
      }
    } catch (SQLException e) {
      results.add("ERROR: An SQL error occurred when connection to database with given username and password.");
    }
    return results;
  }


}