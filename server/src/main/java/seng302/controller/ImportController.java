package seng302.controller;

import static seng302.model.importer.ServerMarshall.replaceOriginalDonorWithNewDonor;
import static seng302.model.person.DonorReceiver.formatStringToExtendedDateTime;
import static seng302.model.person.DonorReceiver.formatStringToMinimalDate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import seng302.model.database.DonorReceiverService;
import seng302.model.database.UserService;
import seng302.model.importer.DBSaving;
import seng302.model.importer.ImportSummary;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.UserAccountStatus;
import seng302.model.person.UserValidationReport;
import seng302.model.security.AdministratorCredentials;
import seng302.model.importer.ServerUserValidator;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.ClinicianCredentials;
import org.apache.commons.csv.*;
import seng302.model.utility.DateFormatter;

@APIController
public class ImportController extends BaseController {


  private DonorReceiverService donorReceiverService;

  private UserService userService;

  private String importString = "import";

  @Autowired
  public ImportController(AuthenticationTokenStore authenticationTokenStore) {
    donorReceiverService = new DonorReceiverService(authenticationTokenStore);
    userService = new UserService(authenticationTokenStore);
  }

  @AdministratorCredentials
  @PostMapping(value = "import/singleImport/donor", headers="Accept=application/json")
  @ResponseBody
  public ResponseEntity singleImportDonor(@RequestBody Map<String, Object> map, @RequestHeader(value = "x-auth-token") String token) {
    String modifyingAccount = "testAdmin"; // Handle tests which can't pass in the correct token.
    if(token != null) {
        modifyingAccount = userService.getUsername(token);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    fixDonorReceiverDateTimes(map);

    DonorReceiver donorReceiver;
    try {
      donorReceiver =  objectMapper.convertValue(map, DonorReceiver.class);
    }
    catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Invalid Donor/Receiver JSON", HttpStatus.BAD_REQUEST);
    }
    donorReceiver.populateIllnessLists();
    String username = donorReceiver.getUserName();

    UserValidationReport errorCreateReport = new UserValidationReport(username);
    errorCreateReport.setAccountStatus(UserAccountStatus.ERROR);
    errorCreateReport.addIssue("ERROR: Couldn't create donor in database.");

    UserValidationReport errorDeleteReport = new UserValidationReport(username);
    errorDeleteReport.setAccountStatus(UserAccountStatus.ERROR);
    errorDeleteReport.addIssue("ERROR: Failed in replacing donor from database");

    UserValidationReport errorGetReport = new UserValidationReport(username);
    errorGetReport.setAccountStatus(UserAccountStatus.ERROR);
    errorGetReport.addIssue("ERROR: Failed in getting existing donor from database");

    UserValidationReport errorCreateLogReport = new UserValidationReport(username);
    errorCreateLogReport.setAccountStatus(UserAccountStatus.ERROR);
    errorCreateLogReport.addIssue("ERROR: Couldn't create log of donor import in database.");

    ServerUserValidator userValidator = new ServerUserValidator(donorReceiver);
    UserValidationReport report = userValidator.getReport();
    UserAccountStatus accountStatus = report.getAccountStatus();

    ImportSummary importSummary = new ImportSummary();

    // Invalid donor JSON so we don't post to DB
    if(accountStatus == UserAccountStatus.INVALID || accountStatus == UserAccountStatus.POOR) { // Failed import
      importSummary.addRejectedImport(report);
    } else if (accountStatus == UserAccountStatus.EXISTS){ // Existing donor in DB, so we update the donor by merging, deleting existing donor in db then adding new donorr
      Connection connection = dbcConnection.getConnection();

      try {
        Map<String, DonorReceiver> novaMap = donorReceiverService.getDonorReceiverSingle(connection, username);
        if (novaMap.isEmpty()) { // Couldn't get donor which shouldn't happen since we already checked for the donor on the db here but is an edge case we should handle in case the donor got deleted while we were running this method
          importSummary.addRejectedImport(errorGetReport);
        }

        DonorReceiver original = novaMap.get(username);
        ResponseEntity response = userService.deleteUser(connection, username);

        if(response.getStatusCode() == HttpStatus.ACCEPTED) {
          DonorReceiver combinedDonor = replaceOriginalDonorWithNewDonor(original, donorReceiver); // Replace the db copy with our local donor/receiver
          DBSaving dbSaving = new DBSaving();
          dbSaving.exportDonorToDatabase(connection, combinedDonor);

          boolean logInserted = insertLog(username, importString,  modifyingAccount, username, "", "merged");
          if(logInserted) {
            importSummary.addSuccessfulImport(report);
          }
          else {
            importSummary.addRejectedImport(errorCreateLogReport);
          }

        }

      }
      catch(SQLException e) {
        importSummary.addRejectedImport(errorDeleteReport);
      }

    } else { // Either repaired or just a new donor. Either way, doesn't exist in the database.
      DBSaving dbSaving = new DBSaving();
      try {
        dbSaving.exportDonorToDatabase(dbcConnection.getConnection(), donorReceiver);
        boolean logInserted = insertLog(username, importString,  modifyingAccount, username, "", "import");
        if(logInserted) {
          importSummary.addSuccessfulImport(report);
        }
        else {
          importSummary.addRejectedImport(errorCreateReport);
        }

      }
      catch (SQLException e) {
        importSummary.addRejectedImport(errorCreateReport);
      }
    }
    return new ResponseEntity<>(importSummary, HttpStatus.ACCEPTED);
  }


