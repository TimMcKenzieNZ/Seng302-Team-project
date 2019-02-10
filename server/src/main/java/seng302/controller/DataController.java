package seng302.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import seng302.model.database.DataService;
import seng302.model.database.UserService;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.ClinicianCredentials;
import seng302.model.statistics.*;

/**
 * Handles all the API endpoints for the graphs of user data
 */
@APIController
public class DataController extends BaseController {

  private UserService service;

  @Autowired
  public DataController(AuthenticationTokenStore authenticationTokenStore) {
    service = new UserService(authenticationTokenStore);
  }

  @ClinicianCredentials
  @GetMapping(value = "getReceiverDataByOrgans")
  @ResponseBody
  public ResponseEntity getReceiverDataByOrgans(
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "age", required = false) String age) {
    OrganCount organData;
    try {
      organData = DataService.getReceiverDataByOrgans("receivers", dbcConnection.getConnection(), gender, region, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (organData == null) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(organData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getDonorDataByOrgans")
  @ResponseBody
  public ResponseEntity getDonorDataByOrgans(
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "age", required = false) String age) {
    OrganCount organData;
    try {
      organData = DataService.getReceiverDataByOrgans("donors", dbcConnection.getConnection(), gender, region, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (organData == null) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(organData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getReceiverDataGender")
  @ResponseBody
  public ResponseEntity getReceiverDataGender(
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "age", required = false) String age) {
    GenderCount receiverData;
    try {
      receiverData = DataService.getDataGender("receivers", dbcConnection.getConnection(), region, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getDonorDataGender")
  @ResponseBody
  public ResponseEntity getDonorDataGender(
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "age", required = false) String age) {
    GenderCount receiverData;
    try {
      receiverData = DataService.getDataGender( "donors", dbcConnection.getConnection(), region, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }



  @ClinicianCredentials
  @GetMapping(value = "getReceiverBloodType")
  @ResponseBody
  public ResponseEntity getReceiverDataBlood(
          @RequestParam(value = "gender", required = false) String gender,
          @RequestParam(value = "region", required = false) String region,
          @RequestParam(value = "age", required = false) String age) {
    BloodTypeCount receiverData;
    try {
      receiverData = DataService.getReceiverBlood(dbcConnection.getConnection(), gender, region, age);
    } catch(SQLException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getReceiverRegion")
  @ResponseBody
  public ResponseEntity getReceiverDataRegion(
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "age", required = false) String age) {
    RegionCount receiverData;
    try {
      receiverData = DataService.getReceiverDataRegion("receiver", dbcConnection.getConnection(), gender, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getDonorRegion")
  @ResponseBody
  public ResponseEntity getDonorDataRegion(
          @RequestParam(value = "gender", required = false) String gender,
          @RequestParam(value = "bloodType", required = false) String bloodType,
          @RequestParam(value = "age", required = false) String age) {
    RegionCount receiverData;
    try {
      receiverData = DataService.getReceiverDataRegion("donors", dbcConnection.getConnection(), gender, bloodType, age);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getReceiverAge")
  @ResponseBody
  public ResponseEntity getReceiverDataAge(
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "region", required = false) String region) {
    AgeCount receiverData;
    try {
      receiverData = DataService.getReceiverDataAge(dbcConnection.getConnection(), gender, bloodType, region);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(receiverData, HttpStatus.OK);
  }


  @ClinicianCredentials
  @GetMapping(value = "getTransplantListOrgans")
  @ResponseBody
  public ResponseEntity getTransplantWaitingListRange(@RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "age", required = false) String age,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "upperBound") String upperBound,
      @RequestParam(value = "lowerBound") String lowerBound) {
    lowerBound += " 01:00:00.000";
    upperBound += " 01:00:00.000";

    try {
      OrganCount receiverData = DataService.getTransplantWaitingListTime(dbcConnection.getConnection(), bloodType, gender, age, region, upperBound, lowerBound);
      return new ResponseEntity<>(receiverData, HttpStatus.OK);
    }
    catch (SQLException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @ClinicianCredentials
  @GetMapping(value = "getTransplantListOrgansYear")
  @ResponseBody
  public ResponseEntity getTransplantWaitingListYear(@RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "age", required = false) String age,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "year") String year) {
    try {
      Map<String, OrganCount> receiverData = DataService.getTransplantWaitingListForYear(dbcConnection.getConnection(), bloodType, gender, age, region, year);
      return new ResponseEntity<>(receiverData, HttpStatus.OK);
    }
    catch (SQLException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @ClinicianCredentials
  @GetMapping(value = "getTransplantListOrgansTotalYear")
  @ResponseBody
  public ResponseEntity getTransplantListOrgansTotalYear(@RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "age", required = false) String age,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "year") String year) {
    List<String> organs = new ArrayList<>();
    organs.add("lungs");
    organs.add("heart");
    organs.add("liver");
    organs.add("kidneys");
    organs.add("pancreas");
    organs.add("intestine");
    organs.add("corneas");
    organs.add("middleEars");
    organs.add("skin");
    organs.add("bone");
    organs.add("boneMarrow");
    organs.add("connectiveTissue");
    try {
      List<Integer> receiverData = DataService.getTransplantWaitingListOrgansForYear(dbcConnection.getConnection(), bloodType, gender, age, region, year,organs);
      return new ResponseEntity<>(receiverData, HttpStatus.OK);
    }
    catch (SQLException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @ClinicianCredentials
  @GetMapping(value = "getOrganDataByRegion")
  @ResponseBody
  public ResponseEntity getOrganDataByRegion(
        @RequestParam(value = "bloodType", required = false) String bloodType,
        @RequestParam(value = "gender", required = false) String gender,
        @RequestParam(value = "age", required = false) String age) {
    try {
        OrgansByRegionCount organsByRegionCount = DataService.getReceiverOrgansByRegion(dbcConnection.getConnection(), bloodType, gender, age);
        return new ResponseEntity<>(organsByRegionCount, HttpStatus.OK);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @ClinicianCredentials
  @GetMapping(value = "getDonorOrganDataByRegion")
  @ResponseBody
  public ResponseEntity getDonorOrganDataByRegion(
          @RequestParam(value = "bloodType", required = false) String bloodType,
          @RequestParam(value = "gender", required = false) String gender,
          @RequestParam(value = "age", required = false) String age) {
    try {
      OrgansByRegionCount organsByRegionCount = DataService.getDonorOrgansByRegion(dbcConnection.getConnection(), bloodType, gender, age);
      return new ResponseEntity<>(organsByRegionCount, HttpStatus.OK);
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ClinicianCredentials
  @GetMapping(value = "getTransplantListOrgansTotal")
  @ResponseBody
  public ResponseEntity getTransplantListOrgansTotal(@RequestParam(value = "bloodType", required = false) String bloodType,
      @RequestParam(value = "gender", required = false) String gender,
      @RequestParam(value = "age", required = false) String age,
      @RequestParam(value = "region", required = false) String region,
      @RequestParam(value = "lowerBound") String lowerBound,
      @RequestParam(value = "upperBound") String upperBound) {
    List<String> organs = new ArrayList<>();
    organs.add("lungs");
    organs.add("heart");
    organs.add("liver");
    organs.add("kidneys");
    organs.add("pancreas");
    organs.add("intestine");
    organs.add("corneas");
    organs.add("middleEars");
    organs.add("skin");
    organs.add("bone");
    organs.add("boneMarrow");
    organs.add("connectiveTissue");
    try {
      int totalCount = DataService.getTotalTransplantWaitingListCount(dbcConnection.getConnection(), bloodType, gender, age, region, lowerBound, upperBound, organs);
      return new ResponseEntity<>(totalCount, HttpStatus.OK);
    }
    catch (SQLException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

