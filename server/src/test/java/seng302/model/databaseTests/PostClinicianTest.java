package seng302.model.databaseTests;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.ClinicianController;
import seng302.model.person.UserCreator;
import seng302.model.security.AuthenticationTokenStore;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PostClinicianTest extends APITest {

    private static ClinicianController clinicianController;
    private static UserCreator userCreator;
    private static ResponseEntity responseEntity;

    @Before
    public void setUp() {
        connectToDatabase();
        adminLogin("test");
        clinicianController = new ClinicianController(authenticationTokenStore);
        userCreator = new UserCreator("13", "Steve", "Jobs", "password", "Canterbury", "Somewhere here", "Sudo", null);
        responseEntity = clinicianController.addClinician(userCreator);
    }

    @Test
    public void postSuccessfulClinician() {
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        responseEntity = clinicianController.deleteClinician("13");
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void postConflictClinician() {
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        responseEntity = clinicianController.addClinician(userCreator);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        responseEntity = clinicianController.deleteClinician("13");
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void postBadRequestClincian() {
        userCreator = new UserCreator("null", "Steve", "Jobs", "password", "Canterbury", "Somewhere here", "Sudo", null);
        ResponseEntity responseEntity = clinicianController.addClinician(userCreator);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @After
    public void tearDown() {
        responseEntity = clinicianController.deleteClinician("13");
    }

}
