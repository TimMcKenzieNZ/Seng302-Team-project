package seng302.model.person;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.Before;
import org.junit.Test;

public class UserValidationTest {


  private DonorReceiver donor;

  private Clinician clinician;

  private Administrator admin;

  private Administrator badAdmin;

  private UserValidator validator;

  private LinkedHashMap<String, User> donors;

  private LinkedHashMap<String, User> clinicians;

  private LinkedHashMap<String, User> admins;


  @Before
  public void setUp() {
    LocalDate DOB = LocalDate.of(1990, 12, 31);

    donor = new DonorReceiver("Bob", "billy", "Dole", DOB, "AMY2367");
    donor.setPassword("secret");
    //donor.setGender('U');
    donor.setBirthGender('U');

    clinician = new Clinician("George", "Foreman", "123 cat in the hat",
        "Auckland", "789", "secret");

    admin = new Administrator("Joe", "", "blog", "thegreat", "average");

    admins = new LinkedHashMap<>();

    donors = new LinkedHashMap<>();

    clinicians = new LinkedHashMap<>();

  }


  /**
   * Checks if a valid donor that is validated results in a Validation report that has no issues and
   * has the status 'VALID'.
   */
  @Test
  public void testBlueSkyScenarioForSuccessfulValidationOfADonor() {
    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had issues", 0, report.getIssues().size());
    assertEquals("report status was not valid", UserAccountStatus.VALID, report.getAccountStatus());

  }


