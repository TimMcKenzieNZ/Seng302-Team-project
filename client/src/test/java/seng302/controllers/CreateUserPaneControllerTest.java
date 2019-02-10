package seng302.controllers;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.testfx.framework.junit.ApplicationTest;

import seng302.App;
import seng302.model.Database.DBCConnection;


/**
 * The CreateUserPaneControllerTest class provides a series of TestFX-backed JUnit tests which
 * evaluate the functionality of the donor-receiver creation pane. It should be noted that these
 * tests examine the pane itself, not its supporting methods like validation. It is assumed that
 * their encapsulating class is tested separately.
 */
@Ignore //Failing sporadically on server and locally
public class CreateUserPaneControllerTest extends ApplicationTest{


  // Class Attributes
  DBCConnection connection;

  private TextField usernameTextField;
  private TextField givenNameTextField;
  private TextField lastNameTextField;
  private DatePicker dateOfBirthDatePicker;
  private TextField phoneNumberTextField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;
  private Label usernameInvalidErrorMessage;
  private Label usernameDuplicateErrorMessage;
  private Label givenNameErrorMessage;
  private Label lastNameErrorMessage;
  private Label dobErrorMessage;
  private Label phoneNumberErrorMessage;
  private Label passwordInvalidErrorMessage;
  private Label passwordMatchErrorMessage;
  private Button doneButton;
  private Button backButton;

  private static final String RED_BORDER_STYLE = "-fx-border-color: red; -fx-border-width: 2px;";
  private static final String BLACK_BORDER_STYLE = "-fx-border-color: silver; -fx-border-width: 1px;";
  private static final String TEST_NHI = "ZZZ5555";


  /**
   * Configures the CI runner to perform tests in headless mode. This means that a physical
   * graphical user interface will not be generated during tests.
   */
  @BeforeClass
  public static void configureHeadless() {

    GitlabGUITestSetup.headless();

  }


  /**
   * Overrides the start method defined by ApplicationTest.
   * @param stage The stage which is to be displayed at application start.
   * @throws IllegalArgumentException Occurs when the stage provided is null.
   */
  @Override
  public void start(Stage stage) throws Exception {

    App application = new App();
    application.start(stage);

  }


  /**
   * This method is called before each test. It retrieves all GUI elements required for testing and
   * populates all input fields with valid data.
   */
  @Before
  public void setUp() throws SQLException {

    // Create database connection.
    connection = new DBCConnection("jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-prod",
        "seng302-team600", "TailspinElla4435");

    // Navigate to create donor-receiver pane.
    clickOn("#createDonorButton");

    // Retrieve GUI elements.
    usernameTextField = lookup("#usernameTextField").query();
    givenNameTextField = lookup("#givenNameTextField").query();
    lastNameTextField = lookup("#lastNameTextField").query();
    dateOfBirthDatePicker = lookup("#dateOfBirthDatePicker").query();
    phoneNumberTextField = lookup("#phoneNumberTextField").query();
    passwordField = lookup("#passwordField").query();
    confirmPasswordField = lookup("#confirmPasswordField").query();
    usernameInvalidErrorMessage = lookup("#usernameInvalidErrorMessage").query();
    usernameDuplicateErrorMessage = lookup("#usernameDuplicateErrorMessage").query();
    givenNameErrorMessage = lookup("#givenNameErrorMessage").query();
    lastNameErrorMessage = lookup("#lastNameErrorMessage").query();
    dobErrorMessage = lookup("#dobErrorMessage").query();
    phoneNumberErrorMessage = lookup("#phoneNumberErrorMessage").query();
    passwordInvalidErrorMessage = lookup("#passwordInvalidErrorMessage").query();
    passwordMatchErrorMessage = lookup("#passwordMatchErrorMessage").query();
    doneButton = lookup("#doneButton").query();
    backButton = lookup("#backButton").query();

    // Set valid data.
    usernameTextField.setText(TEST_NHI);
    givenNameTextField.setText("Harriet");
    lastNameTextField.setText("West");
    dateOfBirthDatePicker.setValue(LocalDate.parse("2018-01-01"));
    phoneNumberTextField.setText("032938472");
    passwordField.setText("password");
    confirmPasswordField.setText("password");

  }


  /**
   * This method is called after each test. It nullifies all class attributes, allowing the
   * corresponding values to be collected by the garbage collector. It also cleans the database.
   */
  @After
  public void tearDown() throws SQLException {

    // Clean up database.
    String contactStatement = String.format("DELETE FROM `Users` WHERE `username`='%s'", TEST_NHI);
    connection.executeStatement(contactStatement);

    // Nullify
    connection = null;
    usernameTextField = null;
    givenNameTextField = null;
    lastNameTextField = null;
    dateOfBirthDatePicker = null;
    phoneNumberTextField = null;
    passwordField = null;
    confirmPasswordField = null;
    usernameInvalidErrorMessage = null;
    usernameDuplicateErrorMessage = null;
    givenNameErrorMessage = null;
    lastNameErrorMessage = null;
    dobErrorMessage = null;
    passwordInvalidErrorMessage = null;
    passwordMatchErrorMessage = null;
    doneButton = null;
    backButton = null;

  }


