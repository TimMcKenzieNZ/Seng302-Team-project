package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.ClinicianController;
import seng302.model.person.Clinician;
import seng302.model.security.AuthenticationTokenStore;

import static org.junit.Assert.assertEquals;

public class GetClinicianUsernameTest extends APITest {

    private static ClinicianController clinicianController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        clinicianController = new ClinicianController(authenticationTokenStore);
    }

    @Test
    public void successfulGetTest() {
        ResponseEntity responseEntity = clinicianController.getClinician("0");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Clinician clinician = (Clinician) responseEntity.getBody();
        assertEquals("0", clinician.getUserName());
    }

    @Test
    public void notFoundGetTest() {
        ResponseEntity responseEntity = clinicianController.getClinician("25");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}
