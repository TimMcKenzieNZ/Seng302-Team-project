package seng302.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seng302.model.database.PhotosService;
import seng302.model.database.UserService;
import seng302.model.person.*;
import seng302.model.security.*;

import java.sql.SQLException;

import static java.lang.Integer.parseInt;


/**
 * The AdminController class that handles all /admin endpoints
 */
@APIController
public class AdminController extends BaseController {

  private UserService service;
  private String versionString = "version";
  private PhotosService photosService;

  @Autowired
  public AdminController(AuthenticationTokenStore authenticationTokenStore) {
    service = new UserService(authenticationTokenStore);
    photosService = new PhotosService(authenticationTokenStore);
  }


  /**
   * Gets all the admins based on query params
   *
   * @param name The name of the admin. applies to firstName, middleName and lastName
   * @param username The username of the admin to find
   * @param amount The amount of admins to return
   * @param offset The amount to offset the returned data by. Primarily used for pagination
   * @return A Collection of admins matching the criteria
   */
  @AdministratorCredentials
  @GetMapping(value = "admins")
  @ResponseBody
  public ResponseEntity getAdmins(@RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "amount", required = false) String amount,
      @RequestParam(value = "offset", required = false) String offset,
      @RequestParam(value = "orderColumn", required = false) String orderColumn,
      @RequestParam(value = "orderDirection", required = false) String orderDirection,
      @RequestParam(value = "region", required = false) String region) {

    String[] params = {name, username, amount, offset, orderColumn, orderDirection, region};
    List<Object> adminQueryResults = service.getAllUsers(dbcConnection.getConnection(),
        "Administrators", Arrays.asList(params));
    Collection<UserSummary> adminSummary = (List<UserSummary>) adminQueryResults.get(0);
    boolean success = (boolean) adminQueryResults.get(1);

    if (!success) {
      return new ResponseEntity<List>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    } else if (adminSummary.isEmpty()) {
      return new ResponseEntity<List>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Collection>(adminSummary, HttpStatus.OK);
    }
  }


  /**
   * Adds a new admin based on the JSON provided
   *
   * @param user The user object
   * @return The user object in the body of the ResponseEntity
   */
  @AdministratorCredentials
  @PostMapping(value = "/admins", headers = "Accept=application/json")
  @ResponseBody
  public ResponseEntity addAdmin(@RequestBody UserCreator user) {

    if (!UserValidator.validateAdminUsername(user.getUsername()) ||
        (!UserValidator.validateAlphanumericString(false, user.getFirst(), 1, 50)) ||
        (!UserValidator.validateAlphanumericString(false, user.getLast(), 1, 50)) ||
        (user.getRegion() == null) ||
        (!UserValidator.validatePassword(user.getPassword()))) {
      return new ResponseEntity<>("Invalid details given",HttpStatus.BAD_REQUEST);
    }

    try {
      int existsCode = service.checkExists(dbcConnection.getConnection(), user);
      if (existsCode == 409) {
        return new ResponseEntity(HttpStatus.CONFLICT);
      }
    } catch (SQLException e) {
        e.printStackTrace();
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    try {
      int[] rs = service.postUser(dbcConnection.getConnection(), user, "admin");
      for (int i : rs) {
        if (i < 0) { // one of the SQL queries failed
          return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
      return new ResponseEntity(HttpStatus.CREATED);
    } catch (SQLException e) {
        e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Get admin information
   * @param username The username of the admin we're requesting
   * @return A RequestEntity containing the admin information
   */
    @AdministratorCredentials
    @RequestMapping(method = RequestMethod.GET, value = "admins/{username}")
    @ResponseBody
    public ResponseEntity getAdmin(@PathVariable String username) {
        try {
            Map<String, User> userMap = service.getUser(dbcConnection.getConnection(), username, false);
            if (userMap.get(username) != null) {
              return new ResponseEntity<>(userMap.get(username), HttpStatus.OK);
            } else {
              return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    /**
     * Update an admin based on the information provided in the body
     * @param username A string of the username of the user being updated
     * @param xAuthToken The session token of the user performing the update
     * @param updates A map of the updates being applied to the user
     * @return A ResponseEntity with an appropriate status code where an "Already Edited" error
     * should return the entire admin object to indicate the differences, especially the version no.
     */
  @AdministratorCredentials
    @RequestMapping(method = RequestMethod.PATCH, value = "admins/{username}")
    @ResponseBody
    public ResponseEntity updateAdmin(@PathVariable String username, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
      try {
          String stringVersion = (String) updates.get(versionString);
          int version = parseInt(stringVersion);
          if(BaseController.checkVersionNumber(username, version) > 0) { // check if the version has changed
              updates.remove(versionString);
              try {
                  return service.updateUser(dbcConnection.getConnection(), username, xAuthToken, updates, version);
              } catch (IllegalArgumentException e) { // we attempted to patch the default admin or the default clinician
                  return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
              }
          } else {
              try { // If version has changed we return the latest version of the user
                  Map<String, User> userMap = service.getUser(dbcConnection.getConnection(), username, false);
                  if (userMap.get(username) != null) {
                      return new ResponseEntity<>(userMap.get(username), HttpStatus.CONFLICT);
                  } else { // we could not find the user
                      return new ResponseEntity(HttpStatus.BAD_REQUEST);
                  }
              } catch (SQLException e) {
                  e.printStackTrace();
                  return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
              }
          }
      } catch (NumberFormatException e) { // we could not find the user to update them, or their version number is not an integer
          return new ResponseEntity<>("User's version number is not an integer", HttpStatus.BAD_REQUEST);
      }
    }

  /**
   * Deletes the admin specified
   * @param username The username of the admin to delete
   * @return A ResponseEntity with the right status code indicating a delete
   */
    @AdministratorCredentials
    @RequestMapping(method = RequestMethod.DELETE, value = "admins/{username}")
    @ResponseBody
    public ResponseEntity deleteAdmin(@PathVariable String username) {
      return service.deleteUser(dbcConnection.getConnection(), username);
    }

    @AdministratorCredentials
    @RequestMapping(method = RequestMethod.GET, value = "log")
    @ResponseBody
    public ResponseEntity getSystemLog() {
        return service.getSystemLog(dbcConnection.getConnection());
    }

    @AnyDonorCredentials
    @RequestMapping(method = RequestMethod.GET, value = "countries")
    @ResponseBody
    public ResponseEntity getCountries() {
        return service.getCountries(dbcConnection.getConnection());
    }

    @AdministratorCredentials
    @RequestMapping(method = RequestMethod.DELETE, value = "countries/{country}")
    @ResponseBody
    public ResponseEntity deleteCountry(@PathVariable String country) {
        return service.deleteCountry(dbcConnection.getConnection(), country);
    }

    @AdministratorCredentials
    @RequestMapping(method = RequestMethod.POST, value = "countries/{country}")
    @ResponseBody
    public ResponseEntity postCountry(@PathVariable String country) {
        return service.postCountry(dbcConnection.getConnection(), country);
    }
}
