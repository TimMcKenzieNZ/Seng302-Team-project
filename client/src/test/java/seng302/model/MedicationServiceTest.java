package seng302.model;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;


/**
 * Contains tests for MedicationService.
 */
public class MedicationServiceTest {

  MedicationAutoCompleteResult medicationAutoCompleteResult;
  String invalidQuery = "\\!??>";
  String emptyQuery = "";
  String shortQuery = "c";
  String normalQuery = "reserp";


  /**
   * Test an invalid query containing special characters gives the expected result, that is it
   * returns an MedicationAutoCompleteResult with the given query and zero suggestions.
   *
   * @throws IOException Thrown by the drugAutoComplete function
   */
  @Test
  public void invalidQueryTest() throws IOException {
    try {
      medicationAutoCompleteResult = MedicationService.drugAutoComplete(invalidQuery);
      assertTrue(medicationAutoCompleteResult.getQuery().equals(invalidQuery));
      assertTrue(medicationAutoCompleteResult.getSuggestions().isEmpty());
    } catch (com.fasterxml.jackson.core.JsonParseException jsonParseException) {
      System.out.println("No internet access");
    }
  }


  /**
   * Test that an empty query string gives the expected result, that is the api connection times out
   * after 10 seconds and it returns a MedicationAutoCompleteResult with the blank query and zero
   * suggestions.
   *
   * @throws IOException an input/output exception
   */
  @Test
  public void emptyQueryTest() throws IOException {
    try {
      medicationAutoCompleteResult = MedicationService.drugAutoComplete(emptyQuery);
      assertTrue(medicationAutoCompleteResult.getQuery().equals(emptyQuery));
      assertTrue(medicationAutoCompleteResult.getSuggestions().isEmpty());
    } catch (com.fasterxml.jackson.core.JsonParseException jsonParseException) {
      System.out.println("No internet access");
    }
  }


  /**
   * Test that a short query string "c" gives the expected result, that is it returns a
   * MedicationAutoCompleteResult with query "c" and suggestions containing(but not limited to)
   * "Camoquin hydrochloride", "Cisplatin" and "Clinolipid".
   *
   * @throws IOException and input/output exception
   */
  @Test
  public void shortQueryTest() throws IOException {
    try {
      medicationAutoCompleteResult = MedicationService.drugAutoComplete(shortQuery);
      assertTrue(medicationAutoCompleteResult.getQuery().equals(shortQuery));
      assertTrue(medicationAutoCompleteResult.getSuggestions().contains("Camoquin hydrochloride"));
      assertTrue(medicationAutoCompleteResult.getSuggestions().contains("Cisplatin"));
      assertTrue(medicationAutoCompleteResult.getSuggestions().contains("Clinolipid"));
    } catch (com.fasterxml.jackson.core.JsonParseException jsonParseException) {
      System.out.println("No internet access");
    }
  }


  /**
   * Test that a typical query string "reserp" gives the expected result, that is it returns a
   * MedicationAutoCompleteResult with query "reserp" and suggestions containing(but not limited to)
   * "Reserpine", "Reserpine, hydralazine hydrochloride and hydrochlorothiazide" and "Reserpine and
   * hydroflumethiazide".
   *
   * @throws IOException an input/output exception
   */
  @Test
  public void normalQueryTest() throws IOException {
    try {
      medicationAutoCompleteResult = MedicationService.drugAutoComplete(normalQuery);
      assertTrue(medicationAutoCompleteResult.getQuery().equals(normalQuery));
      assertTrue(medicationAutoCompleteResult.getSuggestions().contains("Reserpine"));
      assertTrue(medicationAutoCompleteResult.getSuggestions()
          .contains("Reserpine, hydralazine hydrochloride and hydrochlorothiazide"));
      assertTrue(medicationAutoCompleteResult.getSuggestions()
          .contains("Reserpine and hydroflumethiazide"));
    } catch (com.fasterxml.jackson.core.JsonParseException jsonParseException) {
      System.out.println("No internet access");
    }

  }
}
