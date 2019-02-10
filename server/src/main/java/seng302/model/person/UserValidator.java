package seng302.model.person;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng302.model.DonorOrganInventory;
import seng302.model.Illness;
import seng302.model.MedicalProcedure;
import seng302.model.Medications;
import seng302.model.ReceiverOrganInventory;
import seng302.model.UserAttributeCollection;

public class UserValidator {


  /**
   * Report for user attribute validation. It is automatically generated upon construction of the
   * UserValidation class.
   */
  private UserValidationReport report;


  /**
   * Constructor for donor validation. Upon construction the user validator will validate the given
   * donor object and create a report of the results.
   *
   * @param donor A donor account to validate
   * @param donors A Linked Hashmap of Donors with their nhi numbers as the keys
   */
  public UserValidator(DonorReceiver donor, LinkedHashMap<String, User> donors) {
    report = new UserValidationReport(donor.getUserName());
    validateDonor(donor, donors);
  }


  /**
   * Constructor for clinician validation. Upon construction the user validator will validate the
   * given clinician object and create a report of the results.
   *
   * @param clinician a clinician account to validate
   * @param clinicians A Linked Hashmap of Clinicians with their staff numbers as the keys
   */
  public UserValidator(Clinician clinician, LinkedHashMap<String, User> clinicians) {
    report = new UserValidationReport(clinician.getUserName());
    validateClinician(clinician, clinicians);
  }


  /**
   * Constructor for admin validation. Upon construction the user validator will validate the given
   * admin object and create a report of the results.
   *
   * @param admin A admin account to validate
   * @param admins A Linked Hashmap of Admins with their usernames as the keys
   */
  public UserValidator(Administrator admin, LinkedHashMap<String, User> admins) {
    report = new UserValidationReport(admin.getUserName());
    validateAdministrator(admin, admins);
  }


  public UserValidationReport getReport() {
    return report;
  }

  /**
   * Checks whether the given date of birth, time, is less than the current time.
   *
   * @param time A LocalDate object.
   * @return a boolean, 'true' if the time is valid, 'false' otherwise.
   */
  public static boolean validateDateOfBirth(LocalDate time) {
    boolean check = true;
    if (time.isAfter(LocalDate.now())) {
      check = false;
    }
    return check;
  }


  /**
   * Validates the phone number specified by 'phoneNumber'. A phone number is considered valid if
   * the String object representing it is not null and its numeric value is not negative.
   * @param phoneNumber The phone number to validate as a String object.
   * @return A boolean value which is true if the phone number is valid, or false if it isn't.
   */
  public static boolean validatePhoneNumber(String phoneNumber) {

    boolean valid = true;

    try {

      valid = (phoneNumber != null && Integer.parseInt(phoneNumber) >= 0);

    } catch (NumberFormatException exception) {

      valid = false;

    }

    return valid;

  }


  /**
   * checks whether the given date of death, is greater than the donor's date of birth (if it
   * exists). It also checks if the date of death is before the current time and so valid (we assume
   * no divination here!).
   *
   * @param dateOfDeath a LocalDateTime object of the donor's date of death.
   * @param dateOfBirth a LocalDate object of the donor's date of birth.
   * @return returns a boolean, 'true' if the time is valid, 'false' otherwise.
   */
  public static boolean validateDateOfDeath(LocalDateTime dateOfDeath, LocalDate dateOfBirth) {
    boolean check = true;
    if (dateOfBirth != null) {
      LocalDateTime tempDob = LocalDateTime.of(dateOfBirth.getYear(), dateOfBirth.getMonthValue(), dateOfBirth.getDayOfMonth(), 0, 0);
      if (dateOfDeath.isBefore(tempDob)) {
        check = false;
      }
    }
    if (dateOfDeath.isAfter(LocalDateTime.now())) {
      check = false;
    }
    return check;
  }


  /**
   * Checks whether the given gender code is valid, where gender is an element of {M,F,O,U}.
   *
   * @param gen a char code representing a gender.
   * @return returns a boolean, 'true' if the gender is valid, 'false' otherwise. (we are not making
   * an ideological statement here :D)
   */
  public static boolean validateGender(char gen) {
    char[] codes = {'M', 'F', 'O', 'U'};
    if ((new String(codes).contains(Character.toString(gen)))) {
      return true;
    }
    return false;
  }


