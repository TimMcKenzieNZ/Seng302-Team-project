package seng302.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seng302.App;
import seng302.model.*;
import seng302.model.person.UserCreator;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.UserValidator;
import seng302.services.PostTask;

import java.util.Optional;


/* Class for handling all events from the clinicianCreation fxml, specifically creating a clinician.
 */
public class ClinicianCreationController implements CreateEditController {

  /**
   * An Account Manager object created on application start up.
   */
  private AccountManager db = App.getDatabase();

  private static UndoableManager undoableManager = App.getUndoableManager();
  private static Boolean undoRedoComboBox = false;
  private static Boolean undoRedoTextField = false;
  private ObservableList<TextField> fields = FXCollections.observableArrayList();

  public static Session session = App.getCurrentSession();


  /**
   * A string array of all the administrative regions in New Zealand.
   */
  private final String[] REGIONS = {"Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
      "Hawke's Bay",
      "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Malborough", "West Coast",
      "Canterbury", "Otago", "Southland"};

  /**
   * A string of the Clinician's region.
   */
  private String region = null;

  @FXML
  public Label createClinicianLabel;

  @FXML
  public TextField givenNameTextField;

  @FXML
  public TextField lastNameTextField;

  @FXML
  public TextField staffIDTextField;

  @FXML
  public TextField streetAddressTextField;

  @FXML
  public ComboBox regionComboBox;

  @FXML
  public Button doneButton;

  @FXML
  public Button helpButton;

  @FXML
  public Button cancelButton;

  @FXML
  public PasswordField passwordTextField;

