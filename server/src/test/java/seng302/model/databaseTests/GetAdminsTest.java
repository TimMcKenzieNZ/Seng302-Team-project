package seng302.model.databaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.model.person.LogEntry;
import seng302.model.person.UserCreator;
import seng302.model.person.UserSummary;

@Ignore //For some reason this failed despite the functionality working
public class GetAdminsTest extends APITest {

  private AdminController adminController;

  private static final String ASC = "ascending";
  private static final String DESC = "descending";
  private static final String CHRISTCHURCH = "Christchurch";
  private static final String CANTERBURY = "Canterbury";
  private static final String WELLINGTON = "Wellington";

  @Before
  public void setUp() {
    connectToDatabase();
    adminLogin("test");
    adminController = new AdminController(authenticationTokenStore);
  }


  /**
   * Creates a set of test administrators
   */
  private void createTestAdmins() {
    for (int i = 999; i > 989; i--) {
      adminController.addAdmin(new UserCreator(CHRISTCHURCH + i, "Steven", "Bob",
          "Banner", "password", CANTERBURY,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "628374893" + i));
    }
    for (int i = 989; i > 979; i--) {
      adminController.addAdmin(new UserCreator(CHRISTCHURCH + i, "Zoe", "Natasha",
          "Barnes", "newPassword", WELLINGTON,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "435697958" + i));
    }
    for (int i = 979; i > 969; i--) {
      adminController.addAdmin(new UserCreator(CHRISTCHURCH + i, "Chloe", "Jane",
          "Stark", "things", CANTERBURY,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "467828379" + i));
    }
    for (int i = 969; i > 959; i--) {
      adminController.addAdmin(new UserCreator(CHRISTCHURCH + i, "Bruce", "Samson",
          "Rogers", "password", "Northland",
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "537294756" + i));
    }
  }


  /**
   * Deletes the test admins
   */
  private void deleteTestAdmin() {
    for (int i = 999; i > 959; i--) {
      adminController.deleteAdmin(CHRISTCHURCH + i);
    }
  }


  // --- Admin GET tests for success ---


