package seng302.model.databaseTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.database.DBCConnection;
import seng302.model.database.UserService;
import seng302.model.person.DonorReceiverCreator;
import seng302.model.person.DonorReceiverSummary;
import seng302.model.person.LogEntry;
import seng302.model.security.AuthenticationToken;

public class GetDonorReceiversAPITests extends APITest {

  private static DonorReceiverController donorReceiverController;
  private static UserService userService;
  private static final String ALOE = "DEF1234";
  private static final String MONSTERA = "DEF2345";
  private static final String JADE = "DEF3456";
  private static final String SNAKE = "DEF4567";
  private static final String FIDDLE = "DEF5678";
  private static final String SPIDER = "DEF6789";

  private static final String MOBILE = "0213214321";


  @BeforeClass
  public static void setUp() {
    DBCConnection.setTestDatabase(true);
    connectToDatabase();
    adminLogin("test");
    donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    userService = new UserService(authenticationTokenStore);
    AuthenticationToken authenticationToken = new AuthenticationToken(token);
    authenticationTokenStore.add(authenticationToken);
    // also need to change to the test database
    addDonors();
  }

  public static void addDonors() {
    // delete the donors first
    deleteDonors();
    // then add them
    String databaseAccess = "password";
    DonorReceiverCreator donorReceiverCreator = new DonorReceiverCreator(ALOE, "Aloe", "", "Vera", "1996-01-01", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator);
    DonorReceiverCreator donorReceiverCreator2 = new DonorReceiverCreator(MONSTERA, "Monstera", "", "Deliciosa", "1997-01-01", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator2);
    DonorReceiverCreator donorReceiverCreator3 = new DonorReceiverCreator(JADE, "Jade", "", "Plant", "1998-01-01", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator3);
    DonorReceiverCreator donorReceiverCreator4 = new DonorReceiverCreator(SNAKE, "Snake", "", "Plant", "1990-12-31", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator4);
    DonorReceiverCreator donorReceiverCreator5 = new DonorReceiverCreator(FIDDLE, "Fiddle-leaf", "", "Fig", "1990-12-31", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator5);
    DonorReceiverCreator donorReceiverCreator6 = new DonorReceiverCreator(SPIDER, "Spider", "", "Plant", "1995-01-01", databaseAccess, "Sudo", MOBILE);
    donorReceiverController.addDonor(donorReceiverCreator6);
    // set genders
    Map<String, Object> updates = new HashMap<>();
    updates.put("gender", "U");
    updates.put("version", "2");
    donorReceiverController.updateDonorProfile(ALOE, token, updates);
    updates.put("gender", "O");
    updates.put("version", "2");
    donorReceiverController.updateDonorProfile(MONSTERA, token, updates);
    updates.put("gender", "M");
    updates.put("version", "2");
    donorReceiverController.updateDonorProfile(SNAKE, token, updates);
    updates.put("gender", "F");
    updates.put("version", "2");
    donorReceiverController.updateDonorProfile(FIDDLE, token, updates);
    updates.put("gender", "O");
    updates.put("version", "2");
    donorReceiverController.updateDonorProfile(SPIDER, token, updates);
    // set regions
    Map<String, Object> address = new HashMap<>();
    Map<String, Object> region = new HashMap<>();
    final String ADDRESS = "address";
    final String REGION = "region";
    final String CANTERBURY = "Canterbury";
    final String FALSE = "false";
    address.put("version", "3");
    address.put(ADDRESS, region);
    region.put(REGION, "Otago");
    donorReceiverController.updateDonorContactDetails(ALOE, MOBILE, token, FALSE, address);
    region.put(REGION, "Southland");
    address.put("version", "3");
    donorReceiverController.updateDonorContactDetails(MONSTERA, MOBILE, token, FALSE, address);
    address.put("version", "3");
    region.put(REGION, "Northland");
    donorReceiverController.updateDonorContactDetails(JADE, MOBILE, token, FALSE, address);
    address.put("version", "3");
    region.put(REGION, CANTERBURY);
    donorReceiverController.updateDonorContactDetails(SNAKE, MOBILE, token, FALSE, address);
    address.put("version", "3");
    region.put(REGION, CANTERBURY);
    donorReceiverController.updateDonorContactDetails(FIDDLE, MOBILE, token, FALSE, address);
    address.put("version", "3");
    region.put(REGION, CANTERBURY);
    donorReceiverController.updateDonorContactDetails(SPIDER, MOBILE, token, FALSE, address);
    // set donating organs
    final boolean TRUE = true;
    Map<String, Object> donorOrgans = new HashMap<>();
    donorOrgans.put("version", "4");
    donorOrgans.put("heart", TRUE);
    donorReceiverController.updateDonorOrgans(MONSTERA, token, donorOrgans);
    donorOrgans.put("version", "4");
    donorOrgans.put("Liver", TRUE);
    donorOrgans.put("Pancreas", TRUE);
    donorOrgans.put("Corneas", TRUE);
    donorOrgans.put("Skin", TRUE);
    donorOrgans.put("BoneMarrow", TRUE);
    donorOrgans.put("ConnectiveTissue", TRUE);
    donorReceiverController.updateDonorOrgans(SNAKE, token, donorOrgans);
    donorOrgans.put("version", "4");
    donorReceiverController.updateDonorOrgans(FIDDLE, token, donorOrgans);
    donorOrgans.clear();
    donorOrgans.put("version", "4");
    donorOrgans.put("Pancreas", TRUE);
    donorReceiverController.updateDonorOrgans(JADE, token, donorOrgans);
    // set receiving organs
    Map<String, Object> rOrgans = new HashMap<>();
    rOrgans.put("version", "5");
    rOrgans.put("lungs", TRUE);
    donorReceiverController.updateReceiverOrgans(ALOE, token, rOrgans);
    rOrgans.clear();
    rOrgans.put("version", "5");
    rOrgans.put("kidneys", TRUE);
    donorReceiverController.updateReceiverOrgans(MONSTERA, token, rOrgans);
    rOrgans.put("version", "5");
    donorReceiverController.updateReceiverOrgans(FIDDLE, token, rOrgans);
  }

