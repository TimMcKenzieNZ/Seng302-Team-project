package seng302.controllers;

import java.util.ArrayList;
import java.util.Optional;

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
import javafx.scene.paint.Color;
import org.json.JSONObject;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.model.*;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.*;
import seng302.services.GetTask;
import seng302.services.PatchTask;


/**
 * Controller class for the Administrator main menu.
 */
public class AdministratorMainMenuAreaController implements CreateEditController {


  @FXML
  protected TextField givenNameTextField;
  @FXML
  protected TextField otherNameTextField;
  @FXML
  protected TextField lastNameTextField;
  @FXML
  protected TextField usernameTextField;
  @FXML
  protected PasswordField passwordField;
  @FXML
  protected PasswordField confirmPasswordField;

  @FXML
  protected ListView modificationsTextField;

  @FXML
  protected Label givenNameLabel;
  @FXML
  protected Label otherNameLabel;
  @FXML
  protected Label lastNameLabel;
  @FXML
  protected Label usernameLabel;
  @FXML
  protected Label passwordLabel;
  @FXML
  protected Label confirmPasswordLabel;

  @FXML
  protected Label errorLabel;
  @FXML
  protected Label informationLabel;

  @FXML
  protected Button editButton;
  @FXML
  protected Button cancelButton;

  @FXML
  protected Button undoButton;
  @FXML
  protected Button redoButton;

  protected static final String VALID_STYLE = " -fx-border-color: silver ; -fx-border-width: 1px ; ";
  protected static final String INVALID_STYLE = " -fx-border-color: red ; -fx-border-width: 2px ; ";

  /**
   * A boolean to check whether the admin's attribute fields have been edited (true) or not
   * (false).
   */
  protected boolean isEditing;

  /**
   * A boolean to signify whether a field or GUI element is available to be undone or redone.
   */
  protected static Boolean undoRedoTextField = false;

  /**
   * An undoableManager which allows us to implement undo and redo functionality for this class.
   */
  protected static UndoableManager undoableManager = App.getUndoableManager();

  /**
   * The current administrator user using the application Admin main menu GUI.
   */
  protected User admin;

  protected Session session = App.getCurrentSession();

  /**
   * Switches the attribute labels of the administrator to display in the GUI and hides the edit
   * text fields for these attributes from view
   */
  public void editSwitchModeToView() {
    isEditing = false;
    givenNameTextField.setVisible(false);
    otherNameTextField.setVisible(false);
    lastNameTextField.setVisible(false);
    usernameTextField.setVisible(false);
    passwordField.setVisible(false);
    confirmPasswordField.setVisible(false);
    confirmPasswordLabel.setVisible(false);
    passwordLabel.setVisible(false);
    cancelButton.setVisible(false);
    undoButton.setVisible(false);
    redoButton.setVisible(false);
    errorLabel.setVisible(false);
    informationLabel.setVisible(true);
    editButton.getStyleClass().remove("primaryButton");

    givenNameLabel.setVisible(true);
    otherNameLabel.setVisible(true);
    lastNameLabel.setVisible(true);
    usernameLabel.setVisible(true);
    editButton.setText("Edit");
  }


  /**
   * Switches the editable text fields of the administrator to display in the GUI and hides the view
   * labels for these attributes from view
   */
  public void viewSwitchModeToEdit() {
    isEditing = true;
    givenNameTextField.setVisible(true);
    otherNameTextField.setVisible(true);
    lastNameTextField.setVisible(true);
    usernameTextField.setVisible(true);
    passwordField.setVisible(true);
    confirmPasswordField.setVisible(true);
    confirmPasswordLabel.setVisible(true);
    passwordLabel.setVisible(true);
    cancelButton.setVisible(true);
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    givenNameLabel.setVisible(false);
    otherNameLabel.setVisible(false);
    lastNameLabel.setVisible(false);
    usernameLabel.setVisible(false);
    editButton.setText("Done");
    informationLabel.setVisible(false);
    clearInformationLabel();

    //Remove all error labeling
    errorLabel.setText("");
    givenNameTextField.setStyle(VALID_STYLE);
    otherNameTextField.setStyle(VALID_STYLE);
    lastNameTextField.setStyle(VALID_STYLE);
    usernameTextField.setStyle(VALID_STYLE);
    passwordField.setStyle(VALID_STYLE);
    confirmPasswordField.setStyle(VALID_STYLE);


  }

