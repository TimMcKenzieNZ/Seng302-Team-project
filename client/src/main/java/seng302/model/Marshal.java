package seng302.model;

import static seng302.controllers.ImportUsersController.createAlertWithOptOut;
import static seng302.model.person.DonorReceiver.formatDateTimeToString;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.web.client.HttpClientErrorException;
import seng302.App;
import seng302.controllers.ImportUsersReportController;
import seng302.model.enums.ADDRESSES;
import seng302.model.import_export_strategies.UserImport;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.person.User;
import seng302.model.person.UserAccountStatus;
import seng302.model.person.UserValidationReport;
import seng302.model.person.UserValidator;
import seng302.services.PostTask;
import seng302.services.SyncGetTask;


public class Marshal {

  public static Session session =App.getCurrentSession();

  /**
   * A collection of errors that occurred in the importing of accounts
   */
  public static Collection<String> importException = new ArrayList<>();

  /**
   * A collection of file name strings which document all the user json files that were not imported
   * correctly into the database.
   */
  private Collection<UserValidationReport> failedImports;

  /**
   * A collection of file name strings which document all the user json files that were imported
   * correctly into the database.
   */
  private Collection<UserValidationReport> successfulImports;

  /**
   * A linked Hash map of file name strings which document all the user json files that were
   * imported correctly into the database but the user already exists in the database.
   */
  private Map<String, UserValidationReport> duplicateImports;

  /**
   * A collection of file name strings which document all the user json files that were not exported
   * correctly to file.
   */
  private Collection<String> failedExports;

  /**
   * A collection of file name strings which document all the user json files that were exported
   * correctly to file.
   */
  private Collection<String> successfulExports;

  private static List<String> marshalLog = new ArrayList<>();


  /**
   * The set of user profiles that are the result of an import operation. These could be
   * administrators, clinicians, or donor/receivers.
   */
  private Set<User> users;


  /**
   * ArrayList of imported users that already exist in the database.
   */
  private List<User> duplicateUsers;


  /**
   * A Linked hash map of all known Users (of type DonorReceiver OR Clinician OR Administrator) in
   * the database.
   */
  private Map<String, User> currentUsers;


  /**
   * Strategy to select what type of data is going to be imported
   */
  private UserImport importStrategy;

  private static final String jsonFileEnding = ".json";

  private static final String prodDonorReceiverString = "donorReceivers";

  private static final String prodClinicianString = "clinicians";

  private static final String prodAdminString = "administrators";

  private static final String prodLogString = "systemLog";

  private static final String testDonorReceiverString = "../donorReceivers";

  private static final String testClinicianString = "../clinicians";

  private static final String testAdminString = "../administrators";

  private static final String testLogString = "../systemLog";

  private static String donorReceiverString = prodDonorReceiverString;

  private static String clinicianString = prodClinicianString;

  private static String adminString = prodAdminString;

  private static String logString = prodLogString;

  /**
   * Constructor of Marshall class for use in importing new Users. the currentUsers list is set to
   * the given array list.
   *
   * @param users a linked hash map of the current users (Either DonorReceivers, Clinicians, or
   * Admins).
   */
  public Marshal(Map<String, User> users) {

    currentUsers = users;

    failedImports = new ArrayList<>();

    failedExports = new ArrayList<>();

    successfulImports = new ArrayList<>();

    successfulExports = new ArrayList<>();

    duplicateUsers = new ArrayList<>();

    duplicateImports = new LinkedHashMap<>();

  }

  /**
   * Creates a report from the exceptions that occurred during importing files
   */
  public void generateReportsFromExceptions() {
    for (String str : importException) {
      String array[] = str.split(",");
      UserValidationReport report = new UserValidationReport(array[0]);
      report.setAccountStatus(UserAccountStatus.INVALID);
      report.addIssue(array[2]);
      failedImports.add(report);
    }

  }


  /**
   * Basic constructor for the Marshal class to instantiate import/export lists to document
   * successful/failed import/exporting.
   */
  public Marshal() {

    failedImports = new ArrayList<>();

    failedExports = new ArrayList<>();

    successfulImports = new ArrayList<>();

    successfulExports = new ArrayList<>();

    duplicateUsers = new ArrayList<>();

    duplicateImports = new LinkedHashMap<>();

  }

