package seng302.model;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheElementTest {

  JSONObject interact1;
  String key;
  String value;

  @Before
  public void before() {
    // Create a JSON object to use with cache elements
    interact1 = new JSONObject();
    // Put some data in the JSON Object
    key = "zyrtec";
    value = "This is not real data about the hayfever drug, Zyrtec";
    interact1.put(key, value);
  }

  @Test
  public void createCacheElementTest() {
    CacheElement cacheElement = new CacheElement(key, interact1);
    assertEquals(cacheElement.retrieveValue().toString(), interact1.toString());
  }
}
