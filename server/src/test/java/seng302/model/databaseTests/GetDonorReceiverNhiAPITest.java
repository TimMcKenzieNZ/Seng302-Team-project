package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.*;
import seng302.model.person.ContactDetails;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.security.AuthenticationTokenStore;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GetDonorReceiverNhiAPITest extends APITest {

    private static DonorReceiverController donorReceiverController;
    private ArrayList<Illness> masterIllnessList;
    private ReceiverOrganInventory receiverOrganInventory;
    private DonorOrganInventory donorOrganInventory;
    private ArrayList<MedicalProcedure> medicalProcedures;
    private ContactDetails contactDetails;
    private ContactDetails emergencyContactDetails;
    private ArrayList<LogEntry> modifications;
    private UserAttributeCollection userAttributeCollection;
    private Medications medications;


    private void getAllAttributes(DonorReceiver donorReceiver) {
        masterIllnessList = donorReceiver.getMasterIllnessList();
        receiverOrganInventory = donorReceiver.getRequiredOrgans();
        donorOrganInventory = donorReceiver.getDonorOrganInventory();
        medicalProcedures = donorReceiver.getMedicalProcedures();
        contactDetails = donorReceiver.getContactDetails();
        emergencyContactDetails = donorReceiver.getEmergencyContactDetails();
        modifications = donorReceiver.getModifications();
        userAttributeCollection = donorReceiver.getUserAttributeCollection();
        medications = donorReceiver.getMedications();
    }

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    }

    /**
     * Gets a donor that is present in the database
     */
    @Ignore //Currently the test database does not seem to be responding?
    @Test
    public void getPresentDonor() {
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.getDonor("AAA9999");
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            DonorReceiver donorReceiver;
            try {
                donorReceiver = (DonorReceiver) responseEntity.getBody();
                assertEquals("AAA9999", donorReceiver.getUserName());
                getAllAttributes(donorReceiver);
                Illness illness = masterIllnessList.get(0);
                assertEquals("Common Cold" ,illness.getName());
                assertEquals(true, donorOrganInventory.getBone());
                assertEquals(false, donorOrganInventory.getLiver());
                assertEquals(true, receiverOrganInventory.getHeart());
                assertEquals(true, donorReceiver.getReceiver());
                MedicalProcedure medicalProcedure = medicalProcedures.get(0);
                assertEquals("De-gloving surgery", medicalProcedure.getSummary());
                assertEquals("0800838383", contactDetails.getMobileNum());
                assertEquals("0800304050", emergencyContactDetails.getMobileNum());
                LogEntry logEntry = modifications.get(0);
                assertEquals("TEST DONOR", logEntry.getChangedVal());
                assertEquals("1.98", userAttributeCollection.getHeight().toString());
                assertEquals(1, medications.getCurrentMedications().size());
                assertEquals(1, medications.getPreviousMedications().size());
                assertEquals(1, medications.getMedicationLog().size());
            } catch (NullPointerException e) {
                assertFalse(true);
            }

        } else {
            System.out.println("Test cannot be run");
        }
    }

    /**
     * Attempts to get a donor that isn't present in the database
     */
    @Test
    public void getAbsentDonor() {
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.getDonor("ZZZ1234");
            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        } else {
            System.out.println("Test cannot be run");
        }
    }

    /**
     * Attempts to get a donor that isn't present in the database
     */
    @Test
    public void getBadRequest() {
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.getDonor("Z2Z1234");
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        } else {
            System.out.println("Test cannot be run");
        }
    }

    /**
     * Attempts to get a proper server error
     */
    @Ignore //Unsure as to how to get this properly
    @Test
    public void testServerError() {
        if (canRunTests) {
            System.out.println("Test can be run");
        } else {
            System.out.println("Test cannot be run");
        }
    }
}