  /**
   * An overloaded constructor to help with testing. Inputs a set of users to allow tests to test
   * private variables. Only called in testing.
   *
   * @param users Set of the current users
   */
  public Marshal(Set<User> users) {
    this.users = users;
  }

  public Collection<UserValidationReport> getFailedImports() {
    return failedImports;
  }

  public Collection<UserValidationReport> getSuccessfulImports() {
    return successfulImports;
  }

  public Map<String, UserValidationReport> getDuplicateImports() {
    return duplicateImports;
  }

  public List<User> getDuplicateUsers() {
    return duplicateUsers;
  }


  public void setCurrentUsers(Map<String, User> currentUsers) {
    this.currentUsers = currentUsers;
  }


  /**
   * Empties the marshall export and import array lists.
   */
  public void clearImportAndExportLists() {
    failedImports.clear();
    failedExports.clear();
    successfulExports.clear();
    successfulImports.clear();
  }


  /**
   * Takes the name of a file as a String and determines if a JSON file with that name exists in the
   * base directory.
   *
   * @param fileName The name of the file.
   * @return True if the file exists, false otherwise.
   */
  public static boolean fileExists(String fileName) {

    File file = new File(fileName + jsonFileEnding);
    return file.isFile();

  }


  /**
   * Creates the directory given by the string dir if it does not already exist.
   *
   * @param dir A string of the directory to be made. Will be on of "administrator', 'clinicians',
   * 'donorReceivers', or 'systemLog'.
   */
  public static void createDirectory(String dir) {

    File directory = new File(dir);

    // Create directory if no file exists with the name, or if it exists and is not a directory.
    if (!directory.exists() || (directory.exists() && !directory.isDirectory())) {

      directory.mkdir();

    }

  }


  /**
   * DEPRECATED
   *
   * Transforms an ArrayList of donorReceivers into a series of JSON files, each one corresponding
   * to an individual acount.
   *
   * @param donorReceivers The ArrayList to be exported.
   * @throws IOException Occurs when Jackson or Java IO fails.
   */
  public static void exportAccounts(Map<String, DonorReceiver> donorReceivers) throws IOException {

    ObjectMapper mapper = UserImport.createMapper();
    String exportPath = donorReceiverString + File.separator;
    createDirectory(donorReceiverString); // If it does not already exist.


    // For each account, write to an independent file in the donorReceivers directory.
    for (DonorReceiver donorReceiver : donorReceivers.values()) {

      File file = new File(exportPath + donorReceiver.getUserName() + ".json");
      mapper.writeValue(file, donorReceiver);

      // Log export.
      String time = formatDateTimeToString(LocalDateTime.now());
      marshalLog.add("Exported donorReceiver '" + donorReceiver.getUserName() + "' to " +
          "file. Change made at: " + time);

    }

  }


  /**
   * DEPRECATED
   * Imports all files located within the accounts directory.
   *
   * @return The accounts stored in an ArrayList format.
   * @throws IOException When the account files could not be imported.
   */
  public static Map<String, DonorReceiver> importAccounts() throws IOException {
    // Retrieve list of files in donorReceivers directory.
    File directory = new File(donorReceiverString);
    File[] accountFiles = directory.listFiles();

    // For each account file, import the data to donorReceivers.
    ObjectMapper mapper = UserImport.createMapper();
    Map<String, DonorReceiver> donorReceivers = new LinkedHashMap<String, DonorReceiver>();

    // Handle case where no files have been exported before.
    if (accountFiles != null) {
      for (int index = 0; index < accountFiles.length; index++) {
        String fileName = "";
        try {
          File accountFile = accountFiles[index];
          fileName = accountFile.getName();
          DonorReceiver importedDonorReceiver = mapper.readValue(accountFile, DonorReceiver.class);
          importedDonorReceiver.populateIllnessLists();
          donorReceivers.put(importedDonorReceiver.getUserName(), importedDonorReceiver);
          // Log import.
          String time = formatDateTimeToString(LocalDateTime.now());
          marshalLog.add("Imported donorReceiver '" + importedDonorReceiver.getUserName() + "' from file. Change made at: " + time);
        } catch (IOException exception) {
          System.err.println("ERROR: Could not import '" + fileName + "'. It may not be a valid account save file.");
        }
      }
    }
    return donorReceivers;

  }


