package seng302.model;

import org.json.JSONObject;
import seng302.model.person.DonorReceiver;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;

public class DrugInteractions {

  private static final String APIlink = "https://www.ehealthme.com/api/v1/drug-interaction/";
  private static final String errorString = "Error";

  /**
   * Gets the drug interaction information from the eHealthMe api and parses it into a JSONObject.
   *
   * @param drug1 A drug to be tested for interactions.
   * @param drug2 A drug to be tested for interactions.
   * @return A JSONObject of the information returned by the api.
   * @throws IOException Thrown if there is a problem with communicating with the api.
   */
  public static JSONObject getDrugInteractions(String drug1, String drug2) throws IOException {
    String urlString =
        APIlink + drug1 + "/" + drug2 + "/";
    URL url = new URL(urlString);

    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(5000);

    int responseCode = connection.getResponseCode();

    if ((responseCode != 200) && (responseCode != 301)) {
      JSONObject obj = new JSONObject();
      obj.put("Error", "Error in processing the api request " + responseCode);
      return obj;
    } else {
      BufferedReader input = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));

      String inputLine;
      StringBuilder responseString = new StringBuilder();

      while ((inputLine = input.readLine()) != null) {
        responseString.append(inputLine);
      }

      input.close();
      JSONObject jobject = new JSONObject(responseString.toString());
      jobject.put(errorString, "None");

