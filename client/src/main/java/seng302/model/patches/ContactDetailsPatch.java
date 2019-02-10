package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import seng302.model.StringExtension;
import seng302.model.person.Address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContactDetailsPatch {

    static final String invalidValueMessage = "ERROR: Invalid value ";
    static final String updatedMessage = "updated";
    private Address address;
    private String mobileNum;
    private String homeNum;
    private String email;
    private String version;


    /**
     * The constructor for the Contact Details object with JSON
     *
     * @param address The address of the user (all fields can be null bar region)
     * @param mobileNum The mobile number of the user (can be null)
     * @param homeNum The home number of the user (can be null)
     * @param email The email address of the user (can be null)
     */
    @JsonCreator
    public ContactDetailsPatch(@JsonProperty("address") Address address,
                          @JsonProperty("mobileNum") String mobileNum,
                          @JsonProperty("homeNum") String homeNum,
                          @JsonProperty("email") String email,
                          @JsonProperty("version") String version) {
        this.address = address;
        this.mobileNum = StringExtension.nullFilter(mobileNum);
        this.homeNum = StringExtension.nullFilter(homeNum);
        this.email = StringExtension.nullFilter(email);
        this.version = version;
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
        if (!Objects.equals(value, "")) {
            try {
                int number = Integer.parseInt(value.trim());
            } catch (Exception e) {
                return message;
            }
        }
        message = updatedMessage;
        setMobileNum(value);
        return message;
    }

    /**
     * Validates and updates the value homeNumber
     *
     * @param value The value to update homeNumber
     * @return A message whether or no the update passed.
     */
    public String updateHomeNum(String value) {
        int number;
        String message = invalidValueMessage + value + ". Home number must be numeric.\n";
        if (!value.equals("")) {
            try {
                number = Integer.parseInt(value.trim());
            } catch (Exception e) {
                return message;
            }
        }
        message = updatedMessage;
        setHomeNum(value);
        return message;
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
    public boolean validateEmail(String testEmail) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(testEmail);
        return matcher.matches();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
