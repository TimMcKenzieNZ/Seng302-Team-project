package seng302.controllers;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.springframework.http.HttpStatus;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.model.AccountManager;
import seng302.model.CommandStack;
import seng302.model.PageNav;
import seng302.model.UndoableManager;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.Clinician;
import seng302.model.person.ClinicianPatchDetails;
import seng302.model.person.LogEntry;
import seng302.model.person.UserValidator;
import seng302.services.DeleteTask;
import seng302.services.GetTask;
import seng302.services.PatchTask;
import seng302.services.PostTask;

import static seng302.controllers.EditPaneController.MAX_BYTES;

/**
 * The controller class for all viewing or editing functionality for the current logged in clinician
 * in the App.w
 */
public class ClinicianProfileController implements CreateEditController {

  /**
   * The current clinician being viewed/edited.
   */
  public static Clinician clinician;

  private static UndoableManager undoableManager = App.getUndoableManager();
  private static Boolean undoRedoComboBox = false;
  private static Boolean undoRedoTextField = false;
  private static Boolean undoRedoTextArea = false;
  private static Boolean undoRedoLabel = false;



  /**
   * Max width/height of a profile image
   */
  private static final int MAX_DIMENSION = 10000;

  /**
   *  Max byte size of a profile image (5 megabytes)
   */
  public final static int MAX_BYTES = 3000000;

  @FXML
  private TextField clinicianProfileNameText;
  @FXML
  private TextField lastNameText;
  @FXML
  private TextArea workAddressText;
  @FXML
  private Button doneButton;
  @FXML
  private Label staffIDLabel;
  @FXML
  private Label dateLabel;
  @FXML
  private ComboBox<String> regionComboBox;
  @FXML
  private ListView view;
  @FXML
  private Label firstNameLabel;
  @FXML
  private Label lastNameLabel;
  @FXML
  private Label workAddressLabel;
  @FXML
  private Label regionLabel;
  @FXML
  private Button editButton;
  @FXML
  private Button cancelButton;
  @FXML
  private Button backButton;
  @FXML
  private Label clinicianNameLabel;
  @FXML
  private Button setClinicianPhoto;
  @FXML
  private Button deleteClinicianPhoto;
  @FXML private Label firstNameError;
  @FXML private Label lastNameError;
  @FXML private Label addressError;
  @FXML private Label regionError;
  @FXML private Tab profileHistoryTab;
  @FXML
  private ImageView clinicianProfilePhoto;

  private ObservableList<TextField> fields = FXCollections.observableArrayList();

  private ObservableList<Label> labels = FXCollections.observableArrayList();

  /**
   * An DonorReceiver Manager object created on application start up.
   */
  private AccountManager db = App.getDatabase();

  /**
   * A string array of all the administrative regions in New Zealand.
   */
  private static final String[] REGIONS = {"Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
      "Hawke's Bay",
      "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Marlborough", "West Coast",
      "Canterbury", "Otago", "Southland"};

  private static final String ERROR = "ERROR";

  /**
   * A string of the Clinician's region.
   */
  private String region = null;

  public static void setClinician(Clinician newClinician) {
    clinician = newClinician;
  }

  public static Clinician getClinician() {
    return clinician;
  }

  private static boolean isChild = false;
  private boolean instanceIsChild = false;


  public void initialize() {
    PageNav.loading();
    updateClinician();
    PageNav.loaded();
    // show all labels
    loadViewLabels();
    disableEditFields();
    hideErrorMessages();
    loadErrorMessages();
    setUpProfileHistory();
    setUpUndoRedo();
    getPhoto(clinician.getUserName());
    clinicianNameLabel.setText(clinician.getFirstName() + " " + clinician.getLastName());
    setClinicianPhoto.getStyleClass().add("greenButton");
    deleteClinicianPhoto.getStyleClass().add("deletePhotoButton");
    doneButton.getStyleClass().add("primaryButton");
    cancelButton.getStyleClass().add("redButton");
    backButton.getStyleClass().add("backButton");
    if (clinician.getUserName().equals(Clinician.defaultValue)) {
      editButton.setDisable(true);
      editButton.setVisible(false);
    }
    if (isChild) {
      configureWindowAsChild();
    } else {
      backButton.setVisible(true);
      backButton.setDisable(false);
    }
  }

