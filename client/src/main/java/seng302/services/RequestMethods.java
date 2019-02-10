package seng302.services;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

class RequestMethods {
  static HttpHeaders generateHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return headers;
  }

  static HttpHeaders generateHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("x-auth-token", token);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  static HttpHeaders generateHeaders(String token, Map<String, String> customHeaders) {
    boolean contentType = false;
    HttpHeaders headers = new HttpHeaders();
    headers.add("x-auth-token", token);
    for( Map.Entry<String, String> entry: customHeaders.entrySet()){
      if(entry.getKey().equals("Content-Type")) {
        contentType = true;
      }
      headers.add(entry.getKey(), entry.getValue());
    }
    if(!contentType) {
      headers.add("Content-Type", "application/json");
    }
    return headers;
  }

  public static String generateURL(String url, Map<String, String> queryParams) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    for(Map.Entry<String, String> query : queryParams.entrySet()) {
      builder.queryParam(query.getKey(), query.getValue());
    }
    return builder.toUriString();
  }

  public static ResponseEntity makeRequest(String authenticationToken, Object body, String url, HttpMethod method, Class classOfObject, Map<String, String> customHeaders) throws HttpClientErrorException {
    HttpHeaders headers;
    if ((customHeaders != null) && (authenticationToken != null)){
        headers = generateHeaders(authenticationToken, customHeaders);
    }
    else if(authenticationToken != null) {
        headers = generateHeaders(authenticationToken);
    }
    else {
        headers = generateHeaders();
    }



    HttpEntity httpEntity;
    if (body == null) {
      httpEntity = new HttpEntity(headers);
    }
    else {
      httpEntity = new HttpEntity(body, headers);
    }

    RestTemplate restTemplate = new RestTemplate();

    return restTemplate.exchange(url, method, httpEntity, classOfObject);
  }


    public static ResponseEntity makeRequest(String authenticationToken, Object body, String url, HttpMethod method, Class classOfObject) throws HttpClientErrorException {
        HttpHeaders headers;
        if(authenticationToken != null) {
            headers = generateHeaders(authenticationToken);
        }
        else {
            headers = generateHeaders();
        }
        HttpEntity httpEntity;
        if (body == null) {
            httpEntity = new HttpEntity(headers);
        }
        else {
            httpEntity = new HttpEntity(body, headers);
        }
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, method, httpEntity, classOfObject);
    }
}
