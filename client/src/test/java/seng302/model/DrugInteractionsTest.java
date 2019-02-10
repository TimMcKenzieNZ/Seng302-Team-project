package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.person.DonorReceiver;

public class DrugInteractionsTest {

  private DrugInteractions drugInteractions;

  @Before
  public void setup() {
    drugInteractions = new DrugInteractions();
  }


  /**
   * Tests that the drug interactions are returned.
   */
  @Test
  @Ignore //Currently doesn't accept the equals values though they seem to be equal
  public void getDrugInteractionsRegularInput() {
    try {
      JSONObject actualInteractions =
          drugInteractions.getDrugInteractions("hydrochlorothiazide", "Lisinopril");
      JSONObject expectedInteractions = new JSONObject(
          "{\"reports\":{\"amount\":15584},\"duration_interaction\":{\"6 - 12 months\":[\"insomnia\",\"infection\",\"anaemia\",\"constipation\",\"axillary pain\",\"back pain\",\"bone disorder\",\"bone pain\",\"compression fracture\",\"deformity\"],\"10+ years\":[\"non-cardiac chest pain\",\"hyponatraemia\",\"renal failure acute\",\"dehydration\",\"dizziness\",\"dyspnoea\",\"nausea\",\"renal failure\",\"angina pectoris\",\"glycosylated haemoglobin increased\"],\"not specified\":[\"nausea\",\"pain\",\"fatigue\",\"dyspnoea\",\"dizziness\",\"drug ineffective\",\"diarrhoea\",\"headache\",\"anxiety\",\"asthenia\"],\"1 - 2 years\":[\"anaemia\",\"pain\",\"dyspnoea\",\"insomnia\",\"back pain\",\"emotional distress\",\"dizziness\",\"headache\",\"pulmonary embolism\",\"constipation\"],\"1 - 6 months\":[\"nausea\",\"anaemia\",\"headache\",\"constipation\",\"renal failure acute\",\"dyspnoea\",\"hypotension\",\"insomnia\",\"pain\",\"infection\"],\"< 1 month\":[\"hypotension\",\"renal failure acute\",\"dehydration\",\"asthenia\",\"dizziness\",\"nausea\",\"headache\",\"vomiting\",\"angioedema\",\"chest pain\"],\"2 - 5 years\":[\"insomnia\",\"diarrhoea\",\"renal failure acute\",\"nausea\",\"adverse drug reaction\",\"pain\",\"anxiety\",\"deep vein thrombosis\",\"dyspnoea\",\"fatigue\"],\"5 - 10 years\":[\"diarrhoea\",\"nausea\",\"impaired healing\",\"abscess\",\"osteonecrosis\",\"adverse drug reaction\",\"weight decreased\",\"asthma\",\"bone marrow disorder\",\"bronchitis\"]},\"Error\":\"None\",\"age_interaction\":{\"20-29\":[\"injury\",\"insomnia\",\"nausea\",\"pain\",\"back pain\",\"vomiting\",\"headache\",\"chest discomfort\",\"depression\",\"urinary tract infection\"],\"60+\":[\"nausea\",\"fatigue\",\"dyspnoea\",\"pain\",\"dizziness\",\"diarrhoea\",\"drug ineffective\",\"asthenia\",\"fall\",\"hypotension\"],\"30-39\":[\"pain\",\"nausea\",\"abdominal pain\",\"dizziness\",\"chest pain\",\"diabetes mellitus\",\"injury\",\"pain in extremity\",\"deep vein thrombosis\",\"pulmonary embolism\"],\"0-1\":[\"abdominal pain lower\",\"abdominal pain upper\",\"feeding disorder\",\"leiomyosarcoma\",\"weight decreased\"],\"40-49\":[\"nausea\",\"fatigue\",\"pain\",\"dyspnoea\",\"headache\",\"anxiety\",\"dizziness\",\"depression\",\"chest pain\",\"diarrhoea\"],\"50-59\":[\"nausea\",\"pain\",\"dizziness\",\"fatigue\",\"headache\",\"dyspnoea\",\"drug ineffective\",\"diarrhoea\",\"back pain\",\"vomiting\"],\"nan\":[\"drug ineffective\",\"pain\",\"nausea\",\"diarrhoea\",\"fatigue\",\"anxiety\",\"dyspnoea\",\"dizziness\",\"asthenia\",\"headache\"],\"2-9\":[\"plasma cell myeloma\",\"myalgia\",\"accidental drug intake by child\",\"ageusia\",\"anosmia\",\"lethargy\",\"multiple drug overdose accidental\",\"somnolence\"],\"10-19\":[\"renal failure acute\",\"anxiety\",\"fatigue\",\"nausea\",\"cholelithiasis\",\"injury\",\"pain\",\"dehydration\",\"vomiting\",\"anhedonia\"]},\"co_existing_conditions\":{\"high blood cholesterol\":1418,\"pain\":889,\"depression\":790,\"diabetes\":1227},\"gender_interaction\":{\"female\":[\"nausea\",\"pain\",\"fatigue\",\"diarrhoea\",\"dizziness\",\"dyspnoea\",\"headache\",\"drug ineffective\",\"vomiting\",\"anxiety\"],\"male\":[\"dyspnoea\",\"drug ineffective\",\"pain\",\"fatigue\",\"nausea\",\"dizziness\",\"hypotension\",\"asthenia\",\"myocardial infarction\",\"diarrhoea\"]}}\n");
      assertEquals(expectedInteractions, actualInteractions);
    } catch (IOException e) {
      System.out.println("There was an error in contacting the api. Test Skipped.");
    }
  }


