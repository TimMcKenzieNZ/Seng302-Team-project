package seng302.model.databaseTests;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DataController;

public class DataServiceTest extends APITest {

  private static DataController dataController;

  @BeforeClass
  public static void setUp() {
    connectToDatabase();
    adminLogin("test");
    dataController = new DataController(authenticationTokenStore);
  }

  /**
   * Tests that a JSONObject is returned with no valid gender and no region
   */
  @Test
  public void testReceiverReturnsNoParams() {
    ResponseEntity response = dataController.getReceiverDataByOrgans("N", null, null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with valid gender and no region
   */
  @Test
  public void testReceiverGenderReturns() {
    ResponseEntity response = dataController.getReceiverDataByOrgans("M", null, null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with no valid gender and a region
   */
  @Test
  public void testReceiverRegionReturns() {
    ResponseEntity response = dataController.getReceiverDataByOrgans("N", "Canterbury", null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with a valid bloodType
   */
  @Test
  public void testReceiverBloodTypeReturns() {
    ResponseEntity response = dataController.getReceiverDataByOrgans("N",  null, "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with a valid age
   */
  @Test
  public void testReceiverAgeReturns() {
    ResponseEntity response = dataController.getReceiverDataByOrgans("N",  null, null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with no valid gender and no region
   */
  @Test
  public void testDonorReturnsNoParams() {
    ResponseEntity response = dataController.getDonorDataByOrgans("N", null, null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with valid gender and no region
   */
  @Test
  public void testDonorGenderReturns() {
    ResponseEntity response = dataController.getDonorDataByOrgans("M", null, null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with no valid gender and a region
   */
  @Test
  public void testDonorRegionReturns() {
    ResponseEntity response = dataController.getDonorDataByOrgans("N", "Canterbury", null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with a valid bloodType
   */
  @Test
  public void testDonorBloodTypeReturns() {
    ResponseEntity response = dataController.getDonorDataByOrgans("N",  null, "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests that a JSONObject is returned with a valid age
   */
  @Test
  public void testDonorAgeReturns() {
    ResponseEntity response = dataController.getDonorDataByOrgans("N",  null, null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Gender---
  //---Receivers---


  /**
   * Tests the gender endpoint with no filtering
   */
  @Test
  public void testReceiverGenderEndpointReturns() {
    ResponseEntity response = dataController.getReceiverDataGender(null,  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with region filtering
   */
  @Test
  public void testReceiverGenderEndpointRegionFilterReturns() {
    ResponseEntity response = dataController.getReceiverDataGender("Canterbury",  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with blood type filtering
   */
  @Test
  public void testReceiverGenderEndpointBloodTypeFilterReturns() {
    ResponseEntity response = dataController.getReceiverDataGender(null,  "AB+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with age filtering
   */
  @Test
  public void testReceiverGenderEndpointAgeFilterReturns() {
    ResponseEntity response = dataController.getReceiverDataGender(null,  null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Donors


  /**
   * Tests the gender endpoint with no filtering
   */
  @Test
  public void testDonorGenderEndpointReturns() {
    ResponseEntity response = dataController.getDonorDataGender(null,  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with region filtering
   */
  @Test
  public void testDonorGenderEndpointRegionFilterReturns() {
    ResponseEntity response = dataController.getDonorDataGender("Canterbury",  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with blood type filtering
   */
  @Test
  public void testDonorGenderEndpointBloodTypeFilterReturns() {
    ResponseEntity response = dataController.getDonorDataGender(null,  "AB+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the gender endpoint with age filtering
   */
  @Test
  public void testDonorGenderEndpointAgeFilterReturns() {
    ResponseEntity response = dataController.getDonorDataGender(null,  null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Region---
  //---Receivers---


  /**
   * Tests the region endpoint with no filters
   */
  @Test
  public void testReceiverRegionEndpointNoFilters() {
    ResponseEntity response = dataController.getReceiverDataRegion(null,  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the gender filter
   */
  @Test
  public void testReceiverRegionEndpointGender() {
    ResponseEntity response = dataController.getReceiverDataRegion("M",  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the blood type filter
   */
  @Test
  public void testReceiverRegionEndpointBloodType() {
    ResponseEntity response = dataController.getReceiverDataRegion(null,  "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the age filter
   */
  @Test
  public void testReceiverRegionEndpointAge() {
    ResponseEntity response = dataController.getReceiverDataRegion(null,  null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the multiple filters
   */
  @Test
  public void testReceiverRegionEndpointCombo() {
    ResponseEntity response = dataController.getReceiverDataRegion("F",  "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Donor


  /**
   * Tests the region endpoint with no filters
   */
  @Test
  public void testReceiverDonorEndpointNoFilters() {
    ResponseEntity response = dataController.getDonorDataRegion(null,  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the gender filter
   */
  @Test
  public void testReceiverDonorEndpointGender() {
    ResponseEntity response = dataController.getDonorDataRegion("M",  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the blood type filter
   */
  @Test
  public void testReceiverDonorEndpointBloodType() {
    ResponseEntity response = dataController.getDonorDataRegion(null,  "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the age filter
   */
  @Test
  public void testReceiverDonorEndpointAge() {
    ResponseEntity response = dataController.getDonorDataRegion(null,  null, "20");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the region endpoint with the multiple filters
   */
  @Test
  public void testReceiverDonorEndpointCombo() {
    ResponseEntity response = dataController.getDonorDataRegion("F",  "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Age
  //---Receivers


  /**
   * Tests the age endpoint with no filters
   */
  @Test
  public void testReceiverAgeEndpointNoFilters() {
    ResponseEntity response = dataController.getReceiverDataAge(null,  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the age endpoint with the gender filter
   */
  @Test
  public void testReceiverAgeEndpointGender() {
    ResponseEntity response = dataController.getReceiverDataAge("F",  null, null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the age endpoint with the blood type filter
   */
  @Test
  public void testReceiverAgeEndpointBloodType() {
    ResponseEntity response = dataController.getReceiverDataAge(null,  "B+", null);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the age endpoint with the region filter
   */
  @Test
  public void testReceiverAgeEndpointRegion() {
    ResponseEntity response = dataController.getReceiverDataAge(null,  null, "Canterbury");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  /**
   * Tests the age endpoint with the region filter
   */
  @Test
  public void testReceiverAgeEndpointCombo() {
    ResponseEntity response = dataController.getReceiverDataAge("M",  null, "Canterbury");
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  //---Donors

  /**
   * Tests the transplant waiting list endpoints
   */
  @Test
  public void testTransplantListEndpoint() {
    String lowerBound = "1970-01-01";
    String upperBound = "2018-09-27";
    ResponseEntity response = dataController.getTransplantWaitingListRange(null,null,null,null,upperBound, lowerBound);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /*
   * Tests the transplant waiting list endpoints
   */
  @Test
  public void testTransplantListEndpointQueryBloodtype() {
    String lowerBound = "1970-01-01";
    String upperBound = "2018-09-27";
    ResponseEntity response = dataController.getTransplantWaitingListRange("AB+",null,null,null,upperBound, lowerBound);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /*
   * Tests the transplant waiting list endpoints
   */
  @Test
  public void testTransplantListEndpointQueryGender() {
    String lowerBound = "1970-01-01";
    String upperBound = "2018-09-27";
    ResponseEntity response = dataController.getTransplantWaitingListRange(null,"M",null,null,upperBound, lowerBound);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /*
   * Tests the transplant waiting list endpoints
   */
  @Test
  public void testTransplantListEndpointQueryAge() {
    String lowerBound = "1970-01-01";
    String upperBound = "2018-09-27";
    ResponseEntity response = dataController.getTransplantWaitingListRange(null,null,"12",null,upperBound, lowerBound);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /*
   * Tests the transplant waiting list endpoints
   */
  @Test
  public void testTransplantListEndpointQueryRegion() {
    String lowerBound = "1970-01-01";
    String upperBound = "2018-09-27";
    ResponseEntity response = dataController.getTransplantWaitingListRange(null,null,null,"Auckland",upperBound, lowerBound);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testTransplantListEndpointYearQueryBloodType() {
    String year = "2018";
    ResponseEntity response = dataController.getTransplantWaitingListYear("AB+",null,null,null,year);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testTransplantListEndpointYearQueryGender() {
    String year = "2018";
    ResponseEntity response = dataController.getTransplantWaitingListYear(null,"M",null,null,year);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testTransplantListEndpointYearQueryAge() {
    String year = "2018";
    ResponseEntity response = dataController.getTransplantWaitingListYear(null,null,"12",null,year);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testTransplantListEndpointYearQueryRegion() {
    String year = "2018";
    ResponseEntity response = dataController.getTransplantWaitingListYear(null,null,null,"Auckland",year);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  //Organs through regions

  @Test
  public void testReceiverOrgansByRegion() {
    ResponseEntity responseEntity = dataController.getOrganDataByRegion("B+", "F", null);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }


  @Test
  public void testDonorOrgansByRegion() {
    ResponseEntity responseEntity = dataController.getDonorOrganDataByRegion("B+", "F", null);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

}
