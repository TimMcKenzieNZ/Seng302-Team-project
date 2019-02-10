package seng302.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

/**
 * A class for testing the AccountManager class methods.
 */

public class ActiveIngredientsServiceTest {

  private ActiveIngredientsService myAIService;
  private Collection myActiveIngredients;


  /**
   * Set up ActiveIngredientsService
   */
  @Before
  public void setUp() {
    myAIService = new ActiveIngredientsService();
    myActiveIngredients = new ArrayList<String>();

  }

  /**
   * tests that a using the service with a drug name returns the expected list of ingredients
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testValidDrugName() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("reserpine");
      assertTrue(myActiveIngredients.toString()
          .equals("[Hydralazine hydrochloride; hydrochlorothiazide; reserpine, "
              + "Hydrochlorothiazide; reserpine, Hydroflumethiazide; reserpine, Reserpine]"));
    } catch (JsonParseException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testValidDrugName");
    } catch (UnknownHostException unknownHostException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testValidDrugName");
    }
  }

  /**
   * tests for expected behaviour (returns empty list) where the drug name is invalid
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testInvalidDrugName() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("fdnvcz");
      assertTrue(myActiveIngredients.toString().equals("[]"));
    } catch (JsonParseException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testInvalidDrugName");
    } catch (UnknownHostException unknownHostException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testInvalidDrugName");
    }
  }

  /**
   * tests for unexpected behaviour if the drug name contains a space character
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testSpaceChar() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("fd f");
      assertEquals("[invalid drug name]", myActiveIngredients.toString());
    } catch (JsonParseException | UnknownHostException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testSpaceChar");
    } catch (SocketException ex) {
      System.out.println("Socket Exception thrown, usually network error");
    }
  }

  /**
   * tests for unexpected behaviour if the drug name contains special characters
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testSpecialChars() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("fd/?>f");
      assertTrue(myActiveIngredients.toString().equals("[invalid drug name]"));
    } catch (JsonParseException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testSpecialChars");
    } catch (UnknownHostException unknownHostException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testSpecialChars");
    }
  }

  /**
   * tests for unexpected behaviour if the drug name contains a dash character
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testDashChar() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("fd-f");
      assertTrue(myActiveIngredients.toString().equals("[]"));
    } catch (JsonParseException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testDashChar");
    } catch (UnknownHostException unknownHostException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testDashChar");
    }
  }

  /**
   * tests for unexpected behaviour if the drug name contains an underscore character
   *
   * @throws IOException if API does not respond
   */
  @Test
  public void testUnderscoreChar() throws IOException {
    try {
      myActiveIngredients = myAIService.activeIngredients("fd_f");
      assertTrue(myActiveIngredients.toString().equals("[]"));
    } catch (JsonParseException jsonParseException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testUnderscoreChar");
    } catch (UnknownHostException unknownHostException) {
      System.out.println(
          "Internet not enabled - Test skipped - ActiveIngredientsServiceTest - testUnderscoreChar");
    }
  }
}