  /**
   * DEPRECATED
   * Saves the system log to file.
   *
   * @param systemLog The ArrayList of log strings to be exported.
   * @throws IOException Occurs when Jackson or Java IO fails.
   */
  public static void exportSystemLog(ObservableList systemLog) throws IOException {

    ObjectMapper mapper = UserImport.createMapper();
    String exportPath = logString + File.separator;
    createDirectory(logString); // If it does not already exist.
    File file = new File(exportPath + "systemLog.json");
    mapper.writeValue(file, systemLog);
    // Log export.
    String time = formatDateTimeToString(LocalDateTime.now());
    marshalLog.add("Exported system log to file. Change made at: " + time);
  }


  /**
   * DEPRECATED
   * Imports the System Log from the systemlog directory.
   *
   * @return An Array list of log strings.
   */
  public static ArrayList<LogEntry> importSystemLog() {

    // Retrieve the system log file in systemLog directory.
    File file = new File(logString + "/systemLog.json");

    // Import the System log.
    ObjectMapper mapper = UserImport.createMapper();
    ArrayList<LogEntry> importedSystemLog = new ArrayList<>();
    // Handle case where no file have been exported before.
    if (file != null) {
      try {
        JavaType type = mapper.getTypeFactory()
            .constructCollectionType(ArrayList.class, LogEntry.class);
        importedSystemLog = mapper.readValue(file, type);
        // Log import.
        String time = formatDateTimeToString(LocalDateTime.now());
        marshalLog
            .add("Imported DonorReceiver Manager system log from file. Change made at: " + time);

      } catch (IOException exception) {
        System.out.println(exception.getMessage());
        System.out.println("ERROR: Could not import " + logString + "/systemLog.json"
            + ". It may not be a valid DonorReceiver Manager save file.");
      }
    }
    return importedSystemLog;
  }


  /**
   * DEPRECATED
   *
   * Imports all files located within the clinicians directory.
   *
   * @return The clinicians stored in an ArrayList format.
   */
  public static Map<String, Clinician> importClinicians() {

    // Retrieve list of files in clinicians directory.
    File directory = new File(clinicianString);
    File[] clinicianFiles = directory.listFiles();

    // For each clinician file, import the data to clinicians.
    ObjectMapper mapper = UserImport.createMapper();
    Map<String, Clinician> clinicians = new LinkedHashMap<>();

    // Handle case where no files have been exported before.
    if (clinicianFiles != null) {

      for (int index = 0; index < clinicianFiles.length; index++) {

        String fileName = "";

        try {

          File clinicianFile = clinicianFiles[index];
          fileName = clinicianFile.getName();
          Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
          clinicians.put(importedClinician.getUserName(), importedClinician);

          // Log import.
          String time = formatDateTimeToString(LocalDateTime.now());
          marshalLog.add("Imported clinician '" + importedClinician.getUserName() + "' from " +
                  "file. Change made at: " + time);
        } catch (IOException exception) {
          System.err.println("ERROR: Could not import '" + fileName + "'. It may not be a valid clinician save file.");

        }
      }
    }
    return clinicians;
  }


  /**
   * DEPRECATED
   *
   * Transforms an ArrayList of clinicians into a series of JSON files, each one corresponding to an
   * individual clinician.
   *
   * @param clinicians The ArrayList to be exported.
   * @throws IOException Occurs when Jackson or Java IO fails.
   */
  public static void exportClinicians(Map<String, Clinician> clinicians) throws IOException {

    ObjectMapper mapper = UserImport.createMapper();
    String exportPath = clinicianString + File.separator;
    createDirectory(clinicianString); // If it does not already exist.

    // For each clinician, write to an independent file in the clinicians directory.
    for (Clinician clinician : clinicians.values()) {

      File file = new File(exportPath + clinician.getUserName() + ".json");
      mapper.writeValue(file, clinician);

      // Log export.
      String time = formatDateTimeToString(LocalDateTime.now());
      marshalLog.add("Exported clinician '" + clinician.getUserName() + "' to " +
              "file. Change made at: " + time);
    }
  }


