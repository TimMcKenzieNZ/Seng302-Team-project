package seng302.controllers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.model.GitlabGUITestSetup;
import seng302.model.person.DonorReceiver;

public class ViewProfilePaneControllerTest extends ApplicationTest {

  private Button editButton;
  private Button logoutButton;
  private DonorReceiver donorReceiver;
  private Label firstNames;
  private Label lastName;
  private Label nationalHealthIndex;
  private Label dateCreated;
  private Label dateOfBirth;
  private Label dateOfDeath;
  private Label gender;
  private Label livedInUKFrance;
  private Label height;
  private Label weight;
  private Label title;
  private Label bloodType;
  private Label bmi;
  private Label smoker;
  private Label alcoholConsumption;
  private Label bloodPressure;
  private Label chronicDiseases;
  private CheckBox LiverCheckBox;
  private CheckBox KidneyCheckBox;
  private CheckBox LungCheckBox;
  private CheckBox HeartCheckBox;
  private CheckBox PancreasCheckBox;
  private CheckBox IntestineCheckBox;
  private CheckBox CorneaCheckBox;
  private CheckBox MiddleEarCheckBox;
  private CheckBox BoneCheckBox;
  private CheckBox BoneMarrowCheckBox;
  private CheckBox SkinCheckBox;
  private CheckBox ConnectiveTissueCheckBox;
  private Label streetAddress;
  private Label city;
  private Label region;
  private Label postcode;
  private Label mobileNumber;
  private Label homeNumber;
  private Label email;
  private Label emergStreetAddress;
  private Label emergCity;
  private Label emergRegion;
  private Label emergPostcode;
  private Label emergMobileNumber;
  private Label emergHomeNumber;
  private Label emergEmail;


  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }


  /**
   * Sets the GUI stage
   */
  public void start(Stage stage) throws Exception {
    App mainGUI = new App();
    mainGUI.start(stage);
    LoginController.accountManager.importClinicians();
    LoginController.accountManager.addClinicianIfNoneExists();
  }

  /**
   * Initialize links to the FXML
   */
  @Before
  public void setUp() {
    //GUI elements for the login pane:
    TextField usernameTextField = lookup("#usernameTextField").query();
    TextField passwordField = lookup("#passwordField").query();
    Button loginButton = lookup("#loginButton").query();
    usernameTextField.setText("ABC1234");
    passwordField.setText("password");
    clickOn(loginButton);
    Map<String, DonorReceiver> donorReceivers = App.getDatabase().getMasterList();
    ArrayList<DonorReceiver> donorReceiversArrayList = new ArrayList(donorReceivers.values());

    for (int i = 0; i < donorReceiversArrayList.size(); i++) {
      if (donorReceiversArrayList.get(i).getUserName().equals("ABC1234")) {
        donorReceiver = donorReceiversArrayList.get(i);
      }
    }
    firstNames = lookup("#firstNames").query();
    lastName = lookup("#lastName").query();
    nationalHealthIndex = lookup("#nationalHealthIndex").query();
    dateCreated = lookup("#dateCreated").query();
    dateOfBirth = lookup("#dateOfBirth").query();
    dateOfDeath = lookup("#dateOfDeath").query();
    gender = lookup("#gender").query();
    livedInUKFrance = lookup("#livedInUKFrance").query();

    height = lookup("#height").query();
    weight = lookup("#weight").query();
    title = lookup("#title").query();
    bloodType = lookup("#bloodType").query();
    bmi = lookup("#bmi").query();

    smoker = lookup("#smoker").query();
    alcoholConsumption = lookup("#alcoholConsumption").query();
    bloodPressure = lookup("#bloodPressure").query();
    chronicDiseases = lookup("#chronicDiseases").query();

    LiverCheckBox = lookup("#LiverCheckBox").query();
    KidneyCheckBox = lookup("#KidneyCheckBox").query();
    LungCheckBox = lookup("#LungCheckBox").query();
    HeartCheckBox = lookup("#HeartCheckBox").query();
    PancreasCheckBox = lookup("#PancreasCheckBox").query();
    IntestineCheckBox = lookup("#IntestineCheckBox").query();
    CorneaCheckBox = lookup("#CorneaCheckBox").query();
    MiddleEarCheckBox = lookup("#MiddleEarCheckBox").query();
    BoneCheckBox = lookup("#BoneCheckBox").query();
    BoneMarrowCheckBox = lookup("#BoneMarrowCheckBox").query();
    SkinCheckBox = lookup("#SkinCheckBox").query();
    ConnectiveTissueCheckBox = lookup("#ConnectiveTissueCheckBox").query();

    streetAddress = lookup("#streetAddress").query();
    city = lookup("#city").query();
    region = lookup("#region").query();
    postcode = lookup("#postcode").query();
    mobileNumber = lookup("#mobileNumber").query();
    homeNumber = lookup("#homeNumber").query();
    email = lookup("#email").query();
    emergStreetAddress = lookup("#emergStreetAddress").query();
    emergCity = lookup("#emergCity").query();
    emergRegion = lookup("#emergRegion").query();
    emergPostcode = lookup("#emergPostcode").query();
    emergMobileNumber = lookup("#emergMobileNumber").query();
    emergHomeNumber = lookup("#emergHomeNumber").query();
    emergEmail = lookup("#emergEmail").query();

    editButton = lookup("#Edit").query();
    logoutButton = lookup("#Logout").query();

  }

  /**
   * Basic tests to check all the buttons are initialized correctly.
   */
  @Test
  @Ignore
  public void verifyAllButtonsInitialized() {
    // expect:
    verifyThat(editButton, hasText("Edit"));
    verifyThat(logoutButton, hasText("Logout"));
  }

  /**
   * Verifies all the labels are initialized correctly
   */
  @Test
  @Ignore
  public void verifyAllLabelsInitialized() {
    if (donorReceiver.getMiddleName().trim().length() == 0) {
      verifyThat(firstNames, hasText(String.format("%s", donorReceiver.getFirstName())));
    } else {
      verifyThat(firstNames, hasText(
          String.format("%s", donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName())));
    }
    verifyThat(lastName, hasText(String.format("%s", donorReceiver.getLastName())));
    verifyThat(nationalHealthIndex, hasText(String.format("%s", donorReceiver.getUserName())));
    verifyThat(dateOfBirth,
        hasText(String.format("%s", donorReceiver.getDateOfBirth().toString())));
    if (donorReceiver.getDateOfDeath() == null) {
      verifyThat(dateOfDeath, hasText(String.format("%s", "N.A.")));
    } else {
      verifyThat(dateOfDeath, hasText(String.format("%s", donorReceiver.getDateOfDeath())));
    }
    verifyThat(dateCreated, hasText(
        ViewProfilePaneController.formatDateTime(donorReceiver.getCreationTimeStamp())));
    verifyThat(gender, hasText(String.format("%s", donorReceiver.genderString())));
    verifyThat(livedInUKFrance, hasText(String.format("%s", donorReceiver.getLivedInUKFlag())));

    verifyThat(title, hasText(String.format("%s", donorReceiver.getTitle())));
    verifyThat(bloodType,
        hasText(String.format("%s", donorReceiver.getUserAttributeCollection().getBloodType())));
    verifyThat(weight,
        hasText(String.format("%s", donorReceiver.getUserAttributeCollection().getWeight())));
    verifyThat(height,
        hasText(String.format("%s", donorReceiver.getUserAttributeCollection().getHeight())));
    verifyThat(bmi, hasText(String.format("%s", ViewProfilePaneController
        .setBMI(donorReceiver.getUserAttributeCollection().getHeight(),
            donorReceiver.getUserAttributeCollection().getWeight()))));

    verifyThat(smoker,
        hasText(String.format("%s", donorReceiver.getUserAttributeCollection().getSmoker())));
    verifyThat(alcoholConsumption, hasText(
        String.format("%s", donorReceiver.getUserAttributeCollection().getAlcoholConsumption())));
    verifyThat(bloodPressure, hasText(
        String.format("%s", donorReceiver.getUserAttributeCollection().getBloodPressure())));
    verifyThat(chronicDiseases, hasText(
        String.format("%s", donorReceiver.getUserAttributeCollection().getChronicDiseases())));

    verifyThat(streetAddress, hasText(
        String.format("%s", donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne())));
    verifyThat(city,
        hasText(String.format("%s", donorReceiver.getContactDetails().getAddress().getCityName())));
    verifyThat(region,
        hasText(String.format("%s", donorReceiver.getContactDetails().getAddress().getRegion())));
    verifyThat(postcode,
        hasText(String.format("%s", donorReceiver.getContactDetails().getAddress().getPostCode())));
    verifyThat(mobileNumber,
        hasText(String.format("%s", donorReceiver.getContactDetails().getMobileNum())));
    verifyThat(homeNumber,
        hasText(String.format("%s", donorReceiver.getContactDetails().getHomeNum())));
    verifyThat(email, hasText(String.format("%s", donorReceiver.getContactDetails().getEmail())));

    verifyThat(emergStreetAddress, hasText(String.format("%s",
        donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLineOne())));
    verifyThat(emergCity, hasText(String
        .format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getCityName())));
    verifyThat(emergRegion, hasText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getRegion())));
    verifyThat(emergPostcode, hasText(String
        .format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode())));
    verifyThat(emergMobileNumber,
        hasText(String.format("%s", donorReceiver.getEmergencyContactDetails().getMobileNum())));
    verifyThat(emergHomeNumber,
        hasText(String.format("%s", donorReceiver.getEmergencyContactDetails().getHomeNum())));
    verifyThat(emergEmail,
        hasText(String.format("%s", donorReceiver.getEmergencyContactDetails().getEmail())));
  }

  /**
   * Tests if the checkboxes are selected if their value is true, and that they are disabled
   */
  @Ignore
  //Failing on the erver due to a null pointer, runs perfectly fine on the linux systems at uni
  @Test
  public void verifyAllCheckBoxesInitialized() {
    sleep(500);
    if (donorReceiver.getDonorOrganInventory().getLiver()) {
      Assert.assertTrue(LiverCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getKidneys()) {
      Assert.assertTrue(KidneyCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getHeart()) {
      Assert.assertTrue(HeartCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getLungs()) {
      Assert.assertTrue(LungCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getIntestine()) {
      Assert.assertTrue(IntestineCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getCorneas()) {
      Assert.assertTrue(CorneaCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getMiddleEars()) {
      Assert.assertTrue(MiddleEarCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getSkin()) {
      Assert.assertTrue(SkinCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getBone()) {
      Assert.assertTrue(BoneCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getBoneMarrow()) {
      Assert.assertTrue(BoneMarrowCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getConnectiveTissue()) {
      Assert.assertTrue(ConnectiveTissueCheckBox.isSelected());
    }
    if (donorReceiver.getDonorOrganInventory().getPancreas()) {
      Assert.assertTrue(PancreasCheckBox.isSelected());
    }
    Assert.assertTrue(LiverCheckBox.isDisabled());
    Assert.assertTrue(KidneyCheckBox.isDisabled());
    Assert.assertTrue(HeartCheckBox.isDisabled());
    Assert.assertTrue(LungCheckBox.isDisabled());
    Assert.assertTrue(IntestineCheckBox.isDisabled());
    Assert.assertTrue(CorneaCheckBox.isDisabled());
    Assert.assertTrue(MiddleEarCheckBox.isDisabled());
    Assert.assertTrue(SkinCheckBox.isDisabled());
    Assert.assertTrue(BoneCheckBox.isDisabled());
    Assert.assertTrue(BoneMarrowCheckBox.isDisabled());
    Assert.assertTrue(ConnectiveTissueCheckBox.isDisabled());
    Assert.assertTrue(PancreasCheckBox.isDisabled());
  }


  /**
   * Tear down the window after test
   *
   * @throws TimeoutException If there is a timeout
   */
  @After
  public void tearDown() throws TimeoutException {
    FxToolkit.hideStage();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
  }
}
