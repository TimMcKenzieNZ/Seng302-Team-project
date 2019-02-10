package seng302.controllers;


import java.util.Optional;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import org.json.JSONObject;
import seng302.*;
import seng302.model.*;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.UserValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import seng302.services.PostTask;


/**
 * Class for handling all events from the createUserPane fxml, specifically creating a user.
 */
public class CreateUserPaneController implements CreateEditController {


  // Class attributes.
  private AccountManager db = App.getDatabase();
  private static UndoableManager undoableManager = App.getUndoableManager();
  private static Boolean undoRedoDatePicker = false;
  private static Boolean undoRedoTextField = false;
  public static String lastScreen = PageNav.LOGIN; // Cannot be converted to final as is modified elsewhere.
  private static final String RED_BORDER_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";
  private static final String BLACK_BORDER_STYLE = "-fx-border-color: silver; -fx-border-width: 1px;";

  // Value fields.
  @FXML private TextField usernameTextField;
  @FXML private TextField givenNameTextField;
  @FXML private TextField lastNameTextField;
  @FXML private DatePicker dateOfBirthDatePicker;
  @FXML private TextField phoneNumberTextField;
  @FXML private PasswordField passwordField;
  @FXML private PasswordField confirmPasswordField;

  // Error messages.
  @FXML private Label usernameInvalidErrorMessage;
  @FXML private Label usernameDuplicateErrorMessage;
  @FXML private Label givenNameErrorMessage;
  @FXML private Label lastNameErrorMessage;
  @FXML private Label dobErrorMessage;
  @FXML private Label phoneNumberErrorMessage;
  @FXML private Label passwordInvalidErrorMessage;
  @FXML private Label passwordMatchErrorMessage;

  // Buttons
  @FXML private Button doneButton;
  @FXML private Button backButton;