  /**
   * Transforms an ArrayList of administrators into a series of JSON files, each one corresponding
   * to an individual admin.
   *
   * @param admins The ArrayList to be exported.
   * @throws IOException Occurs when Jackson or Java IO fails.
   */
  public static void exportAdministrators(Map<String, Administrator> admins) throws IOException {

    ObjectMapper mapper = UserImport.createMapper();
    String exportPath = adminString + File.separator;
    createDirectory(adminString); // If it does not already exist.

    // For each admin, write to an independent file in the administrators directory.
    for (Administrator admin : admins.values()) {

      File file = new File(exportPath + admin.getUserName() + ".json");
      mapper.writeValue(file, admin);

      // Log export.
      String time = formatDateTimeToString(LocalDateTime.now());
      marshalLog.add("Exported admin '" + admin.getUserName() + "' to " +
              "file. Change made at: " + time);
    }
  }


  /**
   * Imports all files located within the admins directory.
   *
   * @return The admins stored in an ArrayList format.
   */
  public static Map<String, Administrator> importAdministrators() {

    // Retrieve list of files in clinicians directory.
    File directory = new File(adminString);
    File[] adminFiles = directory.listFiles();

    // For each clinician file, import the data to clinicians.
    ObjectMapper mapper = UserImport.createMapper();
    Map<String, Administrator> admins = new LinkedHashMap<String, Administrator>();

    // Handle case where no files have been exported before.
    if (adminFiles != null) {
      for (int index = 0; index < adminFiles.length; index++) {
        String fileName = "";
        try {
          File adminFile = adminFiles[index];
          fileName = adminFile.getName();
          Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
          admins.put(importedAdmin.getUserName(), importedAdmin);

          // Log import.
          String time = formatDateTimeToString(LocalDateTime.now());
          marshalLog.add("Imported admin '" + importedAdmin.getUserName() + "' from file. Change made at: " + time);
        } catch (IOException exception) {
          System.err.println("ERROR: Could not import '" + fileName + "'. It may not be a valid admin save file.");
        }
      }
    }
    return admins;
  }

  /**
   * Sets the Marshall import strategy to the strategy specified.
   *
   * @param userImport Import strategy
   * @throws NullPointerException a null pointer exception if the client did not provide a
   * strategy.
   */
  public void setImportStrategy(UserImport userImport) throws NullPointerException {
    if (userImport == null) {
      throw new NullPointerException();
    } else {
      this.importStrategy = userImport;
    }
  }


  /**
   * Imports and validates the given User files from the given directory/locationString and combines
   * the imported users with the currentUsers of the Marshall class if they are not duplicates and
   * have valid attributes. The type of user to be imported is given by the UserType string.
   *
   * @param userType A string of the type of user to be imported. One of 'donor', 'clinician', or
   * 'admin'.
   * @param locationString A string of the file path of the user file to be imported.
   * @param directory the file to be imported.
   * @return returns a linked HashMap of the currentUsers plus any successfully imported User files.
   * @throws IOException Thrown if there was an issue with the file import (e.g. missing file)
   * @throws IllegalArgumentException Thrown if the given userType is not what is expected or File
   * formatting was wrong (not a .csv or not a .JSON etc) or the reader/format was null
   */
  public LinkedHashMap<String, User> importer(String userType, String locationString,
      File directory) throws IOException, IllegalArgumentException {
    users = new HashSet(importStrategy.importer(userType, locationString, directory));
    if (userType.equalsIgnoreCase("donor/receiver")) {
      LinkedHashMap<String, User> existingDonors = new LinkedHashMap<>(currentUsers);
      // validateAndAddDonors(existingDonors);
      return existingDonors;

    } else if (userType.equalsIgnoreCase("clinician")) {
      LinkedHashMap<String, User> existingClinicians = new LinkedHashMap<>(currentUsers);
      validateAndAddClinicians(existingClinicians);
      return existingClinicians;

    } else if (userType.equalsIgnoreCase("administrator")) {
      LinkedHashMap<String, User> existingAdministrators = new LinkedHashMap<>(currentUsers);
      validateAndAddAdmins(existingAdministrators);
      return existingAdministrators;

    } else {
      throw new IllegalArgumentException(
          "userType must be either 'donor/receiver', 'clinician', or 'administrator'.");
    }
  }

