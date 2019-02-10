package seng302.model.databaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.ClinicianController;
import seng302.model.person.LogEntry;
import seng302.model.person.UserCreator;
import seng302.model.person.UserSummary;
import seng302.model.security.AuthenticationTokenStore;

public class GetCliniciansTest extends APITest {
  private ClinicianController clinicianController;

  private static final String ASC = "ascending";
  private static final String DESC = "descending";
  private static final String CANTERBURY = "Canterbury";
  private static final String WELLINGTON = "Wellington";

  @Before
  public void setUp() {
    clinicianController = new ClinicianController(new AuthenticationTokenStore());
    connectToDatabase();
    clinicianLogin("test");
  }


  /**
   * Creates a set of test clinicians
   */
  private void createTestClinicians() {
    for (int i = 999; i > 989; i--) {
      clinicianController.addClinician(new UserCreator(String.valueOf(i), "Steven", "Bob",
          "Banner", "password", CANTERBURY,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "628374893" + i));
    }
    for (int i = 989; i > 979; i--) {
      clinicianController.addClinician(new UserCreator(String.valueOf(i), "Zoe", "Natasha",
          "Barnes", "newPassword", WELLINGTON,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "435697958" + i));
    }
    for (int i = 979; i > 969; i--) {
      clinicianController.addClinician(new UserCreator(String.valueOf(i), "Chloe", "Jane",
          "Stark", "things", CANTERBURY,
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "467828379" + i));
    }
    for (int i = 969; i > 959; i--) {
      clinicianController.addClinician(new UserCreator(String.valueOf(i), "Bruce", "Samson",
          "Rogers", "password", "Northland",
          new LogEntry("test", "test", "test",
              "test", "test", LocalDateTime.now()),
          new ArrayList<>(), "537294756" + i));
    }
  }


  /**
   * Deletes the test clinicians
   */
  private void deleteTestClinician() {
    for (int i = 999; i > 959; i--) {
      clinicianController.deleteClinician(String.valueOf(i));
    }
  }


  // --- Clinician GET tests for success ---


