package seng302.controllers;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;

/**
 * Test class for the create Clinician controller class and GUI.
 */
public class administratorCreationControllerTest extends ApplicationTest {


  /**
   * Instance of the application in order to test GUI elements.
   */
  private App mainGUI;

  //GUI elements for the login pane:
  private TextField usernameTextField;
  private Button loginButton;
  private PasswordField passwordField;

  // GUI elements for the admin view admin pane
  private Button newAdminButton;

  // GUI elements for the admin creation
  private TextField username;
  private TextField givenName;
  private TextField middleName;
  private TextField lastName;
  private PasswordField initialPasswordField;
  private PasswordField confirmPasswordField;
  private Button doneButton;


  @Override
  /**
   * Sets the GUI stage
   */
  public void start(Stage stage) throws Exception {
    mainGUI = new App();
    mainGUI.start(stage);
    // LoginController.accountManager.importAdmins();
    App.getDatabase().addDefaultAdminIfNoneExists();
  }


  @Before
  /**
   * Initialize links to the FXML of the Login Page
   */
  public void setUp() {
    clickOn("#adminButton");
    usernameTextField = lookup("#usernameTextField").query();
    clickOn(usernameTextField).write("Sudo");
    passwordField = lookup("#passwordField").query();
    clickOn(passwordField).write("001");
    loginButton = lookup("#loginButton").query();
    clickOn(loginButton);
    clickOn("#viewAdministratorButton");
  }

  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }


  /**
   * Tear down the window after test
   *
   * @throws TimeoutException If there is a timeout
   */
  @After
  public void tearDown() throws Exception {
    FxToolkit.hideStage();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
    mainGUI.stop();
  }


  /**
   * Sets up the GUI elements of the clinician creation pane for testFX tests to interact with.
   */
  private void setAdministratorPane() {
    newAdminButton = lookup("#newAdminButton").query();
  }

  private void setFields() {
    username = lookup("#usernameTextField").query();
    givenName = lookup("#givenNameTextField").query();
    middleName = lookup("#middleNameTextField").query();
    lastName = lookup("#lastNameTextField").query();
    initialPasswordField = lookup("#initialPasswordField").query();
    confirmPasswordField = lookup("#confirmPasswordField").query();
    doneButton = lookup("#doneButton").query();
  }

  @Test
  @Ignore
  public void createAdministratorInvalidUsername() {
    setAdministratorPane();
    clickOn(newAdminButton);
    setFields();

    clickOn(username).write("AAA9999");
    clickOn(givenName).write("Tester");
    clickOn(middleName).write("McTesty");
    clickOn(lastName).write("Test");
    clickOn(initialPasswordField).write("hello123");
    clickOn(confirmPasswordField).write("hello123");

    clickOn(doneButton);

    assertEquals(App.getDatabase().getAdministrators().size(), 1);
  }

  @Test
  @Ignore
  public void createAdministratorNonmatchingPasswords() {
    setAdministratorPane();
    clickOn(newAdminButton);
    setFields();

    clickOn(username).write("Test123");
    clickOn(givenName).write("Tester");
    clickOn(middleName).write("McTesty");
    clickOn(lastName).write("Test");
    clickOn(initialPasswordField).write("hello1234");
    clickOn(confirmPasswordField).write("hello123");

    clickOn(doneButton);

    assertEquals(App.getDatabase().getAdministrators().size(), 1);
  }

  @Test
  @Ignore
  public void createAdministratorInvalidName() {
    setAdministratorPane();
    clickOn(newAdminButton);
    setFields();

    clickOn(username).write("Test123");
    clickOn(givenName).write("Tester###");
    clickOn(middleName).write("McTesty");
    clickOn(lastName).write("Test");
    clickOn(initialPasswordField).write("hello123");
    clickOn(confirmPasswordField).write("hello123");

    clickOn(doneButton);

    assertEquals(App.getDatabase().getAdministrators().size(), 1);
  }

  @Test
  @Ignore //Test is broken in this version - being worked on in the Issue_8_Fix branch
  public void createAdministrator() {
    setAdministratorPane();
    clickOn(newAdminButton);
    setFields();

    clickOn(username).write("Test123");
    clickOn(givenName).write("Tester");
    clickOn(middleName).write("McTesty");
    clickOn(lastName).write("Test");
    clickOn(initialPasswordField).write("hello123");
    clickOn(confirmPasswordField).write("hello123");

    clickOn(doneButton);

    assertEquals(App.getDatabase().getAdministrators().get("Test123").getUserName(), "Test123");
  }
}