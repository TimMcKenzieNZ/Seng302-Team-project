package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.person.DonorReceiverCreator;
import seng302.model.security.AuthenticationTokenStore;

import static org.junit.Assert.assertEquals;

public class PostDonorReceiverTest extends APITest {

    private static DonorReceiverController donorReceiverController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    }

    @Test
    public void postSuccessfulDonor() {
        if (canRunTests) {
            donorReceiverController.deleteDonorReceiver("AAA9999");
            DonorReceiverCreator donorReceiverCreator = new DonorReceiverCreator("AAA9999", "Steven", "Peter","Jobs", "2018-06-15", "password", "Sudo", "039833721");
            ResponseEntity responseEntity = donorReceiverController.addDonor(donorReceiverCreator);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            donorReceiverController.deleteDonorReceiver("AAA9999");
        } else {
            System.out.println("Tests cant be run");
        }
    }

    @Test
    public void postBadRequest() {
        if (canRunTests) {
            DonorReceiverCreator donorReceiverCreator = new DonorReceiverCreator("AAA9999", "Steven9", "Peter2","Jobs3", "2018-10-15", "password2", "Sudo", "039833721");
            ResponseEntity responseEntity = donorReceiverController.addDonor(donorReceiverCreator);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        } else {
            System.out.println("Tests cant be run");
        }
    }

    @Test
    public void postConflict() {
        if (canRunTests) {
            DonorReceiverCreator donorReceiverCreator = new DonorReceiverCreator("ABC1234", "Steven", "Peter","Jobs", "2018-06-15", "password", "Sudo", "039833721");
            ResponseEntity responseEntity = donorReceiverController.addDonor(donorReceiverCreator);
            assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        } else {
            System.out.println("Tests cant be run");
        }
    }


}
