package seng302.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seng302.App;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng302.model.person.Address;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.ContactDetails;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.person.User;
import seng302.model.person.UserValidator;

public class AccountManager {

  @JsonIgnore
  private static User currentUser;
  @JsonIgnore
  private static DonorReceiver selectedAccount;
  @JsonIgnore
  private Scanner input;

  @JsonIgnore
  private static String token;

  public static String getToken() {
    return token;
  }

  public static void setToken(String token) {
    AccountManager.token = token;
  }

  @JsonIgnore
  private Map<String, DonorReceiver> donorReceivers = new LinkedHashMap();
  @JsonIgnore
  private Map<String, DonorReceiver> donorReceiverMasterList = new LinkedHashMap();

  @JsonIgnore
  private Map<String, Clinician> clinicians = new LinkedHashMap();
  @JsonIgnore
  private Map<String, Clinician> clinicianMasterList = new LinkedHashMap();
  @JsonIgnore
  public Map<String, Clinician> clinicianTwilightZone = new LinkedHashMap();

  @JsonIgnore
  private Map<String, Administrator> administrators = new LinkedHashMap();
  @JsonIgnore
  private Map<String, Administrator> administratorMasterList = new LinkedHashMap();
  @JsonIgnore
  public Map<String, Administrator> adminTwilightZone = new LinkedHashMap();

  /**
   * A HashMap to store accounts to be deleted before the application is saved.
   */
  @JsonIgnore
  private Map<String, DonorReceiver> twilightZone = new LinkedHashMap();

  public Map<String, DonorReceiver> getTwilightZone() {
    return twilightZone;
  }

  /**
   * A Hashmap to store all the accounts that are waiting to receive an organ.
   */
  private Map<String, DonorReceiver> transplantWaitingList = new LinkedHashMap();


  public static User getCurrentUser() {
    return currentUser;
  }




  /**
   * Sets the current user of the application to the User instance specified by newCurrentUser. Also
   * closes all child windows for security reasons.
   *
   * @param newCurrentUser A User instance which is the new current user.
   */
  public static void setCurrentUser(User newCurrentUser) {

    currentUser = newCurrentUser;
    App.closeChildWindows();

  }


  private static ObservableList<LogEntry> systemLog = FXCollections.observableArrayList();
  private static ObservableList<String> statusUpdates = FXCollections.observableArrayList();

  public ArrayList<DonorReceiver> getDonorReceiversArrayList() {
    ArrayList arrayList = new ArrayList(donorReceivers.values());
    return arrayList;
  }

  public Map<String, DonorReceiver> getDonorReceivers() {
    return donorReceivers;
  }

  public Collection<DonorReceiver> getTransplantWaitingListArrayList() {
    return new ArrayList(transplantWaitingList.values());
  }

  public Map<String, DonorReceiver> getTransplantWaitingList() {
    return transplantWaitingList;
  }

  public Collection<Administrator> getAdministratorsArrayList() {
    return new ArrayList(administrators.values());
  }

  public ArrayList<Clinician> getCliniciansArrayList() {
    return new ArrayList(clinicians.values());
  }

  public Map<String, Administrator> getAdministrators() {
    return administrators;
  }

  public void setAdministrators(Map<String, Administrator> administrators) {
    this.administrators = administrators;
  }

  /**
   * A JSON constructor for the DonorReceiver Manager which imports the system log from file.
   *
   * @param systemLog An arraylist of LogEntry's.
   */
  @JsonCreator
  public AccountManager(@JsonProperty("systemLog") ArrayList<LogEntry> systemLog) {
    AccountManager.systemLog = FXCollections.observableArrayList(systemLog);
  }

  /**
   * Default empty constructor for Account Manager.
   */
  public AccountManager() {
  }







  /**
   * Taks three String arguments which will be passed to a new LogEntry instance, and a
   * DonorReceiver to which the new instance will be appended. The new LogEntry will also be added
   * to the SystemLog.
   *
   * @param modifyingAccount The account making the modification.
   * @param donorReceiver The DonorReceiver object which has been changed.
   * @param valChanged The new value assigned to the attribute specified by changedVal.
   * @param originalVal The old value assigned to the attribute specified by changedVal.
   * @param changedVal The attribute which has been changed.
   */
  private void logDonorReceiverChange(User modifyingAccount, DonorReceiver donorReceiver,
      String valChanged, String originalVal, String changedVal) {

    if (modifyingAccount == null) {

      // The account is being modified by itself.
      modifyingAccount = donorReceiver;

    }

    LogEntry logEntry = new LogEntry(donorReceiver, modifyingAccount, valChanged, originalVal,
        changedVal);
    donorReceiver.logChange(logEntry);
    getSystemLog().add(logEntry);

  }


  /**
   * Restores data of an old donorReceiver if it has been reactivated and logs the reactivation.
   * Otherwise Logs activation of a new donorReceiver.
   *
   * @param modifyingAccount The user who created or is reactivating the new user.
   * @param donorReceiver A newly created DonorReceiver to be checked if it has been deactivated in
   * the past.
   * @return the DonorReceiver with a log of reactivation or creation
   */
  public DonorReceiver reactivateOldAccountIfExists(User modifyingAccount,
      DonorReceiver donorReceiver) {

    DonorReceiver existing = donorReceiverMasterList.get(donorReceiver.getUserName());

    // Check if account already exists and is inactive.
    if (existing != null && !existing.isActive()) {

      logDonorReceiverChange(modifyingAccount, donorReceiver, "reactivated", "inactive", "active");
      donorReceiver.setActive(true);
      return donorReceiver;

    }

    logDonorReceiverChange(modifyingAccount, donorReceiver, "created", "", "active");
    return donorReceiver;

  }