  public void initialize() {
    doneButton.getStyleClass().add("primaryButton");
    backButton.getStyleClass().add("backButton");
    ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    textFieldArrayList.add(usernameTextField);
    textFieldArrayList.add(givenNameTextField);
    textFieldArrayList.add(lastNameTextField);

    for (TextField textField : textFieldArrayList) {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if(!undoRedoTextField) {
          undoableManager.createTextFieldChange(textField, oldValue, newValue);
        }
        undoRedoTextField = false;
      });
      textField.setOnKeyPressed(event -> {
        if(event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if(event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }
    //Set listeners for undo and redo
    dateOfBirthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoDatePicker) {
        undoableManager.createDatePickerChange(dateOfBirthDatePicker, oldValue, newValue);
      }
      undoRedoDatePicker = false;
    });
  }

  /**
   * Undos the last event on the command stack
   */
  @FXML
  public void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undid event on the command stack while editing
   */
  @FXML
  public void redoEvent() {
    redoCalled();
  }

  /**
   * Calls the last undo event when using the menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Date Picker")) {
      undoRedoDatePicker = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().undo();
  }

  /**
   * Redos the last undid event when using menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Date Picker")) {
      undoRedoDatePicker = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().redo();
  }


  @FXML
  private void createDonorReceiverAccount(ActionEvent event) {

    boolean valid = true;
    //Reset old error messages
    usernameDuplicateErrorMessage.setVisible(false);
    usernameInvalidErrorMessage.setVisible(false);
    givenNameErrorMessage.setVisible(false);
    lastNameErrorMessage.setVisible(false);
    dobErrorMessage.setVisible(false);
    phoneNumberErrorMessage.setVisible(false);
    passwordInvalidErrorMessage.setVisible(false);
    passwordMatchErrorMessage.setVisible(false);

    // Validate NHI.
    String nhi = usernameTextField.getText();
    if (!UserValidator.checkNHIRegex(nhi)) {
      valid = false;
      usernameInvalidErrorMessage.setVisible(true);
      usernameTextField.setStyle(RED_BORDER_STYLE);
    } else {
      usernameInvalidErrorMessage.setVisible(false);
      usernameTextField.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate given name.
    String givenName = givenNameTextField.getText();
    if (!UserValidator.validateAlphanumericString(false, givenName, 1, 50)) {
      givenNameErrorMessage.setVisible(true);
      givenNameTextField.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      givenNameErrorMessage.setVisible(false);
      givenNameTextField.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate last name.
    String lastName = lastNameTextField.getText();
    if (!UserValidator.validateAlphanumericString(false, lastName, 1, 50)) {
      lastNameErrorMessage.setVisible(true);
      lastNameTextField.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      lastNameErrorMessage.setVisible(false);
      lastNameTextField.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate date of birth.
    LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
    if (dateOfBirth == null || !UserValidator.validateDateOfBirth(dateOfBirth)) {
      dobErrorMessage.setVisible(true);
      dateOfBirthDatePicker.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      dobErrorMessage.setVisible(false);
      dateOfBirthDatePicker.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate phone number.
    String phoneNumber = phoneNumberTextField.getText();
    if (!UserValidator.validatePhoneNumber(phoneNumber)) {
      phoneNumberErrorMessage.setVisible(true);
      phoneNumberTextField.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      phoneNumberErrorMessage.setVisible(false);
      phoneNumberTextField.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate password.
    String password = passwordField.getText();
    if (!UserValidator.validatePassword(password)) {
      passwordInvalidErrorMessage.setVisible(true);
      passwordField.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      passwordInvalidErrorMessage.setVisible(false);
      passwordField.setStyle(BLACK_BORDER_STYLE);
    }

    // Validate password confirmation.
    String confirmPassword = confirmPasswordField.getText();
    if (!confirmPassword.equals(password)) {
      passwordMatchErrorMessage.setVisible(true);
      passwordField.setStyle(RED_BORDER_STYLE);
      confirmPasswordField.setStyle(RED_BORDER_STYLE);
      valid = false;
    } else {
      passwordMatchErrorMessage.setVisible(false);
      confirmPasswordField.setStyle(BLACK_BORDER_STYLE);
    }

    if (valid) {

      // Create donor-receiver.
      JSONObject donorReceiver = new JSONObject();
      donorReceiver.put("nhi", nhi);
      donorReceiver.put("givenName", givenName);
      donorReceiver.put("middleName", "");
      donorReceiver.put("lastName", lastName);
      donorReceiver.put("dateOfBirth", dateOfBirth.toString());
      donorReceiver.put("password", password);
      donorReceiver.put("modifyingAccount", "");
      donorReceiver.put("mobileNumber", phoneNumber);

      String endpoint = ADDRESSES.POST_DONOR.getAddress();
      ADDRESSES.setVmServer();
      PostTask task = new PostTask(JSONObject.class, ADDRESSES.SERVER.getAddress(), endpoint,
          donorReceiver.toString());
      task.setOnSucceeded(successEvent -> {
        AccountManager.getStatusUpdates().add("User " + nhi + " created.");
        showSuccessMessage();
        undoableManager.getCommandStack().save();
        backSelected(event);

      });
      task.setOnFailed(failureEvent -> {

        usernameDuplicateErrorMessage.setVisible(true);
        usernameTextField.setStyle(RED_BORDER_STYLE);

      });

      new Thread(task).start();

    }

  }


  /**
   * Changes the current page to the login page.
   * @param event
   */
  @FXML void goToLogin(ActionEvent event) {
    undoableManager.getCommandStack().save();
    PageNav.loadNewPage(PageNav.LOGIN);
  }


  /**
   * Loads the last screen viewed before the create user pane. This is specified as the lastScreen
   * variable, which should be updated when a controller switches to the create user pane.
   *
   * @param event The event triggered by selecting the back button.
   */
  @FXML
  private void backSelected(ActionEvent event) {
    if (PageNav.isAdministrator) {
      undoableManager.getCommandStack().save();
      PageNav.loadNewPage(PageNav.LISTVIEW);
    } else {
      undoableManager.getCommandStack().save();
      PageNav.loadNewPage(lastScreen);
    }
  }


  /**
   * Alerts the user of a successful donor-receiver creation operation.
   */
  private void showSuccessMessage() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Success");
    alert.setContentText("The new account was added to the database.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {

    if (!usernameTextField.getText().equals("") || !givenNameTextField.getText().equals("") ||
        !lastNameTextField.getText().equals("") || !phoneNumberTextField.getText().equals("") ||
        !passwordField.getText().equals("") || !confirmPasswordField.getText().equals("") ||
        dateOfBirthDatePicker.getValue() != null) {

      Alert alert = new Alert(AlertType.CONFIRMATION);
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
