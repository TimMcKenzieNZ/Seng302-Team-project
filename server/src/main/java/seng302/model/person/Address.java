package seng302.model.person;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng302.model.utility.StringExtension;


/**
 * Class for storing address details.
 */
public class Address {

  private static final String ERROR_INVALID = "ERROR: Invalid value ";
  private static final String VALID_CHARACTERS_MESSAGE = "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
  private static final String UPDATED = "updated";

  private String streetAddressLineOne;
  private String streetAddressLineTwo;
  private String suburb;
  private String city;
  private String region;
  private String postCode;
  private String countryCode;

  /**
   * Constructor for the Address class, creates a new instance of the Address class with the
   * necessary information with JSON.
   *
   * @param streetAddressLineOne The first line in the address (flat number, ect..)
   * @param streetAddressLineTwo The second line in the address (house number, ect..)
   * @param suburb The name of the suburb the address is in
   * @param city The name of the city the address is in
   * @param region The region the address is in
   * @param postCode The postcode of the address. If the postcode is invalid, then the postcode is
   * set to be an empty string.
   * @param countryCode The country code of the address
   */
  @JsonCreator
  public Address(@JsonProperty("streetAddressLineOne") String streetAddressLineOne,
      @JsonProperty("streetAddressLineTwo") String streetAddressLineTwo,
      @JsonProperty("suburbName") String suburb,
      @JsonProperty("cityName") String city,
      @JsonProperty("region") String region,
      @JsonProperty("postCode") String postCode,
      @JsonProperty("countryCode") String countryCode) {
    setStreetAddressLineOne(StringExtension.nullFilter(streetAddressLineOne));
    setStreetAddressLineTwo(StringExtension.nullFilter(streetAddressLineTwo));
    setSuburb(StringExtension.nullFilter(suburb));
    setCity(StringExtension.nullFilter(city));
    setRegion(StringExtension.nullFilter(region));
    updatePostCode(StringExtension.nullFilter(postCode));
    setCountryCode(StringExtension.nullFilter(countryCode));
  }

  @Override
  public String toString() {
    return "Street Address Line 1: " + this.streetAddressLineOne +
        "\nStreet Address Line 2: " + this.streetAddressLineTwo +
        "\nSuburb: " + this.suburb +
        "\nCity: " + this.city +
        "\nRegion: " + this.region +
        "\nPost Code: " + this.postCode +
        "\nCountry Code: " + this.countryCode;
  }

  public String getStreetAddressLineOne() {
    return streetAddressLineOne;
  }

  /**
   * Sets line 1 of the street address. If the line given is null, then the line will be stored as
   * an empty string instead.
   *
   * @param streetAddressLineOne The line 1 that the address will be set to have.
   */
  public void setStreetAddressLineOne(String streetAddressLineOne) {
    this.streetAddressLineOne = StringExtension.nullFilter(streetAddressLineOne);
  }


  public String getStreetAddressLineTwo() {
    return streetAddressLineTwo;
  }

  /**
   * Sets line 2 of the street address. If the line given is null, then the line will be stored as
   * an empty string instead.
   *
   * @param streetAddressLineTwo The line 2 that the address will be set to have.
   */
  public void setStreetAddressLineTwo(String streetAddressLineTwo) {

    this.streetAddressLineTwo = StringExtension.nullFilter(streetAddressLineTwo);

  }


  /**
   * Returns the name of the suburb as a String object.
   *
   * @return A String object representing the name of the suburb.
   */
  public String getSuburb() {

    return suburb;

  }


  /**
   * Sets the suburb name of the address. If the suburb name given is null, then the suburb name
   * will be stored as an empty string instead.
   *
   * @param suburb The suburb name that the address will be set to have.
   */
  public void setSuburb(String suburb) {

    this.suburb = StringExtension.nullFilter(suburb);

  }


  /**
   * Returns the city of the address as a String object.
   *
   * @return A String object containing the city as its value.
   */
  public String getCity() {

    return city;

  }


  /**
   * Sets the city name of the address. If the city name given is null, then the city name will be
   * stored as an empty string instead.
   *
   * @param city The city name that the address will be set to have.
   */
  public void setCity(String city) {
    this.city = StringExtension.nullFilter(city);
  }


  /**
   * Returns the region of the address as a String object.
   *
   * @return A String object containing the region of the address.
   */
  public String getRegion() {

    return region;

  }


  /**
   * Sets the region of the address. If the region given is null, the the region will be stored as an empty string instead.
   *
   * @param region The region that the address will be set to have.
   */
  public void setRegion(String region) {

    this.region = StringExtension.nullFilter(region);

  }


  /**
   * Returns the post code of the address as a String oject.
   *
   * @return A String object containing the post code of the address.
   */
  public String getPostCode() {

    return postCode;

  }


