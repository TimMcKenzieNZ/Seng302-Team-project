package seng302.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ActiveIngredientsService {

  private String drugNamePatternValid = "^[A-Za-z0-9-_]+$";
  private String drugNamePatternWSpaces = "^[A-Za-z0-9]+[\\s]+[A-Za-z0-9]+[\\s]*[A-Za-z0-9]*$";


  /**
   * Takes a drug name and attempts to get the active ingredient(s) of the drug from the MAPI API.
   * If the API returns the active ingredients list, returns an array list of strings, each string
   * being a variation of the drug. If the drug cannot be found, the API returns an empty list. The
   * function also returns an empty list. If the API cannot be does not respond, or returns a status
   * other than 200, the function throws an IOException.
   *
   * @param drug the name of the drug
   * @return array list of variations (as strings), or an empty list
   * @throws IOException if the API does not respond
   */
  public List<String> ActiveIngredients(String drug) throws IOException {
    String validName;
    String urlString = "http://mapi-us.iterar.co/api/" + drug + "/substances.json";
    List<String> variations;
    variations = new ArrayList<>();

    // First, check the drug name is 'valid' (contains only alphanumeric, hypen and underscore characters)
    // if drug name is invalid, return an appropriate error message
    validName = validateDrugName(drug);
    if (validName == null) {
      variations.add("invalid drug name");
      return variations;
    }

    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    int status = conn.getResponseCode();

    if (status == 200) {
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuffer content = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      ObjectMapper objectMapper = new ObjectMapper();
      variations = objectMapper.readValue(
          content.toString(),
          objectMapper.getTypeFactory().constructCollectionType(
              ArrayList.class, String.class));
    } else {
      variations = new ArrayList<>();
      variations.add("invalid drug name");
    }
    return variations;
  }


  /**
   * Checks the validity of a drug name - one that either has a space in it (currently only works
   * for a single space
   *
   * @param drugName The name of the drug to be checked
   * @return Either a valid drug name (may be the drug name passed to it) or null if the drug is
   * invalid
   */
  private String validateDrugName(String drugName) {
    boolean valid = Pattern.matches(drugNamePatternValid, drugName);
    String validName = drugName;
    if (Pattern.matches(drugNamePatternWSpaces, drugName) && !valid) {
      for (char letter : drugName.toCharArray()) {
        if (letter == ' ') {
          letter = '_';
        }
        validName += letter;
      }
      System.out.print(validName);
    } else if (!valid) {
      validName = null;
    }
    return validName;
  }
}