      return jobject;
    }
  }


  /**
   * Gets the symptoms that people of the donors age have experienced.
   *
   * @param dInteractions The drug interaction symptoms that have already been found (in this case
   * should be empty)
   * @param ageArray An array with all the symptoms experienced by people in different age
   * brackets.
   * @param donorReceiverAge The age of the donorReceiver the drug interactions are being
   * specialised for.
   * @return The drug interactions set with the symptoms found by age included.
   */
  public Set<Object> getAgeSymptoms(Set<Object> dInteractions, JSONObject ageArray,
      Integer donorReceiverAge) {

    String ageString = "nan";

    if (10 <= donorReceiverAge && donorReceiverAge <= 19) {
      ageString = "10-19";
    } else if (20 <= donorReceiverAge && donorReceiverAge <= 29) {
      ageString = "20-29";
    } else if (30 <= donorReceiverAge && donorReceiverAge <= 39) {
      ageString = "30-39";
    } else if (40 <= donorReceiverAge && donorReceiverAge <= 49) {
      ageString = "40-49";
    } else if (50 <= donorReceiverAge && donorReceiverAge <= 59) {
      ageString = "50-59";
    } else if (60 <= donorReceiverAge) {
      ageString = "60+";
    }

    for (Object symptoms : ageArray.getJSONArray(ageString)) {
      dInteractions.add(symptoms.toString());
    }

    return dInteractions;
  }


  /**
   * Gets the symptoms that people of the donors gender have experienced.
   *
   * @param dInteractions The drug interaction symptoms that have already been found
   * @param genderArray An array containing the symptoms that male and female people have
   * experienced.
   * @param gender The gender of the current donorReceiver
   * @return The dInteractions Set<Object> with the new symptoms added.
   */
  private Set<Object> getGenderSymptoms(Set<Object> dInteractions, JSONObject genderArray,
      Character gender) {
    List<String> male = Arrays.asList("M", "m", "O", "o");
    List<String> female = Arrays.asList("F", "f", "O", "o");
    String genderStr = gender == null ? "U" : Character.toString(gender);
    if (male.contains(genderStr)) {
      for (Object maleSymptoms : genderArray.getJSONArray("male")) {
        dInteractions.add(maleSymptoms.toString());
      }
    }

    if (female.contains(genderStr)) {
      for (Object femaleSymptoms : genderArray.getJSONArray("female")) {
        dInteractions.add(femaleSymptoms.toString());
      }
    }

    return dInteractions;
  }


  /**
   * Checks if the symptom is in the JSON array for any of the time frames given. If it is, the time
   * period is returned as a string, if not, the string "Unknown" is returned.
   *
   * @param timeArray An object of arrays containing the symptoms experienced at given times
   * @param symptom A symptom to find the typical time frame of
   * @return A string of the time frame a given symptom has been known to appear (unknown if not in
   * data)
   */
  private String symptomLength(JSONObject timeArray, Object symptom) {
    for (int i = 0; i < 10; i++) {
      if (timeArray.getJSONArray("1 - 2 years").get(i).equals(symptom)) {
        return "1 - 2 Years";
      } else if (timeArray.getJSONArray("1 - 6 months").get(i).equals(symptom)) {
        return "1 - 6 Months";
      } else if (timeArray.getJSONArray("10+ years").get(i).equals(symptom)) {
        return "10+ Years";
      } else if (timeArray.getJSONArray("2 - 5 years").get(i).equals(symptom)) {
        return "2 - 5 Years";
      } else if (timeArray.getJSONArray("5 - 10 years").get(i).equals(symptom)) {
        return "5 - 10 Years";
      } else if (timeArray.getJSONArray("6 - 12 months").get(i).equals(symptom)) {
        return "6 - 12 Months";
      } else if (timeArray.getJSONArray("< 1 month").get(i).equals(symptom)) {
        return "< 1 Month";
      } else if (timeArray.getJSONArray("not specified").get(i).equals(symptom)) {
        return "Not Specified";
      }
    }
    return "Unknown";
  }


  /**
   * Uses the information given by the api (see getDrugInteractions function) to give a customised
   * set of drug interactions for a specific account.
   *
   * @param donorReceiver The account for which the specification is to occur.
   * @param drug1 A drug to be checked for interactions.
   * @param drug2 A drug to be checked for interactions.
   * @return An ArrayList of ArrayLists containing a symptom and the time that the symptom is likely
   * to occur
   * @throws IOException Thrown if there is a problem with communicating with the api.
   */
  public List<List> getCustomisedDrugInteractions(DonorReceiver donorReceiver, String drug1,
      String drug2) throws IOException {
    List<List> interactions = new ArrayList<>();
    Set<Object> dInteractions = new HashSet<>();

    JSONObject drugInteractions = getDrugInteractions(drug1, drug2);

    if (drugInteractions.get(errorString) != "None") {
      return new ArrayList<>();
    }

    JSONObject ageArray = drugInteractions.getJSONObject("age_interaction");
    JSONObject genderArray = drugInteractions.getJSONObject("gender_interaction");
    JSONObject timeArray = drugInteractions.getJSONObject("duration_interaction");

    dInteractions = getAgeSymptoms(dInteractions, ageArray, donorReceiver.calculateAge());
    dInteractions = getGenderSymptoms(dInteractions, genderArray, donorReceiver.getGender());

    for (Object symptom : dInteractions) {
      List<String> symptomsAndTime = new ArrayList<>();
      symptomsAndTime.add(symptom.toString());
      symptomsAndTime.add(symptomLength(timeArray, symptom));
      interactions.add(symptomsAndTime);
    }

    return interactions;
  }

  /**
   * Uses the information given by the api (see getDrugInteractions function) to give a customised
   * set of drug interactions for a specific account.
   *
   * @param donorReceiver The account for which the specification is to occur.
   * @param drugInteractions the JSON response from DrugInteractions getDrugInteractions method
   * @return An ArrayList of strings containing a symptom and the time that the symptom is likely to
   * occur
   */
  public static ArrayList<String> getCustomisedDrugInteractions2(DonorReceiver donorReceiver,
      JSONObject drugInteractions) {
    ArrayList<String> interactions = new ArrayList<>();
    ArrayList<String> genderAgeInteractions = new ArrayList<>();
    ArrayList<String> tempDuration = new ArrayList<>();

    Set<String> genderInteractions = new HashSet<>();
    Set<String> ageInteractions = new HashSet<>();

    Integer donorAge = donorReceiver.calculateAge();

    //Checks the age of the donor account for the symptoms of people their age
    JSONObject ageArray = drugInteractions.getJSONObject("age_interaction");
    String ageString;

    if (10 <= donorAge && donorAge <= 19) {
      ageString = "10-19";
    } else if (20 <= donorAge && donorAge <= 29) {
      ageString = "20-29";
    } else if (30 <= donorAge && donorAge <= 39) {
      ageString = "30-39";
    } else if (40 <= donorAge && donorAge <= 49) {
      ageString = "40-49";
    } else if (50 <= donorAge && donorAge <= 59) {
      ageString = "50-59";
    } else if (60 <= donorAge) {
      ageString = "60+";
    } else {
      ageString = "nan";
    }

    for (Object symptoms : ageArray.getJSONArray(ageString)) {
      ageInteractions.add(symptoms.toString());
    }

    //Checks the gender of the donor account for the symptoms of people their gender
    JSONObject genderArray = drugInteractions.getJSONObject("gender_interaction");

    if (donorReceiver.getGender() == 'M' || donorReceiver.getGender() == 'm') {
      for (Object symptoms : genderArray.getJSONArray("male")) {
        genderInteractions.add(symptoms.toString());
      }
    } else if (donorReceiver.getGender() == 'F' || donorReceiver.getGender() == 'f') {
      for (Object symptoms : genderArray.getJSONArray("female")) {
        genderInteractions.add(symptoms.toString());
      }
    } else {
      for (Object symptoms : genderArray.getJSONArray("male")) {
        genderInteractions.add(symptoms.toString());
      }
      for (Object symptoms : genderArray.getJSONArray("female")) {
        genderInteractions.add(symptoms.toString());
      }
    }

    ageInteractions.retainAll(genderInteractions);
    genderAgeInteractions.addAll(ageInteractions);

    //Gives the duration that the symptoms are experienced and adds them to
    JSONObject timeArray = drugInteractions.getJSONObject("duration_interaction");

    for (String symptom : genderAgeInteractions) {
      tempDuration.clear();

      if ((timeArray.has("< 1 month")) && (timeArray.getJSONArray("< 1 month").toList()
          .contains(symptom))) {
        tempDuration.add("< 1 month");
      }
      if ((timeArray.has("1 - 6 months")) && (timeArray.getJSONArray("1 - 6 months").toList()
          .contains(symptom))) {
        tempDuration.add("1 - 6 months");
      }
      if ((timeArray.has("6 - 12 months")) && (timeArray.getJSONArray("6 - 12 months").toList()
          .contains(symptom))) {
        tempDuration.add("6 - 12 months");
      }
      if ((timeArray.has("1 - 2 years")) && (timeArray.getJSONArray("1 - 2 years").toList()
          .contains(symptom))) {
        tempDuration.add("1 - 2 years");
      }
      if ((timeArray.has("2 - 5 years")) && (timeArray.getJSONArray("2 - 5 years").toList()
          .contains(symptom))) {
        tempDuration.add("2 - 5 years");
      }
      if ((timeArray.has("5 - 10 years")) && (timeArray.getJSONArray("5 - 10 years").toList()
          .contains(symptom))) {
        tempDuration.add("5 - 10 years");
      }
      if ((timeArray.has("10+ years")) && (timeArray.getJSONArray("10+ years").toList()
          .contains(symptom))) {
        tempDuration.add("10+ Years");
      }
      if ((timeArray.has("not specified")) && (timeArray.getJSONArray("not specified").toList()
          .contains(symptom))) {
        tempDuration.add("Not Specified");
      }

      //If the time hasn't been given in the data then the time is set to unknown
      if (tempDuration.size() == 0) {
        tempDuration.add("Unknown");
      }
      String symptomDuration = symptom + " (" + tempDuration.get(0);
      if (tempDuration.size() > 1) {
        for (int i = 1; i < tempDuration.size(); i++) {
          symptomDuration += ", ";
          symptomDuration += tempDuration.get(i);
        }

      }
      symptomDuration += ")";
      interactions.add(symptomDuration);
      tempDuration.clear();
    }

    return interactions;

  }
}
