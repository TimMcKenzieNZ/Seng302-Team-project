package seng302.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.ListView;
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
import org.testfx.util.WaitForAsyncUtils;
import seng302.App;
import seng302.model.AccountManager;

public class GuiMedicationsTests extends ApplicationTest {

  ListView<String> editCurrentMedications;
  ListView<String> editPreviousMedications;
  ListView<String> currentMedications;
  ListView<String> previousMedications;
  ListView<String> historyOfMedications;


  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }

  /**
   * Setup Accounts for GUI
   */
  @Before
  public void setUp() {
    AccountManager database = new AccountManager();
    database = new AccountManager();
    database.importAccounts();
    database.importClinicians();
    database.addClinicianIfNoneExists();
    database.exportClinicians();
  }


  /**
   * Load the Scene and stage of the GUI
   *
   * @param primaryStage The stage of the GUI
   * @throws IOException Thrown by the mainGUI.start function if there is an error
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    App mainGUI = new App();
    mainGUI.start(primaryStage);
    LoginController.accountManager.importClinicians();
    LoginController.accountManager.addClinicianIfNoneExists();
  }


  /**
   * Test to check whether an empty string is added as a medication
   */
  @Ignore
  @Test
  public void addBadMedication() {
    defaultLogin();
    clickOn("#Edit");
    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medications");
    clickOn("#createNewMedication");
    write("               ");
    clickOn("#addMedication");
    assertTrue(editCurrentMedications.getItems().size() == 0);
    assertTrue(editPreviousMedications.getItems().size() == 1); //Item from previous test
  }

  /**
   * Test to check whether medication is moved form current to previous
   */
  @Ignore
  @Test
  public void moveCurrentMedicationToPrevious() {
    defaultLogin();
    clickOn("#Edit");

    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medicationsTab");

    clickOn("#createNewMedication");
    write("MoveMe");
    clickOn("#addMedication");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    editCurrentMedications.getSelectionModel().select(0);
    clickOn("#moveToPrevious");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    assertTrue(editPreviousMedications.getItems().contains("MoveMe"));
    assertTrue(!editCurrentMedications.getItems().contains("MoveMe"));
    clickOn("Cancel");
  }

  /**
   * Test to check whether a duplicate medication is added when it is present in Previous
   * Medications
   */
  @Ignore
  @Test
  public void addDuplicatePreviousMedication() {
    defaultLogin();
    clickOn("#Edit");
    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medicationsTab");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    editCurrentMedications.getSelectionModel().selectFirst();
    clickOn("#moveToPrevious");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    assertTrue(editPreviousMedications.getItems().contains("Aspirin"));
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
  }

  /**
   * Test to check whether a duplicate medication is added when it is present in Current
   * Medications
   */
  @Ignore
  @Test
  public void addDuplicateCurrentMedication() {
    defaultLogin();

    clickOn("#Edit");
    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medicationsTab" +
        "");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
  }

  /**
   * Create and save
   */
  @Ignore
  @Test
  public void checkCurrentMedicationView() {
    defaultLogin();
    clickOn("#Edit");
    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medicationsTab");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    clickOn("#Done");
    clickOn("#medicationsTab");
    currentMedications = (ListView) window(0).getScene().lookup("#currentMedications");
    historyOfMedications = (ListView) window(0).getScene().lookup("#historyOfMedications");
    assertTrue(currentMedications.getItems().contains("Aspirin"));
    assertTrue(historyOfMedications.getItems().size() == 1);
  }

  @Ignore
  @Test
  public void checkPreviousMedications() {
    defaultLogin();
    clickOn("#Edit");
    editCurrentMedications = (ListView) window(0).getScene().lookup("#editCurrentMedications");
    editPreviousMedications = (ListView) window(0).getScene().lookup("#editPreviousMedications");
    clickOn("#medications");
    clickOn("#createNewMedication");
    write("Aspirin");
    clickOn("#addMedication");
    editCurrentMedications.getSelectionModel().selectFirst();
    clickOn("#moveToPrevious");
    clickOn("#Done");
    clickOn("#medicationsTab");
    currentMedications = (ListView) window(0).getScene().lookup("#currentMedications");
    ListView<String> previousMedications = (ListView) window(0).getScene()
        .lookup("#previousMedications");
    historyOfMedications = (ListView) window(0).getScene().lookup("#historyOfMedications");
    assertTrue(previousMedications.getItems().contains("Aspirin"));
    assertTrue(!currentMedications.getItems().contains("Aspirin"));
    assertTrue(historyOfMedications.getItems().size() == 2); //Added another item in previous test

  }


  /**
   * Logon as the default donor profile
   */
  public void defaultLogin() {
    clickOn("#clinicianButton");
    TextField usernameField = lookup("#usernameTextField").query();
    usernameField.setText("123");
    TextField passwordField = lookup("#passwordField").query();
    passwordField.setText("password");
    clickOn("#loginButton");

    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);

    TextField searchTextField = lookup("#searchTextField").query();
    searchTextField.setText("Sweeny Todd");
    clickOn("#searchTextField");
    clickOn("#searchButton");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(2, SECONDS);

    doubleClickOn("#accountsList");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(2, SECONDS);
    press(KeyCode.ENTER);
    doubleClickOn("#accountsList");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(4, SECONDS);

  }

  /**
   * Tear down the window after test
   */
  @After
  public void tearDown() throws TimeoutException {

    closeCurrentWindow().closeCurrentWindow();
    FxToolkit.cleanupStages();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
  }
}