package seng302.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import seng302.model.person.User;
import seng302.model.person.UserValidator;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.statistics.*;

public class DataService extends BaseService {

  private static PreparedStatement preparedStatement;

  public DataService(AuthenticationTokenStore authenticationTokenStore) {
    super(authenticationTokenStore);
  }

  private static final String[] RECEIVER_ORGAN_DATA_QUERIES = {
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rBone`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rLiver`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rKidneys`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rPancreas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rHeart`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rLungs`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rIntestine`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rCorneas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rMiddleEars`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rSkin`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rBoneMarrow`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `rConnectiveTissue`=TRUE"
  };

  private static final String[] DONOR_ORGAN_DATA_QUERIES = {
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dBone`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dLiver`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dKidneys`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dPancreas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dHeart`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dLungs`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dIntestine`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dCorneas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dMiddleEars`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dSkin`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dBoneMarrow`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers d WHERE `dConnectiveTissue`=TRUE"
  };

  private static final String QUERY_STRING_GENDER_REGION =
      "SELECT COUNT(DISTINCT d.username) AS Count FROM DonorReceivers d INNER JOIN ContactDetails c";

  private static final String[] RECEIVER_ORGAN_DATA_QUERIES_REGION = {
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rBone`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rLiver`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rKidneys`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rPancreas`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rHeart`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rLungs`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rIntestine`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rCorneas`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rMiddleEars`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rSkin`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rBoneMarrow`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rConnectiveTissue`=TRUE AND c.emergency=0 AND `region`=?"
  };

  private static final String[] DONOR_ORGAN_DATA_QUERIES_REGION = {
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dBone`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dLiver`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dKidneys`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dPancreas`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dHeart`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dLungs`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dIntestine`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dCorneas`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dMiddleEars`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dSkin`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dBoneMarrow`=TRUE AND c.emergency=0 AND `region`=?",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dConnectiveTissue`=TRUE AND c.emergency=0 AND `region`=?"
  };

  private static final String[] RECEIVER_DATA_QUERIES = {
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rBone`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rLiver`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rKidneys`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rPancreas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rHeart`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rLungs`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rIntestine`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rCorneas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rMiddleEars`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rSkin`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rBoneMarrow`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rConnectiveTissue`=TRUE"
  };

  private static final String[] DONOR_DATA_QUERIES = {
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rBone`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rLiver`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rKidneys`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rPancreas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rHeart`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rLungs`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rIntestine`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rCorneas`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rMiddleEars`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rSkin`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rBoneMarrow`=TRUE",
      "SELECT COUNT(`username`) AS Count FROM DonorReceivers WHERE `rConnectiveTissue`=TRUE"
  };


  private static final String[] RECEIVER_DATA_QUERIES_REGION = {
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rBone`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rLiver`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rKidneys`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rPancreas`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rHeart`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rLungs`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rIntestine`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rCorneas`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rMiddleEars`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rSkin`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rBoneMarrow`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `rConnectiveTissue`=TRUE AND c.emergency=0"
  };

  private static final String[] DONOR_DATA_QUERIES_REGION = {
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dBone`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dLiver`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dKidneys`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dPancreas`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dHeart`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dLungs`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dIntestine`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dCorneas`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dMiddleEars`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dSkin`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dBoneMarrow`=TRUE AND c.emergency=0",
      QUERY_STRING_GENDER_REGION + " ON d.username = c.username WHERE `dConnectiveTissue`=TRUE AND c.emergency=0"
  };

  private static final String DONOR_STRING = "donors";
  private static final String COUNT_STRING = "Count";