  private void loadErrorMessages() {
    String alsoAllowed = "Spaces, commas, apostrophes, and dashes allowed.";
    firstNameError.setText("Must be between 1 and 50 alphabetical characters. " + alsoAllowed);
    lastNameError.setText("Must be between 1 and 50 alphabetical characters. " + alsoAllowed);
    addressError.setText("Must be between 0 and 100 alphanumeric characters. " + alsoAllowed);
    regionError.setText("Must have at most 100 alphabetical characters. " + alsoAllowed);
  }

  /**
   * Changes the pane to reflect its status as a child window.
   */
  private void configureWindowAsChild() {

    instanceIsChild = isChild;
    isChild = false;

    // Disable and hide close button.
    backButton.setDisable(true);
    backButton.setVisible(false);
  }

  /**
   * Takes a boolean value should be true if this pane is supposed to appear in a child window.
   * Otherwise, the boolean value should be false.
   *
   * @param childStatus True if pane appears in a child window, false otherwise.
   */
  public static void setIsChild(boolean childStatus) {

    isChild = childStatus;

  }

  public void loadViewLabels() {
    firstNameLabel.setVisible(true);
    lastNameLabel.setVisible(true);
    regionLabel.setVisible(true);
    workAddressLabel.setVisible(true);
    profileHistoryTab.setDisable(false);
    if (clinician != null) {
      //Set the text boxes to the values of the clinician information
      firstNameLabel.setText(clinician.getFirstName());
      lastNameLabel.setText(clinician.getLastName());
      staffIDLabel.setText(String.valueOf(clinician.getUserName()));
      regionLabel.setText(clinician.getContactDetails().getAddress().getRegion());
      workAddressLabel.setText(clinician.getContactDetails().getAddress().getStreetAddressLineOne());
      dateLabel.setText(formatCreationDate(clinician.getCreationDate()));
    } else {
      //If the clinician value is null, empty the information
      firstNameLabel.setText("");
      lastNameLabel.setText("");
      staffIDLabel.setText(String.valueOf(""));
      regionLabel.setText("");
      workAddressLabel.setText("");
      dateLabel.setText("");
    }
    editButton.setVisible(true);
  }

  public void disableEditFields() {
    clinicianProfileNameText.setVisible(false);
    lastNameText.setVisible(false);
    workAddressText.setVisible(false);
    regionComboBox.setVisible(false);
    doneButton.setVisible(false);
    cancelButton.setVisible(false);
    profileHistoryTab.setDisable(false);
    backButton.setDisable(false);
  }

  public void setUpProfileHistory() {
    ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
    observableLogs.addAll(clinician.getModifications());
    view.setCellFactory(param -> new ListCell<LogEntry>() {
      @Override
      protected void updateItem(LogEntry log, boolean empty) {
        super.updateItem(log, empty);
        if (empty || log == null) {
          setText(null);
        } else {
          setText(log.toString());
        }
      }
    });
    view.setItems(observableLogs);
  }

