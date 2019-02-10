package seng302.model.databaseTests;

import org.apache.tomcat.jni.Local;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.Illness;
import seng302.model.security.AuthenticationTokenStore;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;


public class PostIllnesesTest extends APITest {

    private static DonorReceiverController donorReceiverController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    }

    @Test
    public void addIllnessPass() {
        Illness illness = new Illness("Common Cold", LocalDate.now(), false, false);
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.addDonorIllness("ABC1234", illness);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            donorReceiverController.deleteDonorIllness("ABC1234", illness);
        } else {
            System.out.println("The test cannot be run");
        }
    }

    @Test
    public void badRequestIllness() {
        Illness illness = new Illness("The flu", LocalDate.now(), true, false);
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.addDonorIllness("ABC1234", illness);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            responseEntity = donorReceiverController.addDonorIllness("ABC1234", illness);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            donorReceiverController.deleteDonorIllness("ABC1234", illness);
        } else {
            System.out.println("The test cannot be run");
        }
    }

}
