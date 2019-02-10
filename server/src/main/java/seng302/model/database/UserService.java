package seng302.model.database;

import java.util.Map.Entry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.model.person.*;
import seng302.model.security.AuthenticationTokenStore;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserService extends BaseService {

  private static final String USER_POST_SQL =
      "INSERT INTO Users (`username`, `firstName`, `middleName`, `lastName`, "
          +"`password`, `active`, `creationDate`, `version`, `userType`) "
          + "VALUES ('%s', '%s', '%s', '%s', '%s', '1', CURRENT_TIMESTAMP, '1', '%s');";

  private static final String CONTACT_DETAILS_POST_SQL =
      "INSERT INTO ContactDetails (`mobileNumber`, "
          + "`username`, `homeNumber`, `email`, `streetAddressLineOne`, "
          + "`streetAddressLineTwo`, `suburb`, `city`, "
          + "`region`, `postCode`, `countryCode`) "
          + "VALUES ('%s', '%s', NULL, NULL, NULL, NULL, NULL, NULL, "
          + "'%s', NULL, NULL);";

  private static final String CONTACT_DETAILS_CLINICIAN_POST_SQL =
          "INSERT INTO ContactDetails (`mobileNumber`, "
                  + "`username`, `homeNumber`, `email`, `streetAddressLineOne`, "
                  + "`streetAddressLineTwo`, `suburb`, `city`, "
                  + "`region`, `postCode`, `countryCode`) "
                  + "VALUES ('%s', '%s', NULL, NULL, '%s', NULL, NULL, NULL, "
                  + "'%s', NULL, NULL);";

  private static final String LOG_ENTRY_POST_SQL =
      "INSERT INTO LogEntries (`username`, `valChanged`, " +
          "`modifyingAccount`, `accountModified`, `originalVal`, `changedVal`) "
          + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');";

  private static final String ADMIN_TABLE_GET_SQL = "SELECT * FROM `Administrators` "
      + "INNER JOIN `Users` ON Administrators.username=Users.username "
      + "INNER JOIN `ContactDetails` ON Administrators.username=ContactDetails.username";

  private static final String CLINICIAN_TABLE_GET_SQL = "SELECT * FROM `Clinicians` "
      + "INNER JOIN `Users` ON Clinicians.username=Users.username "
      + "INNER JOIN `ContactDetails` ON Clinicians.username=ContactDetails.username";

  private static final String CLINICIAN_INSERT_SQL = "INSERT INTO `Clinicians` (`username`) "
      + "VALUES ('%s');";

  private static final String ADMIN_INSERT_SQL = "INSERT INTO `Administrators` (`username`) "
      + "VALUES ('%s');";

  private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss.S";

  public UserService(AuthenticationTokenStore authenticationTokenStore) {
    super(authenticationTokenStore);
  }


  /**
   * Creates the sql string for searching for a specific name, username, or region
   * @param name the name of the user (first, middle, or last)
   * @param username the username of the user
   * @param region the name of the region the user is from
   * @return the SQL string for the search of the user
   */
  private String searchNames(String name, String username, String region, String[] addToSql) {
    String sqlString = "";
    String whereString = " WHERE";
    boolean whereCalled = false;

    if (name != null) {
      String nameFinderString = "%" + name + "%";
      whereString += String.format(addToSql[0], nameFinderString, nameFinderString, nameFinderString);
      whereCalled = true;
    }

    if (username != null) {
      if (whereCalled) {
        whereString += " AND";
      }
      whereCalled = true;
      whereString += String.format(addToSql[1], "%" + username + "%");
    }

    if (region != null) {
      if (whereCalled) {
        whereString += " AND";
      }
      whereCalled = true;
      whereString += String.format(addToSql[4], "%" + region + "%");
    }

    if (whereCalled) {
      sqlString += whereString;
    }

    return sqlString;
  }


  /**
   * Helper function for the getAllUsers function returns the sql for getting a column by a list of
   * users
   *
   * Valid Columns:
   * 0 = username
   * 1 = firstName
   * 2 = middleName
   * 3 = lastName
   * 4 = region
   *
   * @param orderColumn The column the sql string is to be ordered by
   * @param direction The direction the column is to be ordered in
   * @return A sql string for ordering the column in the direction specified
   */
  private String orderSqlCreator(String orderColumn, String direction) {
    String sqlString = " GROUP BY Users.username ORDER BY ";
    String column;

    switch (orderColumn) {
      case "0":
        column = "Users.username ";
        break;
      case "1":
        column = "Users.firstName ";
        break;
      case "2":
        column = "Users.middleName ";
        break;
      case "3":
        column = "Users.lastName ";
        break;
      case "4":
        column = "ContactDetails.region ";
        break;
      default:
        return "";
    }

    sqlString += column;

    switch (direction.toLowerCase()) {
      case "ascending":
        sqlString += "ASC";
        break;
      case "descending":
        sqlString += "DESC";
        break;
      default:
        return "";
    }

    return sqlString;
  }


  /**
   * Helper function for the getAllUsers function
   * Executes the database query constructed by the getAllUsers function
   * @param connection The connection to the database
   * @param sqlString The sql string to be executed
   * @param tableName The name of the table the query is being run on (Administrators or Clinicians)
   * @return The users retrieved by the database query
   */
  private List<Object> executeGetSQL(Connection connection, String sqlString, String tableName) {
    List<Object> returningValues = new ArrayList<>(2);
    Collection<UserSummary> users = new ArrayList<>();

    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlString)) {
      while (resultSet.next()) {
        users.add(getUserSummary(resultSet, tableName));
      }
      returningValues.add(0, users);
      returningValues.add(1, true);
      return returningValues;
    } catch (SQLException e) {
      returningValues.add(0, users);
      returningValues.add(1, false);
      return returningValues;
    }
  }


  /**
   * Creates an sql string for getting all the users that meet certain parameters, passes this
   * String on to be executed.
   *
   * @param connection A SQL connection to the database. Should not be null
   * @param tableName The name of the table we wish to request the users from. Either
   * 'Administrators' or 'Clinicians'
   * @param paramList The list of parameters that the user could have specified in the request.
   * Namely, 'name', 'username', 'amount', 'offset', 'order by', 'order direction'
   * @return A Collection of the User attributes we want to show
   */
  public List<Object> getAllUsers(Connection connection, String tableName, List<String> paramList) {

    String sqlString;
    String orderBy;

    String[] addToSql = {
        " (firstName LIKE '%s' OR middleName LIKE '%s' OR lastName LIKE '%s')",
        " " + tableName + ".username LIKE '%s'", " LIMIT %s",
        " OFFSET %s", " region LIKE '%s'"};

    if (tableName.equals("Administrators")) {
      sqlString = ADMIN_TABLE_GET_SQL;
    } else {
      sqlString = CLINICIAN_TABLE_GET_SQL;
    }

    sqlString += searchNames(paramList.get(0), paramList.get(1), paramList.get(6), addToSql);

    if (paramList.get(4) != null && paramList.get(5) != null) {
      orderBy = orderSqlCreator(paramList.get(4), paramList.get(5));
      sqlString += orderBy;
    }

    if (paramList.get(2) != null) { // Search amount
      sqlString += String.format(addToSql[2], paramList.get(2));
    }

    if (paramList.get(3) != null) { // Search offset
      if (paramList.get(2) == null) {
        sqlString += String.format(addToSql[2], "16");
      }
      sqlString += String.format(addToSql[3], paramList.get(3));
    }

    return executeGetSQL(connection, sqlString, tableName);
  }


  /**
   * Takes in a user object and a database string and generates a POST SQL string for every logEntry
   * in the User's modifications list.
   *
   * @param username The username whose log is to be posted to the database. Their username should
   * not be null
   * @return Returns an SQL insert string fo a log entry
   */
  private String generateLogEntryPOSTString(String username, LogEntry entry) {
    assert username != null;
    return String
        .format(LOG_ENTRY_POST_SQL, username, entry.getValChanged(), entry.getModifyingAccount(),
            entry.getAccountModified(), entry.getOriginalVal(), entry.getChangedVal());
  }


  /**
   * Checks if the user being created is already in the database
   * @param connection The connection to the database
   * @param user The user being added to the database
   * @return An integer code of whether the user is in the database (409) or not (201)
   * @throws SQLException An exception thrown when the sql query is incorrect.
   */
  public int checkExists(Connection connection, UserCreator user) throws SQLException {
    String existsQuery = "SELECT `username` FROM Users WHERE `username`=?";
    try (PreparedStatement existsSQL = connection.prepareStatement(existsQuery)) {
      existsSQL.setString(1, user.getUsername());
      try (ResultSet existQueryResult = existsSQL.executeQuery()) {
        if (existQueryResult.isBeforeFirst()) {
          return 409;
        }
      }
    }
    return 201;
  }


  /**
   * Performs a POST request for an administrator and returns an integer of the result of the insert
   * (1 is successful). A user's username should not be null
   *
   * @param connection A SQL connection a MYSQL database, should not be null
   * @param user a user to be added to the database
   * @return a int array where each integer represents the success or failure if an individual SQL
   * query. a number >= 0 represents success
   * @throws SQLException If there is an error with the query
   */
  public int[] postUser(Connection connection, UserCreator user, String userType)
      throws SQLException {

    String userSQL;
    String typeSQL;
    String contactDetailsSQL = null;
    String logStringSQL;

    userSQL = String
        .format(USER_POST_SQL, user.getUsername(), user.getFirst(), user.getMiddle(),
            user.getLast(), user.getPassword(), userType);


    if (user.getRegion() != null ||
        user.getMobileNumber() != null) {
      contactDetailsSQL = String.format(CONTACT_DETAILS_POST_SQL,
          user.getMobileNumber(), user.getUsername(), user.getRegion());
    }

    if (userType.equals("clinician")) {
      typeSQL = String.format(CLINICIAN_INSERT_SQL, user.getUsername());
      contactDetailsSQL = String.format(CONTACT_DETAILS_CLINICIAN_POST_SQL, user.getUsername(), user.getUsername(), user.getStreetAddress(), user.getRegion());
    } else {
      typeSQL = String.format(ADMIN_INSERT_SQL, user.getUsername());
    }

    logStringSQL = generateLogEntryPOSTString(user.getUsername(), user.getEntry());

    try (Statement statement = connection.createStatement()) {
      statement.addBatch(userSQL);
      statement.addBatch(typeSQL);
      if (contactDetailsSQL != null) {
        statement.addBatch(contactDetailsSQL);
      }
      statement.addBatch(logStringSQL);
      int[] resultSet = statement.executeBatch();
      connection.commit();
      return resultSet;
    }
  }


  /**
   * Performs a DELETE request for a user and returns an integer of the deletion (1 is successful).
   *
   * @param connection A SQL connection a MYSQL database, should not be null
   * @param username The username string of the User, should not be null
   * @return a int array where each integer represents the success or failure if an individual SQL
   * query. a number >= 0 represents success
   */
  public ResponseEntity<String> deleteUser(Connection connection, String username){
    if(connection == null) {
      return new ResponseEntity<>("Couldn't connect to database", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if(username == null) {
      return new ResponseEntity<>("Please enter a valid username", HttpStatus.BAD_REQUEST);
    }
    if(username.equals("0"))
    {
      return new ResponseEntity<>("You cannot delete the default clinician", HttpStatus.FORBIDDEN);
    }
    if (username.equals("Sudo")) {
      return new ResponseEntity<>("You cannot delete the default administrator", HttpStatus.FORBIDDEN);
    }

    String userDeleteSQL = "DELETE FROM Users WHERE `username` = ?;";
    try (PreparedStatement statement = connection.prepareStatement(userDeleteSQL)) {
      statement.setString(1, username);
      int changed = statement.executeUpdate();
      connection.commit();
      if(changed > 0) {
        return new ResponseEntity<>("Deleted user", HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>("Couldn't find user", HttpStatus.NOT_FOUND);
    }
    catch (SQLException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Performs a GET request for a user and returns a map with the user inside it.
   *
   * @param connection A SQL connection a MYSQL database, should not be null
   * @param username The username string of the user
   * @param isClinician true if the user is a clinician
   * @return Returns a map with a single entry of the user being queried. The user's key is their username
   * @throws SQLException If there is an error with the query
   */
  public Map<String, User> getUser(Connection connection, String username, boolean isClinician) throws SQLException {
    Map<String, User> userMap = new LinkedHashMap<>();
    String sql = String.format("SELECT * from Users JOIN ContactDetails ON  " +
        "`Users`.username = `ContactDetails`.username" +
        " WHERE `Users`.`username` = '%s'", username);
    try(PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet userSet = statement.executeQuery()) {
      ResultSet logSet = getLogEntries(connection, username);
      User user = createUser(userSet, logSet, isClinician);
      userMap.put(username, user);
      return userMap;
    }
  }


  /**
   * Performs a GET request for an administrator and returns a result set of the get.
   *
   * @param connection A SQL connection a MYSQL database, should not be null
   * @param username The username string of the admin
   * @return Returns a result set of the admins attributes (if the admin was found)
   * @throws SQLException If there is an error with the query
   */
  public ResultSet getLogEntries(Connection connection, String username) throws SQLException {
    String sql = String
        .format("SELECT * from LogEntries WHERE `LogEntries`.`username` = '%s'", username);
    try(PreparedStatement statement = connection.prepareStatement(sql)) {
      return statement.executeQuery();
    }
  }


  /**Lambda expression for checking a string is the number 1.
   *
   */
  interface activeTest {
    boolean computeTest(String n);
  }


  /**
   * Creates an User object from the given Result Set.
   * @param set a SQL result set that has user data in it. it cannot be null
   * @param rs a SQL result set with the user's modifications logs inside it
   * @param isClinician true if the user is a clinician
   * @return Returns an user object.
   * @throws SQLException If there is an error with the query
   */
  public User createUser(ResultSet set, ResultSet rs, boolean isClinician) throws SQLException {
    if (set.next()) {
      User user;
      if (isClinician) {
        user = new Clinician(set.getString("firstName"),
            set.getString("lastName"),
            set.getString("streetAddressLineOne"),
            set.getString("region"),
            set.getString("username"),
            set.getString("password"));
      } else {
        user = new Administrator(set.getString("firstName"),
            set.getString("middleName"),
            set.getString("lastName"),
            set.getString("username"),
            set.getString("password"));
      }
      user.getContactDetails().setMobileNum(set.getString("mobileNumber"));
      user.getContactDetails().setEmail(set.getString("email"));
      user.getContactDetails().setHomeNum(set.getString("homeNumber"));
      Address add = new Address(set.getString("streetAddressLineOne"),
          set.getString("streetAddressLineTwo"),
          set.getString("suburb"),
          set.getString("city"),
          set.getString("region"),
          set.getString("postCode"),
          set.getString("countryCode"));
      user.getContactDetails().setAddress(add);
      user.setVersion(Integer.parseInt(set.getString("version")));
      activeTest isActive = integer -> (Integer.parseInt(integer) == 1);
      user.setActive(isActive.computeTest((set.getString("active"))));
      String creationString = set.getString("creationDate");
      creationString = creationString.substring(0, creationString.length() - 2);
      LocalDateTime localDateTime = formatStringToLocalDateTime(creationString);
      user.setCreationDate(localDateTime);
      addLogsFromResultSetToUser(user, rs);
      return user;
    }
    // the query returned no rows
    return null;
  }


  /**
   * Takes the Log Entries contained in the given ResultSet and adds them to the given User's
   * modification list.
   *
   * @param user User object
   * @param rs A ResultSet that should contain Logs
   * @throws SQLException If there is an error with the result set
   */
  private void addLogsFromResultSetToUser(User user, ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnsNumber = rsmd.getColumnCount();
    while (rs.next()) {
      LinkedHashMap<String, String> data = new LinkedHashMap<>();
      for (int i = 1; i <= columnsNumber; i++) {
        data.put(rsmd.getColumnName(i), rs.getString(i));
      }
      String dateString = data.get("changeTime");
      LogEntry entry = new LogEntry(data.get("accountModified"),
          data.get("modifyingAccount"),
          data.get("valChanged"),
          data.get("originalVal"),
          data.get("changedVal"),
          formatStringToLocalDateTime(dateString));
      user.getModifications().add(entry);
    }
  }

  /**
   * Formats the string of a date object to a LocalDateTime object, ignores nanoseconds
   *
   * @param time The LocalDatetime String to be formatted
   * @return a LocalDateTime
   */
  private LocalDateTime formatStringToLocalDateTime(String time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    try {
      return LocalDateTime.parse(time, formatter);
    } catch (DateTimeException e) { // the time string also contains nano seconds, which we exclude
      time = time.substring(0, time.length() - 2);
      return LocalDateTime.parse(time, formatter);
    }
  }

  private UserSummary getUserSummary(ResultSet rs, String tableName) throws SQLException {
    UserSummary userSummary = new UserSummary();
    userSummary.setUsername(rs.getString(tableName + ".username"));
    userSummary.setFirstName(rs.getString("firstName"));
    userSummary.setMiddleName(rs.getString("middleName"));
    userSummary.setLastName(rs.getString("lastName"));
    userSummary.setRegion(rs.getString("region"));
    return userSummary;
  }


  /**
   * Create a user on the system - creating entries in the Users, LogEntries, and ContactDetails
   *
   * @param connection The connection to the database
   * @param user The user being added to the database
   * @param type The type of user being created
   * @return An integer status code indicating success or otherwise
   * @throws SQLException An SQLException from when the SQL statement given is incorrect
   */
  private int userInsert(Connection connection, User user, String type, String modifyingAccount) throws SQLException {

    int code = 201;

    String userQuery =
        "INSERT INTO Users (`username`, `firstName`, `middleName`, `lastName`, `password`, `userType`) "
            + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + "`firstName`=VALUES(firstName), `middleName`=VALUES(middleName), `lastName`=VALUES(lastName), "
            + "`password`=VALUES(password), `version`=?;";
    try (PreparedStatement userPreparedStatement = connection.prepareStatement(userQuery)) {
      userPreparedStatement.setString(1, user.getUserName());
      userPreparedStatement.setString(2, user.getFirstName());
      userPreparedStatement.setString(3, user.getMiddleName());
      userPreparedStatement.setString(4, user.getLastName());
      userPreparedStatement.setString(5, user.getPassword());
      userPreparedStatement.setString(6, type);

      int userVersion = 1;
      String versionNumberIfExists = "SELECT `version` FROM Users WHERE `username` = ?;";
      try (PreparedStatement versionPreparedStatement =
          connection.prepareStatement(versionNumberIfExists)) {
        versionPreparedStatement.setString(1, user.getUserName());
        try (ResultSet versionResults = versionPreparedStatement.executeQuery()) {
          if (versionResults.isBeforeFirst()) {
            versionResults.next();
            userVersion = versionResults.getInt(1);
            code = 202;
          }
        }
      }
      userPreparedStatement.setString(7, String.valueOf(userVersion));

      int responseStatus = userPreparedStatement.executeUpdate();
      if (responseStatus == 0) {
        return 400;
      }
    }

    if (user.getContactDetails().getAddress() == null) {
      user.getContactDetails().setAddress
          (new Address(null, null, null,
              null, null, null, null));
    }

    if (user.getContactDetails().getMobileNum() == null) {
      user.getContactDetails().setMobileNum("");
    }

    String contactDetailsQuery = "INSERT INTO ContactDetails" +
        "(`mobileNumber`, `username`, `homeNumber`, `email`, `streetAddressLineOne`, `streetAddressLineTwo`, " +
        "`suburb`, `city`, `region`, `postCode`, `countryCode`) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
        "`homeNumber`=VALUES(homeNumber), `email`=VALUES(email), `streetAddressLineOne`=VALUES(streetAddressLineOne), " +
        "`streetAddressLineTwo`=VALUES(streetAddressLineTwo), `suburb`=VALUES(suburb), `city`=VALUES(city), " +
        "`region`=VALUES(region), `postCode`=VALUES(postCode), `countryCode`=VALUES(countryCode);";
    try (PreparedStatement contactDetailsPreparedStatement = connection
        .prepareStatement(contactDetailsQuery)) {
      contactDetailsPreparedStatement.setString(1, user.getContactDetails().getMobileNum());
      contactDetailsPreparedStatement.setString(2, user.getUserName());
      contactDetailsPreparedStatement.setString(3, user.getContactDetails().getHomeNum());
      contactDetailsPreparedStatement.setString(4, user.getContactDetails().getEmail());
      contactDetailsPreparedStatement
          .setString(5, user.getContactDetails().getAddress().getStreetAddressLineOne());
      contactDetailsPreparedStatement
          .setString(6, user.getContactDetails().getAddress().getStreetAddressLineTwo());
      contactDetailsPreparedStatement
          .setString(7, user.getContactDetails().getAddress().getSuburb());
      contactDetailsPreparedStatement.setString(8, user.getContactDetails().getAddress().getCity());
      contactDetailsPreparedStatement
          .setString(9, user.getContactDetails().getAddress().getRegion());
      contactDetailsPreparedStatement
          .setString(10, user.getContactDetails().getAddress().getPostCode());
      contactDetailsPreparedStatement
          .setString(11, user.getContactDetails().getAddress().getCountryCode());
      int responseStatus = contactDetailsPreparedStatement.executeUpdate();
      if (responseStatus == 0) {
        return 400;
      }
    }

    String logQuery = "INSERT INTO LogEntries" +
        "(`username`, `valChanged`, `modifyingAccount`, `accountModified`," +
        "`originalVal`, `changedVal`) VALUES (?, ?, ?, ?, ?, ?);";
    try (PreparedStatement logEntryPreparedStatement = connection.prepareStatement(logQuery)) {
      logEntryPreparedStatement.setString(1, user.getUserName());
      logEntryPreparedStatement.setString(2, "import");
      logEntryPreparedStatement.setString(3, modifyingAccount);
      logEntryPreparedStatement.setString(4, user.getUserName());
      logEntryPreparedStatement.setString(5, "");
      logEntryPreparedStatement.setString(6, "import");
      int responseStatus = logEntryPreparedStatement.executeUpdate();
      if (responseStatus == 0) {
        return 400;
      }
    }
    return code;
  }


  private List invalidUserNameNHI() {
    ArrayList<Object> returningList = new ArrayList<>();
    returningList.add(400);
    List<String> issueList = new ArrayList<>();
    issueList.add("Invalid Username - NHI");
    returningList.add(issueList);
    return returningList;
  }


  /**
   * Takes the value of the clinician that was passed from the json into being a clinician on the
   * db
   *
   * @param connection The connection to the database
   * @param clinician The clinician to be created
   * @return The integer of the status code
   * @throws SQLException An exception thrown if there is a problem with the SQL in the statement
   */
  public List importClinician(Connection connection, Clinician clinician, String modifyingAccount) throws SQLException {

    ArrayList<Object> returningList = new ArrayList<>();

    if (UserValidator.checkNHIRegex(clinician.getUserName()) ||
        clinician.getUserName().matches("[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*")) {
        return invalidUserNameNHI();
    }

    UserValidator userValidator = new UserValidator(clinician, new LinkedHashMap<>());
    UserValidationReport userReport = userValidator.getReport();

    if (userReport.getAccountStatus() == UserAccountStatus.ERROR ||
        userReport.getAccountStatus() == UserAccountStatus.INVALID) {
      returningList.add(400);
      returningList.add(userReport.getIssues());
    }

    int code = userInsert(connection, clinician, "clinician", modifyingAccount);
    if (code == 201 || code == 202) {
      String clinicianString = "INSERT IGNORE INTO Clinicians (`username`) VALUES (?)";
      try (PreparedStatement preparedStatement = connection.prepareStatement(clinicianString)) {
        preparedStatement.setString(1, clinician.getUserName());
        preparedStatement.executeQuery();
      }
    }
    returningList.add(code);
    return returningList;
  }


  /**
   * Takes the value of the administrator that was passed from the JSON into being an administrator
   * on the db.
   *
   * @param connection The connection to the database
   * @param administrator The administrator to be added to the database
   * @return the integer status code of the insertion
   * @throws SQLException An exception thrown if there is a problem with the SQL in the statement
   */
  public List importAdministrator(Connection connection, Administrator administrator, String modifyingAccount) throws SQLException {

    ArrayList<Object> returningList = new ArrayList<>();

    if (UserValidator.checkNHIRegex(administrator.getUserName()) ||
        !administrator.getUserName().matches("[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*")) {
      return invalidUserNameNHI();
    }

    UserValidator userValidator = new UserValidator(administrator, new LinkedHashMap<>());
    UserValidationReport userReport = userValidator.getReport();

    if (userReport.getAccountStatus() == UserAccountStatus.ERROR ||
        userReport.getAccountStatus() == UserAccountStatus.INVALID) {
      returningList.add(400);
      returningList.add(userReport.getIssues());
    }

    int code = userInsert(connection, administrator, "admin", modifyingAccount);
    if (code == 201 || code == 202) {
      String administratorString = "INSERT IGNORE INTO Administrators (`username`) VALUES (?)";
      try (PreparedStatement preparedStatement = connection.prepareStatement(administratorString)) {
        preparedStatement.setString(1, administrator.getUserName());
        preparedStatement.executeQuery();
      }
    }
    returningList.add(code);
    return returningList;
  }

  /**
   * Adds a log entry to the database
   * @param connection Connection to the database
   * @param username Username of the account being modified
   * @param valChanged Value that has been changed
   * @param token Token of the modifying account
   * @param changedVal The updated value
   * @return Whether the log entry was successfully stored on the database
   */
  private boolean addPatchLogEntry(Connection connection, String username, String valChanged, String token, String changedVal) {
    String modifyingAccount = getUsername(token);
    LocalDateTime now = LocalDateTime.now();

    String sql = String.format("INSERT INTO `LogEntries`(`username`, `valChanged`, `changeTime`, `modifyingAccount`, `" +
            "accountModified`, `originalVal`, `changedVal`) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
            username, valChanged, now, modifyingAccount, username, "", changedVal);
    try(PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      return changed > 0;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * Updates a user
   * @param connection connection to database
   * @param username A string of the username of the user
   * @param token the session token of the user performing the update
   * @param updates attributes to be updated
   * @param version An integer of the user's version number
   * @return HTTP response
   */
  public ResponseEntity updateUser(Connection connection, String username, String token, Map<String, Object> updates, int version) {
    if (username.equals("Sudo") || username.equals("0")) {
      throw new IllegalArgumentException("Its forbidden to patch " + username);
    } else{
      String failureToUpdate = "Failed to update user";
      StringBuilder sql = new StringBuilder(String.format(
          "UPDATE `Users` INNER JOIN `ContactDetails` ON Users.username=ContactDetails.username SET version='%s', ",
          version)); // first we make sure we always update the user's version number
      for (Entry<String, Object> command : updates.entrySet()) {
        sql.append(generateSql(command));
        if (!addPatchLogEntry(connection, username, "Profile " + command.getKey(), token,
            command.getValue().toString())) {
          return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
      String result = sql.substring(0, sql.length() - 1);
      result += String.format(" WHERE Users.username='%s'", username);
      try(PreparedStatement statement = connection.prepareStatement(result)){
        int changed = statement.executeUpdate();
        connection.commit();
        if(changed > 0) {
          return new ResponseEntity<>("Updated user", HttpStatus.ACCEPTED);
        } else {
          return new ResponseEntity<>(failureToUpdate, HttpStatus.BAD_REQUEST);
        }
      } catch (SQLException e1) {
        return new ResponseEntity<>(failureToUpdate, HttpStatus.BAD_REQUEST);
      }
    }

  }


  /**
   * Generates sql command based for update single User
   * @param command Command to be done
   * @return sql syntax of command
   */
  private String generateSql(Map.Entry command) {
    StringBuilder sql = new StringBuilder();
    if(command.getKey().equals("userAttributeCollection"))  {
      Map<String, Object> attributes = (Map<String, Object>) command.getValue();
      for (Entry<String, Object> organCommand : attributes.entrySet()) {
        if (organCommand.getValue().toString().equals("true") || organCommand.getValue().toString()
            .equals("false")) {
          sql.append(String.format(" %s=%s,", organCommand.getKey(), organCommand.getValue()));
        } else {
          sql.append(String.format(" %s=\"%s\",", organCommand.getKey(), organCommand.getValue()));
        }
      }
    } else if(command.getKey().equals("requiredOrgans") || command.getKey().equals("donatedOrgans")){
      Map<String, Object> organs = (Map<String, Object>) command.getValue();
      for (Entry<String, Object> organCommand : organs.entrySet()) {
        sql.append(String.format(" %s=%s,", organCommand.getKey(), organCommand.getValue()));
      }
    } else {
      if (command.getKey().equals("username")) {
        throw new IllegalArgumentException("Its forbidden to update a user's username");
      } else {
        String sqlCommand = String.format(" %s=\"%s\",", command.getKey(), command.getValue());
        sql.append(sqlCommand);
      }
    }
    return sql.toString();
  }

  public ResponseEntity getSystemLog(Connection connection) {
    ArrayList<LogEntry> logEntries = new ArrayList<>();
    String sql = "SELECT * FROM `LogEntries` ORDER BY changeTime DESC ";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String valChanged = resultSet.getString("valChanged");
          String modifyingAccount = resultSet.getString("modifyingAccount");
          String accountModified = resultSet.getString("accountModified");
          String originalVal = resultSet.getString("originalVal");
          String changedVal = resultSet.getString("changedVal");
          String changeDate = resultSet.getString("changeTime");
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
          LocalDateTime changeTime = LocalDateTime.parse(changeDate, formatter);
          LogEntry logEntry = new LogEntry(accountModified,modifyingAccount,valChanged,originalVal,changedVal,changeTime);
          logEntries.add(logEntry);
        }
        return new ResponseEntity<>(logEntries, HttpStatus.OK);
      }
    } catch (SQLException e) {
      return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity getCountries(Connection connection) {
    ArrayList<String> countries = new ArrayList<>();
    String sql = "SELECT * FROM Countries";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String country = resultSet.getString("Country");
          countries.add(country);
        }
        return new ResponseEntity<>(countries, HttpStatus.OK);
      }
    } catch (SQLException e) {
      return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity deleteCountry(Connection connection, String country) {
    String sql = String.format("DELETE FROM `Countries` WHERE Country = '%s'", country);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if(changed > 0) {
        return new ResponseEntity<>("Deleted country", HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>("Couldn't find country", HttpStatus.NOT_FOUND);
    } catch (SQLException e) {
      return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity postCountry(Connection connection, String country) {
    String sql = String.format("INSERT INTO Countries (Country) VALUES ('%s')", country);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if(changed > 0) {
        return new ResponseEntity<>("Inserted country", HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>("Couldn't insert country", HttpStatus.BAD_REQUEST);
    } catch (SQLException e) {
      return new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


}
