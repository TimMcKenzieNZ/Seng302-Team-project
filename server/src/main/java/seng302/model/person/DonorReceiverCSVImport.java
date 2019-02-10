package seng302.model.person;

import seng302.model.UserAttributeCollection;

import java.util.List;

public class DonorReceiverCSVImport {
    private String userName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private List<String> dateOfBirth;
    private List<String> dateOfDeath;
    private String birthGender;
    private String gender;
    private String title;
    private UserAttributeCollection userAttributeCollection;
    private ContactDetails contactDetails;

    public DonorReceiverCSVImport(String userName, String firstName, String middleName,
                                  String lastName, String password, List<String> dateOfBirth, List<String> dateOfDeath, String birthGender, String gender, String title, UserAttributeCollection userAttributeCollection, ContactDetails contactDetails) {
        this.userName = userName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.birthGender = birthGender;
        this.gender = gender;
        this.title = title;
        this.userAttributeCollection = userAttributeCollection;
        this.contactDetails = contactDetails;
    }

    public String getUsername() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getDateOfBirth() {
        return dateOfBirth;
    }

    public List<String> getDateOfDeath() {
        return dateOfDeath;
    }

    public String getBirthGender() {
        return birthGender;
    }

    public String getGender() {
        return gender;
    }

    public String getTitle() {
        return title;
    }

    public UserAttributeCollection getUserAttributeCollection() {
        return userAttributeCollection;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }
}
