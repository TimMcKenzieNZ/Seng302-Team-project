package seng302.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.GitlabGUITestSetup;
import seng302.model.Marshal;
import seng302.model.enums.ADDRESSES;
import seng302.model.locationData.CountryList;
import seng302.model.person.DonorReceiver;
import seng302.services.SyncGetTask;


public class EditPaneTest extends ApplicationTest {

  private AccountManager accountManager;
  private DonorReceiver donorReceiver;
  private TextField usernameTextField;
  private Button loginButton;
  private TextField passwordField;
  private Button Edit;
  private Button doneButton;
  private Button cancelButton;

  //Basic information
  private TextField editGivenNames;
  private TextField editLastName;
  private TextField editNHINumber;
  private ChoiceBox editGender;
  private ChoiceBox editTitle;
  private TextField editHeight;
  private TextField editWeight;
  private ChoiceBox editBloodType;
  private CheckBox editLivedInUKFrance;
  private TextField preferredNameField;
  private ChoiceBox birthGenderChoiceBox;

  //Contact details
  private TextField editStreetAddress;
  private TextField editCity;
  private ChoiceBox editRegion;
  private TextField editPostcode;
  private TextField editMobileNumber;
  private TextField editHomeNumber;
  private TextField editEmail;
  private TextField editEmergStreetAddress;
  private TextField editEmergCity;
  private ChoiceBox editEmergRegion;
  private TextField editEmergPostcode;
  private TextField editEmergMobileNumber;
  private TextField editEmergHomeNumber;
  private TextField editEmergEmail;

  //Organs
  private CheckBox editLiver;
  private CheckBox editKidney;
  private CheckBox editLung;
  private CheckBox editHeart;
  private CheckBox editPancreas;
  private CheckBox editIntestine;
  private CheckBox editCornea;
  private CheckBox editMiddleEar;
  private CheckBox editBone;
  private CheckBox editBoneMarrow;
  private CheckBox editSkin;
  private CheckBox editConnectiveTissue;

