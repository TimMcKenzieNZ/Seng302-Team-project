package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

  private String firstName;
  private String middleName;
  private String lastName;
  private ContactDetails contactDetails;

  /**
   * Full constructor for Person class with JSON
   *
   * @param firstName A string of the Person's first (or given) name
   * @param middleName A string of the Person's middle (or other) name
   * @param lastName A string of the Person's last name
   * @param contactDetails A ContactDetails object containing the Person's address and contact
   * numbers.
   */
  @JsonCreator
  public Person(@JsonProperty("firstName") String firstName,
      @JsonProperty("middleName") String middleName,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("contactDetails") ContactDetails contactDetails) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    if (contactDetails == null) {
      this.contactDetails = new ContactDetails(
          new Address(null, null, null, null, null, null, null)
          , null, null, null);
    } else {
      this.contactDetails = contactDetails;
    }
  }

  public ContactDetails getContactDetails() {
    return contactDetails;
  }

  public void setContactDetails(ContactDetails contactDetails) {
    this.contactDetails = contactDetails;
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

  @JsonIgnore
  public String getAddressCity() {
    return contactDetails.getAddress().getCityName();
  }

  @JsonIgnore
  public String getAddressRegion() {
    return contactDetails.getAddress().getRegion();
  }

  @JsonIgnore
  public String getAddressCountryName() {
    return contactDetails.getAddress().retrieveCountryName();
  }

  @JsonIgnore
  public String getAddressCountryCode() { return contactDetails.getAddress().getCountryCode();
  }


  /**
   * Retrieves and creates the full name of the donorReceiver holder
   *
   * @return A string with all parts of the accounts names
   */
  public String fullName() {
    if (middleName == null || middleName.equals("")) {
      if (lastName == null || lastName.equals("")) {
        return getFirstName();
      } else {
        return getFirstName().trim() + " " + getLastName().trim();
      }
    } else {
      return getFirstName().trim() + " " + getMiddleName().trim() + " " + getLastName().trim();
    }
  }
}
