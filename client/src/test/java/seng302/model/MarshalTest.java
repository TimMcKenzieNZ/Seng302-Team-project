package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.App;
import seng302.model.import_export_strategies.CSVImport;
import seng302.model.import_export_strategies.JSONFolderImport;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.person.User;

public class MarshalTest {

  private DonorReceiver donorReceiver;
  private UserAttributeCollection userAttCol;
  private DonorOrganInventory donOrgInv;
  private ReceiverOrganInventory reqOrgans;
  private Map<String, DonorReceiver> donorReceivers;

  private Marshal testMarshal;
  private Marshal testMarshal2;
  private Set<User> users;
  private LinkedHashMap<String, User> validationCheck;
  private int numAdminsInCollection;
  private int numClinciansInCollection;
  private int numDonorsInCollection;


  /**
   * Populating an donorReceiver with values to test.
   */
  @Before
  public void setUp() {

    LocalDate DOB = LocalDate.of(1990, 12, 31);
    LocalDateTime DOD = LocalDateTime.of(2016, 12, 31, 0, 0);
    LocalDateTime organUpdateTime = LocalDate.of(2018, 5, 3).atTime(14, 15);

    userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", false, "",
        false, 2.0, "None");

    donOrgInv = new DonorOrganInventory(true, false, true, true,
        false, false, true,
        false, true, false, true, true);

    reqOrgans = new ReceiverOrganInventory(true, false, false, false, false, false, false, false,
        false, false, false, false, organUpdateTime, organUpdateTime, organUpdateTime,
        organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime,
        organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime);

    donorReceiver = new DonorReceiver("Sweeny", "", "Todd", DOB, "AAA9999");
    donorReceiver.setUserAttributeCollection(userAttCol);
    donorReceiver.setDonorOrganInventory(donOrgInv);
    donorReceiver.setRequiredOrgans(reqOrgans);
    //donorReceiver.setContactDetails(contacts);
    donorReceiver.setTitle("MR");
    donorReceiver.setPreferredName("Pâtissier");
    donorReceiver.setDateOfDeath(DOD);
    donorReceiver.setGender('M');
    donorReceiver.setBirthGender('M');
    donorReceiver.setLivedInUKFlag(false);
    donorReceivers = new LinkedHashMap<String, DonorReceiver>();
    donorReceivers.put(donorReceiver.getUserName(), donorReceiver);

    //========================================
    //Used for testing methods returnUserArrayList
    users = new HashSet<>();
    users.add(new Administrator("Steve", "Peter", "Jobs", "apple", "windowsSucks"));
    users.add(new Administrator("Bill", "Tom", "Gates", "windows", "appleSucks"));
    users.add(new Administrator("Linux", "Benedict", "Torvalds", "linux", "linuxIsFree"));
    numAdminsInCollection = users.size();

    users.add(new Clinician("Linus Benedict", "Torvalds", "13 Clinician Lane", "Canterbury", "1",
        "linuxIsFree"));
    users.add(new Clinician("Linus Benedict", "Torvalds", "13 Clinician Lane", "Canterbury", "2",
        "linuxIsFree"));
    users.add(new Clinician("Linus Benedict", "Torvalds", "13 Clinician Lane", "Canterbury", "3",
        "linuxIsFree"));
    users.add(new Clinician("Linus Benedict", "Torvalds", "13 Clinician Lane", "Canterbury", "4",
        "linuxIsFree"));

    numClinciansInCollection = 4;

