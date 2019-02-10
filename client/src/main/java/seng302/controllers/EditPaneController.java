package seng302.controllers;

import static java.lang.Thread.sleep;
import static seng302.controllers.ViewProfilePaneController.setChronicIllnessFirstSort;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.model.*;
import seng302.model.enums.ADDRESSES;
import seng302.model.patches.*;
import seng302.model.person.*;
import seng302.model.locationData.CountryList;
import seng302.model.locationData.RegionList;

import seng302.services.DeleteTask;
import seng302.services.GetTask;
import seng302.services.PatchTask;
import seng302.services.PostTask;


/**
 * The controller class for the editpane. This class calls the model's issueCommand(Update) methods
 * on whatever data fields the user wishes to edit.
 */
public class EditPaneController implements CreateEditController {

  /**
   * Max width/height of a profile image
   */
  private static final int MAX_DIMENSION = 10000;

  /**
   *  Max byte size of a profile image (5 megabytes)
   */
  public final static int MAX_BYTES = 3000000;

  /**
   * The current donorReceiver being edited.
   */
  private DonorReceiver donorReceiver;
  public static DonorReceiver staticAccount;
  private static boolean isChild = false;
  private boolean instanceIsChild = false;
  private boolean invalidFlag = false;

  private String nhi;
  private AccountManager db = App.getDatabase();
  private ArrayList<String> medicationToDelete = new ArrayList<>();

  public static int CURRENT_VERSION;

  static void setAccount(DonorReceiver newDonorReceiver) {
    staticAccount = newDonorReceiver;
  }

  private static UndoableManager undoableManager = App.getUndoableManager();

  private static Boolean undoRedoChoiceBox = false;
  private static Boolean undoRedoDatePicker = false;
  private static Boolean undoRedoTextField = false;

  private final String[] gender = {"Male", "Female", "Other", "Unknown", "Unspecified"};
  private final String[] bloodTypes = {"","A+", "B+", "O+", "A-", "B-", "O-", "AB+", "AB-"};
  private final String[] titles = {"Dr", "Mr", "Mrs", "Ms", "Miss", "Master"};
  private static final String DONOR = "donor";
  private static final String USER = "User ";
  private static final String MODIFIED = " modified.";
  private static final String NEW_ZEALAND = "NEW ZEALAND";
  private static final String ERROR_DIALOG = "Error Dialog";
  private static final String BORDER_RED = " -fx-border-color: red ; -fx-border-width: 2px ; ";
  private static final String BORDER_SILVER = " -fx-border-color: silver ; -fx-border-width: 1px ; ";
  private static final String EMERGENCY = "emergency";

  private ObservableList<String> autoCompleteSuggestions = FXCollections.observableArrayList();

  private CountryList countryList;
  private RegionList regionList = RegionList.getInstance();

  private List deathDetails = new ArrayList();

  private String errorMessage = "";

  public static String currentUser = "";

  public static void setCurrentUser(String user) {
    currentUser = user;
  }

  public static String getCurrentUser() {
    return currentUser;
  }


  @FXML
  private Text DonorLoggedIn;
  @FXML
  private TabPane mainTabPane;

  //Basic information
  @FXML
  private TextField editGivenNames;
  @FXML
  private Text givenNamesText;
  @FXML
  private TextField editLastName;
  @FXML
  private Text lastNameText;
  @FXML
  private TextField editPreferredName;
  @FXML
  private Text preferredNameText;
  @FXML
  private TextField editNHINumber;
  @FXML
  private Text nhiText;
  @FXML
  private DatePicker editDateOfBirth;
  @FXML
  private Text dateOfBirthText;
  @FXML
  private Text dateOfDeathText;
  @FXML
  private ChoiceBox editGender;
  @FXML
  private Text genderText;
  @FXML
  private ChoiceBox editBirthGender;
  @FXML
  private Text birthGenderText;
  @FXML
  private ChoiceBox editTitle;
  @FXML
  private Text titleText;
  @FXML
  private TextField editHeight;
  @FXML
  private Text heightText;
  @FXML
  private TextField editWeight;
  @FXML
  private Text weightText;
  @FXML
  private ChoiceBox editBloodType;
  @FXML
  private Text bloodTypeText;
  @FXML
  private CheckBox editLivedInUKFrance;
  @FXML
  private Text ukFranceText;

  @FXML
  private Button setDeathDetailsButton;

  @FXML
  private Button clearDeathDetailsButton;

  @FXML
  private Label editDateOfDeath;
  @FXML
  private Label editDeathCountryLabel;
  @FXML
  private Label editDeathRegionLabel;
  @FXML
  private Label editDeathCityLabel;
  @FXML
  private Text deathDetailsText;
  @FXML
  private Text deathCountryText;
  @FXML
  private Text deathCityText;
  @FXML
  private Text deathRegionText;
  @FXML
  private Text deathDateText;


  //Contact details
  @FXML
  private TextField editStreetAddress;
  @FXML
  private Text streetText;
  @FXML
  private TextField editCity;
  @FXML
  private Text cityText;
  @FXML
  private ChoiceBox<String> editRegion;
  @FXML
  private Text regionText;
  @FXML
  private TextField editPostcode;
  @FXML
  private ChoiceBox<String> editCountry;
  @FXML
  private Text postcodeText;

  @FXML
  private Text countryText;

  @FXML
  private TextField editMobileNumber;
  @FXML
  private Text mobileNumberText;
  @FXML
  private TextField editHomeNumber;
  @FXML
  private Text homeNumberText;
  @FXML
  private TextField editEmail;
  @FXML
  private Text emailText;
  @FXML
  private TextField editEmergStreetAddress;
  @FXML
  private Text emergStreetText;
  @FXML
  private TextField editEmergCity;
  @FXML
  private Text emergCityText;
  @FXML
  private ChoiceBox<String> editEmergRegion;
  @FXML
  private Text emergRegionText;
  @FXML
  private TextField editEmergPostcode;
  @FXML
  private Text emergPostcodeText;
  @FXML
  private ChoiceBox<String> emergCountry;
  @FXML
  private ChoiceBox<String> editEmergCountry;
  @FXML
  private TextField editEmergMobileNumber;
  @FXML
  private Text emergMobileNumberText;
  @FXML
  private TextField editEmergHomeNumber;
  @FXML
  private Text emergHomeNumberText;
  @FXML
  private TextField editEmergEmail;
  @FXML
  private Text emergEmailText;

  //Tabs
  @FXML
  private Tab receiverTab;
  @FXML
  private TabPane organTabPane;

  //Organs
  @FXML
  private CheckBox editLiver;
  @FXML
  private CheckBox editKidney;
  @FXML
  private CheckBox editLung;
  @FXML
  private CheckBox editHeart;
  @FXML
  private CheckBox editPancreas;
  @FXML
  private CheckBox editIntestine;
  @FXML
  private CheckBox editCornea;
  @FXML
  private CheckBox editMiddleEar;
  @FXML
  private CheckBox editBone;
  @FXML
  private CheckBox editBoneMarrow;
  @FXML
  private CheckBox editSkin;
  @FXML
  private CheckBox editConnectiveTissue;

  //Receiving organs
  @FXML
  private CheckBox editReceiverLiver;
  @FXML
  private CheckBox editReceiverKidney;
  @FXML
  private CheckBox editReceiverLung;
  @FXML
  private CheckBox editReceiverHeart;
  @FXML
  private CheckBox editReceiverPancreas;
  @FXML
  private CheckBox editReceiverIntestine;
  @FXML
  private CheckBox editReceiverCornea;
  @FXML
  private CheckBox editReceiverMiddleEar;
  @FXML
  private CheckBox editReceiverBone;
  @FXML
  private CheckBox editReceiverBoneMarrow;
  @FXML
  private CheckBox editReceiverSkin;
  @FXML
  private CheckBox editReceiverConnectiveTissue;

  // Donating organ text
  @FXML
  private Text editLiverText;
  @FXML
  private Text editKidneyText;
  @FXML
  private Text editLungText;
  @FXML
  private Text editHeartText;
  @FXML
  private Text editPancreasText;
  @FXML
  private Text editIntestineText;
  @FXML
  private Text editCorneaText;
  @FXML
  private Text editMiddleEarText;
  @FXML
  private Text editBoneText;
  @FXML
  private Text editBoneMarrowText;
  @FXML
  private Text editSkinText;
  @FXML
  private Text editConnectiveTissueText;

  //Receiving organ text
  @FXML
  private Text editReceiverLiverText;
  @FXML
  private Text editReceiverKidneyText;
  @FXML
  private Text editReceiverLungText;
  @FXML
  private Text editReceiverHeartText;
  @FXML
  private Text editReceiverPancreasText;
  @FXML
  private Text editReceiverIntestineText;
  @FXML
  private Text editReceiverCorneaText;
  @FXML
  private Text editReceiverMiddleEarText;
  @FXML
  private Text editReceiverBoneText;
  @FXML
  private Text editReceiverBoneMarrowText;
  @FXML
  private Text editReceiverSkinText;
  @FXML
  private Text editReceiverConnectiveTissueText;

  //Medications
  @FXML
  private Button moveToCurrent;
  @FXML
  private Button moveToPrevious;
  @FXML
  private Button addMedication;
  @FXML
  private Button editMedication;
  @FXML
  private Button removeMedication;
  @FXML
  private ComboBox<String> createNewMedication;
  @FXML
  private ListView editCurrentMedications;
  @FXML
  private ListView editPreviousMedications;
  @FXML
  private Tab medicationsTab;

  //Medical History
  @FXML
  private CheckBox editSmoker;
  @FXML
  private Text smokerText;
  @FXML
  private TextField editAlcoholConsumption;
  @FXML
  private Text alcoholConsumptionText;
  @FXML
  private TextField editBloodPressure;
  @FXML
  private Text bloodPressureText;
  @FXML
  private Text chronicDiseasesText;

  // Menu Buttons
  @FXML
  private Button Done;
  @FXML
  private Button Cancel;
  @FXML
  private Button setButton;
  @FXML
  private Button deleteButton;

  //ChoiceBox values for Undo/Redo

  //medical history diseases
  @FXML
  private ListView history;
  @FXML
  private TableView<Illness> currentTable;
  @FXML
  private TableColumn<Illness, String> CurrentName;
  @FXML
  private TableColumn<Illness, String> chronic;
  @FXML
  private TableColumn<Illness, String> currentDate;
  @FXML
  private TableView<Illness> historicTable;
  @FXML
  private TableColumn<Illness, String> historicName;
  @FXML
  private TableColumn<Illness, String> historicDate;
  @FXML
  private Tab diseasesTab;
  @FXML
  private ImageView profileImage;
  @FXML
  private Button deleteIllnessButton;
  @FXML
  private Button createIllnessButton;
  @FXML
  private Button editCurrentIllnessButton;
  @FXML
  private Button editHistoricIllnessButton;

  private Illness selectedCurrentIllness;

  private Illness selectedHistoricIllness;
  /**
   * An observable array list of Illness objects representing current diseases/illnesses the donor
   * suffers from.
   */
  private ObservableList<Illness> currentDiagnoses;

  /**
   * An observable array list of Illness objects representing historic diseases/illnesses the donor
   * suffered from.
   */
  private ObservableList<Illness> historicDiagnoses;


  // Medical History Procedures
  @FXML
  private TableView<MedicalProcedure> pastProceduresTable;
  @FXML
  private TableColumn<MedicalProcedure, LocalDate> editPastProceduresDateColumn;
  @FXML
  private TableColumn<MedicalProcedure, String> editPastProceduresSummaryColumn;
  @FXML
  private TableView<MedicalProcedure> pendingProcedureTable;
  @FXML
  private TableColumn<MedicalProcedure, LocalDate> editPendingProceduresDateColumn;
  @FXML
  private TableColumn<MedicalProcedure, String> editPendingProceduresSummaryColumn;
  @FXML
  private Label pastProcedureAffectedOrgans;
  @FXML
  private TextArea pastProcedureDescription;
  @FXML
  private Label pendingProcedureAffectedOrgans;
  @FXML
  private TextArea pendingProcedureDescription;
  @FXML
  private Tab medicalProceduresTab;


  public static Session session = App.getCurrentSession();

  private ObservableList<MedicalProcedure> pastProcedures;

  private ObservableList<MedicalProcedure> pendingProcedures;

  //===================================================
  //===================================================