  /**
   * Checks if a donor with a missing NHI number that is validated results in a Validation report
   * that has toe issues and has the status 'INVALID'.
   */
  @Test
  public void testMissingNHIResultsInAnINVALIDReport() {
    donor.setUserName(null);

    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 2, report.getIssues().size());
    assertEquals("report had the wrong issue", "Missing username", report.getIssues().get(0));
    assertEquals("report had the wrong issue", "Invalid NHI number", report.getIssues().get(1));
    assertEquals("report status was not invalid", UserAccountStatus.INVALID,
        report.getAccountStatus());

  }


  /**
   * checks if all the critical attributes of the donor are null and the donor is validated, then
   * the report will log 5 issues and have an 'INVALID' status.
   */
  @Test
  public void testAllInvalidAttributesAreAllReported() {
    donor = new DonorReceiver(null, null, null, null, null);
    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    System.out.println(report.getIssues());
    assertEquals("report had the wrong number of issues", 5, report.getIssues().size());
    assertEquals("report status was not invalid", UserAccountStatus.INVALID,
        report.getAccountStatus());
  }


  /**
   * Checks if a donor with an invalid NHI number that is validated results in a Validation report
   * that has one issue and has the status 'INVALID'.
   */
  @Test
  public void testInvalidNHIResultsInAnINVALIDReport() {
    donor.setUserName("TIM1234");

    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 1, report.getIssues().size());
    assertEquals("report had the wrong issue", "Invalid NHI number", report.getIssues().get(0));
    assertEquals("report status was not invalid", UserAccountStatus.INVALID,
        report.getAccountStatus());

  }


  /**
   * Checks if a donor with a poorly formed firstname that is validated results in a Validation
   * report that has one issue and has the status 'POOR'.
   */
  @Test
  public void testPoorFirstNameResultsInaPOORReport() {
    donor.setFirstName("&*^");

    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 1, report.getIssues().size());
    assertEquals("report had the wrong issue", "poorly formed first name",
        report.getIssues().get(0));
    assertEquals("report status was not poor", UserAccountStatus.POOR, report.getAccountStatus());

  }


  /**
   * Checks if a donor with a null MasterIllnessList attribute that is validated results in a
   * Validation report that has one issue and has the status 'POOR'.
   */
  @Test
  public void testMissingMasterIllnessListResultsInaREPAIREDReport() {
    donor.setMasterIllnessList(null);

    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 1, report.getIssues().size());
    assertEquals("report had the wrong issue", "Missing MasterIllnessList",
        report.getIssues().get(0));
    assertEquals("report status was not repaired", UserAccountStatus.REPAIRED,
        report.getAccountStatus());

  }


  /**
   * Checks if a donor with 10 missing attributes that is validated results in a Validation report
   * that has 10 issues and has the status 'REPAIRED'. EDIT: gender and birth gender are now set automatically if null.
   */
  @Test
  public void testNullValuesForAllAttributesAreAllCaughtInReport() {
    LocalDate DOB = LocalDate.of(1990, 12, 31);
    donor = new DonorReceiver("Bob", "billy", "Dole", DOB, "AMY2367");
    donor.setPassword("secret");
    donor.setMasterIllnessList(null);
    donor.setRequiredOrgans(null);
    donor.setDonorOrganInventory(null);
    donor.setUserAttributeCollection(null);
    donor.setMedications(null);
    donor.setEmergencyContactDetails(null);
    donor.setContactDetails(null);
    donor.setMiddleName(null);
    donor.setModifications(null);
    donor.setCreationDate(null);
    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 10, report.getIssues().size());
    assertEquals("report status was not repaired", UserAccountStatus.REPAIRED,
        report.getAccountStatus());
  }


  /**
   * Checks if a duplicate donor is validated then its validation report logs its status as
   * 'EXISTS'.
   */
  @Test
  public void testDuplicateAccountIsLoggedAsEXISTSInReport() {
    donors.put("AMY2367", donor);
    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    assertEquals("report status was not exists", UserAccountStatus.EXISTS,
        report.getAccountStatus());
  }


  /**
   * Checks if a donor with 3 poorly formed attributes that is validated has 3 issues in its report
   * and has a 'POOR' status.
   */
  @Test
  public void testAllPoorAttributesAreIssuesAddedtoTheReport() {
    donor.setFirstName("");
    donor.setLastName("");
    donor.setPassword("");
    donor.setCreationDate(LocalDateTime.of(0, 1, 1, 0, 0));
    validator = new UserValidator(donor, donors);
    UserValidationReport report = validator.getReport();
    System.out.println(report.getIssues());
    assertEquals("Wrong number of issues", 2, report.getIssues().size());
    assertEquals("status not POOR", UserAccountStatus.POOR, report.getAccountStatus());
  }


  /**
   * Checks if a clinician with an invalid staff id that is validated has one issue and a 'invalid'
   * status in its report.
   */
  @Test
  public void testInvalidStaffIdIsCaughtInReport() {
    clinician.setUserName("abc");

    validator = new UserValidator(clinician, clinicians);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 1, report.getIssues().size());
    assertEquals("report had the wrong issue", "Invalid Staff ID", report.getIssues().get(0));
    assertEquals("report status was not invalid", UserAccountStatus.INVALID,
        report.getAccountStatus());
  }


  /**
   * Checks if a admin with an invalid username that is validated has one issue and a 'invalid'
   * status in its report.
   */
  @Test
  public void testInvalidAdminUsernameIsCaughtInReport() {
    admin.setUserName("$#$#^$");

    validator = new UserValidator(admin, admins);
    UserValidationReport report = validator.getReport();
    assertEquals("report had the wrong number of issues", 1, report.getIssues().size());
    assertEquals("report had the wrong issue", "Invalid Username", report.getIssues().get(0));
    assertEquals("report status was not invalid", UserAccountStatus.INVALID,
        report.getAccountStatus());
  }


  /**
   * Tests the validatePhoneNumber method wiht a valid value (i.e. one zero or greater). The method
   * should return true, indicating a valid phone number.
   */
  @Test
  public void testValidPhoneNumber() {

    assertTrue(UserValidator.validatePhoneNumber("0"));

  }


  /**
   * Tests the validatePhoneNumber method with an invalid value (i.e. one less than zero). The
   * method should return false, indicating the number to be invalid.
   */
  @Test
  public void testInvalidNegativePhoneNumber() {

    assertFalse(UserValidator.validatePhoneNumber("-1"));

  }


  /**
   * Tests the validatePhone number method with a invalid value ('a'). The method should return
   * false, indicating that the input is not valid.
   */
  @Test
  public void testInvalidLetterPhoneNumber() {

    assertFalse(UserValidator.validatePhoneNumber("a"));

  }


  /**
   * Tests the validatePhoneNumber method with a null value. The method should return false.
   */
  @Test
  public void testNullPhoneNumber() {

    assertFalse(UserValidator.validatePhoneNumber(null));

  }


}