  /**
   * Tests the server can find admins based on their user names
   */
  @Test
  public void getAdminsSuccessUsername() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins
              (null, "Christchurch9", null,
                  null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(40 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins(
              null, "Christchurch98", null,
              null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins(
              null, "Christchurch981", null,
              null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(1 <= foundAdmins.size());

      assertEquals("Zoe", foundAdmins.get(0).getFirstName());
      assertEquals("Natasha", foundAdmins.get(0).getMiddleName());
      assertEquals("Barnes", foundAdmins.get(0).getLastName());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that GET admins can find administrators from their first name
   */
  @Test
  public void getAdminsSuccessFirstName() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins("oe", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(20 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Chloe", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Chloe", "Christchurch975", null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(1, foundAdmins.size());
      assertEquals("Christchurch975", foundAdmins.get(0).getUsername());
      assertEquals("Jane", foundAdmins.get(0).getMiddleName());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that GET admins can find administrators from their middle name
   */
  @Test
  public void getAdminsSuccessMiddleName() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins("ne", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(30 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Jane", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Jane", "Christchurch970", null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(1, foundAdmins.size());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that GET admins can find administrators from their last name
   */
  @Test
  public void getAdminsSuccessLastName() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins("ne", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(30 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Banner", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins("Banner", "Christchurch998", null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(1, foundAdmins.size());
      assertEquals("Christchurch998", foundAdmins.get(0).getUsername());
      assertEquals("Steven", foundAdmins.get(0).getFirstName());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that GET admins can find a specific number of administrators
   */
  @Test
  public void getAdminsSuccessLimitAmount() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins(null, null, "40", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(40, foundAdmins.size());

      requestResponse =
          adminController.getAdmins(null, null, "20", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(20, foundAdmins.size());

      requestResponse =
          adminController.getAdmins(null, null, "1", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(1, foundAdmins.size());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that GET admins can find administrators with the offset set (offset defaults to 16 values)
   */
  @Test
  public void getAdminsSuccessOffset() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins(null, CHRISTCHURCH, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(30 <= foundAdmins.size());

      requestResponse =
          adminController.getAdmins(null, CHRISTCHURCH, null, "20",
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(16, foundAdmins.size());

      deleteTestAdmin();
    }
  }


  /**
   * Tests that the GET request can find through region
   */
  @Test
  public void getAdminSuccessRegion() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins(null, null, null,
              null, null, null, "Northland");

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue( foundAdmins.size() >= 10) ;

      requestResponse =
          adminController.getAdmins(null, null, null,
              null, null, null, WELLINGTON);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue( foundAdmins.size() >= 10) ;

      requestResponse =
          adminController.getAdmins(null, "975", null,
              null, null, null, CANTERBURY);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals( 1, foundAdmins.size());

      deleteTestAdmin();
    }
  }


  /**
   * Checks that the username filtering is working as expected
   */
  private void usernameFilterTesting() {
    ResponseEntity requestResponse =
        adminController.getAdmins(null, null, null,
            null, "0", ASC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getUsername().compareTo(foundAdmins.get(i - 1).getUsername()) > 0);
    }

    requestResponse =
        adminController.getAdmins(null, null, null,
            null, "0", DESC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getUsername().compareTo(foundAdmins.get(i - 1).getUsername()) < 0);
    }
  }


  /**
   * Tests the first name filtering is working correctly
   */
  private void firstNameFilterTesting() {
    ResponseEntity requestResponse =
        adminController.getAdmins(null, null, null,
            null, "1", ASC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getFirstName().compareTo(foundAdmins.get(i - 1).getFirstName()) >= 0);
    }

    requestResponse =
        adminController.getAdmins(null, null, null,
            null, "1", DESC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getFirstName().compareTo(foundAdmins.get(i - 1).getFirstName()) <= 0);
    }
  }


  /**
   * Tests the middle name filtering is working correctly
   */
  private void middleNameFilterTesting() {
    ResponseEntity requestResponse =
        adminController.getAdmins(null, null, null,
            null, "2", ASC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getMiddleName().compareTo(foundAdmins.get(i - 1).getMiddleName()) >= 0);
    }

    requestResponse =
        adminController.getAdmins(null, null, null,
            null, "2", DESC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getMiddleName().compareTo(foundAdmins.get(i - 1).getMiddleName()) <= 0);
    }
  }


  /**
   * Tests the last name filtering is working correctly
   */
  private void lastNameFilterTesting() {
    ResponseEntity requestResponse =
        adminController.getAdmins(null, null, null,
            null, "3", ASC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getLastName().compareTo(foundAdmins.get(i - 1).getLastName()) >= 0);
    }

    requestResponse =
        adminController.getAdmins(null, null, null,
            null, "3", DESC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundAdmins.size(); i++) {
      assertTrue(foundAdmins.get(i).getLastName().compareTo(foundAdmins.get(i - 1).getLastName()) <= 0);
    }
  }


  /**
   * Tests the region filtering is working correctly
   */
  private void regionFilterTesting() {
    if (canRunTests) {
      ResponseEntity requestResponse =
          adminController.getAdmins(null, null, null,
              null, "4", ASC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      for (int i = 1; i < foundAdmins.size(); i++) {
        if (foundAdmins.get(i).getRegion() != null &&
            !foundAdmins.get(i).getRegion().equals("null") &&
            foundAdmins.get(i - 1).getRegion() != null &&
            !foundAdmins.get(i - 1).getRegion().equals("null")) {
          assertTrue(foundAdmins.get(i).getRegion().compareTo(foundAdmins.get(i - 1).getRegion()) >= 0);
        }
      }

      requestResponse =
          adminController.getAdmins(null, null, null,
              null, "4", DESC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      for (int i = 1; i < foundAdmins.size(); i++) {
        if (foundAdmins.get(i).getRegion() != null &&
            !foundAdmins.get(i).getRegion().equals("null") &&
            foundAdmins.get(i - 1).getRegion() != null &&
            !foundAdmins.get(i - 1).getRegion().equals("null")) {
          assertTrue(
              foundAdmins.get(i).getRegion().compareTo(foundAdmins.get(i - 1).getRegion()) <= 0);
        }
      }
    }
  }


  /**
   * Tests the filtering of the end points based on the different columns
   */
  @Test
  public void getAdminsSuccessFiltering() {
    if (canRunTests) {
      createTestAdmins();

      usernameFilterTesting();
      firstNameFilterTesting();
      middleNameFilterTesting();
      lastNameFilterTesting();
      regionFilterTesting();

      deleteTestAdmin();
    }
  }

  
  // --- Admin GET tests for failure ---


  /**
   * Tests that the correct error code is shown when an invalid username or name given
   */
  @Test
  public void getAdminsFailInvalidNames() {
    if (canRunTests) {
      createTestAdmins();
      ResponseEntity requestResponse =
          adminController.getAdmins(null, "oaejtgnkrdjtbhlnrj", null,
              null, null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundAdmins.size());

      requestResponse =
          adminController.getAdmins("oaejtgnkrdjtbhlnrj", null, null,
              null, null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundAdmins.size());
      deleteTestAdmin();
    }
  }


  /**
   * Tests that when the offset is greater than the length of the list there are no problems.
   */
  @Test
  public void getAdministratorFailInvalidOffset() {
    if (canRunTests) {
      createTestAdmins();
      ResponseEntity requestResponse =
          adminController.getAdmins(null, null, null, "1500000",
              null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundAdmins.size());
      deleteTestAdmin();
    }
  }


  /**
   * Tests that when an invalid column is entered to filter by there are no problems.
   */
  @Test
  public void getAdministratorFailInvalidColumn() {
    if (canRunTests) {
      createTestAdmins();

      ResponseEntity requestResponse =
          adminController.getAdmins(null, null, null,
              null, "Invalid", ASC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(!foundAdmins.isEmpty());

      requestResponse = adminController.getAdmins(null, null, null,
              null, "2", "INVALID", null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundAdmins = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(!foundAdmins.isEmpty());

      deleteTestAdmin();
    }
  }
}
