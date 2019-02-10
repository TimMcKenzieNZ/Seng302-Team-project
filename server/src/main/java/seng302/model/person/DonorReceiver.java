package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import seng302.model.DonorOrganInventory;
import seng302.model.Illness;
import seng302.model.MedicalProcedure;
import seng302.model.Medications;
import seng302.model.ReceiverOrganInventory;
import seng302.model.utility.StringExtension;
import seng302.model.UserAttributeCollection;

/**
 * A class to store all the Donor's personal information as well as methods to validate the user
 * input when they create the class.
 */
public class DonorReceiver extends User {

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
   * An instance of the class DeathDetails which contains information regarding the death of the donorReceiver
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
  private ArrayList<LogEntry> updateLog = new ArrayList<>();

  /**
   * A boolean that signifies whether the new donor lived in the UK, Ireland, or France during the
   * period of 1980 to 1996 for more than 6 months. This is a conditional requirement of all *blood*
   * donors as they could be carrying Variant Creutzfeldt-Jakob (Mad Cow) disease. This may affect a
   * donor's eligibility to donate organs. See: https://www.nzblood.co.nz/give-blood/donating/am-i-eligible/variant-creutzfeldt-jakob-disease-vcjd/
   */
  private Boolean livedInUKFlag;


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


  private ArrayList<MedicalProcedure> medicalProcedures = new ArrayList<>();
  public static final String PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE = "Procedure already exists for this user";
  public static final String PROCEDURE_DOES_NOT_BELONG_ERROR_MESSAGE = "Procedure does not belong to this account";
  public static final String UNSPECIFIED = "unspecified";

  /**
   * An arrayList of Illness objects which represent the illnesses/diseases the donor has suffered
   * from, both past and present.
   */
  private ArrayList<Illness> masterIllnessList = new ArrayList<>();


  /**
   * An arrayList of Illness objects which represent the illnesses/diseases that the donor currently
   * suffers from.
   */
  @JsonIgnore
  private ArrayList<Illness> currentDiagnoses = new ArrayList<>();