  /**
   * Tests the server can find clinicians based on their user names
   */
  @Test
  public void getCliniciansSuccessUsername() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians
              (null, "9", null,
                  null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(40 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians(
              null, "98", null,
              null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians(
              null, "981", null,
              null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(1 <= foundClinicians.size());

      assertEquals("Zoe", foundClinicians.get(0).getFirstName());
      assertEquals("Natasha", foundClinicians.get(0).getMiddleName());
      assertEquals("Barnes", foundClinicians.get(0).getLastName());

      deleteTestClinician();
    }
  }


  /**
   * Tests that GET clinicians can find clinicians from their first name
   */
  @Test
  public void getCliniciansSuccessFirstName() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians("oe", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(20 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians("Chloe", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Tests that GET clinicians can find clinicians from their middle name
   */
  @Test
  public void getCliniciansSuccessMiddleName() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians("ne", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(30 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians("Jane", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Tests that GET clinicians can find clinicians from their last name
   */
  @Test
  public void getCliniciansSuccessLastName() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians("ne", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(30 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians("Banner", null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(10 <= foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Tests that GET clinicians can find a specific number of clinicians
   */
  @Test
  public void getCliniciansSuccessLimitAmount() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, "40", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(40 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians(null, null, "20", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(20, foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians(null, null, "1", null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(1, foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Tests that GET clinicians can find clinicians with the offset set (offset defaults to 16 values)
   */
  @Test
  public void getCliniciansSuccessOffset() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, null, null,
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(40 <= foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians(null, null, null, "20",
              null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(16, foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Tests that the GET request can find through region
   */
  @Test
  public void getClinicianSuccessRegion() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, null,
              null, null, null, "Northland");

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue( foundClinicians.size() >= 10) ;

      requestResponse =
          clinicianController.getClinicians(null, null, null,
              null, null, null, WELLINGTON);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue( foundClinicians.size() >= 10) ;

      requestResponse =
          clinicianController.getClinicians(null, "975", null,
              null, null, null, null);

      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals( 1, foundClinicians.size());

      deleteTestClinician();
    }
  }


  /**
   * Checks that the username filtering is working as expected
   */
  private void usernameFilterTesting() {
    ResponseEntity requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "0", ASC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getUsername().compareTo(foundClinicians.get(i - 1).getUsername()) > 0);
    }

    requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "0", DESC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getUsername().compareTo(foundClinicians.get(i - 1).getUsername()) < 0);
    }
  }


  /**
   * Tests the first name filtering is working correctly
   */
  private void firstNameFilterTesting() {
    ResponseEntity requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "1", ASC, null);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size() && foundClinicians.get(i).getFirstName() != null; i++) {
      assertTrue(foundClinicians.get(i).getFirstName().compareToIgnoreCase(foundClinicians.get(i - 1).getFirstName()) >= 0);
    }

    requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "1", DESC, null);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size() && foundClinicians.get(i).getFirstName() != null; i++) {
      assertTrue(foundClinicians.get(i).getFirstName().compareToIgnoreCase(foundClinicians.get(i - 1).getFirstName()) <= 0);
    }
  }


  /**
   * Tests the middle name filtering is working correctly
   */
  private void middleNameFilterTesting() {
    ResponseEntity requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "2", ASC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getMiddleName().compareToIgnoreCase(foundClinicians.get(i - 1).getMiddleName()) >= 0);
    }

    requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "2", DESC, WELLINGTON);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getMiddleName().compareToIgnoreCase(foundClinicians.get(i - 1).getMiddleName()) <= 0);
    }
  }


  /**
   * Tests the last name filtering is working correctly
   */
  private void lastNameFilterTesting() {
    ResponseEntity requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "3", ASC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getLastName().compareToIgnoreCase(foundClinicians.get(i - 1).getLastName()) >= 0);
    }

    requestResponse =
        clinicianController.getClinicians(null, null, null,
            null, "3", DESC, CANTERBURY);
    assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
    foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
    for (int i = 1; i < foundClinicians.size(); i++) {
      assertTrue(foundClinicians.get(i).getLastName().compareToIgnoreCase(foundClinicians.get(i - 1).getLastName()) <= 0);
    }
  }


  /**
   * Tests the region filtering is working correctly
   */
  private void regionFilterTesting() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, null,
              null, "4", ASC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      for (int i = 1; i < foundClinicians.size(); i++) {
        if (foundClinicians.get(i).getRegion() != null &&
            !foundClinicians.get(i).getRegion().equals("null") &&
            foundClinicians.get(i - 1).getRegion() != null &&
            !foundClinicians.get(i - 1).getRegion().equals("null")) {
          assertTrue(foundClinicians.get(i).getRegion().compareToIgnoreCase(foundClinicians.get(i - 1).getRegion()) >= 0);
        }
      }

      requestResponse =
          clinicianController.getClinicians(null, null, null,
              null, "4", DESC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      for (int i = 1; i < foundClinicians.size(); i++) {
        if (foundClinicians.get(i).getRegion() != null &&
            !foundClinicians.get(i).getRegion().equals("null") &&
            foundClinicians.get(i - 1).getRegion() != null &&
            !foundClinicians.get(i - 1).getRegion().equals("null")) {
          assertTrue(
              foundClinicians.get(i).getRegion().compareToIgnoreCase(foundClinicians.get(i - 1).getRegion()) <= 0);
        }
      }
      deleteTestClinician();
    }
  }


  /**
   * Tests the filtering of the end points based on the different columns
   */
  @Test
  public void getCliniciansSuccessFiltering() {
    if (canRunTests) {
      createTestClinicians();

      usernameFilterTesting();
      firstNameFilterTesting();
      middleNameFilterTesting();
      lastNameFilterTesting();
      regionFilterTesting();

      deleteTestClinician();
    }
  }


  // --- Clinician GET tests for failure ---


  /**
   * Tests that the correct error code is shown when an invalid username or name given
   */
  @Test
  public void getCliniciansFailInvalidNames() {
    if (canRunTests) {
      createTestClinicians();
      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, "oaejtgnkrdjtbhlnrj", null,
              null, null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundClinicians.size());

      requestResponse =
          clinicianController.getClinicians("oaejtgnkrdjtbhlnrj", null, null,
              null, null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundClinicians.size());
      deleteTestClinician();
    }
  }


  /**
   * Tests that when the offset is greater than the length of the list there are no problems.
   */
  @Test
  public void getClinicianFailInvalidOffset() {
    if (canRunTests) {
      createTestClinicians();
      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, null, "1500000",
              null, null, null);

      assertEquals(HttpStatus.NOT_FOUND, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertEquals(0, foundClinicians.size());
      deleteTestClinician();
    }
  }


  /**
   * Tests that when an invalid column is entered to filter by there are no problems.
   */
  @Test
  public void getClinicianFailInvalidColumn() {
    if (canRunTests) {
      createTestClinicians();

      ResponseEntity requestResponse =
          clinicianController.getClinicians(null, null, null,
              null, "Invalid", ASC, null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      List<UserSummary> foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(!foundClinicians.isEmpty());

      requestResponse = clinicianController.getClinicians(null, null, null,
          null, "2", "INVALID", null);
      assertEquals(HttpStatus.OK, requestResponse.getStatusCode());
      foundClinicians = (ArrayList<UserSummary>) requestResponse.getBody();
      assertTrue(!foundClinicians.isEmpty());

      deleteTestClinician();
    }
  }
}