  /**
   * Gets the number of needed organs of each type
   * @param connection The connection to the database
   * @param gender The gender of the
   * @return A JSONObject with the number of each type of organ needed
   * @throws SQLException Thrown when the SQL for the statement is wrong
   */
  public static OrganCount getReceiverDataByOrgans(String userType, Connection connection, String gender,
      String region, String bloodType, String age) throws SQLException {

    String[] queryList = RECEIVER_ORGAN_DATA_QUERIES;

    if (userType.equals(DONOR_STRING)) {
      queryList = DONOR_ORGAN_DATA_QUERIES;
    }

    boolean[] paramsPresent = new boolean[4];
    Arrays.fill(paramsPresent, false);

    if (region != null) {
      if (userType.equals(DONOR_STRING)) {
        queryList = DONOR_ORGAN_DATA_QUERIES_REGION;
      } else {
        queryList = RECEIVER_ORGAN_DATA_QUERIES_REGION;
      }
      paramsPresent[0] = true;
    }

    if (gender != null && gender.length() == 1 && UserValidator.validateGender(gender.charAt(0))) {
      paramsPresent[1] = true;
    }
    if (bloodType != null && UserValidator.validateBloodType(bloodType)) {
      paramsPresent[2] = true;
    }
    if (age != null && age.matches("[0-9]+")) {
      paramsPresent[3] = true;
    }

    ArrayList<Integer> counts = new ArrayList<>();

    for (String aQueryList : queryList) {
      String query = prepareQuery(aQueryList, paramsPresent[1], paramsPresent[2], paramsPresent[3]);
      preparedStatement = connection.prepareStatement(query);
      prepareStatement(paramsPresent, region, gender, bloodType, age);
      try (ResultSet object = preparedStatement.executeQuery()) {
        int column = object.findColumn(COUNT_STRING);
        object.next();
        counts.add(object.getInt(column));
      }
    }

    OrganCount organCount = new OrganCount();
    organCount.setBoneCount(counts.get(0));
    organCount.setLiverCount(counts.get(1));
    organCount.setKidneyCount(counts.get(2));
    organCount.setPancreasCount(counts.get(3));
    organCount.setHeartCount(counts.get(4));
    organCount.setLungCount(counts.get(5));
    organCount.setIntestineCount(counts.get(6));
    organCount.setCorneasCount(counts.get(7));
    organCount.setMiddleEarsCount(counts.get(8));
    organCount.setSkinCount(counts.get(9));
    organCount.setBoneMarrowCount(counts.get(10));
    organCount.setConnectiveTissueCount(counts.get(11));

    return organCount;
  }


  /**
   * Prepares the database query for getting the different kinds of organs
   * @param paramsPresent A boolean list of the parameters that are being filtered by
   * @param region The region selected by the filter
   * @param gender The gender selected by the filter
   * @param bloodType The blood type selected by the filter
   * @param age The lower bound of the age range being looked for
   * @throws SQLException An SQL Exception thrown if the SQL is wrong
   */
  private static void prepareStatement(boolean[] paramsPresent, String region, String gender,
      String bloodType, String age) throws SQLException {
    int i = 1;
    if (paramsPresent[0]) {
      preparedStatement.setString(i, region);
      i++;
    }
    if (paramsPresent[1]) {
      preparedStatement.setString(i, gender);
      i++;
    }
    if (paramsPresent[2]) {
      preparedStatement.setString(i, String.valueOf(bloodType));
      i++;
    }
    if (paramsPresent[3]) {
      //assuming the age is the lower bound -> can either be enforced client side or server side
      preparedStatement.setInt(i, Integer.parseInt(age));
      i++;
      int upperBound = getAgeUpperBound(age);
      preparedStatement.setInt(i, upperBound);
    }
  }


  /**
   * Constructs the upper limits map and retrieves the upper limit for the given age range
   * @param age The lower bound of the age range
   * @return The upper bound of the age range
   */
  private static int getAgeUpperBound(String age) {
    Map<String, Integer> upperLimits = new HashMap<>();
    upperLimits.put("0", 12);
    upperLimits.put("12", 20);
    upperLimits.put("20", 30);
    upperLimits.put("30", 40);
    upperLimits.put("40", 50);
    upperLimits.put("50", 60);
    upperLimits.put("60", 70);
    upperLimits.put("70", 80);
    upperLimits.put("80", 90);
    upperLimits.put("90", 150);
    return upperLimits.get(age);
  }


  /**
   * Adds more to the query for gathering all the data from the database
   * @param query The database query fields
   * @param genderPresent If the gender value is valid
   * @param bloodTypePresent If the blood type is valid
   * @param agePresent If the age is valid
   * @return The fully formed query list
   */
  private static String prepareQuery(String query, boolean genderPresent, boolean bloodTypePresent, boolean agePresent) {
    if (genderPresent) {
      query += " AND `gender`=?";
    }
    if (bloodTypePresent) {
      query += " AND `bloodType`=?";
    }
    if (agePresent) {
      query += " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= ? "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < ?";
    }
    query += ";";
    return query;
  }


