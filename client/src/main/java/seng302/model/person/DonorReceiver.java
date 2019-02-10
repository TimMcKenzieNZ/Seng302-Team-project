package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.HttpClientErrorException;
import seng302.App;
import seng302.controllers.EditPaneController;
import seng302.model.AccountManager;
import seng302.model.DonorOrganInventory;
import seng302.model.Illness;
import seng302.model.MedicalProcedure;
import seng302.model.Medications;
import seng302.model.ReceiverOrganInventory;
import seng302.model.StringExtension;
import seng302.model.UserAttributeCollection;
import seng302.model.enums.ADDRESSES;
import seng302.model.patches.MedicalProcedurePatch;
import seng302.services.PatchTask;
import seng302.services.PostTask;
import seng302.services.SyncPatchTask;
import seng302.services.SyncPostTask;

/**
 * A class to store all the Donor's personal information as well as methods to validate the user
 * input when they create the class.
 */
public class DonorReceiver extends User {

  static final String invalidValueMessage = "ERROR: Invalid value ";
  private ContactDetails emergencyContactDetails;

  /**
   * A container for the log of the last error message for an attempted update performed on the
   * account.
   */
  private String updateMessage = "waiting for new message";

  /**
   * A class to hold all the donor's attributes such as the donor's address. See the
   * UserAttributeCollection class for more information;
   */
  private UserAttributeCollection userAttributeCollection;


  private Medications medications;

  /**
   * (Optional) An upper case alphabetical title the donor will have e.g. 'Mr', 'Miss', or 'Sir'.
   * The maximum length of the title is 10 letters.
   */
  private String title;

  /**
   * The preferred name of the donor.
   */
  private String preferredName;

  /**
   * The date of the donor's birth as a LocalDate object. The format is CCYYMMDD
   * (Century,Year,Month,Day). This is the format is specified by the Ministry of Health. There is a
   * health requirement that the donor is younger than 65.
   */
  private LocalDate dateOfBirth;

  /**
   * (If Applicable) The date of the donor's death as a LocalDate object. The format is CCYYMMDD
   * (Century,Year,Month,Day). This is the format is specified by the Ministry of Health.
   */
  private LocalDate dateOfDeath;

  /**
   * An instance of the class Deathdetails which contains information regarding the death of the donorReceiver
   */
  private DeathDetails deathDetails;

  /**
   * A single uppercase char code for the gender of the donor; M: Male, F:Female, O: Other, U;
   * unspecified/unknown.
   */
  private Character gender;

  /**
   * The gender of the donor at birth (problematic term is problematic).
   */
  private Character birthGender;

  /**
   * The date of the donor's account creation as a LocalDatetime object.
   */
  private LocalDateTime creationTimeStamp;

  /**
   * A log of all the updates made to the DonorReceiver, e.g. "givenName changed from 'McKenzie' to
   * 'Smith'". A date of when the change was made, formatted to "Change made at: yyyy-MM-dd
   * HH:mm:ss" is associated with each entry.
   */
  private List<LogEntry> updateLog = new ArrayList<>();

  /**
   * A boolean that signifies whether the new donor lived in the UK, Ireland, or France during the
   * period of 1980 to 1996 for more than 6 months. This is a conditional requirement of all *blood*
   * donors as they could be carrying Variant Creutzfeldt-Jakob (Mad Cow) disease. This may affect a
   * donor's eligibility to donate organs. See: https://www.nzblood.co.nz/give-blood/donating/am-i-eligible/variant-creutzfeldt-jakob-disease-vcjd/
   */
  private boolean livedInUKFlag;


  /**
   * A boolean to signify whether an account had been deleted/deactivated or not. A non-active
   * account is kept in the master account list for archival purposes but can no longer be edited.
   * All accounts are instantiated as 'active'.
   */
  private boolean activeFlag = true;

  /**
   * A boolean to signify whether the account is a receiver
   */
  private boolean receiver = false;

  /**
   * The list of organs the user requires.
   */
  private ReceiverOrganInventory requiredOrgans;

  /**
   * A class to hold all the donor's organs to be donated. See the DonorOrganInventory class for
   * more information;
   */
  private DonorOrganInventory donorOrganInventory;


  private List<MedicalProcedure> medicalProcedures = new ArrayList<>();
  public static final String PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE = "Procedure already exists for this user";
  public static final String PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE = "Procedure does not belong to this account";
  public static final String UNSPECIFIED = "unspecified";

  /**
   * An arrayList of Illness objects which represent the illnesses/diseases the donor has suffered
   * from, both past and present.
   */
  private List<Illness> masterIllnessList = new ArrayList<>();


  /**
   * An arrayList of Illness objects which represent the illnesses/diseases that the donor currently
   * suffers from.
   */
  @JsonIgnore
  private List<Illness> currentDiagnoses = new ArrayList<>();


  /**
   * An arrayList of Illness objects which represent the illnesses/diseases that the donor has been
   * diagnosed with in the past that have been resolved.
   */
  @JsonIgnore
  private List<Illness> historicDiagnoses = new ArrayList<>();

  // String objects commonly used throughout the class.
  private static final String INVALID_VALUE_MESSAGE_START = "ERROR: Invalid value ";
  private static final String TITLE_FIELD_NAME = "title";
  private static final String ERROR_PREFIX = "ERROR";

  /**
   * Basic constructor for an account.
   *
   * @param firstName the first name(s) of the donor/receiver.
   * @param middleName the middle names of the donor/receiver.
   * @param lastName the last name of the donor/receiver.
   * @param DOB the date of birth of the donor/receiver.
   * @param nhi the national health index number of the donor/receiver.
   */
  public DonorReceiver(String firstName, String middleName, String lastName, LocalDate DOB,
                       String nhi) {
    super(firstName, middleName, lastName,
        new ContactDetails(new Address(null, null, null, null, null, null, "NZ"),
            null, null, null), nhi, null, null, null, 1);
    dateOfBirth = DOB;
    userAttributeCollection = new UserAttributeCollection();
    donorOrganInventory = new DonorOrganInventory();
    requiredOrgans = new ReceiverOrganInventory();
    medications = new Medications();
    creationTimeStamp = LocalDateTime.now();
    emergencyContactDetails = new ContactDetails(new Address(null, null,
        null, null, null, null, "NZ"),
        null, null, null);
    title = null;
    preferredName = "";
    userAttributeCollection.setBloodPressure("0/0");
    userAttributeCollection.setChronicDiseases("");
    deathDetails = new DeathDetails();
  }


  /**
   * Constructor for a donor account from a parsed .csv file
   *
   * @param firstName the first name(s) of the donor/receiver.
   * @param middleName the middle names of the donor/receiver.
   * @param lastName the last name of the donor/receiver.
   * @param DOB the date of birth of the donor/receiver.
   * @param nhi the national health index number of the donor/receiver.
   * @param details the donors contact details including their address, phone numbers, and email
   * @param gender the given gender of the donor
   * @param birthGender the birth gender of the donor
   * @param bloodType The blood pressure of the donor represented as 'systolic pressure/diastolic
   * pressure'
   * @param height the height of the donor in meters
   * @param weight the weight of the donor in kgs
   */
  public DonorReceiver(String firstName, String middleName, String lastName, LocalDate DOB,
      String nhi, ContactDetails details, char gender, char birthGender, String bloodType, double height,
      double weight) {
    super(firstName, middleName, lastName, details, nhi, "password", LocalDateTime.now(),
        new ArrayList<LogEntry>(), 1);
    dateOfBirth = DOB;
    this.gender = gender;
    this.birthGender = birthGender;
    userAttributeCollection = new UserAttributeCollection(height, weight, bloodType, false, null,
            "0/0", 0.0, null);
    donorOrganInventory = new DonorOrganInventory();
    requiredOrgans = new ReceiverOrganInventory();
    medications = new Medications();
    creationTimeStamp = LocalDateTime.now();
    emergencyContactDetails = new ContactDetails(new Address(null, null,
            null, null, null, null, null),
            null, null, null);
    title = UNSPECIFIED;
    preferredName = "";
    userAttributeCollection.setBloodPressure("0/0");
    userAttributeCollection.setChronicDiseases("");
    receiver = false;
    activeFlag = true;
    deathDetails = new DeathDetails();
    setVersion(1);
  }


