package seng302.model.import_export_strategies;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import seng302.model.Marshal;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;

public class JSONFolderImport implements UserImport {

  @Override
  public Collection<User> importer(String userType, String locationString, File directory) {
    //New import, clear old exceptions
    Marshal.importException.clear();
    // Retrieve list of files in the given directory type.
    File[] userFiles = directory.listFiles();

    Collection<Exception> foundErrors = new ArrayList<>();

    // For each .json file, import the data into the App.
    ObjectMapper mapper = UserImport.createMapper();
    Collection<User> users = new HashSet<>();

    // Handle case where no files have been exported before.
    if (userFiles != null) {

      for (int index = 0; index < userFiles.length; index++) {
        String fileName = "";
        try {
          File userFile = userFiles[index];
          fileName = userFile.getName();
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
        } catch (MismatchedInputException exception) {
          if (exception instanceof InvalidFormatException) {
            foundErrors.add(new Exception(
                fileName + ",FAILED,Not correctly formatted to create " + userType + "\n\t"
                    + exception.getCause().getMessage()));
          } else {
            foundErrors
                .add(new Exception(fileName + ",FAILED,Could not be mapped to " + userType + "\n"));
          }

        } catch (IOException e) {
          Marshal.importException.add(fileName + ",FAILED,FATAL ERROR: File could not be imported");
        }
      }
    }

    for (Exception e : foundErrors) {
      Marshal.importException.add(e.getMessage());
    }

    return users;
  }
}
