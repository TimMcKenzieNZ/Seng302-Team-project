package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Medical Procedure
 */
public class MedicalProcedure {

  private String summary;
  private String description;
  private LocalDate date;
  private ArrayList<String> affectedOrgans;
  private static ArrayList<String> organNames = new ArrayList<>(
      Arrays.asList("Liver", "Kidney", "Pancreas", "Heart",
          "Lung", "Intestines", "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow",
          "Connective Tissue"));
  public static String invalidDateErrorMessage = "Invalid date";
  public static String nullDOBErrorMessage = "DOB cannot be null";
  public static String procedureDateTooEarlyErrorMessage = "Date of procedure more than 12 months before DOB";
  public static String invalidOrgansErrorMessage = "Invalid organ names";


  /**
   * The constructor for a medical procedure object using JSON.
   *
   * @param summary A string containing a short summary of the procedure.
   * @param description A string containing a longer description of the procedure.
   * @param date The LocalDate on which the procedure was/will be performed.
   * @param affectedOrgans An ArrayList containing the names of the organs that are affected by the
   * procedure.
   */
  @JsonCreator
  public MedicalProcedure(@JsonProperty("summary") String summary,
      @JsonProperty("description") String description,
      @JsonProperty("date") LocalDate date,
      @JsonProperty("affectedOrgans") ArrayList<String> affectedOrgans) {
    this.summary = summary;
    this.description = description;
    this.date = date;
    this.affectedOrgans = affectedOrgans;
  }

  /**
   * The constructor for a medical procedure object.
   *
   * @param summary A string containing a short summary of the procedure.
   * @param description A string containing a longer description of the procedure.
   * @param dateString The string value of the date on which the procedure is/was performed. Must be
   * in format "dd/mm/yyyy". Can be null if there is no date confirmed for the procedure.
   * @param dob The person's date of birth. This is the person that the procedure will be/was
   * performed on.
   * @param affectedOrgans An ArrayList containing the names of organs affected by this procedure.
   * The valid organ names are: "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines",
   * "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
   * @throws Exception Throws exception  with message {@link #invalidDateErrorMessage} if the date
   * provided is not a valid date. Throws exception with message {@link #nullDOBErrorMessage} if the
   * date of birth provided is null. Throws exception with message {@link
   * #procedureDateTooEarlyErrorMessage} if the procedure is more than 12 months before the date of
   * birth. Throws exception with message {@link #invalidOrgansErrorMessage} if the organ names in
   * affectedOrgans are not valid (i.e. not in the list of valid organ names {@link
   * #getOrganNames()}).
   */
  public MedicalProcedure(String summary, String description, String dateString, LocalDate dob,
      ArrayList<String> affectedOrgans) throws Exception {
    this.summary = summary;
    this.description = description;
    setDate(dateString, dob);
    if (!validAffectedOrgans(affectedOrgans)) {
      throw new Exception(invalidOrgansErrorMessage);
    } else {
      this.affectedOrgans = new ArrayList<>(affectedOrgans);
    }
  }

  /**
   * The getter method for the summary of the procedure.
   *
   * @return The summary of the procedure.
   */
  public String getSummary() {
    return summary;
  }

  /**
   * The setter method for the summary of the procedure.
   *
   * @param summary The summary that the procedure will be set to have.
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * The getter method for the description of the procedure.
   *
   * @return The description of the procedure.
   */
  public String getDescription() {
    return description;
  }

  /**
   * The setter method for the description of the procedure.
   *
   * @param description The description that the procedure will be set to have.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * The getter method for the date of the procedure.
   *
   * @return The date of the procedure.
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Sets the date of the procedure. The new date must be no more than 12 months before the person's
   * date of birth.
   *
   * @param dateString The string value of the date. Must be in format "dd/mm/yyyy".
   * @param dob The person's date of birth. This is the person that the procedure is/was performed
   * on. Can be null if there is no date confirmed for the procedure.
   * @throws Exception Throws exception  with message {@link #invalidDateErrorMessage} if the date
   * provided is not a valid date. Throws exception with message {@link #nullDOBErrorMessage} if the
   * date of birth provided is null. Throws exception with message {@link
   * #procedureDateTooEarlyErrorMessage} if the procedure is more than 12 months before the date of
   * birth.
   */
  public void setDate(String dateString, LocalDate dob) throws Exception {
    if (dateString == null) {
      this.date = null;
    } else {
      LocalDate date;
      dateString = dateString.replace("/", "");
      try {
        date = LocalDate.of(Integer.parseInt(dateString.substring(4, 8)),
            Integer.parseInt(dateString.substring(2, 4)),
            Integer.parseInt(dateString.substring(0, 2)));
      } catch (NumberFormatException | DateTimeException | StringIndexOutOfBoundsException ex) {
        throw new Exception(invalidDateErrorMessage);
      }
      if (dob == null) {
        throw new Exception(nullDOBErrorMessage);
      }
      // Make sure that the date of the procedure is no more than 12 months before the date of birth
      if (!date.isBefore(dob.minusYears(1))) {
        this.date = date;
      } else {
        throw new Exception(procedureDateTooEarlyErrorMessage);
      }
    }
  }

  /**
   * The getter method for the list of affected organs of this procedure. The valid organ names are:
   * "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines", "Cornea", "Middle Ear", "Skin",
   * "Bone", "Bone Marrow", and "Connective Tissue". See {@link #getOrganNames()}.
   *
   * @return The affected organs of this procedure.
   */
  public ArrayList<String> getAffectedOrgans() {
    return new ArrayList<>(affectedOrgans);
  }

  /**
   * The setter method for the list of affected organs of this procedure. The valid organ names are:
   * "Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines", "Cornea", "Middle Ear", "Skin",
   * "Bone", "Bone Marrow", and "Connective Tissue". See {@link #getOrganNames()}.
   *
   * @param affectedOrgans The list of affected organs of this procedure.
   * @throws Exception Throws exception with message {@link #invalidOrgansErrorMessage} if the organ
   * names in affectedOrgans are not valid (i.e. not in the list of valid organ names {@link
   * #getOrganNames()}).
   */
  public void setAffectedOrgans(ArrayList<String> affectedOrgans) throws Exception {
    if (!MedicalProcedure.validAffectedOrgans(affectedOrgans)) {
      throw new Exception(invalidOrgansErrorMessage);
    } else {
      this.affectedOrgans = new ArrayList<>(affectedOrgans);
    }
  }

  /**
   * The getter method for the list of valid organ names.
   *
   * @return The list of valid organ names.
   */
  public static ArrayList<String> getOrganNames() {
    return new ArrayList<>(organNames);
  }

  /**
   * Tests whether all the organ names given by affectedOrgans are in the list of valid organ names.
   * See {@link #getOrganNames()} for a list of valid organ names.
   *
   * @param affectedOrgans The organ names to be tested.
   * @return Returns true if the organ names are valid, false if they are not.
   */
  private static boolean validAffectedOrgans(ArrayList<String> affectedOrgans) {
    int i;
    boolean valid = true;
    for (i = 0; i < affectedOrgans.size(); i++) {
      if (!organNames.contains(affectedOrgans.get(i))) {
        valid = false;
      }
    }
    return valid;
  }

}
