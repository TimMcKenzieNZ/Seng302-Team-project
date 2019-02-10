package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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

    @JsonDeserialize(using=LocalDateDeserializer.class)
    private LocalDate dateOfBirth;
    private String nhi;
    private String region;
    private String gender;
    private String city;
    private ReceiverOrganInventory receiverOrganInventory;
    private DonorOrganInventory donorOrganInventory;

    public DonorReceiverSummary() {}

    @JsonCreator
    public DonorReceiverSummary(@JsonProperty("givenName") String givenName,
                                @JsonProperty("middleName") String middleName,
                                @JsonProperty("lastName") String lastName,
                                @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                                @JsonProperty("nhi") String nhi,
                                @JsonProperty("region") String region,
                                @JsonProperty("gender") String gender,
                                @JsonProperty("city") String city,
                                @JsonProperty("receiverOrganInventory") ReceiverOrganInventory receiverOrganInventory,
                                @JsonProperty("donorOrganInventory") DonorOrganInventory donorOrganInventory) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhi = nhi;
        this.region = region;
        this.gender = gender;
        this.city = city;
        this.receiverOrganInventory = receiverOrganInventory;
        this.donorOrganInventory = donorOrganInventory;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ReceiverOrganInventory getReceiverOrganInventory() {
        return receiverOrganInventory;
    }

    public void setReceiverOrganInventory(ReceiverOrganInventory receiverOrganInventory) {
        this.receiverOrganInventory = receiverOrganInventory;
    }

    public DonorOrganInventory getDonorOrganInventory() {
        return donorOrganInventory;
    }

    public void setDonorOrganInventory(DonorOrganInventory donorOrganInventory) {
        this.donorOrganInventory = donorOrganInventory;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
