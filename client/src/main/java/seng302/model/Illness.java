package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng302.model.person.DonorReceiver;

/**
 * Class for containing all data on instances of diseases or illnesses.
 */
public class Illness {

  /**
   * A String of the name of the disease or illness.
   */
  private String name;

  /**
   * A LocalDate object representing the date of the diagnoses of the disease/illness.
   */
  private LocalDate date;

  /**
   * A boolean flag which signifies whether the disease/illness has been cured. 'true' means the
   * disease/illness is cured.
   */
  private boolean cured;

  /**
   * A boolean flag which signifies whether the disease/illness is chronic or not. 'true' means the
   * disease/illness is chronic
   */
  private boolean chronic;

  /**
   * A complete constructor for an Illness object.
   * @param name A string of the name of the disease/illness.
   * @param date A LocalDate object of the date of the diagnoses.
   * @param cured Boolean flag, 'true' means the disease/illness is cured.
   * @param chronic Boolean flag, 'true' means the disease/illness is chronic.
   */
    /*
    public Illness(String name, LocalDate date, boolean cured, boolean chronic) {
        this.name = name;
        this.date = date;
        this.cured = cured;
        this.chronic = chronic;
    }
    */


  /**
   * A complete constructor for an Illness object using JSON.
   *
   * @param name A string of the name of the disease/illness.
   * @param date A LocalDate object of the date of the diagnoses.
   * @param cured Boolean flag, 'true' means the disease/illness is cured.
   * @param chronic Boolean flag, 'true' means the disease/illness is chronic.
   */
  @JsonCreator
  public Illness(@JsonProperty("name") String name,
      @JsonProperty("date") LocalDate date,
      @JsonProperty("cured") boolean cured,
      @JsonProperty("chronic") boolean chronic) {
    this.name = name;
    this.date = date;
    this.cured = cured;
    this.chronic = chronic;
  }


  /**
   * A constructor for an Illness object with the date of diagnoses automatically generated.
   *
   * @param name A string of the name of the disease/illness.
   * @param cured Boolean flag, 'true' means the disease/illness is cured.
   * @param chronic Boolean flag, 'true' means the disease/illness is chronic.
   */
  public Illness(String name, boolean cured, boolean chronic) {
    this.name = name;
    this.cured = cured;
    this.chronic = chronic;
    this.date = LocalDate.now();
  }

  // The following methods are the getters and setters for the illness object.

  public void setChronic(boolean chronic) {
    this.chronic = chronic;
  }

