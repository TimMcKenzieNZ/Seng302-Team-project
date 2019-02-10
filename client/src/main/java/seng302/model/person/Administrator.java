package seng302.model.person;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Administrator extends User {

  public static final String defaultAdmin = "Sudo";


  /**
   * The constructor for the Administrator class.
   *
   * @param userName The username of the administrator (a string)
   * @param firstName The first name of the administrator (a string)
   * @param middleName The middle name of the administrator (a string)
   * @param lastName The last name of the administrator (a string)
   * @param contactDetails The contact Details of the administrator (contains their work address)
   * @param password The password of the administrator. (a string)
   * @param creationDate The date the admin account was created. (a LocalDateTime object)
   * @param modifications An array of strings which log the modifications made to the admin's
   * attributes.
   */
  @JsonCreator
  public Administrator(
      //inherited from Person
      @JsonProperty("firstName") String firstName,
      @JsonProperty("middleName") String middleName,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("contactDetails") ContactDetails contactDetails,
      //inherited from User
      @JsonProperty("userName") String userName,
      @JsonProperty("password") String password,
      @JsonProperty("creationDate") LocalDateTime creationDate,
      @JsonProperty("modifications") ArrayList<LogEntry> modifications,
      @JsonProperty("version") Integer version){
    super(firstName, middleName, lastName, contactDetails, userName, password, creationDate,
        modifications, version);
  }

  /**
   * A minimal constructor for the administrator class.
   *
   * @param firstName The first name of the administrator (a string)
   * @param middleName The middle name of the administrator (a string)
   * @param lastName lastName The last name of the administrator (a string)
   * @param userName The username of the administrator (a string)
   * @param password The password of the administrator. (a string)
   */
  public Administrator(String firstName, String middleName, String lastName, String userName,
      String password) {
    super(firstName, middleName, lastName, new ContactDetails(new Address(null, null,
        null, null, null, null, null), null, null,
        null), userName, password, null, null, 1);
    if (firstName == null) {
      this.setFirstName("");
    }

    if (middleName == null) {
      this.setMiddleName("");
    }

    if (lastName == null) {
      this.setLastName("");
    }
  }
}