  //Medical History
  private CheckBox editSmoker;
  private TextField editAlcoholConsumption;
  private TextField editBloodPressure;
  private TextField editChronicDiseases;

  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    //GitlabGUITestSetup.headless();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Marshal.setTestImport();
    CountryList.setTestImport();
    App mainGUI = new App();
    ADDRESSES.setLocalServer();
    mainGUI.start(stage);
  }

  /**
   * Initialize links to the FXML
   */
  @Before
  public void setUp() {
    accountManager = App.getDatabase();
    usernameTextField = lookup("#usernameTextField").query();
    passwordField = lookup("#passwordField").query();
    loginButton = lookup("#loginButton").query();
  }

  /**
   * Sets up the buttons in the test with the expected values (won't cause a null pointer if the
   * element is not on the page)
   */
  private void setEditPane() {
    editGivenNames = lookup("#editGivenNames").query();
    editLastName = lookup("#editLastName").query();
    preferredNameField = lookup("#editPreferredName").query();
    editNHINumber = lookup("#editNHINumber").query();
    editGender = lookup("#editGender").query();
    birthGenderChoiceBox = lookup("#editBirthGender").query();
    editTitle = lookup("#editTitle").query();
    editHeight = lookup("#editHeight").query();
    editWeight = lookup("#editWeight").query();
    editBloodType = lookup("#editBloodType").query();
    editLivedInUKFrance = lookup("#editLivedInUKFrance").query();

    editStreetAddress = lookup("#editStreetAddress").query();
    editCity = lookup("#editCity").query();
    editRegion = lookup("#editRegion").query();
    editPostcode = lookup("#editPostcode").query();
    editMobileNumber = lookup("#editMobileNumber").query();
    editHomeNumber = lookup("#editHomeNumber").query();
    editEmail = lookup("#editEmail").query();
    editEmergStreetAddress = lookup("#editEmergStreetAddress").query();
    editEmergCity = lookup("#editEmergCity").query();
    editEmergRegion = lookup("#editEmergRegion").query();
    editEmergPostcode = lookup("#editEmergPostcode").query();
    editEmergMobileNumber = lookup("#editEmergMobileNumber").query();
    editEmergHomeNumber = lookup("#editEmergHomeNumber").query();
    editEmergEmail = lookup("#editEmergEmail").query();

    editLiver = lookup("#editLiver").query();
    editKidney = lookup("#editKidney").query();
    editLung = lookup("#editLung").query();
    editHeart = lookup("#editHeart").query();
    editPancreas = lookup("#editPancreas").query();
    editIntestine = lookup("#editIntestine").query();
    editCornea = lookup("#editCornea").query();
    editMiddleEar = lookup("#editMiddleEar").query();
    editBone = lookup("#editBone").query();
    editBoneMarrow = lookup("#editBoneMarrow").query();
    editSkin = lookup("#editSkin").query();
    editConnectiveTissue = lookup("#editConnectiveTissue").query();

    editSmoker = lookup("#editSmoker").query();
    editAlcoholConsumption = lookup("#editAlcoholConsumption").query();
    editBloodPressure = lookup("#editBloodPressure").query();
    editChronicDiseases = lookup("#editChronicDiseases").query();

    doneButton = lookup("#Done").query();
    cancelButton = lookup("#Cancel").query();
  }


  /**
   * Sets the fields of the edit controller to test the editing of the donor (will cause a null
   * pointer exception if the element isn't on the page)
   */
  private void setFields() {
    editGivenNames.clear();
    clickOn(editGivenNames).write("Steve Paul");
    editLastName.setText("");
    clickOn(editLastName).write("Jobs");
    preferredNameField.clear();
    clickOn(preferredNameField).write("Stephanie");
    editNHINumber.setText("");
    clickOn(birthGenderChoiceBox).clickOn("Male");
    WaitForAsyncUtils.waitForFxEvents();
    sleep(2, SECONDS);
    clickOn(editGender);
    type(KeyCode.DOWN);
    type(KeyCode.ENTER);
    clickOn(editTitle);
    type(KeyCode.DOWN);
    type(KeyCode.ENTER);
    editHeight.setText("1.82");
    editWeight.setText("70");
    clickOn(editBloodType).clickOn("O+");
    editLivedInUKFrance.setSelected(true);

    editStreetAddress.setText("23 Apple Lane");
    editCity.setText("San Francisco");
    editRegion.getSelectionModel().getSelectedItem();
    editPostcode.setText("8456");
    editMobileNumber.setText("0278383838");
    editHomeNumber.setText("0800838383");
    editEmail.setText("steve.jobs@windows.com");
    editEmergStreetAddress.setText("23 Window Lane");
    editEmergCity.setText("New York");
    editEmergRegion.getSelectionModel().getSelectedItem();
    editEmergPostcode.setText("6854");
    editEmergMobileNumber.setText("0228383838");
    editEmergHomeNumber.setText("0800838383");
    editEmergEmail.setText("Bill.Gates@apple.com");

    editLiver.setSelected(false);
    editKidney.setSelected(true);
    editHeart.setSelected(false);
    editLung.setSelected(true);
    editIntestine.setSelected(true);
    editCornea.setSelected(false);
    editMiddleEar.setSelected(true);
    editSkin.setSelected(false);
    editBone.setSelected(true);
    editBoneMarrow.setSelected(false);
    editConnectiveTissue.setSelected(false);
    editPancreas.setSelected(false);

    editSmoker.setSelected(true);
    editAlcoholConsumption.setText("3.4");
    editBloodPressure.setText("2.0/3.5");
    editChronicDiseases.setText("Cancer");
  }

  /**
   * Sets the fields of the edit controller to test the editing of the donor (will cause a null
   * pointer exception if the element isn't on the page)
   */
  private void undoEdit() {
    donorReceiver.setFirstName("Sweeny");
    donorReceiver.setMiddleName("");
    donorReceiver.setLastName("Todd");
    donorReceiver.setPreferredName("");
    donorReceiver.setUserName("ABC1234");
    donorReceiver.setTitle("MR");
    donorReceiver.setGender('M');
    donorReceiver.getUserAttributeCollection().setHeight(2.0);
    donorReceiver.getUserAttributeCollection().setWeight(51.0);
    donorReceiver.getUserAttributeCollection().setBloodType("AB-");
    donorReceiver.getContactDetails().getAddress().updateStreetAddressLineOne("1 Fleet Street");
    donorReceiver.getContactDetails().getAddress().updateCityName("Christchurch");
    donorReceiver.getContactDetails().getAddress().updateRegion("Canterbury");
    donorReceiver.getContactDetails().getAddress().updatePostCode("5678");
    donorReceiver.getContactDetails().updateMobileNum("0220456543");
    donorReceiver.getContactDetails().updateHomeNum("5452345");
    donorReceiver.getContactDetails().updateEmail("randomPerson92@gmail.com");
    donorReceiver.getEmergencyContactDetails().getAddress()
        .updateStreetAddressLineOne("31b Taylors Ave");
    donorReceiver.getEmergencyContactDetails().getAddress().updateCityName("Christchurch");
    donorReceiver.getEmergencyContactDetails().getAddress().updateRegion("Canterbury");
    donorReceiver.getEmergencyContactDetails().getAddress().updatePostCode("8052");
    donorReceiver.getEmergencyContactDetails().updateMobileNum("0213459876");
    donorReceiver.getEmergencyContactDetails().updateHomeNum("5458769");
    donorReceiver.getEmergencyContactDetails().updateEmail("randomHelper93@yahoo.com");
    donorReceiver.getDonorOrganInventory().setLiver(true);
    donorReceiver.getDonorOrganInventory().setKidneys(false);
    donorReceiver.getDonorOrganInventory().setHeart(true);
    donorReceiver.getDonorOrganInventory().setLungs(false);
    donorReceiver.getDonorOrganInventory().setIntestine(false);
    donorReceiver.getDonorOrganInventory().setCorneas(true);
    donorReceiver.getDonorOrganInventory().setMiddleEars(false);
    donorReceiver.getDonorOrganInventory().setSkin(true);
    donorReceiver.getDonorOrganInventory().setBone(false);
    donorReceiver.getDonorOrganInventory().setBoneMarrow(true);
    donorReceiver.getDonorOrganInventory().setConnectiveTissue(true);
    donorReceiver.getDonorOrganInventory().setPancreas(true);
    donorReceiver.getUserAttributeCollection().setSmoker(false);
    donorReceiver.getUserAttributeCollection().setAlcoholConsumption(2.0);
    donorReceiver.getUserAttributeCollection().setBloodPressure("10/10");
    donorReceiver.getUserAttributeCollection().setChronicDiseases("None");
  }

  private void getAccountSave(ArrayList<DonorReceiver> donorReceivers) {
    for (DonorReceiver donorReceiver2 : donorReceivers) {
      if (donorReceiver2.getUserName().equals("ASD9876")) {
        donorReceiver = donorReceiver2;
        break;
      }
    }
  }


  private void getAccountClose(ArrayList<DonorReceiver> donorReceivers) {
    for (DonorReceiver donorReceiver2 : donorReceivers) {
      if (donorReceiver2.getUserName().equals("ABC1234")) {
        donorReceiver = donorReceiver2;
        break;
      }
    }
  }


  /**
   * Tests the expected behaviour of the done button The user makes changes, presses done, and the
   * changes are applied to the donor (seen through changes in the view pane of the donor).
   */
  @Test
  @Ignore
  //Ignored as it causes other tests to fail - when the NHI number is changed it changes the NHI number of
  // the user so the original donor (the ones the other tests are using) does not exist changing this
  // in this test would require changing a number of fields in the test.
  public void editProfileClickDone() {
//    clickOn(usernameTextField).write("ABC1234");
//    clickOn(passwordField).write("password");
//    clickOn(loginButton);
//    Edit = lookup("#Edit").query();
//    clickOn(Edit);

    usernameTextField.setText("ABC1234");
    passwordField.setText("password");
    clickOn(loginButton);
    sleep(2000);
    Edit = lookup("#Edit").query();
    clickOn(Edit);
    setEditPane();
    setFields();
    clickOn(doneButton);

    ArrayList<DonorReceiver> list = accountManager.getDonorReceiversArrayList();
    getAccountSave(list);
    assertEquals("Steve", donorReceiver.getFirstName());
    assertEquals("Paul", donorReceiver.getMiddleName());
    assertEquals("Jobs", donorReceiver.getLastName());
    assertEquals("Stephanie", donorReceiver.getPreferredName());
    assertEquals("ABC1234", donorReceiver.getUserName());
    assertEquals("Female", donorReceiver.genderString());
    assertEquals("Male", donorReceiver.birthGenderString());
    assertEquals("MRS", donorReceiver.getTitle());
    double weight = donorReceiver.getUserAttributeCollection().getWeight();
    String weight2 = Double.toString(weight);
    double height = donorReceiver.getUserAttributeCollection().getHeight();
    String height2 = Double.toString(height);
    assertEquals("1.82", height2);
    assertEquals("70.0", weight2);
    assertEquals("O+", donorReceiver.getUserAttributeCollection().getBloodType());
    assertTrue(donorReceiver.getLivedInUKFlag());

    assertEquals("23 Apple Lane",
        donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne());
    assertEquals("San Francisco", donorReceiver.getContactDetails().getAddress().getCityName());
    assertEquals("California", donorReceiver.getContactDetails().getAddress().getRegion());
    assertEquals("8456", donorReceiver.getContactDetails().getAddress().getPostCode());
    assertEquals("0278383838", donorReceiver.getContactDetails().getMobileNum());
    assertEquals("0800838383", donorReceiver.getContactDetails().getHomeNum());
    assertEquals("steve.jobs@windows.com", donorReceiver.getContactDetails().getEmail());
    assertEquals("23 Window Lane",
        donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLineOne());
    assertEquals("New York", donorReceiver.getEmergencyContactDetails().getAddress().getCityName());
    assertEquals("New York", donorReceiver.getEmergencyContactDetails().getAddress().getRegion());
    assertEquals("6854", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode());
    assertEquals("0228383838", donorReceiver.getEmergencyContactDetails().getMobileNum());
    assertEquals("0800838383", donorReceiver.getEmergencyContactDetails().getHomeNum());
    assertEquals("Bill.Gates@apple.com", donorReceiver.getEmergencyContactDetails().getEmail());

    assertFalse(donorReceiver.getDonorOrganInventory().getLiver());
    assertTrue(donorReceiver.getDonorOrganInventory().getKidneys());
    assertFalse(donorReceiver.getDonorOrganInventory().getHeart());
    assertTrue(donorReceiver.getDonorOrganInventory().getLungs());
    assertTrue(donorReceiver.getDonorOrganInventory().getIntestine());
    assertFalse(donorReceiver.getDonorOrganInventory().getCorneas());
    assertTrue(donorReceiver.getDonorOrganInventory().getMiddleEars());
    assertFalse(donorReceiver.getDonorOrganInventory().getSkin());
    assertTrue(donorReceiver.getDonorOrganInventory().getBone());
    assertFalse(donorReceiver.getDonorOrganInventory().getBoneMarrow());
    assertFalse(donorReceiver.getDonorOrganInventory().getConnectiveTissue());
    assertFalse(donorReceiver.getDonorOrganInventory().getPancreas());

    assertEquals(true, donorReceiver.getUserAttributeCollection().getSmoker());
    double alcohol = donorReceiver.getUserAttributeCollection().getAlcoholConsumption();
    String alcohol2 = Double.toString(alcohol);
    assertEquals("3.4", alcohol2);
    assertEquals("2.0/3.5", donorReceiver.getUserAttributeCollection().getBloodPressure());
    assertEquals("Cancer", donorReceiver.getUserAttributeCollection().getChronicDiseases());
    undoEdit();
  }


  /**
   * Checks the expected behaviour of the edit pane when the cancel button is pressed (validated by
   * no changes occurring in the view pane for the controller).
   */
  @Test
  @Ignore
  public void editProfileClickCancel() {
    usernameTextField.setText("ABC1234");
    passwordField.setText("password");
    clickOn(loginButton);
    sleep(2000);
    Edit = lookup("#Edit").query();
    clickOn(Edit);
    setEditPane();
    setFields();
    clickOn(cancelButton);
    SyncGetTask syncGetTask = new SyncGetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR.getAddress() + "ABC1234", AccountManager.getToken());
    DonorReceiver donorReceiver;
    try {
      syncGetTask.makeRequest();
      donorReceiver = (DonorReceiver) syncGetTask.responseBody;
      assertEquals("Sweeny", donorReceiver.getFirstName());
      assertEquals("", donorReceiver.getMiddleName());
      assertEquals("Todd", donorReceiver.getLastName());
      assertEquals("ABC1234", donorReceiver.getUserName());
      assertEquals("Male", donorReceiver.genderString());
      assertEquals("MR", donorReceiver.getTitle());

      double weight = donorReceiver.getUserAttributeCollection().getWeight();
      String weight2 = Double.toString(weight);
      double height = donorReceiver.getUserAttributeCollection().getHeight();
      String height2 = Double.toString(height);
      assertEquals("2.0", height2);
      assertEquals("51.0", weight2);
      assertEquals("AB-", donorReceiver.getUserAttributeCollection().getBloodType());

      assertEquals("1 Fleet Street",
          donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne());
      assertEquals("", donorReceiver.getContactDetails().getAddress().getStreetAddressLineTwo());
      assertEquals("Christchurch", donorReceiver.getContactDetails().getAddress().getCityName());
      assertEquals("Canterbury", donorReceiver.getContactDetails().getAddress().getRegion());
      assertEquals("5678", donorReceiver.getContactDetails().getAddress().getPostCode());
      assertEquals("0220456543", donorReceiver.getContactDetails().getMobileNum());
      assertEquals("5452345", donorReceiver.getContactDetails().getHomeNum());
      assertEquals("randomPerson92@gmail.com", donorReceiver.getContactDetails().getEmail());

      assertEquals("31b Taylors Ave",
          donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLineOne());
      assertEquals("Christchurch",
          donorReceiver.getEmergencyContactDetails().getAddress().getCityName());
      assertEquals("Canterbury", donorReceiver.getEmergencyContactDetails().getAddress().getRegion());
      assertEquals("8052", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode());
      assertEquals("0213459876", donorReceiver.getEmergencyContactDetails().getMobileNum());
      assertEquals("5458769", donorReceiver.getEmergencyContactDetails().getHomeNum());
      assertEquals("randomHelper93@yahoo.com", donorReceiver.getEmergencyContactDetails().getEmail());

      assertTrue(donorReceiver.getDonorOrganInventory().getLiver());
      assertFalse(donorReceiver.getDonorOrganInventory().getKidneys());
      assertTrue(donorReceiver.getDonorOrganInventory().getHeart());
      assertFalse(donorReceiver.getDonorOrganInventory().getLungs());
      assertFalse(donorReceiver.getDonorOrganInventory().getIntestine());
      assertTrue(donorReceiver.getDonorOrganInventory().getCorneas());
      assertFalse(donorReceiver.getDonorOrganInventory().getMiddleEars());
      assertTrue(donorReceiver.getDonorOrganInventory().getSkin());
      assertFalse(donorReceiver.getDonorOrganInventory().getBone());
      assertTrue(donorReceiver.getDonorOrganInventory().getBoneMarrow());
      assertTrue(donorReceiver.getDonorOrganInventory().getConnectiveTissue());
      assertTrue(donorReceiver.getDonorOrganInventory().getPancreas());

      assertFalse(donorReceiver.getRequiredOrgans().getLiver());
      assertFalse(donorReceiver.getRequiredOrgans().getKidneys());
      assertFalse(donorReceiver.getRequiredOrgans().getHeart());
      assertFalse(donorReceiver.getRequiredOrgans().getLungs());
      assertFalse(donorReceiver.getRequiredOrgans().getIntestine());
      assertFalse(donorReceiver.getRequiredOrgans().getCorneas());
      assertFalse(donorReceiver.getRequiredOrgans().getMiddleEars());
      assertFalse(donorReceiver.getRequiredOrgans().getSkin());
      assertFalse(donorReceiver.getRequiredOrgans().getBone());
      assertFalse(donorReceiver.getRequiredOrgans().getBoneMarrow());
      assertFalse(donorReceiver.getRequiredOrgans().getConnectiveTissue());
      assertFalse(donorReceiver.getRequiredOrgans().getPancreas());

      assertEquals(false, donorReceiver.getUserAttributeCollection().getSmoker());
      double alcohol = donorReceiver.getUserAttributeCollection().getAlcoholConsumption();
      String alcohol2 = Double.toString(alcohol);
      assertEquals("2.0", alcohol2);
      assertEquals("10/10", donorReceiver.getUserAttributeCollection().getBloodPressure());
      assertEquals("None", donorReceiver.getUserAttributeCollection().getChronicDiseases());
    }
    catch (HttpClientErrorException e) {
      System.out.println("Cannot get donor to run test :(");
    }