  /**
   * An arrayList of Illness objects which represent the illnesses/diseases that the donor has been
   * diagnosed with in the past that have been resolved.
   */
  @JsonIgnore
  private ArrayList<Illness> historicDiagnoses = new ArrayList<>();

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
        new ContactDetails(new Address(null, null, null, null, null, null, null),
            null, null, null), nhi, null, null, null, 1);
    dateOfBirth = DOB;
    userAttributeCollection = new UserAttributeCollection();
    donorOrganInventory = new DonorOrganInventory();
    requiredOrgans = new ReceiverOrganInventory();
    medications = new Medications();
    creationTimeStamp = LocalDateTime.now();
    emergencyContactDetails = new ContactDetails(new Address(null, null,
        null, null, null, null, null),
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
        new ArrayList<>(), 1);
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
  }


  public DeathDetails getDeathDetails() {
    return deathDetails;
  }

  public void setDeathDetails(DeathDetails deathDetails) {
    this.deathDetails = deathDetails;
  }

  public void setCurrentDiagnoses(ArrayList<Illness> currentDiagnoses) {

    this.currentDiagnoses = currentDiagnoses;
  }

  public void setHistoricDiagnoses(ArrayList<Illness> historicDiagnoses) {
    this.historicDiagnoses = historicDiagnoses;
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
   * @param masterIllnessList An ArrayList of Illness objects.
   * @param active Whether or not the account is active
   * @param medicalProcedures medical procedures of donor
   * @param required required organs of donor
   * @param creationDate creation date of the account
   * @param modifications modifications of the account
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
      @JsonProperty("livedInUKFlag") Boolean flag,
      @JsonProperty("active") boolean active,
      @JsonProperty("masterIllnessList") List<Illness> masterIllnessList,
      @JsonProperty("medicalProcedures") List<MedicalProcedure> medicalProcedures,
      @JsonProperty("version") int version,
      @JsonProperty("receiver") boolean receiver,
      @JsonProperty("deathDetails") DeathDetails deathDetails) {
    super(firstName, middleName, lastName, contactDetails, userName, password, creationDate, new ArrayList<>(modifications), version); //Need a json constructor in the superclasses
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
    this.activeFlag = active;
    if (masterIllnessList == null) {
      masterIllnessList = new ArrayList<>();
    }
    this.masterIllnessList = new ArrayList<>(masterIllnessList);
    populateIllnessLists();
    if (emergencyContactDetails == null) {
      this.emergencyContactDetails = new ContactDetails(
          new Address(null, null, null, null, null, null, null)
          , null, null, null);
    } else {
      this.emergencyContactDetails = emergencyContactDetails;
    }
    if (medicalProcedures != null) {
      this.medicalProcedures = new ArrayList<>(medicalProcedures);
    }
    setVersion(version);
    this.receiver = receiver;
    this.deathDetails = deathDetails;
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

  public void setDateOfDeath(LocalDateTime dod) {
    deathDetails.setDoD(dod);
  }

  public LocalDateTime getDateOfDeath() {
    return deathDetails.getDoD();
  }

  public Character getGender() {
    return gender;
  }

  public Character getBirthGender() {
    return birthGender;
  }

  public LocalDateTime getCreationTimeStamp() {
    return creationTimeStamp;
  }

  public ArrayList<LogEntry> getUpdateLog() {
    return updateLog;
  }

  public boolean getLivedInUKFlag() {
    return livedInUKFlag;
  }

  public ArrayList<MedicalProcedure> getMedicalProcedures() {
    return this.medicalProcedures;
  }

  public void setUserAttributeCollection(UserAttributeCollection userAttColl) {
    userAttributeCollection = userAttColl;
  }

  public void setDonorOrganInventory(DonorOrganInventory donorInv) {
    donorOrganInventory = donorInv;
  }

  public void setUpdateLog(ArrayList<LogEntry> newLog) {
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


  /**
   * Get the string representation of gender
   * @return The string representation of gender
   */
  public String genderString() {
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
  }


  /**
   * Get the string representation of birth gender
   * @return The string representation of birth gender
   */
  public String birthGenderString() {
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
  }


  /**
   * Returns the title with the first letter uppercase and the rest lowercase
   *
   * @return title String title of the account holder
   */
  public String titleString() {
    String titleValue = "" + this.title;
    if (titleValue.length() > 1) {
      titleValue = titleValue.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
    } else if (titleValue.length() > 0) {
      titleValue = titleValue.substring(0, 1).toUpperCase();
    }
    return titleValue;
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

  public static LocalDateTime formatStringToExtendedDateTime(String time) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(time, dateFormat);
  }

  /**
   * Converts a string to a minimal dateTime of the format YYYY-MM-dd
   * Converts it to a date first to prevent temporal errors
   * @param time The time to convert
   * @return A LocalDateTime representation of the string
   */
  public static LocalDateTime formatStringToMinimalDateTime(String time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    dateFormat.applyPattern("YYYY-MM-dd");
    try {
      Date date = dateFormat.parse(time);
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    }
    catch (ParseException e) {
      return null;
    }
  }

  public static LocalDate formatStringToMinimalDate(String time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    dateFormat.applyPattern("YYYY-MM-dd");
    try {
      Date date = dateFormat.parse(time);
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    }
    catch (ParseException e) {
      return null;
    }
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
   * Changes the gender to the new value.
   *
   * @param gender a char, either M,F,O, or U
   */
  public void setGender(char gender) {
    this.gender = Character.toUpperCase(gender);
  }

  /**
   * Changes the birth gender to the new value.
   *
   * @param birthGender A char which is either M, F, O, or U.
   */
  public void setBirthGender(char birthGender) {
    this.birthGender = birthGender;
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
   * Gets the currentDiagnoses array list.
   *
   * @return the currentDiagnoses array list.
   */
  public ArrayList<Illness> getCurrentDiagnoses() {
    return currentDiagnoses;
  }

  /**
   * Gets the historicDiagnoses array list.
   *
   * @return the historicDiagnoses array list.
   */
  public ArrayList<Illness> getHistoricDiagnoses() {
    return historicDiagnoses;
  }


  /**
   * Gets the masterIllnessList ArrayList object.
   *
   * @return The master list of illnesses as an ArrayList object.
   */
  public ArrayList<Illness> getMasterIllnessList() {
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
    for (Illness illness : masterIllnessList) {
      if (illness.isCured()) {
        historicDiagnoses.add(illness);
      } else {
        currentDiagnoses.add(illness);
      }
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

  public void setMasterIllnessList(ArrayList<Illness> ml) {
    masterIllnessList = ml;
  }

  public void setMedicalProcedures(ArrayList<MedicalProcedure> mp) {
    medicalProcedures = mp;
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


}
