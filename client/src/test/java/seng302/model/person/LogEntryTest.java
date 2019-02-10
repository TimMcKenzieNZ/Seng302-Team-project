package seng302.model.person;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/*
 * Cannot test log time like done below. Execution time will cause log time to not be equal during execution
 */
@Ignore
public class LogEntryTest {

  private User donor;
  private User clincian;
  private User backupClinician;
  private User admin;
  private User oneNameAdmin;

  @Before
  public void setUp() {
    LocalDate dob = LocalDate.of(1997, 2, 25);
    donor = new DonorReceiver("Johnny", "", "Inwood", dob, "ACB1234");
    clincian = new Clinician("Johnno", "Scary", "Last Name",
        new ContactDetails(null, null, null, null),
        "5", "password", null, null,1);
    backupClinician = new Clinician("BackUp", "", null,
        new ContactDetails(null, null, null, null),
        "22", "password", null, null,1);
    admin = new Administrator("Looming", null, "Percy",
        new ContactDetails(null, null, null, null),
        "looming", "password", null, null,1);
    oneNameAdmin = new Administrator("One Name", "", null,
        new ContactDetails(null, null, null, null),
        "onename", "password", null, null,1);
  }

  //------------------------------------------------------------------------------------------------------------
  // Constructing User Details Tests
  //------------------------------------------------------------------------------------------------------------
  @Test
  public void constructDonor() {
    String donorDetails = LogEntry.constructUserDetails(donor);
    assertEquals("Johnny Inwood (NHI: ACB1234)", donorDetails);
  }

  @Test
  public void constructClinician() {
    String clinicianDetails = LogEntry.constructUserDetails(clincian);
    assertEquals("Johnno Scary Last Name (Clinician, Staff ID: 5)", clinicianDetails);
  }

  @Test
  public void constructAdmin() {
    String adminDetails = LogEntry.constructUserDetails(admin);
    assertEquals("Looming Percy (Administrator, username: looming)", adminDetails);
  }

  @Test
  public void constructOneNameAdmin() {
    String adminDetails = LogEntry.constructUserDetails(oneNameAdmin);
    assertEquals("One Name (Administrator, username: onename)", adminDetails);
  }

