package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A class to hold attribute information about a User account. Specifically whether the account is
 * valid or invalid and what issues there are relating to its attributes.
 */
public class UserValidationReport {

  /**
   * ENUM to describe the overall account status. One of 'valid', 'poor', 'repaired', 'exists', or
   * 'invalid'.
   */
  private UserAccountStatus status;


  /**
   * A list of issues regarding the user account such as an invalid attribute. An empty list implies
   * there are no issues with the account.
   */
  private List<String> issues;


  /**
   * the username of the user this report is for.
   */
  private String username;

  @JsonCreator
  public UserValidationReport(@JsonProperty("issues") List issues,
                              @JsonProperty("username") String username,
                              @JsonProperty("status") UserAccountStatus status) {
    this.issues = issues;
    this.username = username;
    this.status = status;
  }


  public UserValidationReport(String username) {
    issues = new ArrayList<String>();
    this.username = username;
    setAccountStatus(UserAccountStatus.VALID); // default status is valid
  }

  public List getIssues() {
    return Collections.unmodifiableList(issues);
  }

  public void setIssues(List issues) {
    this.issues = issues;
  }

  public UserAccountStatus getAccountStatus() {
    return status;
  }

  public void setAccountStatus(UserAccountStatus status) {
    this.status = status;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  /**
   * Adds the given string into the Issues List.
   *
   * @param issue A string of an issue with a User's attribute.
   */
  public void addIssue(String issue) {
    issues.add(issue);
  }

  /**
   * Overridden toString method to print all related information about the report
   * @return
   */
  public String toString() {
    String toReturn = "Username: " + this.username +
        "\nAccount Status: " + this.status +
        "\nIssues: ";
    for(String issue: issues) {
      toReturn += "\n" + issue;
    }
    return toReturn;
  }

}
