package seng302.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.DonorReceiver;

/**
 * A class used for testing the methods of the Illness class.
 */
public class IllnessTest {


  // THe following attributes will be used for testing the Illness methods.
  private Illness ill;
  private LocalDate date;
  private LocalDate dateOfBirth;
  private String dateString;


  @Before
  /**
   * Sets up an Illness object for testing its methods.
   */
  public void setUp() {
    date = LocalDate.of(1990, 03, 18);
    dateOfBirth = LocalDate.of(1990, 03, 18);
    ill = new Illness("SHINGLES", date, false, false);
    dateString = "1990-03-18";
  }

  @Test
  /**
   * Tests for lower boundary  successful case where the Illness object's date is valid (is equal to the date ofi birth).
   */
  public void validateDateTest1() {
    assertEquals(ill.validateDate(date, dateOfBirth), true);
  }


  @Test
  /**
   * Tests for upper boundary  successful case where the Illness object's date is valid (is equal to the current time).
   */
  public void validateDateTest2() {
    assertEquals(ill.validateDate(LocalDate.now(), LocalDate.now()), true);
  }


  @Test
  /**
   * Tests for upper boundary  failure case where the Illness object's date is invalid (is after the current time).
   */
  public void validateDateTest3() {
    LocalDate now = LocalDate.now();
    now = now.plusDays(1);
    assertEquals(ill.validateDate(now, LocalDate.now()), false);
  }


  @Test
  /**
   * Tests for lower boundary  failure case where the Illness object's date is invalid (is before date of birth).
   */
  public void validateDateTest4() {
    date = date.minusDays(1);
    assertEquals(ill.validateDate(date, dateOfBirth), false);
  }


  @Test
  /**
   * Tests for upper boundary  failure case where one parameter is null.
   */
  public void validateDateTest5() {
    assertEquals(ill.validateDate(date, null), false);
  }


  @Test
  /**
   * Tests for lower boundary successful case where the given name is 1 letter.
   */
  public void validateNameTest1() {
    assertEquals(ill.validateName("a"), true);
  }


  @Test
  /**
   * Tests for upper boundary successful case where the given name is 50 letters.
   */
  public void validateNameTest2() {
    assertEquals(ill.validateName("appleappleappleappleappleappleappleappleappleapple"), true);
  }


  @Test
  /**
   * Tests for upper boundary failure case where the given name is 51 letters.
   */
  public void validateNameTest3() {
    assertEquals(ill.validateName("appleappleappleappleappleappleappleappleappleapplea"), false);
  }


  @Test
  /**
   * Tests for lower boundary failure case where the given name is 0 letters.
   */
  public void validateNameTest4() {
    assertEquals(ill.validateName(""), false);
  }


  @Test
  /**
   * Tests for failure successful case where the given name is null.
   */
  public void validateNameTest5() {
    assertEquals(ill.validateName(null), false);
  }


  @Test
  /**
   * Tests for failure successful case where the given name is non-alphabetic.
   */
  public void validateNameTest6() {
    assertEquals(ill.validateName("123"), false);
  }


  @Test
  /**
   * Test for failure case where both of the given booleans are true. (An Illness cannot be both chronic and cured).
   */
  public void compareChronicWithCuredTest1() {
    assertEquals(ill.compareChronicWithCured(true, true), false);
  }


  @Test
  /**
   * Test for boundary successful case where one of the given booleans is not initialized.
   */
  public void compareChronicWithCuredTest2() {
    assertEquals(ill.compareChronicWithCured(Boolean.valueOf(null), true), true);
  }


  @Test
  /**
   * Tests for successful case where a log is created for the given attributes with a time stamp.
   */
  public void createLogTest1() {
    String expected = String
        .format("\tDiagnosis SHINGLES modified: name changed from 'SHINGLES' to 'DEMENTIA'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expected, ill.createIllnessLog("name", "SHINGLES", "DEMENTIA"));
  }


  @Test
  /**
   * Tests for boundary successful case where a log is created for the given null attributes with a time stamp.
   */
  public void createLogTest2() {
    String expected = String
        .format("\tDiagnosis SHINGLES modified: null changed from 'null' to 'null'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expected, ill.createIllnessLog(null, null, null));
  }


  @Test
  /**
   * Tests for blue sky scenario where every attribute is updated with valid attributes. Note updateName(), updateDate(),
   * updateChronic(), and updateCured are tested here implicitly.
   */
  public void updateAttributeTest1() {
    String[] nameSuccess = ill.updateAttribute("name", "DEMENTIA", null);
    String expectedName = String
        .format("\tDiagnosis SHINGLES modified: name changed from 'SHINGLES' to 'DEMENTIA'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expectedName, nameSuccess[0]);
    String[] dateSuccess = ill.updateAttribute("date", "1993-02-02", dateOfBirth);
    String expectedDate = String
        .format("\tDiagnosis DEMENTIA modified: date changed from '1990-03-18' to '1993-02-02'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expectedDate, dateSuccess[0]);
    String expectedChronic = String
        .format("\tDiagnosis DEMENTIA modified: chronic changed from 'false' to 'true'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expectedChronic, ill.updateAttribute("chronic", "true", null)[0]);
    ill.updateAttribute("chronic", "false", null); // reset for cured test.
    String expectedCured = String
        .format("\tDiagnosis DEMENTIA modified: cured changed from 'false' to 'true'. " +
            "Change made at %s.", DonorReceiver.formatDateTimeToString(LocalDateTime.now()));
    assertEquals(expectedCured, ill.updateAttribute("cured", "true", null)[0]);
  }


  @Test
  /**
   * Tests for failure case messages where none of the given update attributes are valid. Note updateName(), updateDate(),
   * updateChronic(), and updateCured are tested here implicitly.
   */
  public void updateAttributeTest2() {
    String expectedName = "ERROR: Invalid value 123. Name must be between 1 and 50 alphabetical characters.\n";
    assertEquals(expectedName, ill.updateAttribute("name", "123", null)[0]);

    String expectedDate = String
        .format("ERROR: Invalid value %s. Date must be a valid date that is not after the " +
            "current date nor before the donor's birth date of %s. The date also has to be in the following format: "
            +
            "'dd/MM/YYYY'.\n", "ABC-123-^%", ill.formatDate(dateOfBirth));
    assertEquals(expectedDate, ill.updateAttribute("date", "ABC-123-^%", dateOfBirth)[0]);
    ill.updateAttribute("chronic", "true", null);
    String expectedCured = "ERROR: An Illness cannot be both chronic and cured/resolved. First uncheck 'chronic'.\n";
    assertEquals(expectedCured, ill.updateAttribute("cured", "true", null)[0]);
  }


}
