package seng302.model.databaseTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.model.person.UserCreator;

public class DeleteAdminTest extends APITest {

  AdminController adminController;

  @Before
  public void setUp() {
    connectToDatabase();
    adminLogin("test");
    adminController = new AdminController(authenticationTokenStore);
  }


  /**
   * Tests the successful deletion of an administrator through the server
   */
  @Test
  public void deleteUsersSuccess() {
    ResponseEntity responseEntity = adminController.addAdmin(new UserCreator("Wellington33", "Bob","Norman", "Martin",
        "password", "Wellington", "This is a Street", "Sudo"));
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    responseEntity = adminController.deleteAdmin("Wellington33");
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }


  /**
   * Tests the deletion of an administrator that is not in the database
   */
  @Test
  public void deleteNonExistent() {
    ResponseEntity responseEntity = adminController.deleteAdmin("NotAnAdmin");
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals("Couldn't find user", responseEntity.getBody());
  }


  /**
   * Tests what happens when a null value is passed to the delete function
   */
  @Test
  public void deleteNull() {
    ResponseEntity responseEntity = adminController.deleteAdmin(null);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }
}
