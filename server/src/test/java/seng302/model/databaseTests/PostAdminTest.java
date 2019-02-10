package seng302.model.databaseTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.model.person.UserCreator;
import seng302.model.security.AuthenticationTokenStore;

public class PostAdminTest extends APITest {

  private static AdminController adminController;
  private static UserCreator userCreator;
  private static ResponseEntity responseEntity;


  @Before
  public void setUp() {
    connectToDatabase();
    adminLogin("test");
    adminController = new AdminController(authenticationTokenStore);
  }


  /**
   * Tests the successful creation of a valid user
   */
  @Test
  public void postSuccessfulAdmin() {
    userCreator = new UserCreator("Wellington1", "Steve", "Jobs",
        "password", "Wellington", "Somewhere here", "Sudo", null);
    responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
    responseEntity = adminController.deleteAdmin(userCreator.getUsername());
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }


  /**
   * Tests a conflict message is raised when a user already exists with the given username
   */
  @Test
  public void postConflictAdmin() {
    adminController.deleteAdmin("Wellington1");
    userCreator = new UserCreator("Wellington1", "Steve", "Jobs",
        "password", "Wellington", "Somewhere here", "Sudo", null);
    responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    responseEntity = adminController.deleteAdmin(userCreator.getUsername());
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }


  /**
   * Tests that an error message is shown when an invalid value is given to the usercreator
   */
  @Test
  public void postBadRequestAdmin() {
    userCreator = new UserCreator("867975", "Steve", "Jobs", "password",
        "Canterbury", "Somewhere here", "Sudo", null);
    ResponseEntity responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    userCreator = new UserCreator("Wellington1",
        "rhiguhnkufvjytfrdxcjytgfckygfvkjytfdcjkgtvljhgkltgfcdkmhgvktdfkufvluyfgvbkrtuhvbslrkitgblruitbghlsrugblitbg", "",
        "Jobs", "password", "Canterbury", "Somewhere here",
        "Sudo");
    responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    userCreator = new UserCreator("Wellington1", "Steve", "",
        "rhiguhnkufvjytfrdxcjytgfckygfvkjytfdcjkgtvljhgkltgfcdkmhgvktdfkufvluyfgvbkrtuhvbslrkitgblruitbghlsrugblitbg", "Canterbury", "Somewhere here", "Sudo", null);
    responseEntity = adminController.addAdmin(userCreator);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }
}