  /**
   * Calls the exportAccounts method and returns the scanner.
   *
   * @return The scanner that reads all commands.
   */
  public String exporter() {

    return exportAccounts();

  }


  /**
   * Calls the importAccounts method and returns the scanner.
   *
   * @return Message for user
   */
  public String importer() {

    return importAccounts();

  }


  /**
   * Takes a string inputted by the user, and parses the first name within the string.
   *
   * @param fullName The full name of the person
   * @return The first name of the account holder
   */
  public static String parseGivenName(String fullName) {
    try {
      return fullName.substring(0, fullName.indexOf(' '));
    } catch (IndexOutOfBoundsException e) {
      if (fullName.equals("")) {
        return null;
      } else {
        return fullName;
      }
    }
  }

  /**
   * Takes a string inputted by the user, and parses the middle names
   *
   * @param fullName The full name of the person
   * @return the middle names of the account holder
   */
  public static String parseOtherNames(String fullName) {
    String otherName = fullName.substring(fullName.indexOf(' ') + 1, fullName.lastIndexOf(' ') + 1);
    otherName = otherName.trim();
    return otherName;
  }

  /**
   * takes a string inputted by the user, and parses the last name.
   *
   * @param fullName the full name of the person
   * @return the last name of the account holder
   */
  public static String parseLastName(String fullName) {
    String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
    lastName = lastName.trim();
    if (lastName.equals(fullName)) {
      lastName = "";
    }
    return lastName;
  }

  /**
   * Checks if the NHI given is already in use within the database
   *
   * @param NHI The national health index number of the account holder
   * @return A boolean variable  indicating whether the number is already being used.
   */
  public boolean checkUsedNHI(String NHI) {
    int max = donorReceivers.size();
    boolean found = false;
    DonorReceiver person;
    if (max != 0) {
      for (DonorReceiver donorReceiver : donorReceivers.values()) {
        person = donorReceiver;
        if (person.getUserName().equals(NHI)) {
          found = true;
        }
      }
    }
    return found;
  }




  /**
   * Checks whether the given NHI is valid. An NHI code is valid if it meets the following
   * conditions: - It is exactly 7 characters long. - The first 3 are uppercase alphabetical
   * letters, and no letter can be 'I' or 'O'. - The next 3 are any digits. - The last character is
   * a non-zero digit. - The NHI number isn't already present in the List of accounts. That is,
   * there are no duplicate NHI numbers and therefore duplicate accounts.
   *
   * @param nhi A String representing the donor's unique National Health Index code
   * @return a boolean, 'true' if the NHI is valid, 'false' otherwise.
   */
  public boolean validateNHI(String nhi) {
    for (DonorReceiver account : donorReceiverMasterList.values()) {
      if (account.getUserName().equals(nhi)) {
        if (!account.isActive()) {
          System.out.println("WARNING: NHI code already used for deactivated account.");
          System.out
              .println("Reactivated account. Save to make reactivation and changes permanent.");
          System.out.println(
              "Note that previous unsaved changes to the deactivated account will have been lost.");
        } else {
          return false;
        }
      }
    }
    return UserValidator.checkNHIRegex(nhi);
  }


  /**
   * Checks whether an administrator username is valid or not (Username already taken or invalid
   * characters in username)
   *
   * @param username The username the admin wishes to use when logging in
   * @return boolean A boolean for whether the username is valid or not
   */
  public boolean validateAdminUsername(String username) {
    String regularExpressionCharacters = "^[a-zA-Z0-9_]{3,30}$";
    Pattern patternCharacters = Pattern.compile(regularExpressionCharacters);
    Matcher matcher = patternCharacters.matcher(username);

    if (matcher.matches()) {
      String containsLetter = "^(.*[a-zA-Z].*)$";
      Pattern containsLetterPattern = Pattern.compile(containsLetter);
      Matcher matcherLetter = containsLetterPattern.matcher(username);

      return matcherLetter.matches(); // Return true if there is a match otherwise false
    }
    return false;
  }


  /**
   * Prints all statistics (save log information) of every donor in the database (if there are
   * any).
   */
  @Override
  public String toString() {
    String string = "Donors:\n\n";
    if (donorReceivers.size() == 0) {
      string += "No donors to display.\n";
    }
    for (DonorReceiver donorReceiver : donorReceivers.values()) {
      string += donorReceiver.toString();
    }
    return string;
  }


  /**
   * Updates either the DonorReceiver, UserAttributeCollection, DonorOrganInventory attribute
   * depending on the value of the given object.
   *
   * @param modifyingAccount The user updating the donor/receiver.
   * @param donorReceiver a valid string NHI code to identify the donorReceiver to update.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'contacts'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   */
  public void updateObject(User modifyingAccount, DonorReceiver donorReceiver, String object,
      String attribute, String value) {
    if (object.equalsIgnoreCase("profile")) {
      donorReceiver.updateProfile(modifyingAccount, attribute, value);
    } else if (object.equalsIgnoreCase("attributes")) {
      donorReceiver.updateAttribute(modifyingAccount, attribute, value);
    } else if (object.equalsIgnoreCase("organs")) {
      donorReceiver.updateOrgan(modifyingAccount, "donor", attribute, value);
    } else if (object.equalsIgnoreCase("contacts")) {
      donorReceiver.updateContact(modifyingAccount, attribute, value);
    } else {
      System.out.println("ERROR: Unknown object " + object
          + ". Object should be 'profile', 'attributes', 'organs' or 'contacts'.");
    }
  }