  /**
   * Initializes combo boxes/check boxes and labels in the pane
   */
  @FXML
  private void initialize() {
    getCountries();
    Done.getStyleClass().add("primaryButton");
    Cancel.getStyleClass().add("redButton");
    setButton.getStyleClass().add("greenButton");
    deleteButton.getStyleClass().add("deletePhotoButton");

    createIllnessButton.getStyleClass().add("greenButton");
    deleteIllnessButton.getStyleClass().add("redButton");

    if (session.getUserType().equalsIgnoreCase(DONOR)) {
      medicationsTab.setDisable(true);
      medicalProceduresTab.setDisable(true);
      diseasesTab.setDisable(true);
    } else {
      medicationsTab.setDisable(false);
      medicalProceduresTab.setDisable(false);
      diseasesTab.setDisable(false);
    }
    getDonor(staticAccount.getUserName(), false);
  }

  private void getCountries() {
    GetTask task = new GetTask(ArrayList.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_COUNTRIES.getAddress(), session.getToken());

    task.setOnSucceeded(event -> {
      ArrayList<String> listOfCountries = (ArrayList) task.getValue();
      countryList = new CountryList();
      countryList.populateCountryWithNewCountries(listOfCountries);
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get countries");

    });
    new Thread(task).start();
  }

  public void refreshAccount(){
    getDonor(staticAccount.getUserName(), true);
  }