  /**
   * Takes in a given linkedHashMap of existing clinicians and for each clinician in the Marshall
   * 'users' set; validates the clinician's attributes, generates a validation report of the
   * results, and checks if the clinician already exists in the linkedHashMap. If not it is added to
   * the linkHashMap. If does exist, the clinician is added to the duplicateUsers arraylist instead.
   * If the report status is not INVALID then the report is added to the successfulImports list.
   * Otherwise it is added to the failedImports list.
   *
   * @param existingClinicians A linked hashmap of all the known donors in the database.
   */
  private void validateAndAddClinicians(LinkedHashMap<String, User> existingClinicians) {
    ArrayList<Clinician> clinicians = new ArrayList<>();
    for (Clinician clinician : clinicians) {
      UserValidator validator = new UserValidator(clinician, existingClinicians);
      UserValidationReport report = validator.getReport();
      addUserToCorrectCollection(report, clinician, existingClinicians);
    }
  }

  /**
   * Takes in a given linkedHashMap of existing admins and for each admin in the Marshall 'users'
   * set; validates the admin's attributes, generates a validation report of the results, and checks
   * if the admin already exists in the linkedHashMap. If not it is added to the linkHashMap. If
   * does exist, the admin is added to the duplicateUsers arraylist instead. If the report status is
   * not INVALID then the report is added to the successfulImports list. Otherwise it is added to
   * the failedImports list.
   *
   * @param existingAdmins A linked hashmap of all the known admins in the database.
   */
  private void validateAndAddAdmins(LinkedHashMap<String, User> existingAdmins) {
    ArrayList<Administrator> admins = new ArrayList<>();
    for (Administrator admin : admins) {
      UserValidator validator = new UserValidator(admin, existingAdmins);
      UserValidationReport report = validator.getReport();
      addUserToCorrectCollection(report, admin, existingAdmins);
    }
  }

  /**
   * Adds the User to the correct list as detailed in validateAndAdd methods. Created to ease
   * duplication
   *
   * @param report The report created by the validation check
   * @param user The user to add to th assigned collection
   */
  private void addUserToCorrectCollection(UserValidationReport report, User user,
      LinkedHashMap<String, User> existingUsers) {
    if (report.getAccountStatus() == UserAccountStatus.INVALID) {
      failedImports.add(report);
    } else if (report.getAccountStatus() == UserAccountStatus.EXISTS) {
      duplicateImports.put(report.getUsername(), report);
      duplicateUsers.add(user);
    } else {
      successfulImports.add(report);
      LogEntry logEntry = new LogEntry(user, AccountManager.getCurrentUser(), "Imported from file.",
          "", "");
      user.getModifications().add(logEntry);
      if (user instanceof DonorReceiver) {
        ((DonorReceiver) user).getUpdateLog().add(logEntry);
      }
      AccountManager.getSystemLog().add(logEntry);
      existingUsers.put(user.getUserName(), user);
    }
  }


  /**
   * Takes the Marshall 'users' set and instantiates an arrayList of administrators from it.
   *
   * @return An arrayList of imported administrator profiles.
   */
  public ArrayList<Administrator> returnAdministratorArrayList() {
    Administrator a;
    User b;
    ArrayList<Administrator> movedUsers = new ArrayList<>();
    Iterator iterator = users.iterator();
    while (iterator.hasNext()) {
      b = (User) iterator.next();
      if (b instanceof Administrator) {
        a = (Administrator) b;
        movedUsers.add(a);
      }
    }
    return movedUsers;
  }


  /**
   * Takes the Marshall 'users' set and instantiates an arrayList of clinicians from it.
   *
   * @return An arrayList of imported clinician profiles.
   */
  public ArrayList<Clinician> returnClinicianArrayList() {
    Clinician c;
    User b;
    ArrayList<Clinician> movedUsers = new ArrayList<>();
    Iterator iterator = users.iterator();
    while (iterator.hasNext()) {
      b = (User) iterator.next();
      if (b instanceof Clinician) {
        c = (Clinician) b;
        movedUsers.add(c);
      }
    }
    return movedUsers;
  }