  /**
   * full constructor for an DonorReceiver class.
   *
   * @param userAttCol Donor's attribute information (UserAttributeCollection)
   * @param organs Donor's organ information (DonorOrganInventory)
   * @param contactDetails Donor's contact information
   * @param emergencyContactDetails Donor's emergency contact information
   * @param medication Donor's medication information
   * @param userName Donor's National Health Index code (string)
   * @param title Donor's title (string)
   * @param password Donor's password for their account (a string)
   * @param firstName Donor's given name (string)
   * @param middleName Donor's other name (string)
   * @param lastName Donor's last name (string)
   * @param preferredName Donor's preferred name (String)
   * @param dob Donor's date of birth (LocalDate)
   * @param gender Donor's gender code (char)
   * @param birthGender Donor's birth gender code (char)
   * @param creation date of account creation (LocalDateTime)
   * @param flag boolean flag on whether Donor has lived in UK, Ireland, or France between 1980 and
   * 1996
   * @param log An ArrayList of strings composing of the account log.
   * @param masterIllnessList An ArrayList of Illness objects.
   * @param active Whether or not the account is active
   * @param medicalProcedures medical procedures of donor
   * @param required required organs of donor
   * @param creationDate creation date of the account
   * @param modifications modifications of the account
   * @param deathDetails The death details of the account
   */
  @JsonCreator
  public DonorReceiver(
          //inherited from Person
          @JsonProperty("firstName") String firstName,
          @JsonProperty("middleName") String middleName,
          @JsonProperty("lastName") String lastName,
          @JsonProperty("contactDetails") ContactDetails contactDetails,

          //inherited from User

        @JsonProperty("userName") String userName,
        @JsonProperty("password") String password,
        @JsonProperty("modifications") List<LogEntry> modifications,
        @JsonProperty("creationDate") LocalDateTime creationDate,

        //Donor attributes
        @JsonProperty("emergencyContactDetails") ContactDetails emergencyContactDetails,
        @JsonProperty("medications") Medications medication,
        @JsonProperty("title") String title,
        @JsonProperty("userAttributeCollection") UserAttributeCollection userAttCol,
        @JsonProperty("donorOrganInventory") DonorOrganInventory organs,
        @JsonProperty("requiredOrgans") ReceiverOrganInventory required,
        @JsonProperty("preferredName") String preferredName,
        @JsonProperty("dateOfBirth") LocalDate dob,
        @JsonProperty("gender") Character gender,
        @JsonProperty("birthGender") Character birthGender,
        @JsonProperty("creationTimeStamp") LocalDateTime creation,
        @JsonProperty("livedInUKFlag") boolean flag,
        @JsonProperty("updateLog") List<LogEntry> log,
        @JsonProperty("active") boolean active,
        @JsonProperty("masterIllnessList") List<Illness> masterIllnessList,
        @JsonProperty("medicalProcedures") List<MedicalProcedure> medicalProcedures,
        @JsonProperty("deathDetails") DeathDetails deathDetails,
        @JsonProperty("version") int version) {
    super(firstName, middleName, lastName, contactDetails, userName, password, creationDate,
        modifications, version); //Need a json constructor in the superclasses
    if (userAttCol == null) {
      this.userAttributeCollection = new UserAttributeCollection();
    } else {
      userAttributeCollection = userAttCol;
    }
    donorOrganInventory = organs;
    requiredOrgans = required;
    medications = medication;
    this.title = StringExtension.toUpperCase(title);
    if (preferredName == null) {
      this.preferredName = "";
    } else {
      this.preferredName = preferredName;
    }

    dateOfBirth = dob;
    try {
      this.gender = Character.toUpperCase(gender);
    } catch (NullPointerException e) {
      this.gender = null;
    }
    try {
      this.birthGender = Character.toUpperCase(birthGender);
    } catch (NullPointerException e) {
      this.birthGender = null;
    }
    creationTimeStamp = creation;
    livedInUKFlag = flag;
    updateLog = log;
    this.activeFlag = active;
    try {
      this.masterIllnessList = masterIllnessList;

    }    catch(NullPointerException e) {
      this.masterIllnessList = null;
    }

    if (masterIllnessList == null)  {
      this.masterIllnessList = null;
    }
    populateIllnessLists();
    if (emergencyContactDetails == null) {
      this.emergencyContactDetails = new ContactDetails(
              new Address(null, null, null, null, null, null, null)
              , null, null, null);
    } else {
      this.emergencyContactDetails = emergencyContactDetails;
    } if (medicalProcedures != null) {
      this.medicalProcedures = new ArrayList<>(medicalProcedures);
    } else {
      this.medicalProcedures = null;
    }

    if (deathDetails == null) {
      this.deathDetails = new DeathDetails();
    } else {
      this.deathDetails = deathDetails;
    }

  }

  public UserAttributeCollection getUserAttributeCollection() {
    return userAttributeCollection;
  }

  public DonorOrganInventory getDonorOrganInventory() {
    return donorOrganInventory;
  }

  public Medications getMedications() {
    return medications;
  }

  public String getTitle() {
    return title;
  }

  public String getPreferredName() {
    return preferredName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public LocalDateTime getDateOfDeath() {
    return deathDetails.getDoD();
  }

  public Character getGender() {
    if (gender == null) {
      return 'U';
    } else {
      return gender;
    }

  }

  public Character getBirthGender() {

    if (birthGender == null) {
      return 'U';
    } else {
      return birthGender;
    }
  }

  public LocalDateTime getCreationTimeStamp() {
    return creationTimeStamp;
  }

  public List<LogEntry> getUpdateLog() {
    return updateLog;
  }

  public boolean getLivedInUKFlag() {
    return livedInUKFlag;
  }

  public List<MedicalProcedure> getMedicalProcedures() {
    return this.medicalProcedures;
  }

  public void setUserAttributeCollection(UserAttributeCollection userAttColl) {
    userAttributeCollection = userAttColl;
  }

  public void setDonorOrganInventory(DonorOrganInventory donorInv) {
    donorOrganInventory = donorInv;
  }

  public void setUpdateLog(List<LogEntry> newLog) {
    updateLog = newLog;
  }

  public String getUpdateMessage() {
    return updateMessage;
  }

  public void setUpdateMessage(String newMessage) {
    updateMessage = newMessage;
  }

  public boolean getReceiver() {
    return receiver;
  }

  public ReceiverOrganInventory getRequiredOrgans() {
    return requiredOrgans;
  }

  public void setRequiredOrgans(ReceiverOrganInventory inventory) {
    requiredOrgans = inventory;
  }

  public void setReceiver(boolean register) {
    receiver = register;
  }

  public Boolean isReceiver() {
    return receiver;
  }

  public DeathDetails getDeathDetails() {
    return deathDetails;
  }

  public void setDeathDetails(DeathDetails deathDetails) {
    this.deathDetails = deathDetails;
  }

  /**
   * Get the string representation of gender
   * @return The string representation of gender
   */
  public String genderString() {
    try {
      if (gender == 'M') {
        return "Male";
      } else if (gender == 'F') {
        return "Female";
      } else if (gender == 'O') {
        return "Other";
      } else if (gender == 'U') {
        return "Unknown";
      } else {
        return UNSPECIFIED;
      }
    } catch (NullPointerException e) {
      return UNSPECIFIED;
    }
  }


  /**
   * Get the string representation of birth gender
   * @return The string representation of birth gender
   */
  public String birthGenderString() {
    try {
      if (birthGender == 'M') {
        return "Male";
      } else if (birthGender == 'F') {
        return "Female";
      } else if (birthGender == 'O') {
        return "Other";
      } else if (birthGender == 'U') {
        return "Unknown";
      } else {
        return UNSPECIFIED;
      }
    } catch (NullPointerException e) {
      return UNSPECIFIED;
    }
  }


  /**
   * Returns the title with the first letter uppercase and the rest lowercase
   *
   * @return title String title of the account holder
   */
  public String titleString() {
    try {
      String titleValue = "" + this.title;
      if (titleValue.length() > 1) {
        titleValue = titleValue.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
      } else if (titleValue.length() > 0) {
        titleValue = titleValue.substring(0, 1).toUpperCase();
      }
      return titleValue;
    } catch (NullPointerException e) {
      return "";
    }

  }


  /**
   * Adds a log string to the overall System Log. This method  is/should be called whenever a local
   * change to an DonorReceiver object requiring a log of the change.
   *
   * @param log A string of some change made to an DonorReceiver.
   */
  public void addToSystemLog(LogEntry log) {
    AccountManager.getSystemLog().add(log);
  }


  /**
   * Creates a date string from the given LocalDate object formatted to "CCYYMMDD".
   *
   * @param time a LocalDate object.
   * @return returns a formatted string of the form "CCYYMMDD".
   */
  public static String formatDateToString(LocalDate time) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return time.format(dateFormat);
  }