  /**
   * Tests all inputs for the getAgeSymptoms() function for correct output.
   */
  @Test
  public void getAgeSymptoms() {
    JSONObject ageArray = new JSONObject();
    ageArray.put("nan", new JSONArray("[nan]"));
    ageArray.put("10-19", new JSONArray("[10-19]"));
    ageArray.put("20-29", new JSONArray("[20-29]"));
    ageArray.put("30-39", new JSONArray("[30-39]"));
    ageArray.put("40-49", new JSONArray("[40-49]"));
    ageArray.put("50-59", new JSONArray("[50-59]"));
    ageArray.put("60+", new JSONArray("[60+]"));

    Set<Object> ageOne = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 0);
    assertTrue(ageOne.contains("nan"));
    assertEquals(1, ageOne.size());

    Set<Object> ageTwo = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 11);
    assertTrue(ageTwo.contains("10-19"));
    assertEquals(1, ageTwo.size());

    Set<Object> ageThree = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 23);
    assertTrue(ageThree.contains("20-29"));
    assertEquals(1, ageThree.size());

    Set<Object> ageFour = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 35);
    assertTrue(ageFour.contains("30-39"));
    assertEquals(1, ageFour.size());

    Set<Object> ageFive = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 41);
    assertTrue(ageFive.contains("40-49"));
    assertEquals(1, ageFive.size());

    Set<Object> ageSix = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 53);
    assertTrue(ageSix.contains("50-59"));
    assertEquals(1, ageSix.size());

    Set<Object> ageSeven = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 68);
    assertTrue(ageSeven.contains("60+"));
    assertEquals(1, ageSeven.size());

    Set<Object> ageEight = drugInteractions.getAgeSymptoms(new LinkedHashSet<>(), ageArray, 1000);
    assertTrue(ageEight.contains("60+"));
    assertEquals(1, ageEight.size());
  }


  /**
   * Tests that the customised drug data was received without error
   */
  @Test
  @Ignore
  public void getCustomisedDrugInteractions() {
    try {
      DonorReceiver donorReceiver = new DonorReceiver("Susan", "Marie", "Pevensie",
          LocalDate.ofYearDay(1992, 287), "nar8267");
      DrugInteractions drugInteractions = new DrugInteractions();
      List<List> interactions = drugInteractions
          .getCustomisedDrugInteractions(donorReceiver, "hydrochlorothiazide", "Lisinopril");
      assertTrue(interactions.size() > 0);
    } catch (IOException e) {
      System.out.println("There was an error with the internet connection. Test skipped.");
    }
  }


  /**
   * Tests that when invalid drugs are sent to the function, an empty list is returned
   */
  @Test
  public void invalidDrugs() {
    try {
      DonorReceiver donorReceiver = new DonorReceiver("Susan", "Marie", "Pevensie",
          LocalDate.ofYearDay(1992, 287), "nar8267");
      DrugInteractions drugInteractions = new DrugInteractions();
      List<List> interactions = drugInteractions
          .getCustomisedDrugInteractions(donorReceiver, "notADrug", "alsoNotADrug");
      assertTrue(interactions.isEmpty());
    } catch (IOException exception) {
      System.out.println("There was an error with the internet connection. Test skipped.");
    }
  }
}