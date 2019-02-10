package seng302.model.databaseTests;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.person.DonorReceiver;
import seng302.model.person.DonorReceiverCreator;
import seng302.model.person.DonorReceiverSummary;
import seng302.model.security.AuthenticationToken;

import static org.junit.Assert.*;

public class PatchUserAttributeCollectionTest extends APITest {

  private static DonorReceiverController donorReceiverController;
  private static final String AIR = "DEF4321";

  @BeforeClass
  public static void setUp() {
    connectToDatabase();
    adminLogin("test");
    donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    AuthenticationToken authenticationToken = new AuthenticationToken(token);
    authenticationTokenStore.add(authenticationToken);
  }

  private void printCannotRunTests() {
    System.out.println("Can't run the tests");
  }

  @Before
  public void createTestDonor() {
    // try to delete the donor in case they are still there
    donorReceiverController.deleteDonorReceiver(AIR);
    DonorReceiverCreator creator = new DonorReceiverCreator(AIR, "Air", "", "Plant", "1997-01-01", "password", "Sudo", "039833721");
    ResponseEntity responseEntity = donorReceiverController.addDonor(creator);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @After
  public void deleteTestDonor() {
    ResponseEntity responseEntity = donorReceiverController.deleteDonorReceiver(AIR);
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }

  @Test
  public void blueSkyTest() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
      ResponseEntity getDonorResponse = donorReceiverController.getDonor(AIR);
      assertEquals(HttpStatus.OK, getDonorResponse.getStatusCode());
      DonorReceiver donor = (DonorReceiver) getDonorResponse.getBody();
      assertEquals(8, donor.getModifications().size());
//      ArrayList<String> logEntries = new ArrayList<>();
//      for (LogEntry log : donor.getModifications()) {
//        logEntries.add(log.toString().substring(0, log.toString().length() - 20));
//      }
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, alcoholConsumption changed from 'null' to '2' at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, bloodPressure changed from 'null' to '10/10' at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, bloodType changed from 'null' to 'B+' at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, bodyMassIndexFlag changed from 'null' to 'false' at "));
//      assertTrue(logEntries.contains("User Created: DEF4321, Created by: Sudo, at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, height changed from 'null' to '2.0' at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, smoker changed from 'null' to 'false' at "));
//      assertTrue(logEntries.contains("User Being Modified: DEF4321, Changed by User: Sudo, weight changed from 'null' to '51.0' at "));
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void patchUserDoesNotExist() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection("DEF12345", token, updates);
      assertEquals("No such donor.", responseEntity.getBody());
      assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void patchBadVersion() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "0");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void patchNoVersion() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void patchInvalidVersion() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", 0);
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid Version", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void baseTest() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingHeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Height not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void invalidHeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "4.5");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid height.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullHeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "null");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingWeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Weight not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void invalidWeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "-1.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid weight.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullWeight() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "null");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingBloodType() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Blood Type not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void invalidBloodType() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "CB+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid blood type.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullBloodType() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "null");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingBloodPressure() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Blood Pressure not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void invalidBloodPressure() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10/1");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid blood pressure.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullBloodPressure() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "null");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingSmoker() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Smoker status not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullSmoker() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "null");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingAlcohol() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Alcohol Consumption not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void invalidAlcohol() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "-2");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Invalid alcohol consumption.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullAlcohol() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "null");
      updates.put("bodyMassIndexFlag", "false");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void missingBmiFlag() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("BMI Flag not given.", responseEntity.getBody());
      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

  @Test
  public void nullBmiFlag() {
    if (canRunTests) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("version", "2");
      updates.put("weight", "51.0");
      updates.put("height", "2.0");
      updates.put("bloodType", "B+");
      updates.put("bloodPressure", "10/10");
      updates.put("smoker", "false");
      updates.put("alcoholConsumption", "2");
      updates.put("bodyMassIndexFlag", "null");
      ResponseEntity responseEntity = donorReceiverController.updateUserAttributeCollection(AIR, token, updates);
      assertEquals("Updated donor", responseEntity.getBody());
      assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    } else {
      printCannotRunTests();
    }
  }

}
