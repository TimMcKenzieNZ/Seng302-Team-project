package seng302.model.import_export_strategies;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import seng302.model.Marshal;
import seng302.model.person.*;


/**
 * An implementation of the 'Import' strategy specifically to import Donors from a .csv file. it
 * does this by creating a CSV parser which reads an inputstream from the given directory and
 * attempts to create a donor CSV record from it. A set of users is generated from this record and
 * returned.
 */
public class CSVImport implements UserImport {

  /**
   * A CSV parser to read Donor User objects from a .csv file.
   */
  private CSVParser parser;

  /**
   * The date format used to create localDate objects from date strings in the .csv files.
   */
  private final DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

  /**
   * The date format used to create localDate objects from date strings in the .csv files.
   */
  private final DateTimeFormatter DATE_FORMAT2 = DateTimeFormatter.ofPattern("M/dd/yyyy");


  /**
   * The expected header that the .csv file should have. It details the Donor attributes.
   */
  private static final String[] DONOR_FILE_HEADER =
      {"nhi", "first_names", "last_names", "date_of_birth", "date_of_death", "birth_gender",
          "gender", "blood_type",
          "height", "weight", "street_number", "street_name", "neighborhood", "city", "region",
          "zip_code", "country",
          "birth_country", "home_number", "mobile_number", "email"};


  /**
   * Implements the .csv file import strategy by creating a parser fro the given directory and build
   * a CSV record from that directory's file stream. The parser is then closed and a collection of
   * users are returned from the CSV record.
   *
   * @param userType A string of the type of user to import. Currently unused.
   * @param locationString A string of the file location to import from. Currently unused.
   * @param directory The .csv file to import Users from.
   * @return Returns a collection of User objects of type Donor.
   * @throws FileNotFoundException Could not find the requested file
   * @throws IllegalArgumentException File formatting was wrong (not a .csv) or the reader/format
   * was null or The .csv file header does not contain the header variables we were expecting.
   */
  @Override
  public Collection<User> importer(String userType, String locationString, File directory)
          throws IOException {

    //New import, clear old exceptions
    Marshal.importException.clear();
    Collection<Exception> foundErrors = new ArrayList<>();
    String fileName = directory.getName();

    try (FileInputStream fileData = new FileInputStream(directory)) {
      parser = new CSVParser(new InputStreamReader(fileData), CSVFormat.DEFAULT.withHeader());
    } catch (IOException e) {
      foundErrors.add(new FileNotFoundException(fileName + ",INVALID,Fatal Error: File not found"));
    }

    if (parser != null) {
      for (int i = 0; i < DONOR_FILE_HEADER.length - 1; i++) {
        if (!parser.getHeaderMap().keySet().contains(DONOR_FILE_HEADER[i])) {
          foundErrors.add(new IllegalArgumentException(
              fileName + ",INVALID,Missing header: " + DONOR_FILE_HEADER[i]));
        }
      }
    }

    Collection<User> importedDonors = new ArrayList<>();

    if (foundErrors.isEmpty()) {
      importedDonors = importDonors(fileName);
    }

    for (Exception e : foundErrors) {
      e.printStackTrace();
      Marshal.importException.add(e.getMessage());
    }

    if (parser != null) {
      parser.close();
    }

    return importedDonors;
  }


  /**
   * Parses the given string for the words 'male' or 'female' and returns a char 'M' or 'F'
   * respectively. Otherwise returns 'U' for unknown/unspecified.
   *
   * @return returns a char representing gender, either 'M', 'F', or 0 for missing gender.
   */
  private char getGenderFromString(String string) {
    if (string.toLowerCase().equals("male")) {
      return 'M';
    } else if (string.toLowerCase().equals("female")) {
      return 'F';
    } else {
      return 0;
    }
  }


  /**
   * Parses the given date string into a LocalDate object. If the format of the date is incorrect,
   * it returns null instead.
   *
   * @param dateString a string of a date that should be of the form "MM/dd/yyyy"
   * @return a LocalDate object based on the given string or null
   */
  private LocalDate parseStringToDate(String dateString) {
    LocalDate date;
    try {
      date = LocalDate.parse(dateString, DATE_FORMAT1);
    } catch (DateTimeParseException e) {
      try {
        date = LocalDate.parse(dateString, DATE_FORMAT2);
      } catch (DateTimeParseException e2) {
        return null;
      }
    }
    return date;
  }


  /**
   * Reads and returns a set of donors from a .csv file.
   *
   * @return A set of donors.
   */
  private Collection<User> importDonors(String fileName) {
    System.out.println("Importing " + fileName);
    Collection<User> donors = new HashSet<>();
    try {
      System.out.println(parser.getRecords());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    for (CSVRecord record : parser) {
      try {
        String nhi = record.get("nhi");
        String first = record.get("first_names");
        String last = record.get("last_names");
        LocalDate DOB = parseStringToDate(record.get("date_of_birth"));
        LocalDate tempDod = parseStringToDate(record.get("date_of_death"));
        char gender = getGenderFromString(record.get("gender"));
        char birthGender = getGenderFromString(record.get("birth_gender"));
        String bloodType = record.get("blood_type");
        double height;
        try {
          height = Double.parseDouble(record.get("height").trim());
        } catch (NumberFormatException e) {
          height = 0.0;
        }
        double weight;
        try {
          weight = Double.parseDouble(record.get("weight").trim());
        } catch (NumberFormatException e) {
          weight = 0.0;
        }
        String streetAddress = record.get("street_number") + " " + record.get("street_name");
        String suburb = record.get("neighborhood");
        String city = record.get("city");
        String region = record.get("region");
        String postCode = record.get("zip_code");
        String country = record.get("country");
        String mobileNumber = record.get("mobile_number");
        String homeNumber = record.get("home_number");
        String email = record.get("email");
        int version = Integer.parseInt(record.get("version"));
        Address address = new Address(streetAddress, null, suburb, city, region, postCode, country);
        ContactDetails details = new ContactDetails(address, mobileNumber, homeNumber, email);
        ArrayList<LogEntry> logs = new ArrayList<>();
        ContactDetails emergencyDetails = new ContactDetails(null, null, null, null);
        User donor = new DonorReceiver(first, null, last, DOB, nhi, details, gender,
            birthGender, bloodType, height, weight);
        DeathDetails deathDetails;
        if (tempDod != null) {
          LocalDateTime DOD = LocalDateTime.of(tempDod.getYear(), tempDod.getMonthValue(), tempDod.getDayOfMonth(), 0, 0);
          deathDetails = new DeathDetails("Unknown", "Unknown", "Unknown", DOD);
        } else {
          deathDetails = new DeathDetails();
        }
        ((DonorReceiver) donor).setDeathDetails(deathDetails);
        donor.setPassword("password");
        donor.setMiddleName("");
        donors.add(donor);
      } catch (IllegalArgumentException e) {
        Marshal.importException.add(fileName + ",INVALID," + e.getMessage());
      }

    }
    return donors;
  }


}
