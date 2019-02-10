package seng302.model.database;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.model.*;
import seng302.model.DonorOrganInventory;
import seng302.model.ReceiverOrganInventory;
import seng302.model.person.*;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.utility.DateFormatter;
import seng302.model.utility.MedicationLogger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import static java.lang.Integer.parseInt;
import static seng302.model.utility.StringExtension.reverseNullFilter;

public class DonorReceiverService extends BaseService {

  private static final String DATE_FORMATTER = "yyyy-MM-dd";
  private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss.S";
  private static final String DATE_OF_BIRTH = "dateOfBirth";
  private static final String TITLE = "title";
  private static final String BIRTH_GENDER = "birthGender";
  private static final String GENDER = "gender";
  private static final String NOT_A_SECURITY_STRING = "password";
  private static final String USERNAME = "username";
  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
  private static final String UPDATE_DONOR_RECEIVER = "UPDATE `DonorReceivers` SET";
  private static final String WEIGHT = "weight";
  private static final String HEIGHT = "height";
  private static final String BLOOD_TYPE = "bloodType";
  private static final String BLOOD_PRESSURE = "bloodPressure";
  private static final String SMOKER = "smoker";
  private static final String ALCOHOL_CONSUMPTION = "alcoholConsumption";
  private static final String BMI_FLAG = "bodyMassIndexFlag";
  private static final String NO_SUCH_DONOR = "No such donor.";

  private static final List<String> validGenderSearchCommands = new ArrayList<>(
      Arrays.asList("male", "female", "unknown", "unspecified", "other", "any"));
  private static final List<String> validStatusSearchCommands = new ArrayList<>(
      Arrays.asList("donor", "receiver", "donor/receiver", "any", "neither"));
  private static final List<String> validRegionSearchCommands = new ArrayList<>(Arrays
      .asList("Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay",
          "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Marlborough",
          "West Coast", "Canterbury", "Otago", "Southland",  "Chatham Islands"));

  @Autowired
  public DonorReceiverService(AuthenticationTokenStore authenticationTokenStore) {
    super(authenticationTokenStore);
  }

  /**
   * Generate a SQL command for getting the organ details from the DonorReceiver table
   *
   * @param paramList The list of parameters to fetch
   * @param index The index of the organs to fetch
   * @param donation What organs they're donating
   * @return A String SQL command to get the organs from the db
   */
  private String getOrganDetailsSql(List<String> paramList, int index, String donation) {
    StringBuilder whereCommand = new StringBuilder();
    List<String> donating = new ArrayList<>(Arrays.asList(paramList.get(index).split(", ")));
    whereCommand.append("(");
    for (int i = 0; i < donating.size(); i++) {
      String organ = donating.get(i);
      String cap = organ.substring(0, 1).toUpperCase();
      String newOrgan = cap + organ.substring(1);
      String result = donation + newOrgan;
      if (i != 0) {
        whereCommand.append(" ");
      }
      whereCommand.append(result);
      whereCommand.append(" = 1");
      if (i != (donating.size() - 1)) {
        whereCommand.append(" OR");
      }
    }
    whereCommand.append(")");
    return whereCommand.toString();
  }

  /**
   * Generate the where command for DonorReceiver based on the params
   *
   * @param addToSql The array of SQL commands
   * @param paramList The list of parameters to add
   * @param statusDonor A SQL string for donating organs
   * @param statusReceiver A SQL string for receiving organs
   * @return A String SQL command to get the WHERE conditions for getting a donor from the DB
   */
  private String generateWhereCommand(String[] addToSql, List<String> paramList, String statusDonor,
      String statusReceiver) {
    String whereCommand = " WHERE";
    boolean whereCalled = false;
    //Sql query Where goes first
    if (paramList.get(0) != null) { // Search name
      String searchValue = "%" + paramList.get(0) + "%";
      whereCommand += String.format(addToSql[0], searchValue, searchValue, searchValue);
      whereCalled = true;
    }
    if (paramList.get(4) != null && !paramList.get(4).contains("any")) { // Gender
      if (whereCalled) {
        whereCommand += " AND";
      }
      String[] results = extractGenders(paramList.get(4), addToSql[4]);
      if (results[1].equals("true")) {
        whereCalled = true;
      }
      whereCommand += results[0];
    }
    if (paramList.get(5) != null) { // Region
      if (whereCalled) {
        whereCommand += " AND";
      }
      String[] results = extractRegions(paramList.get(5), addToSql[5]);
      if (results[1].equals("true")) {
        whereCalled = true;
      }
      whereCommand += results[0];
    }
    if (paramList.get(8) != null) { // minAge
      whereCommand += createSqlDatetimeMinAge(addToSql, paramList, whereCalled, 8);
      whereCalled = true;

    }
    if (paramList.get(9) != null) { // maxAge
      whereCommand += createSqlDatetimeMaxAge(addToSql, paramList, whereCalled, 9);
      whereCalled = true;
    }
    if (paramList.get(6) != null) { // donating organs
      if (whereCalled) {
        whereCommand += " AND";
      }
      whereCommand += getOrganDetailsSql(paramList, 6, "d");
      whereCalled = true;
    }
    if (paramList.get(7) != null) { // receiving organs
      if (whereCalled) {
        whereCommand += " AND";
      }
      whereCommand += getOrganDetailsSql(paramList, 7, "r");
      whereCalled = true;
    }
    if (paramList.get(3) != null && !paramList.get(3).contains("any")) { // status
      if (whereCalled) {
        whereCommand += " AND";
      }
      String[] results = extractStatuses(paramList.get(3), statusDonor, statusReceiver);
      if (results[1].equals("true")) {
        whereCalled = true;
      }
      whereCommand += results[0];
    }
    if (whereCalled) { //We have queries to take into account
      return whereCommand;
    } else {
      return "";
    }
  }

  /**
   * Takes a string of genders, joined by ", " and creates the correct statement to be added to the
   * "where" section of the sql query. For example, to get all people who have genders "male" or
   * "other", then the gendersString is "male, other".
   *
   * @param gendersString A string containing the genders of the people to search for.
   * @param addToSql The sql statement needed; "(gender='%s')".
   * @return An array of strings, the first is the sql statement, the second is whether "where" has
   * been called, "true" or "false"
   */
  private String[] extractGenders(String gendersString, String addToSql) {
    String[] genders = gendersString.split(", ");
    StringBuilder sqlResult = new StringBuilder();
    boolean whereCalled = false;
    sqlResult.append("(");
    for (int i = 0; i < genders.length; i++) {
      switch (genders[i]) {
        case "male":
          sqlResult.append(String.format(addToSql, "M"));
          whereCalled = true;
          break;
        case "female":
          sqlResult.append(String.format(addToSql, "F"));
          whereCalled = true;
          break;
        case "unknown":
          sqlResult.append(String.format(addToSql, "U"));
          whereCalled = true;
          break;
        case "unspecified":
          sqlResult.append(" (gender is NULL)");
          whereCalled = true;
          break;
        case "other":
          sqlResult.append(String.format(addToSql, "O"));
          whereCalled = true;
          break;
        default:
          break;
      }
      if (i != (genders.length - 1)) {
        sqlResult.append(" OR");
      }
    }
    sqlResult.append(")");
    return new String[]{sqlResult.toString(), Boolean.toString(whereCalled)};
  }

  /**
   * Takes a string of statuses, joined by ", " and creates the correct statement to be added to the
   * "where" section of the sql query. For example, to get all people who have status "donor" or
   * "neither", then the gendersString is "donor, neither".
   *
   * @param statusesString A string containing the statuses of the people to search for.
   * @param statusDonor The sql statement needed to search for a donor.
   * @param statusReceiver The sql statement needed to search for a receiver.
   * @return An array of strings, the first is the sql statement, the second is whether "where" has
   * been called, "true" or "false"
   */
  private String[] extractStatuses(String statusesString, String statusDonor,
      String statusReceiver) {
    String[] statuses = statusesString.split(", ");
    StringBuilder sqlResult = new StringBuilder();
    boolean whereCalled = false;
    sqlResult.append("(");
    for (int i = 0; i < statuses.length; i++) {
      switch (statuses[i]) {
        case "donor/receiver":
          sqlResult.append(String.format(" %s AND %s", statusDonor, statusReceiver));
          whereCalled = true;
          break;
        case "donor":
          sqlResult.append(String.format(" %s", statusDonor));
          whereCalled = true;
          break;
        case "receiver":
          sqlResult.append(String.format(" %s", statusReceiver));
          whereCalled = true;
          break;
        case "neither":
          sqlResult.append(String.format(" NOT %s AND NOT %s", statusDonor, statusReceiver));
          whereCalled = true;
          break;
        default:
          break;
      }
      if (i != (statuses.length - 1)) {
        sqlResult.append(" OR");
      }
    }
    sqlResult.append(")");
    return new String[]{sqlResult.toString(), Boolean.toString(whereCalled)};
  }


  /**
   * Takes a string of regions, joined by ", " and creates the correct statement to be added to the
   * "where" section of the sql query. For example, to get all people who have region "Canterbury"
   * or "Otago", then the regionsString is "Canterbury, Otago".
   *
   * @param regionsString A string containing the statuses of the people to search for.
   * @param addToSql The sql statement needed to search for a region.
   * @return An array of strings, the first is the sql statement, the second is whether "where" has
   * been called, "true" or "false"
   */
  private String[] extractRegions(String regionsString, String addToSql) {
    String[] regions = regionsString.split(", ");
    StringBuilder sqlResult = new StringBuilder();
    boolean whereCalled = false;
    sqlResult.append("(");
    for (int i = 0; i < regions.length; i++) {
      sqlResult.append(String.format(addToSql, regions[i]));
      whereCalled = true;
      if (i != (regions.length - 1)) {
        sqlResult.append(" OR");
      }
    }
    sqlResult.append(")");
    return new String[]{sqlResult.toString(), Boolean.toString(whereCalled)};
  }


