package seng302.model.databaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static seng302.controller.BaseController.getDBCConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.controller.ClinicianController;
import seng302.controller.DonorReceiverController;
import seng302.controller.ImportController;
import seng302.model.importer.ImportSummary;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.UserCreator;
import seng302.model.person.UserAccountStatus;
import seng302.model.person.UserValidationReport;
import seng302.model.security.AuthenticationToken;

public class APIImporterTests extends APITest {

  private static ImportController importController;
  private static AdminController adminController;
  private static ClinicianController clinicianController;
  private static DonorReceiverController donorReceiverController;

  private static File[] adminFiles;
  private static File[] clinicianFiles;
  private static File[] donorReceiverFiles;
  private static File[] donorReceiverCSVFiles;

  @BeforeClass
  public static void setUp() {
    connectToDatabase();
    adminLogin("test");
    importController = new ImportController(authenticationTokenStore);
    adminController = new AdminController(authenticationTokenStore);
    clinicianController = new ClinicianController(authenticationTokenStore);
    donorReceiverController = new DonorReceiverController(authenticationTokenStore);
//    AuthenticationToken authenticationToken = new AuthenticationToken(token);
//    authenticationTokenStore.add(authenticationToken);

    File adminDirectory = new File("../testData/AdminImporting");
    adminFiles = adminDirectory.listFiles();

    File clinicianDirectory = new File("../testData/ClinicianImporting");
    clinicianFiles = clinicianDirectory.listFiles();

    File donorReceiverDirectory = new File("../testData/DonorReceiverImporting");
    donorReceiverFiles = donorReceiverDirectory.listFiles();

    File donorReceiverCSVFile = new File("../testData/DonorReceiverCSVImporting");
    donorReceiverCSVFiles = donorReceiverCSVFile.listFiles();
  }


  /**
   * Finds the file being imported from the import testing directory
   * @param files The directory of files to be looked through
   * @param fileName The file name needed in the test
   * @return The file needed for the importer test
   */
  private File fileFinder(File[] files, String fileName) {
    for (File file : files) {
      if (file.getName().equals(fileName)) {
        return file;
      }
    }
    return null;
  }


