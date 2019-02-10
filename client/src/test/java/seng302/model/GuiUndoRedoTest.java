package seng302.model;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import java.io.IOException;
import java.time.Year;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import seng302.controllers.LoginController;


public class GuiUndoRedoTest extends ApplicationTest {

  private static AccountManager Database;
  Stage stage;
  private App mainGUI;

  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }

  /**
   * Load the Scene and stage of the GUI
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    mainGUI = new App();
    mainGUI.start(primaryStage);
    stage = primaryStage;
    LoginController.accountManager.importClinicians();
    LoginController.accountManager.addClinicianIfNoneExists();


  }


  /**
   * Setup Accounts for GUI
   */
  @Before
  public void setUp() {
    Database = new AccountManager();
    Database.importAccounts();
    Database.importClinicians();
    Database.addClinicianIfNoneExists();
    Database.exportClinicians();
  }


  @After
  public void tearDown() throws Exception {
    FxToolkit.cleanupStages();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
  }

  /**
   * Tests the undo and redo functionality on TextFields
   */
  @Test
  @Ignore
  public void testUndoTextField() {
    loginDefault();
    clickOn("#Edit");
    clickOn("#editGivenNames");
    write("test");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    verifyThat("#editGivenNames", hasText("te"));
    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    verifyThat("#editGivenNames", hasText("test"));
  }


  /**
   * Tests the undo and redo functionality on CheckBoxes
   */
  @Ignore
  @Test
  public void testUndoRedoCheckBoxes() {
    loginDefault();
    clickOn("#Edit");
    clickOn("#organTab");
    clickOn("#editKidney");
    clickOn("#editHeart");
    clickOn("#editCornea");
    clickOn("#editSkin");
    CheckBox editKidney = (CheckBox) stage.getScene().lookup("#editKidney");
    CheckBox editHeart = (CheckBox) stage.getScene().lookup("#editHeart");
    CheckBox editCornea = (CheckBox) stage.getScene().lookup("#editCornea");
    CheckBox editSkin = (CheckBox) stage.getScene().lookup("#editSkin");
    assertTrue(editKidney.isSelected());
    assertTrue(!editHeart.isSelected());
    assertTrue(!editCornea.isSelected());
    assertTrue(!editSkin.isSelected());

    clickOn("#file");
    clickOn("#undoMenu");
    assertTrue(editKidney.isSelected());
    assertTrue(!editHeart.isSelected());
    assertTrue(!editCornea.isSelected());
    assertTrue(editSkin.isSelected());

    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertTrue(editKidney.isSelected());
    assertTrue(editHeart.isSelected());
    assertTrue(editCornea.isSelected());
    assertTrue(editSkin.isSelected());

  }


  /**
   * Tests the undo and redo functionality on DatePickers
   */
  @Ignore
  @Test
  public void testUndoRedoDatePicker() {
    loginDefault();
    clickOn("#Edit");
    DatePicker editDateOfBirth = (DatePicker) stage.getScene().lookup("#editDateOfBirth");

    //editDateOfBirth.setValue(inputDate);
    editDateOfBirth.setValue(null);
    clickOn("#editDateOfBirth");
    clickOn("#editDateOfBirth").write("13/04/2018");
    clickOn("#editDateOfDeath");

    assertEquals(Year.of(2018).atMonth(4).atDay(13), editDateOfBirth.valueProperty().getValue());
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(editDateOfBirth.valueProperty().getValue(), null);
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(Year.of(2018).atMonth(4).atDay(13), editDateOfBirth.valueProperty().getValue());
  }

  /**
   * Tests the undo and redo functionality on ChoiceBoxes
   */
  @Ignore
  @Test
  public void testUndoRedoChoiceBox() {
    loginDefault();
    clickOn("#Edit");
    clickOn("#editTitle");
    press(KeyCode.DOWN);
    press(KeyCode.ENTER);
    ChoiceBox editTitle = (ChoiceBox) stage.getScene().lookup("#editTitle");
    assertEquals(editTitle.valueProperty().getValue(), "Mrs");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(editTitle.valueProperty().getValue(), "Mr");
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(editTitle.valueProperty().getValue(), "Mrs");
  }

  /**
   * Tests the functionality for undo and redo using multiple types of components
   */
  @Test
  @Ignore
  public void checkAllComponentsUndoRedo() {
    loginDefault();

    clickOn("#Edit");

    CheckBox editLivedInUKFrance = (CheckBox) stage.getScene().lookup("#editLivedInUKFrance");
    ChoiceBox editTitle = (ChoiceBox) stage.getScene().lookup("#editTitle");
    TextField editHeight = (TextField) stage.getScene().lookup("#editHeight");
    DatePicker editDateOfBirth = (DatePicker) stage.getScene().lookup("#editDateOfBirth");

    editDateOfBirth.setValue(null);
    clickOn("#editDateOfBirth");
    clickOn("#editDateOfBirth").write("13/05/2005");
    clickOn("#editDateOfDeath");

    clickOn("#editLivedInUKFrance");

    editHeight.setText("");
    clickOn("#editHeight");
    clickOn("#editHeight").write("100");
    clickOn("#editDateOfDeath"); // Just to confirm the edit

    clickOn("#editTitle").clickOn("Ms");
    clickOn();
    assertEquals(editDateOfBirth.valueProperty().getValue(), Year.of(2005).atMonth(5).atDay(13));
    assertEquals(editTitle.valueProperty().getValue(), "Ms");
    verifyThat(editHeight, hasText("100"));

    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(editDateOfBirth.valueProperty().getValue(), Year.of(2005).atMonth(5).atDay(13));
    assertEquals(editTitle.valueProperty().getValue(), "Mr");
    assertTrue(editLivedInUKFrance.isSelected());
    assertEquals(editHeight.textProperty().get(), "");

    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(editDateOfBirth.valueProperty().getValue(), Year.of(2005).atMonth(5).atDay(13));
    assertEquals(editTitle.valueProperty().getValue(), "Mr");
    assertTrue(editLivedInUKFrance.isSelected());
    assertEquals(editHeight.textProperty().get(), "2.0");

    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(editDateOfBirth.valueProperty().getValue(), Year.of(2005).atMonth(5).atDay(13));
    assertEquals(editTitle.valueProperty().getValue(), "Mr");
    assertTrue(editLivedInUKFrance.isSelected());
    assertEquals(editHeight.textProperty().get(), "100");

    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(editDateOfBirth.valueProperty().getValue(), Year.of(2005).atMonth(5).atDay(13));
    assertEquals(editTitle.valueProperty().getValue(), "Ms");
    assertTrue(editLivedInUKFrance.isSelected());
    assertEquals(editHeight.textProperty().get(), "100");


  }


  @Test
  @Ignore
  public void checkMedicationAddUndoRedo() {
    loginDefault();
    clickOn("#Edit");
    clickOn("#medicationsTab");
    clickOn("#createNewMedication");
    write("Aspirin");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    ListView editCurrentMedications = (ListView) stage.getScene().lookup("#editCurrentMedications");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    clickOn("#undoMenu");
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    clickOn("#redoMenu");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
  }

  /**
   * Add medication and check undo and redo functionality
   */
  @Test
  @Ignore
  public void checkMedicationRemoveUndoRedo() {
    defaultClinicianLogin();
    clickOn("#Edit");
    clickOn("#medicationsTab");
    clickOn("#createNewMedication");

    write("Medication1");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    clickOn("#createNewMedication");
    write("Medication2");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    clickOn("#createNewMedication");
    write("Aspirin");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    ListView editCurrentMedications = (ListView) targetWindow().getScene()
        .lookup("#editCurrentMedications");
    sleep(1, SECONDS);
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#editCurrentMedications").clickOn("Aspirin");
    clickOn("#removeMedication");
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    sleep(1, SECONDS);
    clickOn("#undoMenu");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    sleep(1, SECONDS);
    clickOn("#redoMenu");
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
  }

  /**
   * Move medication and check undo and redo fucntionality
   */
  @Ignore
  @Test
  public void checkMedicationMoveUndoRedo() {
    defaultClinicianLogin();
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#Edit");
    clickOn("#medicationsTab");
    clickOn("#createNewMedication");
    write("Medication1");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    clickOn("#createNewMedication");
    write("Medication2");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    clickOn("#createNewMedication");
    write("Aspirin");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    clickOn("#addMedication");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1, SECONDS);
    ListView editCurrentMedications = (ListView) stage.getScene().lookup("#editCurrentMedications");
    ListView editPreviousMedications = (ListView) stage.getScene()
        .lookup("#editPreviousMedications");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    clickOn("#editCurrentMedications").clickOn("Aspirin");
    clickOn("#moveToPrevious");
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
    assertTrue(editPreviousMedications.getItems().contains("Aspirin"));
    clickOn("#editPreviousMedications").clickOn("Aspirin");
    clickOn("#moveToCurrent");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    assertTrue(!editPreviousMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    clickOn("#redoMenu");
    assertTrue(!editCurrentMedications.getItems().contains("Aspirin"));
    assertTrue(editPreviousMedications.getItems().contains("Aspirin"));
    clickOn("#file");
    clickOn("#redoMenu");
    assertTrue(editCurrentMedications.getItems().contains("Aspirin"));
    assertTrue(!editPreviousMedications.getItems().contains("Aspirin"));
  }

  /**
   * Test the undo and reod functionality on the componenets when creating a user
   */
  @Ignore
  @Test
  public void createUserUndoRedo() {
    clickOn("#createDonorButton");
    clickOn("#dateOfBirthDatePicker");
    write("10/10/2000");
    press(KeyCode.ENTER);
    DatePicker dateOfBirthDatePicker = (DatePicker) stage.getScene()
        .lookup("#dateOfBirthDatePicker");
    TextField lastNameTextField = (TextField) stage.getScene().lookup("#lastNameTextField");
    TextField givenNameTextField = (TextField) stage.getScene().lookup("#givenNameTextField");
    assertEquals(dateOfBirthDatePicker.valueProperty().getValue(),
        Year.of(2000).atMonth(10).atDay(10));
    clickOn("#lastNameTextField");
    write("test");
    clickOn("#givenNameTextField");
    write("test");
    press(KeyCode.CONTROL,
        KeyCode.Z); //User can undo and redo using control z and control y when in textfields
    clickOn("#givenNameTextField");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(givenNameTextField.getText(), "");
    assertEquals(lastNameTextField.getText(), "test");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(givenNameTextField.getText(), "");
    assertEquals(lastNameTextField.getText(), "");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(dateOfBirthDatePicker.valueProperty().getValue(), null);
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(dateOfBirthDatePicker.valueProperty().getValue(),
        Year.of(2000).atMonth(10).atDay(10));
  }

  /**
   * Test the undo and redo functionality of componenets when creating a clinician
   */
  @Ignore
  @Test
  public void createClinicianUndoRedo() {
    defaultClinicianLogin();
    clickOn("#createClinicianButton");
    clickOn("#regionComboBox").clickOn("Taranaki");
    clickOn("#regionComboBox").clickOn("Waikato");
    clickOn("#file");
    clickOn("#undoMenu");
    ComboBox regionComboBox = (ComboBox) stage.getScene().lookup("#regionComboBox");
    TextField streetAddressTextField = (TextField) stage.getScene()
        .lookup("#streetAddressTextField");

    assertEquals(regionComboBox.valueProperty().getValue(), "Taranaki");
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(regionComboBox.valueProperty().getValue(), "Waikato");
    clickOn("#streetAddressTextField");
    write("1");
    clickOn("#file");
    clickOn("#redoMenu"); // Check nothing unexpected happens
    verifyThat("#streetAddressTextField", hasText("1"));
    clickOn("#file");
    clickOn("#undoMenu");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(streetAddressTextField.getText(), "");
    assertEquals(regionComboBox.valueProperty().getValue(), "Taranaki");
    clickOn("#file");
    clickOn("#redoMenu");
    clickOn("#file");
    clickOn("#redoMenu");
    assertEquals(streetAddressTextField.getText(), "1");
    assertEquals(regionComboBox.valueProperty().getValue(), "Waikato");
  }

  /**
   * Test the undo and redo functionality of components when editin a clinician
   */
  @Ignore
  @Test
  public void editClinicianUndoRedo() {
    defaultClinicianLogin();
    clickOn("#viewEditClinicianButton");
    clickOn("#editButton");
    clickOn("#regionComboBox").clickOn("Taranaki");
    clickOn("#regionComboBox").clickOn("Waikato");
    clickOn("#file");
    clickOn("#undoMenu");
    ComboBox regionComboBox = (ComboBox) stage.getScene().lookup("#regionComboBox");
    TextField clinicianProfileNameText = (TextField) stage.getScene()
        .lookup("#clinicianProfileNameText");
    assertEquals(regionComboBox.valueProperty().getValue(), "Taranaki");
    clickOn("#clinicianProfileNameText");
    press(KeyCode.CONTROL, KeyCode.A);
    write("2");
    clickOn("#file");
    clickOn("#redoMenu"); //Nothing should happen
    assertEquals(regionComboBox.valueProperty().getValue(), "Taranaki");
    assertEquals(clinicianProfileNameText.getText(), "2");
    clickOn("#regionComboBox").clickOn("Auckland");
    clickOn("#file");
    clickOn("#undoMenu");
    assertEquals(regionComboBox.valueProperty().getValue(), "Taranaki");
    assertEquals(clinicianProfileNameText.getText(), "2");


  }

  /**
   * Log into the default user
   */
  public void loginDefault() {
    clickOn("#usernameTextField");
    write("ABC1234");
    clickOn("#loginButton");
  }

  /**
   * Logon as the default donor profile
   */
  public void defaultClinicianLogin() {
    clickOn("#clinicianButton");
    TextField usernameTextField = lookup("#usernameTextField").query();
    clickOn(usernameTextField).write("3");
    Button loginButton = lookup("#loginButton").query();
    clickOn(loginButton);
  }

}