  /**
   * Takes the Marshall 'users' set and instantiates an arrayList of donor/receivers from it.
   *
   * @return An arrayList of imported donorReceiver profiles.
   */
  public ArrayList<DonorReceiver> returnDonorReceiverArrayList() {
    DonorReceiver d;
    User b;
    ArrayList<DonorReceiver> movedUsers = new ArrayList<>();
    Iterator iterator = users.iterator();
    while (iterator.hasNext()) {
      b = (User) iterator.next();
      if (b instanceof DonorReceiver) {
        d = (DonorReceiver) b;
        movedUsers.add(d);
      }
    }
    return movedUsers;
  }


  /**
   * Converts the given ArrayList of users to a linked hashmap with the key-value pair being the
   * user's username and the user object.
   *
   * @param users an ArrayList of users; DonorReceivers, Clinicians, or Administrators
   * @return returns a LinkedHashMap of <user username, user> mappings
   */
  private LinkedHashMap<String, User> convertUserArrayListToLinkedHashmap(
      ArrayList<? extends User> users) {
    LinkedHashMap<String, User> mappedUsers = new LinkedHashMap<>(1000000);
    for (User user : users) {
      mappedUsers.put(user.getUserName(), user);
    }
    return mappedUsers;

  }


  /**
   * Copies some of the given original user's attributes with the new user and return the merged
   * user. This is for .csv imported users who are missing certain attributes and are duplicates of
   * existing users in the database.
   *
   * @param original the original existing DonorReceiver in the database.
   * @param nova the new DonorReceiver who is the recipient of the original user's attributes.
   * @return the merged DonorReceiver with copied attributes
   */
  public DonorReceiver mergeOriginalDonorWithNewDonor(DonorReceiver original, DonorReceiver nova) {
    nova.setCreationDate(original.getCreationDate());
    nova.setPassword(original.getPassword());
    nova.setMiddleName(original.getMiddleName());
    nova.setModifications(original.getModifications());
    nova.setEmergencyContactDetails(original.getEmergencyContactDetails());
    nova.setTitle(original.getTitle());
    nova.setDonorOrganInventory(original.getDonorOrganInventory());
    nova.setRequiredOrgans(original.getRequiredOrgans());
    nova.setPreferredName(original.getPreferredName());
    nova.setLivedInUKFlag(original.getLivedInUKFlag());
    nova.setActive(original.isActive());
    nova.setReceiver(original.getReceiver());
    nova.setMasterIllnessList(original.getMasterIllnessList());
    nova.populateIllnessLists();
    nova.setMedications(original.getMedications());
    nova.setMedicalProcedures(original.getMedicalProcedures());
    nova.getUserAttributeCollection()
        .setBodyMassIndexFlag(original.getUserAttributeCollection().getBodyMassIndexFlag());
    try {
      nova.getUserAttributeCollection()
          .setSmoker(original.getUserAttributeCollection().getSmoker());
    } catch (NullPointerException e) {
      nova.getUserAttributeCollection().setSmoker(false);
    }
    nova.getUserAttributeCollection()
        .setBloodPressure(original.getUserAttributeCollection().getBloodPressure());
    nova.getUserAttributeCollection()
        .setAlcoholConsumption(original.getUserAttributeCollection().getAlcoholConsumption());
    nova.setModifications(original.getUpdateLog());
    nova.setUpdateLog(original.getUpdateLog());
    LogEntry logEntry = new LogEntry(nova, AccountManager.getCurrentUser(),
        "Merged attributes with existing profile.", "Previous user", "Imported user");
    nova.getModifications().add(logEntry);
    AccountManager.getSystemLog().add(logEntry);

    return nova;
  }


  public static void setProductionImport() {
    donorReceiverString = prodDonorReceiverString;
    clinicianString = prodClinicianString;
    adminString = prodAdminString;
    logString = prodLogString;
  }

  public static void setTestImport() {
    donorReceiverString = testDonorReceiverString;
    clinicianString = testClinicianString;
    adminString = testAdminString;
    logString = testLogString;
  }
}
