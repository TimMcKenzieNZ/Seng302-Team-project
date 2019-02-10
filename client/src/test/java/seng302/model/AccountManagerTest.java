package seng302.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.person.Address;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.ContactDetails;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

/**
 * A class for testing the AccountManager class methods.
 */
public class AccountManagerTest {


  private UserAttributeCollection userAttCol;
  private DonorOrganInventory donOrgInv;
  private ReceiverOrganInventory reqOrgans;
  private ContactDetails contacts;
  private AccountManager accMan;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private Administrator admin;
  private DonorReceiver donorReceiver1;
  private DonorReceiver donorReceiver2;
  private DonorReceiver donorReceiver3;
  private DonorReceiver donorReceiver4;


  @Before
  public void setUp() {

    Marshal.setTestImport();

    accMan = new AccountManager() {
    };

    admin = new Administrator("Looming", null, "Percy",
        new ContactDetails(null, null, null, null),
        "looming", "password", null, null, 1);

    LocalDate DOB = LocalDate.of(1990, 12, 31);
    LocalDateTime DOD = LocalDateTime.of(2016, 12, 31, 0, 0);

    userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", false, "",
        false, 2.0, "None");

    donOrgInv = new DonorOrganInventory(true, false, true, true,
        false, false, true,
        false, true, false, true, true);

