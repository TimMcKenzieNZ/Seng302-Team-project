package seng302.model.database;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;


public class DBCConnection {

  private static String url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-prod?allowMultiQueries=true";
  private String username = "seng302-team600";
  private String databaseAccess = "TailspinElla4435";

  private static final int COLUMN_PADDING = 1;



  private Connection connection;
  private Statement statement;

  private HashMap<String, Integer> numberColumns = new HashMap<>();

  private String errorString = "ERROR: Couldn't connect to the database";


  /**
   * Attempts to connect a MYSQL database based off the url, username, and password attributes.
   *
   * @return Returns a boolean, 'true' if the connection was established, 'false' otherwise.
   * @throws SQLException If there is an error in SQL communications
   */
  public boolean connectToDatabase() throws SQLException {
    try {
      connection = DriverManager.getConnection(url, username, databaseAccess);
      return isConnected();
    } catch (SQLException e) {
      throw new SQLException(errorString);
    }
  }

  public void setConnection(String newConnection){
    try {
      connection.close();
      connectToDatabase(newConnection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  /**
   * Attempts to connect a MYSQL database based off the given url, and the username and password attributes.
   *
   * @return Returns a boolean, 'true' if the connection was established, 'false' otherwise.
   * @throws SQLException If there is an error in SQL communications
   */
  public boolean connectToDatabase(String url) throws SQLException {
    try {
      connection = DriverManager.getConnection(url, username, databaseAccess);
      return isConnected();
    } catch (SQLException e) {
      throw new SQLException(errorString);
    }
  }


    public Connection getConnection() {
      try {
        if (connection.isValid(3000)) {
          return connection;
        } else {
            connectToDatabase(url);
            return connection;
        }
      } catch (SQLException e) {
        try {
          connectToDatabase(url);
        } catch (SQLException e1) {
        }
        return connection;
      }
    }

    public DBCConnection(String url, String username, String password) throws SQLException {
    try {

      connection = DriverManager.getConnection(url, username, password);

      if (isConnected()) {
        this.username = username;
        this.databaseAccess = password;
      }
      statement = connection.createStatement();

      initNumberColumns();

    } catch (SQLException e) {
      throw new SQLException(errorString);

    }
  }

  public DBCConnection() throws SQLException {
    try {
      connection = DriverManager.getConnection(url, username, databaseAccess);
      statement = connection.createStatement();

      initNumberColumns();
    } catch (SQLException e) {
      throw new SQLException(errorString);
    }
  }

  public ResultSet executeStatement(String sql) throws SQLException {
    statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    return resultSet;
  }

  /**
   * Sanitize the SQL statement before carrying it out
   * @param sql The SQL query to execute
   * @return An ArrayList containing the results of the SQL query
   * @throws SQLException An exception if anything went wrong while carrying out the command
   */
  public ArrayList<String> executeSanatizedStatement(String sql) throws SQLException {
    statement = connection.createStatement();
    ArrayList<String> message = new ArrayList<>();
    try {
      if (sql.toLowerCase().contains("select")) {
        ResultSet resultSet = statement.executeQuery(sql);
        return (ArrayList<String>) processSelectResultSet(resultSet);
      }

      int numUpdatedRows = statement.executeUpdate(sql);

      message.add(numUpdatedRows + " row(s) were updated");
    } catch (SQLException e) {
      message.add(
          "ERROR: Something went wrong when executing the script. Note that table names are case sensitive.");
    }
    return message;
  }

  private void initNumberColumns() {
    numberColumns.put("auction", 12);
  }

  /**
   * Get the name of the table we're accessing based on the SQL query
   * @param sql The sql to check
   * @return The name of the table based on the SQL
   * @throws Exception If we couldn't find the name of the table
   */
  private String getTableName(String sql) throws Exception {
    sql = sql.toLowerCase();
    Set<String> tableNames = numberColumns.keySet();

    for (String tableName : tableNames) {
      tableName = tableName.toLowerCase();
      if (sql.contains(tableName)) {
        return tableName;
      }
    }

    throw new Exception("ERROR: Couldn't fetch table names");
  }

  /**
   * Process the SELECT result and format it properly into a List of Strings to add to the CLI output
   * @param resultSet The ResultSet of the SELECT query
   * @return A List of Strings forming a table of the formatted SELECT result
   */
  private List<String> processSelectResultSet(ResultSet resultSet) {

    ArrayList<String> message = new ArrayList<>();
    try {
      ResultSetMetaData rsmd = resultSet.getMetaData();

      int numColumns = rsmd.getColumnCount();
      String[] columnNames = new String[numColumns];
      ArrayList<String[]> data = new ArrayList<>();
      int[] columnSizes = new int[numColumns];

      for (int i = 0; i < numColumns; i++) {
        columnNames[i] = rsmd.getColumnName(i + 1);
        columnSizes[i] = columnNames[i].length();
      }

      while (resultSet.next()) {
        String[] row = new String[numColumns];
        for (int i = 0; i < numColumns; i++) {
          row[i] = resultSet.getString(i + 1);
          if (row[i] != null && columnSizes[i] < row[i].length()) {
            columnSizes[i] = row[i].length();
          }
        }
        data.add(row);
      }
      return formatTableData(numColumns, columnNames, data.toArray(new String[data.size()][numColumns]), columnSizes);
    } catch (SQLException e) {
      message.add("ERROR: Something went wrong when displaying the results of the query");
    }
    return message;
  }

  /**
   * Format the table data including padding the strings to be readable as a table.
   * The padding is 2 spaces
   * @param numColumns The number of columns in the table
   * @param columnNames The labels for the columns
   * @param data The data object
   * @param columnSizes How big each column should be
   * @return An ArrayList of strings for each row in the table
   */
  private ArrayList<String> formatTableData(int numColumns, String[] columnNames, String[][] data,
      int[] columnSizes) {
    ArrayList<String> message = new ArrayList<>();
    StringBuilder tempColumnLabels = new StringBuilder();
    StringBuilder tempColumnData;
    int totalLength = 0;

    for (int i = 0; i < numColumns; i++) {
      tempColumnLabels.append("|").append(StringUtils.repeat(" ", COLUMN_PADDING));
      tempColumnLabels.append(columnNames[i]);
      tempColumnLabels.append(StringUtils.repeat(" ", columnSizes[i] - columnNames[i].length()));
      tempColumnLabels.append(StringUtils.repeat(" ", COLUMN_PADDING));
      totalLength +=
          columnSizes[i] + 2 * COLUMN_PADDING + 1;
    }
    tempColumnLabels.append("|");
    totalLength += 1; // For the added '|' char

    String newLineString = StringUtils.repeat("+", totalLength);
    message.add(newLineString);
    message.add(tempColumnLabels.toString());
    message.add(newLineString);

    for (String[] aData : data) {
      tempColumnData = new StringBuilder();
      for (int i = 0; i < numColumns; i++) {
        tempColumnData.append("|").append(StringUtils.repeat(" ", COLUMN_PADDING));
        String tempData = aData[i];
        if (tempData == null || tempData.equals("")) {
          tempData = "null";
        }
        tempColumnData.append(tempData);
        tempColumnData.append(StringUtils.repeat(" ", columnSizes[i] - tempData.length()));
        tempColumnData.append(StringUtils.repeat(" ", COLUMN_PADDING));
      }
      tempColumnData.append("|");
      message.add(tempColumnData.toString());
    }

    Collections.reverse(message);
    return message;
  }

  public void terminate() throws SQLException {
    statement.close();
    connection.close();
  }

  /**
   * Returns true if the connection variable is not null, false otherwise
   *
   * @return returns a boolean
   */
  public boolean isConnected() {
    return connection != null;
  }


  /**
   * Allows the database to be changed between production and testing.
   * @param useTestDatabase A boolean value which should be true if the test
   *                        database is to be used. This should be false if the
   *                        production database is to be used.
   */
  public static void setTestDatabase(boolean useTestDatabase) {

    if (useTestDatabase) {

      url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600" +
              "-test?allowMultiQueries=true";

    } else {

      url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600" +
              "-prod?allowMultiQueries=true";

    }



  }



}
