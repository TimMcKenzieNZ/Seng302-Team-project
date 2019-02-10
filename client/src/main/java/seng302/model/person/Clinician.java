package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng302.App;
import seng302.model.AccountManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Clinician extends User {

  public static String defaultValue = "0";

  /**
   * A container for the log of the last error message for an attempted update performed on the
   * clinician.
   */
  private String updateMessage = "waiting for new message";


  /**
   * The constructor method for a Clinician (when creating a new Clinician).
   *
   * @param givenName The given name of the Clinician.
   * @param lastName The last name of the Clinician.
   * @param staffID The staff ID of the Clinician.
   * @param workAddress The work address of the Clinician.
   * @param region The region of the Clinician.
   * @param password The password of the Clinician.
   */
  public Clinician(String givenName,
      String lastName,
      String workAddress,
      String region,
      String staffID,
      String password) {

    super(givenName, null, lastName, new ContactDetails(
        new Address(workAddress, null, null, null, region, null, null)
        , null, null, null), staffID, password, LocalDateTime.now(), new ArrayList<>(), 1);

  }


  public String getUpdateMessage() {
    return updateMessage;
  }


  public void setUpdateMessage(String updateMessage) {
    this.updateMessage = updateMessage;
  }

  /**
   * The constructor method for a Clinician (when creating a new Clinician) using json.
   *
   * @param givenName The given name of the Clinician.
   * @param middleName The middle name of the Clinician.
   * @param lastName The last name of the Clinician.
   * @param contacts The contact details of the Clinician.
   * @param staffID The staff ID of the Clinician.
   * @param password The password of the Clinician.
   * @param creationDate the date of creation for this clinician, a LocalDate object.
   * @param modifications An array list of LocalDate objects that record modifications to the
   * clinician.
   */
  @JsonCreator
  public Clinician(
      //inherited from Person
      @JsonProperty("givenName") String givenName,
      @JsonProperty("middleName") String middleName,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("contactDetails") ContactDetails contacts,

      //inherited from User
      @JsonProperty("username") String staffID,
      @JsonProperty("password") String password,
      @JsonProperty("creationDate") LocalDateTime creationDate,
      @JsonProperty("modifications") ArrayList<LogEntry> modifications,
      @JsonProperty("version") Integer version) {
    super(givenName, middleName, lastName, contacts, staffID, password, creationDate,
        modifications, version);
  }


  /**
   * Updates the clinician's given name if the value is valid and logs the result.
   *
   * @param value A string of the clinician's new given name.
   * @return A string log of the result of the operation.
   */
  public String updateGivenName(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Given name must be between 1 and 50 alphabetical characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 1, 50)) {
      message = "updated";
      setFirstName(value);
    }
    return message;
  }


  /**
   * Updates the clinician's last name if the value is valid and logs the result.
   *
   * @param value A string of the clinician's new last name.
   * @return A string log of the result of the operation.
   */
  public String updateLastName(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Last name must be between 1 and 50 alphabetical characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 1, 50)) {
      message = "updated";
      setLastName(value);
    }
    return message;
  }


  /**
   * Updates the clinician's work address if the value is valid and logs the result.
   *
   * @param value A string of the clinician's new work address.
   * @return A string log of the result of the operation.
   */
  public String updateWorkAddress(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Work address must be between 1 and 100 alphanumeric characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(true, value, 1, 100)) {
      message = "updated";
      getContactDetails().getAddress().updateStreetAddressLineOne(value);
    }
    return message;
  }


  /**
   * Updates the clinician's region and logs the result.
   *
   * @param value A string of the clinician's new region.
   * @return A string log of the result of the operation.
   */
  public String updateRegion(String value) {
    return getContactDetails().getAddress().updateRegion(value);
  }


  /**
   * Returns a string of the history of updates made to the clinician if there are any.
   *
   * @return a string of modification logs.
   */
  public String modificationsToString() {

    String logString = "Modification log:\n";
    if (getModifications().size() == 0) {
      logString += "No modifications have been recorded.\n";
    } else {
      for (LogEntry log : getModifications()) {
        logString += log.toString();
      }
    }
    return logString + "\n";
  }

  @Override
  public String toString() {
    String details = "Clinician Details:\n\n";
    details += "Name: " + this.fullName() + "\n";
    details += "Staff ID: " + this.getUserName() + "\n\n";
    details += "Contact Details:\n" + getContactDetails().toString() + "\n";
    details += String.format("Account created at: %s\n",
        getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    return details;
  }
}