  public void setCured(boolean cured) {
    this.cured = cured;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isChronic() {
    return chronic;
  }

  public boolean isCured() {
    return cured;
  }

  public LocalDate getDate() {
    return date;
  }

  public String getName() {
    return name;
  }


  /**
   * Converts the given LocalDate object into a string formatted to "yyyy-MM-dd".
   *
   * @param date a LocalDate object.
   * @return A String date formatted to "yyyy-MM-dd".
   */
  public String formatDate(LocalDate date) {
    return DonorReceiver.formatDateToString(date);
  }


  /**
   * Checks if the given date of diagnoses is greater than or equal to given date of birth and that
   * the date of diagnoses is less than or equal to the current date.
   *
   * @param dateOfDiagnoses A localDate object of the date of diagnoses.
   * @param dateOfBirth A LocalDate object of the date of birth.
   * @return Returns 'true' if the date of diagnoses is greater than or equal to given date of birth
   * and that the date of diagnoses is less than or equal to the current date, returns 'false'
   * otherwise.
   */
  public boolean validateDate(LocalDate dateOfDiagnoses, LocalDate dateOfBirth) {
    if (dateOfDiagnoses != null && dateOfBirth != null) {
      return !dateOfDiagnoses.isAfter(LocalDate.now()) && !dateOfDiagnoses.isBefore(dateOfBirth);
    }
    return false; // we cannot have null values.
  }


  /**
   * Checks if the given chronic boolean flag does not equal the given cured boolean flag.
   *
   * @param chron A boolean flag signifying a chronic disease/illness.
   * @param cure A boolean flag signifying whether a disease/illness is cured.
   * @return Returns true if the given chron boolean flag does not equal the given cure boolean flag
   * or if either of the given values is null.
   */
  public boolean compareChronicWithCured(boolean chron, boolean cure) {
    if (new Boolean(chron) != null && new Boolean(cure) != null) {
      return !(chron && cure);
    } else {
      return true; //one or both of the booleans have not been initialized yet.
    }
  }


  /**
   * Checks if the given name string is alphabetical and is between 1 and 50 characters (inclusive)
   * long.
   *
   * @param name A string of a name of an Illness.
   * @return Returns 'true' if the name is valid, 'false' otherwise.
   */
  public static boolean validateName(String name) {
    try {
      String regularExpression = "^[a-zA-Z\\s]{1,50}$";
      Pattern pattern = Pattern.compile(regularExpression);
      Matcher matcher = pattern.matcher(name);
      return matcher.matches();
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Returns a string representation of the Illness object with each attribute (if the attribute is
   * not null);
   *
   * @return A String of the Illness object.
   */
  public String toString() {
    String string = "";
    if (name != null && !name.equals("")) {
      string += name + " ";
    }
    if (new Boolean(chronic) != null && chronic == true) {
      string += "chronic" + " ";
    }
    if (date != null) {
      string += formatDate(date) + " ";
    }
    if (new Boolean(cured) != null && cured == true) {
      string += "cured";
    }
    return string += ".";
  }


  /**
   * Creates a string log of an update made to one of the Illness attributes. This log includes what
   * attribute changed, its old and new values, and the time the changes occurred.
   *
   * @param attribute A string of an Illness attribute. Should be one of 'name', 'date', 'chronic',
   * or 'cured'.
   * @param oldValue A string of the old value for the attribute.
   * @param newValue A string of the new value for the attribute.
   * @return Returns a log string.
   */
  public String createIllnessLog(String attribute, String oldValue, String newValue) {
    String time = DonorReceiver.formatDateTimeToString(LocalDateTime.now());
    String log = String
        .format("\tDiagnosis %s modified: %s changed from '%s' to '%s'. Change made at %s.",
            getName(), attribute, oldValue, newValue, time);
    return log;
  }


  /**
   * Attempts to update the Illness name attribute and returns a string of the result which will be
   * either a log of the change or an error message.
   *
   * @param value A string of the new value for the name attribute.
   * @return A string of the update log or an update error message.
   */
  public String[] updateName(String value) {
    String message = String
        .format("ERROR: Invalid value %s. Name must be between 1 and 50 alphabetical characters.\n",
            value);
    String oldVal = "";
    if (validateName(value)) {
      if (getName() != null) {
        message = createIllnessLog("name", getName(), value);
        oldVal = getName();
      } else {
        message = createIllnessLog("name", "unspecified", value.toUpperCase());
        oldVal = "unspecified";
      }
      setName(value.toUpperCase());
    }
    return new String[]{message, oldVal, value.toUpperCase()};
  }


  /**
   * Attempts to update the Illness date attribute and returns a string of the result which will be
   * either a log of the change or an error message.
   *
   * @param value A string of the new value for the date attribute.
   * @param dateOfBirth A LocalDate object representing the date of birth of the donor.
   * @return A string of the update log or an update error message.
   */
  public String[] updateDate(String value, LocalDate dateOfBirth) {
    LocalDate date;
    String message = String
        .format("ERROR: Invalid value %s. Date must be a valid date that is not after the " +
            "current date nor before the donor's birth date of %s. The date also has to be in the following format: "
            +
            "'dd/MM/YYYY'.\n", value, formatDate(dateOfBirth));
    String oldVal = "";
    value = value.replace("-", "");
    try {
      date = LocalDate
          .of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)),
              Integer.parseInt(value.substring(6, 8)));
    } catch (Exception e) {
      return new String[]{message, null, null};
    }
    date = LocalDate
        .of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)),
            Integer.parseInt(value.substring(6, 8)));
    if (validateDate(date, dateOfBirth)) {
      value = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6, 8);
      if (date != null) {
        message = createIllnessLog("date", formatDate(getDate()), value);
        oldVal = formatDate(getDate());
      } else {
        message = createIllnessLog("date", "unspecified", value);
        oldVal = "unspecified";
      }
      setDate(date);
    }
    return new String[]{message, oldVal, value};
  }


  /**
   * Attempts to update the Illness chronic attribute and returns a string of the result which will
   * be either a log of the change or an error message.
   *
   * @param value A string of the new value for the chronic attribute (either "true" or some other
   * string which is parsed to "false").
   * @return A string of the update log or an update error message.
   */
  public String[] updateChronic(String value) {
    boolean choice;
    String oldVal = "";
    try {
      choice = Boolean.parseBoolean(value);
    } catch (Exception e) {
      return new String[]{
          ("ERROR: Unknown value " + value + ". Value should be 'true' or 'false'."), null, null};
    }
    choice = Boolean.parseBoolean(value);
    String message = "ERROR: An Illness cannot be both chronic and cured/resolved. First uncheck 'cured/resolved'.\n";
    if (compareChronicWithCured(choice, isCured())) {
      if (Boolean.valueOf(chronic) != null) {
        message = createIllnessLog("chronic", Boolean.toString(chronic), Boolean.toString(choice));
        oldVal = Boolean.toString(chronic);
      } else {
        message = createIllnessLog("chronic", "unspecified", Boolean.toString(choice));
        oldVal = "unspecified";
      }
      setChronic(choice);
    }
    return new String[]{message, oldVal, Boolean.toString(choice)};
  }


  /**
   * Attempts to update the Illness cured attribute and returns a string of the result which will be
   * either a log of the change or an error message.
   *
   * @param value A string of the new value for the cured attribute (either "true" or some other
   * string which is parsed to "false").
   * @return A string of the update log or an update error message.
   */
  public String[] updateCured(String value) {
    boolean choice;
    String oldVal = "";
    try {
      choice = Boolean.parseBoolean(value);
    } catch (Exception e) {
      return new String[]{
          ("ERROR: Unknown value " + value + ". Value should be 'true' or 'false'\n"), null, null};
    }
    choice = Boolean.parseBoolean(value);
    String message = "ERROR: An Illness cannot be both chronic and cured/resolved. First uncheck 'chronic'.\n";
    if (compareChronicWithCured(isChronic(), choice)) {
      if (Boolean.valueOf(cured) != null) {
        message = createIllnessLog("cured", Boolean.toString(cured), value);
        oldVal = Boolean.toString(cured);
      } else {
        message = createIllnessLog("cured", "unspecified", value);
        oldVal = "unspecifed";
      }
      setCured(choice);
    }
    return new String[]{message, oldVal, value};
  }


  /**
   * Parses the given attribute string to identify which Illness attribute to update, and then calls
   * the corresponding update method for the identified attribute. If the given attribute is
   * invalid, or the given value is invalid then an error message is returned. Otherwise, a string
   * of the update log is returned instead.
   *
   * @param attribute A string of an Illness attribute. Should be one of 'name', 'date', 'chronic',
   * or 'cured'.
   * @param value A string of the new value for the given attribute.
   * @param dateOfBirth A LocalDate object representing the date of birth of the donor.
   * @return Returns a string, either an error message documenting the type of error that has
   * occurred or an update log.
   */
  public String[] updateAttribute(String attribute, String value, LocalDate dateOfBirth) {
    String[] result;
    switch (attribute) {
      case "name": {
        result = updateName(value);
        break;
      }
      case "date": {
        result = updateDate(value, dateOfBirth);
        break;
      }
      case "chronic": {
        result = updateChronic(value);
        break;
      }
      case "cured": {
        result = updateCured(value);
        break;
      }
      default: {
        result = new String[]{String.format("ERROR: unknown attribute %s. " +
            "Attribute should be one of 'name', 'date', 'chronic', or 'cured'.", attribute), null,
            null};
        break;
      }
    }
    return result;
  }
}