  @AfterClass
  public static void deleteDonors() {
    // deleting the donors
    userService.deleteUser(connection.getConnection(), ALOE);
    userService.deleteUser(connection.getConnection(), MONSTERA);
    userService.deleteUser(connection.getConnection(), JADE);
    userService.deleteUser(connection.getConnection(), SNAKE);
    userService.deleteUser(connection.getConnection(), FIDDLE);
    userService.deleteUser(connection.getConnection(), SPIDER);
  }

  /**
   * Function for finding Snake (DEF4567) and/or Fiddle (DEF5678) in the list of donors given
   * @param donors A list of donor/receiver summaries to be searched through
   * @return Two booleans, the first being whether Snake was found, the second being whether Fiddle was found.
   */
  private Boolean[] findSnakeAndFiddle(List<DonorReceiverSummary> donors) {
    boolean foundFiddle = false;
    boolean foundSnake = false;
    for (DonorReceiverSummary donor : donors) {
      if (donor.getNhi().equals(FIDDLE)) {
        foundFiddle = true;
      }
      if (donor.getNhi().equals(SNAKE)) {
        foundSnake = true;
      }
    }
    return new Boolean[] {foundSnake, foundFiddle};
  }


  public Boolean[] findTwoDonors(List<DonorReceiverSummary> donors, String nhi1, String nhi2) {
    boolean first = false;
    boolean second = false;
    for (DonorReceiverSummary donor : donors) {
      if (donor.getNhi().equals(nhi1)) {
        first = true;
      } else if (donor.getNhi().equals(nhi2)) {
        second = true;
      }
    }
    return new Boolean[] {first, second};
  }

  public Boolean[] findThreeDonors(List<DonorReceiverSummary> donors, String nhi1, String nhi2, String nhi3) {
    boolean first = false;
    boolean second = false;
    boolean third = false;
    for (DonorReceiverSummary donor : donors) {
      if (donor.getNhi().equals(nhi1)) {
        first = true;
      } else if (donor.getNhi().equals(nhi2)) {
        second = true;
      } else if (donor.getNhi().equals(nhi3)) {
        third = true;
      }
    }
    return new Boolean[] {first, second, third};
  }

  public Boolean[] findFourDonors(List<DonorReceiverSummary> donors, String nhi1, String nhi2, String nhi3, String nhi4) {
    boolean first = false;
    boolean second = false;
    boolean third = false;
    boolean fourth = false;
    for (DonorReceiverSummary donor : donors) {
      if (donor.getNhi().equals(nhi1)) {
        first = true;
      } else if (donor.getNhi().equals(nhi2)) {
        second = true;
      } else if (donor.getNhi().equals(nhi3)) {
        third = true;
      } else if (donor.getNhi().equals(nhi4)) {
        fourth = true;
      }
    }
    return new Boolean[] {first, second, third, fourth};
  }

