package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class CacheElement {

  public LocalDateTime lastModified;
  public String key;
  public String value;

  /*
   * Returns the key (string) of the cache element.
   * @return key of the element
   */
  public String getKey() {
    return key;
  }

  /*
   * Returns the value (as a JSON object).
   * @return The stored value (a JSON Object).
   */
  public JSONObject retrieveValue() {
    // Was called 'getValue()', but when the JSON converter tries to convert the object to JSON in order to save
    // the file, it looks for all public getter methods. Hence, it was trying to convert value into JSON, which is
    // not what we want. It should just use getValueString() to get the value a string, which can be converted to
    // JSON and saved.
    JSONObject value = new JSONObject(this.value);
    return value;
  }

  /*
   * Returns the timestamp of the date and time when the cache element was created, or last modified.
   * @return Date and time the cache element was last modified.
   */
  public LocalDateTime getLastModified() {
    return lastModified;
  }


  /**
   * Calculates and returns number of minutes elapsed since the cache element was last modified.
   *
   * @return the age of the cache element in minutes.
   */
  public int calculateAge() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    return currentDateTime.getMinute() - lastModified.getMinute();
  }

  /**
   * Constructor for the Cache Element class. Takes two strings, a key and a value. Sets the key and
   * value of the cache element, and sets its lastModified attribute to the current time.
   *
   * @param key A key to uniquely identify the cache element.
   * @param valueObject The data to be stored.
   */
  public CacheElement(String key, JSONObject valueObject) {
    this.key = key;
    this.lastModified = LocalDateTime.now();
    this.value = valueObject.toString();
  }


  /**
   * Constructor for the Cache Element class. Takes two strings, a key and a value. Sets the key and
   * value of the cache element, and sets its lastModified attribute to the current time.
   *
   * @param key A key to uniquely identify the cache element.
   * @param value The data to be stored.
   */
  @JsonCreator
  public CacheElement(
      @JsonProperty("key") String key,
      @JsonProperty("value") String value,
      @JsonProperty("lastModified") LocalDateTime lastModified) {
    this.key = key;
    this.lastModified = lastModified;
    this.value = value;


  }
}
