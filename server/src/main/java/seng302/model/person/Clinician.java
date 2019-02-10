package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Clinician extends User {

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




}
