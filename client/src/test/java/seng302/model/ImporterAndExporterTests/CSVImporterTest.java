package seng302.model.ImporterAndExporterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.Marshal;
import seng302.model.import_export_strategies.CSVImport;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;

/**
 * A class to test the CSVImport strategy
 */
public class CSVImporterTest {

  private CSVImport importer;


  /**
   * Creates a new CSVImport object to test .csv file importing.
   */
  @Before
  public void setUp() {
    importer = new CSVImport();
  }


  /**
   * Tests whether all 11300 donors from the sample .csv file are correctly read from the .csv file
   * and added to a set.
   */
  @Test
  @Ignore //To be replaced
  public void verifyBlueSkyScenarioOfDefaultCSVFileImport() {
    try {
      Collection<User> users = importer
          .importer(null, null, new File("../testData/SENG302_Project_Profiles v3.csv"));
      assertEquals(11300, users.size());
    } catch (IOException e) {
      fail("could not find file");
    }

  }


  /**
   * Tests if a missing .csv header causes the importer to throw an IllegalArgumentException
   */
  @Test
  public void checkMissingCSVHeaderIsCaught() {
    try {
      Collection<User> users = importer
          .importer(null, null, new File("../testData/missingHeaderTest.csv"));
      assertEquals(users.size(), 0);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("File not found");
    }
  }


  /**
   * Tests if a missing .csv file causes the importer to throw an IOException
   */
  @Test
  public void checkMissingFileIsCaught() {
    try {
      Collection<User> users = importer
          .importer(null, null, new File("../testData/missingFileTest.csv"));
      assertEquals(users.size(), 0);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("Missing file was not caught");
    }
  }


  /**
   * Tests if an invalid .csv file causes the importer to throw an IllegalArgumentException
   */
  @Test
  public void checkInvalidCSVFileIsCaught() {
    try {
      Collection<User> users = importer.importer(null, null, new File("../testData/not a csv.txt"));
      assertEquals(users.size(), 0);
      assertEquals(Marshal.importException.size(), 20);
    } catch (IOException e) {
      fail("Something went wrong with the test");
    }
  }


  /**
   * Tests if a record in the csv file with missing values will throw an IllegalArgumentException
   */
  @Test
  public void checkMissingValuesAreCaught() {
    try {
      Collection<User> users = importer
          .importer(null, null, new File("../testData/missingValuesTest.csv"));
      assertEquals(users.size(), 0);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("Something went wrong with the test");
    }
  }


  /**
   * Blue Sky test to check if a single donor file is correctly imported into the app and converted
   * into a Donor object with the appropriate attributes.
   */
  @Test
  @Ignore
  public void checkDonorCorrectlyCreatedFromCSVFile() {
    try {
      Collection<User> users = importer.importer(null, null, new File("../testData/singleDonor.csv"));
      ArrayList<User> userList = new ArrayList<User>();
      userList.addAll(users);
      DonorReceiver donor = (DonorReceiver) userList.get(0);
      assertEquals("AAG3309", donor.getUserName());
      assertEquals("Rosette", donor.getFirstName());
      assertEquals("Sivior", donor.getLastName());
      assertEquals(LocalDate.parse("08/27/1995", DateTimeFormatter.ofPattern("MM/dd/yyyy")),
          donor.getDateOfBirth());
      assertEquals(null, donor.getDateOfDeath());
      assertEquals("Female", String.valueOf(donor.genderString()));
      assertEquals("Female", String.valueOf(donor.birthGenderString()));
      assertEquals("B-", donor.getUserAttributeCollection().getBloodType());
      assertTrue(152 == donor.getUserAttributeCollection().getHeight());
      assertTrue(71 == donor.getUserAttributeCollection().getWeight());
      assertEquals("306 Kipling", donor.getContactDetails().getAddress().getStreetAddressLineOne());
      assertEquals("Santana de Parna�ba", donor.getContactDetails().getAddress().getSuburbName());
      assertEquals("Whangarei", donor.getContactDetails().getAddress().getCityName());
      assertEquals("Northland", donor.getContactDetails().getAddress().getRegion());
      assertEquals("2163", donor.getContactDetails().getAddress().getPostCode());
      assertEquals("NZ", donor.getContactDetails().getAddress().getCountryCode());
      assertEquals("03 759 5999", donor.getContactDetails().getHomeNum());
      assertEquals("029 260 0739", donor.getContactDetails().getMobileNum());
      assertEquals("rsivior0@biglobe.ne.jp", donor.getContactDetails().getEmail());
    } catch (IOException e) {
      fail("Something went wrong with the test");
    }

  }

  @Test
  @Ignore //To be replaced when client side is coded
  public void checkCorruptCSVLineDoesNotStopImport() {
    try {
      Collection<User> users = importer
          .importer(null, null, new File("../testData/Corruptlinetest.csv"));
      assertEquals(users.size(), 10);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("Something went wrong with the test");
    }

  }


}