  public static GenderCount getDataGender(String userType, Connection connection, String region, String bloodType, String age) throws SQLException {
    GenderCount genderCount = new GenderCount();

    String endOfQuery = getEndOfQuery(null, region, bloodType, age);
    String[] beginningOfQuery;

    if (userType.equals(DONOR_STRING)) {
      beginningOfQuery = DONOR_DATA_QUERIES;
      if (region != null) {
        beginningOfQuery = DONOR_DATA_QUERIES_REGION;
      }
    } else {
      beginningOfQuery = RECEIVER_DATA_QUERIES;
      if (region != null) {
        beginningOfQuery = RECEIVER_DATA_QUERIES_REGION;
      }
    }

    for (String query : beginningOfQuery) {
      String maleQuery = query + endOfQuery + " AND `gender`='M';";
      preparedStatement = connection.prepareStatement(maleQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.maleCount += resultSet.getInt(column);
      }

      String femaleQuery = query + endOfQuery + " AND `gender`='F';";
      preparedStatement = connection.prepareStatement(femaleQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.femaleCount += resultSet.getInt(column);
      }

      String otherQuery = query + endOfQuery + " AND `gender`='O';";
      preparedStatement = connection.prepareStatement(otherQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.otherCount += resultSet.getInt(column);
      }

      String unspecifiedQuery = query + endOfQuery + " AND `gender`='U';";
      preparedStatement = connection.prepareStatement(unspecifiedQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.unspecifiedCount += resultSet.getInt(column);
      }

      String nullQuery = query + endOfQuery + " AND `gender`=NULL;";
      preparedStatement = connection.prepareStatement(nullQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.unspecifiedCount += resultSet.getInt(column);
      }

      String emptyQuery = query + endOfQuery + " AND `gender`='';";
      preparedStatement = connection.prepareStatement(emptyQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        genderCount.unspecifiedCount += resultSet.getInt(column);
      }
    }
    return genderCount;
  }


  private static String getEndOfQuery(String gender, String region, String bloodType, String age) {
    String queryString = "";
    if (gender != null && gender.length() == 1 && UserValidator.validateGender(gender.charAt(0))) {
      queryString += " AND `gender`='" + gender + "'";
    }
    if (region != null) {
      queryString += " AND `region`='" + region + "'";
    }
    if (bloodType != null && UserValidator.validateBloodType(bloodType)) {
      queryString += " AND `bloodType`='" + bloodType + "'";
    }
    if (age != null && age.matches("[1-9]+[0-9]*")) {
      queryString += " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= " + age + " "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < " + getAgeUpperBound(age);
    }
    return queryString;
  }


