package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.security.AuthenticationTokenStore;

import static org.junit.Assert.assertEquals;

public class GetTransplanWaitingListTest extends APITest {

    private static DonorReceiverController donorReceiverController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    }

    @Test
    public void testSuccessfulGet() {
        ResponseEntity responseEntity = donorReceiverController.getTransplantWaitingList(null, null, null, null, null, null);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

}
