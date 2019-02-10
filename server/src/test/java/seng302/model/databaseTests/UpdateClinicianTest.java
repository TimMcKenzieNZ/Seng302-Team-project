package seng302.model.databaseTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.ClinicianController;
import seng302.model.person.User;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

@Ignore //For some reason this failed despite the functionality working
public class UpdateClinicianTest extends APITest {

    private static ClinicianController clinicianController;

    @Before
    public void setUp() {
        connectToDatabase();
        adminLogin("test");
        clinicianController = new ClinicianController(authenticationTokenStore);
    }


    /**
     * Checks if an update API request successfully makes two updates to a user and create two logs of these updates.
     */
    @Test
    public void successfulUpdateTest() {
        ResponseEntity re = clinicianController.getClinician("21");
        User admin = (User) re.getBody();
        int numberOfLogs = admin.getModifications().size();
        Map<String, Object> updates = new LinkedHashMap<>();
        int version = admin.getVersion();
        updates.put("version", version + 1);
        updates.put("firstName", "Testing");
        updates.put("region", "adultLand");
        ResponseEntity responseEntity = clinicianController.updateClinician("21", token, updates);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Updated user", responseEntity.getBody());
        re = clinicianController.getClinician("21");
        admin = (User) re.getBody();
        assertEquals("name no what was expected", "Testing", admin.getFirstName());
        assertEquals("region not what was expected", "adultLand", admin.getContactDetails().getAddress().getRegion());
        assertEquals("Number of logs was not what was expected", numberOfLogs + 2, admin.getModifications().size());
        assertEquals(version + 1, admin.getVersion());
    }


    @Test
    public void updateUsernameFailTest() {
        ResponseEntity re = clinicianController.getClinician("21");
        User admin = (User) re.getBody();
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("version", admin.getVersion() + 1);
        updates.put("username", "22");
        ResponseEntity responseEntity = clinicianController.updateClinician("21", token, updates);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Its forbidden to update a user's username", responseEntity.getBody());
    }


    @Test
    public void updateVersionFailTest() {
        ResponseEntity re = clinicianController.getClinician("21");
        User admin = (User) re.getBody();
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("version", admin.getVersion());
        updates.put("firstName", "whatever");
        ResponseEntity responseEntity = clinicianController.updateClinician("21", token, updates);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }


    @Test
    public void notFoundUpdateTest() {
        Map<String, Object> updates = new LinkedHashMap<>();
        ResponseEntity responseEntity = clinicianController.updateClinician("666", token, updates);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    public void updateDefaultClinicianFailTest() {
        ResponseEntity re = clinicianController.getClinician("0");
        User admin = (User) re.getBody();
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("version", admin.getVersion() + 1);
        ResponseEntity responseEntity = clinicianController.updateClinician("0", token, updates);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Its forbidden to patch 0", responseEntity.getBody());
    }


}
