package seng302.controller;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seng302.model.Illness;
import seng302.model.MedicalProcedure;
import seng302.model.ProcedureDelete;
import seng302.model.database.DonorReceiverService;
import seng302.model.database.PhotosService;
import seng302.model.database.UserService;
import seng302.model.person.*;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.ClinicianCredentials;
import seng302.model.security.ThisDonorCredentials;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static seng302.model.Illness.validateName;
import static seng302.model.MedicalProcedure.validAffectedOrgans;

@APIController
public class DonorReceiverController extends BaseController {

  private DonorReceiverService donorReceiverService;

  private UserService userService;
  private PhotosService photosService;

  private String badRequestString = "Bad Request";
  private String invalidVersionString = "Invalid Version";
  private String invalidDetailsString = "Invalid details given";
  private String versionString = "version";

  @Autowired
  public DonorReceiverController(AuthenticationTokenStore authenticationTokenStore) {
    donorReceiverService = new DonorReceiverService(authenticationTokenStore);
    userService = new UserService(authenticationTokenStore);
    photosService = new PhotosService(authenticationTokenStore);
  }




  @ClinicianCredentials
  @GetMapping(value = "donors", headers="Accept=application/json")
  @ResponseBody
  public ResponseEntity getDonors(@RequestParam(value = "q", required = false) String search,
                                                    @RequestParam(value = "amount", required = false) String amount,
                                                    @RequestParam(value = "index", required = false) String index,
                                                    @RequestParam(value = "status", required = false) String status,
                                                    @RequestParam(value = "gender", required = false) String gender,
                                                    @RequestParam(value = "region", required = false) String region,
                                                    @RequestParam(value = "donations", required = false) String donations,
                                                    @RequestParam(value = "receiving", required = false) String receiving,
                                                    @RequestParam(value = "minAge", required = false) String minAge,
                                                    @RequestParam(value = "maxAge", required = false) String maxAge,
                                                    @RequestParam(value = "sortBy", required = false) String sortBy,
                                                    @RequestParam(value = "isDescending", required = false) String isDescending) {
    String[] params = {search, amount, index, status, gender, region, donations, receiving, minAge, maxAge};
    List<String> paramList = Arrays.asList(params);
    try {
      return donorReceiverService.getAllDonorReceiver(dbcConnection.getConnection(), paramList, sortBy, isDescending);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

    }
  }


