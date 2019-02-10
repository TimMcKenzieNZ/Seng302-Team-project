package seng302.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.json.JSONObject;

public class DrugInteractionsCache implements Cacheable {

  private static final String directory = "di_cache" + File.separator;
  private static final String jsonFileEnding = ".json";

  ObjectMapper mapper = createMapper();

  private LinkedHashMap<String, CacheElement> dataLibrary;


  /**
   * Constructor. Creates a new dataLibrary and populates it from disk (if any files exist)
   */
  public DrugInteractionsCache() {
    boolean success = false;
    this.dataLibrary = new LinkedHashMap<String, CacheElement>();
    try {
      success = loadCacheFromDisk();
    } catch (IOException exception) {
      System.err.println(exception);
      System.out.println("ERROR: Could not load cache from disk. Please check the directory path.");
    }
    if (!success) {
      System.out.println("ERROR: Problem loading cache from disk. Exception not thrown.");
    }
  }


  /**
   * Creates a cache element with the supplied data and stores it in the cache.
   *
   * @param drugPair A pair of drug names in all lower case, organised alphabetically and separated
   * with an underscore (eg: druga_drugb).
   * @param interactionData A string containing information about the interactions of the two drugs
   * specified in the drugPair variable.
   */
  public void setCacheElement(String drugPair, JSONObject interactionData) {
    CacheElement cacheElement = new CacheElement(drugPair, interactionData);
    dataLibrary.put(drugPair, cacheElement);

    try {
      saveCacheElementToDisk(cacheElement);
    } catch (IOException exception) {
      System.err.println(exception);

    }
  }

  /**
   * Removes the specified cache item from the cache, if it exists.
   *
   * @param drugPair A pair of drug names in all lower case, organised alphabetically and separated
   * with an underscore (eg: druga_drugb).
   */
  public void removeCacheElement(String drugPair) {
    if (dataLibrary.containsKey(drugPair)) {
      dataLibrary.remove(drugPair);

      removeCacheElementFromDisk(drugPair);
    }
  }

  /**
   * Returns the cache element associated with the specified drug pair if it is in the cache. If it
   * does not exist in the cache, Null is returned.
   *
   * @param drugPair A pair of drug names in all lower case, organised alphabetically and separated
   * with an underscore (eg: druga_drugb).
   * @return The cache element stored at the supplied key (drugPair).
   */
  public CacheElement getCacheElement(String drugPair) {
    return dataLibrary.get(drugPair);
  }

  /*
   * Returns true if the supplied pair of drug names exists in the cache.
   * @return true if exists, false if otherwise.
   */
  public boolean cacheElementExists(String drugPair) {
    if (dataLibrary.containsKey(drugPair)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the number of cache elements stored.
   *
   * @return number of cache elements.
   */
  public int size() {
    return dataLibrary.size();
  }

  /*
   * Saves the supplied cache element to the cache directory (which should have been specified when the cache
   * was initialised).
   * @param cacheElement The cache element to be saved to disk.
   * @return True if save completed successfully, false if otherwise.
   */
  public boolean saveCacheElementToDisk(CacheElement cacheElement) throws IOException {
    boolean success = false;
    if (dataLibrary == null) {
      return false;
    }
    Marshal.createDirectory(directory);
    String filepath = constructFilename(cacheElement.getKey());

    try {
      File file = new File(filepath);
      mapper.writeValue(file, cacheElement);
      success = true;

    } catch (IOException exception) {
      System.err.println(exception);
      //System.out.println("ERROR: Cannot write to file " + filepath);
    }
    return success;
  }

  /*
   * Deletes a specified cache element from the cache directory (which should have been specified when the cache
   * was initialised).
   * @param drugPair The key of the cache element to be deleted
   * @return True if the file was successfully deleted or did not exist. False if file is still on disk.
   */
  public boolean removeCacheElementFromDisk(String drugPair) {
    boolean success = false;
    String filename = constructFilename(drugPair);
    File file = new File(filename);
    success = file.delete();
    if (!success) {
      // If delete did not succeed, the file may not exist OR the file may be open. If the file does not exist,
      // it does not matter. But if the file is in use, the file will still exist.
      // Retrieve list of files in the specified directory and check if the file is there.
      File cacheDir = new File(directory);
      File[] cacheFiles = cacheDir.listFiles();
      for (File cacheFile : cacheFiles) {
        if (cacheFile.getName().equals(filename)) {
          success = false; // to indicate deletion failed and file still exists
        } else {
          success = true; // although deletion failed, file does not exist
        }
      }
    }
    return success;
  }

  /*
   * Saves the contents of the cache to the specified directory.
   * @param directoryPath Path of the directory in which to save the cache
   * @return True if save was completed successfully, false if otherwise
   * @throws IOException occurs when a cache element cannot be written to a file
   */
  public boolean saveCacheToDisk() throws IOException {
    boolean saved = false;
    boolean success = false;

    if (dataLibrary == null) {
      return false;
    }

    Marshal.createDirectory("directoryPath");

    // For each cache element in the cache, write to an independent file in the specified directory
    for (CacheElement cacheElement : dataLibrary.values()) {
      saved = saveCacheElementToDisk(cacheElement);
      if (saved) {
        success = true;
      } else {
        success = false;
        return success;
      }
    }
    return success;
  }

  /*
   * Loads all the files located within the specified directory into the cache
   * @param directoryPath Path of the directory from which to load files
   * @return True files were loaded successfully, false if otherwise
   * @throws IOException Occurs when a file or files cannot be loaded
   */
  public boolean loadCacheFromDisk() throws IOException {
    boolean success = false;

    refreshCache();

    try {
      // Create directory if none exists
      Marshal.createDirectory(directory);
      // Retrieve list of files in the specified directory
      File cacheDir = new File(directory);
      File[] cacheFiles = cacheDir.listFiles();

      // For each file, import the data to a cache element object and put it in the cache
      for (File file : cacheFiles) {
        CacheElement cacheElement = mapper.readValue(file, CacheElement.class);
        dataLibrary.put(cacheElement.getKey(), cacheElement);
      }
      success = true;
    } catch (IOException exception) {
      System.err.println(exception);
      System.out.println("ERROR: Could not import files. Please check the directory");
    } catch (NullPointerException npe) {
      System.err.println(npe);
      Marshal.createDirectory(directory);

    }

    return success;
  }

  public String constructFilename(String drugPair) {
    return directory + drugPair + jsonFileEnding;
  }

  /**
   * Clears the dataLibrary completely
   */
  public void refreshCache() {
    ArrayList<String> keys = new ArrayList<>();
    keys.addAll(dataLibrary.keySet());
    for (String key : keys) {
      removeCacheElement(key);
    }
  }

  /**
   * Creates an ObjectMapper for converting between JSON and Java objects specific to this cache,
   *
   * @return The new ObjectMapper instance.
   */
  public static ObjectMapper createMapper() {
    // This method is now public so that the DrugInteractionsCache class can use it

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    return mapper;

  }

}
