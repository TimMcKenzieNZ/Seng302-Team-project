package seng302.model.person;

import java.util.List;
import java.util.Objects;

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

  /**
   * Overrides the equals method for a user summary
   * @param comparingObject The object to be compared
   * @return A boolean value of whether the object is equal to the summary
   */
  @Override
  public boolean equals(Object comparingObject) {
    return comparingObject instanceof UserSummary &&
        Objects.equals(((UserSummary) comparingObject).getFirstName(), this.firstName) &&
        Objects.equals(((UserSummary) comparingObject).getMiddleName(), this.middleName) &&
        Objects.equals(((UserSummary) comparingObject).getLastName(), this.lastName) &&
        ((UserSummary) comparingObject).getUsername().equals(this.username) &&
        Objects.equals(((UserSummary) comparingObject).getRegion(), this.region);
  }


  /**
   * Checks if two User Summary Lists have the same values in them
   * @param userSummaryListOne The first User Summary List
   * @param userSummaryListTwo The second User Summary List
   * @return A boolean value stating if the two lists have the same contents
   */
  public static boolean userSummaryListEquals(List userSummaryListOne, List userSummaryListTwo) {
    if (userSummaryListOne.size() != userSummaryListTwo.size()) {
      return false;
    }
    for (int i = 0; i < userSummaryListOne.size(); i++) {
      if (!userSummaryListOne.get(i).equals(userSummaryListTwo.get(i))) {
        return false;
      }
    }
    return true;
  }
}
