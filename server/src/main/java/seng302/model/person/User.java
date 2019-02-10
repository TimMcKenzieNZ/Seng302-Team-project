package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class User extends Person {

  private String userName;
  private String password;

  private LocalDateTime creationDate;
  private ArrayList<LogEntry> modifications;

  private boolean active = true;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  private int version;


  /**
   * The constructor method for a User (when creating a new User).
   *
   * @param givenName The given name of the User.
   * @param lastName The last name of the User.
   * @param username The staff ID of the User.
   * @param workAddress The work address of the User.
   * @param region The region of the User.
   * @param password The password of the User.
   * @param modifications modifications of the user
   * @param creationDate creation date of user
   */
  public User(String givenName,
      String lastName,
      String workAddress,
      String region,
      String username,
      String password,
      LocalDateTime creationDate,
      ArrayList<LogEntry> modifications) {

    super(givenName, null, lastName, new ContactDetails(
        new Address(workAddress, null, null, null, region, null, null)
        , null, null, null));
    this.userName = username;
    this.password = password;
    if (creationDate == null) {
      this.creationDate = LocalDateTime.now();

    } else {
      this.creationDate = creationDate;
    }
    if (modifications == null) {
      this.modifications = new ArrayList<>();
    } else {
      this.modifications = modifications;
    }
    version = 1;
  }

  /**
   * The constructor method for a User (when creating a new User).
   *
   * @param givenName The given name of the User.
   * @param lastName The last name of the User.
   * @param username The staff ID of the User.
   * @param workAddress The work address of the User.
   * @param region The region of the User.
   * @param password The password of the User.
   * @param modifications modifications of the user
   * @param creationDate creationdate of user
   * @param version version of the donor
   */
  public User(String givenName,
              String lastName,
              String workAddress,
              String region,
              String username,
              String password,
              LocalDateTime creationDate,
              ArrayList<LogEntry> modifications,
              Integer version) {

    super(givenName, null, lastName, new ContactDetails(
            new Address(workAddress, null, null, null, region, null, null)
            , null, null, null));
    this.userName = username;
    this.password = password;
    if (creationDate == null) {
      this.creationDate = LocalDateTime.now();

    } else {
      this.creationDate = creationDate;
    }
    if (modifications == null) {
      this.modifications = new ArrayList<>();
    } else {
      this.modifications = modifications;
    }
    this.version = version;
  }


  /**
   * Full constructor for the User class with JSON
   *  @param firstName A string of the Person's first (or given) name
   * @param middleName A string of the Person's middle (or other) name
   * @param lastName A string of the Person's last name
   * @param contactDetails A ContactDetails object containing the Person's address and contact
* numbers.
   * @param userName A string of the User's username. For a donor this is their NHI number. For a
* clinician it is their staff id. For an admin it is their username.
   * @param password A string of the User's password. It should be between 6 and 30 alphanumeric
* characters long
   * @param creationDate creationDate of person
   * @param modifications modifications of person
   * @param version
   */
  @JsonCreator
  public User(
          //inherited from Person
          @JsonProperty("firstName") String firstName,
          @JsonProperty("middleName") String middleName,
          @JsonProperty("lastName") String lastName,
          @JsonProperty("contactDetails") ContactDetails contactDetails,

          // User attributes
          @JsonProperty("userName") String userName,
          @JsonProperty("password") String password,
          @JsonProperty("creationDate") LocalDateTime creationDate,
          @JsonProperty("modifications") ArrayList<LogEntry> modifications,
          @JsonProperty("version") Integer version) {
    super(firstName, middleName, lastName, contactDetails);
    this.userName = userName;
    this.password = password;
    if (creationDate == null) {
      this.creationDate = LocalDateTime.now();
    } else {
      this.creationDate = creationDate;
    }
    if (modifications == null) {
      this.modifications = new ArrayList<>();
    } else {
      this.modifications = modifications;
    }
    this.version = version;
  }


  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ArrayList<LogEntry> getModifications() {
    return modifications;
  }

  public void setModifications(ArrayList<LogEntry> modifications) {
    this.modifications = modifications;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion( int version) {
    this.version = version;
  }

  /**
   * Overrides the equals method inherited from Object. Returns true if the given object is
   * considered equal to the current instance. Two users are consdiered equal if they share the same
   * username.
   *
   * @param object The object to be compared to the current instance.
   * @return True if object is equal to the current instance. Otherwise false.
   */
  @Override
  public boolean equals(Object object) {
    if (object instanceof User && ((User) object).getUserName().equals(userName)) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Overrides the equals method inherited from Object. Returns a hash value of the current
   * instance. This should be shared with any object which is considered equal to the current
   * instance.
   *
   * @return The hash value of the current instance.
   */
  @Override
  public int hashCode() {

    return userName.hashCode();

  }
}
