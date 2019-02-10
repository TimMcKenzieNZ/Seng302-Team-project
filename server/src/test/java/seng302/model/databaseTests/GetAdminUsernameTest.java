package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.controller.DonorReceiverController;
import seng302.model.person.User;
import seng302.model.security.AuthenticationTokenStore;

import static org.junit.Assert.assertEquals;

public class GetAdminUsernameTest extends APITest {
    private static AdminController adminController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        adminController = new AdminController(authenticationTokenStore);
    }

    @Test
    public void successfulGetTest() {
        ResponseEntity responseEntity = adminController.getAdmin("Sudo");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        User admin = (User) responseEntity.getBody();
        assertEquals("Sudo", admin.getUserName());

    }

    @Test
    public void notFoundGetTest() {
        ResponseEntity responseEntity = adminController.getAdmin("bob2");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
