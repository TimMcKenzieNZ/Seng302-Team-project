package seng302.model.person;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import seng302.model.StringExtension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContactDetails {

    static final String invalidValueMessage = "ERROR: Invalid value ";
    static final String updatedMessage = "updated";
    private Address address;
    private String mobileNum;
    private String homeNum;
    private String email;


    /**
     * The constructor for the Contact Details object with JSON
     *
     * @param address The address of the user (all fields can be null bar region)
     * @param mobileNum The mobile number of the user (can be null)
     * @param homeNum The home number of the user (can be null)
     * @param email The email address of the user (can be null)
     */
    @JsonCreator
    public ContactDetails(@JsonProperty("address") Address address,
                          @JsonProperty("mobileNum") String mobileNum,
                          @JsonProperty("homeNum") String homeNum,
                          @JsonProperty("email") String email) {
        this.address = address;
        this.mobileNum = StringExtension.nullFilter(mobileNum);
        this.homeNum = StringExtension.nullFilter(homeNum);
        this.email = StringExtension.nullFilter(email);
    }

    @Override
    public String toString() {
        return "Address:\n" + this.address + "\nMobile Number: " + this.mobileNum + "\nHome Number: "
                + this.homeNum + "\nEmail: " + this.email + "\n";
    }

    /**
     * Gets the address of the user.
     *
     * @return The address of the user.
     */
    public Address getAddress() {
        return address;
    }


    /**
     * Sets the address of the user.
     *
     * @param address The new address of the user.
     */
    public void setAddress(Address address) {
        this.address = address;
    }


    /**
     * Gets the mobile number of the user.
     *
     * @return The mobile number of the user.
     */
    public String getMobileNum() {
        return mobileNum;
    }


    /**
     * Sets the mobile number of the user.
     *
     * @param mobileNum The new mobile number of the user.
     */
    private void setMobileNum(String mobileNum) {
        this.mobileNum = StringExtension.nullFilter(mobileNum);
    }


    /**
     * Gets the home number of the user.
     *
     * @return The home number of the user.
     */
    public String getHomeNum() {
        return homeNum;
    }


    /**
     * Sets the home number of the user.
     *
     * @param homeNum The new home number of the user.
     */
    private void setHomeNum(String homeNum) {
        this.homeNum = StringExtension.nullFilter(homeNum);
    }


    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }


    /**
     * Sets the email address of the user.
     *
     * @param email The new email address of the user.
     */
    private void setEmail(String email) {
        this.email = StringExtension.nullFilter(email);
    }

    /**
     * Validates and updates the value mobileNumber
     *
     * @param value The String to update mobile number
     * @return A message whether or not to the update passed.
     */
    public String updateMobileNum(String value) {
        String message = invalidValueMessage + value + ". Mobile number must be numeric.\n";
        if (!UserValidator.validatePhoneNumber(value)) {
            return message;
        } else {
            message = updatedMessage;
            setMobileNum(value);
            return message;
        }
    }

    /**
     * Validates and updates the value homeNumber
     *
     * @param value The value to update homeNumber
     * @return A message whether or no the update passed.
     */
    public String updateHomeNum(String value) {

        String message = invalidValueMessage + value + ". Home number must be numeric.\n";
        if (!UserValidator.validatePhoneNumber(value)) {
            return message;
        } else {
            message = updatedMessage;
            setHomeNum(value);
            return message;
        }
    }

    /**
     * Validates and updates the value email
     *
     * @param value The value ot update email
     * @return A message whether or not the update passed.
     */
    public String updateEmail(String value) {
        String message = invalidValueMessage + value + ". Please enter a proper email address.\n";
        if (validateEmail(value) || value.equals("")) {
            message = updatedMessage;
            setEmail(value);
        }
        return message;
    }

    /**
     * A function that validates whether the user has entered string that is of the correct format.
     *
     * @param testEmail The string to be tested
     * @return A boolean whether or not the email is valid
     */
    public static boolean validateEmail(String testEmail) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(testEmail);
        return matcher.matches();
    }


    /**
     * Parses the given contact attribute string and calls the corresponding update function for that
     * attribute.
     *
     * @param attribute A string of a contact details attribute.
     * @param value A string of the new value for the contact details attribute.
     * @return message of update contact
     */
    public String[] updateContact(String attribute, String value) {
        String message = "ERROR: Unknown contact field " + attribute
                + ". Valid fields are 'mobilenum', 'homenum', 'email', 'addressline1', 'addressline2', 'suburb', 'city', 'region' and 'postcode'.";
        String oldVal = "";
        if (value.equalsIgnoreCase("delete")) {
            value = "";
        }
        switch (attribute.toLowerCase()) {
            case "mobilenum": {
                oldVal = getMobileNum();
                message = updateMobileNum(value);
                break;
            }
            case "homenum": {
                oldVal = getHomeNum();
                message = updateHomeNum(value);
                break;
            }
            case "email": {
                oldVal = getEmail();
                message = updateEmail(value);
                break;
            }
            case "addressline1": {
                oldVal = getAddress().getStreetAddressLineOne();
                message = getAddress().updateStreetAddressLineOne(value);
                break;
            }
            case "addressline2": {
                oldVal = getAddress().getStreetAddressLineTwo();
                message = getAddress().updateStreetAddressLineTwo(value);
                break;
            }
            case "suburb": {
                oldVal = getAddress().getSuburbName();
                message = getAddress().updateSuburb(value);
                break;
            }
            case "city": {
                oldVal = getAddress().getCityName();
                message = getAddress().updateCityName(value);
                break;
            }
            case "region": {
                oldVal = getAddress().getRegion();
                message = getAddress().updateRegion(value);
                break;
            }
            case "postcode": {
                oldVal = getAddress().getPostCode();
                message = getAddress().updatePostCode(value);
                break;
            }
          default: {
                message = "Invalid command";
                break;
          }
        }
        String substring = message.substring(0, 5);
        if (!substring.equals("ERROR")) {
            return new String[]{message, oldVal};
        } else {
            return new String[]{message, null};
        }
    }

}
