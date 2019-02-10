package seng302.model.person;

/**
 * This is a DonorReceiver class that only contains the attributes that are required to display DonorReceivers in table views.
 * It is a summary of a DonorReceiver and is used by the /donors (get all donors) endpoint.
 */
public class UserSummary {
  private String username;
  private String firstName;
  private String middleName;
  private String lastName;
  private String region;

  public UserSummary() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }
}