  /**
   * Initializes all the attributes seen by the user when they load the View/Edit Clinician GUI
   * page. This includes the current clinician's details as well as a log of any modifications they
   * have made to their profile.
   */
  public void setUpUndoRedo() {
    regionComboBox.getItems().addAll(REGIONS);
    fields.addAll(clinicianProfileNameText, lastNameText);
    labels.addAll(staffIDLabel, firstNameLabel, lastNameLabel, workAddressLabel, regionLabel);
    // undo and redo for text fields
    for (TextField textField : fields) {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!undoRedoTextField) {
          undoableManager.createTextFieldChange(textField, oldValue, newValue);
        }
        undoRedoTextField = false;
      });
      textField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }
    workAddressText.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoTextArea) {
        undoableManager.createTextAreaUndoable(workAddressText, oldValue, newValue);
      }
      undoRedoTextArea = false;
    });
    workAddressText.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.Z && event.isControlDown()) {
        undoEvent();
      } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
        redoEvent();
      }
    });
    // undo and redo for labels
    for (Label label : labels) {
      label.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!undoRedoLabel) {
          undoableManager.createLabelUndoable(label, oldValue, newValue);
        }
        undoRedoLabel = false;
      });
      label.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }

    //Undo and redo for region combo box
    regionComboBox.getSelectionModel().selectedIndexProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!undoRedoComboBox) {
            undoableManager.createComboBoxChange(regionComboBox, oldValue, newValue);
          }
          undoRedoComboBox = false;
        });
  }


  /**
   * Formats the creation date of an donorReceiver into a readable value
   *
   * @param time The LocalDatetime to be formatted
   * @return a string with creation date
   */
  public String formatCreationDate(LocalDateTime time) {
    return time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
  }


  /**
   * Function setting the values of the text fields in the clinician details tab
   */
  private void loadEditFields() {
    backButton.setDisable(true);
    clinicianProfileNameText.setVisible(true);
    lastNameText.setVisible(true);
    regionComboBox.setVisible(true);
    workAddressText.setVisible(true);
    //Set the text boxes to the values of the clinician information
    clinicianProfileNameText.setText(clinician.getFirstName());
    lastNameText.setText(clinician.getLastName());
    regionComboBox.getSelectionModel()
        .select(clinician.getContactDetails().getAddress().getRegion());
    workAddressText.setText(clinician.getContactDetails().getAddress().getStreetAddressLineOne());
    doneButton.setVisible(true);
    cancelButton.setVisible(true);
  }

  private void disableViewFields() {
    firstNameLabel.setVisible(false);
    lastNameLabel.setVisible(false);
    regionLabel.setVisible(false);
    workAddressLabel.setVisible(false);
    editButton.setVisible(false);
    profileHistoryTab.setDisable(true);
  }

  @FXML
  private void editButtonPressed() {
    loadEditFields();
    disableViewFields();
  }

  @FXML
  private void cancelButtonPressed() {
    loadViewLabels();
    disableEditFields();
    hideErrorMessages();
  }

  /**
   * Undoes the last undoable on the command stack
   */
  @FXML
  public void undoEvent() {
    undoCalled();
  }

  /**
   * Redoes the last undoable on the command stack
   */
  @FXML
  public void redoEvent() {
    redoCalled();

  }


  @FXML
  /*
   * Navigates the user back to the main menu GUI page.
   */
  void backButtonPressed() {
    undoableManager.getCommandStack().save();
    PageNav.loadNewPage(PageNav.MAINMENU);
  }


  @FXML
  /*
   * Sets the region attribute given by the combo box option selected by the user in the GUI.
   * @param event An action event whereby the user has selected a region from the GUI combo box.
   */
  void regionSelected() {
    String reg = String.valueOf(regionComboBox.getSelectionModel().getSelectedIndex());
    try {
      region = REGIONS[Integer.parseInt(reg)];
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      region = REGIONS[13];
    }
  }


  private void showErrorMessage(String message) {
    if (message == null) {
      message = "There was an error with updating the clinician";
    }
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Process Error");
    alert.setContentText(message);
    alert.getDialogPane().setId("ProcessError");
    alert.getDialogPane().lookupButton(ButtonType.OK).setId("processErrorOk");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  private void hideErrorMessages() {
    firstNameError.setVisible(false);
    lastNameError.setVisible(false);
    addressError.setVisible(false);
    regionError.setVisible(false);
  }


  private void updateClinician() {
    GetTask task = new GetTask(Clinician.class, ADDRESSES.SERVER.getAddress(),
        ADDRESSES.GET_CLINICIAN.getAddress() + clinician.getUserName(),
        App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      clinician = (Clinician) task.getValue();
      loadViewLabels();
      disableEditFields();
      setUpProfileHistory();
      PageNav.loaded();
    });
    task.setOnFailed(event -> showErrorMessage("There was a problem getting this clinician's information"));
    new Thread(task).start();
  }

  private boolean validateFields(String firstName, String lastName, String workAddress) {
    boolean valid = true;
    if (!firstName.equals(clinician.getFirstName())) {
      //validate the first name
      if (!UserValidator.validateAlphanumericString(false, firstName, 1, 50)) {
        valid = false;
        firstNameError.setVisible(true);
      }
    }
    if (!lastName.equals(clinician.getLastName())) {
      if (!UserValidator.validateAlphanumericString(false, lastName, 1, 50)) {
        valid = false;
        lastNameError.setVisible(true);
      }
    }
    if (!workAddress.equals(clinician.getContactDetails().getAddress().getStreetAddressLineOne())) {
      if (!UserValidator.validateAlphanumericString(true, workAddress, 1, 100)) {
        valid = false;
        addressError.setVisible(true);
      }
    }
    if (region != null && !region.equals(clinician.getContactDetails().getAddress().getRegion())) {
      if (!UserValidator.validateAlphanumericString(true, region, 0, 100) || region.equals("")) {
        valid = false;
        regionError.setVisible(true);
      }
    }
    return valid;
  }


  /**
   * Checks each text field in the View/Edit Clinician GUI for any changes to the attributes and
   * attempts to validate, then update, then finally applies any changes made. Afterwards the
   * viewing element are shown. If any of the updates were invalid, the user will be informed about
   * the violation in an alert pop-up box.
   */
  @FXML
  void doneButtonPressed() {
    hideErrorMessages();
    boolean valid = validateFields(clinicianProfileNameText.getText(), lastNameText.getText(), workAddressText.getText());
    if (valid) {
      PageNav.loading();
      // then the user changes are valid, so send patch
      ClinicianPatchDetails newClinician = new ClinicianPatchDetails(clinicianProfileNameText.getText(), lastNameText.getText(), workAddressText.getText(), region, clinician.getVersion() + 1);
      PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.PATCH_CLINICIAN.getAddress() + clinician.getUserName(), newClinician, App.getCurrentSession().getToken());
      task.setOnSucceeded(event -> {
        AccountManager.getStatusUpdates().add("Clinician " + clinician.getUserName() + " has been modified.");
        updateClinician();
      });
      task.setOnFailed(event -> {
        if (task.getException().getMessage().equals("409")) {
          showErrorMessage("Someone else edited this profile before you, please try again.");
        } else {
          showErrorMessage(null);
        }
        PageNav.loaded();
      });
      new Thread(task).start();
    }
  }


  /**
   * Undos the last event when using menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Combo Box")) {
      undoRedoComboBox = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Label")) {
      undoRedoLabel = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Area")) {
      undoRedoTextArea = true;
    }
    undoableManager.getCommandStack().undo();
  }


  /**
   * Redos the last event when using the menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Combo Box")) {
      undoRedoComboBox = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Label")) {
      undoRedoLabel = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Area")) {
      undoRedoTextArea = true;
    }
    undoableManager.getCommandStack().redo();

  }

  /**
   * Get a photo of the user logged in
   * @param username username of the donor
   */
  private void getPhoto(String username){
    String endpoint = String.format(ADDRESSES.DONOR_PHOTO.getAddress(), username);
    GetTask task = new GetTask(byte[].class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      byte[] test = (byte[]) task.getValue();
      Image img = new Image(new ByteArrayInputStream(test));
      clinicianProfilePhoto.setImage(img);
      deleteClinicianPhoto.setDisable(false);
    });
    task.setOnFailed(event -> {
      Image img = new Image("/images/default.jpg");
      clinicianProfilePhoto.setImage(img);
      deleteClinicianPhoto.setDisable(true);

    });
    new Thread(task).start();
  }



  /**
   * Sets the Donor profile's photo from a image they select from their local filesystem.
   */
  @FXML
  public void setPhoto() {
    try {
      File file = getPhotoFromFile();
      if (file != null) {
        try {
          Image image = new Image(new FileInputStream(file));
          if (image.getHeight() <= MAX_DIMENSION && image.getWidth() <= MAX_DIMENSION) {
            postPhoto(file);
          } else {
            showBadPhotoMessage(String.format("Image must be no more than %d by %d pixels", MAX_DIMENSION, MAX_DIMENSION));
          }
        } catch (IllegalArgumentException e) {
          showBadPhotoMessage(e.getMessage());
        } catch (IOException e) {
          showBadPhotoMessage(e.getMessage());
        }
      }
    }catch (URISyntaxException e) {
      showBadPhotoMessage("Could not get image from file source");
    }
  }



  /**
   * Creates a file picker for picking image files. The images can be jpg, png, or gif.
   * @return An image file
   * @throws URISyntaxException When there is an error getting the file from the filepath
   */
  public File getPhotoFromFile() throws URISyntaxException {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter newFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif");
    fileChooser.getExtensionFilters().add(newFilter);
    fileChooser.setTitle("Set photo");
    File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI()).getParent().toString());
    fileChooser.setInitialDirectory(defaultDirectory);
    File file = fileChooser.showOpenDialog(App.getWindow());
    if (file.length() > MAX_BYTES) {
      throw new IllegalArgumentException("File size has to be less than or equal to 5mb");
    } else  {
      return file;
    }
  }


  /**
   * Attempts to delete the donor's profile photo from the server. If their is no photo on the server
   * an error message appears. After the photo is deleted, the default photo is shown
   */
  @FXML
  public void deletePhoto() {
    PageNav.loading();
    DeleteTask task = new DeleteTask(ADDRESSES.SERVER.getAddress(), ADDRESSES.DELETE_PHOTO.getAddress() +
            clinician.getUserName() + "/photo", App.getCurrentSession().getToken());

    task.setOnSucceeded(successEvent -> {
      getPhoto(clinician.getUserName());
      PageNav.loaded();
    });
    task.setOnFailed( event -> {
      PageNav.loaded();
      showBadPhotoMessage("No photo to delete");
    });
    new Thread(task).start();
  }



  /**
   * Displays an alert error pop-up to the user with the given error message.
   * @param message
   */
  public void showBadPhotoMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Invalid Photo");
    alert.setContentText(
            message);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Calls a post request for the donor's profile photo to the server
   * @param file An image file which is a png, jpg, or gif
   * @throws FileNotFoundException When the image file could not be found
   * @throws IOException When the image failed to be read into a byte array
   */
  public void postPhoto(File file) throws FileNotFoundException, IOException {
    PageNav.loading();
    byte[] img = Files.readAllBytes(file.toPath());
    PostTask task = new PostTask(ADDRESSES.SERVER.getAddress(),
            ADDRESSES.POST_PHOTO.getAddress() + clinician.getUserName() + "/photo", img, App.getCurrentSession().getToken() );
    task.setOnSucceeded(successEvent -> {
      getPhoto(clinician.getUserName());
      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      PageNav.loaded();
      showBadPhotoMessage("Could not post photo, it may be corrupted");
    });
    new Thread(task).start();
  }

  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {

    if (!firstNameLabel.isVisible() && (
            !clinicianProfileNameText.getText().equals(clinician.getFirstName()) ||
                    !lastNameText.getText().equals(clinician.getLastName()) ||
                    !workAddressText.getText().equals(clinician.getContactDetails().getAddress().getStreetAddressLineOne()) ||
                    !regionComboBox.getValue().equals(clinician.getContactDetails().getAddress().getRegion()))) {

      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Close Window");
      alert.setHeaderText("If you close the window now, the information you have entered will be lost.");
      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent()) {
        if (result.get() == ButtonType.CANCEL) {
          event.consume();
          return false;
        }
      }
    }

    ChildWindowManager cwm = ChildWindowManager.getChildWindowManager();
    boolean isChild = cwm.childWindowToFront(clinician);

    if (!isChild) {

      // Only perform platform and system exit if not in a child window.
      Platform.exit();
      System.exit(0);

    }

    return true;

  }



}