    Address contactAddress = new Address("1 Fleet Street", null, null, "Christchurch", "Canterbury",
        "8044", "64");
    contacts = new ContactDetails(contactAddress, "0220456543", "5452345",
        "randomPerson92@gmail.com");
    donorReceiver1 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");
    donorReceiver1.setUserAttributeCollection(userAttCol);
    donorReceiver1.setDonorOrganInventory(donOrgInv);
    donorReceiver1.setRequiredOrgans(reqOrgans);
    donorReceiver1.setContactDetails(contacts);
    donorReceiver1.setTitle("MR");
    donorReceiver1.setPreferredName("Pâtissier");
    donorReceiver1.setDateOfDeath(DOD);
    donorReceiver1.setGender('M');
    donorReceiver1.setBirthGender('M');
    donorReceiver1.setLivedInUKFlag(false);
    donorReceiver2 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1264");
    donorReceiver2.setUserAttributeCollection(userAttCol);
    donorReceiver2.setDonorOrganInventory(donOrgInv);
    donorReceiver2.setRequiredOrgans(reqOrgans);
    donorReceiver2.setContactDetails(contacts);
    donorReceiver1.setTitle("MR");
    donorReceiver2.setPreferredName("Pâtissier");
    donorReceiver2.setDateOfDeath(DOD);
    donorReceiver2.setGender('M');
    donorReceiver2.setBirthGender('M');
    donorReceiver2.setLivedInUKFlag(false);
    donorReceiver3 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1274");
    donorReceiver3.setUserAttributeCollection(userAttCol);
    donorReceiver3.setDonorOrganInventory(donOrgInv);
    donorReceiver3.setRequiredOrgans(reqOrgans);
    donorReceiver3.setContactDetails(contacts);
    donorReceiver1.setTitle("MR");
    donorReceiver3.setPreferredName("Pâtissier");
    donorReceiver3.setDateOfDeath(DOD);
    donorReceiver3.setGender('M');
    donorReceiver3.setBirthGender('M');
    donorReceiver3.setLivedInUKFlag(false);
    donorReceiver4 = new DonorReceiver("Stu", "", "gog", DOB, "ABC4321");
    donorReceiver4.setUserAttributeCollection(userAttCol);
    donorReceiver4.setDonorOrganInventory(donOrgInv);
    donorReceiver4.setRequiredOrgans(reqOrgans);
    donorReceiver4.setTitle("MR");
    donorReceiver4.setPreferredName("");
    donorReceiver4.setDateOfDeath(DOD);
    donorReceiver4.setGender('M');
    donorReceiver4.setBirthGender('M');
    donorReceiver4.setLivedInUKFlag(false);
    accMan.getMasterList().put(donorReceiver1.getUserName(), donorReceiver1);
    accMan.getMasterList().put(donorReceiver2.getUserName(), donorReceiver2);
    accMan.getMasterList().put(donorReceiver3.getUserName(), donorReceiver3);
    accMan.getMasterList().put(donorReceiver4.getUserName(), donorReceiver4);
    accMan.repopulateDonorAccounts();
  }

  /**
   * tests for niche case where there are no donor profiles
   */
  @Test
  public void printAllDonorsTest() {
    AccountManager accMan2 = new AccountManager();
    assertTrue(accMan2.toString().equals("Donors:\n\nNo donors to display.\n"));

  }

  /**
   * tests for successful case where an NHI code is valid
   */
  @Test
  public void validateNHITest1() {
    assertTrue(accMan.validateNHI("ASD9876"));
  }

  /**
   * tests for failure case where an NHI code is invalid - wrong case
   */
  @Test
  public void validateNHITest2() {
    assertFalse(accMan.validateNHI("aSD9876"));
  }

  /**
   * tests for failure case where an NHI code is invalid - first 3 are not alphabetical
   */
  @Test
  public void validateNHITest3() {
    assertFalse(accMan.validateNHI("1SD9876"));
  }

  /**
   * tests for failure case where an NHI code is invalid - 'I' is present
   */
  @Test
  public void validateNHITest4() {
    assertFalse(accMan.validateNHI("AID9K76"));
  }

  /**
   * tests for failure case where an NHI code is invalid - 'O' is present
   */
  @Test
  public void validateNHITest5() {
    assertFalse(accMan.validateNHI("AOD9K76"));
  }

  /**
   * tests for failure case where an NHI code is invalid - last 4 are not numeric
   */
  @Test
  public void validateNHITest6() {
    assertFalse(accMan.validateNHI("ASD9K76"));
  }

  /**
   * tests for failure case where an NHI code is invalid - last digit is 0
   */
  @Test
  public void validateNHITest7() {
    assertFalse(accMan.validateNHI("AID9K70"));
  }

  /**
   * tests for failure case where an NHI code is invalid - code is too long
   */
  @Test
  public void validateNHITest8() {
    assertFalse(accMan.validateNHI("AID9K730"));
  }

  /**
   * tests for failure case where an NHI code is invalid - code is too short
   */
  @Test
  public void validateNHITest9() {
    assertFalse(accMan.validateNHI("AID9K0"));
  }

  /**
   * tests for failure case where an NHI code is invalid - code present in Accounts
   */
  @Test
  public void validateNHITest10() {
    assertFalse(accMan.validateNHI("ABC1234"));
  }

  /**
   * tests for successful case where the update command is properly issued.
   */
  @Ignore //Working on fix for consistency with CLI handler today. Currently inconsistent
  @Test
  public void issueCommandTest1() {
    accMan.issueCommand(admin, "update", "ABC1234", "contacts", "addressCity", "Springfield");
    assertEquals("Springfield",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
  }

  /**
   * tests for failure case where the update command is in-properly issued. - wrong object
   */
  @Test
  public void issueCommandTest2() {
    accMan.issueCommand(admin, "update", "ABC1234", "organ", "addressCity", "Springfield");
    assertNotEquals("Springfield",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
  }

  /**
   * tests for failure case where the update command is in-properly issued. - wrong target
   */
  @Test
  public void issueCommandTest3() {
    accMan.issueCommand(admin, "update", "XYZ1234", "attribute", "addressCity", "Springfield");
    assertNotEquals("Springfield",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
  }

  /**
   * tests for failure case where the update command is in-properly issued. - wrong attribute
   */
  @Test
  public void issueCommandTest4() {
    accMan.issueCommand(admin, "update", "ABC1234", "attribute", "bloodType", "Springfield");
    assertNotEquals("Springfield",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
  }

  /**
   * tests for failure case where the update command is in-properly issued. - invalid value.
   */
  @Test
  public void issueCommandTest5() {
    accMan.issueCommand(admin, "update", "ABC1234", "attribute", "addressCity", "Springfield 123");
    assertNotEquals("Springfield 123",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
    ;
  }

  /**
   * tests for failure case where the update command is in-properly issued. - wrong command.
   */
  @Test
  public void issueCommandTest6() {
    accMan.issueCommand(admin, "view", "ABC1234", "attribute", "addressCity", "Springfield 123");
    assertNotEquals("Springfield",
        accMan.getDonorReceivers().get("ABC1234").getContactDetails().getAddress().getCityName());
  }

  /**
   * tests for success case where view command is issued to view a specific donor' update log.
   */
  @Test
  @Ignore
  public void issueCommandTest7() {
    String expectedLog1 = new LogEntry(donorReceiver3, admin, "firstName", "Sweeny", "Swoony")
        .toString();
    accMan.issueCommand(admin, "update", "ABC1274", "profile", "givenName", "Swoony");
    accMan.issueCommand(admin, "update", "ABC1274", "attributes", "weight", "55.3");
    accMan.issueCommand(admin, "update", "ABC1274", "organs", "donor", "kidneys", "true");
    accMan.issueCommand("view", "ABC1274", "log");
    String expectedLog2 = new LogEntry(donorReceiver3, admin, "weight", "51.0", "55.3").toString();
    String expectedLog3 = new LogEntry(donorReceiver3, admin, "kidneysDonation", "false", "true")
        .toString();
    expectedLog1 = expectedLog1.substring(0, expectedLog1.length() - 3);
    expectedLog2 = expectedLog2.substring(0, expectedLog2.length() - 3);
    expectedLog3 = expectedLog3.substring(0, expectedLog3.length() - 3);

    boolean found1 = false;
    boolean found2 = false;
    boolean found3 = false;
    for (LogEntry log : donorReceiver3.getUpdateLog()) {
      String logString = log.toString();
      logString = logString.substring(0, logString.length() - 3);
      if (logString.equals(expectedLog1)) {
        found1 = true;
      } else if (logString.equals(expectedLog2)) {
        found2 = true;
      } else if (logString.equals(expectedLog3)) {
        found3 = true;
      }
    }
    assertTrue(found1);
    assertTrue(found2);
    assertTrue(found3);
  }

  /**
   * tests for successful case where the view command is issued to view a specific donor's
   * attributes.
   */
  @Test
  @Ignore
  public void issueCommandTest8() {
    System.setOut(new PrintStream(outContent));
    accMan.issueCommand("view", "ABC1234", "attributes");
    String expected = "Accessing data for donor ABC1234...\n" + "User Attributes:\r\n" +
        "\tHeight: 2.0m\r\n" +
        "\tWeight: 51.0kg\r\n" +
        "\tBlood Type: AB-\r\n" +
        "\r\n" +
        "Address:\r\n" +
        "\t1 Fleet Street\r\n" +
        "\t\r\n" +
        "\tcashmere\r\n" +
        "\tchristchurch\r\n" +
        "\t5678\r\n" +
        "\tNZ\r\n" +
        "\r\n" +
        "\r\n";
    assertTrue(expected.replaceAll("\r", "").equals(outContent.toString().replaceAll("\r", "")));
  }

  /**
   * Checks for successful case where the view command is issued using a donor's name. Finds all
   * donors named "Sweeny Todd".
   */
  @Test
  @Ignore
  public void issueCommandTest9() {
    System.setOut(new PrintStream(outContent));
    accMan.issueCommand("view", "Sweeny", "Todd", "all");
    String expected = "3 match(s) for the name Sweeny Todd.\n" +
        "\n" +
        "Accessing data for donor ABC1234...\n" +
        "Donor Details:\n" +
        "\tName: MR Sweeny  Todd\n" +
        "\tNational Health Index: ABC1234\n" +
        "\tDonorReceiver created at: " + accMan.getDonorReceivers().get(0)
        .formatDateTimeToString(LocalDateTime.now()) + "\n" +
        "\tDate of birth: 1990-12-31\n" +
        "\tDate of death: 2016-12-31\n" +
        "\tGender: M\n" +
        "User Attributes:\n" +
        "\tHeight: 2.0m\n" +
        "\tWeight: 51.0kg\n" +
        "\tBlood Type: AB-\n" +
        "\n" +
        "Address:\n" +
        "\t1 Fleet Street\n" +
        "\t\n" +
        "\tcashmere\n" +
        "\tchristchurch\n" +
        "\t5678\n" +
        "\tNZ\n" +
        "\n" +
        "Organs to donate:\n" +
        "\tLiver\n" +
        "\tPancreas\n" +
        "\tHeart\n" +
        "\tCorneas\n" +
        "\tSkin\n" +
        "\tBone marrow\n" +
        "\tConnective Tissue\n" +
        "\n" +
        "\n" +
        "Personal Contact Details:\n" +
        "\tStreet Address: 1 Fleet Street\n" +
        "\tPostcode: 5678\n" +
        "\tCity: christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0220456543\n" +
        "\tHome Number: 5452345\n" +
        "\tEmail: randomPerson92@gmail.com\n" +
        "Emergency Contact Details:\n" +
        "\tStreet Address: 31b Taylors Ave\n" +
        "\tPostcode: 8052\n" +
        "\tCity: Christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0213459876\n" +
        "\tHome Number: 5458769\n" +
        "\tEmail: randomHelper93@yahoo.com\n" +
        "\n" +
        "Accessing data for donor ABC1264...\n" +
        "Donor Details:\n" +
        "\tName: MR Sweeny  Todd\n" +
        "\tNational Health Index: ABC1264\n" +
        "\tDonorReceiver created at: " + accMan.getDonorReceivers().get(0)
        .formatDateTimeToString(LocalDateTime.now()) + "\n" +
        "\tDate of birth: 1990-12-31\n" +
        "\tDate of death: 2016-12-31\n" +
        "\tGender: M\n" +
        "User Attributes:\n" +
        "\tHeight: 2.0m\n" +
        "\tWeight: 51.0kg\n" +
        "\tBlood Type: AB-\n" +
        "\n" +
        "Address:\n" +
        "\t1 Fleet Street\n" +
        "\t\n" +
        "\tcashmere\n" +
        "\tchristchurch\n" +
        "\t5678\n" +
        "\tNZ\n" +
        "\n" +
        "Organs to donate:\n" +
        "\tLiver\n" +
        "\tPancreas\n" +
        "\tHeart\n" +
        "\tCorneas\n" +
        "\tSkin\n" +
        "\tBone marrow\n" +
        "\tConnective Tissue\n" +
        "\n" +
        "\n" +
        "Personal Contact Details:\n" +
        "\tStreet Address: 1 Fleet Street\n" +
        "\tPostcode: 5678\n" +
        "\tCity: christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0220456543\n" +
        "\tHome Number: 5452345\n" +
        "\tEmail: randomPerson92@gmail.com\n" +
        "Emergency Contact Details:\n" +
        "\tStreet Address: 31b Taylors Ave\n" +
        "\tPostcode: 8052\n" +
        "\tCity: Christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0213459876\n" +
        "\tHome Number: 5458769\n" +
        "\tEmail: randomHelper93@yahoo.com\n" +
        "\n" +
        "Accessing data for donor ABC1274...\n" +
        "Donor Details:\n" +
        "\tName: MR Sweeny  Todd\n" +
        "\tNational Health Index: ABC1274\n" +
        "\tDonorReceiver created at: " + accMan.getDonorReceivers().get(0)
        .formatDateTimeToString(LocalDateTime.now()) + "\n" +
        "\tDate of birth: 1990-12-31\n" +
        "\tDate of death: 2016-12-31\n" +
        "\tGender: M\n" +
        "User Attributes:\n" +
        "\tHeight: 2.0m\n" +
        "\tWeight: 51.0kg\n" +
        "\tBlood Type: AB-\n" +
        "\n" +
        "Address:\n" +
        "\t1 Fleet Street\n" +
        "\t\n" +
        "\tcashmere\n" +
        "\tchristchurch\n" +
        "\t5678\n" +
        "\tNZ\n" +
        "\n" +
        "Organs to donate:\n" +
        "\tLiver\n" +
        "\tPancreas\n" +
        "\tHeart\n" +
        "\tCorneas\n" +
        "\tSkin\n" +
        "\tBone marrow\n" +
        "\tConnective Tissue\n" +
        "\n" +
        "\n" +
        "Personal Contact Details:\n" +
        "\tStreet Address: 1 Fleet Street\n" +
        "\tPostcode: 5678\n" +
        "\tCity: christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0220456543\n" +
        "\tHome Number: 5452345\n" +
        "\tEmail: randomPerson92@gmail.com\n" +
        "Emergency Contact Details:\n" +
        "\tStreet Address: 31b Taylors Ave\n" +
        "\tPostcode: 8052\n" +
        "\tCity: Christchurch\n" +
        "\tRegion: Canterbury\n" +
        "\tMobile Number: 0213459876\n" +
        "\tHome Number: 5458769\n" +
        "\tEmail: randomHelper93@yahoo.com\n" +
        "\n";
    assertEquals(expected, outContent.toString().replaceAll("\r", ""));

  }








  @Test
  public void parseGivenNameTest() {
    AccountManager h = new AccountManager();
    String name = "Alan Laurence Brook";
    String givenName = h.parseGivenName(name);
    assertEquals("Alan", givenName);
  }

  @Test
  public void parseGivenNameTest1() {
    AccountManager h = new AccountManager();
    String name = "";
    String givenName = h.parseGivenName(name);
    assertEquals(null, givenName);
  }

  @Test
  public void parseGivenNameTest2() {
    AccountManager h = new AccountManager();
    String name = "Alan";
    String givenName = h.parseGivenName(name);
    assertEquals("Alan", givenName);
  }

  @Test
  public void parseOtherNamesTest() {
    AccountManager h = new AccountManager();
    String name = "Alan Laurence Brook";
    String OtherNames = h.parseOtherNames(name);
    assertEquals("Laurence", OtherNames);
  }

  @Test
  public void parseOtherNamesTest2() {
    AccountManager h = new AccountManager();
    String name = "Alan Brook";
    String OtherNames = h.parseOtherNames(name);
    assertEquals("", OtherNames);
  }

  @Test
  public void parseOtherNamesTest3() {
    AccountManager h = new AccountManager();
    String name = "Alan Laurence Peter Brook";
    String OtherNames = h.parseOtherNames(name);
    assertEquals("Laurence Peter", OtherNames);
  }


  @Test
  public void parseLastNamesTest() {
    AccountManager h = new AccountManager();
    String name = "Alan Laurence Peter Brook";
    String LastNames = h.parseLastName(name);
    assertEquals("Brook", LastNames);
  }

  @Test
  public void parseLastNamesTest2() {
    AccountManager h = new AccountManager();
    String name = "Alan Brook";
    String LastNames = h.parseLastName(name);
    assertEquals("Brook", LastNames);
  }

  @Test
  public void parseLastNamesTest3() {
    AccountManager h = new AccountManager();
    String name = "Alan";
    String LastNames = h.parseLastName(name);
    assertEquals("", LastNames);
  }


  @Test
  public void emptyRemoveLeadingWhitespaceTest() {

    AccountManager h = new AccountManager();
    String input = "";

    assertTrue(h.removeLeadingWhitespace(input).equals(""));

  }

  @Test
  public void leadingWhitespaceRemoveLeadingWhitespaceTest() {

    AccountManager h = new AccountManager();
    String input = "   A real file name";

    assertTrue(h.removeLeadingWhitespace(input).equals("A real file name"));

  }

  @Test
  public void noLeadingWhitespaceRemoveLeadingWhitespaceTest() {

    AccountManager h = new AccountManager();
    String input = "A real file name";

    assertTrue(h.removeLeadingWhitespace(input).equals("A real file name"));

  }

  /**
   * tests for a correct log when an account is newly created.
   */
  @Test
  public void reactivateOldAccountIfExistsTest1() {
    DonorReceiver donorReceiver = accMan
        .reactivateOldAccountIfExists(admin, accMan.getDonorReceivers().get("ABC1234"));
    //String expected = "DonorReceiver ABC1234 (Sweeny Todd) created at " + donorReceiver.formatDateTimeToString(LocalDateTime.now());
    String log = new LogEntry(donorReceiver, admin, "created", null, "").toString();
    log = log.substring(0, log.length() - 24);
    String actualLog = donorReceiver.getUpdateLog().get(0).toString();
    actualLog = actualLog.substring(0, actualLog.length() - 24);
    assertEquals(log, actualLog);
  }

  /**
   * tests for a correct log when an old account is reactivated.
   */
  @Test
  public void reactivateOldAccountIfExistsTest2() {
    DonorReceiver donorReceiver = accMan.getDonorReceivers().get("ABC1234");
    donorReceiver.setActive(false);
    donorReceiver = accMan.reactivateOldAccountIfExists(admin, donorReceiver);
    String log = new LogEntry(donorReceiver, admin, "reactivated", "inactive", "active").toString();
    log = log.substring(0, log.length() - 3);
    String actualLog = donorReceiver.getUpdateLog().get(0).toString();
    actualLog = actualLog.substring(0, actualLog.length() - 3);
    assertEquals(log, actualLog);
    //String expected = "DonorReceiver ABC1234 (Sweeny Todd) reactivated at " + DonorReceiver.formatDateTimeToString(LocalDateTime.now());
  }


  /**
   * tests for case where a clinician object has not been added to an DonorReceiver Manager (such is
   * the case when app is run for the first time).
   */
  @Test
  public void addClinicianIfNoneExistsTest1() {
    AccountManager am = new AccountManager();
    assertTrue(am.getClinicians().size() == 0);
  }


  /**
   * tests for case where a clinician object has been added to an DonorReceiver Manager (such as the
   * case when addClinicianIfNoneExists has been called and the clinicians list is empty).
   */
  @Test
  public void addClinicianIfNoneExistsTest2() {
    AccountManager am = new AccountManager();
    am.addClinicianIfNoneExists();
    assertTrue(am.getClinicians().size() == 1);
  }


  /**
   * tests for case where addClinicianIfNoneExists is called when there are already clinicians
   * existing.
   */
  @Test
  public void addClinicianIfNoneExistsTest3() {
    AccountManager am = new AccountManager();
    am.getClinicians().put("jabba12", new Clinician("tim", "", "mckenzie", new ContactDetails(
        new Address("123 happly place", null, null,
            null, "Auckland", null, null), null, null, null),
        "jabba12", null, null, null, 1));
    am.getClinicians().put("jabba12", new Clinician("tim", "", "mckenzie", new ContactDetails(
        new Address("123 happly place", null, null,
            null, "Auckland", null, null), null, null, null),
        "jabba12", null, null, null, 1));
    am.addClinicianIfNoneExists();
    assertTrue(am.getClinicians().size() == 2);
    assertTrue(am.getClinicians().containsKey("0"));
    assertTrue(am.getClinicians().containsKey("jabba12"));
  }


}