  public static RegionCount getReceiverDataRegion(String type, Connection connection, String gender, String bloodType, String age) throws SQLException {
    RegionCount regionCount = new RegionCount();
    String endOfQuery = getEndOfQuery(gender, null, bloodType, age);

    String[] dataSet = RECEIVER_DATA_QUERIES_REGION;

    if (type.equals(DONOR_STRING)) {
      dataSet = DONOR_DATA_QUERIES_REGION;
    }

    for (String query : dataSet) {
      String currentQuery;
      currentQuery = query + endOfQuery + " AND `region`='Northland';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.northlandCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Auckland';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.aucklandCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Waikato';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.waikatoCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Bay Of Plenty';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.bayOfPlentyCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Gisborne';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.gisborneCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Hawke''s Bay';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.hawkesBayCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Taranaki';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.taranakiCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Manawatu-Wanganui';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.manawatuWanganuiCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Wellington';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.wellingtonCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Tasman';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.tasminCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Nelson';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.nelsonCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Marlborough';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.marlboroughCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='West Coast';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.westCoastCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Canterbury';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.canterburyCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Otago';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.otagoCount += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND `region`='Southland';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.southlandCount += resultSet.getInt(column);
      }
      currentQuery = query + endOfQuery + " AND `region`='Chatham Islands';";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        regionCount.chathamIslandsCount += resultSet.getInt(column);
      }
    }
    return regionCount;
  }



  public static BloodTypeCount getReceiverBlood(Connection connection, String gender, String region, String age) throws SQLException {
    BloodTypeCount bloodTypeCount = new BloodTypeCount();

    String endOfQuery = getEndOfQuery(gender, region, null, age);
    String[] beginningOfQuery = RECEIVER_DATA_QUERIES;
    if(region != null) {
      beginningOfQuery = RECEIVER_DATA_QUERIES_REGION;
    }
    for (String query : beginningOfQuery) {
      String aPosQuery = query + endOfQuery + " AND `bloodType`='A+';";
      preparedStatement = connection.prepareStatement(aPosQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.aPos += resultSet.getInt(column);
      }

      String aNegQuery = query + endOfQuery + " AND `bloodType`='A-';";
      preparedStatement = connection.prepareStatement(aNegQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.aNeg += resultSet.getInt(column);
      }

      String aBPosQuery = query + endOfQuery + " AND `bloodType`='AB+';";
      preparedStatement = connection.prepareStatement(aBPosQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.aBPos += resultSet.getInt(column);
      }

      String aBNegQuery = query + endOfQuery + " AND `bloodType`='AB-';";
      preparedStatement = connection.prepareStatement(aBNegQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.aBNeg += resultSet.getInt(column);
      }

      String bPosQuery = query + endOfQuery + " AND `bloodType`='B+';";
      preparedStatement = connection.prepareStatement(bPosQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.bPos += resultSet.getInt(column);
      }

      String bNegQuery = query + endOfQuery + " AND `bloodType`='B-';";
      preparedStatement = connection.prepareStatement(bNegQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.bNeg += resultSet.getInt(column);
      }


      String oPosQuery = query + endOfQuery + " AND `bloodType`='O+';";
      preparedStatement = connection.prepareStatement(oPosQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.oPos += resultSet.getInt(column);
      }


      String oNegQuery = query + endOfQuery + " AND `bloodType`='O-';";
      preparedStatement = connection.prepareStatement(oNegQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        bloodTypeCount.oNeg += resultSet.getInt(column);
      }
    }
    return bloodTypeCount;
  }

  public static List<Integer> getTransplantWaitingListOrgansForYear(Connection connection, String bloodType, String gender, String age, String region,
      String year, List<String> organs) throws SQLException{
    List<Integer> numOrgansMonths = new ArrayList<>();
    for(int i = 1; i < 12; i++) { // Iterate for each month
      String monthString1 = String.valueOf(i);
      String monthString2 = String.valueOf(i+1);
      String lowerBound = year + "-" + pad_zero(monthString1,2) + "-01";
      String upperBound = year + "-" + pad_zero(monthString2, 2) + "-01";
      int totalOrgans = getTotalTransplantWaitingListCount(connection,bloodType,gender,age,region, upperBound,lowerBound, organs);
      numOrgansMonths.add(totalOrgans);
    }
    // Handle December case separately
    String lowerBound = year + "-12-01";
    int nextYear = Integer.valueOf(year) + 1;
    String upperBound = nextYear + "-01-01";
    int totalOrgans = getTotalTransplantWaitingListCount(connection,bloodType,gender,age,region, upperBound,lowerBound, organs);
    numOrgansMonths.add(totalOrgans);

    return numOrgansMonths;
  }

  public static int getTotalTransplantWaitingListCount(Connection connection, String bloodType, String gender, String age, String region, String upperBound, String lowerBound, List<String> organs) throws SQLException{
    String query = "SELECT COUNT(DonorReceivers.username) FROM DonorReceivers INNER JOIN RequiredOrgans ON RequiredOrgans.username = DonorReceivers.username WHERE 1=1";

    if(bloodType != null) {
      query += " AND bloodType = \"" + bloodType + "\"";
    }
    if(gender != null) {
      query += " AND gender = \"" + gender + "\"";
    }
    if(region != null) {
      query += " AND region = \"" + region + "\"";
    }
    if(age != null) {
      query += " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= " + age
          + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < " + getAgeUpperBound(age);
    }

    if(organs.size() > 0) {
      query += " AND (1 = 2 ";
    }

    if(organs.contains("lungs")) {
      query += " OR (rLungs =1 AND rTimeLungs >= \"" + lowerBound + "\" AND rTimeLungs <= \"" + upperBound + "\")";
    }
    if(organs.contains("heart")) {
      query += " OR (rHeart =1 AND rTimeHeart >= \"" + lowerBound + "\" AND rTimeHeart <= \"" + upperBound + "\")";
    }
    if(organs.contains("liver")) {
      query += " OR (rLiver =1 AND rTimeLiver >= \"" + lowerBound + "\" AND rTimeLiver <= \"" + upperBound + "\")";
    }
    if(organs.contains("kidneys")) {
      query += " OR (rKidneys =1 AND rTimeKidneys >= \"" + lowerBound + "\" AND rTimeKidneys <= \"" + upperBound + "\")";
    }
    if(organs.contains("pancreas")) {
      query += " OR (rPancreas =1 AND rTimePancreas >= \"" + lowerBound + "\" AND rTimePancreas <= \"" + upperBound + "\")";
    }
    if(organs.contains("intestine")) {
      query += " OR (rIntestine =1 AND rTimeIntestine >= \"" + lowerBound + "\" AND rTimeIntestine <= \"" + upperBound + "\")";
    }
    if(organs.contains("corneas")) {
      query += " OR (rCorneas =1 AND rTimeCorneas >= \"" + lowerBound + "\" AND rTimeCorneas <= \"" + upperBound + "\")";
    }
    if(organs.contains("middleEars")) {
      query += " OR (rMiddleEars =1 AND rTimeMiddleEars >= \"" + lowerBound + "\" AND rTimeMiddleEars <= \"" + upperBound  + "\")";
    }
    if(organs.contains("skin")) {
      query += " OR (rSkin =1 AND rTimeSkin >= \"" + lowerBound + "\" AND rTimeSkin <= \"" + upperBound + "\")";
    }
    if(organs.contains("bone")) {
      query += " OR (rBone =1 AND rTimeBone >= \"" + lowerBound + "\" AND rTimeBone <= \"" + upperBound + "\")";
    }
    if(organs.contains("boneMarrow")) {
      query += " OR (rBoneMarrow =1 AND rTimeBoneMarrow >= \"" + lowerBound + "\" AND rTimeBoneMarrow <= \"" + upperBound + "\")";
    }
    if(organs.contains("connectiveTissue")) {
      query += " OR (rConnectiveTissue =1 AND rTimeConnectiveTissue >= \"" + lowerBound + "\" AND rTimeConnectiveTissue <= \"" + upperBound + "\")";
    }

    if(organs.size() > 0) {
      query += ")";
    }

    System.out.println(query);
    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet result = statement.executeQuery();
    result.next();
    return result.getInt(1); // COUNT
  }


  public static OrganCount getTransplantWaitingListTime(Connection connection, String bloodType,
      String gender, String age, String region, String upperBound, String lowerBound) throws SQLException{

    OrganCount organCount = new OrganCount();
    String query = "SELECT COUNT(DonorReceivers.username) FROM DonorReceivers INNER JOIN ContactDetails ON DonorReceivers.username = ContactDetails.username WHERE ";

    if(bloodType != null) {
      query += " bloodType = \"" + bloodType + "\" AND ";
    }
    if(gender != null) {
      query += " gender = \"" + gender + "\" AND ";
    }
    if(region != null) {
      query += " region = \"" + region + "\" AND emergency = 0 AND ";
    }
    if(age != null) {
      query += " FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= " + age
          + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < " + getAgeUpperBound(age) + " AND ";
    }
    try (PreparedStatement lungStatement = connection.prepareStatement(query + "rLungs =1 AND rTimeLungs >= ? AND rTimeLungs <= ?")) {
      lungStatement.setString(1, lowerBound);
      lungStatement.setString(2, upperBound);
      try (ResultSet result = lungStatement.executeQuery()) {
        result.next();
        int lungCount = result.getInt(1);
        organCount.setLungCount(lungCount);
      }
    }
    try (PreparedStatement liverStatement = connection.prepareStatement(query + "rLiver=1 AND rTimeLiver >= ? AND rTimeLiver <= ?")) {
      liverStatement.setString(1,lowerBound);
      liverStatement.setString(2,upperBound);
      try(ResultSet result = liverStatement.executeQuery()) {
        result.next();
        int liverCount = result.getInt(1);
        organCount.setLiverCount(liverCount);
      }
    }
    try (PreparedStatement kidneysStatement = connection.prepareStatement(query + "rKidneys=1 AND rTimeKidneys>= ? AND rTimeKidneys <= ?")) {
      kidneysStatement.setString(1,lowerBound);
      kidneysStatement.setString(2,upperBound);
      try (ResultSet result = kidneysStatement.executeQuery()) {
        result.next();
        int kidneysCount = result.getInt(1);
        organCount.setKidneyCount(kidneysCount);
      }
    }
    try (PreparedStatement pancreasStatement = connection.prepareStatement(query + "rPancreas=1 AND rTimePancreas >= ? AND rTimePancreas <= ?")) {
      pancreasStatement.setString(1,lowerBound);
      pancreasStatement.setString(2,upperBound);
      try (ResultSet result = pancreasStatement.executeQuery()) {
        result.next();
        int pancreasCount = result.getInt(1);
        organCount.setPancreasCount(pancreasCount);
      }
    }
    try (PreparedStatement heartStatement = connection.prepareStatement(query + "rHeart=1 AND rTimeHeart >= ? AND rTimeHeart <= ?")) {
      heartStatement.setString(1,lowerBound);
      heartStatement.setString(2,upperBound);
      try (ResultSet result = heartStatement.executeQuery()) {
        result.next();
        int heartCount = result.getInt(1);
        organCount.setHeartCount(heartCount);
      }
    }
    try (PreparedStatement intestineStatement = connection.prepareStatement(query + "rIntestine=1 AND rTimeIntestine >= ? AND rTimeIntestine <= ?")) {
      intestineStatement.setString(1,lowerBound);
      intestineStatement.setString(2,upperBound);
      try (ResultSet result = intestineStatement.executeQuery()) {
        result.next();
        int intestineCount = result.getInt(1);
        organCount.setIntestineCount(intestineCount);
      }
    }
    try (PreparedStatement corneasStatement = connection.prepareStatement(query + "rCorneas=1 AND rTimeCorneas >= ? AND rTimeCorneas <= ?")) {
      corneasStatement.setString(1,lowerBound);
      corneasStatement.setString(2,upperBound);
      try(ResultSet result = corneasStatement.executeQuery()) {
        result.next();
        int corneasCount = result.getInt(1);
        organCount.setCorneasCount(corneasCount);
      }
    }
    try (PreparedStatement middleEarsStatement = connection.prepareStatement(query + "rMiddleEars=1 AND rTimeMiddleEars >= ? AND rTimeMiddleEars <= ?")) {
      middleEarsStatement.setString(1,lowerBound);
      middleEarsStatement.setString(2,upperBound);
      try(ResultSet result = middleEarsStatement.executeQuery()) {
        result.next();
        int middleEarsCount = result.getInt(1);
        organCount.setMiddleEarsCount(middleEarsCount);
      }
    }
    try (PreparedStatement skinStatement = connection.prepareStatement(query + "rSkin=1 AND rTimeSkin >= ? AND rTimeSkin <= ?")) {
      skinStatement.setString(1,lowerBound);
      skinStatement.setString(2,upperBound);
      try(ResultSet result = skinStatement.executeQuery()) {
        result.next();
        int skinCount = result.getInt(1);
        organCount.setSkinCount(skinCount);
      }
    }
    try (PreparedStatement boneStatement = connection.prepareStatement(query + "rBone=1 AND rTimeBone >= ? AND rTimeBone <= ?")) {
      boneStatement.setString(1,lowerBound);
      boneStatement.setString(2,upperBound);
      try (ResultSet result = boneStatement.executeQuery()) {
        result.next();
        int boneCount = result.getInt(1);
        organCount.setBoneCount(boneCount);
      }
    }
    try (PreparedStatement boneMarrowStatement = connection.prepareStatement(query + "rBoneMarrow=1 AND rTimeBoneMarrow >= ? AND rTimeBoneMarrow <= ?")) {
      boneMarrowStatement.setString(1,lowerBound);
      boneMarrowStatement.setString(2,upperBound);
      try(ResultSet result = boneMarrowStatement.executeQuery()) {
        result.next();
        int boneMarrowCount = result.getInt(1);
        organCount.setBoneMarrowCount(boneMarrowCount);
      }
    }
    try (PreparedStatement connectiveTissueStatement = connection.prepareStatement(query  + "rConnectiveTissue=1 AND rTimeConnectiveTissue >= ? AND rTimeConnectiveTissue <= ?")) {
      connectiveTissueStatement.setString(1,lowerBound);
      connectiveTissueStatement.setString(2,upperBound);
      try (ResultSet result = connectiveTissueStatement.executeQuery()) {
        result.next();
        int connectiveTissueCount = result.getInt(1);
        organCount.setConnectiveTissueCount(connectiveTissueCount);
      }
    }

    return organCount;
  }

  private static String pad_zero(String currentString, int length) {
    while(currentString.length() < length) {
      currentString = "0"  + currentString;
    }
    return currentString;
  }

  public static Map<String,OrganCount> getTransplantWaitingListForYear(Connection connection, String bloodType,
      String gender, String age, String region, String year) throws SQLException{
    Map<String, OrganCount> organCounts = new LinkedHashMap<>();
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September"
    , "October", "November", "December"};
    for(int i = 1; i < 12; i++) { // Iterate for each month
      String monthString1 = String.valueOf(i);
      String monthString2 = String.valueOf(i+1);
      String lowerBound = year + "-" + pad_zero(monthString1,2) + "-01";
      String upperBound = year + "-" + pad_zero(monthString2, 2) + "-01";
      OrganCount currentCount = getTransplantWaitingListTime(connection,bloodType,gender,age,region, upperBound,lowerBound);
      organCounts.put(months[i-1], currentCount);
    }
    // Handle December case separately
    String lowerBound = year + "-12-01";
    int nextYear = Integer.valueOf(year) + 1;
    String upperBound = nextYear + "-01-01";
    OrganCount currentCount = getTransplantWaitingListTime(connection,bloodType,gender,age,region, upperBound,lowerBound);
    organCounts.put(months[11], currentCount);

    return organCounts;
  }


  public static AgeCount getReceiverDataAge(Connection connection, String gender, String region, String age) throws SQLException {
    AgeCount ageCount = new AgeCount();
    String endOfQuery = getEndOfQuery(gender, region, null, age);

    String[] beginningOfQuery = RECEIVER_DATA_QUERIES;
    if(region != null) {
      beginningOfQuery = RECEIVER_DATA_QUERIES_REGION;
    }

    for (String query : beginningOfQuery) {
      String currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 0 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 12;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.zeroToTwelve += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 12 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 20;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.twelveToTwenty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 20 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 30;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.twentyToThirty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 30 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 40;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.thirtyToForty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 40 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 50;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.fortyToFifty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 50 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 60;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.fiftyToSixty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 60 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 70;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.sixtyToSeventy += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 70 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 80;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.seventyToEighty += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 80 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 90;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.eightyToNinety += resultSet.getInt(column);
      }

      currentQuery = query + endOfQuery + " AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 >= 90 "
          + "AND FLOOR(DATEDIFF(CURRENT_DATE, `dateOfBirth`))/365.242199 < 150;";
      preparedStatement = connection.prepareStatement(currentQuery);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        int column = resultSet.findColumn(COUNT_STRING);
        resultSet.next();
        ageCount.ninetyPlus += resultSet.getInt(column);
      }
    }

    return ageCount;
  }


  /**
   * Gets the organs requests by the region they were pledged in
   * @param connection The connection to the database
   * @param bloodType The blood type filter
   * @param gender The gender filter
   * @param age The age filter
   * @return An object containing the regional count of organs that match the criteria
   * @throws SQLException Thrown if there is a problem with the SQL
   */
  public static OrgansByRegionCount getReceiverOrgansByRegion(Connection connection, String bloodType, String gender, String age) throws SQLException {
    String receiverType = "receiver";
    OrgansByRegionCount organsByRegionCount = new OrgansByRegionCount();
    organsByRegionCount.setNorthland(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Northland", bloodType, age));
    organsByRegionCount.setAuckland(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Auckland", bloodType, age));
    organsByRegionCount.setWaikato(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Waikato", bloodType, age));
    organsByRegionCount.setBayOfPlenty(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Bay Of Plenty", bloodType, age));
    organsByRegionCount.setGisbourne(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Gisbourne", bloodType, age));
    organsByRegionCount.setHawkesBay(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Hawke's Bay", bloodType, age));
    organsByRegionCount.setTaranaki(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Taranaki", bloodType, age));
    organsByRegionCount.setManawatuWanganui(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Manawatu-Wanganui", bloodType, age));
    organsByRegionCount.setWellington(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Wellington", bloodType, age));
    organsByRegionCount.setTasman(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Tasman", bloodType, age));
    organsByRegionCount.setNelson(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Nelson", bloodType, age));
    organsByRegionCount.setMalborough(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Malborough", bloodType, age));
    organsByRegionCount.setWestCoast(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "West Coast", bloodType, age));
    organsByRegionCount.setCanterbury(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Canterbury", bloodType, age));
    organsByRegionCount.setOtago(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Otago", bloodType, age));
    organsByRegionCount.setSouthland(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Southland", bloodType, age));
    organsByRegionCount.setChathamIslands(DataService.getReceiverDataByOrgans(receiverType, connection, gender, "Chatham Islands", bloodType, age));
    return organsByRegionCount;
  }


  /**
   * Gets the pledged organs by the region they were pledged in
   * @param connection The connection to the database
   * @param bloodType The blood type filter
   * @param gender The gender filter
   * @param age The age filter
   * @return An object containing the regional count of organs that match the criteria
   * @throws SQLException Thrown if there is a problem with the SQL
   */
  public static OrgansByRegionCount getDonorOrgansByRegion(Connection connection, String bloodType, String gender, String age) throws SQLException {
    String donor = "donor";
    OrgansByRegionCount organsByRegionCount = new OrgansByRegionCount();
    organsByRegionCount.setNorthland(DataService.getReceiverDataByOrgans(donor, connection, gender, "Northland", bloodType, age));
    organsByRegionCount.setAuckland(DataService.getReceiverDataByOrgans(donor, connection, gender, "Auckland", bloodType, age));
    organsByRegionCount.setWaikato(DataService.getReceiverDataByOrgans(donor, connection, gender, "Waikato", bloodType, age));
    organsByRegionCount.setBayOfPlenty(DataService.getReceiverDataByOrgans(donor, connection, gender, "Bay Of Plenty", bloodType, age));
    organsByRegionCount.setGisbourne(DataService.getReceiverDataByOrgans(donor, connection, gender, "Gisbourne", bloodType, age));
    organsByRegionCount.setHawkesBay(DataService.getReceiverDataByOrgans(donor, connection, gender, "Hawke's Bay", bloodType, age));
    organsByRegionCount.setTaranaki(DataService.getReceiverDataByOrgans(donor, connection, gender, "Taranaki", bloodType, age));
    organsByRegionCount.setManawatuWanganui(DataService.getReceiverDataByOrgans(donor, connection, gender, "Manawatu-Wanganui", bloodType, age));
    organsByRegionCount.setWellington(DataService.getReceiverDataByOrgans(donor, connection, gender, "Wellington", bloodType, age));
    organsByRegionCount.setTasman(DataService.getReceiverDataByOrgans(donor, connection, gender, "Tasman", bloodType, age));
    organsByRegionCount.setNelson(DataService.getReceiverDataByOrgans(donor, connection, gender, "Nelson", bloodType, age));
    organsByRegionCount.setMalborough(DataService.getReceiverDataByOrgans(donor, connection, gender, "Malborough", bloodType, age));
    organsByRegionCount.setWestCoast(DataService.getReceiverDataByOrgans(donor, connection, gender, "West Coast", bloodType, age));
    organsByRegionCount.setCanterbury(DataService.getReceiverDataByOrgans(donor, connection, gender, "Canterbury", bloodType, age));
    organsByRegionCount.setOtago(DataService.getReceiverDataByOrgans(donor, connection, gender, "Otago", bloodType, age));
    organsByRegionCount.setSouthland(DataService.getReceiverDataByOrgans(donor, connection, gender, "Southland", bloodType, age));
    organsByRegionCount.setChathamIslands(DataService.getReceiverDataByOrgans(donor, connection, gender, "Chatham Islands", bloodType, age));
    return organsByRegionCount;
  }
}
