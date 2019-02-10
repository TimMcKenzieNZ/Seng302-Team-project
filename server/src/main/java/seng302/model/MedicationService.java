package seng302.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Provides methods for accessing autocomplete suggestions for complete/partial drug names.
 */
public class MedicationService {

  /**
   * API URL
   */
  private static final String autoCompleteURL = "http://mapi-us.iterar.co/api/autocomplete?";


  /**
   * Used to add parameters to API query
   */
  public static class ParameterStringBuilder {

    public static String getParamsString(Map<String, String> params)
        throws UnsupportedEncodingException {
      StringBuilder result = new StringBuilder();

      for (Map.Entry<String, String> entry : params.entrySet()) {
        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        result.append("&");
      }
      String resultString = result.toString();
      return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
  }


  /**
   * Returns the suggestions for given partial drug name
   *
   * @param searchString The partial drug name
   * @return medicationAutoCompleteResult The result of the query
   * @throws IOException Exception thrown
   */
  public static MedicationAutoCompleteResult drugAutoComplete(String searchString)
      throws IOException {
    String responseString;
    try {

      URL url = new URL(autoCompleteURL);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setReadTimeout(10000);
      con.setConnectTimeout(10000);

      Map<String, String> parameters = new HashMap<>();
      parameters.put("query", searchString);

      con.setDoOutput(true);

      DataOutputStream out = new DataOutputStream(con.getOutputStream());
      out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
      out.flush();
      out.close();

      int status = con.getResponseCode();
      if (status == 200) {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
        }
        in.close();

        responseString = content.toString();

      } else {
        // If the request fails an empty result is returned
        responseString = "{\"query\": \"" + searchString + "\",\"suggestions\": []}";

      }
    } catch (java.net.SocketTimeoutException e) {
      // If the request fails an empty result is returned
      responseString = "{\"query\": \"" + searchString + "\",\"suggestions\": []}";
    } catch (java.io.IOException e) {
      // If the request fails an empty result is returned
      responseString = "{\"query\": \"" + searchString + "\",\"suggestions\": []}";
    }
    ObjectMapper mapper = new ObjectMapper();
    //JSON from String to Object
    return mapper.readValue(responseString, MedicationAutoCompleteResult.class);
  }
}