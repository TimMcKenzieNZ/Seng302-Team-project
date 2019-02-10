package seng302.controllers;

import static seng302.model.Marshal.session;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.web.client.HttpClientErrorException;
import seng302.App;
import seng302.model.ImportSummary;
import seng302.model.PageNav;
import seng302.model.enums.ADDRESSES;
import seng302.model.import_export_strategies.CSVImport;
import seng302.model.import_export_strategies.JSONFolderImport;
import seng302.model.import_export_strategies.JSONImport;
import seng302.model.import_export_strategies.UserImport;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;
import seng302.model.person.UserValidationReport;
import seng302.services.PostTask;
import seng302.services.SyncGetTask;

/**
 * The controller for the Importing Users functionality
 *
 * The purpose of this class is to import a list of donors into the App database.
 * For each user in the list that already exist in the database, it first checks if the user
 * wants to overwrite that existing user in the database or not.
 * Default clinician and administrators will not be overwritten.
 * Returns the number of user profile overwrites performed by the import.
 */
public class ImportUsersController {

  @FXML
  private ComboBox<String> fileType;

  @FXML
  private RadioButton singleFile;

  @FXML
  private RadioButton directory;


  @FXML
  private ComboBox<String> userType;

  @FXML
  public Button cancelButton;
  @FXML
  public Button importButton;

  private List<User> users;

  private int numUsers;

  private int currentUser = 0;

  private final long WARNING_SIZE = 40000;

  private ImportSummary importSummary = new ImportSummary();

  private AtomicBoolean overwriteAllDuplicates = new AtomicBoolean(false);
  private AtomicBoolean ignoreAllDuplicates = new AtomicBoolean(false);

  /**
   * List of failed imports for user files (admin or clinician) by username.
   */
  private List failedImports;

  /**
   * ObservableList containing all types of the user that can be imported
   */
  private ObservableList<String> USER_TYPES = FXCollections
      .observableArrayList("Donor/Receiver", "Clinician",
          "Administrator");

  /**
   * ObservableList containing all types of file that can be imported
   */
  private ObservableList<String> FILE_TYPES = FXCollections.observableArrayList("JSON", "CSV");

  private ToggleGroup group;


  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  public void initialize() {
    importButton.getStyleClass().add("primaryButton");
    cancelButton.getStyleClass().add("redButton");
    setupComboBoxOptions();
    setupRadioButtons();
    setupComboBoxListener();
    failedImports = new ArrayList();
  }


  /**
   * Populates the comboboxes
   */
  private void setupComboBoxOptions() {

    userType.getItems().addAll(USER_TYPES);
    userType.getSelectionModel().selectFirst();
    fileType.getItems().addAll(FILE_TYPES);
    fileType.getSelectionModel().selectFirst();

  }


  /**
   * Adds the radio buttons to a toggle group and selects default option
   */
  private void setupRadioButtons() {

    group = new ToggleGroup();

    singleFile.setToggleGroup(group);
    singleFile.setSelected(true);
    directory.setToggleGroup(group);

  }


  /**
   * Adds a listener to fileType combobox which modifies the radiobuttons in response the selected
   * filetype.
   */
  private void setupComboBoxListener() {
    fileType.getSelectionModel().selectedItemProperty()
        .addListener((options, oldValue, newValue) -> {
              if (newValue.equalsIgnoreCase("CSV")) {
                singleFile.setSelected(true);
                directory.setDisable(true);
                userType.getSelectionModel().select("Donor/Receiver");
                userType.setDisable(true);
              } else {
                directory.setDisable(false);
                userType.setDisable(false);
              }
            }
        );
  }