  /**
   * Creates a new donor from the information provided
   * @param person The information given about the person from the json sent with the request object
   */
  @PostMapping(value = "/donors", headers="Accept=application/json")
  public ResponseEntity addDonor(@RequestBody DonorReceiverCreator person) {

    String[] params = {person.getNhi(), person.getGivenName(), person.getMiddleName(),
            person.getLastName(), person.getDateOfBirth(), person.getPassword(),
            person.getModifyingAccount(), person.getMobileNumber()};
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(params[4], formatter);
    if ((!UserValidator.checkNHIRegex(params[0])) ||
            (!UserValidator.validateAlphanumericString(false, params[1], 1, 50)) ||
            (!UserValidator.validateAlphanumericString(false, params[2], 0, 50)) ||
            (!UserValidator.validateAlphanumericString(false, params[3], 0, 50)) ||
            (!UserValidator.validateDateOfBirth(localDate)) ||
            (!UserValidator.validateAlphanumericString(false, params[5], 1, 50)) ||
        (!UserValidator.validatePhoneNumber(params[7]))) {
      return new ResponseEntity<>(invalidDetailsString,HttpStatus.BAD_REQUEST);
    }
    try {
      int code = donorReceiverService.createUser(dbcConnection.getConnection(), Arrays.asList(params));
      if (code == 409) {
        return new ResponseEntity(HttpStatus.CONFLICT);
      } else if (code == 201) {
        return new ResponseEntity(HttpStatus.CREATED);
      }
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @ThisDonorCredentials
  @GetMapping(value = "donors/{nhi}")
  @ResponseBody
  public ResponseEntity getDonor(@PathVariable String nhi) {
    if (!UserValidator.checkNHIRegex(nhi)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      Map<String, DonorReceiver> donorReceivers = donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi);
      if (donorReceivers.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(donorReceivers.get(nhi), HttpStatus.OK);
    } catch (SQLException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ThisDonorCredentials
  @PatchMapping(value = "donors/{nhi}")
  public ResponseEntity updateDonor(@PathVariable String nhi, @RequestBody Map<String, Object> updates) {
    try{
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        return donorReceiverService.updateUser(dbcConnection.getConnection(), nhi, updates, version);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @PatchMapping(value = "donors/{nhi}/userAttributeCollection")
  public ResponseEntity updateUserAttributeCollection(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if (BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserAttributeCollection(dbcConnection.getConnection(), nhi, updates, version, xAuthToken);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ClinicianCredentials
  @PatchMapping(value ="donors/{nhi}/illnesses")
  public ResponseEntity updateDonorIllness(@PathVariable String nhi, @RequestHeader String illness, @RequestHeader(value = "x-auth-token") String xAuthToken,@RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserIllness(dbcConnection.getConnection(), nhi, illness, updates, version, xAuthToken);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ClinicianCredentials
  @PostMapping(value ="donors/{nhi}/illnesses")
  public ResponseEntity addDonorIllness(@PathVariable String nhi, @RequestBody
      Illness illness) {
    DonorReceiver donorReceiver;
    try {
      Map<String, DonorReceiver> map = donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi);
      donorReceiver = map.get(nhi);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if ((!validateName(illness.getName())) || (!illness.validateDate(illness.getDate(), donorReceiver.getDateOfBirth()))) {
      return new ResponseEntity<>(invalidDetailsString,HttpStatus.BAD_REQUEST);
    }
    return donorReceiverService.addUserIllness(dbcConnection.getConnection(), nhi, illness);
  }

  @ClinicianCredentials
  @DeleteMapping(value="donors/{nhi}/illnesses")
  public ResponseEntity deleteDonorIllness(@PathVariable String nhi, @RequestBody Illness illness) {
    return donorReceiverService.deleteIllness(dbcConnection.getConnection(), nhi, illness);
  }

  @ClinicianCredentials
  @PatchMapping(value ="donors/{nhi}/procedures")
  public ResponseEntity updateDonorProcedure(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserProcedure(dbcConnection.getConnection(), nhi, updates, version, xAuthToken);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }
  @ClinicianCredentials
  @PostMapping(value ="donors/{nhi}/procedures")
  public ResponseEntity addDonorProcedure(@PathVariable String nhi, @RequestBody MedicalProcedure medicalProcedure) {
    DonorReceiver donorReceiver;
    try {
      Map<String, DonorReceiver> map = donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi);
      donorReceiver = map.get(nhi);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if ((!UserValidator.validateProcedureDate(medicalProcedure.getDate(),donorReceiver.getDateOfBirth())) ||
        (medicalProcedure.getSummary().length() > 100) || (medicalProcedure.getDescription().length() < 1) ||
                (!validAffectedOrgans(medicalProcedure.getAffectedOrgans()))) {
      return new ResponseEntity<>(invalidDetailsString,HttpStatus.BAD_REQUEST);
    }
    return donorReceiverService.addUserMedicalProcedure(dbcConnection.getConnection(), nhi, medicalProcedure);
  }

  @ClinicianCredentials
  @DeleteMapping(value="donors/{nhi}/procedures")
  public ResponseEntity deleteDonorProcedure(@PathVariable String nhi, @RequestBody ProcedureDelete summary) {
    return donorReceiverService.deleteProcedure(dbcConnection.getConnection(), nhi, summary);
  }


  @ThisDonorCredentials
  @PatchMapping(value ="donors/{nhi}/contactDetails")
  public ResponseEntity updateDonorContactDetails(@PathVariable String nhi, @RequestHeader String phone, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestHeader String emergency, @RequestBody Map<String, Object> updates) {
    try {
      boolean emergencyBoolean = emergency.equalsIgnoreCase("true");
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserContactDetails(dbcConnection.getConnection(), nhi, phone, emergencyBoolean, updates, version, xAuthToken);
      } else {
        return new ResponseEntity<>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }
  @ThisDonorCredentials
  @PostMapping(value = "donors/{nhi}/contactDetails")
  public ResponseEntity addDonorContactDetails(@PathVariable String nhi, @RequestHeader String emergency, @RequestBody ContactDetails contactDetails) {
    boolean emergencyBoolean = emergency.equalsIgnoreCase("true");
    return donorReceiverService.addUserContactDetails(dbcConnection.getConnection(), nhi, emergencyBoolean, contactDetails);
  }

  @ClinicianCredentials
  @PatchMapping(value = "donors/{nhi}/deathDetails")
  public ResponseEntity updateDonorDeathDetails(@PathVariable String nhi, @RequestBody Map<String, Object> updates) {
    return donorReceiverService.updateUserDeathDetails(dbcConnection.getConnection(), nhi, updates);
  }

  @ClinicianCredentials
  @PostMapping(value = "donors/{nhi}/deathDetails")
  public ResponseEntity createDonorDeathDetails(@PathVariable String nhi, @RequestBody DeathDetails deathDetails) {
    return donorReceiverService.addUserDeathDetails(dbcConnection.getConnection(), nhi, deathDetails);
  }

  @ClinicianCredentials
  @DeleteMapping(value = "donors/{nhi}/deathDetails")
  public ResponseEntity deleteDonorDeathDetails(@PathVariable String nhi) {
    return donorReceiverService.removeUserDeathDetails(dbcConnection.getConnection(), nhi);
  }

  @ClinicianCredentials
  @DeleteMapping(value = "donors/{nhi}")
  @ResponseBody
  public ResponseEntity deleteDonorReceiver(@PathVariable String nhi) {
    return userService.deleteUser(dbcConnection.getConnection(), nhi);
  }

  @ClinicianCredentials
  @PostMapping(value ="donors/{nhi}/medication")
  public ResponseEntity addDonorMedication(@PathVariable String nhi, @RequestBody Medication medication, @RequestHeader(value = "x-auth-token") String token) {
    return donorReceiverService.addUserMedication(dbcConnection.getConnection(), nhi, token, medication);
  }

  @ClinicianCredentials
  @PatchMapping(value ="donors/{nhi}/medication")
  public ResponseEntity updateDonorMedication(@PathVariable String nhi, @RequestBody Medication medication, @RequestHeader(value = "x-auth-token")String token, @RequestHeader(value = "version") String versionString) {
    try {
      int version = parseInt(versionString);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        return donorReceiverService.updateUserMedication(dbcConnection.getConnection(), nhi, token, medication);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }


  }

  @ClinicianCredentials
  @DeleteMapping(value ="donors/{nhi}/medication")
  public ResponseEntity removeDonorMedication(@PathVariable String nhi, @RequestBody Medication medication, @RequestHeader(value = "x-auth-token") String token) {
    return donorReceiverService.deleteUserMedication(dbcConnection.getConnection(), nhi, token, medication);
  }


  @ThisDonorCredentials
  @PatchMapping(value="donors/{nhi}/donorOrgans")
  public ResponseEntity updateDonorOrgans(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserDonorOrgans(dbcConnection.getConnection(), nhi, xAuthToken, updates, version);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }


  @ThisDonorCredentials
  @PatchMapping(value="donors/{nhi}/receiverOrgans")
  public ResponseEntity updateReceiverOrgans(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      String[] brokenString = stringVersion.split(":");
      int version;
      if (brokenString.length == 1) {
        version = parseInt(stringVersion);
      } else {
        version = parseInt(brokenString[2]);
      }
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserReceiverOrgans(dbcConnection.getConnection(), nhi, xAuthToken, updates, stringVersion);
      } else {
        return new ResponseEntity<>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @PatchMapping(value="donors/{nhi}/profile")
  public ResponseEntity updateDonorProfile(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserProfile(dbcConnection.getConnection(), nhi, xAuthToken, updates, version);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @PatchMapping(value="donors/{nhi}/basicInformation")
  public ResponseEntity updateDonorBasicInformation(@PathVariable String nhi, @RequestHeader(value = "x-auth-token") String xAuthToken, @RequestBody Map<String, Object> updates) {
    try {
      String stringVersion = (String) updates.get(versionString);
      int version = parseInt(stringVersion);
      if(BaseController.checkVersionNumber(nhi, version) > 0) {
        updates.remove(versionString);
        return donorReceiverService.updateUserBasicInformation(dbcConnection.getConnection(), nhi, xAuthToken, updates, version);
      } else {
        return new ResponseEntity<DonorReceiver>(donorReceiverService.getDonorReceiverSingle(dbcConnection.getConnection(), nhi).get(nhi), HttpStatus.CONFLICT);
      }
    } catch (NumberFormatException | SQLException e) {
      return new ResponseEntity<>(badRequestString, HttpStatus.BAD_REQUEST);
    } catch (ClassCastException e2) {
      return new ResponseEntity<>(invalidVersionString, HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @PostMapping(value="donors/{nhi}/photo")
  public ResponseEntity addProfilePhoto(@PathVariable String nhi, @RequestHeader(value="x-auth-token") String xAuthToken, @RequestBody byte[] photoBytes) {
    try{
      String type = "image";
      ByteArrayInputStream photo = new ByteArrayInputStream(photoBytes);
      if(photo.available() >= PhotosService.MAX_BYTES) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }
      return photosService.insertPhoto(dbcConnection.getConnection(), photo, type, nhi);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @ThisDonorCredentials
  @GetMapping(value="donors/{nhi}/photo")
  public ResponseEntity getProfilePhoto(@PathVariable String nhi, @RequestHeader(value="x-auth-token") String xAuthToken) {
    return photosService.getPhoto(dbcConnection.getConnection(), nhi);
  }

  @ThisDonorCredentials
  @DeleteMapping(value="donors/{nhi}/photo")
  public ResponseEntity removeProfilePicture(@PathVariable String nhi, @RequestHeader(value="x-auth-token") String xAuthToken) {
    if(photosService.deletePhoto(dbcConnection.getConnection(), nhi)) {
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @ClinicianCredentials
  @GetMapping(value="donors/transplantWaitingList")
  public ResponseEntity getTransplantWaitingList(@RequestParam(value="index", required = false) String index,
      @RequestParam(value = "amount", required = false) String amount,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "receivingOrgan", required = false) String receivingOrgan,
      @RequestParam(value = "sortVariable", required = false) String sortVariable,
      @RequestParam(value = "isDesc", required = false) String isDesc) {
    HashMap<String, String> params = new HashMap<>();
    params.put("index", index);
    params.put("amount", amount);
    params.put("region", region);
    params.put("receivingOrgan", receivingOrgan);
    params.put("sortVariable", sortVariable);
    params.put("isDesc", isDesc);
    try {
      return donorReceiverService.getTransplantWaitingList(dbcConnection.getConnection(), params);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