  /**
   * Creates a clinician from a given JSON
   * @param clinician The clinician to be imported
   * @return The response object containing the status code result
   */
  @AdministratorCredentials
  @PostMapping(value = "import/singleImport/clinician", headers = "Accept=application/json")
  @ResponseBody
  public ResponseEntity singleImportClinician(@RequestBody Clinician clinician, @RequestHeader(value = "x-auth-token") String token) {
    try {
      String modifyingAccount = "";
      if(token != null) {
        modifyingAccount = userService.getUsername(token);
      }
      List importResults = userService.importClinician(dbcConnection.getConnection(), clinician, modifyingAccount);
      if ((int) importResults.get(0) == 400) {
        return new ResponseEntity<>((List) importResults.get(1), HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity(HttpStatus.valueOf((int) importResults.get(0)));
    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }


  /**
   * Creates an administrator form a given JSON
   * @param administrator The administrator to be imported
   * @return The response object containing the status code result
   */
  @AdministratorCredentials
  @PostMapping(value = "import/singleImport/admin", headers = "Accept=application/json")
  @ResponseBody
  public ResponseEntity singleImportAdmin(@RequestBody Administrator administrator, @RequestHeader(value = "x-auth-token") String token) {
    try {
      String modifyingAccount = "";
      if(token != null) {
        modifyingAccount = userService.getUsername(token);
      }
      List importResults = userService.importAdministrator(dbcConnection.getConnection(), administrator, modifyingAccount);
      if ((int) importResults.get(0) == 400) {
        return new ResponseEntity<>((List) importResults.get(1), HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity(HttpStatus.valueOf((int) importResults.get(0)));
    } catch (SQLException e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * API call for importing a donor though a csv file
   * @param file The file object itself
   * @return A response entity containing the code for whether the creation was successful.
   */
  @ClinicianCredentials
  @PostMapping(value = "import/csv", headers = "Accept=application/json")
  @ResponseBody
  public ResponseEntity importDonorCSV(@RequestBody File file, @RequestHeader(value = "x-auth-token") String token) {
    try {
      String modifyingAccount = "";
      if(token != null) {
        modifyingAccount = userService.getUsername(token);
      }

      try(FileInputStream fileData = new FileInputStream(file)) {
        CSVParser csvParser = new CSVParser(new InputStreamReader(fileData), CSVFormat.DEFAULT.withHeader());

        List resultsList = donorReceiverService.importDonor(dbcConnection.getConnection(), csvParser, modifyingAccount);
        Map<String, String> successList = (HashMap<String, String>) resultsList.get(0);
        Map<String, List> repairedList = (HashMap<String, List>) resultsList.get(1);
        Map<String, String> failList = (HashMap<String, String>) resultsList.get(2);

        if (successList.isEmpty() && failList.isEmpty() && repairedList.isEmpty()) {
          return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (successList.isEmpty() && repairedList.isEmpty()) {
          return new ResponseEntity<>(failList, HttpStatus.BAD_REQUEST);
        } else if (repairedList.isEmpty()) {
          return new ResponseEntity<>(resultsList, HttpStatus.OK);
        } else {
          return new ResponseEntity<>(resultsList, HttpStatus.ACCEPTED);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }

    } catch (SQLException e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  private LocalDateTime formatCreationDateFromList(List<Integer> creationDateList) {
    StringBuilder creationDateString = new StringBuilder();
    if(creationDateList == null || creationDateList.size() != 3) {
      return null;
    }
    creationDateString
    .append(creationDateList.get(0)).append("-")
    .append(creationDateList.get(1)).append("-")
    .append(creationDateList.get(2));

    return formatStringToExtendedDateTime(creationDateString.toString());
  }


  private LocalDate formatDateOfBirthFromList(List<Integer> dateOfBirthList) {
    StringBuilder dateOfBirthString = new StringBuilder();
    if(dateOfBirthList == null || dateOfBirthList.size() != 3) {
      return null;
    }
    dateOfBirthString
        .append(dateOfBirthList.get(0)).append("-")
        .append(dateOfBirthList.get(1)).append("-")
        .append(dateOfBirthList.get(2));

    return formatStringToMinimalDate(dateOfBirthString.toString());
  }


  private LocalDate formatDateOfDeathFromList(List<Integer> dateOfDeathList) {
    StringBuilder dateOfDeathString = new StringBuilder();
    if(dateOfDeathList == null || dateOfDeathList.size() != 3) {
      return null;
    }
    dateOfDeathString
        .append(dateOfDeathList.get(0)).append("-")
        .append(dateOfDeathList.get(1)).append("-")
        .append(dateOfDeathList.get(2));

    return formatStringToMinimalDate(dateOfDeathString.toString());
  }


  private boolean insertLog(String username, String valChanged, String modifyingAccount, String accountModified,
      String originalVal, String changedVal) {
    String logEntry = "INSERT INTO LogEntries " +
        "(`username`, `valChanged`, `modifyingAccount`, `accountModified`, `originalVal`, `changedVal`) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    Connection connection = dbcConnection.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(logEntry)) {
      preparedStatement.setString(1,username);
      preparedStatement.setString(2, valChanged);
      preparedStatement.setString(3, modifyingAccount);
      preparedStatement.setString(4, accountModified);
      preparedStatement.setString(5, originalVal);
      preparedStatement.setString(6, changedVal);

      return preparedStatement.executeUpdate() > 0;
    }
    catch (SQLException e) {
      return false;
    }
  }

  private void fixDonorReceiverDateTimes(Map<String, Object> map){
    List<Integer> creationDateList = (List<Integer>) map.get("creationDate");
    List<Integer> dateOfBirthList = (List<Integer>) map.get("dateOfBirth");
    Map<String, Object> dateOfDeathMap = (Map<String, Object> ) map.get("deathDetails");
    List<Integer> dateOfDeathList = (List<Integer>) dateOfDeathMap.get("doD");

    if (creationDateList != null) {
      LocalDateTime creationDate = formatCreationDateFromList(creationDateList);
      map.put("creationDate", creationDate);
    }
    if (dateOfBirthList != null) {
      LocalDate dateOfBirth = formatDateOfBirthFromList(dateOfBirthList);
      map.put("dateOfBirth", dateOfBirth);
    }
    if (dateOfDeathList != null) {
      LocalDate dateOfDeath = formatDateOfDeathFromList(dateOfDeathList);
      dateOfDeathMap.put("doD", dateOfDeath);
      map.put("deathDetails", dateOfDeathMap);
    }
  }

}