    users.add(new DonorReceiver("Linus", "Benedict", "Torvalds", LocalDate.now(), "ABC9876"));
    users.add(new DonorReceiver("Linus", " Benedict", "Torvalds", LocalDate.now(), "ABC1234"));
    numDonorsInCollection = 2;
    testMarshal = new Marshal(users);
    //=============================
    App.getDatabase().addDefaultAdminIfNoneExists();
    AccountManager.setCurrentUser(App.getDatabase().getAdminByUsername("Sudo"));
  }

  @After
  public void cleanUp() {
    File file = new File("../donorReceivers" + File.separator + "AAA9999.json");
    if (file.delete()) {
      System.out.println("Success");
    } else {
      System.out.println("Potential file error");
    }
  }


  /**
   * Calls the fileExists method for a file which does not exist. Should return false.
   */
  @Test
  public void fileDoesNotExistTest() {

    assertFalse(Marshal.fileExists("fileDoesNotExistTest"));

  }

  /**
   * Calls the fileExists method for a file which does exist. Should return true. Will return false
   * if an exception occurs.
   */
  @Test
  public void fileExistsTest() {

    try {

      File file = new File("../fileExistsTest.json");
      file.createNewFile();
      assertTrue(Marshal.fileExists("fileExistsTest"));

    } catch (IOException exception) {

      assertTrue(false);

    }

  }


  /**
   * Attempts to import donorReceivers from a valid file.
   */
  @Test
  @Ignore
  public void importExportTest() {

    try {
      Marshal.exportAccounts(this.donorReceivers);
      this.donorReceivers.clear(); // Clear donorReceivers ArrayList.
      donorReceivers = Marshal.importAccounts();

      for (int index = 0; index < donorReceivers.size(); index++) {

        DonorReceiver importedDonorReceiver = donorReceivers.get(index);

        if (importedDonorReceiver.getUserName().equals(donorReceiver.getUserName())) {

          assertTrue(importedDonorReceiver.toString().equals(donorReceiver.toString()));
          return;

        }

      }

      assertTrue(false);
      return;

    } catch (IOException exception) {

      assertTrue(false);

    }
  }

  @Test
  public void returnAdministratorArrayListTest() {
    ArrayList<Administrator> arrayList = testMarshal.returnAdministratorArrayList();
    assertEquals(numAdminsInCollection, arrayList.size());
  }

  @Test
  public void returnClinicianArrayListTest() {
    ArrayList<Clinician> arrayList = testMarshal.returnClinicianArrayList();
    assertEquals(numClinciansInCollection, arrayList.size());
  }

  @Test
  public void returnDonorReceiverArrayListTest() {
    ArrayList<DonorReceiver> arrayList = testMarshal.returnDonorReceiverArrayList();
    assertEquals(numDonorsInCollection, arrayList.size());
  }

  @Test
  @Ignore //To be replaced when the client side is rewritten
  public void validateAndAddDonorsBlueSky() {
    testMarshal2 = new Marshal(new LinkedHashMap<String, User>());
    try {
      testMarshal2.setImportStrategy(new CSVImport());
      File file = new File("../testData/MarshalTestValidateAndAddDonorsBlueSky.csv");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("donor/receiver", "../testData", file);
        assertEquals(2, testMarshal2.getSuccessfulImports().size());
        assertEquals(0, testMarshal2.getDuplicateUsers().size());
        assertEquals(0, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }

  }

  @Test
  @Ignore //To be replaced when the client side is rewritten
  public void validateAndAddDonorsCloudySky() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      user.put("AAG3309", new DonorReceiver("Rosette", null, "Sivior", LocalDate.now(), "AAG3309"));
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new CSVImport());
      File file = new File("../testData/MarshalTestValidateAndAddDonorsCloudySky.csv");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("donor/receiver", "../testData", file);
        assertEquals(1, testMarshal2.getSuccessfulImports().size());
        assertEquals(1, testMarshal2.getDuplicateUsers().size());
        assertEquals(1, testMarshal2.getFailedImports().size());

      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore
  public void validateAndAddDonorsRain() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new CSVImport());
      File file = new File("../testData/MarshalTestValidateAndAddDonorsRain.csv");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("donor/receiver", "testData", file);
        assertEquals(12, testMarshal2.getSuccessfulImports().size());
        assertEquals(1, testMarshal2.getDuplicateUsers().size());
        assertEquals(3, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }


  @Test
  @Ignore //Tests server functionality
  public void validateAndAddCliniciansBlueSky() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/ValidateAndAddClinicianBlueSkyJSONFolder");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("clinician", "../testData", file);
        assertEquals(3, testMarshal2.getSuccessfulImports().size());
        assertEquals(0, testMarshal2.getDuplicateUsers().size());
        assertEquals(0, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore //Tests server functionality
  public void validateAndAddCliniciansCloudySky() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      user.put("13",
          new Clinician("peter", "parker", "999 Country road", "Canterbury", "13", "hello"));

      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/ValidateAndAddClinicianBlueSkyJSONFolder");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("clinician", "../testData", file);
        assertEquals(2, testMarshal2.getSuccessfulImports().size());
        assertEquals(1, testMarshal2.getDuplicateUsers().size());
        assertEquals(0, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore //Tests server functionality
  public void validateAndAddCliniciansRain() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      user.put("13",
          new Clinician("peter", "parker", "999 Country road", "Canterbury", "13", "hello"));
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/ValidateAndAddClinicianRainJSONFolder");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("clinician", "../testData", file);
        assertEquals(1, testMarshal2.getSuccessfulImports().size());
        assertEquals(0, testMarshal2.getDuplicateUsers().size());
        assertEquals(3, testMarshal2.getFailedImports().size());


      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore //Tests server functionality
  public void validateAndAddAdminsBlueSky() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/AdminImporting");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("administrator", "../testData", file);
        assertEquals(2, testMarshal2.getSuccessfulImports().size());
        assertEquals(0, testMarshal2.getDuplicateImports().size());
        assertEquals(0, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore //Tests server functionality
  public void validateAndAddAdminsCloudySky() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      user.put("rand123", new Administrator("peter", "parker", "spiderman", "rand123", "password"));
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/ValidateAndAddAdminCloudySkyJSONFolder");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("administrator", "../testData", file);
        assertEquals(1, testMarshal2.getSuccessfulImports().size());
        assertEquals(1, testMarshal2.getDuplicateImports().size());
        assertEquals(0, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }

  @Test
  @Ignore //Tests server functionality
  public void validateAndAddAdminsRain() {
    try {
      LinkedHashMap<String, User> user = new LinkedHashMap<>();
      user.put("rand123", new Administrator("peter", "parker", "spiderman", "rand123", "password"));
      testMarshal2 = new Marshal(user);
      testMarshal2.setImportStrategy(new JSONFolderImport());
      File file = new File("../testData/ValidateAndAddAdminRainJSONFolder");
      if (file.canRead()) {
        assertTrue(true);
        LinkedHashMap<String, User> linkedHashMap = testMarshal2
            .importer("administrator", "../testData", file);
        assertEquals(1, testMarshal2.getSuccessfulImports().size());
        assertEquals(1, testMarshal2.getDuplicateImports().size());
        assertEquals(1, testMarshal2.getFailedImports().size());
      } else {
        assertFalse(true);
      }
    } catch (IOException e) {
      assertTrue(false);
    }
  }


  @Test
  /**
   * Tests whether the mergeOriginalDonorWithNewDonor() function correctly copies over a selection of attributes from
   * one donor object to another using a sample of copied attributes.
   */
  public void testDonorMergingFunctionBlueSky() {
    LocalDate DOB = LocalDate.of(1990, 12, 31);
    DonorReceiver nova = new DonorReceiver("Sweeny", "", "Todd", DOB, "AAA9999");
    nova = testMarshal.mergeOriginalDonorWithNewDonor(donorReceiver, nova);
    assertEquals(true, nova.getDonorOrganInventory().getLiver());
    assertEquals("Pâtissier", nova.getPreferredName());
    assertEquals(true, nova.getRequiredOrgans().getLiver());

  }

}