  private List<DonorReceiverSummary> createDonorsList(ResponseEntity responseEntity) {
    return (ArrayList<DonorReceiverSummary>) responseEntity.getBody();
  }

  private void printCannotRunTests() {
    System.out.println("Can't run the tests");
  }

  @Test
  public void testGetDonorsBasic() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertNotEquals(0, donors.size());
      assertNotEquals(1, donors.size());
      Boolean[] foundSnakeAndFiddle = findTwoDonors(donors, SNAKE, FIDDLE);
      assertTrue(foundSnakeAndFiddle[0]);
      assertTrue(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Search=======================================

  /**
   * Test that the search parameter finds Snake Plant.
   */
  @Test
  public void testGetDonorsSearch() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors("Snake", null, null, null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertEquals(1, donors.size());
      assertEquals(SNAKE, donors.get(0).getNhi());
      assertEquals("Snake", donors.get(0).getGivenName());
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that the search parameter doesn't find a person with the name "mnbvcxz".
   */
  @Test
  public void testGetDonorsSearchDoesNotExist() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors("mnbvcxz", null, null, null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertEquals(0, donors.size());
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Amount=======================================

  /**
   * Test that just one donor is returned when the amount is set to 1.
   */
  @Test
  public void testGetDonorsAmount1() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, "1", null, null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertEquals(1, donors.size());
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that no donors are returned when the amount is set to 0.
   */
  @Test
  public void testGetDonorsAmount0() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, "0", null, null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertEquals(0, donors.size());
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Index========================================

  /**
   * Test that the first donor is not included when the index is set to 1.
   */
  @Test
  public void testGetDonorsIndex1() {
    if (canRunTests) {
      ResponseEntity allDonorsResponse = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, null, null,null,null);
      List<DonorReceiverSummary> allDonors = (ArrayList<DonorReceiverSummary>) allDonorsResponse.getBody();
      DonorReceiverSummary firstDonor = allDonors.get(0);
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, "1", null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertNotEquals(firstDonor.getNhi(), donors.get(0).getNhi());
      assertTrue(donors.size() <= 16 && donors.size() >= 6);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that 15 are included when the index is set to 0.
   */
  @Test
  public void testGetDonorsIndex0() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, "0", null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      assertTrue(donors.size() <= 16 && donors.size() >= 6);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that the response code is 400 bad request when the index is set to "abc"
   */
  @Test
  public void testGetDonorsIndexInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, "abc", null, null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Status=======================================

  /**
   * Test that when the status is set to "donor", DEF5678 is part of the results (they are a donor
   * and a receiver)
   */
  @Test
  public void testGetDonorsStatusDonor() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "donor", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      boolean foundFiddle = false;
      for (DonorReceiverSummary donor : donors) {
        if (donor.getNhi().equals(FIDDLE)) {
          foundFiddle = true;
        }
      }
      assertTrue(foundFiddle);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "donor/receiver", DEF5678 is part of the results (they are
   * a donor and a receiver), and that DEF4567 is NOT part of the results (they are only a donor).
   */
  @Test
  public void testGetDonorsStatusDonorReceiver() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "donor/receiver", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findTwoDonors(donors, SNAKE, FIDDLE);
      assertFalse(foundSnakeAndFiddle[0]);
      assertTrue(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "neither", DEF5678 is NOT part of the results (they are
   * a donor and a receiver), and that DEF4567 is NOT part of the results (they are only a donor).
   */
  @Test
  public void testGetDonorsStatusNeither() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "neither", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleSpider = findThreeDonors(donors, SNAKE, FIDDLE, SPIDER);
      assertFalse(foundSnakeFiddleSpider[0]);
      assertFalse(foundSnakeFiddleSpider[1]);
      assertTrue(foundSnakeFiddleSpider[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "receiver", DEF5678 is part of the results (they are
   * a donor and a receiver), and that DEF4567 is NOT part of the results (they are only a donor).
   */
  @Test
  public void testGetDonorsStatusReceiver() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "receiver", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findSnakeAndFiddle(donors);
      assertFalse(foundSnakeAndFiddle[0]);
      assertTrue(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "any", DEF5678 is part of the results (they are
   * a donor and a receiver), and that DEF4567 is part of the results (they are only a donor). Also
   * checks for Spider Plant (DEF6789) (they are not a donor or a receiver).
   */
  @Test
  public void testGetDonorsStatusAny() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "any", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleSpider = findThreeDonors(donors, SNAKE, FIDDLE, SPIDER);
      assertTrue(foundSnakeFiddleSpider[0]);
      assertTrue(foundSnakeFiddleSpider[1]);
      assertTrue(foundSnakeFiddleSpider[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "anywhom", a bad request status code is returned as the status
   * given is invalid.
   */
  @Test
  public void testGetDonorsStatusInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "anywhom", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // ==============Mutliple Statuses==================

  /**
   * Test that when the status is set to "anywhom", a bad request status code is returned as the status
   * given is invalid.
   */
  @Test
  public void testGetDonorsStatusInvalidNoSpace() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "donor,neither", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "any, neither", DEF5678 is part of the results (they are
   * a donor and a receiver), and that DEF4567 is part of the results (they are only a donor). Also
   * checks for Spider Plant (DEF6789) (they are not a donor or a receiver). This is to test that
   * the "keyword" is working correctly (it is handled differently than the others).
   */
  @Test
  public void testGetDonorsStatusAnyOrNeither() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "any, neither", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleSpider = findThreeDonors(donors, SNAKE, FIDDLE, SPIDER);
      assertTrue(foundSnakeFiddleSpider[0]);
      assertTrue(foundSnakeFiddleSpider[1]);
      assertTrue(foundSnakeFiddleSpider[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when the status is set to "donor, neither", DEF5678 is part of the results (they are
   * a donor and a receiver), and that DEF4567 is part of the results (they are only a donor). Also
   * checks for Spider Plant (DEF6789) (they are not a donor or a receiver).
   */
  @Test
  public void testGetDonorsStatusDonorOrNeither() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, "donor, neither", null, null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleSpider = findThreeDonors(donors, SNAKE, FIDDLE, SPIDER);
      assertTrue(foundSnakeFiddleSpider[0]);
      assertTrue(foundSnakeFiddleSpider[1]);
      assertTrue(foundSnakeFiddleSpider[2]);
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Gender=======================================

  /**
   * Test that when gender is set to "any", that both DEF4567 (M, Snake Plant), and DEF5678
   * (F, Fiddle-leaf Fig) are found.
   */
  @Test
  public void testGetDonorsGenderAny() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "any", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findTwoDonors(donors, SNAKE, FIDDLE);
      assertTrue(foundSnakeAndFiddle[0]);
      assertTrue(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "male", that DEF4567 (M, Snake Plant) is found, and DEF5678
   * (F, Fiddle-leaf Fig) is NOT found.
   */
  @Test
  public void testGetDonorsGenderMale() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "male", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findTwoDonors(donors, SNAKE, FIDDLE);
      assertTrue(foundSnakeAndFiddle[0]);
      assertFalse(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "female", that DEF4567 (M, Snake Plant) is NOT found, and DEF5678
   * (F, Fiddle-leaf Fig) is found.
   */
  @Test
  public void testGetDonorsGenderFemale() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "female", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findTwoDonors(donors, SNAKE, FIDDLE);
      assertFalse(foundSnakeAndFiddle[0]);
      assertTrue(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "unknown", that DEF4567 (M, Snake Plant) is NOT found, DEF5678
   * (F, Fiddle-leaf Fig) is NOT found, and DEF1234 (U, Aloe Vera) is found.
   */
  @Test
  public void testGetDonorsGenderUnknown() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "unknown", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleAloe = findThreeDonors(donors, SNAKE, FIDDLE, ALOE);
      assertFalse(foundSnakeFiddleAloe[0]);
      assertFalse(foundSnakeFiddleAloe[1]);
      assertTrue(foundSnakeFiddleAloe[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "other", that DEF4567 (M, Snake Plant) is NOT found, DEF5678
   * (F, Fiddle-leaf Fig) is NOT found, and DEF2345 (O, Monstera Deliciosa) is found.
   */
  @Test
  public void testGetDonorsGenderOther() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "other", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonstera = findThreeDonors(donors, SNAKE, FIDDLE, MONSTERA);
      assertFalse(foundSnakeFiddleMonstera[0]);
      assertFalse(foundSnakeFiddleMonstera[1]);
      assertTrue(foundSnakeFiddleMonstera[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "unspecified", that DEF4567 (M, Snake Plant) is NOT found, DEF5678
   * (F, Fiddle-leaf Fig) is NOT found, and DEF3456 (null, Jade Plant) is found.
   */
  @Test
  public void testGetDonorsGenderUnspecified() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "unspecified", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleJade = findThreeDonors(donors, SNAKE, SNAKE, JADE);
      assertFalse(foundSnakeFiddleJade[0]);
      assertFalse(foundSnakeFiddleJade[1]);
      assertTrue(foundSnakeFiddleJade[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "anywhom", a bad request status code is returned as the gender
   * given is invalid.
   */
  @Test
  public void testGetDonorsGenderInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "anywhom", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // =================Gender Multiple=====================
  /**
   * Test that when gender is set to "male, other", that DEF4567 (M, Snake Plant) is found, DEF5678
   * (F, Fiddle-leaf Fig) is NOT found, and DEF2345 (O, Monstera Deliciosa) is found.
   */
  @Test
  public void testGetDonorsGenderMaleOrOther() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "male, other", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonstera = findThreeDonors(donors, SNAKE, FIDDLE, MONSTERA);
      assertTrue(foundSnakeFiddleMonstera[0]);
      assertFalse(foundSnakeFiddleMonstera[1]);
      assertTrue(foundSnakeFiddleMonstera[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when gender is set to "any, other", that DEF4567 (M, Snake Plant) is found, DEF5678
   * (F, Fiddle-leaf Fig) is found, and DEF2345 (O, Monstera Deliciosa) is found. The gender search values
   * should be ignored if the "any" keyword is there.
   */
  @Test
  public void testGetDonorsGenderAnyOrOther() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, "any, other", null, null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonstera = findThreeDonors(donors, SNAKE, FIDDLE, MONSTERA);
      assertTrue(foundSnakeFiddleMonstera[0]);
      assertTrue(foundSnakeFiddleMonstera[1]);
      assertTrue(foundSnakeFiddleMonstera[2]);
    } else {
      printCannotRunTests();
    }
  }

  // ==================================================Region=======================================

  /**
   * Test that when region is set to "Canterbury", that DEF4567 (Canterbury, Snake Plant) is found, DEF5678
   * (Canterbury, Fiddle-leaf Fig) is found, and DEF1234 (Otago, Aloe Vera) is NOT found.
   */
  @Test
  public void testGetDonorsRegionCanterbury() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, "Canterbury", null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleAloe = findThreeDonors(donors, SNAKE, FIDDLE, ALOE);
      assertTrue(foundSnakeFiddleAloe[0]);
      assertTrue(foundSnakeFiddleAloe[1]);
      assertFalse(foundSnakeFiddleAloe[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when region is set to "Otago", that DEF4567 (Canterbury, Snake Plant) is NOT found,
   * DEF5678 (Canterbury, Fiddle-leaf Fig) is NOT found, and DEF1234 (Otago, Aloe Vera) is found.
   */
  @Test
  public void testGetDonorsRegionOtago() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, "Otago", null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleAloe = findThreeDonors(donors, SNAKE, FIDDLE, ALOE);
      assertFalse(foundSnakeFiddleAloe[0]);
      assertFalse(foundSnakeFiddleAloe[1]);
      assertTrue(foundSnakeFiddleAloe[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when region is set to "Banterbury", a bad request status code is returned as the region
   * given is invalid.
   */
  @Test
  public void testGetDonorsRegionInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, "Banterbury", null, null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // =====================Multiple Regions===============

  /**
   * Test that when region is set to "Canterbury, Otago", that DEF4567 (Canterbury, Snake Plant) is found,
   * DEF5678 (Canterbury, Fiddle-leaf Fig) is found, and DEF1234 (Otago, Aloe Vera) is found.
   */
  @Test
  public void testGetDonorsRegionCanterburyOrOtago() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, "Canterbury, Otago", null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleAloe = findThreeDonors(donors, SNAKE, FIDDLE, ALOE);
      assertTrue(foundSnakeFiddleAloe[0]);
      assertTrue(foundSnakeFiddleAloe[1]);
      assertTrue(foundSnakeFiddleAloe[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when region is set to "Southland, Northland", that DEF4567 (Canterbury, Snake Plant) is NOT found,
   * DEF5678 (Canterbury, Fiddle-leaf Fig) is NOT found, DEF2345 (Southland, Monstera Deliciosa) is found,
   * and DEF3456 (Northland, Jade Plant) is found.
   */
  @Test
  public void testGetDonorsRegionSouthlandOrNorthland() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, "Southland, Northland", null, null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonsteraJade = findFourDonors(donors, SNAKE, FIDDLE, MONSTERA, JADE);
      assertFalse(foundSnakeFiddleMonsteraJade[0]);
      assertFalse(foundSnakeFiddleMonsteraJade[1]);
      assertTrue(foundSnakeFiddleMonsteraJade[2]);
      assertTrue(foundSnakeFiddleMonsteraJade[3]);
    } else {
      printCannotRunTests();
    }
  }

  // ============================================Donating Organs====================================

  /**
   * Test that when donations is set to "heart", that DEF4567 (donating heart, Snake Plant) is found,
   * DEF5678 (donating heart, Fiddle-leaf Fig) is found, and DEF3456 (not donating, Jade Plant) is NOT found.
   */
  @Test
  public void testGetDonorsDonatingHeart() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, "heart", null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleJade = findThreeDonors(donors, SNAKE, FIDDLE, JADE);
      assertTrue(foundSnakeFiddleJade[0]);
      assertTrue(foundSnakeFiddleJade[1]);
      assertFalse(foundSnakeFiddleJade[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when donations is set to "hearty", the response code returned is a 400 bad request.
   */
  @Test
  public void testGetDonorsDonatingInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, "hearty", null, null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // ===================Multiple Donating Organs=================

  /**
   * Test that when donations is set to "liver, pancreas", that DEF4567 (donating liver and pancreas,
   * Snake Plant) is found, DEF5678 (donating liver and pancreas, Fiddle-leaf Fig) is found, DEF3456
   * (donating pancreas, Jade Plant) is found, and DEF1234 (donating heart, Aloe Vera) is NOT found.
   */
  @Test
  public void testGetDonorsDonatingLiverOrPancreas() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, "liver, pancreas", null, null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleJadeAloe = findFourDonors(donors, SNAKE, FIDDLE, JADE, ALOE);
      assertTrue(foundSnakeFiddleJadeAloe[0]);
      assertTrue(foundSnakeFiddleJadeAloe[1]);
      assertTrue(foundSnakeFiddleJadeAloe[2]);
      assertFalse(foundSnakeFiddleJadeAloe[3]);
    } else {
      printCannotRunTests();
    }
  }

  // ===========================================Receiving Organs====================================

  /**
   * Test that when receiving is set to "kidneys", that DEF4567 (receiving nothing, Snake Plant) is
   * NOT found, DEF5678 (receiving kidneys, Fiddle-leaf Fig) is found, and DEF2345 (receiving kidneys,
   * Monstera Deliciosa) is found.
   */
  @Test
  public void testGetDonorsReceivingKidneys() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, "kidneys", null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonstera = findThreeDonors(donors, SNAKE, FIDDLE, MONSTERA);
      assertFalse(foundSnakeFiddleMonstera[0]);
      assertTrue(foundSnakeFiddleMonstera[1]);
      assertTrue(foundSnakeFiddleMonstera[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when receiving is set to "hearty", the response code returned is a 400 bad request.
   */
  @Test
  public void testGetDonorsReceivingInvalid() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, "hearty", null, null,null,null);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  // ===================Multiple Receiving Organs=================

  /**
   * Test that when receiving is set to "kidneys, lungs", that DEF4567 (not receiving, Snake Plant)
   * is NOT found, DEF5678 (receiving kidneys, Fiddle-leaf Fig) is found, DEF1234 (receiving lungs, Aloe
   * Vera) is found, and DEF2345 (receiving kidneys, Monstera Deliciosa) is NOT found.
   */
  @Test
  public void testGetDonorsDonatingKidneysOrLungs() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, "kidneys, lungs", null, null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonsteraAloe = findFourDonors(donors, SNAKE, FIDDLE, MONSTERA, ALOE);
      assertFalse(foundSnakeFiddleMonsteraAloe[0]);
      assertTrue(foundSnakeFiddleMonsteraAloe[1]);
      assertTrue(foundSnakeFiddleMonsteraAloe[2]);
      assertTrue(foundSnakeFiddleMonsteraAloe[3]);
    } else {
      printCannotRunTests();
    }
  }

  // ======================================Minimum and Maximum Ages=================================

  /**
   * Test that when minAge is set to "21", that DEF4567 (27, Snake Plant) is found, DEF5678 (27,
   * Fiddle-leaf Fig) is found, DEF2345 (21, Monstera Deliciosa) is found, and DEF3456 (20, Jade Plant) is NOT found.
   */
  @Test
  public void testGetDonorsMinAge21() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, "21", null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeFiddleMonsteraJade = findFourDonors(donors, SNAKE, FIDDLE, MONSTERA, JADE);
      assertTrue(foundSnakeFiddleMonsteraJade[0]);
      assertTrue(foundSnakeFiddleMonsteraJade[1]);
      assertTrue(foundSnakeFiddleMonsteraJade[2]);
      assertFalse(foundSnakeFiddleMonsteraJade[3]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when minAge is set to "100", that DEF4567 (27, Snake Plant) is NOT found, and DEF5678 (27,
   * Fiddle-leaf Fig) is NOT found.
   */
  @Test
  public void testGetDonorsMinAge100() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, "100", null,null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundSnakeAndFiddle = findSnakeAndFiddle(donors);
      assertFalse(foundSnakeAndFiddle[0]);
      assertFalse(foundSnakeAndFiddle[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when maxAge is set to "22", DEF1234 (22, Aloe Vera) is found, DEF2345 (21, Monstera Deliciosa) is
   * found, and DEF6789 (23, Spider Plant) is NOT found.
   */
  @Test
  public void testGetDonorsMaxAge22() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, null, "22",null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundAloeMonsteraSpider = findThreeDonors(donors, ALOE, MONSTERA, SPIDER);
      assertTrue(foundAloeMonsteraSpider[0]);
      assertTrue(foundAloeMonsteraSpider[1]);
      assertFalse(foundAloeMonsteraSpider[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when maxAge is set to "0", DEF1234 (22, Aloe Vera) is NOT found, DEF3455 (20,
   * Jade Plant) is NOT found, as they are currently the youngest in the database.
   */
  @Test
  public void testGetDonorsMaxAge0() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, null, "0",null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundAloeJade = findTwoDonors(donors, ALOE, JADE);
      assertFalse(foundAloeJade[0]);
      assertFalse(foundAloeJade[1]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when minAge is set to "21" and maxAge is set to "21", DEF1234 (22, Aloe Vera) is NOT
   * found, DEF2345 (21, Monstera Deliciosa) is found, and DEF3456 (20, Jade Plant) is NOT found.
   */
  @Test
  public void testGetDonorsMinAge21AndMaxAge21() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, "21", "21",null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundAloeMonsteraJade = findThreeDonors(donors, ALOE, MONSTERA, JADE);
      assertFalse(foundAloeMonsteraJade[0]);
      assertTrue(foundAloeMonsteraJade[1]);
      assertFalse(foundAloeMonsteraJade[2]);
    } else {
      printCannotRunTests();
    }
  }

  /**
   * Test that when minAge is set to "21" and maxAge is set to "22", DEF1234 (22, Aloe Vera) is
   * found, DEF2345 (21, Monstera Deliciosa) is found, DEF3456 (20, Jade Plant) is NOT found, and
   * DEF6789 (23, Spider Plant) is NOT found.
   */
  @Test
  public void testGetDonorsMinAge21AndMaxAge22() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors(null, null, null, null, null, null, null, null, "21", "22",null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      Boolean[] foundAloeMonsteraJadeSpider = findFourDonors(donors, ALOE, MONSTERA, JADE, SPIDER);
      assertTrue(foundAloeMonsteraJadeSpider[0]);
      assertTrue(foundAloeMonsteraJadeSpider[1]);
      assertFalse(foundAloeMonsteraJadeSpider[2]);
      assertFalse(foundAloeMonsteraJadeSpider[3]);
    } else {
      printCannotRunTests();
    }
  }

  // ======================================Test Everything Together=================================

  @Test
  public void testGetDonorsEverythingTogether() {
    if (canRunTests) {
      ResponseEntity responseEntity = donorReceiverController.getDonors("Fiddle", "1", "0", "donor/receiver", "female", "Canterbury", "liver", "kidneys", "27", "27",null,null);
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      List<DonorReceiverSummary> donors = createDonorsList(responseEntity);
      boolean foundFiddle = false;
      for (DonorReceiverSummary donor : donors) {
        if (donor.getNhi().equals(FIDDLE)) {
          foundFiddle = true;
        }
      }
      assertTrue(foundFiddle);
    } else {
      printCannotRunTests();
    }
  }
}
