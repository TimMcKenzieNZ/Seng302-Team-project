package seng302.model;

import java.io.IOException;
import org.json.JSONObject;

import java.io.IOException;

public interface Cacheable {

  /*
   * Creates a cacheElement and stores it in the cache.
   * @param key Key to uniquely identify the cache element in the cache.
   * @JSONObject value The data to be stored.
   */
  void setCacheElement(String key, JSONObject value);

  /*
   * Removes the specified cache element from the cache.
   * @param key The key of the element ot be removed.
   */
  void removeCacheElement(String key);

  /*
   * Returns the specified cache element.
   * @param key The key of the desired cache element.
   * @return The cache element.
   */
  CacheElement getCacheElement(String key);

  /*
   * Returns the number of cache elements in the cache.
   * @return The number of elements in the cache.
   */
  int size();

  /*
   * Saves the passed cached element to disk.
   * @param cacheElement The cache element to be saved to disk.
   * @return True if save succeeded, false if failed.
   * @throws IOException if an error occurred saving the file.
   */
  boolean saveCacheElementToDisk(CacheElement cacheElement) throws IOException;

  /*
   * Removes the specified cache element from disk.
   * @param key The key of the cache element to be deleted from disk.
   * @return True if file is now not on disk, false if it is still on disk.
   * @throws IOException if an error occurred deleting the file.
   */
  boolean removeCacheElementFromDisk(String drugPair);

  /*
   * Saves the entire cache to disk.
   * @return True if save was successful, false if otherwise.
   * @throws IOException if an error occurred saving files.
   */
  boolean saveCacheToDisk() throws IOException;

  /*
   * Loads the cache files into memory from disk (Directory is specified as a constant in the Cache class).
   * @return True if load successful (this includes if the cache directory contains no files), false if otherwise.
   * @throws IOException if an error occurs locating or creating the directory, or loading files.
   */
  boolean loadCacheFromDisk() throws IOException;

  /*
   * Constructs a filename for a cache element. Filenames are based on the key of the cache element.
   * @param key The key of the cache element.
   * @return The filename the cache element would/will have.
   */
  String constructFilename(String key);

}