  /**
   * Updates either the DonorReceiver, UserAttributeCollection, DonorOrganInventory attribute
   * depending on the value of the given object.
   *
   * @param modifyingAccount The account modifying the donor/receiver.
   * @param donorReceiver The donor/receiver being updated.
   * @param donReceiver a valid string NHI code to identify the donorReceiver to update.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'organs'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   */
  public void updateObject(User modifyingAccount, DonorReceiver donorReceiver, String object,
      String donReceiver, String attribute, String value) {
    if (object.equalsIgnoreCase("profile")) {
      donorReceiver.updateProfile(modifyingAccount, attribute, value);
    } else if (object.equalsIgnoreCase("attributes")) {
      donorReceiver.updateAttribute(modifyingAccount, attribute, value);
    } else if (object.equalsIgnoreCase("organs")) {
      donorReceiver.updateOrgan(modifyingAccount, donReceiver, attribute, value);
    } else if (object.equalsIgnoreCase("contacts")) {
      String[] result = donorReceiver.getContactDetails().updateContact(attribute, value);
      if (result[1] != null) {
        if (value.equals("delete")) {
          value = "";
        }
        donorReceiver.logChange(
            new LogEntry(donorReceiver, modifyingAccount, "personal contact " + attribute,
                result[1], value));
      }
    } else if (object.equalsIgnoreCase("emergencyContacts")) {
      String[] result = donorReceiver.getEmergencyContactDetails().updateContact(attribute, value);
      if (result[1] != null) {
        donorReceiver.logChange(
            new LogEntry(donorReceiver, modifyingAccount, "emergency contact " + attribute,
                result[1], value));
      }
    } else {
      System.out.println("ERROR: Unknown object " + object
          + ". Object should be 'profile', 'attributes', 'organs', 'contacts' or 'emergencyContacts'.");
    }
  }


  /**
   * Prints certain details of the given donorReceiver depending on the specified object: -'all'
   * prints all the details of the donorReceiver. -'attributes' prints only the donorReceiver
   * attributes information. -'organs' prints only the accounts list of organs that will be donated.
   * ='log' prints all the logs in the update log of the given donorReceiver.
   *
   * @param donorReceiver a valid string NHI code to identify the donorReceiver.
   * @param object a string, either 'all', 'attributes', 'log', or 'organs'.
   */
  public void viewObject(DonorReceiver donorReceiver, String object) {
    String message = "";
    System.out.println("Accessing data for donor " + donorReceiver.getUserName() + "...");
    if (object.equalsIgnoreCase("all")) {
      message = donorReceiver.toString();
    } else if (object.equalsIgnoreCase("attributes")) {
      message = donorReceiver.getUserAttributeCollection().toString();
    } else if (object.equalsIgnoreCase("organs")) {
      message = donorReceiver.getDonorOrganInventory().toString();
    } else if (object.equalsIgnoreCase("contacts")) {
      message = donorReceiver.getContactDetails().toString();
    } else if (object.equalsIgnoreCase("log")) {
      message = donorReceiver.updateLogToString();
    } else {
      message = "ERROR: Unknown object " + object
          + ". Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n";
    }
    System.out.println(message);
  }


  /**
   * Performs identically to the issueDelete method, but does not require any input from the user.
   * It is called by controllers.
   *
   * @param modifyingAccount The account doing the deletion.
   * @param target The NHI number of the account to delete.
   */
  public void issueDeleteWithoutConfirmation(User modifyingAccount, String target) {

    DonorReceiver person = donorReceivers.get(target);
    if (person != null) {
      try {
        person.setActive(false);
        logDonorReceiverChange(modifyingAccount, person, "deleted", null, null);
        twilightZone.put(person.getUserName(), person);
        donorReceivers.remove(person.getUserName());
      } catch (IllegalArgumentException e) { // wrong action was given
        System.out.println(e.getMessage());
        System.exit(0); // probably overkill for this kind of error
      }

    }

  }