  public void refreshView(){
    // Clear procedures
    pastProcedures.clear();
    pendingProcedures.clear();

    pastProcedures = FXCollections.observableArrayList(staticAccount.extractPastProcedures());
    pendingProcedures = FXCollections.observableArrayList(staticAccount.extractPendingProcedures());

    donorReceiver = staticAccount;
    nhi = donorReceiver.getUserName();
    CURRENT_VERSION = donorReceiver.getVersion();
    getPhoto(nhi);
    editGender.getItems().addAll(gender);
    editBirthGender.getItems().addAll(gender);
    editBloodType.getItems().addAll(bloodTypes);
    editTitle.getItems().addAll(titles);

    initCheckBoxes();
    initLabels();
    initMedications();
    hideMedicationsEditingFromDonor();
    initContactDetails();
    initBasicInfomation();
    initMedicalHistory();
    emergMobileNumberText.setText(mobileNumberText.getText());

    donorReceiver.setUpdateMessage("waiting for new message");
    DonorLoggedIn.setText(donorReceiver.fullName());
    hideOrShowMedicalProcedures();
    new AutoCompleteComboBoxListener<>(createNewMedication);

    //medical history diseases related gui elements
    currentDiagnoses = FXCollections.observableArrayList(donorReceiver.getCurrentDiagnoses());
    historicDiagnoses = FXCollections.observableArrayList(donorReceiver.getHistoricDiagnoses());

    historicName.setCellValueFactory(new PropertyValueFactory<>("name"));
    historicDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    CurrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
    currentDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    setChronicColoumn();
    currentTable.setItems(currentDiagnoses);
    historicTable.setItems(historicDiagnoses);

    setChronicIllnessFirstSort(currentTable);
    setChronicIllnessFirstSort(
            historicTable); // Used because it also gives the default sort of descending date

    hideMedicationsEditingFromDonor();
    if (AccountManager
            .getCurrentUser() instanceof DonorReceiver) { // a donor/receiver is logged in so we hide edit medical history tab
      diseasesTab.setDisable(true); // index 5 is the medicial history (disease tab)
    }

    hideOrShowMedicationsTab();

    // Undo and redo for text fields
    ArrayList<TextField> textFieldArrayList = new ArrayList<TextField>();
    textFieldArrayList.add(editBloodPressure);
    textFieldArrayList.add(editAlcoholConsumption);
    textFieldArrayList.add(editEmergEmail);
    textFieldArrayList.add(editEmergHomeNumber);
    textFieldArrayList.add(editEmergMobileNumber);
    textFieldArrayList.add(editEmergPostcode);
    //textFieldArrayList.add(editEmergRegion);
    textFieldArrayList.add(editEmergCity);
    textFieldArrayList.add(editEmergStreetAddress);
    textFieldArrayList.add(editEmail);
    textFieldArrayList.add(editHomeNumber);
    textFieldArrayList.add(editMobileNumber);
    textFieldArrayList.add(editPostcode);
    //textFieldArrayList.add(editRegion);
    textFieldArrayList.add(editCity);
    textFieldArrayList.add(editStreetAddress);
    textFieldArrayList.add(editHeight);
    textFieldArrayList.add(editWeight);
    textFieldArrayList.add(editNHINumber);
    textFieldArrayList.add(editLastName);
    textFieldArrayList.add(editGivenNames);

    for (TextField textField : textFieldArrayList) {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!undoRedoTextField) {
          undoableManager.createTextFieldChange(textField, oldValue, newValue);
        }
        undoRedoTextField = false;
      });
      textField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }

    editRegion.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editRegion, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editEmergRegion.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editEmergRegion, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editCountry.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editCountry, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editEmergCountry.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editEmergCountry, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });

    editTitle.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editTitle, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editGender.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editGender, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editBloodType.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editBloodType, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });

    editDateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoDatePicker) {
        undoableManager.createDatePickerChange(editDateOfBirth, oldValue, newValue);
      }
      undoRedoDatePicker = false;
    });

    editCurrentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    editPreviousMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    handleView();
    disableReceivingOrgansIfDead();

  }

  private void setup() {
    pastProcedures = FXCollections.observableArrayList(staticAccount.extractPastProcedures());
    pendingProcedures = FXCollections.observableArrayList(staticAccount.extractPendingProcedures());

    // Handle configuration of scene.
    configureWindowAsChild();

    donorReceiver = staticAccount;
    nhi = donorReceiver.getUserName();
    CURRENT_VERSION = donorReceiver.getVersion();
    getPhoto(nhi);
    editGender.getItems().addAll(gender);
    editBirthGender.getItems().addAll(gender);
    editBloodType.getItems().addAll(bloodTypes);
    editTitle.getItems().addAll(titles);

    initCheckBoxes();
    initLabels();
    initMedications();
    hideMedicationsEditingFromDonor();
    initContactDetails();
    initBasicInfomation();
    initMedicalHistory();

    donorReceiver.setUpdateMessage("waiting for new message");
    DonorLoggedIn.setText(donorReceiver.fullName());
    hideOrShowMedicalProcedures();
    new AutoCompleteComboBoxListener<>(createNewMedication);

    //medical history diseases related gui elements
    currentDiagnoses = FXCollections.observableArrayList(donorReceiver.getCurrentDiagnoses());
    historicDiagnoses = FXCollections.observableArrayList(donorReceiver.getHistoricDiagnoses());

    historicName.setCellValueFactory(new PropertyValueFactory<>("name"));
    historicDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    CurrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
    currentDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    setChronicColoumn();
    currentTable.setItems(currentDiagnoses);
    historicTable.setItems(historicDiagnoses);

    editCurrentIllnessButton.setDisable(true);
    editHistoricIllnessButton.setDisable(true);
    deleteIllnessButton.setDisable(true);
    currentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      historicTable.getSelectionModel().clearSelection(historicTable.getSelectionModel().getSelectedIndex());
      selectedCurrentIllness = currentTable.getSelectionModel().getSelectedItem();
      editCurrentIllnessButton.setDisable(false);
      deleteIllnessButton.setDisable(false);
    });

    historicTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        selectedHistoricIllness = historicTable.getSelectionModel().getSelectedItem();
        editHistoricIllnessButton.setDisable(false);
      deleteIllnessButton.setDisable(false);
    });

    setChronicIllnessFirstSort(currentTable);
    setChronicIllnessFirstSort(
            historicTable); // Used because it also gives the default sort of descending date

    hideMedicationsEditingFromDonor();
    if (AccountManager
            .getCurrentUser() instanceof DonorReceiver) { // a donor/receiver is logged in so we hide edit medical history tab
      diseasesTab.setDisable(true); // index 5 is the medicial history (disease tab)
    }

    hideOrShowMedicationsTab();

    // Undo and redo for text fields
    ArrayList<TextField> textFieldArrayList = new ArrayList<TextField>();
    textFieldArrayList.add(editBloodPressure);
    textFieldArrayList.add(editAlcoholConsumption);
    textFieldArrayList.add(editEmergEmail);
    textFieldArrayList.add(editEmergHomeNumber);
    textFieldArrayList.add(editEmergMobileNumber);
    textFieldArrayList.add(editEmergPostcode);
    //textFieldArrayList.add(editEmergRegion);
    textFieldArrayList.add(editEmergCity);
    textFieldArrayList.add(editEmergStreetAddress);
    textFieldArrayList.add(editEmail);
    textFieldArrayList.add(editHomeNumber);
    textFieldArrayList.add(editMobileNumber);
    textFieldArrayList.add(editPostcode);
    //textFieldArrayList.add(editRegion);
    textFieldArrayList.add(editCity);
    textFieldArrayList.add(editStreetAddress);
    textFieldArrayList.add(editHeight);
    textFieldArrayList.add(editWeight);
    textFieldArrayList.add(editNHINumber);
    textFieldArrayList.add(editLastName);
    textFieldArrayList.add(editGivenNames);

    for (TextField textField : textFieldArrayList) {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!undoRedoTextField) {
          undoableManager.createTextFieldChange(textField, oldValue, newValue);
        }
        undoRedoTextField = false;
      });
      textField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }

    editRegion.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editRegion, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editEmergRegion.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editEmergRegion, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editCountry.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editCountry, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editEmergCountry.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editEmergCountry, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });

    editTitle.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editTitle, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editGender.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editGender, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });
    editBloodType.getSelectionModel().selectedIndexProperty()
            .addListener((observable, oldValue, newValue) -> {
              if (!undoRedoChoiceBox) {
                undoableManager.createChoiceBoxUndoable(editBloodType, oldValue, newValue);
              }
              undoRedoChoiceBox = false;
            });

    editDateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoDatePicker) {
        undoableManager.createDatePickerChange(editDateOfBirth, oldValue, newValue);
      }
      undoRedoDatePicker = false;
    });

    editCurrentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    editPreviousMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    handleView();
    disableReceivingOrgansIfDead();
  }


  /**
   * Changes the pane to reflect its status as a child window.
   */
  private void configureWindowAsChild() {
    instanceIsChild = isChild;
    isChild = false;
  }




  private void disableReceivingOrgansIfDead() {
    // if the donor is dead, then disable being able to change receiving organs
    if (donorReceiver.getDateOfDeath() != null) {
      receiverTab.setDisable(true);
    }
  }


  /**
   * Initialise the ListViews containing medication information
   */
  @FXML
  public void initMedications() {
    ObservableList<String> currentMedications = FXCollections
        .observableArrayList(donorReceiver.getMedications().getCurrentMedications());
    editCurrentMedications.setItems(currentMedications);
    ObservableList<String> previousMedications = FXCollections
        .observableArrayList(donorReceiver.getMedications().getPreviousMedications());
    editPreviousMedications.setItems(previousMedications);
  }


  /**
   * Create a new medication and add to current medications
   */
  @FXML
  public void addCurrentMedication() {
    String newMedication = createNewMedication.getEditor().getText();
    if (!newMedication.trim().equals("") && !editCurrentMedications.getItems()
        .contains(newMedication) && !editPreviousMedications.getItems().contains(newMedication)) {
      createMedication(newMedication);
    }
  }


  /**
   * Remove the currently selected medication
   */
  @FXML
  private void removeCurrentPreviousMedication() {
    PageNav.loading();
    ObservableList<String> currentlySelectedMedications = editCurrentMedications.getSelectionModel()
        .getSelectedItems();
    ArrayList<String> currentToRemove = new ArrayList<>(
        currentlySelectedMedications); //Selection doesn't work with ObservableList
    ObservableList<String> currentlySelectedPreviousMedications = editPreviousMedications
        .getSelectionModel().getSelectedItems();
    ArrayList<String> previousToRemove = new ArrayList<>(
        currentlySelectedPreviousMedications); //Selection doesn't work with ObservableList
    for (String currentlySelected : currentToRemove) {
      if (currentlySelected != null) {
        Medication medication = new Medication(currentlySelected, true);
        DeleteTask task = new DeleteTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_MEDICATION.getAddress(), staticAccount.getUserName()), medication, App.getCurrentSession().getToken());
        task.setOnSucceeded(event -> {
          editCurrentMedications.getItems().remove(currentlySelected);
          editCurrentMedications.getSelectionModel().clearSelection();
          editPreviousMedications.getSelectionModel().clearSelection();
          editCurrentMedications.refresh();
          editPreviousMedications.refresh();
          PageNav.loaded();
        });
        task.setOnFailed(event -> {
          PageNav.loaded();
          showBadSaveError();
        });
        new Thread(task).start();
      }
    }
    for (String previousSelected : previousToRemove) {
      if (previousSelected != null) {
        Medication medication = new Medication(previousSelected, false);
        DeleteTask task = new DeleteTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_MEDICATION.getAddress(), staticAccount.getUserName()), medication, App.getCurrentSession().getToken());
        task.setOnSucceeded(event -> {
          PageNav.loaded();
          editCurrentMedications.getItems().remove(previousSelected);
          editCurrentMedications.getSelectionModel().clearSelection();
          editPreviousMedications.getSelectionModel().clearSelection();
          editCurrentMedications.refresh();
          editPreviousMedications.refresh();
        });
        task.setOnFailed(event -> {
          PageNav.loaded();
          showBadSaveError();
        });
        new Thread(task).start();
      }
    }

  }

  /**
   * Moves a medication located in Current Medications to Previous Medications
   */
  @FXML
  private void moveCurrentToPrevious() {
    PageNav.loading();
    ObservableList<String> currentlySelectedMedications = editCurrentMedications.getSelectionModel()
        .getSelectedItems();
    ArrayList<String> medicationsToMove = new ArrayList<>(
        currentlySelectedMedications); //Selection doesn't work with ObservableList
    for (int i = 0; i < medicationsToMove.size(); i++) {
      String currentlySelected = medicationsToMove.get(i);
      if (currentlySelected != null) {
        CURRENT_VERSION++;
        Map<String, String> customheaders = new HashMap<String, String>();
        customheaders.put("version", Integer.toString(CURRENT_VERSION));
        Medication medication = new Medication(currentlySelected, false);
        PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_MEDICATION.getAddress(), staticAccount.getUserName()), medication, App.getCurrentSession().getToken(), customheaders);
        task.setOnFailed(event -> {
          PageNav.loaded();
          showBadSaveError();
        });
        task.setOnSucceeded(event -> {
          AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
          editCurrentMedications.getItems().remove(currentlySelected);
          editPreviousMedications.getItems().add(currentlySelected);
          editCurrentMedications.getSelectionModel().clearSelection();
          editPreviousMedications.getSelectionModel().clearSelection();
          editCurrentMedications.refresh();
          editPreviousMedications.refresh();
          PageNav.loaded();
        });
        new Thread(task).start();
      }
    }

  }

  /**
   * Moves a medication located in Previous Medications to Current Medications
   */
  @FXML
  private void movePreviousToCurrent() {
    String previousSelected = (String) editPreviousMedications.getSelectionModel()
        .getSelectedItem();
    if (previousSelected != null) {
      CURRENT_VERSION++;
      Map<String, String> customheaders = new HashMap<String, String>();
      customheaders.put("version", Integer.toString(CURRENT_VERSION));
      Medication medication = new Medication(previousSelected, true);
      PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_MEDICATION.getAddress(), staticAccount.getUserName()), medication, App.getCurrentSession().getToken(), customheaders);
      task.setOnFailed(event -> {
        PageNav.loaded();
        showBadSaveError();
      });
      task.setOnSucceeded(event -> {
        AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
        editPreviousMedications.getItems().remove(previousSelected);
        editCurrentMedications.getItems().add(previousSelected);
        editCurrentMedications.getSelectionModel().clearSelection();
        editPreviousMedications.getSelectionModel().clearSelection();
        editCurrentMedications.refresh();
        editPreviousMedications.refresh();
        PageNav.loaded();
      });
      new Thread(task).start();
    }

  }

  /**
   * Initializes all check boxes
   */
  @FXML
  public void initCheckBoxes() {
    editLivedInUKFrance.setSelected(donorReceiver.getLivedInUKFlag());

    editSmoker.setSelected(
        BooleanExtension.getBoolean(donorReceiver.getUserAttributeCollection().getSmoker()));

    editLiver.setSelected(donorReceiver.getDonorOrganInventory().getLiver());
    editKidney.setSelected(donorReceiver.getDonorOrganInventory().getKidneys());
    editLung.setSelected(donorReceiver.getDonorOrganInventory().getLungs());
    editHeart.setSelected(donorReceiver.getDonorOrganInventory().getHeart());
    editPancreas.setSelected(donorReceiver.getDonorOrganInventory().getPancreas());
    editIntestine.setSelected(donorReceiver.getDonorOrganInventory().getIntestine());
    editCornea.setSelected(donorReceiver.getDonorOrganInventory().getCorneas());
    editMiddleEar.setSelected(donorReceiver.getDonorOrganInventory().getMiddleEars());
    editBone.setSelected(donorReceiver.getDonorOrganInventory().getBone());
    editBoneMarrow.setSelected(donorReceiver.getDonorOrganInventory().getBoneMarrow());
    editSkin.setSelected(donorReceiver.getDonorOrganInventory().getSkin());
    editConnectiveTissue.setSelected(donorReceiver.getDonorOrganInventory().getConnectiveTissue());

    editReceiverLiver.setSelected(donorReceiver.getRequiredOrgans().getLiver());
    editReceiverKidney.setSelected(donorReceiver.getRequiredOrgans().getKidneys());
    editReceiverLung.setSelected(donorReceiver.getRequiredOrgans().getLungs());
    editReceiverHeart.setSelected(donorReceiver.getRequiredOrgans().getHeart());
    editReceiverPancreas.setSelected(donorReceiver.getRequiredOrgans().getPancreas());
    editReceiverIntestine.setSelected(donorReceiver.getRequiredOrgans().getIntestine());
    editReceiverCornea.setSelected(donorReceiver.getRequiredOrgans().getCorneas());
    editReceiverMiddleEar.setSelected(donorReceiver.getRequiredOrgans().getMiddleEars());
    editReceiverBone.setSelected(donorReceiver.getRequiredOrgans().getBone());
    editReceiverBoneMarrow.setSelected(donorReceiver.getRequiredOrgans().getBoneMarrow());
    editReceiverSkin.setSelected(donorReceiver.getRequiredOrgans().getSkin());
    editReceiverConnectiveTissue
        .setSelected(donorReceiver.getRequiredOrgans().getConnectiveTissue());
  }


  /**
   * Highlight the organ label of any organ that the donator is both receiving and donating
   */
  public void initLabels() {

    DonorOrganInventory donorOrganInventory = donorReceiver.getDonorOrganInventory();
    ReceiverOrganInventory receiverOrganInventory = donorReceiver.getRequiredOrgans();
    if (donorOrganInventory != null && receiverOrganInventory != null) {

      if (donorOrganInventory.getLiver() && receiverOrganInventory.getLiver()) {
        editLiverText.setFill(Color.RED);
        editReceiverLiverText.setFill(Color.RED);
      }
      if (donorOrganInventory.getKidneys() && receiverOrganInventory.getKidneys()) {
        editKidneyText.setFill(Color.RED);
        editReceiverKidneyText.setFill(Color.RED);
      }
      if (donorOrganInventory.getLungs() && receiverOrganInventory.getLungs()) {
        editLungText.setFill(Color.RED);
        editReceiverLungText.setFill(Color.RED);
      }
      if (donorOrganInventory.getHeart() && receiverOrganInventory.getHeart()) {
        editHeartText.setFill(Color.RED);
        editReceiverHeartText.setFill(Color.RED);
      }
      if (donorOrganInventory.getPancreas() && receiverOrganInventory.getPancreas()) {
        editPancreasText.setFill(Color.RED);
        editReceiverPancreasText.setFill(Color.RED);
      }
      if (donorOrganInventory.getIntestine() && receiverOrganInventory.getIntestine()) {
        editIntestineText.setFill(Color.RED);
        editReceiverIntestineText.setFill(Color.RED);
      }
      if (donorOrganInventory.getCorneas() && receiverOrganInventory.getCorneas()) {
        editCorneaText.setFill(Color.RED);
        editReceiverCorneaText.setFill(Color.RED);
      }
      if (donorOrganInventory.getMiddleEars() && receiverOrganInventory.getMiddleEars()) {
        editMiddleEarText.setFill(Color.RED);
        editReceiverMiddleEarText.setFill(Color.RED);
      }
      if (donorOrganInventory.getBone() && receiverOrganInventory.getBone()) {
        editBoneText.setFill(Color.RED);
        editReceiverBoneText.setFill(Color.RED);
      }
      if (donorOrganInventory.getBoneMarrow() && receiverOrganInventory.getBoneMarrow()) {
        editBoneMarrowText.setFill(Color.RED);
        editReceiverBoneMarrowText.setFill(Color.RED);
      }
      if (donorOrganInventory.getSkin() && receiverOrganInventory.getSkin()) {
        editSkinText.setFill(Color.RED);
        editReceiverSkinText.setFill(Color.RED);
      }
      if (donorOrganInventory.getConnectiveTissue() && receiverOrganInventory
          .getConnectiveTissue()) {
        editConnectiveTissueText.setFill(Color.RED);
        editReceiverConnectiveTissueText.setFill(Color.RED);
      }
    }
  }


  /**
   * Initializes the contact details tab with the selected donorReceiver(the donor) current values
   */
  private void initContactDetails() {

    String userCountryCode = donorReceiver.getAddressCountryCode();
    String emergCountryCode = donorReceiver.getEmergencyContactDetails().getAddress().getCountryCode();

    editStreetAddress.setText(
        String.format("%s", donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne()));
    editCity
        .setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getCityName()));


      // If the user's country is set, initialise the region choice box and the country text field
      //countryText.setText(donorReceiver.getAddressCountryName());
      editRegion.getItems().addAll(regionList.getRegions("NZ"));
      if (!donorReceiver.getAddressRegion().equals("") && donorReceiver.getAddressRegion() != null) {
        editRegion.getSelectionModel().select(donorReceiver.getAddressRegion());
      } else {
        editRegion.getSelectionModel().select("");
      }



    editPostcode        .setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getPostCode()));
    editMobileNumber.setText(String.format("%s", donorReceiver.getContactDetails().getMobileNum().replaceAll(" ", "")));
    editHomeNumber.setText(String.format("%s", donorReceiver.getContactDetails().getHomeNum().replaceAll(" ", "")));
    editEmail.setText(String.format("%s", donorReceiver.getContactDetails().getEmail()));

    editCountry.getItems().add(NEW_ZEALAND);
    editCountry.getSelectionModel().select(donorReceiver.getAddressCountryName());


    editEmergStreetAddress.setText(String.format("%s",
        donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLineOne()));
    editEmergCity.setText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getCityName()));
    editEmergRegion.getItems().addAll(regionList.getRegions("NZ"));
    editEmergRegion.getSelectionModel().select(donorReceiver.getEmergencyContactDetails().getAddress().getRegion());
    editEmergPostcode.setText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode()));
      //emergCountryText.setText(donorReceiver.getEmergencyContactDetails().getAddress().retrveCountryName());


    editEmergMobileNumber
        .setText(String.format("%s", editMobileNumber.getText().replaceAll(" ","")));
    editEmergHomeNumber
        .setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getHomeNum().replaceAll(" ", "")));
    editEmergEmail
        .setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getEmail()));
    editEmergCountry.getItems().add(NEW_ZEALAND);
    editEmergCountry.getSelectionModel().select(
            donorReceiver.getEmergencyContactDetails().getAddress().retrieveCountryName());


  }


  /**
   * Initializes the basic infomation tab with the selected donorReceiver(the donor) current values
   */
  private void initBasicInfomation() {

    editGivenNames.setText(donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName());
    editLastName.setText(donorReceiver.getLastName());
    editPreferredName.setText(donorReceiver.getPreferredName());
    editNHINumber.setText(String.format("%s", donorReceiver.getUserName().toString()));
    editNHINumber.setDisable(true);
    editDateOfBirth.setValue(donorReceiver.getDateOfBirth());
    editGender.getSelectionModel().select(donorReceiver.genderString());
    editBirthGender.getSelectionModel().select(donorReceiver.birthGenderString());
    editLivedInUKFrance.setSelected(donorReceiver.getLivedInUKFlag());
    if (donorReceiver.getUserAttributeCollection().getHeight() == 0.0) {
      editHeight.setText("");
    } else {
      editHeight.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getHeight()));
    }
    if (donorReceiver.getUserAttributeCollection().getWeight() == 0.0) {
      editWeight.setText("");
    } else {
      editWeight.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getWeight()));
    }
    editBloodType.getSelectionModel()
        .select(donorReceiver.getUserAttributeCollection().getBloodType());
    editTitle.getSelectionModel().select(donorReceiver.titleString());
    initDeathDetails();
  }





  /**
   * Initializes the medical history tab with the selected donorReceiver(the donor) current values
   */
  private void initMedicalHistory() {
    editSmoker.setSelected(
        BooleanExtension.getBoolean(donorReceiver.getUserAttributeCollection().getSmoker()));
    editAlcoholConsumption.setText(
        String.valueOf(donorReceiver.getUserAttributeCollection().getAlcoholConsumption()));
    editBloodPressure.setText(donorReceiver.getUserAttributeCollection().getBloodPressure());
  }

  //=============================================================//

  //=============================================================//
  //Hide tabs that donor/receiver cannot edit.
  //=============================================================//

  /**
   * Hide the medications editing tab from donors, as only clinicians can edit medications.
   */
  void hideMedicationsEditingFromDonor() {
    if (session.getUserType().equalsIgnoreCase(DONOR)) {
      mainTabPane.getTabs().remove(medicationsTab);
    }
  }

  private void hideOrShowMedicalProcedures() {
    if (session.getUserType().equalsIgnoreCase(DONOR)) {
      mainTabPane.getTabs().remove(diseasesTab);
      mainTabPane.getTabs().remove(medicalProceduresTab);

    } else {
      medicalProceduresTab.setDisable(false);
      populateMedicalProcedureTables();
    }
  }

  private void hideOrShowMedicationsTab() {
    if (session.getUserType().equalsIgnoreCase(DONOR)) {
      medicationsTab.setDisable(true);
    } else {
      medicationsTab.setDisable(false);
    }
  }


  /**
   * Checks if the user who is editing the profile is a clinician and if the donor has died. If so, the editing functionality for their death details
   * is hidden from them as they should not be able to edit the death details
   */
  private void initDeathDetails() {
    String user = session.getUserType();
    if (!user.equalsIgnoreCase("clinician")) {
      setDeathDetailsButton.setVisible(false);
      clearDeathDetailsButton.setVisible(false);
      dateOfDeathText.setVisible(false);

    }
    // Case if the Donor has already died
    if (donorReceiver.getDeathDetails().getDoD() != null) {
      setDeathDetailsButton.setVisible(false);
      clearDeathDetailsButton.setVisible(false);
      dateOfDeathText.setVisible(false);
    }

    if (donorReceiver.getDeathDetails().getDoD() == null) {
      deathDetailsText.setVisible(false);
      editDateOfDeath.setVisible(false);
      editDeathCityLabel.setVisible(false);
      editDeathCountryLabel.setVisible(false);
      editDeathRegionLabel.setVisible(false);
      deathDateText.setVisible(false);
      deathCityText.setVisible(false);
      deathCountryText.setVisible(false);
      deathRegionText.setVisible(false);

    } else {
      editDateOfDeath.setText(ViewProfilePaneController.formatDateTime(donorReceiver.getDeathDetails().getDoD()));
      editDeathCityLabel.setText(staticAccount.getDeathDetails().getRegion());
      editDeathCountryLabel.setText(staticAccount.getDeathDetails().getCity());
      editDeathRegionLabel.setText(staticAccount.getDeathDetails().getCountry());
      editDateOfDeath.setVisible(true);
      editDeathCityLabel.setVisible(true);
      editDeathCountryLabel.setVisible(true);
      editDeathRegionLabel.setVisible(true);
    }
  }

  //=============================================================//

  //=============================================================//
  //Medical History Tab
  //=============================================================//


  @FXML
  /**
   *  Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets the Illness donor
   *  attribute to the current Account being edited.
   */
  void createDiagnosesButtonPressed(ActionEvent event) {

    createOrModifyIllnessController.setDonor(donorReceiver);
    createOrModifyIllnessController.setIllness(null);
    loadIllnessPage();
  }


  /**
   * Load the illness window on top of the current scene.
   */
  public void loadIllnessPage() {
    try {
      // Create new pane.
      FXMLLoader loader = new FXMLLoader();
      VBox diseasePane = loader.load(getClass().getResourceAsStream(PageNav.ILLNESS));

      // Create new scene.
      Scene diseaseScene = new Scene(diseasePane);

      // Retrieve current stage and set scene.
      Stage current = (Stage) Cancel.getScene().getWindow();
      current.setScene(diseaseScene);

    } catch (IOException exception) {
      System.out.println("Error loading illness pane");
    }

  }


  /**
   * An alert error message dialog box which is called when there has been an error in the GUI
   * diagnosis selection.
   *
   * @param message A string of the selection error message to display in the alert box.
   */
  public void showSelectionErrorDialog(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ERROR_DIALOG);
    alert.setHeaderText("Invalid Selection");
    alert.setContentText(message);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets
   * the Illness donor attribute to the current Account being edited as well as the Illness illness
   * attribute to the diagnosis selected in the current diagnosis table in the GUI.
   */
  @FXML
  public void editCurrentDiagnosesButtonPressed(ActionEvent event) {
    Illness current = currentTable.getSelectionModel().getSelectedItem();
    if (current != null) {
      createOrModifyIllnessController.setIllness(current);
      createOrModifyIllnessController.setDonor(donorReceiver);
      loadIllnessPage();
    } else {
      showSelectionErrorDialog(
          "No current diagnosis has been selected. Please select a diagnosis to edit.");
    }
  }


  /**
   * Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets
   * the Illness donor attribute to the current Account being edited as well as the Illness illness
   * attribute to the diagnosis selected in the historic diagnosis table in the GUI.
   */
  @FXML
  void editHistoricDiagnosesButtonPressed(ActionEvent event) {
    Illness historic = historicTable.getSelectionModel().getSelectedItem();
    if (historic != null) {
      createOrModifyIllnessController.setIllness(historic);
      createOrModifyIllnessController.setDonor(donorReceiver);
      loadIllnessPage();
    } else {
      showSelectionErrorDialog(
          "No historic diagnosis has been selected. Please select a diagnosis to edit.");
    }
  }


  /**
   * Converts the boolean values of the cells in the chronic TableColumn into strings, 'true'
   * becomes 'chronic' (in red text), and 'false' becomes 'no' (in black text). This method is based
   * on code by two posts, one post from user jkaufmann on Stack Overflow on 11/10/2011 at 2.44pm.
   * See https://stackoverflow.com/questions/6998551/setting-font-color-of-javafx-tableview-cells.
   * The other post was from user Grimerie at 6/4/2016 at 6.05pm. See
   * https://stackoverflow.com/questions/36436169/boolean-to-string-in-tableview-javafx.
   */
  public void setChronicColoumn() {
    chronic.setSortable(false);
    chronic.setCellValueFactory(new PropertyValueFactory<Illness, String>("chronic"));
    //Converts boolean cell values into strings
    chronic.setCellValueFactory(cellData -> {
      boolean chronic = cellData.getValue().isChronic();
      String chronicString;
      if (chronic) {
        chronicString = "chronic";
      } else {
        chronicString = "no";
      }
      return new ReadOnlyStringWrapper(chronicString);
    });
    chronic
        .setCellFactory(new Callback<TableColumn<Illness, String>, TableCell<Illness, String>>() {
          @Override
          public TableCell<Illness, String> call(TableColumn<Illness, String> param) {
            return new TableCell<Illness, String>() {
              @Override
              public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                //override default CellFactory to colour the cell text depending on the cell value.
                if (!isEmpty()) {
                  if (item.contains("chronic")) {
                    this.setTextFill(Color.RED);
                  } else {
                    this.setTextFill(Color.BLACK);
                  }
                  setText(item);
                }
              }
            };
          }
        });
  }


  /**
   * A success dialog alert box given if an Illness object has been deleted. The given message is
   * displayed in the alert.
   *
   * @param message A string to display in the alert box, in the GUI.
   **/
  public void showDeletionSuccessMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Deletion Success");
    alert.setContentText(
        String.format("%s Please save the application to make the deletion permanent.", message));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  @FXML
  /**
   * Checks if any Diagnoses have been selected for deletion by the user in the medical history GUI. If one or more
   * have been selected then callDeleteConfirmation is called. Otherwise an selection error message is shown.
   */
  void deleteDiagnosisButtonPressed(ActionEvent event) {
    Illness historic = historicTable.getSelectionModel().getSelectedItem();
    Illness current = currentTable.getSelectionModel().getSelectedItem();
    if (historic != null) {
      callDeleteConfirmation(historic);callDeleteConfirmation(historic);
    } else if (current != null) {
      callDeleteConfirmation(current);
    } else {
      showSelectionErrorDialog(
              "No diagnosis has been selected. Please select a diagnosis to delete.");
    }
  }


  /**
   * Attempts to delete the given donor's illness diagnosis from their medical history. The user
   * must press the 'OK' button to proceed with the deletion. The user may optionally pick two
   * diagnoses to delete. A log of the deletion will be created if the deletion is carried out.
   *
   * @param diagnosis An Illness object to be deleted.
   */
  private void callDeleteConfirmation(Illness diagnosis) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.setTitle("Delete selected Diagnosis/Diagnoses.");
    alert.setHeaderText("You are about to perform a delete operation.");
    alert.setContentText(String.format("The Diagnosis %s will be deleted. Do you wish to proceed?", diagnosis.getName()));
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent()) {
      if (result.get() == ButtonType.OK) {
        deleteIllness(diagnosis);
      }
    }
  }

  private void deleteIllness(Illness illness) {
    PageNav.loading();
    DeleteTask deleteTask = new DeleteTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.DELETE_ILLNESSES.getAddress(),staticAccount.getUserName()), illness, session.getToken());
    deleteTask.setOnSucceeded(event -> {
      PageNav.loaded();
      currentDiagnoses.remove(illness);
      historicDiagnoses.remove(illness);
      currentTable.setItems(currentDiagnoses);
      historicTable.setItems(historicDiagnoses);
      setChronicColoumn();
    });
    deleteTask.setOnFailed(event -> {
      showBadSaveError();
      PageNav.loaded();
    });
    new Thread(deleteTask).start();
  }


  //=============================================================//
  //Medical Procedure History Tab
  //=============================================================//



  /**
   * Overwrites the default comparator for the java fx TableView to order by chronic illnesses
   * first, and then by date. Currently cannot order by name.
   */
  public void orderMedicalProceduresTable() {

    pastProceduresTable.sortPolicyProperty()
        .set(new Callback<TableView<MedicalProcedure>, Boolean>() {
          @Override
          public Boolean call(TableView<MedicalProcedure> param) {
            Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
              public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                LocalDate date1 = i1.getDate();
                LocalDate date2 = i2.getDate();
                if (editPastProceduresDateColumn.getSortType() == TableColumn.SortType.ASCENDING) {
                  return date1.compareTo(date2) * -1;
                } else {
                  return date1.compareTo(date2);
                }
              }
            };
            FXCollections.sort(pastProceduresTable.getItems(), comparator);
            return true;
          }
        });
    pendingProcedureTable.sortPolicyProperty()
        .set(new Callback<TableView<MedicalProcedure>, Boolean>() {
          @Override
          public Boolean call(TableView<MedicalProcedure> param) {
            Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
              public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                LocalDate date1 = i1.getDate();
                LocalDate date2 = i2.getDate();
                if (editPendingProceduresDateColumn.getSortType()
                    == TableColumn.SortType.ASCENDING) {
                  if (date1 == null && date2 == null) {
                    return 0;
                  } else if (date1 == null) {
                    return -1;
                  } else if (date2 == null) {
                    return 1;
                  } else {
                    return date1.compareTo(date2);
                  }
                } else {
                  if (date1 == null && date2 == null) {
                    return 0;
                  } else if (date1 == null) {
                    return 1;
                  } else if (date2 == null) {
                    return -1;
                  } else {
                    return date1.compareTo(date2) * -1;
                  }
                }
              }
            };
            FXCollections.sort(pendingProcedureTable.getItems(), comparator);
            return true;
          }
        });

  }

  private void populateMedicalProcedureTables() {
    editPastProceduresSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
    editPastProceduresDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    editPastProceduresDateColumn.setCellFactory(column -> {
      return new TableCell<MedicalProcedure, LocalDate>() {
        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
          } else {
            setText(myDateFormatter.format(item));
          }
        }
      };
    });
    editPendingProceduresSummaryColumn.setCellFactory(tc -> {
      TableCell<MedicalProcedure, String> cell = new TableCell<>();
      Text text = new Text();
      cell.setGraphic(text);
      cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
      text.wrappingWidthProperty().bind(editPendingProceduresSummaryColumn.widthProperty());
      text.textProperty().bind(cell.itemProperty());
      return cell;
    });
    editPendingProceduresSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
    editPendingProceduresDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    editPendingProceduresDateColumn.setCellFactory(column -> {
      return new TableCell<MedicalProcedure, LocalDate>() {
        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
          } else {
            setText(myDateFormatter.format(item));
          }
        }
      };
    });
    editPastProceduresSummaryColumn.setCellFactory(tc -> {
      TableCell<MedicalProcedure, String> cell = new TableCell<>();
      Text text = new Text();
      cell.setGraphic(text);
      cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
      text.wrappingWidthProperty().bind(editPastProceduresSummaryColumn.widthProperty());
      text.textProperty().bind(cell.itemProperty());
      return cell;
    });
    pastProceduresTable.setItems(pastProcedures);
    pendingProcedureTable.setItems(pendingProcedures);
    orderMedicalProceduresTable();
  }

  @FXML
  void addNewProcedureButtonPressed(ActionEvent event) throws IOException {
    AddEditProcedureController.setMedicalProcedure(null);
    AddEditProcedureController.setDonorReceiver(donorReceiver);
    loadAddEditProcedureWindow();
  }

  @FXML
  void pastProcedureDeleteButtonPressed(ActionEvent event) throws Exception {
    MedicalProcedure procedureToDelete = pastProceduresTable.getSelectionModel().getSelectedItem();
    if (procedureToDelete != null) {
      areYouSureDeleteProcedure(procedureToDelete);
    }
  }

  void areYouSureDeleteProcedure(MedicalProcedure procedure) throws Exception {
    Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
    deleteConfirmation.setTitle("Medical Procedure Deletion");
    deleteConfirmation.setHeaderText("Are you sure you want to delete this medical procedure?");
    deleteConfirmation.setContentText(
        "Are you sure you want to delete medical procedure: '" + procedure.getSummary() + "'?");
    Optional<ButtonType> result = deleteConfirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      ProcedureDelete procedureDelete = new ProcedureDelete(procedure.getSummary());
      DeleteTask task = new DeleteTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_PROCEDURE.getAddress(), staticAccount.getUserName()), procedureDelete, App.getCurrentSession().getToken());
      task.setOnSucceeded(event -> {
        pastProceduresTable.getItems().clear();
        pendingProcedureTable.getItems().clear();
        try {
          donorReceiver.deleteMedicalProcedure(donorReceiver, procedure);
          pastProceduresTable.getItems().addAll(donorReceiver.extractPastProcedures());
          pendingProcedureTable.getItems().addAll(donorReceiver.extractPendingProcedures());
        } catch (Exception e) {
          showBadSaveError();
        }
      });
      task.setOnFailed(event -> {
        showBadSaveError();
      });
      new Thread(task).start();
    }
  }

  @FXML
  void pastProcedureEditButtonPressed() throws IOException {
    MedicalProcedure procedureToEdit = pastProceduresTable.getSelectionModel().getSelectedItem();
    if (procedureToEdit != null) {
      AddEditProcedureController.setDonorReceiver(donorReceiver);
      AddEditProcedureController.setMedicalProcedure(procedureToEdit);
      loadAddEditProcedureWindow();
    }
  }

  @FXML
  void pendingProcedureDeleteButtonPressed() throws Exception {
    MedicalProcedure procedureToDelete = pendingProcedureTable.getSelectionModel()
        .getSelectedItem();
    if (procedureToDelete != null) {
      areYouSureDeleteProcedure(procedureToDelete);
    }
  }

  @FXML
  void pendingProcedureEditButtonPressed() throws IOException {
    MedicalProcedure procedureToEdit = pendingProcedureTable.getSelectionModel().getSelectedItem();
    if (procedureToEdit != null) {
      AddEditProcedureController.setDonorReceiver(donorReceiver);
      AddEditProcedureController.setMedicalProcedure(procedureToEdit);
      loadAddEditProcedureWindow();
    }
  }

  @FXML
  void pastProcedureClicked(MouseEvent event) {
    procedureDescriptionAffectedOrgansPopulate(false);
  }

  @FXML
  void pendingProcedureClicked(MouseEvent event) {
    procedureDescriptionAffectedOrgansPopulate(true);
  }

  private void procedureDescriptionAffectedOrgansPopulate(boolean pending) {
    MedicalProcedure procedureClicked;
    if (pending) {
      procedureClicked = pendingProcedureTable.getSelectionModel().getSelectedItem();
    } else {
      procedureClicked = pastProceduresTable.getSelectionModel().getSelectedItem();
    }
    String affectedOrgansText = "";
    try {
      ArrayList<String> affectedOrgans = procedureClicked.getAffectedOrgans();
      for (String affectedOrgan : affectedOrgans) {
        if (affectedOrgansText.length() == 0) {
          affectedOrgansText = affectedOrgansText + affectedOrgan;
        } else {
          affectedOrgansText = affectedOrgansText + ", " + affectedOrgan;
        }
      }
      if (pending) {
        if (affectedOrgansText.length() == 0) {
          pendingProcedureAffectedOrgans.setText("None");
        } else {
          pendingProcedureAffectedOrgans.setText(affectedOrgansText);
        }
        pendingProcedureDescription.setText(procedureClicked.getDescription());
      } else {
        if (affectedOrgansText.length() == 0) {
          pastProcedureAffectedOrgans.setText("None");
        } else {
          pastProcedureAffectedOrgans.setText(affectedOrgansText);
        }
        pastProcedureDescription.setText(procedureClicked.getDescription());
      }
    } catch (NullPointerException e) {
      System.out.print(" ");
    }
  }

  /**
   * Loads the add edit procedure scene in the donor's window.
   *
   * @throws IOException When the FXML cannot be retrieved.
   */
  private void loadAddEditProcedureWindow() throws IOException {
    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    AnchorPane addEditProcedure = loader.load(getClass().getResourceAsStream(PageNav.PROCEDURE));

    // Create new scene.
    Scene addEditProcedureScene = new Scene(addEditProcedure);

    // Retrieve current stage and set scene.
    Stage current = (Stage) Cancel.getScene().getWindow();
    current.setScene(addEditProcedureScene);

  }

  //=============================================================//

  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ERROR_DIALOG);
    alert.setHeaderText("Save failed");
    alert.setContentText("Whoops, looks like something went wrong. Please try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showConflictError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ERROR_DIALOG);
    alert.setHeaderText("Conflict");
    alert.setContentText("Whoops, looks like someone else edited the account while you were! Please try again");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Modify what is displayed/editable based on who's logged in
   */
  private void handleView() {
    if (App.getCurrentSession().getUserType().equalsIgnoreCase(DONOR)) {    // Check that it is a donor logged in and that they're a receiver before hiding
      disableReceiver();
      hideReceiver();
    } else {
      CheckBox[] receiverOrgansCheckBox = {
          editReceiverLiver, editReceiverKidney, editReceiverLung, editReceiverHeart,
          editReceiverPancreas,
          editReceiverIntestine, editReceiverCornea, editReceiverMiddleEar, editReceiverBone,
          editReceiverBoneMarrow,
          editReceiverSkin, editReceiverConnectiveTissue
      };
      for (CheckBox receiverOrgan : receiverOrgansCheckBox) {
        if (receiverOrgan.isSelected()) {
          receiverOrgan.setDisable(true);
        }
      }
    }
  }

  /**
   * Calls all listeners, to check for updates.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  private void editAll(ActionEvent event) {
    errorMessage = "";

    if (checkValidity()) {
      invalidFlag = true;
      showBadSaveError();
      event.consume();
    } else {
      editUserAttributeCollection();
      editDeathDetails();
    }
  }

  /**
   * Checks if all fields have a valid variable
   * @return a boolean which shows if any variables entered are bad
   */
  private boolean checkValidity() {
    boolean valid = checkBasicInformation();
    boolean valid1 = checkContactDetails();
    boolean valid2 = checkMedicalHistory();

    if ((valid) || (valid1) || (valid2)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks the validity of all fields on the basic information pane
   * @return a boolean that depends on the validity of the entered values.
   */
  private boolean checkBasicInformation() {

    boolean notValid = false;
    if (!UserValidator.validateAlphanumericString(false, editGivenNames.getText(), 0, 50)) {
      notValid = true;
      editGivenNames.setStyle(BORDER_RED);
    } else {
      editGivenNames.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateAlphanumericString(false, editLastName.getText(), 0, 50)) {
      notValid = true;
      editLastName.setStyle(BORDER_RED);
    } else {
      editLastName.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateAlphanumericString(false, editPreferredName.getText(), 0, 50)) {
      notValid = true;
      editPreferredName.setStyle(BORDER_RED);
    } else {
      editPreferredName.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.checkNHIRegex(editNHINumber.getText())) {
      notValid = true;
      editNHINumber.setStyle(BORDER_RED);
    } else {
      editNHINumber.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateDateOfBirth(editDateOfBirth.getValue())) {
      notValid = true;
      editDateOfBirth.setStyle(BORDER_RED);
    } else {
      editDateOfBirth.setStyle(BORDER_SILVER);
    }
    try {
      double height = Double.parseDouble(editHeight.getText());
      if (!UserValidator.validateHeight(height)) {
        notValid = true;
        editHeight.setStyle(BORDER_RED);
      } else {
        editHeight.setStyle(BORDER_SILVER);
      }
    } catch (NumberFormatException e) {
      if (!editHeight.getText().trim().equals("")) {
        notValid = true;
        editHeight.setStyle(BORDER_RED);
      }
    }

    try {
      double weight = Double.parseDouble(editWeight.getText());
      if (!UserValidator.validateWeight(weight)) {
        notValid = true;
        editWeight.setStyle(BORDER_RED);
      } else {
        editWeight.setStyle(BORDER_SILVER);
      }
    } catch (NumberFormatException e) {
      if (!editWeight.getText().trim().equals("")) {
        notValid = true;
        editWeight.setStyle(BORDER_RED);
      }
    }
    return notValid;
  }

  /**
   * Checks the validity of the given contact details
   * @return a boolean if they are valid are not
   */
  private boolean checkContactDetails() {
    boolean notValid = false;
    if (!UserValidator.validateAlphanumericString(true, editStreetAddress.getText(), 0, 100)) {
      notValid = true;
      editStreetAddress.setStyle(BORDER_RED);
    } else {
      editStreetAddress.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateAlphanumericString(true, editEmergStreetAddress.getText(), 0, 100)) {
      notValid = true;
      editEmergStreetAddress.setStyle(BORDER_RED);
    } else {
      editEmergStreetAddress.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateAlphanumericString(false, editCity.getText(), 0, 100)) {
      notValid = true;
      editCity.setStyle(BORDER_RED);
    } else {
      editCity.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validateAlphanumericString(false, editEmergCity.getText(), 0, 100)) {
      notValid = true;
      editEmergCity.setStyle(BORDER_RED);
    } else {
      editEmergCity.setStyle(BORDER_SILVER);
    }
    try {
      int postcode = Integer.parseInt(editPostcode.getText());
      if (!UserValidator.validatePostCode(postcode)) {
        notValid = true;
        editPostcode.setStyle(BORDER_RED);
      } else {
        editPostcode.setStyle(BORDER_SILVER);
      }
    } catch (NumberFormatException e) {
      if (!editPostcode.getText().trim().equals("")) {
        notValid = true;
        editPostcode.setStyle(BORDER_RED);
      }
    }
    try {
      int postcode = Integer.parseInt(editEmergPostcode.getText());
      if (!UserValidator.validatePostCode(postcode)) {
        notValid = true;
        editEmergPostcode.setStyle(BORDER_RED);
      } else {
        editEmergPostcode.setStyle(BORDER_SILVER);
      }
    } catch (NumberFormatException e) {
      if (!editEmergPostcode.getText().trim().equals("")) {
        notValid = true;
        editEmergPostcode.setStyle(BORDER_RED);
      }
    }
    if (!UserValidator.validatePhoneNumber(editMobileNumber.getText())) {
      notValid = true;
      editMobileNumber.setStyle(BORDER_RED);
    } else {
      editMobileNumber.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validatePhoneNumber(editEmergMobileNumber.getText())) {
      if (!editEmergMobileNumber.getText().trim().equals("")) {
        notValid = true;
        editEmergMobileNumber.setStyle(BORDER_RED);
      }
    } else {
      editEmergMobileNumber.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validatePhoneNumber(editHomeNumber.getText())) {
      if (!editHomeNumber.getText().trim().equals("")) {
        notValid = true;
        editHomeNumber.setStyle(BORDER_RED);
      }
    } else {
      editHomeNumber.setStyle(BORDER_SILVER);
    }
    if (!UserValidator.validatePhoneNumber(editEmergHomeNumber.getText())) {
      if (!editEmergHomeNumber.getText().trim().equals("")) {
        notValid = true;
        editEmergHomeNumber.setStyle(BORDER_RED);
      }
    } else {
      editEmergHomeNumber.setStyle(BORDER_SILVER);
    }
    if (!ContactDetails.validateEmail(editEmail.getText())) {
      if (!editEmail.getText().trim().equals("")) {
        notValid = true;
        editEmail.setStyle(BORDER_RED);
      }
    } else {
      editEmail.setStyle(BORDER_SILVER);
    }
    if (!ContactDetails.validateEmail(editEmergEmail.getText())) {
      if (!editEmergEmail.getText().trim().equals("")) {
        notValid = true;
        editEmergEmail.setStyle(BORDER_RED);
      }
    } else {
      editEmergEmail.setStyle(BORDER_SILVER);
    }
    return notValid;
  }

  /**
   * Checks the validity of the given medical history vales
   * @return a boolean if they are valid are not
   */
  private boolean checkMedicalHistory() {
    boolean notValid = false;
    if (!UserValidator.validateBloodPressure(editBloodPressure.getText())) {
      notValid = true;
      editBloodPressure.setStyle(BORDER_RED);
    } else {
      editBloodPressure.setStyle(BORDER_SILVER);
    }
    try {
      Double.parseDouble(editAlcoholConsumption.getText());
      editAlcoholConsumption.setStyle(BORDER_SILVER);
    } catch (NumberFormatException e) {
      notValid = true;
      editAlcoholConsumption.setStyle(BORDER_RED);
    }

    return notValid;
  }


  /**
   * Controls when the close button is pressed. Closes the window but does not save changes. Changes
   * are lost
   */
  @FXML
  private void cancelSelected() {
    handlePageSwitching();
    donorReceiver = null;
    undoableManager.getCommandStack().save();
  }

  /**
   * Controls when the done button is pressed. Updates using data entered in the edit pane for the
   * current session. Not a permanent save
   *
   * @param event The clicking of the button
   */
  @FXML
  private void doneSelected(ActionEvent event) {
    invalidFlag = false;
    editAll(event);
    if (invalidFlag) {
      event.consume();
    } else {
      try {
        DonorListController.triggerRefresh();
      } catch (NullPointerException exception) {
        // Catch case where DonorListController is null because the main
        // window is not currently on that screen.
      }
    }
  }


  /**
   * Sets the isChild property.
   *
   * @param isChildValue The value to set isChild to.
   */
  public static void setIsChild(boolean isChildValue) {

    isChild = isChildValue;

  }


  /**
   * Handles how page switching occurs, which is dependent on whether or not the current window is a
   * child window.
   */
  private void handlePageSwitching() {
    PageNav.loading();
    String endpoint = ADDRESSES.GET_DONOR.getAddress() + staticAccount.getUserName();
    GetTask task = new GetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      PageNav.loaded();
      EditPaneController.staticAccount = (DonorReceiver) task.getValue();
      ViewProfilePaneController.setAccount(staticAccount);
      if (instanceIsChild) {
        try {
          loadProfileWindow();
        } catch (IOException exception) {
          System.out.println("Error loading the view pane");
        }
      } else {
        PageNav.loaded();
        PageNav.loadNewPage(PageNav.VIEW, mainTabPane.getSelectionModel().getSelectedIndex(),
                "profileViewTabbedPane");
      }
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get donor");

    });
    new Thread(task).start();

  }

  /**
   * Change the pane to hide any details of being a receiver to the user
   */
  private void hideReceiver() {
    organTabPane.getTabs().remove(receiverTab);
  }

  /**
   * Disable the reciever organs tickbox if its not a clinician
   */
  private void disableReceiver() {
    // Receiving organs
    editReceiverLiver.setDisable(true);
    editReceiverKidney.setDisable(true);
    editReceiverLung.setDisable(true);
    editReceiverHeart.setDisable(true);
    editReceiverPancreas.setDisable(true);
    editReceiverIntestine.setDisable(true);
    editReceiverCornea.setDisable(true);
    editReceiverMiddleEar.setDisable(true);
    editReceiverBone.setDisable(true);
    editReceiverBoneMarrow.setDisable(true);
    editReceiverSkin.setDisable(true);
    editReceiverConnectiveTissue.setDisable(true);
  }

  /**
   * Returns to the profile screen within the same window.
   */
  private void loadProfileWindow() throws IOException {

    // Set child status.
    ViewProfilePaneController.setIsChild(true);
    ViewProfilePaneController.setAccount(staticAccount);
    ViewProfilePaneController.setStaticUsername(staticAccount.getUserName());

    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    StackPane editPane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

    // Create new scene.
    Scene editScene = new Scene(editPane);
    TabPane tabPane = (TabPane) editPane.lookup("#profileViewTabbedPane");
    int currentTab = mainTabPane.getSelectionModel().getSelectedIndex();
    if (currentTab >= 3) {
      currentTab++; //History tab is not in edit profile
    }

    tabPane.getSelectionModel().clearAndSelect(currentTab);

    // Retrieve current stage and set scene.
    Stage current = (Stage) Cancel.getScene().getWindow();
    current.setScene(editScene);

    undoableManager.getCommandStack().save();

  }

  // Undo and Redo

  /**
   * Adds an undoable event when a checkbox is selected or unselected
   *
   * @param event Event of un/selection od checkbox
   */
  @FXML
  private void checkBoxEvent(ActionEvent event) {
    undoableManager.createDonationUndoable(event);
  }

  /**
   * Undos the last event on the command stack while editing
   */
  @FXML
  private void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undid event on the command stack while editing
   */
  @FXML
  private void redoEvent() {
    redoCalled();
  }

  /**
   * Calls the undo event when using menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Choice Box")) {
      undoRedoChoiceBox = true;
    }
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
   * Calls the redo event when using menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Choice Box")) {
      undoRedoChoiceBox = true;
    }
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


  @FXML
  public void setDeathDetails() {
    Dialog<Pair<String, LocalDateTime>> dialog = new Dialog<>();
    dialog.setTitle("Input death details");
    dialog.setHeaderText("Please input details of death");
    dialog.getDialogPane().setPrefSize(890, 300);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 10, 10, 10));

    Label dateLabel = new Label();  //Date of death label
    dateLabel.setText("Date and time of Death: ");

    DatePicker date = new DatePicker();  //For the date of death option
    date.setValue(LocalDate.now());

    TextField timeOfDeath = new TextField();
    timeOfDeath.setText("12:00");
    Label errorDateTimeOfDeath = new Label(); //Error label for date/time of death
    errorDateTimeOfDeath.setText("Please enter time in the form hh:mm");
    errorDateTimeOfDeath.setTextFill(Color.RED);
    errorDateTimeOfDeath.setVisible(false);

    Label countryLabel = new Label();  //Country Date of death label
    countryLabel.setText("Country of Death: ");

    Label cityLabel = new Label();  // city Date of death label
    cityLabel.setText("City of Death: ");

    Label enterCityErrorLabel = new Label(); //Error label for city of death
    enterCityErrorLabel.setText("Please enter a city/town");
    enterCityErrorLabel.setTextFill(Color.RED);
    enterCityErrorLabel.setVisible(false);

    Label regionLabel = new Label();  //region Date of death label
    regionLabel.setText("Region of Death: ");

    ComboBox<String> chooseCountry = new ComboBox<>();
    chooseCountry.getItems().addAll(countryList.getAllowableCountries());
    chooseCountry.getSelectionModel().select(NEW_ZEALAND);

    ComboBox<String> chooseRegion = new ComboBox<>();
    chooseRegion.getItems().addAll(regionList.getRegions("NZ"));
    chooseRegion.getSelectionModel().select(9);

    TextField typeRegion = new TextField();
    typeRegion.setVisible(false);
    Label typeRegionError = new Label();
    typeRegionError.setTextFill(Color.RED);
    typeRegionError.setText("Please enter a region");
    typeRegionError.setVisible(false);

    TextField enterCity = new TextField();

    grid.add(countryLabel, 1, 1);
    grid.add(chooseCountry, 2, 1);
    grid.add(regionLabel, 1, 2);
    grid.add(chooseRegion, 2, 2);
    grid.add(typeRegion, 2, 2);
    grid.add(typeRegionError, 3, 2);
    grid.add(cityLabel, 1, 3);
    grid.add(enterCity, 2, 3);
    grid.add(enterCityErrorLabel, 3, 3);
    grid.add(dateLabel, 1, 4);
    grid.add(date, 2, 4);
    grid.add(timeOfDeath, 3, 4);
    grid.add(errorDateTimeOfDeath, 4, 4);


    chooseCountry.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              if (chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEW_ZEALAND)) {
                typeRegion.setVisible(false);
                chooseRegion.setVisible(true);
              } else {
                chooseRegion.setVisible(false);
                typeRegion.setVisible(true);
              }
            });

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.addEventFilter(ActionEvent.ACTION, event2 -> {
      enterCityErrorLabel.setVisible(false);
      errorDateTimeOfDeath.setVisible(false);
      typeRegionError.setVisible(false);
      boolean isValid = true;
      if (date.getValue() == null) {
        errorDateTimeOfDeath.setText("Date of death cannot be blank");
        errorDateTimeOfDeath.setVisible(true);
        isValid = false;
        event2.consume();
      }
      if ((!chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEW_ZEALAND))
          && (typeRegion.getText().equalsIgnoreCase(""))) {
        typeRegionError.setVisible(true);
        isValid = false;
        event2.consume();
      }
      if (enterCity.getText().equalsIgnoreCase("")) {
        enterCityErrorLabel.setVisible(true);
        isValid = false;
        event2.consume();
      }
      if (!correctInputForm(timeOfDeath.getText())) {
        errorDateTimeOfDeath.setVisible(true);
        errorDateTimeOfDeath.setText("Please enter time in the form hh:mm");
        isValid = false;
        event2.consume();
      }
      if (isValid) {
        LocalTime localTime = LocalTime.parse(timeOfDeath.getText());
        LocalDateTime localDateTime = LocalDateTime.of(date.getValue(), localTime);
        if (localDateTime.isAfter(LocalDateTime.now())) {
          errorDateTimeOfDeath.setText("Date of death cannot be in the future");
          errorDateTimeOfDeath.setVisible(true);
          event2.consume();
        } else if (localDateTime
            .isBefore(LocalDateTime.of(donorReceiver.getDateOfBirth(), LocalTime.of(0, 0, 0)))) {
          errorDateTimeOfDeath.setText("Date of death cannot be before date of birth");
          errorDateTimeOfDeath.setVisible(true);
          event2.consume();
        } else {
          deathDetails.clear();
          ArrayList<Boolean> organsInList = donorReceiver.getRequiredOrgans().getOrgansInList();
          boolean found = false;
          for (boolean organ : organsInList) {
            if (organ) {
              found = true;
            }
          }
          deathDetails.add(AccountManager.getCurrentUser());
          deathDetails.add(chooseCountry.getSelectionModel().getSelectedItem());
          // if they died in new zealand, then region is set by the drop-down
          if (chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEW_ZEALAND)) {
            deathDetails.add(chooseRegion.getSelectionModel().getSelectedItem());
          } else {
            // if they didn't die in new zealand, then region is set by text field
            deathDetails.add(typeRegion.getText());
          }
          deathDetails.add(enterCity.getText());
          deathDetails.add(localDateTime);
          deathDetails.add(date.getValue());
          deathDetails.add(found);
        }
      }
    });
    dialog.showAndWait();
  }

  /**
   * Checks if a string can be parsed into a LocalTime instance
   * @param string The string to be parsed
   * @return A boolean depicting whether or not it is possible to parse the string.
   */

  private boolean correctInputForm(String string) {
    string = string.trim();
    try {
      LocalTime.parse(string);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  @FXML
  public void clearDeathDetails() {
    Dialog<Pair<String, LocalDateTime>> dialog = new Dialog<>();
    dialog.setTitle("Clear Death details");
    dialog.setHeaderText("Are you sure you want to clear the users death details?");
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);


    final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
    yesButton.addEventFilter(ActionEvent.ACTION, event2 -> {
      DeathDetails DeathDetails = donorReceiver.getDeathDetails();
      if (DeathDetails.getDoD() != null) {
        donorReceiver.clearDeathDetails(AccountManager.getCurrentUser());
      }
      if (!deathDetails.isEmpty()) {
        deathDetails.clear();
      }
    });
    dialog.showAndWait();
  }

  public void editDeathDetails() {
    if (deathDetails.size() != 0) {
      if ((boolean) deathDetails.get(6)) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", donorReceiver.getUserName(), "organs", "receiver", "all",
                "false, reason for removal: Death,");
        donorReceiver.setReceiver(false);
      }
      donorReceiver.updateDeathDetails(AccountManager.getCurrentUser(), (String) deathDetails.get(1), (String) deathDetails.get(2), (String) deathDetails.get(3), (LocalDateTime) deathDetails.get(4));
    }

  }

  public void updateRegions() {
    String selectedCountryName;
    String selectedCountryCode;
    Collection newRegions;

    selectedCountryName = editCountry.getSelectionModel().getSelectedItem();
    // Check if the newly selected country is different from the user's current country.
    if (selectedCountryName.equalsIgnoreCase(donorReceiver.getAddressCountryName())) {
      // If country has not changed, exit this method.
      return;
    }
    // Get the list of regions in the newly selected country and add them to the regions choice box.
    selectedCountryCode = countryList.getCountryCode(selectedCountryName);
    newRegions = regionList.getRegions(selectedCountryCode);
    editRegion.getItems().removeAll();
    editRegion.getItems().addAll(newRegions);
  }


  /**
   * Displays an alert error pop-up to the user with the given error message.
   * @param message
   */
  public void showBadPhotoMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(ERROR_DIALOG);
    alert.setHeaderText("Invalid Photo");
    alert.setContentText(
            message);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Calls a post request for the donor's profile photo to the server
   * @param file An image file which is a png, jpg, or gif
   * @throws FileNotFoundException When the image file could not be found
   * @throws IOException When the image failed to be read into a byte array
   */
  public void postPhoto(File file) throws FileNotFoundException, IOException {
    PageNav.loading();
    byte[] img = Files.readAllBytes(file.toPath());
    PostTask task = new PostTask(ADDRESSES.SERVER.getAddress(),
            ADDRESSES.POST_PHOTO.getAddress() + donorReceiver.getUserName() + "/photo", img, session.getToken() );
    task.setOnSucceeded(successEvent -> {
        getPhoto(nhi);
      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      PageNav.loaded();
      showBadPhotoMessage("Could not post photo, it may be corrupted");
    });
    new Thread(task).start();
  }


  /**
   * Sets the Donor profile's photo from a image they select from their local filesystem.
   */
  @FXML
  public void setPhoto() {
    try {
      File file = getPhotoFromFile();
      if (file != null) {
        try {
          Image image = new Image(new FileInputStream(file));
          if (image.getHeight() <= MAX_DIMENSION && image.getWidth() <= MAX_DIMENSION) {
            postPhoto(file);
          } else {
            showBadPhotoMessage(String.format("Image must be no more than %d by %d pixels", MAX_DIMENSION, MAX_DIMENSION));
          }
        } catch (IllegalArgumentException e) {
          showBadPhotoMessage(e.getMessage());
        } catch (IOException e) {
          showBadPhotoMessage(e.getMessage());
        }
      }
    }catch (URISyntaxException e) {
      showBadPhotoMessage("Could not get image from file source");
    }
  }


  /**
   * Creates a file picker for picking image files. The images can be jpg, png, or gif.
   * @return An image file
   * @throws URISyntaxException When there is an error getting the file from the filepath
   */
  public File getPhotoFromFile() throws URISyntaxException {
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter newFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif");
      fileChooser.getExtensionFilters().add(newFilter);
      fileChooser.setTitle("Set photo");
      File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
              .getCodeSource().getLocation().toURI()).getParent().toString());
      fileChooser.setInitialDirectory(defaultDirectory);
      File file = fileChooser.showOpenDialog(App.getWindow());
      if (file.length() > MAX_BYTES) {
        throw new IllegalArgumentException("File size has to be less than or equal to 5mb");
      } else  {
        return file;
      }
  }


  /**
   * Attempts to delete the donor's profile photo from the server. If their is no photo on the server
   * an error message appears. After the photo is deleted, the default photo is shown
   */
  @FXML
  public void deletePhoto() {
    PageNav.loading();
    DeleteTask task = new DeleteTask(ADDRESSES.SERVER.getAddress(), ADDRESSES.DELETE_PHOTO.getAddress() +
            donorReceiver.getUserName() + "/photo", session.getToken());

    task.setOnSucceeded(successEvent -> {
        getPhoto(nhi);
      PageNav.loaded();
    });
    task.setOnFailed( event -> {
      PageNav.loaded();
      showBadPhotoMessage("No photo to delete");
    });
    new Thread(task).start();
  }


  /**
   * Edit the user attribute collection of a donor account
   */
  private void editUserAttributeCollection() {
    String blood = "";
    if(editBloodType.getSelectionModel().getSelectedIndex() > 0)
      blood = editBloodType.getSelectionModel().getSelectedItem().toString();
    EditPaneController.CURRENT_VERSION++;
    double height;
    try {
      height = Double.parseDouble(editHeight.getText());
    } catch (NumberFormatException e) {
      height = 0.0;
    }
    double weight;
    try {
      weight = Double.parseDouble(editWeight.getText());
    } catch (NumberFormatException e) {
      weight = 0.0;
    }

    Double alcoholconsumption;
    try {
      alcoholconsumption = Double.parseDouble(editAlcoholConsumption.getText());
    } catch (NumberFormatException e) {
      alcoholconsumption = 0.0;
    }
    UserAttributeCollectionPatch collection = new UserAttributeCollectionPatch(height,
                                                                     weight,
                                                                     blood,
                                                                     false,
                                                                     editSmoker.isSelected(),
                                                                     editBloodPressure.getText(),
                                                                     alcoholconsumption,
                                                                     "",
                                                                     Integer.toString(EditPaneController.CURRENT_VERSION));
    if (editHeight.getText().equals("0.0") || editHeight.getText().trim().equals("")) {
      collection.setHeight("null");
    }
    if (editWeight.getText().equals("0.0") || editWeight.getText().trim().equals("")) {
      collection.setWeight("null");
    }
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_ATTRIBUTE_COLLECTION.getAddress(), staticAccount.getUserName()), collection, session.getToken());
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      editDonorOrgans();
    });
    task.setOnFailed(event -> {
        if(checkConflict(task.getException())){
          getDonor(staticAccount.getUserName(), true);
          showConflictError();
        } else {
          showBadSaveError();
        }
    });
    new Thread(task).start();

  }

  /**
   * Edit the donating organs of a donor
   */
  private void editDonorOrgans() {
    EditPaneController.CURRENT_VERSION++;
    DonorOrganInventoryPatch patch = new DonorOrganInventoryPatch(editLiver.isSelected(),
                                                                  editKidney.isSelected(),
                                                                  editPancreas.isSelected(),
                                                                  editHeart.isSelected(),
                                                                  editLung.isSelected(),
                                                                  editIntestine.isSelected(),
                                                                  editCornea.isSelected(),
                                                                  editMiddleEar.isSelected(),
                                                                  editSkin.isSelected(),
                                                                  editBone.isSelected(),
                                                                  editBoneMarrow.isSelected(),
                                                                  editConnectiveTissue.isSelected(),
                                                                  Integer.toString(EditPaneController.CURRENT_VERSION));
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.DONOR_ORGANS.getAddress(), staticAccount.getUserName()), patch, session.getToken());
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      editProfile();
    });
    task.setOnFailed(event -> {
      if(checkConflict(task.getException())){
        getDonor(staticAccount.getUserName(), true);
        showConflictError();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }


  /**
   * Creates a medication
   */
  public void createMedication(String newMedication) {
    Medication medication = new Medication(createNewMedication.getValue(), true);
    PageNav.loading();
    PostTask task = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_MEDICATION.getAddress(), staticAccount.getUserName()), medication, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      editCurrentMedications.getItems().add(newMedication);
      editCurrentMedications.refresh();
      undoableManager.createMedicationAddChange(editCurrentMedications, newMedication);
      createNewMedication.getItems().removeAll(createNewMedication.getItems());
      createNewMedication.getEditor().setText("");
      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      if(checkConflict(task.getException())){
        getDonor(staticAccount.getUserName(), true);
        showConflictError();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }

  private void editReceiverOrgans() {
    this.CURRENT_VERSION++;
    ReceiverOrganInventory organs = donorReceiver.getRequiredOrgans();
    ReceiverOrganInventoryPatch patch;
    patch = new ReceiverOrganInventoryPatch(editReceiverLiver.isSelected(),
            editReceiverKidney.isSelected(), editReceiverPancreas.isSelected(), editReceiverHeart.isSelected(),
            editReceiverLung.isSelected(), editReceiverIntestine.isSelected(), editReceiverCornea.isSelected(),
            editReceiverMiddleEar.isSelected(), editReceiverSkin.isSelected(), editReceiverBone.isSelected(),
            editReceiverBoneMarrow.isSelected(), editReceiverConnectiveTissue.isSelected(),
            organs.getLiverTimeStamp().toString(),organs.getKidneysTimeStamp().toString(),organs.getPancreasTimeStamp().toString(),
            organs.getHeartTimeStamp().toString(),organs.getLungsTimeStamp().toString(),organs.getIntestineTimeStamp().toString(),organs.getCorneasTimeStamp().toString(),
            organs.getMiddleEarsTimeStamp().toString(),organs.getSkinTimeStamp().toString(),organs.getBoneTimeStamp().toString(),
            organs.getBoneMarrowTimeStamp().toString(),organs.getConnectiveTissueTimeStamp().toString(), Integer.toString(CURRENT_VERSION));
    addReceiverTimes(patch, organs);
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.PATCH_RECEIVER_ORGANS.getAddress(), staticAccount.getUserName()), patch, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      handlePageSwitching();
    });
    task.setOnFailed(event -> {
      if(checkConflict(task.getException())){
        getDonor(staticAccount.getUserName(), true);
        showConflictError();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }

  /**
   * Checks if an organ is being donated and if it is changes the donation time
   * @param patch Request body to send which
   * @param organs Organ inventory to compare
   */
  public void addReceiverTimes(ReceiverOrganInventoryPatch patch, ReceiverOrganInventory organs) {
    LocalDateTime date = LocalDateTime.now();
    String now = date.toString();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime old = LocalDateTime.parse("1970-01-01 01:00", formatter);
    if(editReceiverLiver.isSelected() && (organs.getLiverTimeStamp() == null || organs.getLiverTimeStamp().equals(old))) {
      patch.setLiverTimeStamp(now);
    } else {
      patch.setLiverTimeStamp(organs.getLiverTimeStamp().toString());
    }
    if(editReceiverKidney.isSelected() && (organs.getKidneysTimeStamp() == null) || organs.getKidneysTimeStamp().equals(old)) {
      patch.setKidneysTimeStamp(now);
    } else{
      patch.setKidneysTimeStamp(organs.getKidneysTimeStamp().toString());
    }
    if(editReceiverPancreas.isSelected() && ( organs.getPancreasTimeStamp() == null || organs.getPancreasTimeStamp().equals(old))) {
      patch.setPancreasTimeStamp(now);
    } else{
      patch.setPancreasTimeStamp(organs.getPancreasTimeStamp().toString());
    }
    if(editReceiverHeart.isSelected() && organs.getHeartTimeStamp().equals(old)) {
      patch.setHeartTimeStamp(now);
    } else {
      patch.setHeartTimeStamp(organs.getHeartTimeStamp().toString());
    }
    if(editReceiverLung.isSelected() && organs.getLungsTimeStamp().equals(old)) {
      patch.setLungsTimeStamp(now);
    } else {
      patch.setLungsTimeStamp(organs.getLungsTimeStamp().toString());
    }
    if(editReceiverIntestine.isSelected() && organs.getIntestineTimeStamp().equals(old)) {
      patch.setIntestineTimeStamp(now);
    } else {
      patch.setIntestineTimeStamp(organs.getLungsTimeStamp().toString());
    }
    if(editReceiverCornea.isSelected() && organs.getCorneasTimeStamp().equals(old)) {
      patch.setCorneasTimeStamp(now);
    } else {
      patch.setCorneasTimeStamp(organs.getCorneasTimeStamp().toString());
    }
    if(editReceiverMiddleEar.isSelected() && organs.getMiddleEarsTimeStamp().equals(old)) {
      patch.setMiddleEarsTimeStamp(now);
    } else {
      patch.setMiddleEarsTimeStamp(organs.getMiddleEarsTimeStamp().toString());
    }
    if(editReceiverSkin.isSelected() && organs.getSkinTimeStamp().equals(old)) {
      patch.setSkinTimeStamp(now);
    } else {
      patch.setSkinTimeStamp(organs.getSkinTimeStamp().toString());
    }
    if(editReceiverBone.isSelected() && organs.getBoneTimeStamp().equals(old)) {
      patch.setBoneTimeStamp(now);
    } else {
      patch.setBoneMarrowTimeStamp(organs.getBoneMarrowTimeStamp().toString());
    }
    if(editReceiverBoneMarrow.isSelected() && organs.getBoneMarrowTimeStamp().equals(old)) {
      patch.setBoneMarrowTimeStamp(now);
    } else {
      patch.setBoneMarrowTimeStamp(organs.getBoneMarrowTimeStamp().toString());
    }
    if(editReceiverConnectiveTissue.isSelected() && organs.getConnectiveTissueTimeStamp().equals(old)) {
      patch.setConnectiveTissueTimeStamp(now);
    } else {
      patch.setConnectiveTissueTimeStamp(organs.getConnectiveTissueTimeStamp().toString());
    }
  }

  /**
   * Edit a donors profile information
   */
  private void editProfile() {
    this.CURRENT_VERSION++;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    ProfilePatch patch = new ProfilePatch(editPreferredName.getText(), editTitle.getSelectionModel().getSelectedItem().toString(),
            editDateOfBirth.getValue().format(formatter), editGender.getSelectionModel().getSelectedItem().toString().substring(0,1),
            editBirthGender.getSelectionModel().getSelectedItem().toString().substring(0,1), editLivedInUKFrance.isSelected(), Integer.toString(this.CURRENT_VERSION));
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_PROFILE.getAddress(), staticAccount.getUserName()), patch, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      editBasicInformation();
    });
    task.setOnFailed(event -> {
      if(checkConflict(task.getException())){
        getDonor(staticAccount.getUserName(), true);
        showConflictError();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }

  private void editBasicInformation() {
    this.CURRENT_VERSION++;
    String[] names = editGivenNames.getText().split(" ", 2);
    String middle;
    try{
      middle = names[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      middle = "";
    }
    staticAccount.setUserName(editNHINumber.getText());
    BasicInformationPatch patch = new BasicInformationPatch(names[0], middle, editLastName.getText(), editNHINumber.getText(), staticAccount.getPassword(), Integer.toString(this.CURRENT_VERSION));
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_BASIC_INFO.getAddress(), staticAccount.getUserName()), patch, session.getToken());
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      editContactDetails();
    });
    task.setOnFailed(event -> {
      if(checkConflict(task.getException())){
        getDonor(staticAccount.getUserName(), true);
        showConflictError();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }



  private void editContactDetails() {
    this.CURRENT_VERSION++;
    Address address = new Address(editStreetAddress.getText(), "", "", editCity.getText(), editRegion.getSelectionModel().getSelectedItem(), editPostcode.getText(), "NZ");
    ContactDetailsPatch patch = new ContactDetailsPatch(address, editMobileNumber.getText(), editHomeNumber.getText(), editEmail.getText(), Integer.toString(this.CURRENT_VERSION));
    Map<String, String> customheaders = new HashMap<String, String>();
    customheaders.put("phone", staticAccount.getContactDetails().getMobileNum());
    customheaders.put(EMERGENCY, "false");
    PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_CONTACT_DETAILS.getAddress(), staticAccount.getUserName()), patch, session.getToken(), customheaders);
    task.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
      addEmergencyContactDetails();
    });
    task.setOnFailed(event -> {
        Throwable throwable = task.getException();
        if(checkResponse(throwable)) {
            addEmergencyContactDetails();
        } else {
            showBadSaveError();
        }
    });
    new Thread(task).start();
  }



  /**
   * PATCH emergency contact details.
   */
    private void updateEmergencyContactDetails(){
      this.CURRENT_VERSION++;
      Address address = new Address(editEmergStreetAddress.getText(), "", "", editEmergCity.getText(), editEmergRegion.getSelectionModel().getSelectedItem(), editEmergPostcode.getText(), "NZ");
      ContactDetailsPatch patch = new ContactDetailsPatch(address, editEmergMobileNumber.getText(), editEmergHomeNumber.getText(), editEmergEmail.getText(), Integer.toString(this.CURRENT_VERSION));
      Map<String, String> customheaders = new HashMap<String, String>();
      customheaders.put("phone", staticAccount.getEmergencyContactDetails().getMobileNum());
      customheaders.put(EMERGENCY, "true");
      PatchTask task = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_CONTACT_DETAILS.getAddress(), staticAccount.getUserName()), patch, session.getToken(), customheaders);
      task.setOnSucceeded(event -> {
        AccountManager.getStatusUpdates().add(USER + staticAccount.getUserName() + MODIFIED);
        editReceiverOrgans();
      });
      task.setOnFailed(event -> {
        if(checkConflict(task.getException())){
          getDonor(staticAccount.getUserName(), true);
          showConflictError();
        } else {
          showBadSaveError();
        }
      });
      new Thread(task).start();
    }


  /**
   * POST the emergency contact details.
   */
    private void addEmergencyContactDetails(){
        if(!editEmergMobileNumber.getText().equals("")) {
            Address address = new Address(editEmergStreetAddress.getText(), "", "", editEmergCity.getText(), editEmergRegion.getSelectionModel().getSelectedItem(), editEmergPostcode.getText(), "NZ");
            ContactDetails contactDetails = new ContactDetails(address, editEmergMobileNumber.getText(), editEmergHomeNumber.getText(), editEmergEmail.getText());
            Map<String, String> customheaders = new HashMap<String, String>();
            customheaders.put(EMERGENCY, "true");
            PostTask task = new PostTask(String.class,ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_CONTACT_DETAILS.getAddress(), staticAccount.getUserName()), contactDetails, session.getToken(), customheaders);
            task.setOnSucceeded(event -> {
                editReceiverOrgans();
            });
            task.setOnFailed(event -> {
                Throwable throwable = task.getException();
                if (throwable instanceof HttpClientErrorException){
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) throwable;
                    if (httpClientErrorException.getStatusCode() == HttpStatus.CONFLICT){
                        // Emergency contact details already exist patch them instead.
                        updateEmergencyContactDetails();
                    } else if (httpClientErrorException.getStatusCode() == HttpStatus.BAD_REQUEST){
                        updateEmergencyContactDetails();
                    }
                }
            });
            new Thread(task).start();
        } else {
            editReceiverOrgans();
        }
    }

  /**
   * Returns a single donor from the server
   * @param username username of the donor to get
   */
  private void getDonor(String username, boolean refresh){
    String endpoint = ADDRESSES.GET_DONOR.getAddress() + username;
    GetTask task = new GetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      this.staticAccount = (DonorReceiver) task.getValue();
      if (!refresh){
        setup();
      } else {
        refreshView();
      }

    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get donor");

    });
    new Thread(task).start();
    getPhoto(username);

  }


  /**
   * Get a photo of the user logged in
   * @param username username of the donor
   */
  private void getPhoto(String username){
    String endpoint = String.format(ADDRESSES.DONOR_PHOTO.getAddress(), username);
    GetTask task = new GetTask(byte[].class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      byte[] test = (byte[]) task.getValue();
      Image img = new Image(new ByteArrayInputStream(test));
      profileImage.setImage(img);
      deleteButton.setDisable(false);

    });
    task.setOnFailed(event -> {
      Image img = new Image("/images/default.jpg");
      profileImage.setImage(img);
      deleteButton.setDisable(true);

    });
    new Thread(task).start();
  }


    /**
     * Checks the response and returns whether the request was successful
     * @param throwable Throwable error throwable client/server
     * @return Whether the response was successful
     */
  public boolean checkResponse(Throwable throwable) {
      if (throwable instanceof HttpClientErrorException){
          HttpClientErrorException httpClientErrorException = (HttpClientErrorException) throwable;
          if (httpClientErrorException.getStatusCode() == HttpStatus.ACCEPTED){
              return true;
          } else if (httpClientErrorException.getStatusCode() == HttpStatus.BAD_REQUEST){
              return false;
          }
      }
      return false;
  }

  public boolean checkConflict(Throwable throwable) {
    if (throwable instanceof HttpClientErrorException){
      HttpClientErrorException httpClientErrorException = (HttpClientErrorException) throwable;
      if (httpClientErrorException.getStatusCode() == HttpStatus.CONFLICT){
        return true;
      }
    }
    return false;
  }


  /**
   * Implementaton of the close window method which checks all fields for changes and, if there are
   * any changes, asks the user if they really want to close.
   */
  @Override
  public boolean closeWindow(Event event) {
    String[] firstNames = editGivenNames.getText().split(" ", 2);
    if (!donorReceiver.getFirstName().equals(firstNames[0]) ||
            !donorReceiver.getMiddleName().equals(firstNames[1]) ||
            !donorReceiver.getLastName().equals(editLastName.getText()) ||
            !donorReceiver.getPreferredName().equals(editPreferredName.getText()) ||
            !donorReceiver.getUserName().equals(editNHINumber.getText()) ||
            !donorReceiver.getDateOfBirth().equals(editDateOfBirth.getValue()) ||
            !donorReceiver.getGender().equals(editGender.getValue().toString().charAt(0)) ||
            !donorReceiver.getTitle().equalsIgnoreCase(editTitle.getValue().toString()) ||
            !doubleEquality(donorReceiver.getUserAttributeCollection().getHeight(), Double.parseDouble(editHeight.getText())) ||
            !doubleEquality(donorReceiver.getUserAttributeCollection().getWeight(), Double.parseDouble(editWeight.getText())) ||
            !donorReceiver.getUserAttributeCollection().getBloodType().equals(editBloodType.getValue()) ||
            !donorReceiver.getLivedInUKFlag() == editLivedInUKFrance.isSelected() ||
            !donorReceiver.getBirthGender().equals(editBirthGender.getValue().toString().charAt(0)) ||
            !donorReceiver.getContactDetails().getAddress().getStreetAddressLineOne().equals(editStreetAddress.getText()) ||
            !donorReceiver.getContactDetails().getAddress().getCityName().equals(editCity.getText()) ||
            !donorReceiver.getContactDetails().getAddress().getRegion().equals(editRegion.getValue()) ||
            !donorReceiver.getContactDetails().getAddress().getPostCode().equals(editPostcode.getText()) ||
            !donorReceiver.getContactDetails().getMobileNum().equals(editMobileNumber.getText()) ||
            !donorReceiver.getContactDetails().getHomeNum().equals(editHomeNumber.getText()) ||
            !donorReceiver.getContactDetails().getEmail().equals(editEmail.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLineOne().equals(editEmergStreetAddress.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getAddress().getCityName().equals(editEmergCity.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getAddress().getRegion().equals(editEmergRegion.getValue()) ||
            !donorReceiver.getEmergencyContactDetails().getAddress().getPostCode().equals(editEmergPostcode.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getMobileNum().equals(editEmergMobileNumber.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getHomeNum().equals(editEmergHomeNumber.getText()) ||
            !donorReceiver.getEmergencyContactDetails().getEmail().equals(editEmergEmail.getText())
            ) {

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
    boolean isChild = cwm.childWindowToFront(donorReceiver);

    if (!isChild) {

      // Only perform platform and system exit if not in a child window.
      Platform.exit();
      System.exit(0);

    }

    return true;

  }


  /**
   * Compares to double values and evaluations true if the difference between
   * the two values is less than 0.00001.
   * @param a The first double to be compared.
   * @param b The second double to be compared.
   * @return True if the difference between a and b is les than 0.00001.
   */
  private boolean doubleEquality(double a, double b) {

    double difference = a - b;
    double maxDelta = 0.00001;
    return (difference < maxDelta) && (difference > 0.0 - maxDelta);

  }


}
