package seng302.controllers;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.model.DonorOrganInventory;
import seng302.model.ReceiverOrganInventory;
import seng302.model.UserAttributeCollection;
import seng302.model.person.Address;
import seng302.model.person.ContactDetails;
import seng302.model.person.DonorReceiver;

public class DonorListControllerTest {

  private DonorListController controller;

  private UserAttributeCollection userAttCol;
  private DonorOrganInventory donOrgInv;
  private ReceiverOrganInventory reqOrgans;
  private ContactDetails contacts;

  private DonorReceiver donorReceiver1;
  private DonorReceiver donorReceiver2;

  private ObservableList<String> regions = FXCollections.observableArrayList();
  private ObservableList<String> genders = FXCollections.observableArrayList();
  private ObservableList<String> donorOrgans = FXCollections.observableArrayList();
  private ObservableList<String> receiverOrgans = FXCollections.observableArrayList();
  private ObservableList<String> status = FXCollections.observableArrayList();


  /**
   * Initialise variables before each test.
   */
  @Before
  public void setup() {

    LocalDate DOB = LocalDate.of(1990, 12, 31);

    userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", false, false, "10/20", 2.0, "None");

    donOrgInv = new DonorOrganInventory(true, false, true, true,
        false, false, true,
        false, true, false, true, true);

    reqOrgans = new ReceiverOrganInventory();

    Address contactAddress = new Address("1 Fleet Street", null, null, "Christchurch", "Canterbury",
        "8044", "64");
    contacts = new ContactDetails(contactAddress, "0220456543", "5452345",
        "randomPerson92@gmail.com");
    donorReceiver1 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");
    donorReceiver1.setUserAttributeCollection(userAttCol);
    donorReceiver1.setDonorOrganInventory(donOrgInv);
    donorReceiver1.setRequiredOrgans(reqOrgans);
    donorReceiver1.setContactDetails(contacts);
    donorReceiver1.updateOrgan(donorReceiver1, "receiver", "liver","true");
    donorReceiver1.setGender('M');
    donorReceiver1.setBirthGender('M');

    donorReceiver2 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");

    regions = FXCollections.observableArrayList();
    genders = FXCollections.observableArrayList();
    donorOrgans = FXCollections.observableArrayList();
    receiverOrgans = FXCollections.observableArrayList();
    status = FXCollections.observableArrayList();

    controller = new DonorListController();


  }


  /**
   * Set all variables to null.
   */
  @After
  public void tearDown() {

    donorReceiver1 = null;
    donorReceiver2 = null;
    controller = null;

    userAttCol = null;
    donOrgInv = null;
    reqOrgans = null;
    contacts = null;
    regions = null;
    genders = null;
    donorOrgans = null;
    receiverOrgans = null;
    status = null;

  }

}

