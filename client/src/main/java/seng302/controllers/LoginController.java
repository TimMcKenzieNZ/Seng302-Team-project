package seng302.controllers;

import static seng302.controllers.BaseController.createBadPopUp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import seng302.App;
import seng302.exceptions.InvalidCredentialsException;
import seng302.model.*;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.*;
import seng302.services.GetTask;
import seng302.services.PostTask;


public class LoginController {

  @FXML
  public Text welcomeMessage;
  @FXML
  public TextField usernameTextField;
  @FXML
  public PasswordField passwordField;
  @FXML
  public Button loginButton;
  @FXML
  public Button createDonorButton;
  @FXML
  public Label errorLabel;

  public static Session session = App.getCurrentSession();

  public static AccountManager accountManager = App.getDatabase();

  public void setErrorLabel() {
    errorLabel.setText("Donor is dead, cannot log in");
  }

  @FXML
  private void initialize() {
    loginButton.setDefaultButton(true); // when enter is pressed, fires this button.
    if (session.getDonorIsDead()) {
      errorLabel.setText("Donor is dead, cannot log in");
      errorLabel.setTextFill(Color.web("red"));
      session.setDonorIsDead(false);
    } else {
      errorLabel.setText("");
      errorLabel.setTextFill(Color.web("black"));
    }
  }


  /**
   * resets the text fields of the GUI of the login pane.
   */
  public void resetFields() {
    errorLabel.setText("");
    usernameTextField.clear();
    passwordField.clear();

    errorLabel.setTextFill(Color.web("black"));

  }

  /**
   * Clears all fields on the log in screen and changes page to the donor creation panel
   */
  public void createDonor() {
    resetFields();
    PageNav.loadNewPage(PageNav.CREATE);
  }


  /**
   * Sets the username TextField and password PasswordField to have a
   */
  private void invalidLoginNotification() {
    errorLabel.setText("Invalid username or password.");
    errorLabel.setTextFill(Color.web("red"));
    usernameTextField.getStyleClass().add("failed");
    passwordField.getStyleClass().add("failed");
  }

  /**
   * Sets the username TextField and password PasswordField to have a
   */
  private void deceasedDonorLoginNotification() {
    errorLabel.setText("Deceased user. Failed to login");
    errorLabel.setTextFill(Color.web("red"));
    usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    passwordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
  }


  /**
   * Attempts to login and get session token.
   */
  @FXML
  private void remoteLogin() {
      PageNav.loading();
      LoginAttempt attempt = new LoginAttempt();
      if (UserValidator.checkNHIRegex(usernameTextField.getText().toUpperCase())) {
        attempt.setUsername(usernameTextField.getText().toUpperCase());
      } else {
        attempt.setUsername(usernameTextField.getText());
      }
      attempt.setPassword(passwordField.getText());

      // Uses Task to check cache/query remote API off main gui thread
      PostTask task = new PostTask(LoginResult.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.LOGIN.getAddress(), attempt);
      task.setOnSucceeded(event -> {
        LoginResult result = (LoginResult) task.getValue();
        try {
          session.setSession(result);
        } catch (InvalidCredentialsException e) {
          createBadPopUp();
        }
        PageNav.loaded();
        try {
          if(session.getUserType().equalsIgnoreCase("donor")){
            PageNav.loadNewPage(PageNav.VIEW);
          } else if (session.getUserType().equalsIgnoreCase("clinician")) {
            getClinician(result.getUsername(), result.getToken());
          } else if (session.getUserType().equalsIgnoreCase("admin")){
            PageNav.loadNewPage(PageNav.ADMINMENU);
          }
        } catch(IllegalArgumentException e) {
          createBadPopUp();
        }
      });
      task.setOnFailed(event -> {
        PageNav.loaded();
        invalidLoginNotification();
      });
      new Thread(task).start();
  }


  /**
   * Gets a clinician object from the server
   * @param username username of clinician
   * @param token token of the current user
   */
  private void getClinician(String username, String token) {

    String endpoint = ADDRESSES.GET_CLINICIAN.getAddress() + username;
    GetTask task = new GetTask(Clinician.class, ADDRESSES.SERVER.getAddress(), endpoint, token);
    task.setOnSucceeded(event -> {
      Clinician clinician = (Clinician) task.getValue();
      AccountManager.setCurrentUser(clinician);
      ClinicianProfileController.setClinician(clinician);
      PageNav.loadNewPage(PageNav.MAINMENU);
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get clinician");
    });
    new Thread(task).start();

  }
}




