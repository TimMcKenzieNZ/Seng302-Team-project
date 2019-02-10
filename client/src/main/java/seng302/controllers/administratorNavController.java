package seng302.controllers;

import java.io.IOException;
import java.util.Optional;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowType;
import seng302.model.*;
import seng302.model.person.LogEntry;
import seng302.model.person.User;


/**
 * Controller class for the Administrator main menu.
 */
public class administratorNavController {

  private ListChangeListener<LogEntry> listener;

  @FXML
  private TextField givenNameTextField;
  @FXML
  private TextField otherNameTextField;
  @FXML
  private TextField lastNameTextField;
  @FXML
  private TextField usernameTextField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private ListView modificationsTextField;


  @FXML
  private Label givenNameLabel;
  @FXML
  private Label otherNameLabel;
  @FXML
  private Label lastNameLabel;
  @FXML
  private Label usernameLabel;
  @FXML
  private Label passwordLabel;
  @FXML
  private Label confirmPasswordLabel;
  @FXML
  private Label errorLabel;
  @FXML
  private Label informationLabel;

  @FXML
  private Button editButton;
  @FXML
  private Button cancelButton;
  @FXML
  private Button undoButton;
  @FXML
  private Button redoButton;
  @FXML
  private Button viewAdministratorButton;
  @FXML
  private Button commandLineButton;


  /**
   * An instance of the AccountManager class. The linked hashmaps are all declared as static.
   */
  private AccountManager accountManager;

  /**
   * A boolean to check whether the admin's attribute fields have been edited (true) or not
   * (false).
   */
  private boolean isEditing;

  /**
   * A boolean to signify whether a field or GUI element is available to be undone or redone.
   */
  private static Boolean undoRedoTextField = false;


  /**
   * An undoableManager which allows us to implement undo and redo functionality for this class.
   */
  private static UndoableManager undoableManager = App.getUndoableManager();

  /**
   * The current administrator user using the application Admin main menu GUI.
   */
  private User admin;


