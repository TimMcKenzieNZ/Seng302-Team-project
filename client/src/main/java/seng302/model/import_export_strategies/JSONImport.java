package seng302.model.import_export_strategies;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import seng302.model.Marshal;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;


public class JSONImport implements UserImport {

  @Override
  public Collection<User> importer(String userType, String locationString, File directory)
      throws IOException, IllegalArgumentException {
    //New import, clear old exceptions
    Marshal.importException.clear();
    // Retrieve list of files in the given directory type.
    File userFile = directory;
    String fileName = userFile.getName();
    // For each .json file, import the data into the App.
    ObjectMapper mapper = UserImport.createMapper();
    Collection<User> users = new HashSet<>();
    // Handle case where no files have been exported before.
    if (directory != null) {
      try {
        if (userType.equalsIgnoreCase("donor/receiver")) {
          DonorReceiver importedDonor = mapper.readValue(userFile, DonorReceiver.class);
          users.add(importedDonor);
        } else if (userType.equalsIgnoreCase("clinician")) {
          Clinician importedClinician = mapper.readValue(userFile, Clinician.class);
          users.add(importedClinician);
        } else {
          Administrator importedAdmin = mapper.readValue(userFile, Administrator.class);
          users.add(importedAdmin);
        }
      } catch (IOException e) {
        e.printStackTrace();
        Marshal.importException.add(fileName
            + ",INVALID,Malformed file format"); // this will be caught and handled in Marshall
      }
    }
    return users;
  }
}