//    accountManager = App.getDatabase();
//    ArrayList<DonorReceiver> list = accountManager.getDonorReceiversArrayList();
//    getAccountClose(list);
  }


  /**
   * Verifies that the status bar displays a message notifying the user of an account modification
   * after changes are made to donor ABC1234.
   */
  @Test
  @Ignore
  public void verifyStatusBarIsUpdated() {
    toggleLivedInUKFrance();
    clickOn(doneButton);
    // From the status bar.
    Label actionLabel = lookup("#actionLabel").query();
    assertEquals("Sweeny Todd (NHI: ABC1234) modified.", actionLabel.getText());
  }


  /**
   * Verifies that the status indicator (an asterisk in the title of the main window) appears after
   * changes are made to donor ABC1234.
   */
  @Test
  @Ignore
  public void verifyUnsavedIndicatorAppearsAfterChanges() {
    toggleLivedInUKFrance();
    clickOn(doneButton);
    String title = App.getWindow().getTitle();
    assertEquals("SapioCulture*", title);
  }


  /**
   * Verifies that the unsaved indicator (an asterisk in the title of the main window) does not
   * appear when no changes are made to donor.
   */
  @Test
  @Ignore
  public void verifyUnsavedIndicatorDoesNotAppearAfterNoChanges() {
    clickOn(usernameTextField).write("ABC1234");
    clickOn(passwordField).write("password");
    clickOn(loginButton);
    Edit = lookup("#Edit").query();
    clickOn(Edit);
    setEditPane();
    clickOn(doneButton);
    String title = App.getWindow().getTitle();
    assertEquals("SapioCulture", title);
  }


  /**
   * Verifies that the unsaved indicator (an asterisk in the title of the main window) does not
   * appear when changes made to the donor are cancelled.
   */
  @Test
  @Ignore
  public void verifyUnsavedIndicatorDoesNotAppearAfterCancel() {
    toggleLivedInUKFrance();
    clickOn(cancelButton);
    String title = App.getWindow().getTitle();
    assertEquals("SapioCulture", title);
  }


  /**
   * Logs in using donor profile ABC1234, switches to the edit pane, toggles the boolean variable
   * editLivedInUKFrance, and clicks the done button. This is a helper method for tests that examine
   * post-change actions.
   */
  private void toggleLivedInUKFrance() {
    // Login.
    usernameTextField.setText("ABC1234");
    passwordField.setText("password");
    clickOn(loginButton);
    sleep(2000);
    // Find and open edit pane.
    Edit = lookup("#Edit").query();
    clickOn(Edit);
    setEditPane();
    // Change smoker and save.
    clickOn(editLivedInUKFrance);
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
    // mainGUI.stop();
  }
}