  /**
   * Sets all the attribute labels of the administrator in the GUI to the values given in their
   * account.
   */
  public void setAdministatorLabels() {

    givenNameLabel.setText(admin.getFirstName());
    otherNameLabel.setText(admin.getMiddleName());
    lastNameLabel.setText(admin.getLastName());
    usernameLabel.setText(admin.getUserName());
    givenNameTextField.setText(admin.getFirstName());
    otherNameTextField.setText(admin.getMiddleName());
    lastNameTextField.setText(admin.getLastName());
    usernameTextField.setText(admin.getUserName());
    passwordField.setText(admin.getPassword());
    confirmPasswordField.setText(admin.getPassword());
    setListView(modificationsTextField);
  }


  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  @FXML
  public void initialize() {
    PageNav.loading();
    editSwitchModeToView();
    getAdmin(true);
  }

  protected void setup(){
    editSwitchModeToView();
    setAdministatorLabels();

    if (admin.getUserName().equals("Sudo")) {
      editButton.setVisible(false);
      editButton.setDisable(true);
    }

    errorLabel.setTextFill(Color.web("red"));

    // An array to store our text fields, we will add listeners to each field to enable undo/redo on each of them
    ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    textFieldArrayList.add(usernameTextField);
    textFieldArrayList.add(otherNameTextField);
    textFieldArrayList.add(givenNameTextField);
    textFieldArrayList.add(lastNameTextField);

    ArrayList<PasswordField> passwordFieldArrayList = new ArrayList<>();
    passwordFieldArrayList.add(passwordField);
    passwordFieldArrayList.add(confirmPasswordField);

    // We now add listeners to each editable text field so we can can call command pattern methods to invoke undo/redo actions.
    for (TextField textField : textFieldArrayList) {
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

    for (PasswordField passwordInputField : passwordFieldArrayList) {
      passwordInputField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(passwordInputField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      passwordInputField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
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
    PageNav.loaded();
  }

  /**
   * Retrieves an administrator from the server.
   * @param setup If true, the setup method will be called after an admin is retrieved.
   */
  protected void getAdmin(boolean setup){
    String endpoint = ADDRESSES.GET_ADMIN.getAddress() + session.getUsername();
    GetTask task = new GetTask(Administrator.class, ADDRESSES.SERVER.getAddress(), endpoint, session.getToken());
    task.setOnSucceeded(event -> {
      admin = (Administrator) task.getValue();
      if (setup) {
        setup();
      }
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get admin");
    });
    new Thread(task).start();

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
            .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().redo();
  }


  /**
   * sets and prints the update log of the admin to the list view in the History pane
   *
   * @param toView listview to set of
   */
  public void setListView(ListView toView) {
    ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
    observableLogs.addAll(admin.getModifications());
    toView.setCellFactory(param -> new ListCell<LogEntry>() {
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
    toView.setItems(observableLogs);
  }





  /**
   * Appends a message to a new line of the error label.
   * @param message The message to be added as a String object.
   */
  protected void appendErrorLabel(String message) {

    String currentText = errorLabel.getText();
    if (currentText.equals("")) {
      errorLabel.setText(message);
    } else {
      errorLabel.setText(currentText + "\n" + message);
    }

  }


  /**
   * Confirms that the specified given name is valid. If it is not, the method will return false and
   * add appropriate error notifications to the user interface. If it is, and a change has occurred,
   * it will be appended to the updated admin.
   * @param editedGivenName The given name to be validated as a String object.
   * @param updatedAdmin A JSON object to which the given name will be appended if edited.
   * @return True if given name is valid. False otherwise.
   */
  protected boolean checkEditedGivenName(String editedGivenName, JSONObject updatedAdmin) {

    givenNameTextField.setStyle(VALID_STYLE);
    if (!editedGivenName.equals(admin.getFirstName())) {
      if (UserValidator.validateAlphanumericString(false, editedGivenName, 1, 50)) {
        updatedAdmin.put("firstName", editedGivenName);
      } else {
        givenNameTextField.setStyle(INVALID_STYLE);
        appendErrorLabel("Given names must consist of between 1 and 50 letters.");
        return false;
      }
    }

    return true;

  }


  /**
   * Confirms that the specified middle name is valid. If it is not, the method will return false and
   * add appropriate error notifications to the user interface. If it is, and a change has occurred,
   * it will be appended to the updated admin.
   * @param editedMiddleName The middle name to be validated as a String object.
   * @param updatedAdmin A JSON object to which the middle name will be appended if edited.
   * @return True if middle name is valid. False otherwise.
   */
  protected boolean checkEditedMiddleName(String editedMiddleName, JSONObject updatedAdmin) {

    otherNameTextField.setStyle(VALID_STYLE);
    if (!editedMiddleName.equals(admin.getMiddleName())) {
      if (UserValidator.validateAlphanumericString(false, editedMiddleName, 0, 50)) {
        updatedAdmin.put("middleName", editedMiddleName);
      } else {
        otherNameTextField.setStyle(INVALID_STYLE);
        appendErrorLabel("Middle names must be composed of less than 50 letters.");
        return false;
      }
    }

    return true;

  }


  /**
   * Confirms that the specified last name is valid. If it is not, the method will return false and
   * add appropriate error notifications to the user interface. If it is, and a change has occurred,
   * it will be appended to the updated admin.
   * @param editedLastName The last name to be validated as a String object.
   * @param updatedAdmin A JSON object to which the last name will be appended if edited.
   * @return True if last name is valid. False otherwise.
   */
  protected boolean checkEditedLastName(String editedLastName, JSONObject updatedAdmin) {

    lastNameTextField.setStyle(VALID_STYLE);
    if (!editedLastName.equals(admin.getLastName())) {
      if (UserValidator.validateAlphanumericString(false, editedLastName, 0, 50)) {
        updatedAdmin.put("lastName", editedLastName);
      } else {
        lastNameTextField.setStyle(INVALID_STYLE);
        appendErrorLabel("A last name should consist of less than 50 letters.");
        return false;
      }
    }

    return true;

  }


  /**
   * Confirms that the specified password is valid. If it is not, the method will return false and
   * add appropriate error notifications to the user interface. If it is, and a change has occurred,
   * it will be appended to the updated admin.
   * @param editedPassword The password to be validated as a String object.
   * @param updatedAdmin A JSON object to which the password will be appended if edited.
   * @return True if password is valid. False otherwise.
   */
  protected boolean checkEditedPassword(String editedPassword, JSONObject updatedAdmin) {

    passwordField.setStyle(VALID_STYLE);
    if (!editedPassword.equals(admin.getPassword())) {
      if (UserValidator.validatePassword(editedPassword)) {
        updatedAdmin.put("password", editedPassword);
      } else {
        appendErrorLabel("A password must be at least 6 characters.");
        passwordField.setStyle(INVALID_STYLE);
        return false;
      }

    }

    return true;

  }


  /**
   * Confirms that the two password fields are matching. If they are not, false will be returned and
   * appropriate error notifications will appear on the user interface. If they are, the method will
   * return true.
   * @param editedPassword The first apssword as a String object.
   * @param matchingPassword The second password as a String object.
   * @return True if both passwords match. False otherwise.
   */
  protected boolean checkMatchingPassword(String editedPassword, String matchingPassword) {

    passwordField.setStyle(VALID_STYLE);
    confirmPasswordField.setStyle(VALID_STYLE);

    if (!editedPassword.equals(matchingPassword)) {
      appendErrorLabel("The two passwords you have specified do not match.");
      passwordField.setStyle(INVALID_STYLE);
      confirmPasswordField.setStyle(INVALID_STYLE);
      return false;
    }

    return true;

  }


  /**
   * Checks all input fields and sends a confirmation
   */
  protected void checkInputsAndUpdateIfValid() {

    errorLabel.setText("");
    errorLabel.setVisible(false);
    JSONObject updatedAdmin = new JSONObject();

    // Update given name if a name change has occurred and the new name is valid.
    String editedGivenName = givenNameTextField.getText();
    boolean allValid = checkEditedGivenName(editedGivenName, updatedAdmin);

    // Update middle name if a name change has occurred and the new name is valid.
    String editedMiddleName = otherNameTextField.getText();
    allValid = checkEditedMiddleName(editedMiddleName, updatedAdmin) && allValid;

    // Update last name if a name change has occurred and the new name is valid.
    String editedLastName = lastNameTextField.getText();
    allValid = checkEditedLastName(editedLastName, updatedAdmin) && allValid;

    // Update password if it has changed and the new value is valid.
    String editedPassword = passwordField.getText();
    allValid = checkEditedPassword(editedPassword, updatedAdmin) && allValid;

    // Confirm that both password fields match.
    String matchingPassword = confirmPasswordField.getText();
    allValid = checkMatchingPassword(editedPassword, matchingPassword) && allValid;


    if (editedGivenName.equals(admin.getFirstName()) && editedMiddleName.equals(admin.getMiddleName()) &&
            editedLastName.equals(admin.getLastName()) && editedPassword.equals(admin.getPassword()) && matchingPassword.equals(admin.getPassword())) {

      editSwitchModeToView();
      setAdministatorLabels();
      return;

    }

    if (allValid && updatedAdmin.length() > 0) {

      updatedAdmin.put("version", Integer.toString(admin.getVersion() + 1));
      String endpoint = ADDRESSES.PATCH_ADMIN.getAddress() + admin.getUserName();
      PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), endpoint,
              updatedAdmin.toString(), session.getToken());
      task.setOnSucceeded(successEvent -> {
        AccountManager.getStatusUpdates().add("Administrator " + admin.getUserName() + " modified.");
        if (isEditing) {
          // Do not switch if user has already switch.
          getAdmin(true);
          editSwitchModeToView();
          setAdministatorLabels();
        }

      });

      task.setOnFailed(failureEvent -> {

        errorLabel.setText("Failed to updated. The account you are modifying was changed by another person, or the server is inaccessible.");
        errorLabel.setVisible(true);
        getAdmin(false);
        setAdministatorLabels();

      });

      new Thread(task).start();

    } else if (!allValid) {

      errorLabel.setVisible(true);

    }

  }


  /**
   * Carries out a swapping of the admins attribute test fields and labels when the 'edit' or 'done'
   * button is pressed in the GUI.
   *
   * @param event The action of the user pressing the 'Edit' or 'Done' button.
   */
  @FXML
  public void editButtonPressed(ActionEvent event) {

    if (isEditing) {
      checkInputsAndUpdateIfValid();
    } else {
      viewSwitchModeToEdit();
      setAdministatorLabels();
    }

  }

  public void clearInformationLabel() {
    informationLabel.setText(" ");
  }


  /**
   * Toggles the admin attribute editable text fields to uneditable labels without committing any
   * changes.
   *
   * @param event event when cancelled
   */
  @FXML
  public void cancelButtonPressed(ActionEvent event) {
    editSwitchModeToView();
  }


  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {

    if (isEditing && (!givenNameTextField.getText().equals(admin.getFirstName()) ||
            !otherNameTextField.getText().equals(admin.getMiddleName()) ||
            !lastNameTextField.getText().equals(admin.getLastName()) ||
            !passwordField.getText().equals(admin.getPassword()) ||
            !confirmPasswordField.getText().equals(admin.getPassword()))) {

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
    boolean isChild = cwm.childWindowToFront(admin);

    if (!isChild) {

      // Only perform platform and system exit if not in a child window.
      Platform.exit();
      System.exit(0);

    }

    return true;

  }


}