  @FXML public Label staffIdError;
  @FXML public Label firstNameError;
  @FXML public Label lastNameError;
  @FXML public Label passwordError;
  @FXML public Label addressError;
  @FXML public Label regionError;


  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Creation failed");
    alert.setContentText(
        "Something went wrong and and the application failed to create the clinician. Please try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Changes the current page to the Login page when the cancel button is clicked in the GUI.
   *
   */
  @FXML
  void cancelButtonClicked() {

    undoableManager.getCommandStack().save();
    if (session.getUserType().equalsIgnoreCase("admin")) {
      PageNav.isAdministrator = false;
      PageNav.loadNewPage(PageNav.CLINICIANSLIST);
    } else {
      PageNav.loadNewPage(PageNav.MAINMENU);
    }
  }


  public void hideErrorMessage() {
    firstNameError.setVisible(false);
    lastNameError.setVisible(false);
    staffIdError.setVisible(false);
    passwordError.setVisible(false);
    addressError.setVisible(false);
    regionError.setVisible(false);
  }


  @FXML
  /**
   * Creates a new clinician and adds it to the database from the provided details. The details provided are:
   * given name, last name, staff ID, address, region, and password. Invalid details result in a corresponding error
   * dialog box. If there are no errors then the clinician is saved to file.
   */
  void doneButtonClicked(ActionEvent event) {
    hideErrorMessage();
    boolean valid = true;
    boolean testID2 = UserValidator.validateStaffIDIsInt(staffIDTextField.getText());
    if (!testID2) {
      staffIdError.setText("Staff ID has to be an integer greater than zero.");
      staffIdError.setVisible(true);
      valid = false;
    }

    boolean testGivenName = UserValidator.validateAlphanumericString
        (false, givenNameTextField.getText(), 1, 50);
    if (!testGivenName) {
      firstNameError.setText("Given name must be between 1 and 50 alphabetical characters. " +
          "Spaces, commas, apostrophes, & dashes allowed.");
      firstNameError.setVisible(true);
      valid = false;
    }

    boolean testLastName = UserValidator.validateAlphanumericString
        (false, lastNameTextField.getText(), 1, 50);
    if (!testLastName) {
      lastNameError.setText("Last name  must be between 1 and 50 alphabetical characters. " +
          "Spaces, commas, apostrophes, & dashes allowed.");
      lastNameError.setVisible(true);
      valid = false;
    }

    boolean testStreetAddress = UserValidator.validateAlphanumericString
        (true, streetAddressTextField.getText(), 0, 100);
    if (!testStreetAddress) {
      addressError.setText("Street Address must be between 0 & 100 alphanumeric characters. " +
          "Spaces, commas, apostrophes, & dashes allowed.");
      addressError.setVisible(true);
      valid = false;
    }

    if (region == null) {
      regionError.setText("Region has not been selected");
      regionError.setVisible(true);
      valid = false;
    }

    boolean testPassword = UserValidator.validatePassword(passwordTextField.getText());
    if (!testPassword) {
      passwordError.setText("Password should be between 6 and 30 alphanumeric characters of any case.");
      passwordError.setVisible(true);
      valid = false;
    }

    if (!valid) {
      return;
    }
    UserCreator clinician = new UserCreator(staffIDTextField.getText(), givenNameTextField.getText(), "", lastNameTextField.getText(), passwordTextField.getText(), region, streetAddressTextField.getText(), session.getUsername());
    PageNav.loading();
    PostTask postTask = new PostTask(UserCreator.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_CLINICIAN.getAddress(), clinician, App.getCurrentSession().getToken());
    postTask.setOnSucceeded(event1 -> {
      PageNav.loaded();
      AccountManager.getStatusUpdates().add("Clinician " + staffIDTextField.getText() + " created");
      undoableManager.getCommandStack().save();
      if (App.getCurrentSession().getUserType().equalsIgnoreCase("admin")) {
        PageNav.isAdministrator = false;
        PageNav.loadNewPage(PageNav.CLINICIANSLIST);
      } else {
        PageNav.loadNewPage(PageNav.MAINMENU);
      }
    });
    postTask.setOnFailed(event1 -> {
      PageNav.loaded();
      if (postTask.getException().getMessage().contains("409")) {
        staffIdError.setText("This Staff ID is already in use, please choose another.");
        staffIdError.setVisible(true);

      } else {
        showBadSaveError();
      }
    });
    new Thread(postTask).start();
  }

  /**
   * Sets the region attribute given by the combo box option selected by the user in the GUI.
   */
  @FXML
  private void regionSelected() {
    region = regionComboBox.getSelectionModel().getSelectedItem().toString();
  }

  @FXML
  private void initialize() {
    hideErrorMessage();
    regionComboBox.getItems().addAll(REGIONS);
    fields.addAll(givenNameTextField, lastNameTextField, staffIDTextField, passwordTextField,
        streetAddressTextField);

    for (TextField textField : fields) {
      textField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(textField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
        @Override
        public void handle(javafx.scene.input.KeyEvent event) {
          if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            undoEvent();
          } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
            redoEvent();
          }
        }
      });
    }

    //Undo and redo
    regionComboBox.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          @Override
          public void changed(ObservableValue<? extends Number> observable, Number oldValue,
              Number newValue) {
            if (!undoRedoComboBox) {
              undoableManager.createComboBoxChange(regionComboBox, oldValue, newValue);
            }
            undoRedoComboBox = false;
          }
        });


  }


  /**
   * Undos the last undoable on the command stack
   */
  @FXML
  public void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undoable on the commands stack
   */
  @FXML
  public void redoEvent() {
    redoCalled();

  }

  /**
   * Calls the undo event when using menu bar
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
    undoableManager.getCommandStack().undo();
  }

  /**
   * Calls the redo event when using the menu bar
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
    undoableManager.getCommandStack().redo();

  }


  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {

    if (!givenNameTextField.getText().equals("") || !lastNameTextField.getText().equals("") ||
            !staffIDTextField.getText().equals("") || !passwordTextField.getText().equals("") ||
            !streetAddressTextField.getText().equals("") || regionComboBox.getValue() != null) {

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

    Platform.exit();
    System.exit(0);
    return true;

  }

}