  /**
   * A new object mapper for the test to run in. This is currently a duplicate in server as exists
   * in client.
   * @return The new mapper for the test.
   */
  private ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    return mapper;
  }

  // --- Donor/Receiver Tests ---
  // --- Expected Behaviours Normal Input ---

  /**
   * Test whether we can import a new donor through their JSON - Blue sky scenario
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterCreation() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverCorrect.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getSuccessfulImports().get(0);
    String username = userReport.getUsername();

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    donorReceiverController.deleteDonorReceiver(username);
    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.VALID, accountStatus);
  }

  /**
   * Test whether we can overwrite an existing donor with a correct version
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterValidOverwrite() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverOverwriteValid.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getSuccessfulImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.EXISTS, accountStatus);

    reduceVersion(getDBCConnection().getConnection(), userReport.getUsername());
  }

  /**
   * Test whether a valid donor JSON with null fields is properly repaired and posted on the db.
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterRepaired() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverNullFields.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getSuccessfulImports().get(0);
    String username = userReport.getUsername();

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    donorReceiverController.deleteDonorReceiver(username);
    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.REPAIRED, accountStatus);
  }

  // --- Expected Behaviours Unexpected Correct Input ---

  /**
   * Tests whether a JSON with the mandatory field `birth gender` missing will be repaired
   * and sent to the db
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterMissingBirthGender() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverMissingBirthGender.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getSuccessfulImports().get(0);
    String username = userReport.getUsername();

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    donorReceiverController.deleteDonorReceiver(username);
    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.REPAIRED, accountStatus);
  }

  /**
   * Tests whether a JSON with the non-mandatory field `contact details` missing will be sent to the
   * db without being repaired
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterMissingContactDetails() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverMissingContactDetails.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getSuccessfulImports().get(0);
    String username = userReport.getUsername();

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    donorReceiverController.deleteDonorReceiver(username);
    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.VALID, accountStatus);
  }

  // --- Expected Behaviours Incorrect Input ---

  /**
   * Tests whether a JSON with the mandatory field `Date of Birth` missing, which cannot be repaired,
   * is rejected by the server
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterMissingDateOfBirth() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverMissingDateOfBirth.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getRejectedImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.INVALID, accountStatus);
  }

  /**
   * Tests whether a JSON with the mandatory field `username`, which cannot be repaired is rejected,
   * is rejected by the server
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterMissingUsername() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverMissingUsername.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getRejectedImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.INVALID, accountStatus);
  }

  /**
   * Tests whether a JSON with an invalid username is rejected by the server
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterInvalidUsername() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverInvalidUsername.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getRejectedImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.INVALID, accountStatus);
  }

  /**
   * Tests whether a JSON with an invalid first name is rejected by the server
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterInvalidFirstName() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverInvalidFirstName.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    System.out.println(response.getBody());
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getRejectedImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.POOR, accountStatus);
  }

  /**
   * Tests whether a JSON with an invalid last name is rejected by the server
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void donorReceiverJSONImporterInvalidLastName() throws IOException {
    File donorFile = fileFinder(donorReceiverFiles, "SingleDonorReceiverInvalidLastName.json");
    ObjectMapper mapper = createObjectMapper();
    HashMap donorReceiverMap = mapper.readValue(donorFile, HashMap.class);

    ResponseEntity response = importController.singleImportDonor(donorReceiverMap, null);
    ImportSummary importSummary = (ImportSummary) response.getBody();
    UserValidationReport userReport = importSummary.getRejectedImports().get(0);

    HttpStatus statusCode = response.getStatusCode();
    UserAccountStatus accountStatus = userReport.getAccountStatus();

    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertEquals(UserAccountStatus.POOR, accountStatus);
  }

  // Note: Version is not checked when importing a single JSON, so we don't validate the version

  // Tests for invalid files can't be done through unit tests and are done as Postman tests on the
  // eng-git wiki - https://eng-git.canterbury.ac.nz/seng302-2018/team-600/wikis/postman-tests


  // --- Clinician Tests ---
  // --- Expected Behaviours Normal Input ---


  /**
   * Testing that the clinician JSON importer creates an clinician with a correctly
   * formatted JSON file given.
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImporterCreation() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianCorrect.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    clinicianController.deleteClinician(importedClinician.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  /**
   * Testing that the clinician JSON importer overwrites an clinician when a file is
   * supplied for an clinician already in the system.
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImporterOverwrite() throws IOException {
    ObjectMapper mapper = createObjectMapper();
    UserCreator user = new UserCreator("8888", "Etienne", "something","Hallows", "oldPassword", "Canterbury", "", "Sudo");
    ResponseEntity responseEntity = clinicianController.addClinician(user);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianCorrectOverwriteChanged.json");
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    responseEntity = importController.singleImportClinician(importedClinician, null);
    clinicianController.deleteClinician("8888");
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }


  // --- Expected Behaviours Unexpected Correct Input ---


  /**
   * Testing that an administrator can be made when all contact details set to null
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImporterNoContactDetails() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianCorrectNoContactDetails.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    clinicianController.deleteClinician(importedClinician.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  /**
   * Testing that an administrator can be made when all contact details set to null and
   * a couple of fields are given to the contact details
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImporterNoAddress() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianCorrectNoAddress.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    clinicianController.deleteClinician(importedClinician.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  // --- Expected Behaviours Incorrect Input ---


  /**
   * Tests that an administrator is not created when an upper case NHI is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImportFailNHIUpper() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianIncorrectNHIUpper.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when a lower case NHI is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImportFailNHILower() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianIncorrectNHILower.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when a number is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImportFailUsernameNotNumber() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianIncorrectUsernameNotNumber.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when invalid fields are given
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void clinicianJSONImportFailInvalidUserAttributes() throws IOException {
    File clinicianFile = fileFinder(clinicianFiles, "SingleClinicianIncorrectInvalidUserAttributes.json");
    ObjectMapper mapper = createObjectMapper();
    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
    HttpStatus statusCode = importController.singleImportClinician(importedClinician, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  // --- Administrator Tests ---
  // --- Expected Behaviours Normal Input ---


  /**
   * Testing that the administrator JSON importer creates an administrator with a correctly
   * formatted JSON file given.
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImporterCreation() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminCorrect.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    adminController.deleteAdmin(importedAdmin.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  /**
   * Testing that the administrator JSON importer overwrites an administrator when a file is
   * supplied for an administrator already in the system.
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImporterOverwrite() throws IOException {
    ObjectMapper mapper = createObjectMapper();
    UserCreator userCreatorAdmin = new UserCreator("Narnia1", "Default", "Admin", "Tester", "password", "Canterbury", "", "");
    adminController.addAdmin(userCreatorAdmin);
    File adminFile = fileFinder(adminFiles,"SingleAdminCorrectOverwriteChanged.json");
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    adminController.deleteAdmin("Narnia1");
    assertEquals(HttpStatus.ACCEPTED, statusCode);
  }


  // --- Expected Behaviours Unexpected Correct Input ---


  /**
   * Testing that an administrator can be made when all contact details set to null
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImporterNoContactDetails() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminCorrectNoContactDetails.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    adminController.deleteAdmin(importedAdmin.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  /**
   * Testing that an administrator can be made when all contact details set to null and
   * a couple of fields are given to the contact details
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImporterNoAddress() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminCorrectNoContactDetailsDetailed.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    adminController.deleteAdmin(importedAdmin.getUserName());
    assertEquals(HttpStatus.CREATED, statusCode);
  }


  // --- Expected Behaviours Incorrect Input ---


  /**
   * Tests that an administrator is not created when an upper case NHI is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImportFailNHIUpper() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminIncorrectNHIUsernameUpper.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when a lower case NHI is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImportFailNHILower() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminIncorrectNHIUsernameLower.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when a number is given as the username
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImportFailNumber() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminIncorrectUsernameNumber.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  /**
   * Tests that an administrator is not created when invalid fields are given
   * @throws IOException An IOException triggered by the reading of the file to the object
   */
  @Test
  public void administratorJSONImportFailInvalidUserAttributes() throws IOException {
    File adminFile = fileFinder(adminFiles, "SingleAdminIncorrectInvalidUserAttributes.json");
    ObjectMapper mapper = createObjectMapper();
    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
    HttpStatus statusCode = importController.singleImportAdmin(importedAdmin, null).getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }


  //--- Donor Receiver CSV Importing Expected Success ---


  /**
   * Checks for success when valid csv file given with one user in it
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVFileSuccessOneEntry() throws IOException {
    File donorFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryCSV.csv");
    assertNotNull(donorFile);

    ResponseEntity importResponse = importController.importDonorCSV(donorFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    donorReceiverController.deleteDonorReceiver("THP4329");
  }


  /**
   * Checks for success when valid csv file given with many users in it
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVFilesSuccess() throws IOException {
    File donorsFile = fileFinder(donorReceiverCSVFiles, "FiveDonorReceiverEntriesCSV.csv");
    assertNotNull(donorsFile);

    ResponseEntity importResponse = importController.importDonorCSV(donorsFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    getResponse = donorReceiverController.getDonor("THP4330");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    getResponse = donorReceiverController.getDonor("THP4331");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    getResponse = donorReceiverController.getDonor("THP4332");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    getResponse = donorReceiverController.getDonor("THP4333");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    donorReceiverController.deleteDonorReceiver("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4330");
    donorReceiverController.deleteDonorReceiver("THP4331");
    donorReceiverController.deleteDonorReceiver("THP4332");
    donorReceiverController.deleteDonorReceiver("THP4333");
  }


  //--- Donor Receiver CSV Importing Input with Values Missing ---

  /**
   * Tests what happens when the NHI of the CSV donor is missing
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNullNHI() throws IOException {
    File birthDateFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullNHI.csv");
    assertNotNull(birthDateFile);

    ResponseEntity importResponse = importController.importDonorCSV(birthDateFile, null);
    donorReceiverController.deleteDonorReceiver("");
    assertEquals(HttpStatus.BAD_REQUEST, importResponse.getStatusCode());

    Map<String, String> failList = (Map<String, String>) importResponse.getBody();
    assertEquals(1, failList.size());
    assertEquals("INVALID: [Invalid NHI number]", failList.get(""));
  }


  /**
   * Checks for success when valid csv file given though the user has no first name
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingSuccessNoFirstName() throws IOException {
    File donorFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoFirstName.csv");
    assertNotNull(donorFile);

    ResponseEntity importResponse = importController.importDonorCSV(donorFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());

    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    assertEquals(1, successList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));
    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for success when valid csv file given though the user has no last name
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingSuccessNoLastName() throws IOException {
    File donorFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoLastName.csv");
    assertNotNull(donorFile);

    ResponseEntity importResponse = importController.importDonorCSV(donorFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());

    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    assertEquals(1, successList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));
    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for repairing and success when valid csv file given though the user has no middle name
   * Tests that a reason is given for the repair (and that it is the one expected).
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingSuccessNoMiddleName() throws IOException {
    File donorFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoMiddleName.csv");
    assertNotNull(donorFile);

    ResponseEntity importResponse = importController.importDonorCSV(donorFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());

    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));
    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a date of death
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoDeathDate() throws IOException {
    File deathDateFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoDeathDate.csv");
    assertNotNull(deathDateFile);

    ResponseEntity importResponse = importController.importDonorCSV(deathDateFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());

    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(0, repairList.size());
    assertEquals(1, successList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a date of birth
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoBirthGender() throws IOException {
    File birthGenderFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoBirthGender.csv");
    assertNotNull(birthGenderFile);

    ResponseEntity importResponse = importController.importDonorCSV(birthGenderFile, null);
    assertEquals(HttpStatus.ACCEPTED, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(0, successList.size());
    assertEquals(1, repairList.size());
    assertTrue(repairList.get("THP4329").contains("Missing BirthGender"));

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a gender
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoGender() throws IOException {
    File genderFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoGender.csv");
    assertNotNull(genderFile);

    ResponseEntity importResponse = importController.importDonorCSV(genderFile, null);
    assertEquals(HttpStatus.ACCEPTED, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(0, successList.size());
    assertEquals(1, repairList.size());
    assertEquals(1, repairList.get("THP4329").size());
    assertEquals("Missing Gender", repairList.get("THP4329").get(0));

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a blood type
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoBloodType() throws IOException {
    File bloodTypeFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoBloodType.csv");
    assertNotNull(bloodTypeFile);

    ResponseEntity importResponse = importController.importDonorCSV(bloodTypeFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a height
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoHeight() throws IOException {
    File heightFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoHeight.csv");
    assertNotNull(heightFile);

    ResponseEntity importResponse = importController.importDonorCSV(heightFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a weight
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNoWeight() throws IOException {
    File weightFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNoWeight.csv");
    assertNotNull(weightFile);

    ResponseEntity importResponse = importController.importDonorCSV(weightFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }


  /**
   * Checks for importing a donor without a street number
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactStreetNumberNull() throws IOException {
    File streetNumberFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullStreetNumber.csv");
    assertNotNull(streetNumberFile);

    ResponseEntity importResponse = importController.importDonorCSV(streetNumberFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals("Kipling", donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne());
  }


  /**
   * Checks for importing a donor without a street name
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactStreetNameNull() throws IOException {
    File streetNameFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullStreetName.csv");
    assertNotNull(streetNameFile);

    ResponseEntity importResponse = importController.importDonorCSV(streetNameFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());
    assertEquals(UserAccountStatus.VALID, successList.get("THP4329"));

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals("306", donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne());
  }


  /**
   * Checks for importing a donor without a neighbourhood
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactNeighbourhoodNull() throws IOException {
    File neighbourhoodFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullNeighbourhood.csv");
    assertNotNull(neighbourhoodFile);

    ResponseEntity importResponse = importController.importDonorCSV(neighbourhoodFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getAddress().getSuburb());
  }


  /**
   * Checks for importing a donor without a city
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactCityNull() throws IOException {
    File contactCityFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullCity.csv");
    assertNotNull(contactCityFile);

    ResponseEntity importResponse = importController.importDonorCSV(contactCityFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getAddress().getCity());
  }


  /**
   * Checks for importing a donor without a region
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactRegionNull() throws IOException {
    File regionFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullRegion.csv");
    assertNotNull(regionFile);

    ResponseEntity importResponse = importController.importDonorCSV(regionFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getAddressRegion());
  }


  /**
   * Checks for importing a donor without a post code
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactZipCodeNull() throws IOException {
    File zipCodeFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullZipCode.csv");
    assertNotNull(zipCodeFile);

    ResponseEntity importResponse = importController.importDonorCSV(zipCodeFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getAddress().getPostCode());
  }


  /**
   * Checks for importing a donor without a country
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactCountryNull() throws IOException {
    File countryFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullCountry.csv");
    assertNotNull(countryFile);

    ResponseEntity importResponse = importController.importDonorCSV(countryFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getAddress().getCountryCode());
  }


  /**
   * Checks for importing a donor without a home number
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactHomeNumberNull() throws IOException {
    File homeNumberFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullHomeNumber.csv");
    assertNotNull(homeNumberFile);

    ResponseEntity importResponse = importController.importDonorCSV(homeNumberFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getHomeNum());
  }


  /**
   * Checks for importing a donor without a mobile number
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactMobileNull() throws IOException {
    File mobilNumFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullMobile.csv");
    assertNotNull(mobilNumFile);

    ResponseEntity importResponse = importController.importDonorCSV(mobilNumFile, null);
    assertEquals(HttpStatus.OK, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(1, successList.size());
    assertEquals(0, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getMobileNum());
  }


  /**
   * Checks for importing a donor without an email address
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidContactEmailNull() throws IOException {
    File emailFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullEmail.csv");
    assertNotNull(emailFile);

    ResponseEntity importResponse = importController.importDonorCSV(emailFile, null);
    assertEquals(HttpStatus.ACCEPTED, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(0, successList.size());
    assertEquals(1, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getEmail());
  }


  /**
   * Checks for importing a donor without a birth date (expected failure)
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingNullBirthDate() throws IOException {
    File birthDateFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryNullBirthDate.csv");
    assertNotNull(birthDateFile);

    ResponseEntity importResponse = importController.importDonorCSV(birthDateFile, null);
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.BAD_REQUEST, importResponse.getStatusCode());

    Map<String, String> failList = (Map<String, String>) importResponse.getBody();
    assertEquals(1, failList.size());
    assertEquals("INVALID: [Missing date of birth]", failList.get("THP4329"));
  }


  //--- Donor Receiver CSV Importing Input with Invalid Values In a Number of Cases ---


  /**
   * Checks for importing a donor with an invalid nhi (expected failure)
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidNHI() throws IOException {
    File nhiFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryInvalidNHI.csv");
    assertNotNull(nhiFile);

    ResponseEntity importResponse = importController.importDonorCSV(nhiFile, null);
    donorReceiverController.deleteDonorReceiver("THP4329B");
    assertEquals(HttpStatus.BAD_REQUEST, importResponse.getStatusCode());

    Map<String, String> failList = (Map<String, String>) importResponse.getBody();
    assertEquals(1, failList.size());
    assertEquals("INVALID: [Invalid NHI number]", failList.get("THP4329B"));
  }


  /**
   * Checks for importing a donor with an invalid first name (expected failure)
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidFirstName() throws IOException {
    File firstNameFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryInvalidFirstName.csv");
    assertNotNull(firstNameFile);

    ResponseEntity importResponse = importController.importDonorCSV(firstNameFile, null);
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.BAD_REQUEST, importResponse.getStatusCode());

    Map<String, String> failList = (Map<String, String>) importResponse.getBody();
    assertEquals(1, failList.size());
    assertEquals("POOR: [Invalid First Name]", failList.get("THP4329"));
  }


  /**
   * Checks for importing a donor with an invalid last name (expected failure)
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidLastName() throws IOException {
    File lastNameFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryInvalidLastName.csv");
    assertNotNull(lastNameFile);

    ResponseEntity importResponse = importController.importDonorCSV(lastNameFile, null);
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.BAD_REQUEST, importResponse.getStatusCode());

    Map<String, String> failList = (Map<String, String>) importResponse.getBody();
    assertEquals(1, failList.size());
    assertEquals("POOR: [Invalid Last Name]", failList.get("THP4329"));
  }


  /**
   * Checks for importing a donor with an invalid email address
   * The user that was modified is recorded and the email address to be stored is set to null
   * @throws IOException Thrown if there's an error reading the file.
   */
  @Test
  public void donorReceiverCSVImportingInvalidEmailAddress() throws IOException {
    File emailFile = fileFinder(donorReceiverCSVFiles, "SingleDonorReceiverEntryInvalidEmail.csv");
    assertNotNull(emailFile);

    ResponseEntity importResponse = importController.importDonorCSV(emailFile, null);
    assertEquals(HttpStatus.ACCEPTED, importResponse.getStatusCode());
    List responseList = (LinkedList) importResponse.getBody();
    Map<String, UserAccountStatus> successList = (Map<String, UserAccountStatus>) responseList.get(0);
    Map<String, List<String>> repairList = (Map<String, List<String>>) responseList.get(1);
    assertEquals(0, successList.size());
    assertEquals(1, repairList.size());

    ResponseEntity getResponse = donorReceiverController.getDonor("THP4329");
    donorReceiverController.deleteDonorReceiver("THP4329");
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    DonorReceiver donorReceiver = (DonorReceiver) getResponse.getBody();
    assertEquals("", donorReceiver.getContactDetails().getEmail());
  }
}
