package seng302.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seng302.model.database.PhotosService;
import seng302.model.database.UserService;
import seng302.model.person.User;
import seng302.model.person.UserCreator;
import seng302.model.person.UserSummary;
import seng302.model.person.UserValidator;
import seng302.model.security.AdministratorCredentials;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.ClinicianCredentials;
import seng302.model.security.ThisDonorCredentials;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * * The ClinicianController class that handles all /clinician endpoints
 */
@APIController
public class ClinicianController extends BaseController {

  private UserService service;
  private String versionString = "version";
  private PhotosService photosService;


  @Autowired
  public ClinicianController(AuthenticationTokenStore authenticationTokenStore) {
    service = new UserService(authenticationTokenStore);
    photosService = new PhotosService(authenticationTokenStore);
  }

  /**
   * Gets all the clinicians based on the query params
   * @param name The name of the clinician to search for
   * @param id The id of the clinician
   * @param amount The number of clinicians to return
   * @param offset The offset of the data returned
   * @return A collection of clinicians matching the summary
   */
  @ClinicianCredentials
  @GetMapping(value = "clinicians")
  @ResponseBody
  public ResponseEntity getClinicians(@RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "id", required = false) String id,
      @RequestParam(value = "amount", required = false) String amount,
      @RequestParam(value = "offset", required = false) String offset,
      @RequestParam(value = "orderColumn", required = false) String orderColumn,
      @RequestParam(value = "orderDirection", required = false) String orderDirection,
      @RequestParam(value = "region", required = false) String region) {
    String[] params = {name, id, amount, offset, orderColumn, orderDirection, region};

    List<Object> clinicianQueryResult = service.getAllUsers(dbcConnection.getConnection(),
        "Clinicians", Arrays.asList(params));
    Collection<UserSummary> clinicianSummary = (List<UserSummary>) clinicianQueryResult.get(0);
    boolean success = (boolean) clinicianQueryResult.get(1);

    if (!success) {
      return new ResponseEntity<List>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    } else if (clinicianSummary.isEmpty()) {
      return new ResponseEntity<List>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Collection>(clinicianSummary, HttpStatus.OK);
    }
  }

  /**
   *
   * @param user
   * @return
   */
  @ClinicianCredentials
  @PostMapping(value = "/clinicians", headers="Accept=application/json")
  @ResponseBody
  public ResponseEntity addClinician(@RequestBody UserCreator user) {

    if (user.getStreetAddress() == null) {
      user.setStreetAddress("");
    }

    if ((!UserValidator.validateStaffIDIsInt(user.getUsername())) ||
        (!UserValidator.validateAlphanumericString(false, user.getFirst(), 1, 50)) ||
        (!UserValidator.validateAlphanumericString(false, user.getLast(), 1, 50)) ||
        (!UserValidator.validateAlphanumericString(true, user.getStreetAddress(), 0, 100)) ||
        (user.getRegion() == null) ||
        (!UserValidator.validatePassword(user.getPassword()))) {
      return new ResponseEntity<>("Invalid details given",HttpStatus.BAD_REQUEST);
    }

    String existsQuery = String.format("SELECT `username` FROM Users WHERE `username`='%s'", user.getUsername());
    try (PreparedStatement preparedStatement = dbcConnection.getConnection().prepareStatement(existsQuery)) {
      try (ResultSet result = preparedStatement.executeQuery()) {
        if (result.isBeforeFirst()) {
          return new ResponseEntity(HttpStatus.CONFLICT);
        }
      }
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    try {
      int[] rs = service.postUser(dbcConnection.getConnection(), user, "clinician");
      for (int i: rs) {
        if (i < 0) { // one of the SQL queries failed
          return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
      return new ResponseEntity(HttpStatus.CREATED);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }


  @ClinicianCredentials
  @GetMapping(value = "clinicians/{staffId}")
  @ResponseBody
  public ResponseEntity getClinician(@PathVariable String staffId) {
    try {
      Map<String, User> userMap = service.getUser(dbcConnection.getConnection(), staffId, true);
      if (userMap.get(staffId) != null) {
        return new ResponseEntity<>(userMap.get(staffId), HttpStatus.OK);
      } else {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
      }
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @ClinicianCredentials
  @PatchMapping(value = "clinicians/{staffId}")
  @ResponseBody
  public ResponseEntity updateClinician(@PathVariable String staffId, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      int version = (Integer) updates.get(versionString);
      if(BaseController.checkVersionNumber(staffId, version) > 0) { // check if the version has changed
        updates.remove(versionString);
        try {
          return service.updateUser(dbcConnection.getConnection(), staffId, xAuthToken, updates, version);
        } catch (IllegalArgumentException e) { // we attempted to patch the default admin or the default clinician
          return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
      } else {
        try { // If version has changed we return the latest version of the user
          Map<String, User> userMap = service.getUser(dbcConnection.getConnection(), staffId, true);
          if (userMap.get(staffId) != null) {
            return new ResponseEntity<>(userMap.get(staffId), HttpStatus.CONFLICT);
          } else { // we could not find the user
            return new ResponseEntity(HttpStatus.NOT_FOUND);
          }
        } catch (SQLException e) {
          return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    } catch (NumberFormatException | NullPointerException e) { // we could not find the user to update them, or their version number is not an integer
      return new ResponseEntity<>("User's version number is not an integer", HttpStatus.BAD_REQUEST);
    }
  }


  @AdministratorCredentials
  @DeleteMapping(value = "clinicians/{staffId}")
  @ResponseBody
  public ResponseEntity deleteClinician(@PathVariable String staffId) {
    return service.deleteUser(dbcConnection.getConnection(), staffId);
  }

  @ClinicianCredentials
  @PostMapping(value="clinicians/{staffId}/photo")
  public ResponseEntity addProfilePhoto(@PathVariable String staffId, @RequestHeader(value="x-auth-token") String xAuthToken, @RequestHeader(value="Content-Type") String type, @RequestBody byte[] photoBytes) {
    try{
      ByteArrayInputStream photo = new ByteArrayInputStream(photoBytes);
      if(photo.available() >= PhotosService.MAX_BYTES || !PhotosService.goodPhotoType.contains(type)) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }
      return photosService.insertPhoto(dbcConnection.getConnection(), photo, type, staffId);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @DeleteMapping(value="clinicians/{staffId}/photo")
  public ResponseEntity removeProfilePicture(@PathVariable String staffId, @RequestHeader(value="x-auth-token") String xAuthToken) {
    if(photosService.deletePhoto(dbcConnection.getConnection(), staffId)) {
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @ClinicianCredentials
  @GetMapping(value="clinicians/{staffId}/photo")
  public ResponseEntity getProfilePhoto(@PathVariable String staffId, @RequestHeader(value="x-auth-token") String xAuthToken) {
    return photosService.getPhoto(dbcConnection.getConnection(), staffId);
  }
}