  /**
   * An instance of the admin class specifically to get imported data information.
   */
  private Marshal marshal;


  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  @FXML
  public void initialize() {
    marshal = new Marshal();

    admin = AccountManager.getCurrentUser();

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
  private static void undoCalled() {
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
  private static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().redo();
  }


  /**
   * An alert which appears when there is an IO exception when either the System Log window or the
   * CLI window failed to load.
   */
  private void showPaneLoadErrorMessage() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Unable to load Pane");
    alert.setContentText(
        "We were unable to load the pane, please restart the application and try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }


  /**
   * Creates a new CLI window if one does not already exist when the "Command Line" button is
   * pressed in the GUI. If it does exist, it is brought to the front.
   */
  @FXML
  public void createCLIWindow() {

    // Check to see if a CLI window already exists.
    if (!App.childWindowToFront(ChildWindowType.CONSOLE)) {

      Pane cliPane = new Pane();
      try {
        FXMLLoader loader = new FXMLLoader();
        cliPane = loader.load(getClass().getResourceAsStream(PageNav.COMMANDLINE));
      } catch (IOException e) {
        showPaneLoadErrorMessage();
      }
      Stage window = new Stage();
      Scene scene = new Scene(cliPane);
      window.setTitle("SapioCulture - Command Line Interface");
      window.setScene(scene);
      window.show();

      App.addChildWindow(window, ChildWindowType.CONSOLE);

    }
  }


  /**
   * Creates a new system log window if one does not already exist. If it does, the previous system
   * log window is brought to the front.
   */
  @FXML
  public void createSystemLogWindow() {

    if (!App.childWindowToFront(ChildWindowType.SYSTEM_LOG)) {
      Pane logPane = new Pane();
      try {
        FXMLLoader loader = new FXMLLoader();

        logPane = loader.load(getClass().getResourceAsStream(PageNav.SYSTEMLOG));

      } catch (IOException e) {
        showPaneLoadErrorMessage();
      }
      Stage window = new Stage();
      Scene scene = new Scene(logPane);
      window.setTitle("SapioCulture - System Log");
      window.setScene(scene);
      window.show();

      App.addChildWindow(window, ChildWindowType.SYSTEM_LOG);
    }
  }

  /**
   * Loads the clinician list fxml when the "View Clinicians" button is pressed in the GUI.
   */
  @FXML
  public void viewCliniciansButtonPressed(ActionEvent event) {
    PageNav.loadNewPage(PageNav.CLINICIANSLIST);
  }


  /**
   * Loads the admins list fxml when the "View Clinicians" button is pressed in the GUI.
   *
   * @param event The action of the user pressing the "View Clinicians" button.
   */
  @FXML
  public void viewAdminsButtonPressed(ActionEvent event) {
    PageNav.loadNewPage(PageNav.ADMINSLIST);
  }


  /**
   * A failure dialog alert box given if the application fails to save.
   */
  private void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Save failed");
    alert.setContentText("Something went wrong and and the save failed.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * A success dialog alert box given if the application successfully saved.
   */
  private void showGoodSave() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Success");
    alert.setContentText(String.format("All changes have been successfully saved."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Informs the user that the action they are about to perform will override existing data and
   * prompt them to save the application. A boolean is returned depending on the user's choice (in a
   * GUI alert box): They can choose to save the application, in which case a 'true' boolean is
   * returned after the application saves. They can choose to continue the operation without saving,
   * in which case a 'true' boolean is returned. They can choose to cancel the operation in which
   * case a 'false' boolean is returned. A false boolean is returned if all else fails.
   *
   * @return Returns a boolean, 'true' if the user decides to save and or continue an operation,
   * 'false' otherwise.
   */
  private boolean saveOrContinueOrCancelAnOperation() {
    if (!App.saveInProgress()) {
      //We build the alert and buttons
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirmation Overwrite Dialog");
      alert.setHeaderText("Warning, possible data overwrite.");
      alert.setContentText(
          "You have unsaved changes, these changes may be lost in ensuring action. Do you wish to save the application before proceeding?");

      ButtonType buttonTypeSave = new ButtonType("Save changes");
      ButtonType buttonTypeContinue = new ButtonType("Continue with no save");
      ButtonType buttonTypeCancel = new ButtonType("Cancel action",
          ButtonBar.ButtonData.CANCEL_CLOSE);

      // The following adds the buttons to the dialog pane and resized it to correctly show all the buttons
      // We got the code from https://stackoverflow.com/questions/45866249/javafx-8-alert-different-button-sizes
      alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeContinue, buttonTypeCancel);
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.getDialogPane().getButtonTypes().stream()
          .map(alert.getDialogPane()::lookupButton)
          .forEach(node -> ButtonBar.setButtonUniformSize(node, false));

      Optional<ButtonType> result = alert.showAndWait();

      //User decides what action to take.  The thread created will not prevent the application from shutting down
      if (result.isPresent()) {
        if (result.get() == buttonTypeSave) {
          App.setSaveInProgress(true);
          SaveTask saveTask = new SaveTask(App.getDatabase());
          saveTask.setOnSucceeded(event1 -> {
            App.setSaveInProgress(false);
            showGoodSave();
            informationLabel.setText(""); // we clear the edit label in the admin profile.
          });
          saveTask.setOnFailed(event2 -> {
            showBadSaveError();
            App.setSaveInProgress(false);
          });
          Thread t = new Thread(saveTask);
          t.setDaemon(true); // thread will not prevent application shutdown
          t.start();
          return true;

        } else if (result.get() == buttonTypeContinue) {
          return true;
        } else if (result.get() == buttonTypeCancel) {
          alert.close();
          return false;
        } else {
          alert.close();
          return false;
        }
      } else {
        return false;
      }
    } else {
      return true;
    }

  }


  /**
   * Opens the import user data FXML so that we can import users into the system
   *
   * @throws IOException If the import user data FXML cannot be loaded.
   */
  private void importUserData() throws IOException {
    PageNav.isAdministrator = true;
    PageNav.loadNewPage(PageNav.IMPORTDATA);
  }


  /**
   * Checks if the application has had a change in state, and if it has, prompts the user to save
   * the application before proceeding with the importation of user files.
   * @throws IOException IOException when the users can not be imported
   */
  @FXML
  public void importUserDataButtonPressed() throws IOException {
    if (App.unsavedChangesExist()) {
      if (saveOrContinueOrCancelAnOperation()) {
        importUserData();
      }
    } else {
      importUserData();
    }
  }


  /**
   * Logs the admin out of the application when the 'logout' button is pressed in the GUI.
   */
  @FXML
  public void logoutButtonPressed() {
    AccountManager.setCurrentUser(null);
    PageNav.isAdministrator = false;
    PageNav.loadNewPage(PageNav.LOGIN);
  }


  @FXML
  public void viewDonorsButtonPressed() {
    PageNav.isAdministrator = true;
    PageNav.loadNewPage(PageNav.LISTVIEW);
  }


  /**
   * Clears the Drug Interactions API cache if the user selects 'OK' from a GUI pop-up. A system log
   * of the action is stored if the cache is cleared.
   */
  @FXML
  public void clearCache() {

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Clearing the Drugs Interaction Cache");
    alert.setHeaderText("You are going to completely empty the local Drug Interactions cache.");
    alert.setContentText("Do you wish to proceed?");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      int cacheSize = App.getDrugInteractionsCache().size();
      App.getDrugInteractionsCache().refreshCache();
      LogEntry logEntry = new LogEntry(admin, admin, "Cleared the Drugs Interactions Cache.",
          "size: " + cacheSize, "size: 0");
      AccountManager.getSystemLog().add(logEntry);
    }

  }

  @FXML
  public void viewCountries(ActionEvent event) {
    PageNav.loadNewPage(PageNav.COUNTRYLIST);}

  @FXML
  public void viewGraphs(ActionEvent event) {
    PageNav.loadNewPage(PageNav.GRAPHS);
  }
}