  /**
   * Checks if the given height is a reasonable value.
   *
   * @param height the height of the donor.
   * @return returns true if the height of the donor is within 'normal' bounds, false otherwise.
   */
  public static boolean validateHeight(double height) {
    return height >= 0.0 && height <= 4.0;
  }

  /**
   * Checks if the given weight is greater than 0.0kg. T
   *
   * @param weight the weight of the donor.
   * @return returns 'true' if the weight of the donor is greater than or equal to 0.0 kilograms,
   * 'false' otherwise.
   */
  public static boolean validateWeight(double weight) {
    return weight >= 0.0;
  }


  /**
   * Checks if the given blood type string is a valid blood type.
   *
   * @param bloodType A string blood type code.
   * @return returns 'true' if bloodType is valid, 'false' otherwise.
   */
  public static boolean validateBloodType(String bloodType) {
    if (bloodType.equalsIgnoreCase("")) {
      return true;
    }
    String bloodTypePattern = "^(A|B|O|AB)[+-]$";
    Pattern pattern = Pattern.compile(bloodTypePattern);
    Matcher matcher = pattern.matcher(bloodType);
    return matcher.matches();
  }


  /**
   * Checks if the given string is alphanumeric (if isAlphanumeric is 'true'), or if the string is
   * alphabetical (if isAlphanumeric is 'false'). The string is also allowed to contain spaces,
   * apostrophes, commas, and dashes (-). Also checks if the string is between minLength and
   * maxLength characters (inclusive).
   *
   * @param isAlphanumeric a boolean that sets whether to check a string is Alphanumeric (true) or
   * Alphabetical (false).
   * @param string A string to be validated.
   * @param minLength an int representing the strings minimum required size. minLength should be
   * less than or equal to maxLength.
   * @param maxLength an int representing the string's maximum required size.
   * @return returns 'true' if the string meets the specified conditions, 'false' otherwise.
   */
  public static boolean validateAlphanumericString(boolean isAlphanumeric, String string,
      int minLength, int maxLength) {
    String numbers = "";
    if (minLength > maxLength) {
      return false;
    }
    if (isAlphanumeric) {
      numbers = "0-9";
    }
    try {
      String regularExpression =
          "^[a-zA-Z\\s-'," + numbers + "]{" + minLength + "," + maxLength + "}$";
      Pattern pattern = Pattern.compile(regularExpression);
      Matcher matcher = pattern.matcher(string);
      return matcher.matches();
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Checks if the given postcode is a represented with 4 digits (the given postCode may be padded
   * with zeros) and is between 0001 and 9999. Assumes there is no postcode '0000'. (unverified).
   *
   * @param postCode an int
   * @return returns 'true' if the post code has a 4 digit representation, 'false' otherwise.
   */
  public static boolean validatePostCode(int postCode) {
    String postCodeString = Integer.toString(postCode);
    String formattedString = postCodeString.format("%04d", postCode);
    return postCode > 0 && postCode <= 9999 && formattedString.length() == 4;
  }


  /**
   * Checks if the given password string is alphanumeric and is between 6 and 30 characters
   * (inclusive) long.
   *
   * @param password password to check
   * @return boolean of password orrect format
   */
  public static boolean validatePassword(String password) {
    String numbers = "";
    try {
      String regularExpression = "^[a-zA-Z0-9]{6,30}$";
      Pattern pattern = Pattern.compile(regularExpression);
      Matcher matcher = pattern.matcher(password);
      return matcher.matches();
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Checks if the given id can be successfully parsed as an integer and is greater than zero.
   *
   * @param id A string representation of an integer.
   * @return Returns 'true' if the given id can be parsed as an int and is greater than zero,
   * returns 'false' otherwise.
   */
  public static boolean validateStaffIDIsInt(String id) {
    try {
      int i = Integer.parseInt(id);
      return i > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  /**
   * Checks if the NHI given is already in use within the active database
   *
   * @param NHI The national health index number of the account holder
   * @param database A Map of all the active donorReceivers to check the nhi number against.
   * @return A boolean variable  indicating whether the number is already being used.
   */
  public static boolean checkUsedNHI(String NHI, Map<String, DonorReceiver> database) {
    return database.get(NHI) != null;
  }


  /**
   * Checks whether the given NHI is valid and whether it is already being used in the database.
   *
   * @param NHI A String representing the donor's unique National Health Index code
   * @param database A Map of all the active/deactive donorReceivers to check the nhi number
   * against.
   * @return a boolean, 'true' if the NHI is valid, 'false' otherwise.
   */
  public static boolean validateNHI(String NHI, Map<String, DonorReceiver> database) {
    if (NHI.length() != 7) {
      return false;
    }
    if (database.get(NHI) != null && database.get(NHI).isActive()) {
      return false;
    }
    return checkNHIRegex(NHI);
  }


  /**
   * Checks whether an NHI is valid using regex patterns - It is exactly 7 characters long. - The
   * first 3 are uppercase alphabetical letters, and no letter can be 'I' or 'O'. - The next 3 are
   * any digits. - The last character is a non-zero digit. - The NHI number isn't already present in
   * the List of accounts. That is, there are no duplicate NHI numbers and therefore duplicate
   * accounts.
   *
   * @param NHI The NHI we want to check
   * @return Boolean Whether the NHI is a valid one or not
   */
  public static boolean checkNHIRegex(String NHI) {
    return NHI.matches("^[A-HJ-NP-Za-hj-np-z]{3}[0-9]{4}$");
  }


  /**
   * Checks whether an administrator username is valid or not (Username already taken or invalid
   * characters in username) The pattern is alphanumeric and underscores(_) allowed.
   *
   * @param username The username the admin wishes to use when logging in
   * @return boolean A boolean for whether the username is valid or not
   */
  public static boolean validateAdminUsername(String username) {
    String regularExpressionCharacters = "^[a-zA-Z0-9_]{3,30}$";
    Pattern patternCharacters = Pattern.compile(regularExpressionCharacters);
    Matcher matcher = patternCharacters.matcher(username);

    if (matcher.matches()) {
      String containsLetter = "^(.*[a-zA-Z].*)$";
      Pattern containsLetterPattern = Pattern.compile(containsLetter);
      Matcher matcherLetter = containsLetterPattern.matcher(username);
      if (username.equals("Sudo")) {
        return false;
      }

      return matcherLetter.matches(); // Return true if there is a match otherwise false
    }

    return false;
  }


  /**
   * Checks if the given staff id has not been used by other clinicians.
   *
   * @param id a staff id.
   * @param clinicians A Map of existing clinicians to check the staff id against.
   * @return Returns 'false' if the given ID is already in use, returns 'true' otherwise.
   */
  public static boolean checkStaffIDIsNotUsed(String id, Map<String, Clinician> clinicians) {
    return clinicians.get(id) == null;
  }


  /**
   * Validates a positive double.
   *
   * @param value The value to be validated as a String.
   * @return True if the value is a positive double, false otherwise.
   */
  public static boolean validatePositiveDouble(String value) {
    try {
      Double newDouble = Double.parseDouble(value);
      if (newDouble < 0) {
        return false;
      } else {
        return true;
      }
    } catch (Exception exception) {
      return false;
    }
  }


  /**
   * Validates a String representing blood pressure. Returns true if String is of the form "systolic
   * pressure/diastolic pressure".
   *
   * @param value The String to be validated.
   * @return True if the String is valid, false otherwise.
   */
  public static boolean validateBloodPressure(String value) {
    try {
      String[] splitValue = value.split("/");
      if (splitValue.length == 2) {
        Double systolic = Double.parseDouble(splitValue[0]);
        Double diastolic = Double.parseDouble(splitValue[1]);
        return true;
      } else {
        return false;
      }
    } catch (Exception exception) {
      return false;
    }
  }


  /**
   * Parses and validates a string meant to represent a boolean value. Returns null if the string
   * cannot be parsed.
   *
   * @param value The boolean represented as a string.
   * @return A Boolean object.
   */
  public static Boolean validateBoolean(String value) {
    if (value.toLowerCase().equals("true")) {
      return true;
    } else if (value.toLowerCase().equals("false")) {
      return false;
    } else {
      return null;
    }

  }


  /**
   * Validates the username, password, firstname, and lastname attributes of the given user for null
   * values. If there are null values, the user is invalid and the given user report is given the
   * status "INVALID" as well as be given a log of the particular issue.
   *
   * @param report the UserValidationReport relating to the User being validated
   * @param user the User whose attributes are being validated
   */
  private void validateUserAttributesAreNotMissing(UserValidationReport report, User user) {
    if (user.getUserName() == null) {
      report.addIssue("Missing username");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
    if (user.getFirstName() == null) {
      report.addIssue("Missing first name");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
    if (user.getLastName() == null) {
      report.addIssue("Missing last name");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
  }


  /**
   * Checks if the non-essential user attributes (modifications, middle name, and creation date) of
   * the given user are not null. If they are, they are given default values and the given report is
   * updated with this result and the report's status is set to "repaired".
   *
   * @param report the UserValidationReport relating to the User being repaired
   * @param user the User whose attributes are being validated
   */
  private void repairMissingUserAttributesIfTheyDontExist(UserValidationReport report, User user) {
    if (user.getModifications() == null) {
      user.setModifications(new ArrayList<>());
      report.addIssue("Missing modifications list");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (user.getCreationDate() == null) {
      user.setCreationDate(LocalDateTime.now());
      report.addIssue("Missing creation date");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
  }


  /**
   * Checks the given donor's attributes to see if they are null. Any null attributes will be given
   * default values and the given report will have its status changed to "repaired" as well as be
   * given a log of the particular issue.
   *
   * @param report the UserValidationReport relating to the donor being repaired
   * @param donor the DonorReceiver whose attributes are being validated
   */
  private void repairMissingDonorAttributesIfTheyDontExist(UserValidationReport report,
      DonorReceiver donor) {
    if (donor.getEmergencyContactDetails() == null) {
      donor.setEmergencyContactDetails(new ContactDetails(
          new Address(null, null, null, null, null, null, null),
          null, null, null));
      report.addIssue("Missing emergency contact details");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getMedications() == null) {
      donor.setMedications(new Medications());
      report.addIssue("Missing Medications");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getUserAttributeCollection() == null) {
      donor.setUserAttributeCollection(new UserAttributeCollection());
      donor.getUserAttributeCollection().setBloodPressure("0/0");
      donor.getUserAttributeCollection().setChronicDiseases("");
      report.addIssue("Missing UserAttributeCollection");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getDonorOrganInventory() == null) {
      donor.setDonorOrganInventory(new DonorOrganInventory());
      report.addIssue("Missing DonorOrganInventory");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getRequiredOrgans() == null) {
      donor.setRequiredOrgans(new ReceiverOrganInventory());
      report.addIssue("Missing RequiredOrganInventory");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getGender() == 0) {
      donor.setGender('U');
      report.addIssue("Missing Gender");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getBirthGender() == 0) {
      donor.setBirthGender('U');
      report.addIssue("Missing BirthGender");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getMasterIllnessList() == null) {
      donor.setMasterIllnessList(new ArrayList<>());
      report.addIssue("Missing MasterIllnessList");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (donor.getMedicalProcedures() == null) {
      donor.setMedicalProcedures(new ArrayList<>());
      report.addIssue("Missing MedicalProcedures");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (!donor.getReceiver() && donor.getReceiver()) {
      donor.setReceiver(
          false); // If the receiver boolean was never set, we assume the donor is not also a receiver
      report.addIssue("Missing receiver boolean");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
    if (!donor.isActive() && donor.isActive()) {
      donor.setActive(true); // If the active boolean was never set, we assume the donor is active
      report.addIssue("Missing active boolean");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }

  }


  /**
   * Checks the given user's contact details attribute to see it is null. If it is, it will be given
   * default value and the given report will have its status changed to "repaired" as well as be
   * given a log of the particular issue.
   *
   * @param report the UserValidationReport relating to the user being repaired
   * @param user the User whose contact details are being validated
   */
  private void repairMissingContactDetailsIfTheyDontExist(UserValidationReport report, User user) {
    if (user.getContactDetails() == null) {
      user.setContactDetails((new ContactDetails(
          new Address(null, null, null, null, null, null, null),
          null, null, null)));
      report.addIssue("Missing contact details");
      report.setAccountStatus(UserAccountStatus.REPAIRED);
    }
  }


  /**
   * Checks if the given user's first name, last name, and password are well formed. If they are
   * not, the given report is updated with the status 'Poor' as well as a log of the particular
   * issue.
   *
   * @param report the UserValidationReport relating to the User being checked
   * @param user the User whose attributes are being validated
   */
  private void validateUserAttributesAreCorrect(UserValidationReport report, User user) {
    if (!validateAlphanumericString(false, user.getFirstName(), 0, 50)) {
      report.addIssue("Invalid First Name");
      report.setAccountStatus(UserAccountStatus.POOR);
    }
    if (!validateAlphanumericString(false, user.getLastName(), 0, 50)) {
      report.addIssue("Invalid Last Name");
      report.setAccountStatus(UserAccountStatus.POOR);
    }
  }


  /**
   * Checks if the given donor's nhi number conforms to NHI requirements. If it does not, the given
   * report status is set to 'invalid' and is updated with a log of the issue.
   *
   * @param report The UserValidationReport relating to the User being checked
   * @param donor the donor whose nhi number is being checked
   */
  private void validateDonorNHINumberIsWellFormed(UserValidationReport report,
      DonorReceiver donor) {
    if (!checkNHIRegex(donor.getUserName())) {
      report.addIssue("Invalid NHI number");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
  }


  /**
   * Checks if the given Clinician staff ID is a valid int. If it does not, the given report status
   * is set to 'invalid' and is updated with a log of the issue.
   *
   * @param report The UserValidationReport relating to the Clinician being checked
   * @param clinician the clinician whose staff ID is being checked
   */
  private void validateStaffIDIsWellFormed(UserValidationReport report, Clinician clinician) {
    if (!validateStaffIDIsInt(clinician.getUserName())) {
      report.addIssue("Invalid Staff ID");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
  }


  /**
   * Checks if the given administrators username is well formed. If it does not, the given report
   * status is set to 'invalid' and is updated with a log of the issue.
   *
   * @param report The UserValidationReport relating to the administrator being checked
   * @param admin the Administrator whose username is being checked
   */
  private void validateAdminUsernameIsWellFormed(UserValidationReport report, Administrator admin) {
    if (!validateAdminUsername(admin.getUserName())) {
      report.addIssue("Invalid Username");
      report.setAccountStatus(UserAccountStatus.INVALID);
    }
  }


  /**
   * Checks if the given user's userName is not already being used by another user account in the
   * given user database. If it is, the given report's status is set to 'exists' and is updated with
   * a log of the issue.
   *
   * @param report The UserValidationReport relating to the User being checked
   * @param user the User whose username is being checked against the given user database
   * @param users A linkedHashMap of either DonorReceivers, Clinicians, or Administrator users.
   */
  private void validateUserNameIsNotUsed(UserValidationReport report, User user,
      LinkedHashMap<String, User> users) {
    if (users.get(user.getUserName()) != null) {
      report.addIssue("Duplicate account exists");
      report.setAccountStatus(UserAccountStatus.EXISTS);
    }
  }


  /**
   * Validates the attributes of a donor and returns a validation report based on the validation
   * process.
   *
   * @param donor A DonorReceiver to be tested
   * @param donors LinkedHashMap of the usernames and the User objects
   * @return A ValidationReport which documents whether the attributes of the DonorReceiver are
   * valid/invalid and why.
   */
  public UserValidationReport validateDonor(DonorReceiver donor,
      LinkedHashMap<String, User> donors) {

    // validation stage
    validateUserAttributesAreNotMissing(report, donor);
    validateDonorNHINumberIsWellFormed(report, donor);

    if (donor.getDateOfBirth() == null) {
      report.setAccountStatus(UserAccountStatus.INVALID);
      report.addIssue("Missing date of birth");
    }

    if (report.getAccountStatus() != UserAccountStatus.INVALID) {

      // repair stage
      repairMissingUserAttributesIfTheyDontExist(report, donor);
      repairMissingDonorAttributesIfTheyDontExist(report, donor);
      repairMissingContactDetailsIfTheyDontExist(report, donor);
      validateEmail(report, donor);

      // quality stage
      validateUserAttributesAreCorrect(report, donor);

      // duplication stage
      validateUserNameIsNotUsed(report, donor, donors);
    }
    return report;
  }


  /**
   * Validates the attributes of a clinician and returns a validation report based on the validation
   * process.
   *
   * @param clinician A Clinician to be tested
   * @param clinicians LinkedHashMap of the usernames and the User objects
   * @return A ValidationReport which documents whether the attributes of the Clinician are
   * valid/invalid and why.
   */
  public UserValidationReport validateClinician(Clinician clinician,
      LinkedHashMap<String, User> clinicians) {

    // validation stage
    validateUserAttributesAreNotMissing(report, clinician);
    validateStaffIDIsWellFormed(report, clinician);

    if (report.getAccountStatus() != UserAccountStatus.INVALID) {

      // repair stage
      repairMissingUserAttributesIfTheyDontExist(report, clinician);
      repairMissingContactDetailsIfTheyDontExist(report, clinician);

      // quality stage
      validateUserAttributesAreCorrect(report, clinician);

      // duplication stage
      validateUserNameIsNotUsed(report, clinician, clinicians);
    }
    return report;
  }


  /**
   * Validates the attributes of a administrator and returns a validation report based on the
   * validation process.
   *
   * @param admin A Administrator to be tested
   * @param admins LinkedHashMap of the usernames and the User objects
   * @return A ValidationReport which documents whether the attributes of the Administrator are
   * valid/invalid and why.
   */
  public UserValidationReport validateAdministrator(Administrator admin,
      LinkedHashMap<String, User> admins) {

    // validation stage
    validateUserAttributesAreNotMissing(report, admin);
    validateAdminUsernameIsWellFormed(report, admin);

    if (report.getAccountStatus() != UserAccountStatus.INVALID) {

      // repair stage
      repairMissingUserAttributesIfTheyDontExist(report, admin);

      // quality stage
      validateUserAttributesAreCorrect(report, admin);

      // duplication stage
      validateUserNameIsNotUsed(report, admin, admins);
    }
    return report;
  }

  /**
   * Checks if the given date is greater than or equal to given date of birth and that
   * the date is less than or equal to the current date.
   *
   * @param dateToBeChecked A localDate object of the date of diagnoses.
   * @param dateOfBirth A LocalDate object of the date of birth.
   * @return Returns 'true' if the date of diagnoses is greater than or equal to given date of birth
   * and that the date of diagnoses is less than or equal to the current date, returns 'false'
   * otherwise.
   */
  public static boolean validateDate(LocalDate dateToBeChecked, LocalDate dateOfBirth) {
    if (dateToBeChecked != null && dateOfBirth != null) {
      return !dateToBeChecked.isAfter(LocalDate.now()) && !dateToBeChecked.isBefore(dateOfBirth);
    }
    return false; // we cannot have null values.
  }


  /**
   * Checks if the given date is greater than or equal to given date of birth
   *
   * @param dateToBeChecked A localDate object of the date of diagnoses.
   * @param dateOfBirth A LocalDate object of the date of birth.
   * @return Returns 'true' if the date of diagnoses is greater than or equal to given date of birth
   * returns 'false' otherwise.
   */
  public static boolean validateProcedureDate(LocalDate dateToBeChecked, LocalDate dateOfBirth) {
    if (dateToBeChecked == null) {
      return true;
    } else {
      if (!dateToBeChecked.isBefore(dateOfBirth.minusYears(1))) {
        return true;
      } else {
        return false;
      }
    }
  }


  /**
   * Validates that the email address provided for the donor receiver is valid
   * @param report The report of the errors in the file to be imported
   * @param donor The donor being imported
   */
  private void validateEmail(UserValidationReport report, DonorReceiver donor) {
    if (!donor.getContactDetails().validateEmail(donor.getContactDetails().getEmail())) {
      report.addIssue("Invalid Email Address: " + donor.getContactDetails().getEmail());
      report.setAccountStatus(UserAccountStatus.REPAIRED);
      donor.getContactDetails().setEmail(null);
    }
  }
}
