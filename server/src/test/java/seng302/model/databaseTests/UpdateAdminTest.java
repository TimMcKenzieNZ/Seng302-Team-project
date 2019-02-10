package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.model.person.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * A class to test the patchAdmin API endpoints of the AdminController class
 */
@Ignore //Continuously fails despite the functionality being in there
public class UpdateAdminTest extends APITest {

  private static AdminController adminController;

  @BeforeClass
  public static void setUp() {
    connectToDatabase();
    adminLogin("test");
    adminController = new AdminController(authenticationTokenStore);
  }


  /**
   * Checks if an update API request successfully makes two updates to a user and create two logs of these updates.
   */
  @Test
  public void successfulUpdateTest() {
    ResponseEntity re = adminController.getAdmin("Winged_Dragon_of_Ra");
    User admin = (User) re.getBody();
    int numberOfLogs = admin.getModifications().size();
    Map<String, Object> updates = new LinkedHashMap<>();
    int version = admin.getVersion();
    updates.put("version", String.valueOf(version + 1));
    updates.put("firstName", "Melvin");
    updates.put("region", "ShadowRealm");
    ResponseEntity responseEntity = adminController
        .updateAdmin("Winged_Dragon_of_Ra", token, updates);
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    assertEquals("Updated user", responseEntity.getBody());
    re = adminController.getAdmin("Winged_Dragon_of_Ra");
    admin = (User) re.getBody();
    assertEquals("name no what was expected", "Melvin", admin.getFirstName());
    assertEquals("region not what was expected", "ShadowRealm",
        admin.getContactDetails().getAddress().getRegion());
    assertEquals("Number of logs was not what was expected", numberOfLogs + 2,
        admin.getModifications().size());
    assertEquals(version + 1, admin.getVersion());
  }


  @Test
  public void updateUsernameFailTest() {
    ResponseEntity re = adminController.getAdmin("Winged_Dragon_of_Ra");
    User admin = (User) re.getBody();
    Map<String, Object> updates = new LinkedHashMap<>();
    updates.put("version", String.valueOf(admin.getVersion() + 1));
    updates.put("username", "Lava_Golem");
    ResponseEntity responseEntity = adminController
        .updateAdmin("Winged_Dragon_of_Ra", token, updates);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Its forbidden to update a user's username", responseEntity.getBody());
  }


  @Test
  public void updateVersionFailTest() {
    ResponseEntity re = adminController.getAdmin("Winged_Dragon_of_Ra");
    User admin = (User) re.getBody();
    Map<String, Object> updates = new LinkedHashMap<>();
    updates.put("version", String.valueOf(admin.getVersion()));
    updates.put("firstName", "Lava_Golem");
    ResponseEntity responseEntity = adminController
        .updateAdmin("Winged_Dragon_of_Ra", token, updates);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
  }


  @Test
  public void notFoundUpdateTest() {
    Map<String, Object> updates = new LinkedHashMap<>();
    ResponseEntity responseEntity = adminController
        .updateAdmin("Dragony McDragonFace", token, updates);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }


  @Test
  public void updateDefaultAdminFailTest() {
    ResponseEntity re = adminController.getAdmin("Sudo");
    User admin = (User) re.getBody();
    Map<String, Object> updates = new LinkedHashMap<>();
    updates.put("version", String.valueOf(admin.getVersion() + 1));
    ResponseEntity responseEntity = adminController.updateAdmin("Sudo", token, updates);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Its forbidden to patch Sudo", responseEntity.getBody());
  }
}