  //------------------------------------------------------------------------------------------------------------
  // toString Tests
  //------------------------------------------------------------------------------------------------------------
  // Edit (i.e. standard log use) tests
  @Test
  public void toStringClinicianEditDonor() {
    LogEntry log = new LogEntry(donor, clincian, "dateOfBirth", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Johnny Inwood (NHI: ACB1234), Changed by User: Johnno Scary Last Name (Clinician, Staff ID: 5), dateOfBirth changed from '04/05/1989' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminEditDonor() {
    LogEntry log = new LogEntry(donor, admin, "dateOfBirth", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Johnny Inwood (NHI: ACB1234), Changed by User: Looming Percy (Administrator, username: looming), dateOfBirth changed from '04/05/1989' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminEditClinician() {
    LogEntry log = new LogEntry(clincian, admin, "dateOfBirth", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Johnno Scary Last Name (Clinician, Staff ID: 5), Changed by User: Looming Percy (Administrator, username: looming), dateOfBirth changed from '04/05/1989' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminEditThemselves() {
    LogEntry log = new LogEntry(admin, admin, "dateOfBirth", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Looming Percy (Administrator, username: looming), Changed by User: Looming Percy (Administrator, username: looming), dateOfBirth changed from '04/05/1989' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminEditOtherAdmin() {
    LogEntry log = new LogEntry(admin, oneNameAdmin, "dateOfBirth", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Looming Percy (Administrator, username: looming), Changed by User: One Name (Administrator, username: onename), dateOfBirth changed from '04/05/1989' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  //Boundary cases (null values)
  @Test
  public void toStringNullUser() {
    boolean called = false;
    try {
      LogEntry log = new LogEntry(null, oneNameAdmin, "dateOfBirth", "04/05/1989", "25/02/1997");
      String logString = log.toString();
    } catch (NullPointerException e) {
      called = true;
    }
    assertTrue(called);
  }

  @Test
  public void toStringNullValChanged() {
    boolean called = false;
    try {
      LogEntry log = new LogEntry(donor, oneNameAdmin, null, "04/05/1989", "25/02/1997");
      String logString = log.toString();
    } catch (NullPointerException e) {
      called = true;
    }
    assertTrue(called);
  }

  @Ignore
  //The runner on the server fails to create the date time correctly, causing this test to fail when normally it would run fine
  @Test
  public void toStringNullOrigValue() {
    LogEntry log = new LogEntry(donor, oneNameAdmin, "dateOfBirth", null, "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Johnny Inwood (NHI: ACB1234), Changed by User: One Name (Administrator, username: onename), dateOfBirth changed from '' to '25/02/1997' at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringNullChangedValue() {
    LogEntry log = new LogEntry(donor, oneNameAdmin, "dateOfBirth", "04/05/1989", null);
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Being Modified: Johnny Inwood (NHI: ACB1234), Changed by User: One Name (Administrator, username: onename), dateOfBirth changed from '04/05/1989' to '' at "
            + now + ".", log.toString());
  }

  //Created User cases
  @Test
  @Ignore
  public void toStringDonorCreateThemselves() {
    LogEntry log = new LogEntry(donor, donor, "created", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Johnny Inwood (NHI: ACB1234), Created by: Johnny Inwood (NHI: ACB1234), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringClinicianCreateDonor() {
    LogEntry log = new LogEntry(donor, clincian, "created", null, null);
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Johnny Inwood (NHI: ACB1234), Created by: Johnno Scary Last Name (Clinician, Staff ID: 5), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringClinicianCreateClinician() {
    LogEntry log = new LogEntry(clincian, backupClinician, "created", null, null);
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Johnno Scary Last Name (Clinician, Staff ID: 5), Created by: BackUp (Clinician, Staff ID: 22), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminCreateDonor() {
    LogEntry log = new LogEntry(donor, admin, "created", "", "");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Johnny Inwood (NHI: ACB1234), Created by: Looming Percy (Administrator, username: looming), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminCreateClinician() {
    LogEntry log = new LogEntry(clincian, admin, "created", "hi", "hi");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Johnno Scary Last Name (Clinician, Staff ID: 5), Created by: Looming Percy (Administrator, username: looming), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminCreateOtherAdmin() {
    LogEntry log = new LogEntry(admin, oneNameAdmin, "created", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Created: Looming Percy (Administrator, username: looming), Created by: One Name (Administrator, username: onename), at "
            + now + ".", log.toString());
  }

  //Deleted user cases
  @Test
  public void toStringDonorDeleteThemselves() {
    LogEntry log = new LogEntry(donor, donor, "deleted", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Johnny Inwood (NHI: ACB1234), Deleted by: Johnny Inwood (NHI: ACB1234), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringClinicianDeleteDonor() {
    LogEntry log = new LogEntry(donor, clincian, "deleted", null, null);
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Johnny Inwood (NHI: ACB1234), Deleted by: Johnno Scary Last Name (Clinician, Staff ID: 5), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringClinicianDeleteClinician() {
    LogEntry log = new LogEntry(clincian, backupClinician, "deleted", null, null);
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Johnno Scary Last Name (Clinician, Staff ID: 5), Deleted by: BackUp (Clinician, Staff ID: 22), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminDeleteDonor() {
    LogEntry log = new LogEntry(donor, admin, "deleted", "", "");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Johnny Inwood (NHI: ACB1234), Deleted by: Looming Percy (Administrator, username: looming), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminDeleteClinician() {
    LogEntry log = new LogEntry(clincian, admin, "deleted", "hi", "hi");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Johnno Scary Last Name (Clinician, Staff ID: 5), Deleted by: Looming Percy (Administrator, username: looming), at "
            + now + ".", log.toString());
  }

  @Test
  public void toStringAdminDeleteOtherAdmin() {
    LogEntry log = new LogEntry(admin, oneNameAdmin, "deleted", "04/05/1989", "25/02/1997");
    LocalDateTime now = LocalDateTime.now();
    assertEquals(
        "User Deleted: Looming Percy (Administrator, username: looming), Deleted by: One Name (Administrator, username: onename), at "
            + now + ".", log.toString());
  }
}
