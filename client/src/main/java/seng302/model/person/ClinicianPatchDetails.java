package seng302.model.person;

public class ClinicianPatchDetails {

  public String firstName;
  public String lastName;
  public String streetAddressLineOne;
  public String region;
  public int version;

  public ClinicianPatchDetails(String firstName, String lastName, String streetAddressLineOne, String region, int version) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.streetAddressLineOne = streetAddressLineOne;
    this.region = region;
    this.version = version;
  }
}
