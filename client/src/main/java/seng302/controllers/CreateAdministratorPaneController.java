package seng302.controllers;

import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng302.App;
import seng302.model.*;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.*;
import seng302.services.PostTask;


/* Class for handling all events from the createUserPane fxml, specifically creating a user.
 */
public class CreateAdministratorPaneController implements CreateEditController {

  /* Instance of the AccountManager class, which has methods to manipulate data
   */
  private AccountManager accountManager;

  private User admin;

  public Label createAccountLabel;
  private AccountManager db = App.getDatabase();
  private static UndoableManager undoableManager = App.getUndoableManager();
  private static Boolean undoRedoDatePicker = false;
  private static Boolean undoRedoTextField = false;

  @FXML
  private TextField usernameTextField;

  @FXML
  private TextField givenNameTextField;

  @FXML
  private TextField middleNameTextField;

  @FXML
  private TextField lastNameTextField;

  @FXML
  private PasswordField initialPasswordField;

  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private Button doneButton;

  @FXML
  private Button cancelButton;

  @FXML
  private Label errorLabel;

  public static String lastScreen = PageNav.ADMINSLIST;

  public static Session session = App.getCurrentSession();

  public void initialize() {
    admin = db.getCurrentUser();

    errorLabel.setTextAlignment(TextAlignment.CENTER);

    ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    textFieldArrayList.add(usernameTextField);
    textFieldArrayList.add(middleNameTextField);
    textFieldArrayList.add(givenNameTextField);
    textFieldArrayList.add(lastNameTextField);

    ArrayList<PasswordField> passwordFieldArrayList = new ArrayList<>();
    passwordFieldArrayList.add(initialPasswordField);
    passwordFieldArrayList.add(confirmPasswordField);

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
      textField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
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

    for (PasswordField passwordField : passwordFieldArrayList) {
      passwordField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(passwordField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      passwordField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
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

  /**
   * Checks if the usernameTextField text is a valid username and returns 'true' if so. If not the
   * usernameTextField will be highlighted in red and the GUI error label will be updated with an
   * appropriate error message.
   *
   * @return Returns 'true' if the usernameTextField is valid, 'false' otherwise.
   */
  public boolean checkUsername() {
    boolean usernameIsValid = UserValidator.validateAdminUsername(usernameTextField.getText());
    if (!usernameIsValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid username\nUsername needs to be between 3 and 20 alphanumeric characters and contain at least 1 letter\nUnderscores are allowed. 'Sudo' is forbidden.");
      usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      boolean usernameIsUsedAdmin = (null != App.getDatabase()
          .getAdminIfItExists(usernameTextField.getCharacters().toString()));
      boolean usernameIsNHI = UserValidator.checkNHIRegex(usernameTextField.getText());

      if (usernameIsNHI) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username cannot be a NHI\nPlease use another username.");
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      } else if (usernameIsUsedAdmin) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username is already in use\nPlease use another username.");
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      } else {
        // We recolor the text field border if the border was previously red. We also clear the error message.
        usernameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        errorLabel.setText("");
        return true;
      }
    }
  }


  /**
   * Creates a new Administrator and adds it to the database from the provided details. The details
   * provided are: username, given name, middle name, last name, and password. The account is not
   * saved. Invalid details result in a corresponding error dialog box.
   */
  @FXML
  void createAccount(ActionEvent event) {
    boolean testUsername = checkUsername();
    if (!testUsername) {
      return;
    }

    boolean testGivenName = checkGivenName();
    if (!testGivenName) {
      return;
    }

    boolean testMiddleName = checkMiddleName();
    if (!testMiddleName) {
      return;
    }

    boolean testLastName = checkLastName();
    if (!testLastName) {
      return;
    }

    boolean testPassword = checkPassword();
    if (!testPassword) {
      return;
    }

    String givenName = givenNameTextField.getText();
    String middleName = middleNameTextField.getText();
    String lastName = lastNameTextField.getText();
    String username = usernameTextField.getText();
    String password = initialPasswordField.getText();

    UserCreator administrator = new UserCreator(username, givenName, middleName, lastName,
        password, "", "", session.getUsername());

    createAdminInDatabase(administrator);

    cancelSelected(event);
    PageNav.loadNewPage(PageNav.ADMINSLIST);
  }


  private void createAdminInDatabase(UserCreator administrator) {
    PageNav.loading();
    PostTask postTask = new PostTask(UserCreator.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.POST_ADMIN.getAddress(), administrator, session.getToken());
    postTask.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add("Administrator " + administrator.getUsername() + " created.");
      PageNav.loaded();
    });
    postTask.setOnFailed(event -> {
      PageNav.loaded();
    });
    new Thread(postTask).start();
  }

  /**
   * Loads the last screen viewed before the create user pane. This is specified as the lastScreen
   * variable, which should be updated when a controller switches to the create user pane.
   *
   * @param event The event triggered by selecting the back button.
   */
  @FXML
  private void cancelSelected(ActionEvent event) {
    undoableManager.getCommandStack().save();
    PageNav.loadNewPage(PageNav.ADMINSLIST);
  }

  /**
   * Check whether the given name that the admin has entered is valid A valid given name is between
   * 1-50 characters and contains only alphanumeric characters
   *
   * @return Boolean Whether or not the given name is valid
   */
  private Boolean checkGivenName() {
    Boolean isValid = UserValidator.validateAlphanumericString
        (false, givenNameTextField.getCharacters().toString(), 1, 50);

    if (!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Given Name\nGiven name must be between 1 and 50 alphabetical characters"
              + "\nSpaces, commas, apostrophes, and dashes are also allowed");
      givenNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      givenNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Check whether the middle name that the admin has entered is valid A valid middle name is
   * between 1-50 characters and contains only alphanumeric characters
   *
   * @return Boolean Whether or not the middle name is valid
   */
  private Boolean checkMiddleName() {
    Boolean isValid = UserValidator.validateAlphanumericString
        (false, middleNameTextField.getCharacters().toString(), 1, 50);
    if (!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Middle Name\nMiddle name must be between 1 and 50 alphabetical characters\n"
              + "Spaces, commas, apostrophes, and dashes are also allowed.");
      middleNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      middleNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Check whether the last name that the admin has entered is valid A valid last name is between
   * 1-100 characters and contains only alphanumeric characters
   *
   * @return Boolean Whether or not the last name is valid
   */
  private Boolean checkLastName() {
    Boolean isValid = UserValidator.validateAlphanumericString
        (false, lastNameTextField.getCharacters().toString(), 1, 100);
    if (!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Last Name\nLast name must have have at most 100 characters\n" +
              "Spaces, commas, apostrophes, and dashes are also allowed.");
      lastNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      lastNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
      return true;
    }
  }

  /**
   * Check whether the password is valid and if the passwords are the same The admin has the same
   * password restriction as clinicians which is alphanumeric string of 6-30 characters
   *
   * @return Boolean Whether the password fields match
   */
  private Boolean checkPassword() {

    Boolean validPassword = UserValidator
        .validatePassword(initialPasswordField.getCharacters().toString());
    if (!validPassword) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText("Invalid password\nPassword must be between 6-30 characters\n"
          + "Password can only contain letters and numbers");
      initialPasswordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      confirmPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      return false;
    } else {
      initialPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    }

    Boolean samePassword = initialPasswordField.getCharacters().toString()
        .equals(confirmPasswordField.getCharacters().toString());
    if (!samePassword) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText("Passwords don't match");
      confirmPasswordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      confirmPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {

    if (!usernameTextField.getText().equals("") || !givenNameTextField.getText().equals("") ||
        !middleNameTextField.getText().equals("") || !lastNameTextField.getText().equals("") ||
        !initialPasswordField.getText().equals("") || !confirmPasswordField.getText().equals("")) {

      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Close Window");
      alert.setHeaderText("If you close the window now, the information you have entered will be lost.");
      Optional<ButtonType> result = alert.showAndWait();


      if ((result.isPresent()) && (result.get() == ButtonType.CANCEL)) {

        // If cancel is pressed, consume the event.
        event.consume();
        return false;

      } else {

        Platform.exit();
        System.exit(0);
        return true;

      }

    } else {

      Platform.exit();
      System.exit(0);
      return true;

    }

  }


}
