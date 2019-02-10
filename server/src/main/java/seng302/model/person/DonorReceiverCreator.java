package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to create the donor/receiver on the server
 * dateOfBirth has to be in the format YYYY-MM-DD for this to work
 */
public class DonorReceiverCreator {

  private String nhi;
  private String givenName;
  private String middleName;
  private String lastName;
  private String dateOfBirth;
  private String password;
  private String modifyingAccount;
  private String mobileNumber;

  @JsonCreator
  public DonorReceiverCreator(
          @JsonProperty("nhi") String nhi,
          @JsonProperty("givenName") String givenName,
          @JsonProperty("middleName") String middleName,
          @JsonProperty("lastName") String lastName,
          @JsonProperty("dateOfBirth") String dateOfBirth,
          @JsonProperty("password") String password,
          @JsonProperty("modifyingAccount") String modifyingAccount,
          @JsonProperty("mobileNumber") String mobileNumber) {
    this.nhi = nhi;
    this.givenName = givenName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.password = password;
    this.modifyingAccount = modifyingAccount;
    this.mobileNumber = mobileNumber;
  }

  public String getNhi() {
    return nhi;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public String getPassword() {
    return password;
  }

  public String getModifyingAccount() {
    return modifyingAccount;
  }

  public String getMobileNumber() { return mobileNumber; }

}