  /**
   * Sets the post code of the address. If the post code has no characters, then the post code will
   * be stored as an empty string instead. If the post code is invalid, then the post code will not
   * be changed. If the post code is valid (by the definition of {@link Address#postCodeIsValid})
   * then that is the post code the address will be set to have.
   *
   * @param postCode The post code that the address will be set to have.
   * @return String of status of update postcode
   */
  public String updatePostCode(String postCode) {
    String message =
            ERROR_INVALID + postCode + ". Post code must be a 4 digit integer.\n";
    if (postCode == null || postCode.trim().length() == 0) {
      message = "success";
      this.postCode = "";
    } else {
      //validate post code
      postCode = postCode.trim();
      if (postCodeIsValid(postCode)) {
        message = "success";
        this.postCode = postCode;
      }
    }
    return message;
  }

  public void setPostCode(String PostCode) {
    if (postCodeIsValid(PostCode)) {
      postCode = PostCode;
    }
  }

  /**
   * Validates a (non-empty) post code. The post code must be 4 characters long and contain only
   * numbers in order to be considered valid.
   *
   * @param postCode A string containing the post code to be tested (not an empty string).
   * @return Whether or not a post code is valid i.e. true if the post code is valid, false if not.
   */
  public static boolean postCodeIsValid(String postCode) {
    boolean valid = false;
    if (postCode != null && postCode.matches("[0-9]+") && postCode.length() == 4) {
      valid = true;
    }
    return valid;
  }


  public String getCountryCode() {
    return countryCode;
  }

  /**
   * Sets the country code of the address. If the country code given is null, the the country code will be stored as an empty string instead.
   *
   * @param countryCode The country code that the address will be set to have.
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = StringExtension.nullFilter(countryCode);
  }

  /**
   * Validates then updates the value addressStreet. returns an error message if update not possible
   *
   * @param value The value to update addressStreet with.
   * @return A message whether or not the update passed.
   */
  public String updateStreetAddressLineOne(String value) {
    String message = ERROR_INVALID + value + ". Street address line 1 must be between 1 and 100 alphanumeric " +
        VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 1, 100) || value.equals("")) {
      message = UPDATED;
      setStreetAddressLineOne(value);
    }
    return message;
  }

  /**
   * Validates then updates the value addressStreet. returns an error message if update not possible
   *
   * @param value The value to update addressStreet with.
   * @return A message whether or not the update passed.
   */
  public String updateStreetAddressLineTwo(String value) {
    String message = ERROR_INVALID + value + ". Street address line 2 must be between 0 and 100 alphanumeric " +
        VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 0, 100) || value.equals("")) {
      message = UPDATED;
      setStreetAddressLineTwo(value);
    }
    return message;
  }

  /**
   * Validates and updates the value addressCity.
   *
   * @param value The value to update addressCity
   * @return A message whether the update passed or not.
   */
  public String updateCityName(String value) {
    String message =
            ERROR_INVALID + value + ". City name must have at most 100 alphabetical " +
                    VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 0, 100) || value.equals("")) {
      message = UPDATED;
      setCity(value);
    }
    return message;
  }

  /**
   * Validates and updates the value addressRegion
   *
   * @param value The value to update addressRegion
   * @return A message whether or not the update passed
   */
  public String updateRegion(String value) {
    String message =
            ERROR_INVALID + value + ". Region must have at most 100 alphabetical " +
                    VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 0, 100) || value.equals("")) {
      message = UPDATED;
      setRegion(value);
    }
    return message;
  }

  public String updateSuburb(String value) {
    String message =
            ERROR_INVALID + value + ". Suburb must have at most 100 alphabetical " +
                    VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 0, 100) || value.equals("")) {
      message = UPDATED;
      setSuburb(value);
    }
    return message;
  }

  public String updateCountryCode(String value) {
    String message =
            ERROR_INVALID + value + ". Country Code must have at most 100 alphabetical " +
                    VALID_CHARACTERS_MESSAGE;
    if (validateAlphanumericString(true, value, 0, 100) || value.equals("")) {
      message = UPDATED;
      setCountryCode(value);
    }
    return message;
  }

  /**
   * Validates a string that is provided. Validation is whether or not the string is alphanumeric
   * and within the given length bounds
   *
   * @param isAlphanumeric A boolean variable htat tells whether the string is alphanumeric or not
   * @param string The string to be tested
   * @param minLength the minimum possible length for the string
   * @param maxLength The maximum possible length for the string
   * @return A boolean whether the string is valid for the given constraints
   */
  public static boolean validateAlphanumericString(boolean isAlphanumeric, String string, int minLength, int maxLength) {
    String numbers = "";
    if (minLength > maxLength) {
      return false;
    }
    if (isAlphanumeric) {
      numbers = "0-9";
    }
    String regularExpression = "^[a-zA-Z\\s-'," + numbers + "]{" + minLength + "," + maxLength + "}$";
    Pattern pattern = Pattern.compile(regularExpression);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }


}
