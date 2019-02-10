package seng302.model.person;

import seng302.model.DonorOrganInventory;
import seng302.model.ReceiverOrganInventory;

import java.time.LocalDate;

/**
 * This is a DonorReceiver class that only contains the attributes that are required to display DonorReceivers in table views.
 * It is a summary of a DonorReceiver and is used by the /donors (get all donors) endpoint.
 */
public class DonorReceiverSummary {
        private String givenName;
        private String middleName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String nhi;
        private String region;
        private DonorOrganInventory donatedOrgans;
        private char gender;
        private String city;
        private ReceiverOrganInventory receiverOrganInventory;

    public DonorReceiverSummary() {
    }

    public String getGivenName() {

        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public DonorOrganInventory getDonorOrganInventory() {
        return donatedOrgans;
    }

    public void setDonorOrganInventory(DonorOrganInventory donorOrganInventory) {
        this.donatedOrgans = donorOrganInventory;
    }

    public ReceiverOrganInventory getReceiverOrganInventory() {
        return receiverOrganInventory;
    }

    public void setReceiverOrganInventory(ReceiverOrganInventory receiverOrganInventory) {
        this.receiverOrganInventory = receiverOrganInventory;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
}