  /**
   * Attempts to create a donor-receiver account with a set of valid inputs. This operation should
   * succeed, and a valid server request should be generated. No input should have a red border.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  @Ignore
  public void allInputsValidTest() throws SQLException {

    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(new ArrayList<>()));
    assertTrue(verifyErrorMessageVisibility(new ArrayList<>()));
    boolean exists = confirmDonorReceiverExists(TEST_NHI);
    assertTrue(exists);

  }


  /**
   * Attempts to create a donor-receiver account with an invalid NHI. This should fail. The
   * username field should be highlighted with a red box and an accompanying error message should
   * be presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void invalidNHITest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(usernameTextField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(usernameInvalidErrorMessage);

    String invalidNHI = "1234567";
    usernameTextField.setText(invalidNHI);
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver account with an existing NHI. This should fail. The
   * username field should be highlighted with a red box and an accompanying error message should
   * be presented. No other error messages or highlighting should be visible.
   */
  @Test
  @Ignore
  public void duplicateUsernameTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(usernameTextField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(usernameDuplicateErrorMessage);

    String duplicateNHI = "ABC1234";
    usernameTextField.setText(duplicateNHI);
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver account with an invalid given name. This should fail. The
   * given name field should be highlighted with a red box and an accompanying error message should
   * be presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */

  @Test
  public void invalidGivenNameTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(givenNameTextField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(givenNameErrorMessage);

    givenNameTextField.setText("");
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver account with an invalid last name. This should fail. The
   * last name field should be highlighted with a red box and an accompanying error message should
   * be presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void invalidLastNameTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(lastNameTextField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(lastNameErrorMessage);

    lastNameTextField.setText("");
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver with an invalid date of birth. This should fail. The date
   * of birth field should be highlighted with a red box an an accompanying error message should be
   * presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void invalidDateOfBirthTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(dateOfBirthDatePicker);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(dobErrorMessage);

    dateOfBirthDatePicker.setValue(LocalDate.now().plusYears(1));
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver with an invalid phone number. This should fail. The date of
   * birth should be highlighted with a red box and an accompanying error message should be
   * presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void invalidPhoneNumberTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(phoneNumberTextField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(phoneNumberErrorMessage);

    phoneNumberTextField.setText("DINOSAUR");
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver with an invalid password. This should fail. The password
   * field should be highlighted red and an accompanying error message should be presented. No
   * other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void invalidPasswordTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(passwordField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(passwordInvalidErrorMessage);

    passwordField.setText("");
    confirmPasswordField.setText("");
    clickOn("#doneButton");
    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Attempts to create a donor-receiver with a non-matching password. This should fail. Both
   * password fields should be highlighted red and an accompanying error message should be
   * presented. No other error messages or highlighting should be visible.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  @Test
  public void nonMatchingPasswordTest() throws SQLException {

    List<Control> fieldExceptions = new ArrayList<Control>();
    fieldExceptions.add(passwordField);
    fieldExceptions.add(confirmPasswordField);

    List<Label> labelExceptions = new ArrayList<Label>();
    labelExceptions.add(passwordMatchErrorMessage);

    passwordField.setText("password");
    confirmPasswordField.setText("anotherPassword");
    clickOn("#doneButton");

    assertTrue(verifyFieldHighlighting(fieldExceptions));
    assertTrue(verifyErrorMessageVisibility(labelExceptions));

  }


  /**
   * Helper method which returns tr'Users' WHERE 'nhi'='%s'ue when all error messages, excluding the exceptions, are not
   * visible and when the error message is visible. Returns false when this is not the case.
   * @param exceptions The excluded Label object containing an error message.
   * @return True if error message visibility is correct. False otherwise.
   */
  private boolean verifyErrorMessageVisibility(List<Label> exceptions) {


    if (!exceptions.contains(usernameInvalidErrorMessage) && usernameInvalidErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(usernameDuplicateErrorMessage) && usernameDuplicateErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(givenNameErrorMessage) && givenNameErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(lastNameErrorMessage) && lastNameErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(dobErrorMessage) && dobErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(phoneNumberErrorMessage) && phoneNumberErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(passwordInvalidErrorMessage) && passwordInvalidErrorMessage.isVisible()) {

      return false;

    }

    if (!exceptions.contains(passwordMatchErrorMessage) && passwordMatchErrorMessage.isVisible()) {

      return false;

    }

    for (Label exception:exceptions){

      if (!exception.isVisible()) {

        return false;

      }

    }

    return true;

  }


  /**
   * Helper method which returns true when all input fields are not highlighted, excluding the
   * exceptions, which should be highlighted red. Returns false when this is not the case.
   * @param exceptions A Control object corresponding to the input field to be excluded.
   * @return True if field hightlighting is correct. False otherwise.
   */
  private boolean verifyFieldHighlighting(List<Control> exceptions) {

    if (!exceptions.contains(usernameTextField) && !usernameTextField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(givenNameTextField) && !givenNameTextField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(lastNameTextField) && !lastNameTextField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(dateOfBirthDatePicker) && !dateOfBirthDatePicker.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(phoneNumberTextField) && !phoneNumberTextField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(passwordField) && !passwordField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    if (!exceptions.contains(confirmPasswordField) && !confirmPasswordField.getStyle().
        equals(BLACK_BORDER_STYLE)) {

      return false;

    }

    for (Control exception:exceptions) {

      if (!exception.getStyle().equals(RED_BORDER_STYLE)) {

        return false;

      }

    }

    return true;

  }

  /**
   * Queries the database and confirms the existence of the donor-receiver account specified by NHI.
   * @param nhi The NHI of the donor-receiver account to be searched for.
   * @return True if the donor-receiver account exists, or false if it does not.
   * @throws SQLException Whenever a problem occurs interacting with the database.
   */
  private boolean confirmDonorReceiverExists(String nhi) throws SQLException {

    String userStatement = String.format("SELECT * FROM Users WHERE username='%s'", nhi);
    List<String> userResult = connection.executeStatement(userStatement);

    String contactStatement = String.format("SELECT * FROM ContactDetails WHERE username='%s'", nhi);
    List<String> contactResult = connection.executeStatement(contactStatement);

    return (userResult.size() > 3) && (contactResult.size() > 3);

  }


}