  /**
   * Called when import users button is pressed
   */
  public void importUsers() {
    UserImport importStrategy = getImportStrategy();
    String fileTypeString = fileType.getSelectionModel().getSelectedItem();
    File donorFiles;
    if (singleFile.isSelected()) {
      donorFiles = fileSelect();
    } else {
      donorFiles = directorySelect();
      System.out.println(donorFiles.list());
    }

    if(donorFiles == null) {
      return;
    }

    String userTypeString = userType.getSelectionModel().getSelectedItem();

    if (fileTypeString.equalsIgnoreCase("csv")) {
      if(donorFiles.length() > WARNING_SIZE) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "");
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        alert.getDialogPane().setGraphic(graphic);
        alert.setTitle("Warning");
        alert.setHeaderText("Large file uploaded");
        alert.setContentText("You're uploading a file above 40KB which might take a long"
            + "time to upload to the database. The users will be slowly imported in the "
            + "background.");
        alert.showAndWait();

        if(alert.getResult() == ButtonType.OK) {
          importCSV(donorFiles);
        }
      }
      else {
        importCSV(donorFiles);
      }
      return;
    }

    try {
      Collection<User> userCollection = importStrategy.importer(userTypeString, donorFiles.getPath(),
          donorFiles);
      users = new ArrayList<>(userCollection);
      numUsers = users.size();
      importUsers(userTypeString);
    }
    catch (IOException e) {
      showBadFile();
    }

  }

  /**
   * Returns the import strategy selected
   */
  public UserImport getImportStrategy() {
    String fileTypeString = fileType.getSelectionModel().getSelectedItem();
    if (fileTypeString.equalsIgnoreCase("csv")) {
      return new CSVImport();
    } else if (fileTypeString.equalsIgnoreCase("json")) {
      if (singleFile.isSelected()) {
        return new JSONImport();
      } else {
        return new JSONFolderImport();
      }
    }
    return null;
  }


  /**
   * Generates a title for the directory/file chooser window
   *
   * @return title generated
   */
  private String generateTitle() {
    String title = "";
    String fileTypeString = fileType.getSelectionModel().getSelectedItem();
    String userTypeString = userType.getSelectionModel().getSelectedItem();
    if (fileTypeString.equalsIgnoreCase("csv")) {
      title = "Select CSV containing " + userTypeString + " data.";
    } else if (fileTypeString.equalsIgnoreCase("json")) {
      if (singleFile.isSelected()) {
        title = "Select " + userTypeString + " User.json file.";
      } else {
        title = "Select directory containing " + userTypeString + " User.json files.";
      }
    }
    return title;
  }


  /**
   * Called when a directory of files is being imported. Opens a directory chooser.
   */
  private File directorySelect() {
    try {
      //Create a new instance of a file chooser for the admin to pick a directory to import user files
      DirectoryChooser chooser = new DirectoryChooser();
      //Generate the file chooser title based on options selected by admin
      chooser.setTitle(generateTitle());
      File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI()).getParent().toString());
      chooser.setInitialDirectory(defaultDirectory);
      File directory = chooser.showDialog(App.getWindow());
      return directory;
    } catch (URISyntaxException e) {
      System.err.println("Error selecting file");
      return null;
    }
  }

  /**
   * Called when a single file is being imported. Opens a file chooser.
   */
  private File fileSelect() {
    try {
      //Create a new instance of a file chooser for the admin to pick a directory to import user files
      FileChooser fileChooser = new FileChooser();
      //Generate the file chooser title based on options selected by admin
      fileChooser.setTitle(generateTitle());
      File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI()).getParent().toString());
      fileChooser.setInitialDirectory(defaultDirectory);
      return fileChooser.showOpenDialog(App.getWindow());
    } catch (URISyntaxException e) {
      return null;
    }
  }


  /**
   * A factory method to create a customizable alert dialog box that has a check box. This code was
   * sourced from a stack overflow post from user 'ctg' made on 30/4/2016. See:
   * https://stackoverflow.com/questions/36949595/how-do-i-create-a-javafx-alert-with-a-check-box-for-do-not-ask-again
   * The comments in the code are from the code's author. the code itself is entirely unmodified.
   *
   * @param type the type of Alert the alert will be.
   * @param title A String of the alert's title.
   * @param headerText A string of what the alert's header tex twill be.
   * @param message A string of the alert content message.
   * @param optOutMessage A string which will be the alert's check box method.
   * @param optOutAction A Consumer variable that is used to return the result of the user's
   * interaction with the alert text box.
   * @param buttonTypes The button types the alert will have
   * @return An Alert
   */
  public static Alert createAlertWithOptOut(Alert.AlertType type, String title, String headerText,
      String message, String optOutMessage, Consumer<Boolean> optOutAction,
      ButtonType... buttonTypes) {
    Alert alert = new Alert(type);
    // Need to force the alert to layout in order to grab the graphic,
    // as we are replacing the dialog pane with a custom pane
    alert.getDialogPane().applyCss();
    Node graphic = alert.getDialogPane().getGraphic();
    // Create a new dialog pane that has a checkbox instead of the hide/show details button
    // Use the supplied callback for the action of the checkbox
    alert.setDialogPane(new DialogPane() {
      @Override
      protected Node createDetailsButton() {
        CheckBox optOut = new CheckBox();
        optOut.setText(optOutMessage);
        optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
        return optOut;
      }
    });
    alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
    alert.getDialogPane().setContentText(message);
    // Fool the dialog into thinking there is some expandable content
    // a Group won't take up any space if it has no children
    alert.getDialogPane().setExpandableContent(new Group());
    alert.getDialogPane().setExpanded(true);
    // Reset the dialog graphic using the default style
    alert.getDialogPane().setGraphic(graphic);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    return alert;
  }

  private void showImportReport() throws IOException{
    int numberOfDuplicates = 0;

    FXMLLoader loader = new FXMLLoader();
    AnchorPane reportPane = loader.load(getClass().getResourceAsStream(PageNav.IMPORTREPORT));

    // Create new scene.
    Scene reportScene = new Scene(reportPane);

    // Create new stage.
    Stage reportStage = new Stage();
    reportStage.setTitle("Report");
    reportStage.setScene(reportScene);
    reportStage.show();

    // Place stage in center of main window.
    reportStage.setX(
        App.getWindow().getX() + ((App.getWindow().getWidth() - reportStage.getWidth()) / 2.0));
    reportStage.setY(
        App.getWindow().getY() + ((App.getWindow().getHeight() - reportStage.getHeight()) / 2.0));

    ImportUsersReportController importUsersReportController = loader.getController();
    importUsersReportController.setDuplicates(numberOfDuplicates);
    importUsersReportController.setImportSummary(importSummary);

    PageNav.loadNewPage(PageNav.ADMINMENU);
    resetController();

  }

  private void resetController() {
    currentUser = 0;
    importSummary = new ImportSummary();
    overwriteAllDuplicates.set(false);
    ignoreAllDuplicates.set(false);
    failedImports.clear();
  }

  /**
   * A method to show an alert dialog and return an int indicating whether we are overwriting the
   * user or not and whether to save this setting in the future.
   * @param user The DonorReceiver that the admin is importing that already exists in the system
   * @return int An int representing the response of the user
   * 0 = User didn't click "remember" and didn't overwrite user
   * 1 = User didn't click "remember" and overwrote the user
   * 2 = User did click "remember" and didn't overwrite the user
   * 3 = User did click "remember" and overwrote the user
   */
  private int importDuplicateUser(User user) {
    HashMap<Integer, String> choice = new HashMap<>(); // A HashMap to store the result of whether the user choose to overwrite all files automatically
    Alert alert = createAlertWithOptOut(Alert.AlertType.CONFIRMATION, "Overwrite file", null,
        "Do you wish to proceed?", "Do this for all files",
        param -> choice.put(0, param ? "Always" : "Never"), ButtonType.YES, ButtonType.NO);
    String fullName = user.getFirstName() + " " + user.getLastName();
    alert.setHeaderText("You are going to overwrite existing " + "user" + " "
        + user.getUserName() + " ( " + fullName + " ).");

    if(alert.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
      // The user chose to remember and decided to overwrite the user
      if ((choice.get(0) != null) && (choice.get(0).equals("Always"))) {
        return 3;
      }
      // The user chose to not remember and overwrote the user
      else {
        return 1;
      }
    }
    else {
      // The user chose to remember and didn't overwrite the user
      if (choice.get(0) != null && choice.get(0).equals("Always")) {
        return 2;
      }
      // The user chose to not remember and didn't overwrite the user
      else {
        return 0;
      }
    }
  }

  private void importUsers(String userType) {
    switch(userType) {
      case "Donor/Receiver":
        importDonorReceivers();
        break;

      case "Clinician":
          importClinicianOrOverwriteExistingClinician();
        break;

      case "Administrator":
          importAdminOrOverwriteExistingAdmin();
        break;
    }
    resetController();

  }

  private void addToImportSummary(ImportSummary tempSummary) {
    List<UserValidationReport> successfulImports = tempSummary.getSuccessfulImports();
    List<UserValidationReport> rejectedImports = tempSummary.getRejectedImports();

    for (UserValidationReport report : successfulImports) {
      importSummary.addSuccessfulImport(report);
    }

    for (UserValidationReport report : rejectedImports) {
      importSummary.addRejectedImport(report);
    }
  }

  // Change this method to take a collection and an int for currentPosition.
  private void sendDonorImportRequest() {
    User donor = users.get(currentUser);
    PostTask task = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.POST_IMPORT_DONOR_SINGLE.getAddress(), donor, session.getToken());
    task.setOnSucceeded(event -> {
      String result = (String) task.getValue();
      try {
        ObjectMapper mapper = UserImport.createMapper();
        ImportSummary newSummary = mapper.readValue(result, ImportSummary.class);
        addToImportSummary(newSummary);
        System.out.println(newSummary.toString());
      }
      catch (IOException e) {
        e.printStackTrace();        System.out.println("Didn't receiver importSummary from server");
      }
      currentUser++;
      if(currentUser == numUsers) { // All users imported
        try {
          showImportReport();
        }
        catch (IOException e) {
          System.err.println("Error with displaying ImportSummary");
        }
      }
      else {
        importDonorReceivers(); // Call to import a new donor in case there are more donors to import
      }
    });
    task.setOnFailed(event -> {
      System.out.println(task.getValue());
      System.out.println("Something went wrong while trying to import the donor");
    });
    new Thread(task).start();
    System.out.println("==========");
    System.out.println(donor.getFirstName());
  }


  /**
   * Generates a pop-up that displays statistics of the last user import attempt. It lists the number of import attempts,
   * the number of success, and lists the users who failed to be imported.
   */
  public void generateUserImportPopUp() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Import Summary Dialog");
    alert.setHeaderText("Results of user import");
    alert.setContentText(String.format("%s file import attempts.%n%s successful imports.%nFollowing users failed to be imported:%n%s.",
            numUsers, numUsers - failedImports.size(), failedImports.toString()));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }


  /**
   * imports a single clinician from the selected file. If it is a duplicate of another clinician on the server, that
   * clinician will be overwritten by the one in the file. This function recursively calls itself until all clinicians
   * in the file are imported.
   */
  private void importClinicianOrOverwriteExistingClinician() {
      if (currentUser < numUsers) { // we have not yet imported all the users from the file
          User clinician = users.get(currentUser);
          if (clinician.getMiddleName() == null) { // we repair the file if its missing the middle name
              clinician.setMiddleName("");
          }
          if (!clinician.getUserName().equals("0")) {
              PostTask task = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.POST_CLINICIAN_OVERWRITE.getAddress(), clinician, session.getToken());
              task.setOnSucceeded(event -> {
                  // posted/overwritten the clinician
                  currentUser++;
                  importClinicianOrOverwriteExistingClinician(); // we recursively call the function until all users are imported
              });
              task.setOnFailed(event -> {
                  failedImports.add(clinician.getUserName());
                  currentUser++;
                  importClinicianOrOverwriteExistingClinician();
              });
              new Thread(task).start();
          } else {
              // cannot overwrite default clinician
              currentUser++;
              failedImports.add("0");
              importClinicianOrOverwriteExistingClinician();
          }
      } else { // we have imported all the users so we stop the recursion
          generateUserImportPopUp();
          resetController();
      }
  }





  /**
   * imports a single admin from the selected file. If it is a duplicate of another admin on the server, that
   * admin will be overwritten by the one in the file. This function recursively calls itself until all admins
   * in the file are imported.
   */
  private void importAdminOrOverwriteExistingAdmin() {
      if (currentUser < numUsers) { // we have not yet imported all the users from the file
          User admin = users.get(currentUser);
          if (admin.getMiddleName() == null) { // we repair the file if its missing the middle name
              admin.setMiddleName("");
          }
          if (!admin.getUserName().equals("Sudo")) {
              PostTask task = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.POST_ADMIN_OVERWRITE.getAddress(), admin, session.getToken());
              task.setOnSucceeded(event -> {
                  // posted/overwritten the admin
                  currentUser++;
                  importAdminOrOverwriteExistingAdmin(); // we recursively call the function until all users are imported
              });
              task.setOnFailed(event -> {
                  failedImports.add(admin.getUserName());
                  currentUser++;
                  importAdminOrOverwriteExistingAdmin();
              });
              new Thread(task).start();
          } else {
              // cannot overwrite default admin
              currentUser++;
              failedImports.add("Sudo");
              importAdminOrOverwriteExistingAdmin();
          }
      } else { // we have imported all the users so we stop the recursion
          generateUserImportPopUp();
          resetController();
      }
  }


  private void importDonorReceivers() {
    try{
      User donor = users.get(currentUser);
      String username = donor.getUserName();
      boolean duplicate = checkUserExists(username);

      if (duplicate) {
        if (!overwriteAllDuplicates.get() && !ignoreAllDuplicates.get()) {
          int result = importDuplicateUser(donor);
          switch(result) {
            case 0:
              break;
            case 1:
              sendDonorImportRequest();
              break;
            case 2:
              ignoreAllDuplicates.set(true);
              break;
            case 3:
              overwriteAllDuplicates.set(true);
              sendDonorImportRequest();
              break;
            default:
              break;
          }
        }
        else if(overwriteAllDuplicates.get()) {
          sendDonorImportRequest();
          // Overwrite
        }
        // ignoreAllDuplicates must be true here so we just ignore and continue
      }
      else {
        sendDonorImportRequest();
      }
    } catch (Exception e) {
      generateUserImportPopUp();
      resetController();
    }

  }


  /**
   * Since this method is async, we can't actually verify whether the donor doesn't exist.
   * That is, if `boolean exists = checkUserExists(username)`, either `exists` is true and the donor
   * does exist or we don't know. Thus, we have to handle the case of the user existing and not handle
   * the case of the user not existing but run that execution step if we don't receiver false form this method.
   * @param username The username of the donor to GET
   * @return A boolean indicating whether the donor exists (true = exists, otherwise = doesn't exist).
   */
  public boolean checkUserExists(String username){
    String endpoint = ADDRESSES.GET_DONOR.getAddress() + username;
    SyncGetTask task = new SyncGetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), endpoint, session.getToken());

    try {
      task.makeRequest();
      return true;
    }
    catch (HttpClientErrorException e) {
      return false;
    }
  }

  private void importCSV(File file) {
    PageNav.loading();
    if (file.getName().contains(".csv")) {
      PostTask task = new PostTask(List.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.POST_IMPORT_CSV.getAddress(), file, session.getToken());
      task.setOnSucceeded(event -> {
        List<Map<String, Object>> csvResponse = (List<Map<String, Object>>) task.getValue();
        Map<String, Object> validResponses = csvResponse.get(0);
        Map<String, Object> repairedResponses = csvResponse.get(1);
        Map<String, Object> invalidResponses = csvResponse.get(2);
        for(Map.Entry<String, Object> entry: validResponses.entrySet()) {
          String username = entry.getKey();
          String reason = (String) entry.getValue();
          UserValidationReport userValidationReport = new UserValidationReport(username);
          userValidationReport.addIssue(reason);
          importSummary.addSuccessfulImport(userValidationReport);
        }

        for(Map.Entry<String, Object> entry : repairedResponses.entrySet()) {
          String username = entry.getKey();
          ArrayList<String> reasonList = (ArrayList<String>) entry.getValue();
          UserValidationReport userValidationReport = new UserValidationReport(username);
          for(String reason: reasonList) {
            userValidationReport.addIssue(reason);
          }
          importSummary.addSuccessfulImport(userValidationReport);
        }

        for(Map.Entry<String, Object> entry : invalidResponses.entrySet()) {
          String username = entry.getKey();
          String reason = (String) entry.getValue();
          UserValidationReport userValidationReport = new UserValidationReport(username);
          userValidationReport.addIssue(reason);
          importSummary.addRejectedImport(userValidationReport);
        }
        PageNav.loaded();
        try {
          showImportReport();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      });
      task.setOnFailed(event -> {
        PageNav.loaded();
      });
      new Thread(task).start();
    } else {
      PageNav.loaded();
      showBadFile();
    }

  }

  private void showBadFile() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Import failed");
    alert.setContentText("Whoops, looks you tried to import the wrong file type. ");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  @FXML
  public void cancelButtonPressed() {
    PageNav.loadNewPage(PageNav.ADMINMENU);
  }
}