  /**
   * Prints either all the details of all the donors in the database, or prints selected information
   * about a single donor depending on the value of 'target'.
   *
   * @param target a string of the target to print. Will be either a valid NHI code or 'donors'.
   * @param object a string, either 'all', 'attributes', or 'organs'.
   */
  public void issueView(String target, String object) {

    if (target.equalsIgnoreCase("donors")) {
      if (object.equalsIgnoreCase("all")) {
        System.out.println(toString());
      } else if (object.equalsIgnoreCase("organs")) {

        for (DonorReceiver donorReceiver : donorReceivers.values()) {
          System.out.print("Organ Donation list for " + donorReceiver.getUserName() + ":\n");
          viewObject(donorReceiver, "organs");
        }
      } else if (object.equalsIgnoreCase("attributes")) {
        for (DonorReceiver donorReceiver : donorReceivers.values()) {
          System.out.print("Attribute list for " + donorReceiver.getUserName() + ":\n");
          viewObject(donorReceiver, "attributes");
        }
      } else {
        System.out.println(
            "ERROR: Unknown object " + object + ". " + ". For donors, object should be 'all'," +
                "'organs', or 'attributes'.\n");
      }
    } else {
      // Print information for a specific person. In this case, target is an NHI code.
      DonorReceiver person = donorReceivers.get(target);
      if (person != null) {
        viewObject(person, object);
      } else {
        // If person is null, ie: not found.
        System.out.println("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
      }
    }
  }


  /**
   * prints the details specified by the given object, of all donors in the database whose name
   * matches the given first and last name.
   *
   * @param first A string of the first name of the donor.
   * @param last A string of the last name of the donor.
   * @param object a string, either 'all', 'attributes', 'log', or 'organs'.
   */
  public void issueView(String first, String last, String object) {

    ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();

    for (DonorReceiver donorReceiver : donorReceivers.values()) {
      if (donorReceiver.getFirstName().equals(first) && donorReceiver.getLastName().equals(last)) {
        matches.add(donorReceiver);
      }
    }

    if (matches.size() == 0) {
      System.out.println(
          "ERROR: " + first + " " + last + " not found in database. Did you spell it right? " +
              "Try searching using the donor NHI code instead. Check the read me for a list of available commands\n");
    } else {
      System.out.println(matches.size() + " match(s) for the name " + first + " " + last + ".\n");
      for (DonorReceiver acc : matches) {
        viewObject(acc, object);
      }
    }
  }

  /**
   * Searches for accounts by name. Returns an arraylist containing matching accounts. If an empty
   * string is passed, it returns all accounts.
   *
   * @param value The value to search by.
   * @return A list of accounts containing the value in their name.
   */
  public ArrayList<DonorReceiver> getAccountsByName(String value) {

    // Handle special case where an empty string is the search value. Just return all the DonorReceivers in the
    // system.
    if (value.equals("")) {

      return new ArrayList<>(donorReceivers.values());

    }

    // Otherwise iterate through and search.
    ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();
    value = value.replaceAll("\\s", "").toLowerCase();

    for (DonorReceiver donorReceiver : donorReceivers.values()) {

      String fullName = donorReceiver.getFirstName() + donorReceiver.getMiddleName() + donorReceiver
          .getLastName();
      fullName = fullName.replaceAll("\\s", "").toLowerCase(); // Remove any whitespace.

      if (fullName.contains(value)) {

        matches.add(donorReceiver);

      }

    }

    return matches;

  }


  /**
   * Searches for accounts by NHI. Returns an ArrayList containing all matching DonorReceiver
   * objects, or all DonorReceiver objects if searching for an empty string.
   *
   * @param value The value to search by.
   * @return A list of accounts containing the value in their NHI.
   */
  public LinkedHashMap<String, DonorReceiver> getAccountsByNHI(String value) {

    // Handle special case where an empty string is the search value.
    if (value.equals("")) {

      return new LinkedHashMap<String, DonorReceiver>(donorReceivers);

    }

    // Otherwise iterate through and search.
    LinkedHashMap<String, DonorReceiver> matches = new LinkedHashMap<String, DonorReceiver>();

    for (DonorReceiver donorReceiver : donorReceivers.values()) {

      if (donorReceiver.getUserName().contains(value)) {

        matches.put(donorReceiver.getUserName(), donorReceiver);

      }

    }

    return matches;

  }


  /**
   * Issues an update command to the App depending on the given parameters.
   *
   * @param modifyingAccount The account performing the update.
   * @param target a string NHI code of the target account for the update. Cannot be 'all'.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'organs'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   */
  public void issueUpdate(User modifyingAccount, String target, String object, String attribute,
      String value) {
    if (target.equalsIgnoreCase("donors")) {
      System.out.println("ERROR: target = donors. You cannot update all donors at once!" +
          " The target should be the NHI code of the donor you wish to update.\n");
    } else {
      DonorReceiver person = donorReceivers.get(target);
      if (person != null) {
        updateObject(modifyingAccount, person, object, attribute, value);
      } else {
        System.out.println("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
      }
    }
  }

  /**
   * Issues an update command to the App depending on the given parameters.
   *
   * @param modifyingAccount The account performing the update.
   * @param target a string NHI code of the target account for the update. Cannot be 'all'.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'organs'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   * @param donReceiver a string representation of whether the donating organ is in receiving or
   * donating
   */
  public void issueUpdate(User modifyingAccount, String target, String object, String donReceiver,
      String attribute, String value) {
    if (target.equalsIgnoreCase("donors")) {
      System.out.println("ERROR: target = donors. You cannot update all donors at once!" +
          " The target should be the NHI code of the donor you wish to update.\n");
    } else {
      DonorReceiver person = donorReceivers.get(target);
      if (person != null) {
        updateObject(modifyingAccount, person, object, donReceiver, attribute, value);
      } else {
        System.out.println("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
      }
    }
  }


  /**
   * Issues a clinician update command to the App depending on the given parameters. If clinician
   * given by the given target integer is not in the database, an error message is given.
   *
   * @param modifyingAccount The account performing the update.
   * @param target an integer of the clinician staff ID which identifies the specific clinician to
   * update.
   * @param attribute A string representation of the attribute. It should be one of the clinician
   * attributes.
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   */
  public void issueClinicianUpdate(User modifyingAccount, int target, String attribute,
      String value) {
    boolean found = false;
    String targetString = Integer.toString(target);
    Clinician clinician = clinicians.get(targetString);
    if (clinician != null) {
      updateClinicianAttribute(clinician, modifyingAccount, attribute, value);
    } else {
      System.out.println("ERROR: Staff ID " + target + " not found in database. " +
          "Did you input it correctly? Check the read me for a list of available commands\n");
    }
  }

  /**
   * Updates the given clinician attribute with the given value if both parameters are valid. If the
   * process was successful, a log is added to the clinician modifications log list. Otherwise an
   * error message is displayed.
   *
   * @param clinician The clinician to be modified.
   * @param modifyingAccount The account modifying the clinician.
   * @param attribute A string representation of the attribute. It should be one of the clinician
   * attributes
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   */
  public void updateClinicianAttribute(Clinician clinician, User modifyingAccount, String attribute,
      String value) {
    String message = "";
    String oldVal = "";
    switch (attribute) {
      case "givenName": {
        oldVal = clinician.getFirstName();
        message = clinician.updateGivenName(value);
        break;
      }
      case "lastName": {
        oldVal = clinician.getLastName();
        message = clinician.updateLastName(value);
        break;
      }
      case "workAddress": {
        oldVal = clinician.getContactDetails().getAddress().getStreetAddressLineOne();
        message = clinician.updateWorkAddress(value);
        break;
      }
      case "region": {
        oldVal = clinician.getContactDetails().getAddress().getRegion();
        message = clinician.updateRegion(value);
        break;
      }
      default: {
        message = "ERROR: unknown attribute " + attribute
            + ". Attribute should be 'givenName', 'lastName'," +
            " 'workAddress', or 'region'.\n";
        break;
      }
    }
    String substring = message.substring(0, 5);
    if (!substring.equals("ERROR")) {
      LogEntry logEntry = new LogEntry(clinician, modifyingAccount, attribute, oldVal, value);
      getSystemLog().add(logEntry);
      clinician.getModifications().add(logEntry);
    } else {
      clinician.setUpdateMessage(message);
    }
  }


  /**
   * an overloaded method to issue a 'view' command to the App.
   *
   * @param command a string which should be 'view'.
   * @param target a string NHI code of the account to view, or 'donors' for all donors.
   * @param object a string representation of the object to be viewed. This being 'all',
   * 'attributes', or 'organs'.
   */
  public void issueCommand(String command, String target, String object) {
    if (command.equalsIgnoreCase("view")) {
      issueView(target, object);
    } else {
      System.out.println("ERROR: Unknown command " + command + ". " +
          "Did you spell it right? Check the read me for a list of available commands\n");
    }
  }

  /**
   * an overloaded method to issue the 'update' command to the App (for a donor).
   *
   * @param modifyingAccount The account performing the update.
   * @param command a string which should be 'update'
   * @param target a string NHI code of the target account for the update. Cannot be 'all'.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'organs'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   */
  public void issueCommand(User modifyingAccount, String command, String target, String object,
      String attribute, String value) {
    if (command.equalsIgnoreCase("update")) {
      issueUpdate(modifyingAccount, target, object, attribute, value);
    } else {
      System.out.println("ERROR: Wrong command " + command + ". " +
          "Did you spell it right? Did you have too many parameters?" +
          " Check the read me for a list of available commands\n");
    }
  }

  /**
   * an overloaded method to issue the 'update' command to the App.
   *
   * @param modifyingAccount The account performing the update.
   * @param command a string which should be 'update'
   * @param target a string NHI code of the target account for the update. Cannot be 'all'.
   * @param object a string representation of the object to be updated. This being 'profile',
   * 'attributes', or 'organs'.
   * @param attribute a string representation of the attribute to be updated.
   * @param value a string representation of the new value that attribute will be updated as.
   * @param donReceiver a string representation of whether the organ is donating receiving
   */
  public void issueCommand(User modifyingAccount, String command, String target, String object,
      String donReceiver, String attribute, String value) {
    if (command.equalsIgnoreCase("update")) {
      issueUpdate(modifyingAccount, target, object, donReceiver, attribute, value);
    } else {
      System.out.println("ERROR: Wrong command " + command + ". " +
          "Did you spell it right? Did you have too many parameters?" +
          " Check the read me for a list of available commands\n");
    }
  }


  /**
   * an overloaded method to view a donor's details by searching the db using their name.
   *
   * @param command a string which should be 'view'.
   * @param first a string of the donor's first name.
   * @param last a string of the donor's last name
   * @param object a string representation of the object to be viewed. This being 'all',
   * 'attributes', 'log' or 'organs'.
   */
  public void issueCommand(String command, String first, String last, String object) {
    if (command.equalsIgnoreCase("view")) {
      issueView(first, last, object);
    } else {
      System.out.println("ERROR: Unknown command " + command + ". " +
          "Did you spell it right? Check the read me for a list of available commands\n");
    }
  }




  /**
   * Returns the full Linked HashMap of all DonorReceivers in the system.
   *
   * @return Linked HashMap of DonorReceivers
   */
  public Map<String, DonorReceiver> getAccountList() {
    return donorReceivers;
  }


  /**
   * Calls exportAccounts method in Marshal class to export current accounts.
   *
   * @return Returns a string containing the result.
   */
  public String exportAccounts() {

    try {
      updateMasterListUponSave();
      repopulateDonorAccounts();
      Marshal.exportAccounts(donorReceiverMasterList);
      Marshal.exportSystemLog(systemLog);
      return "Accounts successfully saved\n";
    } catch (IOException exception) {
      return "ERROR: Accounts could not be exported.\n";
    }
  }

  /**
   * updates the master list of accounts with all those in the Accounts array and in the Twilight
   * array. Thus The old account records will be overwritten by any account with saved changes. Note
   * that the logs of reactivated accounts will be merged with logs of new changes.
   */
  public void updateMasterListUponSave() {
    // Create a temporary arraylist containing all the donor/receiver accounts in the twilightZone.
    ArrayList<DonorReceiver> intermediate = new ArrayList<>();
    intermediate.addAll(twilightZone.values());

    for (DonorReceiver acc : donorReceivers.values()) {
      if (intermediate.contains(acc)) { // account was deleted and recreated in same session
        DonorReceiver old = intermediate.get(intermediate.lastIndexOf(acc));
        List<LogEntry> oldLogs = old.getUpdateLog();
        List<LogEntry> newLogs = acc.getUpdateLog();
        System.out.println(newLogs);
        for (LogEntry log : newLogs) { // we add any new logs to the old account's updateLog.
          if (!oldLogs.contains(log)) {
            oldLogs.add(log);
          }
        }
        acc.setUpdateLog(
            oldLogs); // now the reactivated account has a deletion and reactivation log.
        intermediate.set(intermediate.lastIndexOf(acc), acc);
      } else {
        intermediate.add(acc);
      }
    }
    for (DonorReceiver acc : donorReceiverMasterList.values()) {

      if (!intermediate.contains(acc)) {
        intermediate.add(acc);
      }
    }
    donorReceiverMasterList.clear();
    // Put all the accounts from the arraylist, intermediate, into the donorReceiverMasterList.
    for (DonorReceiver acc : intermediate) {
      donorReceiverMasterList.put(acc.getUserName(), acc);
    }
    twilightZone.clear();
  }

  /**
   * updates the master list of accounts with all those in the Accounts array and in the Twilight
   * array. Thus The old account records will be overwritten by any account with saved changes. Note
   * that the logs of reactivated accounts will be merged with logs of new changes.
   */
  public void updateAdminMasterListUponSave() {
    // Create a temporary arraylist containing all the administrator accounts in the adminTwilightZone.
    ArrayList<Administrator> intermediate = new ArrayList<>();
    intermediate.addAll(adminTwilightZone.values());

    for (Administrator acc : administrators.values()) {
      if (intermediate.contains(acc)) { // account was deleted and recreated in same session
        Administrator old = intermediate.get(intermediate.lastIndexOf(acc));
        intermediate.set(intermediate.lastIndexOf(acc), acc);
      } else {
        intermediate.add(acc);
      }
    }
    for (Administrator acc : administratorMasterList.values()) {

      if (!intermediate.contains(acc)) {
        intermediate.add(acc);
      }
    }

    // Transfer the accounts in 'intermediate' to the administratorMasterList
    administratorMasterList.clear();
    for (Administrator admin : intermediate) {
      administratorMasterList.put(admin.getUserName(), admin);
    }
    adminTwilightZone.clear();
  }

  /**
   * updates the master list of accounts with all those in the Accounts array and in the Twilight
   * array. Thus The old account records will be overwritten by any account with saved changes. Note
   * that the logs of reactivated accounts will be merged with logs of new changes.
   */
  public void updateClinicianMasterListUponSave() {
    ArrayList<Clinician> intermediate = new ArrayList<>();
    intermediate.addAll(clinicianTwilightZone.values());
    for (Clinician acc : clinicians.values()) {
      if (intermediate.contains(acc)) { // account was deleted and recreated in same session
        Clinician old = intermediate.get(intermediate.lastIndexOf(acc));
        intermediate.set(intermediate.lastIndexOf(acc), acc);
      } else {
        intermediate.add(acc);
      }
    }
    for (Clinician acc : clinicianMasterList.values()) {

      if (!intermediate.contains(acc)) {
        intermediate.add(acc);
      }
    }
    // transfer all clinician accounts from 'intermediate' to the clincianMasterList
    clinicianMasterList.clear();
    for (Clinician acc : intermediate) {
      clinicianMasterList.put(acc.getUserName(), acc);
    }
    clinicianTwilightZone.clear();
  }

  /**
   * Returns true if the specified DonorReceiver object is in the transplant waiting list
   *
   * @param donorReceiver The account we want to check is, or is not, in the transplant waiting
   * list
   * @return true if the DonorReceiver is waiting for a transplant; false if not.
   */
  private boolean inTransplantList(DonorReceiver donorReceiver) {
    // Try to get the person from the transplant waiting list
    DonorReceiver person = transplantWaitingList.get(donorReceiver.getUserName());
    if (person != null) {
      // They are waiting for a transplant
      return true;
    } else {
      return false;
    }
  }

  /**
   * Verifies the transplant waiting list is up-to-date. Checks each DonorReceiver on the transplant
   * waiting list is actually waiting for a transplant.
   */
  public void updateTransplantWaitingList() {
    for (DonorReceiver acc : donorReceiverMasterList.values()) {
      if (acc.getReceiver()) {
        if (!inTransplantList(acc)) {
          transplantWaitingList.put(acc.getUserName(), acc);
        }
      } else {
        if (inTransplantList(acc)) {
          transplantWaitingList.remove(acc.getUserName());
        }
      }
    }
  }


  /**
   * repopulates the donorReceivers linked hashmap with every active account in the master list
   */
  public void repopulateDonorAccounts() {
    donorReceivers.clear();
    for (DonorReceiver acc : donorReceiverMasterList.values()) {
      if (acc.isActive()) {
        // If the account is active, add it to the donorReceivers linked hashmap
        donorReceivers.put(acc.getUserName(), acc);
      }
    }

  }

  /**
   * Repopulates the clinicians linked hashmap with every active account in the master list
   */
  public void repopulateClinicianAccounts() {
    clinicians.clear();
    for (Clinician acc : clinicianMasterList.values()) {
      if (acc.isActive()) {
        clinicians.put(acc.getUserName(), acc);
      }
    }
  }

  /**
   * Repopulates the admins linked hashmap with every active account in the master list
   */
  public void repopulateAdminAccounts() {
    administrators.clear();
    for (Administrator acc : administratorMasterList.values()) {
      if (acc.isActive()) {
        administrators.put(acc.getUserName(), acc);
      }
    }
  }


  /**
   * Calls the importAccounts method in the Marshal class to import a new set of accounts from the
   * accounts directory.
   *
   * @return A string containing the result.
   */
  public String importAccounts() {

    try {

      donorReceiverMasterList = Marshal.importAccounts();
      repopulateDonorAccounts();
      String message = ("Accounts successfully imported.\n");
      System.out.println(message);
      return message;

    } catch (IOException exception) {

      String message = ("ERROR: One or more of the account files may have been corrupted.\n");
      System.out.println(message);
      return message;
    }

  }


  /**
   * Removes leading whitespace from a string.
   *
   * @param string The string that is to be processed.
   * @return The original string with leading whitespace removed.
   */
  public String removeLeadingWhitespace(String string) {

    int index = 0;

    while (index < string.length() &&
        Character.isWhitespace(string.charAt(index))) {

      index += 1;

    }

    return string.substring(index);

  }


  /**
   * Checks whether an NHI exists in the donorReceivers linked hashmap.
   *
   * @param nhi NHI to check
   * @return True if the NHI exists; false if it does not
   */
  public boolean checkNHIExists(String nhi) {
    DonorReceiver person = donorReceivers.get(nhi);
    if (nhi != null) {
      // NHI exists
      return true;
    } else {
      // NHI not found
      return false;
    }
  }

  /**
   * Returns the donorReceiverMasterList
   *
   * @return the master list of donorReceivers
   */
  public Map<String, DonorReceiver> getMasterList() {
    return donorReceiverMasterList;
  }


  /**
   * Gets all the logs in the system log and returns a string of all the logs.
   *
   * @return A string of all system logs.
   */
  public String getLogsAsString() {
    StringBuilder logs = new StringBuilder();
    for (LogEntry log : systemLog) {
      logs.append(log.toString()).append("\n");
    }
    logs.append("\n");
    return logs.toString();
  }

  /**
   * Getter method for the system log array list.
   *
   * @return system log of application
   */
  public static ObservableList<LogEntry> getSystemLog() {
    return systemLog;
  }

  public static ObservableList<String> getStatusUpdates() {
    return statusUpdates;
  }

  /**
   * Setter method for the system log array list.
   *
   * @param systemLog An array list of log strings.
   */
  public void setSystemLog(ArrayList<LogEntry> systemLog) {
    this.systemLog = FXCollections.observableArrayList(systemLog);
  }

  public void setStatusUpdates(ArrayList<String> statusUpdates) {
    AccountManager.statusUpdates = FXCollections.observableArrayList(statusUpdates);
  }


  /**
   * Adds a listener to the system log, allowing actions to be performed on the system log after an
   * update.
   *
   * @param listener A new ListChangeListener.
   * @throws NullPointerException When systemLog has not been initialised.
   */
  public static void addStatusUpdatesListener(ListChangeListener<String> listener)
      throws NullPointerException {

    if (statusUpdates != null) {

      statusUpdates.addListener(listener);

    } else {

      throw new NullPointerException(listener.toString()
          + " could not be added because statusUpdates is null. It must be initialised.");

    }

  }

  /**
   * Adds a listener to the system log, allowing actions to be performed on the system log after an
   * update.
   *
   * @param listener A new ListChangeListener.
   * @throws NullPointerException When systemLog has not been initialised.
   */
  public static void addSystemLogListener(ListChangeListener<LogEntry> listener)
      throws NullPointerException {

    if (systemLog != null) {

      systemLog.addListener(listener);

    } else {

      throw new NullPointerException(listener.toString()
          + " could not be added because systemLog is null. It must be initialised.");

    }

  }



  /**
   * The getter method for the linked hashmap of clinicians.
   *
   * @return The linked hashmap of clinicians.
   */
  public Map<String, Clinician> getClinicians() {
    return clinicians;
  }

  /**
   * The setter method for the linked hashmap of clinicians.
   *
   * @param clinicians The linked hashmap of clinicians.
   */
  public void setClinicians(Map<String, Clinician> clinicians) {
    this.clinicians = clinicians;
  }


  /**
   * Checks if the clinicians array list is empty, and if it is, populates it with a default
   * clinician object. This ensures that there is always a clinician object in the the clinicians
   * array list upon application start up.
   */
  public void addClinicianIfNoneExists() {
    if (!clinicians.containsKey(Clinician.defaultValue)) {
      LocalDateTime now = LocalDateTime.now();
      ArrayList<LogEntry> modifications = new ArrayList<>();
      Clinician defaultClinician = new Clinician("Default Clinician", "", "Placeholder",
          new ContactDetails(
              new Address("University of Canterbury", null, null,
                  null, "Canterbury", null, null), null, null, null),
          "0", "password", now, modifications, 1);
      clinicians.put(defaultClinician.getUserName(), defaultClinician);
    }
  }


  /**
   * Checks if the given staff id has not been used by other clinicians.
   *
   * @param id a staff id.
   * @return Returns 'false' if the given ID is already in use, returns 'true' otherwise.
   */
  public boolean checkStaffIDIsNotUsed(String id) {
    for (Clinician clinician : clinicians.values()) {
      if (id.equals(clinician.getUserName())) {
        return false;
      }
    }
    return true;
  }


  /**
   * Calls exportClinicians method in Marshal class to export current clinicians.
   */
  public void exportClinicians() {
    try {
      updateClinicianMasterListUponSave();
      repopulateClinicianAccounts();
      Marshal.exportClinicians(clinicianMasterList);
    } catch (IOException exception) {

      System.out.println("ERROR: Clinicians could not be exported.\n");

    }
  }


  /**
   * Calls the importClinicians method in the Marshal class to import a new set of clinicians from
   * the clinicians directory.
   */
  public void importClinicians() {
    clinicianMasterList = Marshal.importClinicians();
    repopulateClinicianAccounts();
  }


  /**
   * Checks if the given username corresponds to an administrator account in the database. If it
   * does, the administrator User object is returned (or null if it does not).
   *
   * @param username A string of the username to be checked.
   * @return Returns the administrator user object corresponding to the given username  if it
   * exists, null otherwise.
   */
  public Administrator getAdminIfItExists(String username) {
    // Try to get admin by username
    Administrator admin = administrators.get(username);
    // If the username exists in the administrators map, 'admin' will be that account.
    // If the username was not found, 'admin' will be null.
    // So, we just return 'admin'.
    return admin;
  }


  /**
   * Checks if the administrator array list is empty, and if it is, populates it with a default
   * administrator object. This ensures that there is always a administrator object in the the
   * administrator array list upon application start up.
   */
  public void addDefaultAdminIfNoneExists() {
    if (!administrators.containsKey("Sudo")) {
      LocalDateTime now = LocalDateTime.now();
      ArrayList<LogEntry> modifications = new ArrayList<>();
      String defaultUsername = Administrator.defaultAdmin;
      Administrator admin = new Administrator("Default", "", "Administrator",
          new ContactDetails(null, null, null, null),
          defaultUsername, "password", now, modifications, 1);
      administrators.put(admin.getUserName(), admin);
    }
  }


  /**
   * Calls exportAdmins method in Marshal class to export current admins.
   */
  public void exportAdmins() {
    try {
      updateAdminMasterListUponSave();
      repopulateAdminAccounts();
      Marshal.exportAdministrators(administratorMasterList);
    } catch (IOException exception) {
      System.out.println("ERROR: Admins could not be exported.\n");
    }
  }


  /**
   * Calls the importAdministrators method in the Marshal class to import a new set of admins from
   * the administrator directory.
   */
  public void importAdmins() {
    administratorMasterList = Marshal.importAdministrators();
    repopulateAdminAccounts();
  }


  /**
   * Inserts an admin into the database administrator list. If the username is already in use, that
   * administrator will be overwritten if with the given administrator only if 'replace' is set to
   * 'True'. If it is set to 'False', no changes will be made. Precondition: IT IS UP TO THE CALLER
   * OF THIS FUNCTION TO ALREADY KNOW IF THE ADMIN EXISTS OR NOT AND SET 'REPLACE' APPROPRIATELY.
   * Precondition: The admin to be added cannot be the default admin whose username is 'Sudo' as it
   * is treated as immutable.
   *
   * @param admin A Administrator object to be added to the database.
   * @param replace A boolean to specify whether or not to overwrite an administrator if the
   * username is already in use.
   */
  public void insertAdmin(Administrator admin, boolean replace) {
    if (!admin.getUserName()
        .equals(Administrator.defaultAdmin)) { // We cannot replace the default Admin!
      if (replace) {
        // If 'replace' is set to true, put the new admin in the admins list
        administrators.put(admin.getUserName(), admin);
      } else {
        // If 'replace' is set to false, the new admin will only be added if the username is not already in use
        if (!administrators.containsKey(admin.getUserName())) {
          administratorMasterList.put(admin.getUserName(), admin);
        }
      }
    }
  }


  /**
   * Inserts a clinician into the database clinicians list. If the username is already in use, that
   * clinician will be overwritten by the given clinician only if 'replace' is set to 'True'. If it
   * is set to 'False', no change will be made. Precondition: IT IS UP TO THE CALLER OF THIS
   * FUNCTION TO ALREADY KNOW IF THE CLINICIAN EXISTS OR NOT AND SET 'REPLACE' APPROPRIATELY.
   * Precondition: The clinician to be added cannot be the default clinician whose username is '0'
   * as it is treated as immutable.
   *
   * @param clinician A clinician object to be added to the database.
   * @param replace A boolean to specify whether the clinician list will be searched in order to
   * replace the existing clinician.
   */
  public void insertClinician(Clinician clinician, boolean replace) {
    if (!clinician.getUserName()
        .equals(Clinician.defaultValue)) { // We cannot replace the default clinician!
      if (replace) {
        // If 'replace' is set to True, put the clinician in the clinicians list
        clinicians.put(clinician.getUserName(), clinician);
      } else {
        // If 'replace is set to False, the clinician will be added if the username is not already in use
        if (!clinicians.containsKey(clinician.getUserName())) {
          clinicians.put(clinician.getUserName(), clinician);
        }
      }
    }
  }


  /**
   * Inserts a donor/receiver into the database donorReceiver list. If the username is already in
   * use, that donor/receiver will be overwritten only if 'replace' is set to True. If it is set to
   * False, no change will be made. Precondition: IT IS UP TO THE CALLER OF THIS FUNCTION TO ALREADY
   * KNOW IF THE DONOR EXISTS OR NOT AND SET 'REPLACE' APPROPRIATELY.
   *
   * @param donorReceiver A donorReceiver object to be added to the database.
   * @param replace A boolean to specify whether the donor list will be searched in order to replace
   * the existing donor.
   */
  public void insertDonorReceiver(DonorReceiver donorReceiver, boolean replace) {
    if (replace) {
      donorReceivers.put(donorReceiver.getUserName(), donorReceiver);
    } else {
      if (!donorReceivers.containsKey(donorReceiver.getUserName())) {
        donorReceivers.put(donorReceiver.getUserName(), donorReceiver);
      }
    }

  }

  /* Get and return a donor/receiver using their username (nhi).
   * @return the donor/receiver object
   */
  public DonorReceiver getDonorReceiverByUsername(String username) {
    return donorReceivers.get(username);
  }

  /* Get and return a clinician by their username (staff ID)
   * @return the clinician object
   */
  public Clinician getClinicianByUsername(String username) {
    return clinicians.get(username);
  }


  /* Get and return an administrator by their username
   * @return the administrator object
   */
  public Administrator getAdminByUsername(String username) {
    return administrators.get(username);
  }

  public void setDonorReceivers(Map<String, DonorReceiver> donorReceivers) {
    this.donorReceivers = donorReceivers;
  }

  public Map<String, DonorReceiver> getDonorReceiverMasterList() {
    return donorReceiverMasterList;
  }


  /**
   * Takes a Clinician object and adds it to the database.
   *
   * @param clinician The Clinician object to be added.
   */
  public void addClinician(Clinician clinician) {

    // Create system log entry and append to account and global log.
    LogEntry logEntry = new LogEntry(clinician, currentUser, "created", null, null);
    systemLog.add(logEntry);
    clinician.getModifications().add(logEntry);

    // Set active status and add to master list.
    clinician.setActive(true);

    // Add clinician to database.
    clinicians.put(clinician.getUserName(), clinician);
    clinicianMasterList.put(clinician.getUserName(), clinician);

  }


}