  /**
   * Generates part of a WHERE command based on a time query
   *
   * @param addToSql The array of SQL commands
   * @param paramList The list of parameters to add
   * @param whereCalled Whether where has been called
   * @param index The index of the SQL query that this is being called as part of
   * @return A String SQL command to get part of the  WHERE conditions for checking the time as part
   * of the DB.
   */
  private String createSqlDatetimeMaxAge(String[] addToSql, List<String> paramList,
      boolean whereCalled, int index) {
    String result = "";
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -Integer.parseInt(paramList.get(index))); // to get previous year add -1
    cal.add(Calendar.YEAR, -1);
    cal.add(Calendar.DAY_OF_MONTH, 1);
    Date nextYear = cal.getTime();
    String formattedDate = new SimpleDateFormat(DATE_FORMATTER).format(nextYear);
    if (whereCalled) {
      result += " AND";
    }
    result += String.format(addToSql[index], formattedDate);
    return result;
  }


  /**
   * Generates part of a WHERE command based on a time query
   *
   * @param addToSql The array of SQL commands
   * @param paramList The list of parameters to add
   * @param whereCalled Whether where has been called
   * @param index The index of the SQL query that this is being called as part of
   * @return A String SQL command to get part of the  WHERE conditions for checking the time as part
   * of the DB.
   */
  private String createSqlDatetimeMinAge(String[] addToSql, List<String> paramList,
      boolean whereCalled, int index) {
    String result = "";
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -Integer.parseInt(paramList.get(index))); // to get previous year add -1
    Date nextYear = cal.getTime();
    String formattedDate = new SimpleDateFormat(DATE_FORMATTER).format(nextYear);
    if (whereCalled) {
      result += " AND";
    }
    result += String.format(addToSql[index], formattedDate);
    return result;
  }

  /**
   * Validate the status(es), gender(s), and region(s) that the endpoint received for getting all
   * donorReceivers.
   *
   * @param paramList The paramList from getAllDonorReceiver.
   * @throws SQLException If any of the keywords are invalid, then an SQL exception is thrown, which
   * gets caught and sends a 400 bad request reponse code.
   */
  private void validateKeywords(List<String> paramList) throws SQLException {
    validateStatuses(paramList.get(3));
    validateGenders(paramList.get(4));
    validateRegions(paramList.get(5));
  }

  /**
   * Validate the regions in the string given.
   *
   * @param regionsStr The string must have region names, separated by a comma and a space.
   * @throws SQLException If any of the keywords are invalid, then an SQL exception is thrown, which
   * gets caught and sends a 400 bad request response code.
   */
  private void validateRegions(String regionsStr) throws SQLException {
    if (regionsStr != null && !validRegionSearchCommands.contains(regionsStr)) {
      String[] regions = regionsStr.split(", ");
      for (String region : regions) {
        if (!validRegionSearchCommands.contains(region)) {
          throw new SQLException("This region option is invalid");
        }
      }
    }
  }

  /**
   * Validate the genders in the string given.
   *
   * @param gendersStr The string must have gender names, separated by a comma and a space.
   * @throws SQLException If any of the keywords are invalid, then an SQL exception is thrown, which
   * gets caught and sends a 400 bad request response code.
   */
  private void validateGenders(String gendersStr) throws SQLException {
    if (gendersStr != null) {
      String[] genders = gendersStr.split(", ");
      for (String gender : genders) {
        if (!validGenderSearchCommands.contains(gender)) {
          throw new SQLException("This gender option is invalid");
        }
      }
    }
  }

  /**
   * Validate the statuses in the string given.
   *
   * @param statusesStr The string must have status names, separated by a comma and a space.
   * @throws SQLException If any of the keywords are invalid, then an SQL exception is thrown, which
   * gets caught and sends a 400 bad request response code.
   */
  private void validateStatuses(String statusesStr) throws SQLException {
    if (statusesStr != null) {
      String[] statuses = statusesStr.split(", ");
      for (String status : statuses) {
        if (!validStatusSearchCommands.contains(status)) {
          throw new SQLException("The status command is invalid");
        }
      }
    }
  }


  /**
   * Adds the emergency = 0 to the end of the sql query for the contact details table, and adds the
   * limit and/or offset specified by the user.
   *
   * @param sql The current sql string.
   * @param paramList The paramList from getAllDonorReceiver
   * @param addToSql The addToSql from getAllDonorReceiver
   * @return Returns the string with everything added (that needed to be added).
   */
  private String addEndOfSQLQuery(String sql, List<String> paramList, String[] addToSql, String sortBy, String isDescending, boolean counting) {
    if (sql.contains("WHERE")) {
      sql += " AND emergency=0";
    } else {
      sql += " WHERE emergency=0";
    }

    if (!counting) {
      sql += " GROUP BY Users.username";
    }

    if (sortBy != null) {
      sql += getOrderingString(sortBy, isDescending);
    }

    if (!counting){
      // add the limit or offset
      if (paramList.get(1) != null) { // Amount to return
        sql += String.format(addToSql[1], paramList.get(1));
      }
      if (paramList.get(2) != null) { // Index the result
        if (paramList.get(1) == null) {
          sql += String.format(addToSql[1], "16"); //Default if none supplied
        }
        sql += String.format(addToSql[2], paramList.get(2));
      }
    }

    return sql;
  }


    /**
     * Gets the order of the items on the list
     * @param sortBy The thing to sort the list by
     * @param isDescending Whether the order is ascending or descending
     * @return The string order of the
     */
  private String getOrderingString(String sortBy, String isDescending) {
    String sql = "";
    if (sortBy.equalsIgnoreCase("age")) {
      if (isDescending.equalsIgnoreCase("true")) {
        sql += " ORDER BY `dateOfBirth` ASC";
      } else {
        sql += " ORDER BY `dateOfBirth` DESC";
      }
    } else if (sortBy.equalsIgnoreCase("name")) {
      if (isDescending.equalsIgnoreCase("true")) {
        sql += " ORDER BY firstName DESC, lastName DESC";
      } else {
        sql += " ORDER BY firstName ASC, lastName ASC";
      }
    } else {
      sql += " ORDER BY `" + sortBy + "`";
      if (isDescending.equalsIgnoreCase("true")) {
        sql += " DESC";
      }
    }
    return sql;
  }


  /**
   * The method to get all donorReceivers from the db based on various query params specified in the
   * paramList
   *
   * @param connection The Connection with the db.
   * @param paramList The list of query params in a specified order
   * @return A Collection of DonorReceivers matching the query
   * @throws SQLException If any SQL error occurs while executing the command
   */
  public ResponseEntity getAllDonorReceiver(Connection connection,
      List<String> paramList, String sortBy, String isDescending) throws SQLException {

    String[] addToSql = {
        " (firstName LIKE \"%s\" OR middleName LIKE \"%s\" OR lastName LIKE \"%s\")",
        " LIMIT %s", " OFFSET %s", "STATUS", " (gender=\"%s\")", " (region=\"%s\")", "DONATING",
        "RECEIVING", " (dateOfBirth<=\"%s\")", " (dateOfBirth>=\"%s\")"};
    String statusDonor = "(dLiver=1 OR dKidneys=1 OR dPancreas=1 OR dHeart=1 OR dLungs=1 OR dIntestine=1 OR dCorneas=1 OR dMiddleEars=1 OR dSkin=1 OR dBone=1 OR dBoneMarrow=1 OR dConnectiveTissue=1)";
    String statusReceiver = "(rLiver=1 OR rKidneys=1 OR rPancreas=1 OR rHeart=1 OR rLungs=1 OR rIntestine=1 OR rCorneas=1 OR rMiddleEars=1 OR rSkin=1 OR rBone=1 OR rBoneMarrow=1 OR rConnectiveTissue=1)";
    String sql = "SELECT * FROM `DonorReceivers` INNER JOIN `Users` ON DonorReceivers.username=Users.username INNER JOIN `ContactDetails` ON DonorReceivers.username=ContactDetails.username";


    String sqlCount = "SELECT COUNT(*) FROM `DonorReceivers` INNER JOIN `Users` ON DonorReceivers.username=Users.username INNER JOIN `ContactDetails` ON DonorReceivers.username=ContactDetails.username";
 ;
    validateKeywords(paramList);

    sql += generateWhereCommand(addToSql, paramList, statusDonor, statusReceiver);
    sql = addEndOfSQLQuery(sql, paramList, addToSql, sortBy, isDescending, false);



    Collection<DonorReceiverSummary> donorReceivers = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          donorReceivers.add(getDonorReceiverSummary(resultSet));
        }
      }
    }

    int total;

    sqlCount += generateWhereCommand(addToSql, paramList, statusDonor, statusReceiver);
    sqlCount = addEndOfSQLQuery(sqlCount, paramList, addToSql, sortBy, isDescending, true);
    try (PreparedStatement statement = connection.prepareStatement(sqlCount)) {
      try (ResultSet resultSet = statement.executeQuery()) {
          resultSet.next();

        total = resultSet.getInt(1);

      }
    }


    return ResponseEntity.accepted().header("total",String.valueOf(total)).body(donorReceivers);
    }



  /**
   * Returns a single DonorReceiverSummary (as returned by /donors endpoint) from a ResultSet
   * containing one row of donorReceiver table in the database.
   *
   * @param rs ResultSet containing row from donor/receiver table inner joined with users table.
   * @return DonorReceiverSummary object which contains summary of donor/receiver for displaying in
   * tables.
   * @throws SQLException If given bad ResultSet
   */
  public DonorReceiverSummary getDonorReceiverSummary(ResultSet rs) throws SQLException {
    DonorReceiverSummary donorReceiver = new DonorReceiverSummary();
    donorReceiver.setGivenName(rs.getString("firstName"));
    donorReceiver.setMiddleName(rs.getString("middleName"));
    donorReceiver.setLastName(rs.getString("lastName"));
    donorReceiver.setDateOfBirth(rs.getDate(DATE_OF_BIRTH).toLocalDate());
    donorReceiver.setNhi(rs.getString(USERNAME));
    donorReceiver.setRegion(rs.getString("region"));
    String gender;
    try{
      gender = rs.getString("gender");
      if(gender == null) {
        gender = "U";
      }
    } catch(NullPointerException e) {
      gender = "U";
    }
    donorReceiver.setGender(gender.charAt(0));
    DonorOrganInventory donorOrganInventory = parseDonorOrgans(rs);
    ReceiverOrganInventory receiverOrganInventory = parseReceiverOrgans(rs);
    donorReceiver.setDonorOrganInventory(donorOrganInventory);
    donorReceiver.setReceiverOrganInventory(receiverOrganInventory);

    return donorReceiver;
  }


  /**
   * Parses all of the donor organs from the given result set
   *
   * @param resultSet the result of a database query
   * @return An instance of DonorOrganInventory
   * @throws SQLException an exception with the parsing
   */
  private DonorOrganInventory parseDonorOrgans(ResultSet resultSet) throws SQLException {
    boolean liver = resultSet.getBoolean("dLiver");
    boolean kidney = resultSet.getBoolean("dKidneys");
    boolean pancreas = resultSet.getBoolean("dPancreas");
    boolean heart = resultSet.getBoolean("dHeart");
    boolean lungs = resultSet.getBoolean("dLungs");
    boolean intestine = resultSet.getBoolean("dIntestine");
    boolean corneas = resultSet.getBoolean("dCorneas");
    boolean middleEars = resultSet.getBoolean("dMiddleEars");
    boolean skin = resultSet.getBoolean("dSkin");
    boolean bone = resultSet.getBoolean("dBone");
    boolean boneMarrow = resultSet.getBoolean("dBoneMarrow");
    boolean connectiveTissue = resultSet.getBoolean("dConnectiveTissue");

    return new DonorOrganInventory(liver, kidney, pancreas, heart, lungs, intestine, corneas,
        middleEars, skin, bone, boneMarrow, connectiveTissue);
  }

  /**
   * Parses all of the receiver organs and timestamps form the given result set
   *
   * @param rs the result of the database query
   * @return an instance of ReceiverOrganInventory
   * @throws SQLException an exception with the parsing
   */
  private ReceiverOrganInventory parseReceiverOrgans(ResultSet rs) throws SQLException {
    boolean liver = rs.getBoolean("rLiver");
    boolean kidney = rs.getBoolean("rKidneys");
    boolean pancreas = rs.getBoolean("rPancreas");
    boolean heart = rs.getBoolean("rHeart");
    boolean lungs = rs.getBoolean("rLungs");
    boolean intestine = rs.getBoolean("rIntestine");
    boolean corneas = rs.getBoolean("rCorneas");
    boolean middleEars = rs.getBoolean("rMiddleEars");
    boolean skin = rs.getBoolean("rSkin");
    boolean bone = rs.getBoolean("rBone");
    boolean boneMarrow = rs.getBoolean("rBoneMarrow");
    boolean connectiveTissue = rs.getBoolean("rConnectiveTissue");

    String liverTimeStampStr = rs.getString("rTimeLiver");
    String kidneyTimeStampStr = rs.getString("rTimeKidneys");
    String pancreasTimeStampStr = rs.getString("rTimePancreas");
    String heartTimeStampStr = rs.getString("rTimeHeart");
    String lungsTimeStampStr = rs.getString("rTimeLungs");
    String intestineTimeStampStr = rs.getString("rTimeIntestine");
    String corneasTimeStampStr = rs.getString("rTimeCorneas");
    String middleEarsTimeStampStr = rs.getString("rTimeMiddleEars");
    String skinTimeStampStr = rs.getString("rTimeSkin");
    String boneTimeStampStr = rs.getString("rTimeBone");
    String boneMarrowTimeStampStr = rs.getString("rTimeBoneMarrow");
    String connectiveTissueTimeStampStr = rs.getString("rTimeConnectiveTissue");

    LocalDateTime liverTimeStamp;
    if (liverTimeStampStr != null) {
      liverTimeStamp = parseDateTimeFromDB(liverTimeStampStr);
    } else {
      liverTimeStamp = null;
    }

    LocalDateTime kidneyTimeStamp;
    if (kidneyTimeStampStr != null) {
      kidneyTimeStamp = parseDateTimeFromDB(kidneyTimeStampStr);
    } else {
      kidneyTimeStamp = null;
    }

    LocalDateTime pancreasTimeStamp;
    if (pancreasTimeStampStr != null) {
      pancreasTimeStamp = parseDateTimeFromDB(pancreasTimeStampStr);
    } else {
      pancreasTimeStamp = null;
    }

    LocalDateTime heartTimeStamp;
    if (heartTimeStampStr != null) {
      heartTimeStamp = parseDateTimeFromDB(heartTimeStampStr);
    } else {
      heartTimeStamp = null;
    }

    LocalDateTime lungsTimeStamp;
    if (lungsTimeStampStr != null) {
      lungsTimeStamp = parseDateTimeFromDB(lungsTimeStampStr);
    } else {
      lungsTimeStamp = null;
    }
    LocalDateTime intestineTimeStamp;
    if (intestineTimeStampStr != null) {
      intestineTimeStamp = parseDateTimeFromDB(intestineTimeStampStr);
    } else {
      intestineTimeStamp = null;
    }
    LocalDateTime corneasTimeStamp;
    if (corneasTimeStampStr != null) {
      corneasTimeStamp = parseDateTimeFromDB(pancreasTimeStampStr);
    } else {
      corneasTimeStamp = null;
    }
    LocalDateTime middleEarsTimeStamp;
    if (middleEarsTimeStampStr != null) {
      middleEarsTimeStamp = parseDateTimeFromDB(middleEarsTimeStampStr);
    } else {
      middleEarsTimeStamp = null;
    }
    LocalDateTime skinTimeStamp;
    if (skinTimeStampStr != null) {
      skinTimeStamp = parseDateTimeFromDB(skinTimeStampStr);
    } else {
      skinTimeStamp = null;
    }
    LocalDateTime boneTimeStamp;
    if (boneTimeStampStr != null) {
      boneTimeStamp = parseDateTimeFromDB(boneTimeStampStr);
    } else {
      boneTimeStamp = null;
    }
    LocalDateTime boneMarrowTimeStamp;
    if (boneMarrowTimeStampStr != null) {
      boneMarrowTimeStamp = parseDateTimeFromDB(boneMarrowTimeStampStr);
    } else {
      boneMarrowTimeStamp = null;
    }
    LocalDateTime connectiveTissueTimeStamp;
    if (connectiveTissueTimeStampStr != null) {
      connectiveTissueTimeStamp = parseDateTimeFromDB(connectiveTissueTimeStampStr);
    } else {
      connectiveTissueTimeStamp = null;
    }

    return new ReceiverOrganInventory(liver, kidney, pancreas, heart, lungs, intestine, corneas,
        middleEars, skin, bone, boneMarrow, connectiveTissue, liverTimeStamp, kidneyTimeStamp,
        pancreasTimeStamp, heartTimeStamp, lungsTimeStamp, intestineTimeStamp, corneasTimeStamp,
        middleEarsTimeStamp, skinTimeStamp, boneTimeStamp, boneMarrowTimeStamp,
        connectiveTissueTimeStamp);
  }

  /**
   * For parsing date times from the database, where they may be rounded to one millisecond. This
   * method tries to parse first with 3, then 2, then 1 decimal places of precision. If this fails,
   * then it is likely that the date time is in the incorrect format/we are expecting the wrong
   * format.
   *
   * @param dateTimeString The string to be parsed.
   * @return The LocalDateTime found in the string.
   * @throws DateTimeParseException if the date/time is in the wrong format.
   */
  private LocalDateTime parseDateTimeFromDB(String dateTimeString) throws DateTimeParseException {
    DateTimeFormatter formatter3dp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    DateTimeFormatter formatter2dp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    DateTimeFormatter formatter1dp = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    LocalDateTime result;
    try {
      result = LocalDateTime.parse(dateTimeString, formatter3dp);
    } catch (DateTimeParseException e1) {
      try {
        result = LocalDateTime.parse(dateTimeString, formatter2dp);
      } catch (DateTimeParseException e2) {
        result = LocalDateTime.parse(dateTimeString, formatter1dp);
      }
    }
    return result;
  }


  /**
   * Queries database, gets results and creates a new instance of donorOrganInventory of which it
   * returns
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An instance of the class DonorOrganInventory which represents the the users donating
   * organs
   */
  private DonorOrganInventory getDonorOrganInventory(Connection connection, String nhi) {
    DonorOrganInventory donorOrganInventory = new seng302.model.DonorOrganInventory();
    String sql = "SELECT * FROM DonorReceivers WHERE username='" + nhi + "'";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          donorOrganInventory = parseDonorOrgans(resultSet);
        }
        return donorOrganInventory;
      }
    } catch (SQLException e) {
      return donorOrganInventory;
    }
  }

  /**
   * Queries database, gets results and creates a new instance of receiverOrganInventory of which it
   * returns
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An instance of the class ReceiverOrganInventory which represents the the users
   * receiving organs
   */
  private ReceiverOrganInventory getReceiverOrganInventory(Connection connection, String nhi) {
    seng302.model.ReceiverOrganInventory receiverOrganInventory = new seng302.model.ReceiverOrganInventory();
    String sql = String.format("SELECT * FROM DonorReceivers WHERE username='%s'", nhi);
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          receiverOrganInventory = parseReceiverOrgans(resultSet);
        }
        return receiverOrganInventory;
      }
    } catch (SQLException e) {
      return receiverOrganInventory;
    }
  }


  /**
   * Helper class for the two contact detail classes
   *
   * @param connection The connection to the database
   * @param contactDetails The contact details to be retrieved from the database
   * @param sql The sal statement for getting the contact details from the database
   * @param address The address to be retrieved from the database
   * @return The contact details retrieved from the database
   */
  private ContactDetails getDetails(Connection connection, ContactDetails contactDetails,
      String sql, Address address) {
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          contactDetails.setMobileNum(resultSet.getString("mobileNumber"));
          contactDetails.setHomeNum(resultSet.getString("homeNumber"));
          contactDetails.setEmail(resultSet.getString("email"));
          address.setStreetAddressLineOne(resultSet.getString("streetAddressLineOne"));
          address.setStreetAddressLineTwo(resultSet.getString("streetAddressLineTwo"));
          address.setSuburb(resultSet.getString("suburb"));
          address.setCity(resultSet.getString("city"));
          address.setRegion(resultSet.getString("region"));
          address.setPostCode(resultSet.getString("postCode"));
          address.setCountryCode(resultSet.getString("countryCode"));
          contactDetails.setAddress(address);
        }
        return contactDetails;
      }
    } catch (SQLException e) {
      return contactDetails;
    }
  }


  /**
   * Queries the database, collects results relating to the users contact details and returns a new
   * instance of the class ContactDetails
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return new instance of the class ContactDetails containing data from the database
   */
  private ContactDetails getContactDetails(Connection connection, String nhi) {

    String sql = String
        .format("SELECT * FROM `ContactDetails` WHERE username='%s' AND emergency = 0", nhi);
    Address address = new Address("", "", "", "", "", "", "");
    ContactDetails contactDetails = new ContactDetails(address, "", "", "");
    return getDetails(connection, contactDetails, sql, address);
  }


  /**
   * Queries the database , collects results relating to the users emergency contact details and
   * returns a new instance of ContactDetails
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return a new instance of ContactDetails containing the emergency contact details
   */
  private ContactDetails getEmergencyContactDetails(Connection connection, String nhi) {

    String sql = String
        .format("SELECT * FROM `ContactDetails` WHERE username='%s' AND emergency = 1", nhi);
    Address address = new Address("", "", "", "", "", "", "");
    ContactDetails emergencyContactDetails = new ContactDetails(address, "", "", "");
    return getDetails(connection, emergencyContactDetails, sql, address);
  }


  /**
   * Calls sub methods, from which it creates a new medications instance which is returned.
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return A new instance of medications containing all of the users medication logs/current
   * medications/previous medications
   */
  private Medications getMedications(Connection connection, String nhi) {

    ArrayList<String> previousMedications = getPreviousMedications(connection, nhi);
    ArrayList<String> currentMedications = getCurrentMedications(connection, nhi);
    ArrayList<String> medicationLogs = getMedicationLogs(connection, nhi);

    return new Medications(currentMedications, previousMedications, medicationLogs);
  }


  /**
   * Sub-method of getMedications. Queries the database, creates a new ArrayList which is then added
   * to by the result of the search.
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An arrayList containing all of the users previous medications
   */
  private ArrayList<String> getPreviousMedications(Connection connection, String nhi) {
    String sql = String
        .format("SELECT * FROM Medications WHERE username='%s' AND `isCurrent`='0'", nhi);
    ArrayList<String> previousMedications = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          String medication = resultSet.getString("name");
          previousMedications.add(medication);
        }
        return previousMedications;
      }
    } catch (SQLException e) {
      return previousMedications;
    }
  }


  /**
   * Sub-method of getMedications. Queries the database, creates a new ArrayList which is then added
   * to by the result of the search.
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An arrayList containing all of the users current medications
   */
  private ArrayList<String> getCurrentMedications(Connection connection, String nhi) {
    String sql = String
        .format("SELECT * FROM Medications WHERE username='%s' AND `isCurrent`='1'", nhi);
    ArrayList<String> currentMedications = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          String medication = resultSet.getString("name");
          currentMedications.add(medication);
        }
        return currentMedications;
      }
    } catch (SQLException e) {
      return currentMedications;
    }
  }


  /**
   * Sub-method of getMedications. Queries the database, creates a new ArrayList which is then added
   * to by the result of the search.
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An arrayList containing all of the users medication logs
   */
  private ArrayList<String> getMedicationLogs(Connection connection, String nhi) {
    String sql = String.format("SELECT * FROM MedicationLogs WHERE username='%s'", nhi);
    ArrayList<String> medicationLogs = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          String medication = resultSet.getString("medicationLog");
          medicationLogs.add(medication);
        }
      }
      return medicationLogs;
    } catch (SQLException e) {
      return medicationLogs;
    }
  }


  /**
   * Queries the database, retrieves all information regarding the users illness and places them in
   * an ArrayList
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return And arrayLit containing all of the illneses the user has
   */
  private ArrayList<Illness> getMasterIllnessList(Connection connection, String nhi) {

    String sql = String.format("SELECT * FROM Illnesses WHERE username = '%s'", nhi);
    ArrayList<Illness> masterIllnessList = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          LocalDate date = resultSet.getDate("date").toLocalDate();
          String name = resultSet.getString("name");
          boolean cured = resultSet.getBoolean("cured");
          boolean chronic = resultSet.getBoolean("chronic");
          Illness illness = new Illness(name, date, cured, chronic);
          masterIllnessList.add(illness);
        }
        return masterIllnessList;
      }
    } catch (SQLException e) {
      return masterIllnessList;
    }
  }


  /**
   * Queries the database, retrieves all information regarding the user's logs and places them in an
   * ArrayList
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An Arraylist containing all of the users Logs
   */
  private ArrayList<LogEntry> getModificationsList(Connection connection, String nhi) {

    String sql = String.format("SELECT * FROM LogEntries WHERE username = '%s'", nhi);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    ArrayList<LogEntry> modifications = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          String valChanged = resultSet.getString("valChanged");
          String strTimeStamp = resultSet.getString("changeTime");
          LocalDateTime timeStamp = LocalDateTime.parse(strTimeStamp, formatter);
          String modifyingAccount = resultSet.getString("modifyingAccount");
          String accountModified = resultSet.getString("accountModified");
          String originalVal = resultSet.getString("originalVal");
          String changedVal = resultSet.getString("changedVal");
          LogEntry logEntry = new LogEntry(accountModified, modifyingAccount, valChanged,
              originalVal, changedVal, timeStamp);
          modifications.add(logEntry);
        }
        return modifications;
      }
    } catch (SQLException e) {
      return new ArrayList<>();
    }
  }


  /**
   * Queries the database, collects data relating to medical procedures and places them in an
   * ArrayList
   *
   * @param connection The connection to the database
   * @param nhi The nhi to be searched for in the database
   * @return An ArrayList containing all of the medical procedures the user has had
   */
  private ArrayList<MedicalProcedure> getMedicalProcedures(Connection connection, String nhi) {
    String sql = String.format("SELECT * FROM MedicalProcedures WHERE username='%s'", nhi);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
    ArrayList<MedicalProcedure> medicalProcedures = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql);) {
        while (resultSet.next()) {
          String summary = resultSet.getString("summary");
          String strDate = resultSet.getString("date");
          String description = resultSet.getString("description");
          LocalDate date;
          try {
            date = LocalDate.parse(strDate.substring(0, 10), formatter);
          } catch (NullPointerException e) {
            date = null;
          }
          ArrayList<String> affectedOrgans = getAffectedOrgans(connection, summary);
          MedicalProcedure medicalProcedure = new MedicalProcedure(summary, description, date,
              affectedOrgans);
          medicalProcedures.add(medicalProcedure);
        }
        return medicalProcedures;
      }
    } catch (SQLException e) {
      return medicalProcedures;
    }
  }


  /**
   * Sub-method of getMedicalProcedures. Gets all the affected organs from the database, and places
   * them in an ArrayList
   *
   * @param connection The connection to the database
   * @param summary The summary to be searched for in the database
   * @return An ArrayList containing all the organs affected by medical procedures
   */
  private ArrayList<String> getAffectedOrgans(Connection connection, String summary) {
    String sql = String.format("SELECT * FROM AffectedOrgans WHERE summary = '%s'", summary);
    ArrayList<String> affectedOrgans = new ArrayList<>();
    try (Statement statement = connection.createStatement();) {
      try (ResultSet resultSet = statement.executeQuery(sql);) {
        while (resultSet.next()) {
          String affectedOrgan = resultSet.getString("affectedOrganName");
          affectedOrgans.add(affectedOrgan);
        }
        return affectedOrgans;
      }
    } catch (SQLException e) {
      return new ArrayList<>();
    }
  }


  /**
   * Takes a result set from a previous query, and gets the required values to create an instance of
   * User Attribute collection;
   *
   * @param rs The result set form the original query in getDonorReceiverSingle
   * @return An instance of UserAttributeCollection which contains the users data
   * @throws SQLException An exception if the query fails or there isn't the required column in the
   * table
   */
  private UserAttributeCollection getUserAttributeCollection(ResultSet rs) throws SQLException {
    String height = rs.getString(HEIGHT);
    String weight = rs.getString(WEIGHT);
    String bloodType = rs.getString(BLOOD_TYPE);
    String bloodPressure = rs.getString(BLOOD_PRESSURE);
    String alcoholConsumption = rs.getString(ALCOHOL_CONSUMPTION);
    boolean bodyMassIndexFlag = rs.getBoolean(BMI_FLAG);
    UserAttributeCollection userAttributeCollection = new UserAttributeCollection();
    try {
      boolean smoker = rs.getBoolean(SMOKER);
     userAttributeCollection.setSmoker(smoker);
   } catch (NullPointerException e) {
      userAttributeCollection.setSmoker(false);
   }
    try {
      userAttributeCollection.setHeight(Double.parseDouble(height));
    } catch (NullPointerException e) {
      userAttributeCollection.setHeight(0.0);
    }
    try {
      userAttributeCollection.setWeight(Double.parseDouble(weight));
    } catch (NullPointerException e) {
      userAttributeCollection.setWeight(0.0);
    }
    try {
      userAttributeCollection.setAlcoholConsumption(Double.parseDouble(alcoholConsumption));
    } catch (NullPointerException e) {
      userAttributeCollection.setAlcoholConsumption(0.0);
    }
    userAttributeCollection.setBMI(
        calculateBMI(userAttributeCollection.getHeight(), userAttributeCollection.getWeight()));
    userAttributeCollection.setBloodType(bloodType);
    userAttributeCollection.setBloodPressure(bloodPressure);
    userAttributeCollection.setBodyMassIndexFlag(bodyMassIndexFlag);
    return userAttributeCollection;
  }


  /**
   * Calculates the bmi of the user
   *
   * @param height the height of the user in meters
   * @param weight the weight of the user in kgs
   * @return double, the bmi of the user
   */
  private double calculateBMI(Double height, Double weight) {
    if ((height == 0.0) || (weight == 0.0)) {
      return 0.0;
    } else {
      return (weight / (height * height));
    }
  }


  /**
   * Updates a user
   *
   * @param connection connection to database
   * @param nhi Nhi of the donor
   * @param updates attributes to be updated
   * @return HTTP response
   */
  public ResponseEntity updateUser(Connection connection, String nhi, Map<String, Object> updates,
      int version) {
    String failureToUpdate = "Failed to update donor";
    String sql = "UPDATE `Users` SET";
    Iterator<Map.Entry<String, Object>> iterator = updates.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> command = iterator.next();
      sql += generateSql(command);
    }
    String result = sql.substring(0, sql.length() - 1);
    result += String.format(" WHERE username=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(result)) {
      Integer changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        String versionSql = String
            .format("UPDATE `Users` SET version=%s WHERE username=\"%s\"", version, nhi);
        try (PreparedStatement versionUpdate = connection.prepareStatement(versionSql)) {
          Integer versionChanged = versionUpdate.executeUpdate();
          connection.commit();
          if (versionChanged > 0) {
            return new ResponseEntity<String>("Updated donor", HttpStatus.ACCEPTED);
          } else {
            return new ResponseEntity<String>(failureToUpdate, HttpStatus.BAD_REQUEST);
          }
        } catch (SQLException e1) {
          return new ResponseEntity<String>(failureToUpdate, HttpStatus.BAD_REQUEST);
        }
      } else {
        return new ResponseEntity<String>(failureToUpdate, HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<String>(failureToUpdate, HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Generates sql command based for update single donor
   *
   * @param command Command to be done
   * @return sql syntax of command
   */
  private String generateSql(Map.Entry command) {
    String sql = "";
    if (command.getKey().equals("userAttributeCollection")) {
      Map<String, Object> attributes = (Map<String, Object>) command.getValue();
      Iterator<Map.Entry<String, Object>> attributesIterator = attributes.entrySet().iterator();
      while (attributesIterator.hasNext()) {
        Map.Entry<String, Object> organCommand = attributesIterator.next();
        if (organCommand.getValue().toString().equals("true") || organCommand.getValue().toString()
            .equals("false")) {
          sql += String.format(" %s=%s,", organCommand.getKey(), organCommand.getValue());
        } else {
          sql += String.format(" %s=\"%s\",", organCommand.getKey(), organCommand.getValue());
        }
      }
    } else if (command.getKey().equals("requiredOrgans") || command.getKey()
        .equals("donatedOrgans")) {
      Map<String, Object> organs = (Map<String, Object>) command.getValue();
      Iterator<Map.Entry<String, Object>> organsIterator = organs.entrySet().iterator();
      while (organsIterator.hasNext()) {
        Map.Entry<String, Object> organCommand = organsIterator.next();
        sql += String.format(" %s=%s,", organCommand.getKey(), organCommand.getValue());
      }
    } else {
      String sqlCommand = String.format(" %s=\"%s\",", command.getKey(), command.getValue());
      sql += sqlCommand;
    }

    return sql;

  }


  /**
   * Adds an illness to the table illness in the database
   *
   * @param connection The connection to the database
   * @param nhi The user that has the illness
   * @param illness An object of type illness that contains date, name, cured and chronic
   * information on the illness
   * @return A responseEntity dictating if the transaction was successful or not and why.
   */
  public ResponseEntity addUserIllness(Connection connection, String nhi, Illness illness) {
    int isChronic = 0;
    if (illness.isChronic()) {
      isChronic = 1;
    }
    int cured = 0;
    if (illness.isCured()) {
      cured = 1;
    }
    String SQL = String
        .format("INSERT INTO Illnesses (`username`, `name`, `date`, `cured`, `chronic`) " +
                "VALUES ('%s','%s','%s','%s','%s')", nhi, illness.getName(), illness.getDate(), cured,
            isChronic);
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQL);) {
      int updateStatus = preparedStatement.executeUpdate();
      connection.commit();
      if (updateStatus == 0) {
        return new ResponseEntity<String>(
            "ERROR: Something went wrong while trying to add the illness",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<String>("Added illness", HttpStatus.CREATED);
    } catch (SQLException e) {
      return new ResponseEntity<String>("ERROR: Invalid request", HttpStatus.BAD_REQUEST);
    }
  }


  public ResponseEntity addUserContactDetails(Connection connection, String username,
      boolean isEmergency, ContactDetails contactDetails) {
    String sql = "INSERT INTO ContactDetails (`mobileNumber`, `username`, `homeNumber`, `email`, "
        + "`streetAddressLineOne`, `streetAddressLineTwo`, `suburb`, `city`, `region`, `postCode`, "
        + "`countryCode`, `emergency`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, reverseNullFilter(contactDetails.getMobileNum()));
      preparedStatement.setString(2, username);
      preparedStatement.setString(3, reverseNullFilter(contactDetails.getHomeNum()));
      preparedStatement.setString(4, reverseNullFilter(contactDetails.getEmail()));
      Address address = contactDetails.getAddress();
      preparedStatement.setString(5, reverseNullFilter(address.getStreetAddressLineOne()));
      preparedStatement.setString(6, reverseNullFilter(address.getStreetAddressLineTwo()));
      preparedStatement.setString(7, reverseNullFilter(address.getSuburb()));
      preparedStatement.setString(8, reverseNullFilter(address.getCity()));
      preparedStatement.setString(9, reverseNullFilter(address.getRegion()));
      preparedStatement.setString(10, reverseNullFilter(address.getPostCode()));
      preparedStatement.setString(11, reverseNullFilter(address.getCountryCode()));
      preparedStatement.setBoolean(12, isEmergency);

      int updateStatus = preparedStatement.executeUpdate();
      try {
        // This is needed for the database to do the query! Without this the database query is never run
        connection.commit();
      } catch (SQLException e) {
        return new ResponseEntity<String>(
            "ERROR: Something went wrong while trying to add the contact details",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
      if (updateStatus == 0) {
        return new ResponseEntity<String>(
            "ERROR: Something went wrong while trying to add the contact details",
            HttpStatus.CONFLICT);
      }
      return new ResponseEntity<String>("Added contact details", HttpStatus.CREATED);
    } catch (SQLException exception) {
        if (exception.getErrorCode() == 1062)
        return new ResponseEntity<String>("ERROR: Contact details already exist.",HttpStatus.CONFLICT);
        else {
          return new ResponseEntity<String>("ERROR: Something went wrong while trying to add the contact details", HttpStatus.BAD_REQUEST);
        }

    }
  }


  public ResponseEntity addUserMedicalProcedure(Connection connection, String username,
      MedicalProcedure medicalProcedure) {
    String summary = medicalProcedure.getSummary();
    String description = medicalProcedure.getDescription();
    LocalDate date = medicalProcedure.getDate();
    String SQL;
    if (date == null) {
      SQL = String.format(
          "INSERT INTO MedicalProcedures (`username`, `summary`, `date`, `description`) VALUES ('%s','%s', NULL,'%s')",
          username, summary, description);
    } else {
      SQL = String.format(
          "INSERT INTO MedicalProcedures (`username`, `summary`, `date`, `description`) VALUES ('%s','%s','%s','%s')",
          username, summary, date, description);
    }
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
      int updateStatus = preparedStatement.executeUpdate();
      if (updateStatus == 0) {
        return new ResponseEntity<String>(
            "ERROR: Something went wrong while trying to add the medical procedures",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
      updateStatus = addUserAffectedOrgans(connection, username, medicalProcedure);
      if (updateStatus == 0) {
        return new ResponseEntity<String>(
            "ERROR: Something went wrong while trying to add the medical procedures",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<String>("Added medical procedure", HttpStatus.CREATED);
    } catch (SQLException e) {
      // Bad Information Given or Not enough information given
      return new ResponseEntity<String>("ERROR: Invalid request", HttpStatus.BAD_REQUEST);
    }
  }


  private int addUserAffectedOrgans(Connection connection, String username,
      MedicalProcedure medicalProcedure) throws SQLException {
    int updateStatus = 1;
    ArrayList<String> affectedOrgans = medicalProcedure.getAffectedOrgans();
    for (String organ : affectedOrgans) {
      String SQL;
      if (medicalProcedure.getDate() == null) {
        SQL = String.format(
            "INSERT INTO AffectedOrgans (`username`, `summary`, `affectedOrganName`, `date`) VALUES ('%s','%s','%s', NULL)",
            username, medicalProcedure.getSummary(), organ, medicalProcedure.getDate());
      } else {
        SQL = String.format(
            "INSERT INTO AffectedOrgans (`username`, `summary`, `affectedOrganName`, `date`) VALUES ('%s','%s','%s','%s')",
            username, medicalProcedure.getSummary(), organ, medicalProcedure.getDate());
      }
      try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
        updateStatus = preparedStatement.executeUpdate();
        if (updateStatus == 0) {
          break;
        }
      }
    }
    return updateStatus;
  }


  /**
   * Updates the affected organs in a procedure
   *
   * @param connection Connection to dbc
   * @param username nhi of the user
   * @param summary summary of the procedure
   * @param date date of the procedure
   * @param organs organs that are affected, new version
   * @return Status of query
   * @throws SQLException If database throws an error
   */
  private int updateUserAffectedOrgans(Connection connection, String username, String summary,
      String date, List<String> organs) throws SQLException {
    int updateStatus = 1;
    String deleteSql = String.format("DELETE FROM `AffectedOrgans` WHERE summary=\"%s\"", summary);
    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
      int changed = deleteStatement.executeUpdate();
      if(changed > 0) {
        for (String organ : organs) {
          String SQL;
          if (date == null) {
            SQL = String.format(
                "INSERT INTO AffectedOrgans (`username`, `summary`, `affectedOrganName`, `date`) VALUES ('%s','%s','%s', NULL)",
                username, summary, organ);
          } else {
            SQL = String.format(
                "INSERT INTO AffectedOrgans (`username`, `summary`, `affectedOrganName`, `date`) VALUES ('%s','%s','%s','%s')",
                username, summary, organ, date);
          }
          try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            updateStatus = preparedStatement.executeUpdate();
            if (updateStatus == 0) {
              break;
            }
          }
        }
      }
    }
    return updateStatus;
  }


  /**
   * Updates a donors illness
   *
   * @param connection DBC connection
   * @param nhi nhi of the donor
   * @param illness illness to be edited
   * @param updates udpate commands to be done
   * @return HTTP Response
   */
  public ResponseEntity updateUserIllness(Connection connection, String nhi, String illness,
      Map<String, Object> updates, int version, String token) {
    String sql = "UPDATE `Illnesses` SET";
    Iterator<Map.Entry<String, Object>> iterator = updates.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> command = iterator.next();
      if (command.getValue().toString().equals("true")) {
        sql += String.format(" %s=1,", command.getKey());
      } else if (command.getValue().toString().equals("false")) {
        sql += String.format(" %s=0,", command.getKey());
      } else {
        sql += String.format(" %s=\"%s\",", command.getKey(), command.getValue());
      }
      if (!addPatchLogEntry(connection, nhi, "Illness " + command.getKey(), token,
          command.getValue().toString())) {
        return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\" AND name=\"%s\"", nhi, illness);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update Illness", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Illness", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Updates a donors receiving organs
   *
   * @param connection Connection to the database
   * @param nhi Nhi of the donor donating
   * @param token token of the editing user
   * @param updates updates to be done
   * @param version version of the donor
   * @return Http Response of query
   */
  public ResponseEntity updateUserDonorOrgans(Connection connection, String nhi, String token,
      Map<String, Object> updates, int version) {
    String sql = UPDATE_DONOR_RECEIVER;
    Iterator<Map.Entry<String, Object>> iterator = updates.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> command = iterator.next();
      String databaseVariable = String.format("d%s", command.getKey());
      sql += String.format(" %s=%s,", databaseVariable, command.getValue());
      if (!addPatchLogEntry(connection, nhi, "Donating Organs " + command.getKey(), token,
          command.getValue().toString())) {
        return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update Donating Organs", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Donating Organs", HttpStatus.BAD_REQUEST);
    }
  }

  private HashMap<String, Boolean> findChangedOrgans(HashMap<String, Boolean> changed, ReceiverOrganInventory receiverOrganInventory, Map<String, Object> updates) {
    if (updates.get("liver") != null && receiverOrganInventory.getLiver() == (boolean) updates.get("liver")) {
      changed.put("liver", false);
    } else {
      changed.put("liver", true);
    }

    if (updates.get("kidneys") != null && receiverOrganInventory.getKidneys() == (boolean) updates.get("kidneys")) {
      changed.put("kidneys", false);
    } else {
      changed.put("kidneys", true);
    }

    if (updates.get("pancreas") != null && receiverOrganInventory.getPancreas() == (boolean) updates.get("pancreas")) {
      changed.put("pancreas", false);
    } else {
      changed.put("pancreas", true);
    }

    if (updates.get("heart") != null && receiverOrganInventory.getHeart() == (boolean) updates.get("heart")) {
      changed.put("heart", false);
    } else {
      changed.put("heart", true);
    }

    if (updates.get("lungs") != null && receiverOrganInventory.getLungs() == (boolean) updates.get("lungs")) {
      changed.put("lungs", false);
    } else {
      changed.put("lungs", true);
    }

    if (updates.get("intestine") != null && receiverOrganInventory.getIntestine() == (boolean) updates.get("intestine")) {
      changed.put("intestine", false);
    } else {
      changed.put("intestine", true);
    }


    if (updates.get("corneas") != null && receiverOrganInventory.getCorneas() == (boolean) updates.get("corneas")) {
      changed.put("corneas", false);
    } else {
      changed.put("corneas", true);
    }

    if (updates.get("middleEars") != null && receiverOrganInventory.getMiddleEars() == (boolean) updates.get("middleEars")) {
      changed.put("middleEars", false);
    } else {
      changed.put("middleEars", true);
    }


    if (updates.get("skin") != null && receiverOrganInventory.getSkin() == (boolean) updates.get("skin")) {
      changed.put("skin", false);
    } else {
      changed.put("skin", true);
    }

    if (updates.get("bone") != null && receiverOrganInventory.getBone() == (boolean) updates.get("bone")) {
      changed.put("bone", false);
    } else {
      changed.put("bone", true);
    }


    if (updates.get("boneMarrow") != null && receiverOrganInventory.getBoneMarrow() == (boolean) updates.get("boneMarrow")) {
      changed.put("boneMarrow", false);
    } else {
      changed.put("boneMarrow", true);
    }

    if (updates.get("connectiveTissue") != null && receiverOrganInventory.getConnectiveTissue() == (boolean) updates.get("connectiveTissue")) {
      changed.put("connectiveTissue", false);
    } else {
      changed.put("connectiveTissue", true);
    }
    return changed;
  }


  /**
   * Updates a donor receivers receiving organs and their time stamps
   *
   * @param connection Connection to the database
   * @param nhi nhi of the donor being edited
   * @param token token of the editing account
   * @param updates updates to be done
   * @param version version of the account being updated
   * @return Http response of the query
   */
  public ResponseEntity updateUserReceiverOrgans(Connection connection, String nhi, String token,
      Map<String, Object> updates, String version) throws SQLException {

    HashMap<String, Boolean> changes = new HashMap<>();
    ReceiverOrganInventory receiverOrganInventory = getReceiverOrganInventory(connection, nhi);
    changes = findChangedOrgans(changes, receiverOrganInventory, updates);

    String[] brokenString = version.split(":");
    int realVersion;
    String reason = "";
    boolean transplant = false;
    if (brokenString.length == 1) {
      realVersion = parseInt(version);
    } else {
      transplant = true;
      reason = brokenString[1];
      realVersion = parseInt(brokenString[2]);
    }
    String sql = UPDATE_DONOR_RECEIVER;
    Iterator<Map.Entry<String, Object>> iterator = updates.entrySet().iterator();
    Pattern pattern = Pattern.compile("(\\w*)(TimeStamp)");
    while (iterator.hasNext()) {
      Map.Entry<String, Object> command = iterator.next();
      String databaseVariable;
      if (command.getKey().endsWith("TimeStamp")) { //Changes api spec given to database variable
        Matcher matcher = pattern.matcher(command.getKey());
        if (matcher.find()) {
          String organString = matcher.group(1);
          String capitalised = organString.substring(0, 1).toUpperCase() + organString.substring(1);
          if (capitalised.equalsIgnoreCase("MiddleEar")) {
            capitalised = "MiddleEars";
          }
          databaseVariable = String.format("rTime%s", capitalised);
          if (command.getValue().equals("")) {
            sql += String.format(" %s=\"%s\",", databaseVariable, "1970-01-01 01:00:00");
          } else {
            sql += String.format(" %s=\"%s\",", databaseVariable, command.getValue());
          }
        }
      } else {
        String capitalised = command.getKey().substring(0, 1).toUpperCase() + command.getKey().substring(1);
        databaseVariable = String.format("r%s", capitalised);

        int result = (boolean) command.getValue() ? 1 : 0;
        sql += String.format(" %s=%s,", databaseVariable, result);
      }
      String extraReason = "";
      if (transplant) {
        if (!command.getKey().contains("TimeStamp")) {
          if (changes.get(command.getKey())) {
            if (reason.equalsIgnoreCase("D")) {
              extraReason = ". Reason for removal: Death";
            } else if (reason.equalsIgnoreCase("M")) {
              extraReason = ". Reason for removal: Mistake";
            } else if (reason.equalsIgnoreCase("C")) {
              extraReason = ". Reason for removal: Cured";
            }
          }
        }
      }
      if (command.getValue().equals("")) {
        if (!addPatchLogEntry(connection, nhi, "Receiving Organs " + command.getKey(), token,
                "1970-01-01 01:00:00"+extraReason)) {
          return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
                  HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else {
        if (!addPatchLogEntry(connection, nhi, "Receiving Organs " + command.getKey(), token,
                command.getValue().toString()+extraReason)) {
          return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
                  HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }

    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        ResponseEntity result = updateVersion(connection, realVersion, nhi);
        if (!result.getStatusCode().equals(HttpStatus.ACCEPTED)) {
          return result;
        }
      } else {
        return new ResponseEntity<>("Failed to update Donating Organs", HttpStatus.BAD_REQUEST);
      }
    }
    DonorReceiver donorReceiver;
    if (changes.size() > 0) {
      // get the donors name
      Map<String, DonorReceiver> map = getDonorReceiverSingle(connection, nhi);
      donorReceiver = map.get(nhi);
      String name = donorReceiver.fullName();
      String region = donorReceiver.getContactDetails().getAddress().getRegion();
      // update the transplant waiting list
      // use updates - is capitalised
      // use changes for the changed organs - not capitalised
      for (String organChanged: changes.keySet()) {
        // need to update the waiting list
        // if updates at organ is true, then add to waiting list
        // else, remove from waiting list
        if (changes.get(organChanged)) {
          String sqlWaitingList;
          String stringTimeStamp = (String) updates.get(organChanged + "TimeStamp");
          LocalDateTime dateTime;
          if (stringTimeStamp == null) {
            dateTime = LocalDateTime.of(1970, 1, 1, 1, 0);
          } else {
            stringTimeStamp = stringTimeStamp.replace("T", " ");
            if (stringTimeStamp.equalsIgnoreCase("")) {
              dateTime = LocalDateTime.of(1970, 1, 1, 1, 0);
            } else {
              dateTime = getLocalDateTime(stringTimeStamp);
            }
          }
          try {
            if ((boolean) updates.get(organChanged)) {
              sqlWaitingList = "INSERT INTO RequiredOrgans (username, name, organ, region, registrationTime) VALUES (?, ?, ?, ?, ?)";
              try (PreparedStatement statement = connection.prepareStatement(sqlWaitingList)) {
                statement.setString(1, nhi);
                statement.setString(2, name);
                statement.setString(3, getOrganName(organChanged));
                statement.setString(4, region);
                statement.setTimestamp(5, Timestamp.valueOf(dateTime));
                statement.executeUpdate();
                connection.commit();
              }
              // nhi, name, getOrganName(organCapital), region,
            } else {
              sqlWaitingList = "DELETE FROM RequiredOrgans WHERE username = ? AND organ = ?";
              try (PreparedStatement statement = connection.prepareStatement(sqlWaitingList)) {
                statement.setString(1, nhi);
                statement.setString(2, getOrganName(organChanged));
                statement.executeUpdate();
                connection.commit();
              }
            }
          } catch (NullPointerException e) {
            System.err.print("");
          }
        }
      }
    }
    return new ResponseEntity<>("Updated donor", HttpStatus.ACCEPTED);
  }


  public LocalDateTime getLocalDateTime(String timeString) {
    LocalDateTime dateTime;
    try {
      dateTime = LocalDateTime
          .parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    } catch (DateTimeParseException e) {
      try {
        dateTime = LocalDateTime
            .parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
      } catch (DateTimeParseException e1) {
        try {
          dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        } catch (DateTimeParseException e2) {
          dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
      }
    }
    return dateTime;
  }


  public String getOrganName(String organ) {
    if (organ.equalsIgnoreCase("liver")) {
      return "Liver";
    } else if (organ.equalsIgnoreCase("kidneys")) {
      return "Kidneys";
    } else if (organ.equalsIgnoreCase("pancreas")) {
      return "Pancreas";
    } else if (organ.equalsIgnoreCase("heart")) {
      return "Heart";
    } else if (organ.equalsIgnoreCase("lungs")) {
      return "Lungs";
    } else if (organ.equalsIgnoreCase("intestine")) {
      return "Intestine";
    } else if (organ.equalsIgnoreCase("corneas")) {
      return "Corneas";
    } else if (organ.equalsIgnoreCase("middleEar") || organ.equalsIgnoreCase("middleEars")) {
      return "Middle Ears";
    } else if (organ.equalsIgnoreCase("skin")) {
      return "Skin";
    } else if (organ.equalsIgnoreCase("bone")) {
      return "Bone";
    } else if (organ.equalsIgnoreCase("boneMarrow")) {
      return "Bone Marrow";
    } else if (organ.equalsIgnoreCase("connectiveTissue")) {
      return "Connective Tissue";
    } else {
      return "Unknown Organ";
    }
  }

  /**
   * Updates a donorReceivers userAttributeCollection.
   *
   * @param connection Connection to the database.
   * @param nhi The NHI of the donor to be updated.
   * @param updates The new userAttributeCollection.
   * @param version The version of the account.
   * @param token The token of the logged in user.
   * @return The Http Response to the query.
   */
  public ResponseEntity updateUserAttributeCollection(Connection connection, String nhi,
      Map<String, Object> updates, int version, String token) {
    String sql = "UPDATE `DonorReceivers` SET `height` = ?, `weight` = ?, `bloodType` = ?, `bloodPressure` = ?, `smoker` = ?, `alcoholConsumption` = ?, `bodyMassIndexFlag` = ? WHERE `DonorReceivers`.`username` = ?";
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      addHeight(statement, updates);
      addWeight(statement, updates);
      addBloodType(statement, updates);
      addBloodPressure(statement, updates);
      addSmokerStatus(statement, updates);
      addAlcoholConsumption(statement, updates);
      addBmiFlag(statement, updates);
      statement.setString(8, nhi);
      addPatchLogEntriesUserAttributeCollection(connection, updates, nhi, token);
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e) {
      if (e.getMessage().equals("Could not update logs correctly.")) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      } else if (e.getMessage().equals(NO_SUCH_DONOR)) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } catch (DataFormatException e2) {
      return new ResponseEntity<>(e2.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e3) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Add the log entries for the updates to be applied to the userAttributeCollection. Should be
   * called before the updates are applied so that there is still access to the original values.
   *
   * @param connection The connection to the database.
   * @param updates The new userAttributeCollection.
   * @param nhi The NHI of the user being updated.
   * @param token The token of the logged in user applying the updates.
   * @throws SQLException Thrown if there is an error getting something from the database, or if the
   * logs are not updated correctly, or if the donor does not exist.
   */
  private void addPatchLogEntriesUserAttributeCollection(Connection connection,
      Map<String, Object> updates, String nhi, String token) throws SQLException {
    String sql = "SELECT `height`, `weight`, `bloodType`, `bloodPressure`, `smoker`, `alcoholConsumption`, `bodyMassIndexFlag` FROM `DonorReceivers` WHERE `DonorReceivers`.`username` = ?";
    ;
    Double originalHeight = null;
    Double originalWeight = null;
    String originalBloodType = null;
    String originalBloodPressure = null;
    Boolean originalSmoker = null;
    String originalAlcoholConsumption = null;
    Boolean originalBmiFlag = null;
    String failedToUpdateLogs = "Could not update logs correctly.";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, nhi);
      try (ResultSet originalValues = statement.executeQuery();) {
        int i = 0;
        while (originalValues.next()) {
          i++;
          originalHeight = originalValues.getDouble(1);
          if (originalValues.wasNull()) {
            originalHeight = null;
          }
          originalWeight = originalValues.getDouble(2);
          if (originalValues.wasNull()) {
            originalWeight = null;
          }
          originalBloodType = originalValues.getString(3);
          originalBloodPressure = originalValues.getString(4);
          originalSmoker = originalValues.getBoolean(5);
          if (originalValues.wasNull()) {
            originalSmoker = null;
          }
          originalAlcoholConsumption = originalValues.getString(6);
          originalBmiFlag = originalValues.getBoolean(7);
          if (originalValues.wasNull()) {
            originalBmiFlag = null;
          }
        }
        if (i > 0) {
          String origHeight = originalHeight == null ? "null" : originalHeight.toString();
          String origWeight = originalWeight == null ? "null" : originalWeight.toString();
          String origSmoker = originalSmoker == null ? "null" : originalSmoker.toString();
          String origBmiFlag = originalBmiFlag == null ? "null" : originalBmiFlag.toString();
          if (checkIfValueChanged(originalHeight, updates.get(HEIGHT).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, HEIGHT, token,
              origHeight, updates.get(HEIGHT).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalWeight, updates.get(WEIGHT).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, WEIGHT, token,
              origWeight, updates.get(WEIGHT).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalBloodType, updates.get(BLOOD_TYPE).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, BLOOD_TYPE, token,
              originalBloodType, updates.get(BLOOD_TYPE).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalBloodPressure,
              updates.get(BLOOD_PRESSURE).toString()) && !addPatchLogEntryWithOrigVal(
              connection, nhi, BLOOD_PRESSURE, token, originalBloodPressure,
              updates.get(BLOOD_PRESSURE).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalSmoker, updates.get(SMOKER).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, SMOKER, token,
              origSmoker, updates.get(SMOKER).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalAlcoholConsumption,
              updates.get(ALCOHOL_CONSUMPTION).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, ALCOHOL_CONSUMPTION, token,
              originalAlcoholConsumption, updates.get(ALCOHOL_CONSUMPTION).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
          if (checkIfValueChanged(originalBmiFlag, updates.get(BMI_FLAG).toString())
              && !addPatchLogEntryWithOrigVal(connection, nhi, BMI_FLAG, token,
              origBmiFlag, updates.get(BMI_FLAG).toString())) {
            throw new SQLException(failedToUpdateLogs);
          }
        } else {
          throw new SQLException(NO_SUCH_DONOR);
        }
      }
    } catch (SQLException e) {
      if (e.getMessage().equals(NO_SUCH_DONOR)) {
        throw new SQLException(e.getMessage());
      }
      throw new SQLException(failedToUpdateLogs);
    }
  }


  /**
   * Checks if a value has been changed from null to not null, or from a value to a different
   * value.
   *
   * @param originalValue The original value.
   * @param newValue The new value.
   * @return True if the value has been changed, true if it is still the same.
   */
  private boolean checkIfValueChanged(Object originalValue, String newValue) {
    if ((originalValue == null && !(newValue.equals("") || newValue.equals("null")))) {
      return true;
    } else if (originalValue != null && !originalValue.toString().equals(newValue)) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Adds the new height to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the height is invalid or missing.
   */
  private void addHeight(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String heightStr;
    double height;
    // extract the string
    try {
      heightStr = updates.get(HEIGHT).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Height not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (heightStr.equals("null")) {
        statement.setNull(1, Types.DOUBLE);
      } else {
        height = Double.parseDouble(heightStr);
        // if the height is valid, then add it to the statement
        if (UserValidator.validateHeight(height)) {
          statement.setDouble(1, height);
        } else {
          throw new DataFormatException("Invalid height.");
        }
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Adds the new weight to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the weight is invalid or missing.
   */
  private void addWeight(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String weightStr;
    double weight;
    // extract the string
    try {
      weightStr = updates.get(WEIGHT).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Weight not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (weightStr.equals("null")) {
        statement.setNull(2, Types.DOUBLE);
      } else {
        weight = Double.parseDouble(weightStr);
        // if the weight is valid, then add it to the statement
        if (UserValidator.validateWeight(weight)) {
          statement.setDouble(2, weight);
        } else {
          throw new DataFormatException("Invalid weight.");
        }
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }

  /**
   * Adds the new blood type to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the blood type is invalid or missing.
   */
  private void addBloodType(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String bloodType;
    // extract the string
    try {
      bloodType = updates.get(BLOOD_TYPE).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Blood Type not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (bloodType.equals("null")) {
        statement.setNull(3, Types.VARCHAR);
      } else {
        // if the blood type is valid, then add it to the statement
        if (UserValidator.validateBloodType(bloodType)) {
          statement.setString(3, bloodType);
        } else {
          throw new DataFormatException("Invalid blood type.");
        }
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Adds the new blood pressure to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the blood pressure is invalid or missing.
   */
  private void addBloodPressure(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String bloodPressure;
    // extract the string
    try {
      bloodPressure = updates.get(BLOOD_PRESSURE).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Blood Pressure not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (bloodPressure.equals("null")) {
        statement.setNull(4, Types.VARCHAR);
      } else {
        // if the blood pressure is valid, then add it to the statement
        if (UserValidator.validateBloodPressure(bloodPressure)) {
          statement.setString(4, bloodPressure);
        } else {
          throw new DataFormatException("Invalid blood pressure.");
        }
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Adds the new smoker status to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the smoker status is invalid or missing.
   */
  private void addSmokerStatus(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String smokerStr;
    // extract the string
    try {
      smokerStr = updates.get(SMOKER).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Smoker status not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (smokerStr.equals("null")) {
        statement.setNull(5, Types.BOOLEAN);
      } else {
        // if the smoker boolean is valid, then add it to the statement
        boolean smoker = Boolean.parseBoolean(smokerStr);
        statement.setBoolean(5, smoker);
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Adds the new alcohol consumption to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the alcohol consumption is invalid or missing.
   */
  private void addAlcoholConsumption(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String alcoholConsumption;
    // extract the string
    try {
      alcoholConsumption = updates.get(ALCOHOL_CONSUMPTION).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("Alcohol Consumption not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (alcoholConsumption.equals("null")) {
        statement.setNull(6, Types.VARCHAR);
      } else {
        // if the alcohol consumption is valid, then add it to the statement
        if (UserValidator.validatePositiveDouble(alcoholConsumption)) {
          statement.setString(6, alcoholConsumption);
        } else {
          throw new DataFormatException("Invalid alcohol consumption.");
        }
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Adds the new BMI flag to the updateUserAttributeCollection statement.
   *
   * @param statement The statement updating the UserAttributeCollection.
   * @param updates The new UserAttributeCollection
   * @throws DataFormatException thrown if the BMI flag is invalid or missing.
   */
  private void addBmiFlag(PreparedStatement statement, Map<String, Object> updates)
      throws DataFormatException {
    String bmiFlagStr;
    // extract the string
    try {
      bmiFlagStr = updates.get(BMI_FLAG).toString();
    } catch (NullPointerException e) {
      throw new DataFormatException("BMI Flag not given.");
    }
    // if the string isn't null, then validate it
    try {
      if (bmiFlagStr.equals("null")) {
        statement.setNull(7, Types.BOOLEAN);
      } else {
        // if the bmi flag is valid, then add it to the statement
        boolean bmiFlag = Boolean.parseBoolean(bmiFlagStr);
        statement.setBoolean(7, bmiFlag);
      }
    } catch (SQLException e) {
      throw new DataFormatException();
    }
  }


  /**
   * Updates a donors procedures
   *
   * @param connection DBC connection
   * @param nhi nhi of the donor
   * @param updates update commands to be done
   * @return HTTP Response
   */
  public ResponseEntity updateUserProcedure(Connection connection, String nhi,
      Map<String, Object> updates, int version, String token) {
    List<String> organs = new ArrayList<>();
    String sql = "UPDATE `MedicalProcedures` SET";
    String oldSummary = "";
    try {
      oldSummary = updates.get("oldSummary").toString();
      updates.remove("oldSummary");
    } catch (NullPointerException e) {
      return new ResponseEntity<>("Summary not given", HttpStatus.BAD_REQUEST);
    }
    for (Entry<String, Object> command : updates.entrySet()) {
      if (command.getKey().equals("affectedOrgans")) {
        organs = (ArrayList) command.getValue();
      } else {
          String key = "";
          if(command.getKey().equals("date")) {
              if (command.getValue() != null) {
                ArrayList dateList = (ArrayList) command.getValue();
                key = String.format("%s-%s-%s", dateList.get(0), dateList.get(1), dateList.get(2));
              }
          } else {
              key = command.getValue().toString();
          }
          if (command.getValue() != null) {
            sql += String.format(" %s=\"%s\",", command.getKey(), key);
          } else {
            sql += String.format(" %s=NULL,", command.getKey());
          }
          String newValue;
          if (command.getValue() == null) {
            newValue = "null";
          } else {
            newValue = command.getValue().toString();
          }
        if (!addPatchLogEntry(connection, nhi, "Procedure " + command.getKey(), token,
            newValue)) {
          return new ResponseEntity<>(INTERNAL_SERVER_ERROR_MESSAGE,
              HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\" AND summary=\"%s\"", nhi, oldSummary);
    String date;
    if (updates.get("date") != null) {
      ArrayList dateList = (ArrayList) updates.get("date");
      date = String.format("%s-%s-%s", dateList.get(0), dateList.get(1), dateList.get(2));
    } else {
      date = null;
    }
    if (!organs.isEmpty()) {
      int result = 0;
      try {
        result = updateUserAffectedOrgans(connection, nhi, oldSummary, date, organs);
      } catch (SQLException e) {
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
      if (result == 0) {
        return new ResponseEntity<>("Failed to update Procedure", HttpStatus.BAD_REQUEST);
      }
    }
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update Procedure", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Procedure", HttpStatus.BAD_REQUEST);
    }

  }


  /**
   * Updates a user's death details
   *
   * @param connection Connection to the database
   * @param nhi nhi of the donor to update
   * @param updates update commands to be performed
   * @return HTTP response of the update
   */
  public ResponseEntity updateUserDeathDetails(Connection connection, String nhi,
      Map<String, Object> updates) {
    String sql = "UPDATE `DeathDetails` SET";
    for (Entry<String, Object> command : updates.entrySet()) {
      sql += String.format(" %s=\"%s\",", command.getKey(), command.getValue());
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE ddUsername=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return new ResponseEntity<String>("Updated Death Details", HttpStatus.ACCEPTED);
      } else {
        return new ResponseEntity<String>("Failed to update Death Details", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<String>("Failed to update Death Details", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Updates contact details of donor receiver
   *
   * @param connection DBC Connections
   * @param nhi nhi of the donor
   * @param phone phone number of the contact details
   * @param emergency whether the details are an emergency contact or not
   * @param updates update commands to be done
   * @param version version of the donor
   * @return HTTP response
   */
  public ResponseEntity updateUserContactDetails(Connection connection, String nhi, String phone,
      boolean emergency, Map<String, Object> updates, int version, String token) {
    String emergencyLog;
    if(emergency) emergencyLog = "Emergency ";
    else emergencyLog = "Normal ";
    String sql = "UPDATE `ContactDetails` SET";
    for (Entry<String, Object> command : updates.entrySet()) {
      if (command.getKey().equals("address")) {
        Map<String, Object> address = (Map<String, Object>) command.getValue();
        for (Entry<String, Object> addressCommand : address.entrySet()) {
            String key = "";
            if (addressCommand.getKey().endsWith("Name")) {
              key = addressCommand.getKey().replace("Name", "");
            } else {
              key = addressCommand.getKey();
            }
          sql += String.format(" %s=\"%s\",", key, addressCommand.getValue());
          if (!addPatchLogEntry(connection, nhi, emergencyLog + "Contact Details " + addressCommand.getKey(),
              token, addressCommand.getValue().toString())) {
            return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
                HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
      } else {
        String key;
        if(command.getKey().contains("Num")) {
          key = command.getKey().replace("Num", "Number");
        } else {
          key = command.getKey();
        }
        sql += String.format(" %s=\"%s\",", key, command.getValue());
        if (!addPatchLogEntry(connection, nhi, emergencyLog + "Contact Details " + command.getKey(), token,
            command.getValue().toString())) {
          return new ResponseEntity<String>(INTERNAL_SERVER_ERROR_MESSAGE,
              HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    if (emergency) {
      sql += String
          .format(" WHERE username=\"%s\" AND mobileNumber=\"%s\" AND emergency=%s", nhi, phone,
              "1");
    } else {
      sql += String
          .format(" WHERE username=\"%s\" AND mobileNumber=\"%s\" AND emergency=%s", nhi, phone,
              "0");
    }
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity(HttpStatus.OK);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Contact Details", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Updates a user's basic information
   *
   * @param connection Connection to the database
   * @param nhi Nhi of the donor
   * @param token token of the account modifying
   * @param updates updates to be done
   * @param version version of the user
   * @return Response to the query
   */
  public ResponseEntity updateUserBasicInformation(Connection connection, String nhi, String token,
      Map<String, Object> updates, int version) {
    String sql = "UPDATE `Users` SET";
    for (Entry<String, Object> command : updates.entrySet()) {
      switch (command.getKey()) {
        case "givenName":
          sql += String.format(" firstName=\"%s\",", command.getValue());
          break;
        case "nhi":
          sql += String.format(" username=\"%s\",", command.getValue());
          break;
        default:
          sql += String.format(" %s=\"%s\",", command.getKey(), command.getValue());
          break;
      }
      if (!addPatchLogEntry(connection, nhi, "Basic Information " + command.getKey(), token,
          command.getValue().toString())) {
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update Basic Information",
            HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Basic Information",
          HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Updates a user's profile information
   *
   * @param connection COnnection to the database
   * @param nhi Nhi of the donor
   * @param token Token of the editing account
   * @param updates Updates to be taken
   * @param version version of the donor
   * @return Http response to the query
   */
  public ResponseEntity updateUserProfile(Connection connection, String nhi, String token,
      Map<String, Object> updates, int version) {
    String sql = UPDATE_DONOR_RECEIVER;
    for (Entry<String, Object> command : updates.entrySet()) {
      if (command.getKey().equals("livedInUKFlag")) {
        int result = (boolean) command.getValue() ? 1 : 0;
        sql += String.format(" %s=\"%s\",", command.getKey(), result);
      } else {
        sql += String.format(" %s=\"%s\",", command.getKey(), command.getValue());
      }
      if (command.getValue() == null) {
        command.setValue("null");
      }
      if (!addPatchLogEntry(connection, nhi, "Profile " + command.getKey(), token,
          command.getValue().toString())) {
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    sql = sql.substring(0, sql.length() - 1);
    sql += String.format(" WHERE username=\"%s\"", nhi);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return updateVersion(connection, version, nhi);
      } else {
        return new ResponseEntity<>("Failed to update Profile", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update Profile", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Delete a procedure belonging to a donor
   *
   * @param connection Connection to the database
   * @param nhi Nhi of the donor
   * @param summary Summary of the procedure
   * @return Http response to the query
   */
  public ResponseEntity deleteProcedure(Connection connection, String nhi,
      ProcedureDelete summary) {
    String sql = String
        .format("DELETE FROM `MedicalProcedures` WHERE username=\"%s\" and summary=\"%s\"", nhi,
            summary.getSummary());
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return new ResponseEntity<String>("Deleted Procedure", HttpStatus.OK);
      } else {
        return new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND);
      }
    } catch (SQLException e) {
      return new ResponseEntity<String>("Failed to delete procedure", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Delete an illness belonging to a donor
   *
   * @param connection Connection to the database
   * @param nhi Nhi of the donor
   * @param illness illness to be deleted
   * @return Http response to the query
   */
  public ResponseEntity deleteIllness(Connection connection, String nhi, Illness illness) { String sql = String
        .format("DELETE FROM `Illnesses` WHERE username=\"%s\" and name=\"%s\"", nhi, illness.getName());
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        return new ResponseEntity<>("Deleted Illness", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
      }
    } catch (SQLException e) {
      return new ResponseEntity<>("Failed to delete illness", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Updates the version of a given donor
   *
   * @param connection connection to the database
   * @param version new version of the donor
   * @param nhi nhi of the donor
   * @return Response of the request
   */
  private ResponseEntity updateVersion(Connection connection, int version, String nhi) {
    String failureToUpdate = "Failed to update donor";
    String versionSql = String
        .format("UPDATE `Users` SET version=%s WHERE username=\"%s\"", version, nhi);
    try (PreparedStatement versionUpdate = connection.prepareStatement(versionSql)) {
      int versionChanged = versionUpdate.executeUpdate();
      connection.commit();
      if (versionChanged > 0) {
        return new ResponseEntity<>("Updated donor", HttpStatus.ACCEPTED);
      } else {
        return new ResponseEntity<>(failureToUpdate, HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>(failureToUpdate, HttpStatus.BAD_REQUEST);
    }
  }

  /**
   *
   * @param connection
   * @param nhi
   * @return
   */
    public DeathDetails getDeathDetails(Connection connection, String nhi) {
      String sql = "SELECT * FROM `DeathDetails` WHERE `ddUsername`= ?";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, nhi);
        try (ResultSet resultSet = statement.executeQuery()) {
          if (resultSet.next()) {
            String country = resultSet.getString("country");
            String region = resultSet.getString("region");
            String city = resultSet.getString("city");
            String dateOfdeathStr = resultSet.getString("dateOfDeath");
            LocalDateTime dateOfDeath = LocalDateTime.parse(dateOfdeathStr, formatter);
            return new DeathDetails(city, region, country, dateOfDeath);
          } else {
            return new DeathDetails();
          }

        } catch (SQLException e1) {
          return new DeathDetails();
        }
      } catch (SQLException e1) {
        return new DeathDetails();
      }
    }


    /**
     * Takes a connection and a string, queries the database, collates instances created, creates a single donorReceiver and returns it in a Map
     * @param connection The connection to the database
     * @param nhi The nhi to be searched for in the database
     * @return A Map containing all of the Donor/Receivers found by the search. Should only be one donor returned
     * @throws SQLException An exception if the query fails or there isn't the required column in the table
     */
    public Map<String, DonorReceiver> getDonorReceiverSingle (Connection connection, String nhi) throws SQLException {
        Map<String, DonorReceiver> donorReceivers = new LinkedHashMap<>();
        String sql = String.format("SELECT * FROM DonorReceivers JOIN Users ON Users.username=DonorReceivers.username WHERE Users.username='%s'", nhi);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    String username = resultSet.getString(USERNAME);
                    String givenName = resultSet.getString("firstName");
                    String middleName = resultSet.getString("middleName");
                    String lastName = resultSet.getString("lastName");
                    String createDate = resultSet.getString("creationDate");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
                    LocalDateTime creationDate = LocalDateTime.parse(createDate, formatter);
                    boolean active = resultSet.getBoolean("active");
                    String preferredName = resultSet.getString("preferredName");
                    String title = resultSet.getString(TITLE);
                    formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                    String strDateOfBirth = resultSet.getString(DATE_OF_BIRTH);
                    Character birthGender;
                    Character gender;
                    Boolean livedInUKFlag;
                    try {
                        birthGender = resultSet.getString(BIRTH_GENDER).charAt(0);
                    } catch (NullPointerException e) {
                        birthGender = null;
                    }
                    try {
                        gender = resultSet.getString(GENDER).charAt(0);
                    } catch (NullPointerException e) {
                        gender = null;
                    }
                    try {
                        livedInUKFlag = resultSet.getBoolean("livedInUKFlag");
                    } catch (NullPointerException e) {
                        livedInUKFlag = null;
                    }
                    LocalDate dateOfBirth = LocalDate.parse(strDateOfBirth, formatter);
                    int version = resultSet.getInt("version");

                    ArrayList<Illness> masterIllnessList = getMasterIllnessList(connection, nhi);
                    ReceiverOrganInventory receiverOrganInventory = getReceiverOrganInventory(connection, nhi);
                    DonorOrganInventory donorOrganInventory = getDonorOrganInventory(connection, nhi);
                    ArrayList<MedicalProcedure> medicalProcedures = getMedicalProcedures(connection, nhi);
                    ContactDetails contactDetails = getContactDetails(connection, nhi);
                    ContactDetails emergencyContactDetails = getEmergencyContactDetails(connection, nhi);
                    ArrayList<LogEntry> modifications = getModificationsList(connection, nhi);
                    UserAttributeCollection userAttributeCollection = getUserAttributeCollection(resultSet);
                    Medications medications = getMedications(connection, nhi);

                    DeathDetails deathDetails = getDeathDetails(connection, nhi);
                    boolean receiver = false;
                    for (boolean organ : receiverOrganInventory.getOrgansInList()) {
                        if (organ) {
                            receiver = true;
                        }
                    }
                    DonorReceiver donorReceiver = new DonorReceiver(givenName, middleName, lastName, contactDetails, username, NOT_A_SECURITY_STRING, modifications, creationDate, emergencyContactDetails, medications, title, userAttributeCollection, donorOrganInventory, receiverOrganInventory, preferredName, dateOfBirth, gender, birthGender, creationDate, livedInUKFlag, active, masterIllnessList, medicalProcedures, version, receiver, deathDetails);
                    donorReceivers.put(donorReceiver.getUserName(), donorReceiver);

                }
            }
            return donorReceivers;
    }
  }


  /**
   * Creates a user from the parameters given: nhi, given name, last name, date of birth, password,
   * creating user (can be null)
   *
   * @param connection The connection to the database
   * @param commandList The list of attributes of the new user, retrieved from the api query
   * @throws SQLException An exception thrown if the query to the database is incorrect
   *
   * String nhi String givenName String middleName String lastName String dateOfBirth String
   * password String modifyingAccount
   */
  public int createUser(Connection connection, List<String> commandList) throws SQLException {
    String existsQuery = String
        .format("SELECT `username` FROM Users WHERE `username`='%s'", commandList.get(0));
    try (PreparedStatement preparedStatement = connection.prepareStatement(existsQuery)) {
      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.isBeforeFirst()) {
          return 409;
        }
      }
    }

    String userQuery = String
        .format("INSERT INTO Users (`username`, `active`, `firstName`, `middleName`,"
                + "`lastName`, `password`, `userType`) VALUES ('%s', 1, '%s', '%s', '%s', '%s', 'donor')",
            commandList.get(0), commandList.get(1), commandList.get(2), commandList.get(3),
            commandList.get(5));
    try (PreparedStatement preparedStatement = connection.prepareStatement(userQuery)) {
      preparedStatement.executeQuery();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
    LocalDate dateOfBirth = LocalDate.parse(commandList.get(4), formatter);

    String donorQuery = "INSERT INTO DonorReceivers (`username`, `dateOfBirth`, `activeFlag`) VALUES (?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(donorQuery)) {
      preparedStatement.setString(1, commandList.get(0));
      preparedStatement.setDate(2, java.sql.Date.valueOf(dateOfBirth));
      preparedStatement.setBoolean(3, true);
      preparedStatement.executeQuery();
    }


    String contactDetailsQuery = "INSERT INTO ContactDetails (`mobileNumber`, `username`) VALUES (?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(contactDetailsQuery)) {
      preparedStatement.setString(1, commandList.get(7));
      preparedStatement.setString(2, commandList.get(0));
      preparedStatement.executeQuery();
    }

    String logQuery = String.format("INSERT INTO LogEntries (`username`, `valChanged`, "
            + "`modifyingAccount`, `accountModified`, `originalVal`, `changedVal`) VALUES "
            + "('%s', 'created', '%s', '%s', 'None', 'user created')", commandList.get(0),
        commandList.get(6), commandList.get(0));

    try (PreparedStatement preparedStatement = connection.prepareStatement(logQuery)) {
      preparedStatement.executeQuery();
    }
    return 201;
  }


  /**
   * Checks the type of accounts being passed to the server, returns modification string
   *
   * @param accountString The account that has been sent to the server
   * @return The logging string for the account.
   */
  private String accountStringCreation(String accountString) {
    if (accountString.matches("[a-zA-Z]{3}[0-9]{4}")) {
      return "User: " + accountString;
    } else if (accountString.matches("[0-9]+")) {
      return "Clinician: " + accountString;
    } else {
      return "Administrator: " + accountString;
    }
  }


  /**
   * Parses the given string for the words 'male' or 'female' and returns a char 'M' or 'F'
   * respectively. Otherwise returns 'U' for unknown/unspecified.
   *
   * @return returns a char representing gender, either 'M', 'F', or 0 for missing gender.
   */
  private char getGenderFromString(String string) {
    switch (string.toLowerCase()) {
      case "male":
        return 'M';
      case "female":
        return 'F';
      default:
        return 0;
    }
  }


  /**
   * Extracts a double form the csv file, translating to be a double type object
   *
   * @param value The value to be changed to double
   * @return The double value of the value to be changed (0.0 if error)
   */
  private double extractDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }


  /**
   * Extracts a donor field from a csv file
   *
   * @param record The record of the donor receiver to be extracted
   * @return The result of the extraction.
   */
  private List<Object> extractDonorFields(CSVRecord record) {
    List<Object> returningList = new LinkedList<>();
    try {
      String nhi = record.get("nhi");
      String first = record.get("first_names");
      String last = record.get("last_names");
      LocalDate dateOfBirth = DateFormatter.parseStringToDate(record.get("date_of_birth"));
      LocalDate dateOfDeath = DateFormatter.parseStringToDate(record.get("date_of_death"));

      char gender = getGenderFromString(record.get(GENDER));
      char birthGender = getGenderFromString(record.get("birth_gender"));
      String bloodType = record.get("blood_type");
      double height = extractDouble(record.get(HEIGHT).trim());
      double weight = extractDouble(record.get(WEIGHT).trim());

      String streetNumber = record.get("street_number");
      String streetName = record.get("street_name");

      String streetAddress;

      if (streetNumber.equals("")) {
        streetAddress = streetName;
      } else if (streetName.equals("")) {
        streetAddress = streetNumber;
      } else {
        streetAddress = streetNumber + " " + streetName;
      }

      String suburb = record.get("neighborhood");
      String city = record.get("city");
      String region = record.get("region");
      String postCode = record.get("zip_code");
      String country = record.get("country");
      String mobileNumber = record.get("mobile_number");
      String homeNumber = record.get("home_number");
      String email = record.get("email");

      Address address = new Address(streetAddress, null, suburb, city, region, postCode, country);
      ContactDetails details = new ContactDetails(address, mobileNumber, homeNumber, email);
      DonorReceiver donor = new DonorReceiver(first, null, last, dateOfBirth, nhi, details,
          gender, birthGender, bloodType, height, weight);

      if (dateOfDeath != null) {
        donor.setDateOfDeath(dateOfDeath.atStartOfDay());
      }

      donor.setPassword(NOT_A_SECURITY_STRING);
      returningList.add(0, 201);
      returningList.add(1, donor);
      return returningList;
    } catch (IllegalArgumentException e) {
      returningList.add(0, 404);
      returningList.add(1, "INVALID," + e.getMessage());
      return returningList;
    }
  }


  /**
   * Imports a single donor from a csv file
   *
   * @param connection The connection to the database
   * @param donorReceiver The donor from the csv
   * @return A list with the results of the import
   * @throws SQLException Thrown if there's an error in the sql being executed
   */
  private LinkedList importCSVDonor(Connection connection, DonorReceiver donorReceiver, String modifyingAccount) throws SQLException {
    LinkedList<Object> resultList = new LinkedList<>();
    UserValidator validator = new UserValidator(donorReceiver, new LinkedHashMap<>());
    UserValidationReport userReport = validator.getReport();

    if (donorReceiver.getMiddleName() == null) {
      donorReceiver.setMiddleName("null");
    }

    if ((userReport.getAccountStatus() != UserAccountStatus.INVALID) &&
        (userReport.getAccountStatus() != UserAccountStatus.ERROR) &&
        (userReport.getAccountStatus() != UserAccountStatus.POOR)) {

      if (userReport.getAccountStatus() == UserAccountStatus.REPAIRED) {
        resultList.add(202);
        resultList.add(userReport.getAccountStatus());
        resultList.add(userReport.getIssues());
      } else {
        resultList.add(200);
        resultList.add(userReport.getAccountStatus());
        resultList.add(new LinkedList<>());
      }
    } else {
      resultList.add(404);
      resultList.add(userReport.getAccountStatus());
      resultList.add(userReport.getIssues());
    }
    return resultList;
  }

  private String generateCSVDonorSQL(Connection connection, DonorReceiver donorReceiver, String modifyingAccount) throws SQLException {
    String SQL = "";

    String username = donorReceiver.getUserName();
    String firstName = donorReceiver.getFirstName().replaceAll("'","''");
    String middleName = donorReceiver.getMiddleName().replaceAll("'","''");;
    String lastName = donorReceiver.getLastName().replaceAll("'","''");;
    String password = donorReceiver.getPassword();

    String userQuery = "INSERT INTO Users (`username`, `active`, `firstName`, `middleName`,"
        + "`lastName`, `password`, `userType`) VALUES ('%s', true, '%s', '%s', '%s', '%s', 'donor') ON DUPLICATE KEY "
        + "UPDATE `firstName`=VALUES(`firstName`), `middleName`=VALUES(`middleName`), "
        + "`lastName`=VALUES(`lastName`), `password`=VALUES(`password`);";
    String formattedUserQuery = String.format(userQuery, username ,firstName,
        middleName, lastName, password);
    SQL += formattedUserQuery.replaceAll("'null'","''");

    UserAttributeCollection userAttributeCollection = donorReceiver
        .getUserAttributeCollection();

    String donorQuery =
        "INSERT INTO DonorReceivers (`username`, `dateOfDeath`, `dateOfBirth`, `birthGender`, "
            + "`title`, `gender`, `height`, `weight`, `bloodType`) " +
            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE " +
            "dateOfDeath=VALUES(`dateOfDeath`), dateOfBirth=VALUES(`dateOfBirth`), " +
            "birthGender=VALUES(`birthGender`), title=VALUES(`title`), gender=VALUES(`gender`), " +
            "height=VALUES(`height`), weight=VALUES(`weight`), bloodType=VALUES(`bloodType`);";

    LocalDateTime dateOfDeath = donorReceiver.getDateOfDeath();
    String dateOfDeathString = dateOfDeath == null ? null : dateOfDeath.toString();

    String dateOfBirthString = donorReceiver.getDateOfBirth().toString();
    String birthGenderString = String.valueOf(donorReceiver.getBirthGender());
    String title = donorReceiver.getTitle();
    title = title.equals("unspecified") ? null : title;
    String genderString = String.valueOf(donorReceiver.getGender());
    String heightString = String.valueOf(userAttributeCollection.getHeight());
    String weightString = String.valueOf(userAttributeCollection.getWeight());
    String bloodTypeString = String.valueOf(userAttributeCollection.getBloodType());

    String formattedDonorQuery = String.format(donorQuery, username, dateOfDeathString,
        dateOfBirthString, birthGenderString, title, genderString, heightString,
        weightString, bloodTypeString);
    SQL += formattedDonorQuery;
//    try (PreparedStatement preparedStatement = connection.prepareStatement(donorQuery)) {
//      preparedStatement.setString(1, donorReceiver.getUserName());
//
//      if (donorReceiver.getDateOfDeath() == null) {
//        preparedStatement.setString(2, null);
//      } else {
//        preparedStatement.setString(2, donorReceiver.getDateOfDeath().toString());
//      }
//
//      preparedStatement.setString(3, donorReceiver.getDateOfBirth().toString());
//      preparedStatement.setString(4, String.valueOf(donorReceiver.getBirthGender()));
//      preparedStatement.setString(5, donorReceiver.getTitle());
//      preparedStatement.setString(6, String.valueOf(donorReceiver.getGender()));
//      preparedStatement.setString(7, String.valueOf(userAttributeCollection.getHeight()));
//      preparedStatement.setString(8, String.valueOf(userAttributeCollection.getWeight()));
//      preparedStatement.setString(9, String.valueOf(userAttributeCollection.getBloodType()));
//      // preparedStatement.executeQuery();
//    }


    String contactDetails = "INSERT INTO ContactDetails " +
        "(`mobileNumber`, `username`, `homeNumber`, `email`, `streetAddressLineOne`, " +
        "`streetAddressLineTwo`, `suburb`, `city`, `region`, `postCode`, `countryCode`) VALUES "
        +
        "('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE " +
        "homeNumber=VALUES(`homeNumber`), email=VALUES(`email`), " +
        "streetAddressLineOne=VALUES(`streetAddressLineOne`), " +
        "streetAddressLineTwo=VALUES(`streetAddressLineTwo`), " +
        "suburb=VALUES(`suburb`), city=VALUES(`city`), region=VALUES(`region`), " +
        "postCode=VALUES(`postCode`), countryCode=VALUES(`countryCode`);";
    ContactDetails userContactDetails = donorReceiver.getContactDetails();

    String mobileNumber = userContactDetails.getMobileNum();
    String homeNumber = userContactDetails.getHomeNum();
    String email = userContactDetails.getEmail();
    Address address = userContactDetails.getAddress();
    String streetAddressLineOne = address.getStreetAddressLineOne().replaceAll("'","''");
    String streetAddressLineTwo = address.getStreetAddressLineTwo().replaceAll("'","''");
    String suburb = address.getSuburb().replaceAll("'","''");
    String city = address.getCity().replaceAll("'","''");
    String region = address.getRegion().replaceAll("'","''");
    String postCode = address.getPostCode();
    String countryCode = address.getCountryCode();

    String formattedContactDetailsQuery = String.format(contactDetails, mobileNumber,
        username, homeNumber, email, streetAddressLineOne, streetAddressLineTwo, suburb,
        city, region, postCode, countryCode);
    SQL += formattedContactDetailsQuery;

//    try (PreparedStatement preparedStatement = connection.prepareStatement(contactDetails)) {
//      preparedStatement.setString(1, userContactDetails.getMobileNum());
//      preparedStatement.setString(2, donorReceiver.getUserName());
//      preparedStatement.setString(3, userContactDetails.getHomeNum());
//      preparedStatement.setString(4, userContactDetails.getEmail());
//      preparedStatement.setString(5, userContactDetails.getAddress().getStreetAddressLineOne());
//      preparedStatement.setString(6, userContactDetails.getAddress().getStreetAddressLineTwo());
//      preparedStatement.setString(7, userContactDetails.getAddress().getSuburb());
//      preparedStatement.setString(8, userContactDetails.getAddress().getCity());
//      preparedStatement.setString(9, userContactDetails.getAddress().getRegion());
//      preparedStatement.setString(10, userContactDetails.getAddress().getPostCode());
//      preparedStatement.setString(11, userContactDetails.getAddress().getCountryCode());
//      // preparedStatement.executeQuery();
//    }

    String logEntry =
        "INSERT INTO LogEntries (`username`, `valChanged`, `modifyingAccount`, `accountModified`, `originalVal`, "
            + "`changedVal`) VALUES ('%s', 'imported', '%s', '%s', '', 'imported');";

    String formattedLogEntry = String.format(logEntry, username, modifyingAccount, username);
//    try (PreparedStatement preparedStatement = connection.prepareStatement(logEntry)) {
//      preparedStatement.setString(1, donorReceiver.getUserName());
//      preparedStatement.setString(2, modifyingAccount);
//      preparedStatement.setString(3, donorReceiver.getUserName());
//      // preparedStatement.executeQuery();
//
//      // TODO: Make these output strings and send single query
//    }
    SQL += formattedLogEntry;

    return SQL.replaceAll("'null'","NULL");
  }


  /**
   * Takes the information for importing a user into the database.
   */
  public List importDonor(Connection connection, CSVParser parser, String modifyingUsername) throws SQLException {
    List<Object> results = new LinkedList<>();
    Map<String, UserAccountStatus> successList = new HashMap<>();
    Map<String, List> repairedList = new HashMap<>();
    Map<String, String> failList = new HashMap<>();
    for (CSVRecord record : parser) {
      List<Object> extractionResult = extractDonorFields(record);

      if ((int) extractionResult.get(0) == 404) {
        failList.put(record.get("nhi"), "ERROR: Extraction Failed");
        continue;
      }

      DonorReceiver donorReceiver = (DonorReceiver) extractionResult.get(1);
      LinkedList resultList = importCSVDonor(connection, donorReceiver, modifyingUsername);
      String SQL = generateCSVDonorSQL(connection, donorReceiver, modifyingUsername);

      new Thread(new Runnable() {
        @Override
        public void run() {
          try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.executeQuery();
            System.out.println(donorReceiver.toString());
            System.out.println("==========");
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }).start();

      if ((int) resultList.get(0) == 200) {
        successList.put(record.get("nhi"), (UserAccountStatus) resultList.get(1));
      } else if ((int) resultList.get(0) == 202) {
        repairedList.put(record.get("nhi"), (List) resultList.get(2));
      } else {
        failList.put(record.get("nhi"), resultList.get(1) + ": " + resultList.get(2));
      }
    }
    results.add(successList);
    results.add(repairedList);
    results.add(failList);

    return results;
  }


    /**
     * Adds a medication to medications table.
     *
     * @param connection database connection.
     * @param username username of the user that is taking the medication.
     * @param medication medication object.
     * @return ResponseEntity response.
     */
    public ResponseEntity addUserMedication(Connection connection, String username, String token, Medication medication) {
        String sql = "INSERT INTO `Medications`(`username`, `name`, `isCurrent`) VALUES (?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setString(2, medication.getName());
            statement.setBoolean(3, medication.isCurrent());
            Integer changed = statement.executeUpdate();
            if(changed > 0) {
                String userMakingChanges = getUsername(token);
                String logEntry = MedicationLogger.addMedicationLogEntry(medication, userMakingChanges);
                if (addUserMedicationLog(connection, username, logEntry)){
                    return new ResponseEntity<String>("Successfully added medication.", HttpStatus.ACCEPTED);
                }
            }
            return new ResponseEntity<>("Failed to add medication.", HttpStatus.BAD_REQUEST);

    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to add medication, medication already exists.",
          HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Updates a medication
   *
   * @param connection database connection.
   * @param username username of the user that is taking the medication.
   * @param medication medication object.
   * @return ResponseEntity response.
   */
  public ResponseEntity updateUserMedication(Connection connection, String username, String token,
      Medication medication) {
    String sql = "UPDATE `Medications` SET `isCurrent`= ? WHERE `username`= ? AND `name`= ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setBoolean(1, medication.isCurrent());
      statement.setString(2, username);
      statement.setString(3, medication.getName());
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        String userMakingChanges = getUsername(token);
        String logEntry = MedicationLogger.changeMedicationLogEntry(medication, userMakingChanges);
        if (addUserMedicationLog(connection, username, logEntry)) {
          return new ResponseEntity<>("Updated medication.", HttpStatus.ACCEPTED);
        }
      }
      return new ResponseEntity<>("Failed to update medication.", HttpStatus.BAD_REQUEST);

    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to update medication.", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Gets everything from donor receivers that is required to populate the transplant waiting list.
   *
   * @param connection The connection to the database
   * @return A response identity containing either receiver records or an error message along with
   * the correct status code
   */
  public ResponseEntity getTransplantWaitingList(Connection connection, HashMap<String, String> params) throws SQLException{
    // need to do index and amount
    //" LIMIT %s", " OFFSET %s"
    List<ReceiverRecord> allReceiverRecords = new ArrayList<>();
    String newSql = "SELECT * FROM RequiredOrgans";
    String count = "SELECT COUNT(*) FROM RequiredOrgans";
    String extra = createWhereAndOrderStatement(params);
    newSql += extra;
    count += extra;
    // add amount + index
    if (params.get("amount") != null) {
      newSql += String.format(" LIMIT %d", Integer.parseInt(params.get("amount")));
      if (params.get("index") != null) {
        newSql += String.format(" OFFSET %d", Integer.parseInt(params.get("index")));
      }
    }

    try (PreparedStatement statement = connection.prepareStatement(newSql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String username = resultSet.getString(USERNAME);
          String name = resultSet.getString("name");
          String region = resultSet.getString("region");
          String timeStamp = resultSet.getString("registrationTime");
          String organ = resultSet.getString("organ");
          ReceiverRecord record = new ReceiverRecord(name, username, region, timeStamp, organ);
          allReceiverRecords.add(record);
        }
      }
    }
    // get the count
    int total = 0;
    try (PreparedStatement statement = connection.prepareStatement(count)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        resultSet.next();
        total = resultSet.getInt(1);
      }
    }
    return ResponseEntity.accepted().header("total",String.valueOf(total)).body(allReceiverRecords);
  }

  private String createWhereAndOrderStatement(HashMap<String, String> params) {
    String where = " WHERE";
    boolean whereCalled = false;
    if (params.get("region") != null) {
      whereCalled = true;
      if (params.get("region").equalsIgnoreCase("Hawke's Bay")) {
        where += " region = 'Hawke''s Bay'";
      } else {
        where += " region = '" + params.get("region") + "'";
      }
    }
    if (params.get("receivingOrgan") != null) {
      if (whereCalled) {
        where += " AND";
      }
      whereCalled = true;
      if (params.get("receivingOrgan").equalsIgnoreCase("MiddleEars")){
        where += " organ = '" + "Middle Ears" + "'";
      } else if (params.get("receivingOrgan").equalsIgnoreCase("BoneMarrow")){
        where += " organ = '" + "Bone Marrow" + "'";
      } else if (params.get("receivingOrgan").equalsIgnoreCase("ConnectiveTissue")){
        where += " organ = '" + "Connective Tissue" + "'";
      } else {
        where += " organ = '" + params.get("receivingOrgan") + "'";
      }
    }
    String order = " ORDER BY";
    boolean orderCalled = false;
    if (params.get("sortVariable") != null) {
      orderCalled = true;
      order += " " + params.get("sortVariable");
      if (params.get("isDesc") != null) {
        order += " DESC";
      }
    }
    String returnString = "";
    if (whereCalled) {
      returnString += where;
    }
    if (orderCalled) {
      returnString += order;
    }
    //SELECT * FROM `RequiredOrgans` WHERE organ = "heart" ORDER BY registrationTime LIMIT 2
    //region
    //receivingOrgan
    //sortVariable
    //isDesc
    return returnString;
  }


  /**
   * Creates the full name of a receiver
   *
   * @param givenName The first name of the receiver
   * @param middleName The middle name of the receiver
   * @param lastName The last name of the receiver
   * @return A string with the users full name
   */
  private String createFullName(String givenName, String middleName, String lastName) {
    if (middleName == null || middleName.equals("")) {
      if (lastName == null || lastName.equals("")) {
        return givenName;
      } else {
        return givenName + " " + lastName;
      }
    } else {
      return givenName + " " + middleName + " " + lastName;
    }
  }


  /**
   * deletes a medication
   *
   * @param connection database connection.
   * @param username username of the user that is taking the medication.
   * @param medication medication to delete.
   * @return ResponseEntity response.
   */
  public ResponseEntity deleteUserMedication(Connection connection, String username, String token,
      Medication medication) {
    String sql = "DELETE FROM `Medications` WHERE `username`= ? AND `name`= ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);
      statement.setString(2, medication.getName());
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
        String userMakingChanges = getUsername(token);
        String logEntry = MedicationLogger.removeMedicationLogEntry(medication, userMakingChanges);
        if (addUserMedicationLog(connection, username, logEntry)) {
          return new ResponseEntity<>("Successfully deleted medication.", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Failed to delete medication.", HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>("Failed to delete medication.", HttpStatus.BAD_REQUEST);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to delete medication.", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Add a log entry to medication log.
   *
   * @param connection Database connection.
   * @param username Username that the log applies to.
   * @param logEntry the log entry
   */
  private boolean addUserMedicationLog(Connection connection, String username, String logEntry) {
    String sql = "INSERT INTO `MedicationLogs`(`username`, `medicationLog`) VALUES (?,?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);
      statement.setString(2, logEntry);
      int changed = statement.executeUpdate();
      connection.commit();
      return (changed > 0);
    } catch (SQLException e1) {
      return false;
    }
  }


  /**
   * Adds a log entry to the database
   *
   * @param connection Connection to the database
   * @param username Username of the account being modified
   * @param valChanged Value that has been changed
   * @param token Token of the modifying account
   * @param changedVal The updated value
   * @return Whether the log entry was successfully stored on the database
   */
  private boolean addPatchLogEntry(Connection connection, String username, String valChanged,
      String token, String changedVal) {
    String modifyingAccount = getUsername(token);
    LocalDateTime now = LocalDateTime.now();
    String sql = String.format(
        "INSERT INTO `LogEntries`(`username`, `valChanged`, `changeTime`, `modifyingAccount`, `accountModified`, `originalVal`, `changedVal`) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
        username, valChanged, now, modifyingAccount, username, "", changedVal);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      return changed > 0;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * Adds a log entry to the database
   *
   * @param connection Connection to the database
   * @param username Username of the account being modified
   * @param valChanged Value that has been changed
   * @param token Token of the modifying account
   * @param originalVal The original value of the attribute being updated.
   * @param changedVal The updated value
   * @return Whether the log entry was successfully stored on the database
   */
  private boolean addPatchLogEntryWithOrigVal(Connection connection, String username,
      String valChanged, String token, String originalVal, String changedVal) {
    String modifyingAccount = getUsername(token);
    LocalDateTime now = LocalDateTime.now();

    String sql = String.format(
        "INSERT INTO `LogEntries`(`username`, `valChanged`, `changeTime`, `modifyingAccount`, `accountModified`, `originalVal`, `changedVal`) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
        username, valChanged, now, modifyingAccount, username, originalVal, changedVal);
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int changed = statement.executeUpdate();
      connection.commit();
      return changed > 0;
    } catch (SQLException e) {
      return false;
    }
  }


  /**
   * Adds user death details to the the database.
   *
   * @param connection Connection to the database.
   * @param username Username of the user that is deceased.
   * @param deathDetails Death details object containing the date and location of death.
   * @return ResponseEntity
   */
  public ResponseEntity addUserDeathDetails(Connection connection, String username,
                                             DeathDetails deathDetails) {
    String sql = "INSERT INTO `DeathDetails`(`dateOfDeath`, `country`, `region`, `city`, `ddUsername`) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setTimestamp(1, Timestamp.valueOf(deathDetails.getDoD()));
      statement.setString(2, deathDetails.getCountry());
      statement.setString(3, deathDetails.getRegion());
      statement.setString(4, deathDetails.getCity());
      statement.setString(5, username);
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
          return new ResponseEntity<>("Successfully added death details.", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Failed to add death details.", HttpStatus.CONFLICT);
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to add death details.", HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Removes user death details from the database
   *
   * @param connection Connection to the database.
   * @param username Username of the user that is deceased.
   * @return ResponseEntity
   */
  public ResponseEntity removeUserDeathDetails(Connection connection, String username) {
    String sql = "DELETE FROM `DeathDetails` WHERE `ddUsername`= ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);
      int changed = statement.executeUpdate();
      connection.commit();
      if (changed > 0) {
          return new ResponseEntity<>("Successfully removed death details.", HttpStatus.ACCEPTED);
        } else {
        return new ResponseEntity<>("Failed to remove death details.", HttpStatus.NOT_FOUND);
      }
    } catch (SQLException e1) {
      return new ResponseEntity<>("Failed to remove death details.", HttpStatus.BAD_REQUEST);
    }
  }
}