  public static String formatDateToStringSlash(LocalDate date) {
    if (date == null) {
      return "N.A.";
    }
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return date.format(dateFormat);
  }


  /**
   * Creates a date string from the given LocalDateTime object formatted to "yyyy-MM-dd HH:mm:ss".
   *
   * @param time a LocalDateTime object.
   * @return returns a formatted string of the form "yyyy-MM-dd HH:mm:ss".
   */
  public static String formatDateTimeToString(LocalDateTime time) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return time.format(dateFormat);
  }


  /**
   * Changes the title to the new value.
   *
   * @param title a string
   */
  public void setTitle(String title) {
    this.title = StringExtension.toUpperCase(title);
  }

  /**
   * Changes the preferred name to the new value.
   *
   * @param preferredName The preferred name of the donor as a String object.
   */
  public void setPreferredName(String preferredName) {
    this.preferredName = preferredName;
  }

  /**
   * Changes the date of birth to the new value.
   *
   * @param newDateOfBirth a LocalDate object
   */
  public void setDateOfBirth(LocalDate newDateOfBirth) {
    dateOfBirth = newDateOfBirth;
  }

  /**
   * Changes the date of death to the new value.
   *
   * @param  newDateOfDeath LocalDate object
   */
  public void setDateOfDeath(LocalDateTime newDateOfDeath) {
    deathDetails.setDoD(newDateOfDeath);
  }

  /**
   * Changes the gender to the new value.
   *
   * @param gender a char, either M,F,O, or U
   */
  public void setGender(Character gender) {
    if (gender == null) {
      this.gender = null;
    } else {
      this.gender = Character.toUpperCase(gender);
    }
  }

  /**
   * Changes the birth gender to the new value.
   *
   * @param birthGender A char which is either M, F, O, or U.
   */
  public void setBirthGender(Character birthGender) {
    if (birthGender == null) {
      this.birthGender = null;
    } else {
      this.birthGender = Character.toUpperCase(birthGender);
    }
  }

  /**
   * Changes the livedInUKFlag to the new value.
   *
   * @param flag a boolean flag
   */
  public void setLivedInUKFlag(boolean flag) {
    livedInUKFlag = flag;
  }

  /**
   * Changes the active to the new value.
   *
   * @param active a boolean flag
   */
  public void setActiveFlag(boolean active) {
    this.activeFlag = active;
  }


  /**
   * returns a string representation list of the DonorReceiver holder's personal details excepting
   * the update log.
   *
   * @return a string list of the donor's personal details.
   */
  @Override
  public String toString() {
    String detailsString = "Donor Details:\n\tName: ";
    if (title != null) {
      detailsString += title + " ";
    }
    if (getFirstName() != null) {
      detailsString += getFirstName() + " ";
    }
    if (getMiddleName() != null && !getMiddleName().equalsIgnoreCase("")) {
      detailsString += getMiddleName() + " ";
    }
    if (getLastName() != null && !getLastName().equalsIgnoreCase("")) {
      detailsString += getLastName();
    }
    if (preferredName != null) {
      detailsString += "\n\tPreferred Name: " + preferredName;
    }
    detailsString += "\n\tNational Health Index: " + getUserName() + "\n";

    detailsString += String
            .format("\tDonorReceiver created at: %s%n", formatDateTimeToString(creationTimeStamp));
    if (dateOfBirth != null) {
      detailsString += String.format("\tDate of birth: %s%n", formatDateToString(dateOfBirth));
    }
    if (getDateOfDeath() != null) {
      detailsString += "\tDeath Details:\n";
      detailsString += String.format("\t\tDate of Death: %s\n", formatDateTimeToString(getDateOfDeath()));
      detailsString += String.format("\t\tCity of Death: %s\n", this.deathDetails.getCity());
      detailsString += String.format("\t\tRegion of Death: %s\n", this.deathDetails.getRegion());
      detailsString += String.format("\t\tCountry of Death: %s\n", this.deathDetails.getCountry());
      detailsString += String.format("\t\tCountry of Death: %s\n", this.deathDetails.getCountry());
    }
    if (gender != null) {
      detailsString += String.format("\tGender: %s\n", gender);
    }
    if (birthGender != null) {
      detailsString += String.format("\tBirth Gender: %s\n", gender);
    }
    String livedString = "";
    if (Boolean.TRUE.equals(livedInUKFlag)) {
      livedString += "\tLived in UK/Ireland/France during 1980 to 1996: Yes\n\n";
    }
    detailsString += livedString;

    String attributeString = userAttributeCollection.toString();
    String organString = donorOrganInventory.toString();
    String contacts = "Personal Contact Details:\n" + getContactDetails().toString();
    String emergencyContacts =
            "Emergency Contact Details:\n" + getEmergencyContactDetails().toString();
    return detailsString + attributeString + organString + contacts + emergencyContacts + "\n";
  }


  /**
   * Returns a string of the history of updates made to the account if there are any.
   *
   * @return a string of update logs.
   */
  public String updateLogToString() {
    StringBuilder logString = new StringBuilder();
    logString.append("Update log:\n");

    if (updateLog.isEmpty()) {
      logString.append("No updates have been recorded.");
    } else {
      for (LogEntry log : updateLog) {

        logString.append(log.toString() + "\n");
      }
    }
    logString.append("\n");

    return logString.toString();
  }


  /**
   * Returns the age of the account holder in years. Note that this value refers to the number of
   * birthdays a person has add, rather than the actual number of years elapsed since their birth
   * date. If the account owner is dead, their age at death is returned.
   *
   * @return The age of the account holder in years.
   */
  public int calculateAge() {

    LocalDateTime currentDate;
    // If account owner is dead, use their date of death to calculate age.
    // Otherwise, use LocalDate.now().
    if (deathDetails.getDoD() == null) {
      currentDate = LocalDateTime.now();
    } else {
      currentDate = deathDetails.getDoD();
    }
    int age = currentDate.getYear() - dateOfBirth.getYear();
    // Handle case where birthday is after current date.
    if (dateOfBirth.getMonthValue() > currentDate.getMonthValue() ||
        (dateOfBirth.getMonthValue() == currentDate.getMonthValue() &&
            dateOfBirth.getDayOfMonth() > currentDate.getDayOfMonth())) {
      age = age - 1;
    }
    return age;
  }


  /**
   * Updates the account title attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new title.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateTitle(User modifyingAccount, String value) {
    String message =
            INVALID_VALUE_MESSAGE_START + value + ". Title must have at most 10 alphabetical characters.\n";
    if (UserValidator.validateAlphanumericString(false, value, 0, 10)) {

      if (title != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, TITLE_FIELD_NAME, title,
                value.toUpperCase());
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, TITLE_FIELD_NAME, UNSPECIFIED,
                value.toUpperCase());
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setTitle(value.toUpperCase());
    }
    return message;
  }


  /**
   * Updates the account firstName attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new firstName.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateFirstName(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". First name must be between 1 and 50 alphabetical characters." +
            " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 1, 50)) {
      if (getFirstName() != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "firstName", getFirstName(),
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "firstName", UNSPECIFIED, value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setFirstName(value);
    }
    return message;
  }


  /**
   * Updates the account middleName attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new middleName.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateMiddleName(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". Middle name must have at most 100 alphabetical characters." +
            " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 0, 100)) {
      if (getMiddleName() != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "middleName", getMiddleName(),
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "middleName", UNSPECIFIED,
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setMiddleName(value);
    }
    return message;
  }


  /**
   * Updates the account lastName attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new lastName.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateLastName(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". Last name must have at most 100 alphabetical characters." +
            " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 0, 100)) {
      if (getLastName() != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "lastName", getLastName(), value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "lastName", UNSPECIFIED, value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setLastName(value);
    }
    return message;
  }


  public String updateNHINumber(User modifyingAccount, String value) {
    String message;
    AccountManager a = App.getDatabase();
    boolean testNHI = UserValidator.validateNHI(value, a.getDonorReceivers());
    if (testNHI) {
      if (UserValidator.checkUsedNHI(value, a.getDonorReceivers())) {
        message = "Error: That NHI number (" + value + ") is already in use. Transaction failed";
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "NHI", getUserName(), value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
        setUserName(value);
      }
    } else {
      message = "ERROR: Invalid NHI number " + value + ". An invalid NHI number has been entered.";
    }
    return message;
  }

  /**
   * Updates the account preferredName attribute if it is valid and logs the result.
   *
   * @param value A String representation of the new preferred name.
   * @param modifyingAccount account to be modified
   * @return A log of the operation as a String object.
   */
  public String updatePreferredName(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". Preferred name must have at most 100 alphabetical characters." +
            " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 0, 100)) {
      if (preferredName != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "preferredName", preferredName,
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "preferredName", UNSPECIFIED,
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setPreferredName(value);
    }
    return message;

  }


  /**
   * Updates the account gender attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new gender.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateGender(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". Gender must be a char value, one of 'M', 'F', 'U', or 'O'.\n";
    if (value.length() > 1 && !value.equalsIgnoreCase("delete")) {
      return message;
    }
    if (value.equalsIgnoreCase("delete")) {
      value = "";
    }
    if (value.equals("") || UserValidator.validateGender(value.charAt(0))) {
      if (gender != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "gender",
                Character.toString(gender), value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "gender", UNSPECIFIED, value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      if (value.equals("")) {
        setGender(null);
      } else {
        setGender(value.charAt(0));
      }
    }
    return message;
  }


  /**
   * Updates the birth gender attribute of the donor account. Validation checks are performed to
   * ensure that the value is valid.
   *
   * @param modifyingAccount account to be modified
   * @param value A String object represnting the new birth gender.
   * @return A String log of the result of the operation.
   */
  public String updateBirthGender(User modifyingAccount, String value) {
    String message = INVALID_VALUE_MESSAGE_START + value
            + ". Birth gender must be a char value, one of 'M' (Male), 'F' (Female), 'U' (Unknown), or 'O' (Other).\n";
    if (value.length() > 1 && !value.equalsIgnoreCase("delete")) {
      return message;
    }
    if (value.equalsIgnoreCase("delete")) {
      value = "";
    }
    if (value.equals("") || UserValidator.validateGender(value.charAt(0))) {
      if (birthGender != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "birthGender",
                Character.toString(birthGender), value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "birthGender", UNSPECIFIED,
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      if (value.equals("")) {
        setBirthGender(null);
      } else {
        setBirthGender(value.charAt(0));
      }
    }

    return message;

  }


  /**
   * Updates the account dateOfBirth attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new dateOfBirth.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateDateOfBirth(User modifyingAccount, String value) {
    LocalDate date;
    String message =
            INVALID_VALUE_MESSAGE_START + value + ". Date of birth must be a valid date that is before the "
                    +
                    "current date. The date also has to be in the following format: " + "'YYYYMMDD'.\n";
    try {
      date = LocalDate
              .of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)),
                      Integer.parseInt(value.substring(6, 8)));
    } catch (Exception e) {
      return message;
    }
    date = LocalDate
            .of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)),
                    Integer.parseInt(value.substring(6, 8)));
    if (UserValidator.validateDateOfBirth(date)) {

      if (dateOfBirth != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "dateOfBirth",
                formatDateToString(dateOfBirth), value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      } else {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, "dateOfBirth", UNSPECIFIED,
                value);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
      setDateOfBirth(date);
    }
    return message;
  }


  /**
   * Updates the account dateOfDeath attribute if it is valid and logs the result.
   *
   * @param value a string representation of the new dateOfDeath.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateDateOfDeath(User modifyingAccount, String value) {
    LocalDateTime date;
    String message =
            INVALID_VALUE_MESSAGE_START + value + ". Date of death must be a valid date that is before the "
                    +
                    "current date "
                    + " and after the donor's date of birth. It also has to be in the following format: " +
                    "'YYYY-MM-DD HH:mm:ss'.\n";
    if (value.equalsIgnoreCase("")) {
      LogEntry logEntry = new LogEntry(this, modifyingAccount, "dateOfDeath",
          formatDateTimeToString(getDateOfDeath()), UNSPECIFIED);
      addToSystemLog(logEntry);
      updateLog.add(logEntry);
      message = logEntry.toString();
      setDateOfDeath(null);
    } else {
      try {
        date  = LocalDateTime.of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(5, 7)),
            Integer.parseInt(value.substring(8, 10)), Integer.parseInt(value.substring(11, 13)),
            Integer.parseInt(value.substring(14, 16)));
      } catch (Exception e) {
        return message;
      }
      date  = LocalDateTime.of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(5, 7)),
          Integer.parseInt(value.substring(8, 10)), Integer.parseInt(value.substring(11, 13)),
          Integer.parseInt(value.substring(14, 16)));
      if (UserValidator.validateDateOfDeath(date, dateOfBirth)) {
        if (getDateOfDeath() != null) {
          LogEntry logEntry = new LogEntry(this, modifyingAccount, "dateOfDeath",
              formatDateTimeToString(getDateOfDeath()), value);
          addToSystemLog(logEntry);
          updateLog.add(logEntry);
          message = logEntry.toString();
        } else {
          LogEntry logEntry = new LogEntry(this, modifyingAccount, "dateOfDeath", UNSPECIFIED,
                  value);
          addToSystemLog(logEntry);
          updateLog.add(logEntry);
          message = logEntry.toString();
        }
        setDateOfDeath(date);
      }
    }
    return message;
  }


  /**
   * Updates the account livedInUKFlag attribute if it is valid and logs the result.
   *
   * @param value a string 'true', or 'false'.
   * @param modifyingAccount account to be modified
   * @return a string log of the result of the operation.
   */
  public String updateLivedInUKFlag(User modifyingAccount, String value) {
    boolean choice;
    try {
      choice = Boolean.parseBoolean(value);
    } catch (Exception e) {
      return ("ERROR: Unknown value " + value + ". Value should be 'true' or 'false'\n");
    }
    choice = Boolean.parseBoolean(value);
    String message = "";

    if (Boolean.valueOf(livedInUKFlag) != null) {
      LogEntry logEntry = new LogEntry(this, modifyingAccount, "livedInUKFlag",
              Boolean.toString(livedInUKFlag), value);
      addToSystemLog(logEntry);
      updateLog.add(logEntry);
      message = logEntry.toString();
    } else {
      LogEntry logEntry = new LogEntry(this, modifyingAccount, "livedInUKFlag", UNSPECIFIED,
              value);
      addToSystemLog(logEntry);
      updateLog.add(logEntry);
      message = logEntry.toString();
    }
    setLivedInUKFlag(choice);
    return message;
  }


  /**
   * Updates the given account attribute with the given value if both parameters are valid. If the
   * process was successful, a log is added to the account update log list. Otherwise an error
   * message is displayed.
   *
   * @param attribute A string representation of the attribute. It should be one of the account
   * attributes
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   * @param modifyingAccount account to be modified
   * @return Message to be displayed
   */
  public String updateProfile(User modifyingAccount, String attribute, String value) {
    String message = "";
    switch (attribute) {
      case "title": {
        message = updateTitle(modifyingAccount, value);
        break;
      }
      case "givenName": {
        message = updateFirstName(modifyingAccount, value);
        break;
      }
      case "otherName": {
        message = updateMiddleName(modifyingAccount, value);
        break;
      }
      case "lastName": {
        message = updateLastName(modifyingAccount, value);
        break;
      }
      case "preferredName": {
        message = updatePreferredName(modifyingAccount, value);
        break;
      }
      case "dateOfBirth": {
        message = updateDateOfBirth(modifyingAccount, value);
        break;
      }
      case "dateOfDeath": {
        message = updateDateOfDeath(modifyingAccount, value);
        break;
      }
      case "gender": {
        message = updateGender(modifyingAccount, value);
        break;
      }
      case "birthGender": {
        message = updateBirthGender(modifyingAccount, value);
        break;
      }
      case "livedInUKFlag": {
        message = updateLivedInUKFlag(modifyingAccount, value);
        break;
      }
      case "NHI": {
        message = updateNHINumber(modifyingAccount, value);
        break;
      }
      default: {
        message =
                "ERROR: unknown attribute " + attribute + ". Attribute should be 'title', 'givenName',"
                        +
                        "'otherName', 'lastName', 'NHI', 'dateOfBirth', 'dateOfDeath', 'gender', or 'livedInUKFlag'.\n";
        break;
      }
    }
    String substring = message.substring(0, 5);
    if (substring.equals(ERROR_PREFIX)) {
      updateMessage = message;
    }
    return message;
  }


  /**
   * Updates the given account UserAttributeCollection attribute with the given value if both
   * parameters are valid. If the process was successful, a log is added to the account update log
   * list. Otherwise an error message is displayed.
   *
   * @param attribute A string representation of the attribute. It should be one of the account
   * attributes
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   * @param modifyingAccount account to be modified
   * @return Messsage to be displayed to the user
   */
  public String updateAttribute(User modifyingAccount, String attribute, String value) {
    String message = "";
    String oldVal = "";
    if (value.equalsIgnoreCase("delete")) {
      value = "";
    }
    switch (attribute) {
      case "height": {
        oldVal = (getUserAttributeCollection().getHeight() == null) ? UNSPECIFIED
                : Double.toString(getUserAttributeCollection().getHeight());
        message = getUserAttributeCollection().updateHeight(value);
        break;
      }
      case "weight": {
        oldVal = (getUserAttributeCollection().getWeight() == null) ? UNSPECIFIED
                : Double.toString(getUserAttributeCollection().getWeight());
        message = getUserAttributeCollection().updateWeight(value);
        break;
      }
      case "bloodType": {
        oldVal = (getUserAttributeCollection().getBloodType() == null) ? UNSPECIFIED
                : getUserAttributeCollection().getBloodType();
        message = getUserAttributeCollection().updateBloodType(value);
        break;
      }
      case "smoker": {
        oldVal = (getUserAttributeCollection().getSmoker() == null) ? UNSPECIFIED
                : Boolean.toString(getUserAttributeCollection().getSmoker());
        message = getUserAttributeCollection().updateSmoker(value);
        break;
      }
      case "bloodPressure": {
        oldVal = (getUserAttributeCollection().getBloodPressure() == null) ? UNSPECIFIED
                : getUserAttributeCollection().getBloodPressure();
        message = getUserAttributeCollection().updateBloodPressure(value);
        break;
      }
      case "chronicDiseases": {
        oldVal = (getUserAttributeCollection().getChronicDiseases() == null) ? UNSPECIFIED
                : getUserAttributeCollection().getChronicDiseases();
        message = getUserAttributeCollection().updateChronicDiseases(value);
        break;
      }
      case "alcoholConsumption": {
        oldVal = (getUserAttributeCollection().getAlcoholConsumption() == null) ? UNSPECIFIED
                : Double.toString(getUserAttributeCollection().getAlcoholConsumption());
        message = getUserAttributeCollection().updateAlcoholConsumption(value);
        break;
      }
      default: {
        message =
                "ERROR: unknown attribute " + attribute + ". Attribute should be 'height', 'weight'," +
                        "'bloodType', 'smoker', 'bloodPressure', 'chronicDiseases', or 'alcoholConsumption'.\n";
        break;
      }
    }
    String substring = message.substring(0, 5);
    if (!substring.equals(ERROR_PREFIX)) {
      LogEntry logEntry = new LogEntry(this, modifyingAccount, attribute, oldVal, value);
      addToSystemLog(logEntry);
      updateLog.add(logEntry);
      message = logEntry.toString();
    } else {
      updateMessage = message;
    }
    return message;
  }


  /**
   * Updates the given account DonorOrganInventory attribute with the given value if both parameters
   * are valid. If the process was successful, a log is added to the account update log list.
   * Otherwise an error message is displayed.
   *
   * @param donReceiv A string representation of whether the command is for a donor or receiver
   * @param attribute A string representation of the attribute. It should be one of the account
   * attributes
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   * @param modifyingAccount account to be modified
   * @return Message to be displayed to the user
   */
  public String updateOrgan(User modifyingAccount, String donReceiv, String attribute,
                            String value) {
    String[] result;
    String message = "";
    String[] organs = new String[]{"liver", "kidneys", "pancreas", "heart", "lungs", "intestines",
            "corneas", "middleEars",
            "skin", "bone", "boneMarrow", "connectiveTissue", "all"};
    List<String> list = Arrays.asList(organs);
    if (list.contains(attribute)) {
      if (donReceiv.equals("receiver")) {
        result = getRequiredOrgans().updateOrganDonation(donReceiv, attribute, value);
        message = result[0];
        if ((value.equalsIgnoreCase("true")) && (message.equalsIgnoreCase("success"))) {
          setReceiver(true);
        }
      } else {
        result = getDonorOrganInventory().updateOrganDonation(donReceiv, attribute, value);
        message = result[0];
      }
      if (result[1] != null) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount, result[1], result[2], result[3]);
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
        message = logEntry.toString();
      }
    } else {
      message =
              "ERROR: unknown attribute " + attribute + ". Attribute should be 'liver', 'kidneys'," +
                      "'pancreas', 'heart', 'lungs', 'intestines', 'corneas', 'middleEars', 'skin', 'bone',"
                      +
                      "'boneMarrow', 'connectiveTissue' or 'all'.\n";
    }
    String substring = message.substring(0, 5);
    if (substring.equals(ERROR_PREFIX)) {
      updateMessage = message;
    }
    return message;
  }


  /**
   * Updates the given attribute in the current instance with the given value. If the message was
   * successful, a log entry is added and a success message is returned. Otherwise, an error message
   * is returned.
   *
   * @param modifyingAccount account to be modified
   * @param attribute A string representation of the attribute. It should be one of the account
   * attributes
   * @param value A string representation of the new value of the attribute. It should be a legal
   * value.
   * @return Message to be displayed to the user
   */
  public String updateContact(User modifyingAccount, String attribute, String value) {

    String message = "ERROR: Cannot recognize attribute + '" + attribute + "'.";
    String oldValue = "";

    // Retrieve the contact details.
    Address address = getContactDetails().getAddress();
    String mobileNum = getContactDetails().getMobileNum();
    String homeNum = getContactDetails().getHomeNum();
    String email = getContactDetails().getEmail();

    // Retrieve emergency contact details.
    Address emergencyAddress = getEmergencyContactDetails().getAddress();
    String emergencyMobileNum = getEmergencyContactDetails().getMobileNum();
    String emergencyHomeNum = getEmergencyContactDetails().getHomeNum();
    String emergencyEmail = getEmergencyContactDetails().getEmail();
    if (value.equalsIgnoreCase("delete")) {
      value = null;
    }
    switch (attribute) {
      case "addressStreet": {
        oldValue = address.getStreetAddressLineOne();
        message = address.updateStreetAddressLineOne(value);
        break;
      }
      case "addressCity": {
        oldValue = address.getCityName();
        message = address.updateCityName(value);
        break;
      }
      case "addressRegion": {
        oldValue = address.getRegion();
        message = address.updateRegion(value);
        break;
      }
      case "addressPostcode": {
        oldValue = address.getPostCode();
        message = address.updatePostCode(value);
        break;
      }
      case "addressCountryCode": {
        oldValue = address.getCountryCode();
        message = address.updateCountryCode(value);
        break;
      }
      case "mobileNumber": {
        oldValue = mobileNum;
        message = getContactDetails().updateMobileNum(value);
        break;
      }
      case "homeNumber": {
        oldValue = homeNum;
        message = getContactDetails().updateHomeNum(value);
        break;
      }
      case "email": {
        oldValue = email;
        message = getContactDetails().updateEmail(value);
        break;
      }
      case "emergAddressStreet": {
        oldValue = emergencyAddress.getStreetAddressLineOne();
        message = emergencyAddress.updateStreetAddressLineOne(value);
        break;
      }
      case "emergAddressCity": {
        oldValue = emergencyAddress.getCityName();
        message = emergencyAddress.updateCityName(value);
        break;
      }
      case "emergAddressRegion": {
        oldValue = emergencyAddress.getRegion();
        message = emergencyAddress.updateRegion(value);
        break;
      }
      case "emergAddressPostcode": {
        oldValue = emergencyAddress.getPostCode();
        message = emergencyAddress.updatePostCode(value);
        break;
      }
      case "emergAddressCountryCode": {
        oldValue = emergencyAddress.getCountryCode();
        message = emergencyAddress.updateCountryCode(value);
        break;
      }
      case "emergMobileNumber": {
        oldValue = emergencyMobileNum;
        message = getEmergencyContactDetails().updateMobileNum(value);
        break;
      }
      case "emergHomeNumber": {
        oldValue = emergencyHomeNum;
        message = getEmergencyContactDetails().updateHomeNum(value);
        break;
      }
      case "emergEmail": {
        oldValue = emergencyEmail;
        message = getEmergencyContactDetails().updateEmail(value);
        break;
      }
    }
    String substring = message.substring(0, 5);
    if (!substring.equals(ERROR_PREFIX)) {
      if (value == null) {
        value = "";
      }
      LogEntry logEntry = new LogEntry(this, modifyingAccount, attribute, oldValue, value);
      addToSystemLog(logEntry);
      updateLog.add(logEntry);
      message = logEntry.toString();
    } else {
      updateMessage = message;
    }
    return message;

  }


  /**
   * Returns the boolean value of the attribute 'active', signifying if the account is active or
   * not.
   *
   * @return returns 'true' if the account is active, 'false' otherwise
   */
  public boolean getActiveFlag() {
    return activeFlag;
  }

  /**
   * Updates the updateLog with the given log.
   *
   * @param log a log entry to be added to the updateLog list
   */
  public void logChange(LogEntry log) {
    updateLog.add(log);
    addToSystemLog(log);
  }


  /**
   * Gets the currentDiagnoses array list.
   *
   * @return the currentDiagnoses array list.
   */
  public List<Illness> getCurrentDiagnoses() {
    return currentDiagnoses;
  }

  /**
   * Gets the historicDiagnoses array list.
   *
   * @return the historicDiagnoses array list.
   */
  public List<Illness> getHistoricDiagnoses() {
    return historicDiagnoses;
  }


  /**
   * Gets the masterIllnessList ArrayList object.
   *
   * @return The master list of illnesses as an ArrayList object.
   */
  public List<Illness> getMasterIllnessList() {
    return masterIllnessList;
  }


  /**
   * Clears the current and historic array lists of Illness objects and then repopulates them with
   * the Illness objects from the masterIllnessList. Cured/resolved Illnesses go into the historic
   * list while the rest go into the current list.
   */
  public void populateIllnessLists() {
    currentDiagnoses.clear();
    historicDiagnoses.clear();
    if (masterIllnessList != null) {
      for (Illness illness : masterIllnessList) {
        if (illness.isCured()) {
          historicDiagnoses.add(illness);
        } else {
          currentDiagnoses.add(illness);
        }
      }
    }

  }


  /**
   * Returns an overview of the account as a String, including the name and the list of organs the
   * account holder has donated.
   *
   * @return The account overview as a String.
   */
  public String generateOverview() {
    String overview = (getFirstName() + " " + getMiddleName() + " " + getLastName()).
            trim().replaceAll("\\s+", " ");
    String donatedOrgans = donorOrganInventory.toString();
    String receivingOrgans = requiredOrgans.toString();
    if (donatedOrgans.equals("Organs to donate:\nNo organs to donate\n\n")) {
      if (receivingOrgans.equals("Organs to receive:\nNo organs to receive\n\n")) {
        return overview;
      } else {
        overview += "\n" + receivingOrgans;
      }
      return overview;
    } else {
      overview += "\n" + donatedOrgans;
      if (receivingOrgans.equals("Organs to receive:\nNo organs to receive\n\n")) {
        return overview;
      } else {
        overview += "\n" + receivingOrgans;
      }
      return overview;
    }
  }

  /**
   * Returns a string of all conflicting (Both receiving and donating) organs or null if there are
   * none
   *
   * @return String A list of conflicting organs
   */
  public String isReceivingDonatingOrgans() {
    if (getRequiredOrgans() == null) {
      return null;
    }
    String toReturn = "Conflicting organs:";
    if (getDonorOrganInventory().getLiver() && getRequiredOrgans().getLiver()) {
      toReturn += "\nLiver";
    }
    if (getDonorOrganInventory().getKidneys() && getRequiredOrgans().getKidneys()) {
      toReturn += "\nKidneys";
    }
    if (getDonorOrganInventory().getLungs() && getRequiredOrgans().getLungs()) {
      toReturn += "\nLungs";
    }
    if (getDonorOrganInventory().getHeart() && getRequiredOrgans().getHeart()) {
      toReturn += "\nHeart";
    }
    if (getDonorOrganInventory().getPancreas() && getRequiredOrgans().getPancreas()) {
      toReturn += "\nPancreas";
    }
    if (getDonorOrganInventory().getIntestine() && getRequiredOrgans().getIntestine()) {
      toReturn += "\nIntestine";
    }
    if (getDonorOrganInventory().getCorneas() && getRequiredOrgans().getCorneas()) {
      toReturn += "\nCorneas";
    }
    if (getDonorOrganInventory().getMiddleEars() && getRequiredOrgans().getMiddleEars()) {
      toReturn += "\nMiddle Ears";
    }
    if (getDonorOrganInventory().getBone() && getRequiredOrgans().getBone()) {
      toReturn += "\nBone";
    }
    if (getDonorOrganInventory().getBoneMarrow() && getRequiredOrgans().getBoneMarrow()) {
      toReturn += "\nBone Marrow";
    }
    if (getDonorOrganInventory().getSkin() && getRequiredOrgans().getSkin()) {
      toReturn += "\nSkin";
    }
    if (getDonorOrganInventory().getConnectiveTissue() && getRequiredOrgans()
            .getConnectiveTissue()) {
      toReturn += "\nConnective Tissue";
    }

    if (toReturn.equals("Conflicting organs:")) {
      return null;
    }
    return toReturn;
  }

  /**
   * Takes the summary, description, dateString, and affected organs of a medical procedure (that
   * may or may not be created yet), and returns whether a medical procedure with the same summary,
   * description, date, and affected organs already exists for this account.
   *
   * @param summary The summary of the medical procedure being checked.
   * @param description The description of the medical procedure being checked.
   * @param dateString The string value of the date on which the procedure is/was performed. Must be
   * in format "dd/mm/yyyy". Can be null if there is no date confirmed for the procedure.
   * @param affectedOrgans An ArrayList containing the names of organs affected by this procedure.
   * The valid organ names are: "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines",
   * "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
   * @return True if there is a procedure that already exists with the same details for this user,
   * false if not.
   */
  public boolean isDuplicateMedicalProcedure(String summary, String description, String dateString,
                                             ArrayList<String> affectedOrgans) {
    boolean duplicate = false;
    Collections.sort(affectedOrgans);
    for (MedicalProcedure procedure : this.medicalProcedures) {
      if (summary.equals(procedure.getSummary())) {
        if (description.equals(procedure.getDescription())) {
          ArrayList<String> procedureOrgans = procedure.getAffectedOrgans();
          Collections.sort(procedureOrgans);
          if (affectedOrgans.equals(procedureOrgans)) {
            if ((dateString == null && procedure.getDate() == null) || dateString != null &&
                    procedure.getDate() != null && dateString.equals(formatDateToStringSlash(procedure.getDate()))) {
              duplicate = true;
            }
          }
        }
      }
    }
    return duplicate;
  }

  /**
   * Takes the summary, description, date, and affected organs of a medical procedure (that may or
   * may not be created yet), and returns whether a medical procedure with the same summary,
   * description, date, and affected organs already exists for this account.
   *
   * @param summary The summary of the medical procedure being checked.
   * @param description The description of the medical procedure being checked.
   * @param date The date on which the procedure is/was performed. Can be null if there is no date
   * confirmed for the procedure.
   * @param affectedOrgans An ArrayList containing the names of organs affected by this procedure.
   * The valid organ names are: "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines",
   * "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
   * @return True if there is a procedure that already exists with the same details for this user,
   * false if not.
   */
  public boolean isDuplicateMedicalProcedure(String summary, String description, LocalDate date,
                                             ArrayList<String> affectedOrgans) {
    boolean duplicate = false;
    Collections.sort(affectedOrgans);
    for (MedicalProcedure procedure : this.medicalProcedures) {
      if (summary.equals(procedure.getSummary())) {
        if (description.equals(procedure.getDescription())) {
          ArrayList<String> procedureOrgans = procedure.getAffectedOrgans();
          Collections.sort(procedureOrgans);
          if (affectedOrgans.equals(procedureOrgans)) {
            if (date == null && procedure.getDate() == null) {
              duplicate = true;
            } else if (date != null && procedure.getDate() != null && date
                    .equals(procedure.getDate())) {
              duplicate = true;
            }
          }
        }
      }
    }
    return duplicate;
  }


  public boolean addMedicalProcedure(String summary, String description,
      String dateString,
      ArrayList<String> affectedOrgans) throws Exception {
      MedicalProcedure newProcedure = new MedicalProcedure(summary, description, dateString,
          this.dateOfBirth, affectedOrgans);
      SyncPostTask task = new SyncPostTask(String.class, ADDRESSES.SERVER.getAddress(),
          String.format(ADDRESSES.USER_PROCEDURE.getAddress(), getUserName()), newProcedure,
          App.getCurrentSession().getToken());
      try {
        task.makeRequest();
        return true;
      } catch (HttpClientErrorException e) {
        // when the task fails
        return false;
      }
  }


    /**
     * Adds a new medical procedure to an account. Returns whether or not the new medical procedure
     * was added to the account. Will return false if the account already had the same medical
     * procedure.
     *
     * @param summary A string containing a short summary of the procedure.
     * @param description A string containing a longer description of the procedure.
     * @param dateString The string value of the date on which the procedure is/was performed. Must be
     * in format "dd/mm/yyyy". Can be null if there is no date confirmed for the procedure.
     * @param affectedOrgans An ArrayList containing the names of organs affected by this procedure.
     * The valid organ names are: "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines",
     * "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
     * @param modifyingAccount account to be modified
     * @throws Exception Throws exception from {@link MedicalProcedure} class with message {@link
     * MedicalProcedure#invalidDateErrorMessage} if the date provided is not a valid date. Throws
     * exception from {@link MedicalProcedure} class with message {@link
     * MedicalProcedure#nullDOBErrorMessage} if the date of birth provided is null. Throws exception
     * from {@link MedicalProcedure} class with message {@link MedicalProcedure#procedureDateTooEarlyErrorMessage}
     * if the procedure is more than 12 months before the date of birth. Throws exception from {@link
     * MedicalProcedure} class with message {@link MedicalProcedure#invalidOrgansErrorMessage} if the
     * organ names in affectedOrgans are not valid (i.e. not in the list of valid organ names {@link
     * MedicalProcedure#getOrganNames()}). Throws exception with message {@link
     * #PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE} if the account already has a procedure with details the
     * same as the updated procedure (same summary, description, date, and affected organs).
     */
    public void addMedicalProcedure(User modifyingAccount, String summary, String description,
                                    String dateString,
                                    ArrayList<String> affectedOrgans) throws Exception {
        // Check that the procedure is unique
        boolean duplicate = isDuplicateMedicalProcedure(summary, description, dateString,
                new ArrayList<>(affectedOrgans));
        if (duplicate) {
            throw new IllegalArgumentException(PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE);
        } else {
            MedicalProcedure newProcedure = new MedicalProcedure(summary, description, dateString,
                    this.dateOfBirth, affectedOrgans);
            this.medicalProcedures.add(newProcedure);
            LogEntry logEntry = new LogEntry(this, modifyingAccount,
                    "Medical Procedure '" + newProcedure.getSummary() + "' with description: '" +
                            newProcedure.getDescription() + "', date: '" + DonorReceiver
                            .formatDateToStringSlash(newProcedure.getDate()) +
                            "' has been added.", "non-existent", "added");
            addToSystemLog(logEntry);
            updateLog.add(logEntry);
        }
    }



  /**
   * Returns whether or not the medical procedure matches a medical procedure of this account.
   *
   * @param procedure The medical procedure to be tested.
   * @return True if the medical procedure belongs to this account, otherwise false.
   */
  private boolean medicalProcedureBelongsToAccount(MedicalProcedure procedure) {
    boolean belongs = false;
    int i;
    for (i = 0; i < this.getMedicalProcedures().size(); i++) {
      if (procedure.equals(medicalProcedures.get(i))) {
        belongs = true;
      }
    }
    return belongs;
  }

  public boolean updateMedicalProcedure(MedicalProcedure procedure, String summary)
      throws Exception {
      EditPaneController.CURRENT_VERSION++;
    MedicalProcedurePatch medicalProcedurePatch = new MedicalProcedurePatch(summary, procedure.getSummary(), procedure.getDescription(), procedure.getDate(), procedure.getAffectedOrgans(), Integer.toString(EditPaneController.CURRENT_VERSION));
    SyncPatchTask task = new SyncPatchTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_PROCEDURE.getAddress(), getUserName()), medicalProcedurePatch, App.getCurrentSession().getToken());
    try {
      task.makeRequest();
      return true;
    } catch (HttpClientErrorException e) {
      return false;
    }
  }



    /**
     * Updates the summary, description, date, and affected organs of the given procedure for this
     * account. Logs the change to the summary, description, date or affected organs in the account's
     * log based on whether their contents changed due to the update. If anything goes wrong (i.e. an
     * exception is thrown) as a result of the update, then any changes are reversed, and nothing is
     * written to the account's log.
     *
     * @param procedure The medical procedure that is being updated.
     * @param summary The summary that the medical procedure will be set to have.
     * @param description The description that the medical procedure will be set to have.
     * @param dateString The string value of the date on which the procedure is/was performed. Must be
     * in format "dd/mm/yyyy". Can be null if there is no date confirmed for the procedure.
     * @param affectedOrgans An ArrayList containing the names of organs affected by this procedure.
     * The valid organ names are: "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines",
     * "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
     * @param modifyingAccount account to be modified
     * @throws Exception Throws exception with message {@link #PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE} if
     * the procedure is not in the list of medical procedures for this account. Throws exception with
     * message {@link #PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE} if the account already has a procedure with
     * details the same as the updated procedure (same summary, description, date, and affected
     * organs). Throws exception from {@link MedicalProcedure} class with message {@link
     * MedicalProcedure#invalidOrgansErrorMessage} if the organ names in affectedOrgans are not valid
     * (i.e. not in the list of valid organ names {@link MedicalProcedure#getOrganNames()}). Throws
     * exception from {@link MedicalProcedure} class with message {@link
     * MedicalProcedure#invalidDateErrorMessage} if the date provided is not a valid date. Throws
     * exception from {@link MedicalProcedure} class with message {@link
     * MedicalProcedure#nullDOBErrorMessage} if the date of birth provided is null. Throws exception
     * from {@link MedicalProcedure} class with message {@link MedicalProcedure#procedureDateTooEarlyErrorMessage}
     * if the procedure is more than 12 months before the date of birth.
     */
    public void updateMedicalProcedure(User modifyingAccount, MedicalProcedure procedure,
                                       String summary, String description, String dateString, ArrayList<String> affectedOrgans)
            throws Exception {
    if (!medicalProcedureBelongsToAccount(procedure)) {
      throw new IllegalArgumentException(PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE);
    }
    String originalSummary = procedure.getSummary();
    String originalDescription = procedure.getDescription();
    LocalDate originalDate = procedure.getDate();
    String originalDateString = DonorReceiver.formatDateToStringSlash(procedure.getDate());
    ArrayList<String> originalAffectedOrgans = procedure.getAffectedOrgans();
    try {
      if (isDuplicateMedicalProcedure(summary, description, dateString,
              new ArrayList<>(affectedOrgans))) {
        throw new IllegalArgumentException(PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE);
      }
      procedure.setSummary(summary);
      procedure.setDescription(description);
      procedure.setDate(dateString, this.dateOfBirth);
      procedure.setAffectedOrgans(affectedOrgans);
      // log the changes
      if (!summary.equals(originalSummary)) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount,
                "Summary of medical procedure '" + procedure.getSummary() + "'", originalSummary,
                procedure.getSummary());
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
      }
      if (!description.equals(originalDescription)) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount,
                "Description of medical procedure '" + procedure.getSummary() + "'",
                originalDescription, procedure.getDescription());
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
      }
      //handle if the date is null
      if (dateHasChanged(procedure.getDate(), originalDate)) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount,
                "Date of medical procedure '" + procedure.getSummary() + "'", originalDateString,
                formatDateToStringSlash(procedure.getDate()));
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
      }
      Collections.sort(originalAffectedOrgans);
      Collections.sort(affectedOrgans);
      if (!affectedOrgans.equals(originalAffectedOrgans)) {
        LogEntry logEntry = new LogEntry(this, modifyingAccount,
                "Affected Organs of medical procedure '" + procedure.getSummary() + "'",
                originalAffectedOrgans.toString(), procedure.getAffectedOrgans().toString());
        addToSystemLog(logEntry);
        updateLog.add(logEntry);
      }
    } catch (Exception ex) {
      // reverse changes
      procedure.setSummary(originalSummary);
      procedure.setDescription(originalDescription);
      if (originalDate == null) {
        originalDateString = null;
      }
      procedure.setDate(originalDateString, this.dateOfBirth);
      procedure.setAffectedOrgans(originalAffectedOrgans);
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  /**
   * Deletes the given medical procedure from this account's list of medical procedures, and logs
   * the change for this account. Throws exception if the procedure does not belong to this
   * account.
   *
   * @param procedure The medical procedure that is being deleted.
   * @param modifyingAccount account to be modified
   * @throws Exception Throws exception with message {@link #PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE} if
   * the procedure is not in the list of medical procedures for this account.
   */
  public void deleteMedicalProcedure(User modifyingAccount, MedicalProcedure procedure)
          throws Exception {
    if (!medicalProcedureBelongsToAccount(procedure)) {
      throw new IllegalArgumentException(PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE);
    }
    String summary = procedure.getSummary();
    String description = procedure.getDescription();
    String date = DonorReceiver.formatDateToStringSlash(procedure.getDate());
    medicalProcedures.remove(procedure);
    LogEntry logEntry = new LogEntry(this, modifyingAccount,
            "Medical Procedure '" + summary + "' with description: '" + description + "', and date: '" +
                    date + "'", "active", "deleted");
    addToSystemLog(logEntry);
    updateLog.add(logEntry);
  }

  /**
   * Extracts the list of past procedures from the list of medical procedures for this account. Past
   * procedures are any procedures for which the date is today or before today. If the date of a
   * procedure is null it is not included as a past procedure.
   *
   * @return The list of past procedures for this account.
   */
  public ArrayList<MedicalProcedure> extractPastProcedures() {
    ArrayList<MedicalProcedure> pastProcedures = new ArrayList<>();
    int i;
    LocalDate today = LocalDate.now();
    for (i = 0; i < this.getMedicalProcedures().size(); i++) {
      LocalDate date = medicalProcedures.get(i).getDate();
      if (date != null && !(date.isAfter(today))) {
        pastProcedures.add(medicalProcedures.get(i));
      }
    }
    return pastProcedures;
  }

  /**
   * Extracts the list of pending procedures from the list of medical procedures for this account.
   * Pending procedures are any procedures for which the date is after today. If the date of a
   * procedure is null then the procedure is included as a pending procedure.
   *
   * @return The list of pending procedures for this account.
   */
  public ArrayList<MedicalProcedure> extractPendingProcedures() {
    ArrayList<MedicalProcedure> pendingProcedures = new ArrayList<>();
    int i;
    LocalDate today = LocalDate.now();
    for (i = 0; i < this.getMedicalProcedures().size(); i++) {
      LocalDate date = medicalProcedures.get(i).getDate();
      if (date == null || date.isAfter(today)) {
        pendingProcedures.add(medicalProcedures.get(i));
      }
    }
    return pendingProcedures;
  }

  /**
   * Method to determine if the date of a medical procedure has changed during an update, as using
   * the .equals method throws a NullPointerException when one (or both) of the dates are null.
   *
   * @param newDate The new (updated) date of the procedure.
   * @param originalDate The original date of the procedure.
   * @return Returns whether or not the date has changed. True if the date has changed, false if the
   * date is the same.
   */
  private boolean dateHasChanged(LocalDate newDate, LocalDate originalDate) {
    // if they are both null
    if (newDate == null && originalDate == null) {
      return false;
    } else if (originalDate == null || newDate == null) {
      // if original is null, new is not null
      // or if new is null and original is not null
      return true;
    } else {
      return (!newDate.equals(originalDate));
    }
  }

  public ContactDetails getEmergencyContactDetails() {
    return emergencyContactDetails;
  }

  public void setEmergencyContactDetails(ContactDetails emergencyContactDetails) {
    this.emergencyContactDetails = emergencyContactDetails;
  }

  public void setMedications(Medications meds) {
    medications = meds;
  }

  public void setMasterIllnessList(List<Illness> ml) {
    masterIllnessList = ml;
  }

  public void setMedicalProcedures(List<MedicalProcedure> mp) {
    medicalProcedures = mp;
  }

  /**
   * Clears the deathdetails object of the donor receiver
   */
  public void clearDeathDetails(User modifyingAccount) {
    deathDetails.clear();
    LogEntry logEntry = new LogEntry(this, modifyingAccount, "DeathDetails", "all", "cleared");
    addToSystemLog(logEntry);
    updateLog.add(logEntry);
    deathDetails.clear();

  }

  /**
   * Updates the death details accordingly
   * @param country
   * @param region
   * @param city
   * @param dateTimeOfDeath
   */
  public void updateDeathDetails(User modifyingAccount,String country, String region, String city, LocalDateTime dateTimeOfDeath) {
    LogEntry logEntry = new LogEntry(this, modifyingAccount, "DeathDetailsCountry",
            deathDetails.getCountry(), country);
    LogEntry logEntry2 = new LogEntry(this, modifyingAccount, "DeathDetailsRegion",
            deathDetails.getRegion(), region);
    LogEntry logEntry3 = new LogEntry(this, modifyingAccount, "DeathDetailsCity",
            deathDetails.getCity(), city);
    LogEntry logEntry4;
    try {
      logEntry4 = new LogEntry(this, modifyingAccount, "DeathDetailsDOD",
              deathDetails.getDoD().toString(), dateTimeOfDeath.toString());
    } catch (NullPointerException e) {
      logEntry4 = new LogEntry(this, modifyingAccount, "DeathDetailsDOD",
              "", dateTimeOfDeath.toString());
    }
    addToSystemLog(logEntry);
    updateLog.add(logEntry);
    addToSystemLog(logEntry2);
    updateLog.add(logEntry2);
    addToSystemLog(logEntry3);
    updateLog.add(logEntry3);
    addToSystemLog(logEntry4);
    updateLog.add(logEntry4);
    deathDetails.update(country, region, city, dateTimeOfDeath);
  }

}
