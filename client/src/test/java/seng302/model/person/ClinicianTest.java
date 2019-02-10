package seng302.model.person;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import seng302.model.AccountManager;


/**
 * A class to test the methods of the Clinician class.
 */
public class ClinicianTest {

  /**
   * A Clinician object for which to test its methods.
   */
  private Clinician clinician;
  private AccountManager db;


  @Before
  /**
   * Setting up the clinician object for testing.
   */
  public void setUp() {
    LocalDateTime now = LocalDateTime.now();
    ArrayList<LogEntry> modifications = new ArrayList<>();
    clinician = new Clinician("tim", "", "mckenzie", new ContactDetails(
        new Address("123 happly place", null, null,
            null, "Auckland", null, null), null, null, null),
        "jabba12", null, now, modifications,1);
    db = new AccountManager();
  }

  @Test
  /**
   * Tests for successful case where the given password is valid (6-30 alphanumeric characters).
   */
  public void validatePasswordTest1() {
    assertTrue(UserValidator.validatePassword("jabba12"));

  }


  @Test
  /**
   * Tests for failure case where the given password is invalid (spaces, commas, apostrophes, and dashes etc).
   */
  public void validatePasswordTest2() {
    assertFalse(UserValidator.validatePassword("jabba12 , '*&&^%"));

  }

  @Test
  /**
   * Tests for  lower boundry successful case where the given password is valid (6-30 alphanumeric characters).
   */
  public void validatePasswordTest3() {
    assertTrue(UserValidator.validatePassword("123456"));

  }

  @Test
  /**
   * Tests for upper boundary successful case where the given password is valid (6-30 alphanumeric characters).
   */
  public void validatePasswordTest4() {
    assertTrue(UserValidator.validatePassword("123456789012345678901234567890"));

  }

  @Test
  /**
   * Tests for failure case where the given password is invalid (null).
   */
  public void validatePasswordTest5() {
    assertFalse(UserValidator.validatePassword(null));

  }

  @Test
  /**
   * Tests for successful case where the given staff ID is an int > 0.
   */
  public void validateStaffIDIsIntTest1() {
    assertTrue(UserValidator.validateStaffIDIsInt("123"));
  }


  @Test
  /**
   * Tests for the boundary failure case where the given staff ID is an int <= 0.
   */
  public void validateStaffIDIsIntTest2() {
    assertFalse(UserValidator.validateStaffIDIsInt("0"));
  }


  @Test
  /**
   * Tests for the failure case where the given staff ID is not an int.
   */
  public void validateStaffIDIsIntTest3() {
    assertFalse(UserValidator.validateStaffIDIsInt("jabbaTheHutt"));
  }


  @Test
  /**
   * Tests for the failure case where the given staff ID is null.
   */
  public void validateStaffIDIsIntTest4() {
    assertFalse(UserValidator.validateStaffIDIsInt(null));
  }


  @Test
  /**
   * Tests for successful case where the clinician makes 3 valid updates
   */
  public void makeLogTest1() {
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bob");
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bill");
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bruce");
    assertEquals(3, clinician.getModifications().size());
  }

  @Test
  /**
   * Tests for successful case where the clinician makes 2 valid updates and one invalid update
   */
  public void makeLogTest2() {
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bob");
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bill1234");
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bruce");
    assertEquals(2, clinician.getModifications().size());
  }


  @Test
  /**
   * Tests for successful case where the modifications should have two specific logs
   */
  public void makeLogTest3() {
    //Note: The reason for failing was the timestamp. Potential to fail irregularly in the future
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bob");
    String logEntry1 = new LogEntry(clinician, clinician, "givenName", "tim", "Bob").toString();
    db.updateClinicianAttribute(clinician, clinician, "givenName", "Bruce");
    String logEntry2 = new LogEntry(clinician, clinician, "givenName", "Bob", "Bruce").toString();
    logEntry1 = logEntry1.substring(0, logEntry1.length() - 24);
    logEntry2 = logEntry2.substring(0, logEntry2.length() - 24);
    boolean found1 = false;
    boolean found2 = false;
    for (LogEntry log : clinician.getModifications()) {
      String logString = log.toString();
      logString = logString.substring(0, logString.length() - 24);
      if (logString.equals(logEntry1)) {
        found1 = true;
      } else if (logString.equals(logEntry2)) {
        found2 = true;
      }
    }
    assertTrue(found1);
    assertTrue(found2);
  }

  @Test
  /**
   * Tests for successful case where there have been no modfications to the clinician.
   */
  public void makeLogTest4() {

    String expected = "Modification log:\n" + "No modifications have been recorded.\n" + "\n";
    assertEquals(expected, clinician.modificationsToString());
  }


  @Test
  /**
   * Tests for successful case where a clinician's region is changed.
   */
  public void updateTest1() {
    String expected = "updated";
    String log = clinician.updateRegion("Canterbury");
    assertEquals("Canterbury", clinician.getContactDetails().getAddress().getRegion());
    assertEquals(expected, log);

  }

  @Test
  /**
   * Tests for failure case where a clinician's given work address to update is invalid.
   */
  public void updateTest2() {
    String expected =
        "ERROR: Invalid value #$%. Work address must be between 1 and 100 alphanumeric characters. "
            +
            "Spaces, commas, apostrophes, and dashes are also allowed.\n";
    String log = clinician.updateWorkAddress("#$%");
    assertEquals("123 happly place",
        clinician.getContactDetails().getAddress().getStreetAddressLineOne());
    assertEquals(expected, log);

  }

  @Test
  /**
   * Tests for failure case where a updating a clinician's given name is null.
   */
  public void updateTest3() {
    String expected =
        "ERROR: Invalid value null. Given name must be between 1 and 50 alphabetical characters. " +
            "Spaces, commas, apostrophes, and dashes are also allowed.\n";
    String log = clinician.updateGivenName(null);
    assertEquals("tim", clinician.getFirstName());
    assertEquals(expected, log);

  }

  @Test
  /**
   * Tests for boundary failure case where a updating a clinician's last name is too short (empty string).
   */
  public void updateTest4() {
    String expected =
        "ERROR: Invalid value . Last name must be between 1 and 50 alphabetical characters. " +
            "Spaces, commas, apostrophes, and dashes are also allowed.\n";
    String log = clinician.updateLastName("");
    assertEquals("mckenzie", clinician.getLastName());
    assertEquals(expected, log);

  }

}
