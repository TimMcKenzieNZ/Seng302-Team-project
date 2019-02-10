package seng302.model.import_export_strategies;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import seng302.model.person.User;

public interface UserImport {

  /**
   * Import the current Users
   *
   * @param userType The type of user to be imported
   * @param locationString The location of the directory to be imported
   * @param directory The directories path to be imported
   * @return Collection of Users
   * @throws IOException File was missing, or there was a problem reading the header/skipping the
   * first record
   * @throws IllegalArgumentException File formatting was wrong (not a .csv or not a .JSON etc) or
   * the reader/format was null
   */
  abstract Collection<User> importer(String userType, String locationString, File directory)
      throws IOException, IllegalArgumentException;


  /**
   * Creates an ObjectMapper for converting between JSON and Java objects,
   * @return The new ObjectMapper instance.
   */
  static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    return mapper;
  }
}
