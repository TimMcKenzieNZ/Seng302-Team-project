package seng302.model.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class DBCConnection {

  private static final String url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-prod";
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

  public DBCConnection(String url, String username, String password) throws SQLException {
    try {

      connection = DriverManager.getConnection(url, username, password);

      if (!isConnected()) {
        throw new SQLException(errorString);
      }

      this.username = username;
      this.databaseAccess = password;
      initNumberColumns();

    } catch (SQLException e) {
      throw new SQLException(errorString);
    }
  }

  DBCConnection() throws SQLException {
    try {
      connection = DriverManager.getConnection(url, username, databaseAccess);
      statement = connection.createStatement();

      initNumberColumns();
    } catch (SQLException e) {
      throw new SQLException(errorString);
    }
  }

  public ArrayList<String> executeStatement(String sql) throws SQLException {
    statement = connection.createStatement();
    ArrayList<String> message = new ArrayList<>();
    try {
      if (sql.toLowerCase().contains("select")) {
        ResultSet resultSet = statement.executeQuery(sql);
        return processSelectResultSet(resultSet);
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

  private ArrayList<String> processSelectResultSet(ResultSet resultSet) {

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
          if(row[i] == null || row[i].equals("")) {
            row[i] = "null";
          }
          if (columnSizes[i] < row[i].length()) {
            columnSizes[i] = row[i].length();
          }
        }
        data.add(row);
      }
      return formatTableData(numColumns, columnNames, data.toArray(new String[data.size()][numColumns]), columnSizes);
    } catch (SQLException e) {
      message.add("ERROR: Something went wrong when displaying the results of the query");
      return message;
    }
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
        tempColumnData.append(tempData);
        tempColumnData.append(StringUtils.repeat(" ", columnSizes[i] - tempData.length()));
        tempColumnData.append(StringUtils.repeat(" ", COLUMN_PADDING));
      }
      tempColumnData.append("|");
      message.add(tempColumnData.toString());
    }

    return message;
  }

  public void terminate() throws SQLException {
    statement.close();
    connection.close();
  }

  public void terminateConnection() throws SQLException {
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

}
