package seng302.model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.util.FileUtils;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class DrugInteractionsCacheTest {

  private static final String CACHE_PATH = "di_cache" + File.separator;

  private DrugInteractionsCache interactionsCache;

  private String drugPair1;
  private String drugPair2;
  private String drugPair3;

  private JSONObject interact1;
  private JSONObject interact2;
  private JSONObject interact3;

  @Before
  public void before() {
    // Create a new drug interactions cache
    interactionsCache = new DrugInteractionsCache();

    System.out.println(interactionsCache.toString());

    // Create some JSON objects to use with the cache
    drugPair1 = "druga_drugb";
    drugPair2 = "drug1_drug2";
    drugPair3 = "drugname1_drugname2";
    interact1 = new JSONObject();
    interact2 = new JSONObject();
    interact3 = new JSONObject();

    interact1.append("age_interaction", "0-1");
    interact2.append("age_interaction", "10-19");
    interact3.append("age_interaction", "40-49");
  }


  @Test
  public void setCacheElementTest() {
    System.out.println("setCacheElementTest called");
    interactionsCache.setCacheElement(drugPair1, interact1);
    interactionsCache.setCacheElement(drugPair2, interact2);
    interactionsCache.setCacheElement(drugPair3, interact3);

    assertEquals(interactionsCache.size(), 3);
  }

  @Test
  public void getCacheElementTest() {
    CacheElement cacheElement;

    // Put some cache elements in the cache
    interactionsCache.setCacheElement("druga_drugb", interact1);
    interactionsCache.setCacheElement("drug1_drug2", interact2);
    interactionsCache.setCacheElement("drugname1_drugname2", interact3);

    cacheElement = interactionsCache.getCacheElement("drug1_drug2");

    // Check the expected JSON Object was returned
    assertEquals(cacheElement.retrieveValue().toString(), interact2.toString());
  }

  @Test
  public void getCacheElementWithNonExistantDrugPair() {
    CacheElement cacheElement;

    // Put some cache elements in the cache
    interactionsCache.setCacheElement("druga_drugb", interact1);
    interactionsCache.setCacheElement("drug1_drug2", interact2);
    interactionsCache.setCacheElement("drugname1_drugname2", interact3);

    cacheElement = interactionsCache.getCacheElement("hydrochlorothiazide_lisinopril");
    assertEquals(cacheElement, null);
  }


  @Test
  public void saveCacheElementTest() throws IOException {
    boolean found = false;

    String drugPair = "druga_drugb";
    // Create a new cache element
    CacheElement element = new CacheElement("druga_drugb", interact1);

    // Construct the filename
    String filename = drugPair + ".json";

    // Save the cache element to disk
    //interactionsCache.saveCacheElementToDisk(element);
    interactionsCache.setCacheElement(drugPair, interact1);
    // Check the file is where it should be
    File cacheDir = new File(CACHE_PATH);
    File[] files = cacheDir.listFiles();
    for (File file : files) {
      if (file.getName().equals(filename)) {
        found = true;
      }
    }
    assertTrue(found);
  }

  /**
   * Attempts to load cache files into the cache
   */
  @Test
  public void loadCacheTest() throws IOException {
    // Clear the cache
    interactionsCache.refreshCache();

    // Create three cache elements and save them to disk
    CacheElement element1 = new CacheElement(drugPair1, interact1);
    CacheElement element2 = new CacheElement(drugPair2, interact2);
    CacheElement element3 = new CacheElement(drugPair3, interact3);

    interactionsCache.saveCacheElementToDisk(element1);
    interactionsCache.saveCacheElementToDisk(element2);
    interactionsCache.saveCacheElementToDisk(element3);

    // Load the cache files
    interactionsCache.loadCacheFromDisk();

    // Check the cache in memory
    assertEquals(interactionsCache.size(), 3);
    assertEquals(interactionsCache.getCacheElement(drugPair2).retrieveValue().toString(),
        interact2.toString());

  }

  @AfterClass
  public static void tearDown() {
    File cacheDir = new File(CACHE_PATH);
    try {
      FileUtils.deleteDirectory(cacheDir);
    }
    catch (IOException e) {
      System.out.println("Couldn't delete cache directory made during tests");
    }
  }

}
