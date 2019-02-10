package seng302.controllers;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.springframework.http.ResponseEntity;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.ReceiverRecord;
import seng302.model.Session;
import seng302.model.TransplantWaitingList;
import seng302.model.enums.ADDRESSES;
import seng302.model.locationData.CountryList;
import seng302.model.locationData.RegionList;
import seng302.model.person.DeathDetails;
import seng302.model.person.DonorReceiver;
import seng302.services.GetTask;
import static seng302.controllers.BaseController.createBadPopUp;

public class TransplantWaitingListPaneController extends ListController {

  public static Session session = App.getCurrentSession();

  private CountryList countryList = CountryList.getInstance();
  private RegionList regionList = RegionList.getInstance();

  private DonorReceiver selectedDonorReceiver;

  private Boolean isDescending = null;

  private String sortBy = "";

  private int total = 0;

  private String lastSuccessfulQuery = "";

  @FXML
  private HeaderlessTable transplantWaitingList;

  @FXML
  private TableColumn<ReceiverRecord, String> nameColumn;

  @FXML
  private TableColumn<ReceiverRecord, String> organColumn;

  @FXML
  private TableColumn<ReceiverRecord, String> regionColumn;

  @FXML
  private TableColumn<ReceiverRecord, String> dateColumn;

  @FXML
  private Button donorButton;

  @FXML
  private Button viewButton;

  @FXML
  private Button editButton;

  @FXML
  private Button removeOrgan;

  @FXML
  private Button nameButton;

  @FXML
  private Button regionButton;

  @FXML
  private Button organButton;

  @FXML
  private Button dateButton;

  @FXML
  private ComboBox<String> organFilterComboBox;

  @FXML
  private ComboBox<String> regionFilterComboBox;

  @FXML
  private Label matchingAccounts;

  @FXML
  private TextField searchForField;

  @FXML
  private Pagination pageControl;

  private ObservableList<String> regions = FXCollections.observableArrayList();

  private ObservableList<String> organs = FXCollections.observableArrayList();

  private  ObservableList<ReceiverRecord> records = FXCollections.observableArrayList();

  private List<ReceiverRecord> receiverRecordsServer = new ArrayList<>();

  private List<ReceiverRecord> receiverRecordsBlank = new ArrayList<>();

  /**
   * An account selected by the user on the table.
   */
  private ReceiverRecord selectedAccount;

  private DonorReceiver account;

  /**
   * The AccountManager object which handles all account operations.
   */
  private AccountManager manager = App.getDatabase();

  /**
   * Constant which determines the number of accounts on each table page.
   */
  private static final double ACCOUNTS_PER_PAGE = 15.0;

  /**
   * The list of all accounts.
   */
  private ObservableList<ReceiverRecord> observableAccounts;

  /**
   * The list of all accounts to be displayed in TableView that match the search criteria.
   */
  private FilteredList<ReceiverRecord> filteredRecords;


  /**
   * A list of sorted accounts derived from filteredRecords.
   */
  private SortedList<ReceiverRecord> sortedRecords;

  /**
   * Enumeration for referencing column sort order.
   */
  private enum Order {
    DEFAULT, NAME_ASC, NAME_DESC, ORGAN_ASC, ORGAN_DESC,
    REGION_ASC, REGION_DESC, DATE_ASC, DATE_DESC
  }

  /**
   * The currently selected sort order.
   */
  private Order sortOrder = Order.DEFAULT;

  /**
   * A constant for the ascending button text.
   */
  private static final String ascText = " (Ascending)";

  /**
   * A constant for the descending button text.
   */
  private static final String descText = " (Descending)";

  /**
   * Retrieves the selected account.
   *
   * @return The selected DonorReceiver object.
   */
  public ReceiverRecord getSelectedAccount() {
    return selectedAccount;
  }


  private AccountManager accountManagerTest;

  private String regionString = "Any Region";
  private String organString = "Any Organ";

  private String errorOption = "1. Error";
  private String curedOption = "2. Cured";
  private String deathOption = "3. Death";

  private String receiverAsString = "receiver";
  private String organsAsString = "organs";
  private String updateAsString = "update";

  private static GetTask getTaskTest;



  /**
   * Updates the text of all order buttons on the list view pane.
   */
  private void updateOrderButtons() {

    // Reset button names.
    nameButton.setText("Name");
    organButton.setText("Organ");
    regionButton.setText("Region");
    dateButton.setText("Date Added");

    if (sortOrder == Order.NAME_ASC) {

      nameButton.setText(nameButton.getText() + ascText);
      sortBy="name";
      isDescending=null;

    } else if (sortOrder == Order.NAME_DESC) {

      nameButton.setText(nameButton.getText() + descText);
      sortBy="name";
      isDescending=true;

    } else if (sortOrder == Order.ORGAN_ASC) {

      organButton.setText(organButton.getText() + ascText);
      sortBy="organ";
      isDescending=null;

    } else if (sortOrder == Order.ORGAN_DESC) {

      organButton.setText(organButton.getText() + descText);
      sortBy="organ";
      isDescending=true;

    } else if (sortOrder == Order.DATE_ASC) {

      dateButton.setText(dateButton.getText() + ascText);
      sortBy="registrationTime";
      isDescending=null;

    } else if (sortOrder == Order.DATE_DESC) {

      dateButton.setText(dateButton.getText() + descText);
      sortBy="registrationTime";
      isDescending=true;

    } else if (sortOrder == Order.REGION_ASC) {

      regionButton.setText(regionButton.getText() + ascText);
      sortBy="region";
      isDescending=null;

    } else if (sortOrder == Order.REGION_DESC) {

      regionButton.setText(regionButton.getText() + descText);
      sortBy="region";
      isDescending=true;

    } else if (sortOrder == Order.DEFAULT) {

      // Revert to default order based on name.
      sortBy="";
      isDescending=null;


    }

    updateFiltering();

  }


  /**
   * Used to filter the list, called by filter and search event listeners
   */
  private void updateFiltering() {

    String address = "";

    if (regionFilterComboBox.getSelectionModel().getSelectedIndex() != 0){
      address += "&region=";
      address += regionFilterComboBox.getSelectionModel().getSelectedItem().toString();
    }
    if (organFilterComboBox.getSelectionModel().getSelectedIndex() != 0){
      address += "&receivingOrgan=";
      address += organFilterComboBox.getSelectionModel().getSelectedItem().toString().trim().replaceAll(" ", "");
    }
    if (!sortBy.equalsIgnoreCase("")){
      address += "&sortVariable=";
      address += sortBy;
      if (isDescending != null){
        if (isDescending){
          address += "&isDesc=";
          address += "true";
        }
      }


    }

    lastSuccessfulQuery = address;
    receiverRecordsServer.clear();
    observableAccounts.removeAll(observableAccounts);
    observableAccounts.addAll(receiverRecordsBlank);

    setAccountsListRowFactory();
    getReceiverRecordsFromServer(session.getToken(), address, false, false);

  }


  /**
   * eventHandler for the nameOrder button.
   *
   * @param event An action which occurred on the nameOrder button.
   */
  @FXML
  private void updateNameOrder(ActionEvent event) {

    if (sortOrder == Order.NAME_ASC) {

      sortOrder = Order.NAME_DESC;

    } else if (sortOrder == Order.NAME_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.NAME_ASC;

    }

    updateOrderButtons();

  }


  /**
   * Event handler for the ageOrder button.
   *
   * @param event An action which occurred on the ageOrder button.
   */
  @FXML
  private void updateOrganOrder(ActionEvent event) {

    if (sortOrder == Order.ORGAN_ASC) {

      sortOrder = Order.ORGAN_DESC;

    } else if (sortOrder == Order.ORGAN_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.ORGAN_ASC;

    }

    updateOrderButtons();

  }


  /**
   * Event handler for the genderOrder button.
   *
   * @param event An action which occurred on the genderOrder button.
   */
  @FXML
  private void updateDateOrder(ActionEvent event) {

    if (sortOrder == Order.DATE_ASC) {

      sortOrder = Order.DATE_DESC;

    } else if (sortOrder == Order.DATE_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.DATE_ASC;

    }

    updateOrderButtons();

  }


  /**
   * Event handler for the regionOrder button.
   *
   * @param event An action which occurred on the regionOrder button.
   */
  @FXML
  private void updateRegionOrder(ActionEvent event) {

    if (sortOrder == Order.REGION_ASC) {

      sortOrder = Order.REGION_DESC;

    } else if (sortOrder == Order.REGION_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.REGION_ASC;

    }

    updateOrderButtons();

  }

  //===================================================
  //Navigating buttons

  @FXML
  private void donorSelected() {
    if(App.getCurrentSession().getUserType().equals("clinician")) {
      PageNav.loadNewPage(PageNav.MAINMENU);
    } else {
      PageNav.loadNewPage(PageNav.ADMINMENU);
    }
  }


  /**
   * Resets the combobox filters for organ and region
   */
  @FXML
  private void resetFilters() {
    regionFilterComboBox.getSelectionModel().select(regionString);
    organFilterComboBox.getSelectionModel().select(organString);
    updateFiltering();
  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    matchingAccounts.setTextAlignment(TextAlignment.CENTER);
    matchingAccounts.setText(getMatchesMessage("account"));

    // Retrieve start and end index of current page.
    int startIndex = pageControl.getCurrentPageIndex() * accountsPerPage;

    String address = lastSuccessfulQuery + "&index=" + startIndex;
    receiverRecordsServer.clear();
    getReceiverRecordsFromServer(session.getToken(), address, false, true);
  }

  /**
   * Updates the page numbers and account numbers displayed at the bottom
   * of the page.
   */
  private void updateMessage(){
    // Update label showing number of matches.
    matchingAccounts.setTextAlignment(TextAlignment.CENTER);
    matchingAccounts.setText(getMatchesMessage("account"));
  }

  private void refresh(boolean pageChange){



    observableAccounts.removeAll(observableAccounts);

    generateAccounts();

    filteredRecords = new FilteredList<ReceiverRecord>(observableAccounts);

    sortedAccounts = new SortedList<>(filteredRecords);

    super.sortedAccounts = sortedAccounts;
    // Create new sublist.
    SortedList<DonorReceiver> pageDonorReceivers = new SortedList<>(
            FXCollections.observableArrayList(sortedAccounts));
    pageDonorReceivers.comparatorProperty().bind(transplantWaitingList.comparatorProperty());

    transplantWaitingList.setItems(pageDonorReceivers);
    //setAccountsListRowFactory();
    updateMessage();
    if(!pageChange) {
      updatePageCount();
    }
    transplantWaitingList.refresh();
  }


  private void generateAccounts() {

    // Clear and repopulate observableAccounts.
    observableAccounts.removeAll(observableAccounts);

    observableAccounts.addAll(receiverRecordsServer);

  }

  @Override
  void updatePageCount() {
    pageControl.setCurrentPageIndex(0);
    pageControl.setPageCount(Math.max(1, (int) Math.ceil((float) total /
            accountsPerPage)));
  }


  /**
   * A method to generate the matches message underneath the pagination control based on the type of
   * account passed in
   *
   * @param accountName The name of the account so we can specify between "record and records",
   * "account and accounts", etc.
   * @return The string to display as the message above the pagination control
   */
  @Override
  String getMatchesMessage(String accountName) {
    int number = total;

    if (number == 1) {
      return "\n" +
              number + " matching " + accountName;
    } else {
      int startNumber = pageControl.getCurrentPageIndex() * accountsPerPage + 1;
      int endNumber = (pageControl.getCurrentPageIndex() + 1) * accountsPerPage;

      endNumber = (endNumber < number) ? endNumber : number; // Set the endNumber to the lower value

      startNumber = (endNumber == 0) ? 0
              : startNumber; // Set the start number to 0 if there are no results to display
      return accountName.substring(0, 1).toUpperCase() + accountName.substring(1) + "s "
              + startNumber + "-" + endNumber + "\n" +
              number + " matching " + accountName + "s";
    }
  }



  /**
   * Creates a dialog box whenever the remove button is selected (Requires an item in the transplant
   * waiting list to be selected). This dialog contains a combo box to choose the reason for
   * removal, and a date picker to choose the date of death if option 3 is selected.
   */
  @FXML
  public void removeFromTableChoiceDialog() {
    if (selectedAccount == null) {
      // Warn user if no account was selected.
      noAccountSelectedAlert("Removal from waiting list");
    } else {
      Dialog<Pair<String, LocalDateTime>> dialog = new Dialog<>();
      dialog.setTitle("Removal from Transplant List");
      dialog.setHeaderText("Removal from Transplant List");
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      dialog.getDialogPane().setPrefSize(890, 300);

      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(20, 10, 10, 10));

      //Creating a setting labels buttons for dialog
      ComboBox<String> choose = new ComboBox<>();

      choose.getItems().addAll(
          errorOption,
          curedOption,
          deathOption
      );
      choose.getSelectionModel().selectFirst();

      DatePicker date = new DatePicker();  //For the date of death option
      date.setValue(LocalDate.now());
      date.setVisible(false);

      Label dateLabel = new Label();  //Date of death label
      dateLabel.setText("Date of Death: ");
      dateLabel.setVisible(false);

      Label errorLabel = new Label(); //Error label for date of death
      errorLabel.setText("Date of death cannot be blank");
      errorLabel.setTextFill(Color.RED);
      errorLabel.setVisible(false);

      CheckBox yes = new CheckBox();
      yes.setText("Yes");
      yes.setVisible(false);

      CheckBox no = new CheckBox();
      no.setText("No");
      no.setVisible(false);

      Label curedQuestion = new Label();
      curedQuestion.setText("Would you like to check the illnesses of the receiver?");
      curedQuestion.setVisible(false);

      Label curedQuestionError = new Label();
      curedQuestionError.setText("Please choose yes or no");
      curedQuestionError.setTextFill(Color.RED);
      curedQuestionError.setVisible(false);

      TextField timeOfDeath = new TextField();
      timeOfDeath.setText("12:00");
      timeOfDeath.setVisible(false);
      Label errorTimeOfDeath = new Label(); //Error label for date of death
      errorTimeOfDeath.setText("Please enter time in the form hh:mm");
      errorTimeOfDeath.setTextFill(Color.RED);
      errorTimeOfDeath.setVisible(false);

      Label countryLabel = new Label();  //Country Date of death label
      countryLabel.setText("Country of Death: ");
      countryLabel.setVisible(false);

      Label cityLabel = new Label();  // city Date of death label
      cityLabel.setText("City of Death: ");
      cityLabel.setVisible(false);

      Label enterCityErrorLabel = new Label(); //Error label for date of death
      enterCityErrorLabel.setText("Please enter a city/town");
      enterCityErrorLabel.setTextFill(Color.RED);
      enterCityErrorLabel.setVisible(false);

      Label regionLabel = new Label();  //region Date of death label
      regionLabel.setText("Region of Death: ");
      regionLabel.setVisible(false);

      ComboBox<String> chooseCountry = new ComboBox<>();
      chooseCountry.getItems().addAll(countryList.getAllowableCountries());
      chooseCountry.getSelectionModel().select("NEW ZEALAND");
      chooseCountry.setVisible(false);

      ComboBox<String> chooseRegion = new ComboBox<>();
      chooseRegion.getItems().addAll(regionList.getRegions("NZ"));
      chooseRegion.getSelectionModel().select(9);
      chooseRegion.setVisible(false);

      TextField enterCity = new TextField();
      enterCity.setVisible(false);

      TextField typeRegion = new TextField();
      typeRegion.setVisible(false);

      Label typeRegionError = new Label();
      typeRegionError.setTextFill(Color.RED);
      typeRegionError.setText("Please enter a region");
      typeRegionError.setVisible(false);

      //Adding buttons/Labels to the grid
      grid.add(new Label("Reason for removal:"), 0, 0);
      grid.add(choose, 1, 0);
      grid.add(dateLabel, 0, 1);
      grid.add(date, 1, 1);
      grid.add(errorLabel, 3, 1);

      grid.add(curedQuestion, 0, 1);
      grid.add(yes, 1, 1);
      grid.add(no, 2, 1);
      grid.add(curedQuestionError, 3, 1);

      grid.add(countryLabel, 0, 2);
      grid.add(chooseCountry, 1, 2);
      grid.add(regionLabel, 0, 3);
      grid.add(chooseRegion, 1, 3);
      grid.add(typeRegion, 1, 3);
      grid.add(typeRegionError, 2, 3);
      grid.add(cityLabel, 0, 4);
      grid.add(enterCity, 1, 4);
      grid.add(enterCityErrorLabel, 2, 4);
      grid.add(timeOfDeath, 2, 1);
      grid.add(errorTimeOfDeath, 3, 1);


      createDeathListener(choose, errorLabel, curedQuestionError, date, dateLabel, curedQuestion,
              yes, no, chooseRegion, errorTimeOfDeath, countryLabel,
              enterCityErrorLabel, regionLabel, cityLabel, chooseCountry, timeOfDeath, enterCity, typeRegion);

      chooseCountry.getSelectionModel().selectedItemProperty().addListener(
              (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase("NEW ZEALAND")) {
                  typeRegion.setVisible(false);
                  chooseRegion.setVisible(true);
                } else {
                  chooseRegion.setVisible(false);
                  typeRegion.setVisible(true);
                }
              });

      dialog.getDialogPane().setContent(grid);
      String NEWZEALAND = "NEW ZEALAND";
      chooseCountry.getSelectionModel().selectedItemProperty().addListener(
              (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEWZEALAND)) {
                  typeRegion.setVisible(false);
                  chooseRegion.setVisible(true);
                } else {
                  chooseRegion.setVisible(false);
                  typeRegion.setVisible(true);
                }
              });


      //Catches a null date of death value if the reason is death, otherwise closes
      final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
      okButton.addEventFilter(ActionEvent.ACTION, event -> {
        // reset all error labels
        enterCityErrorLabel.setVisible(false);
        errorTimeOfDeath.setVisible(false);
        typeRegionError.setVisible(false);
        errorLabel.setVisible(false);
        curedQuestionError.setVisible(false);
        // if error chosen, then close dialog
        if (choose.getSelectionModel().getSelectedItem().equals(errorOption)) {
          closeDeathPopup(choose, date, yes, null);
        } else if (choose.getSelectionModel().getSelectedItem().equals(curedOption)) {
          // if cured is chosen, then ask if they want to view the illnesses
          // first check if they have answered
          if ((!yes.isSelected()) && (!no.isSelected()) && (choose.getSelectionModel()
              .getSelectedItem().equals(curedOption))) {
            curedQuestionError.setVisible(true);
            event.consume();
          } else {
            closeDeathPopup(choose, date, yes, null);
          }
        } else if (choose.getSelectionModel().getSelectedItem().equals(deathOption)) {
          // if death is chosen then validate values
          // show error if date is null
          boolean isValid = true;
          if (date.getValue() == null) {
            errorLabel.setText("Date of death cannot be blank");
            isValid = false;
            errorLabel.setVisible(true);
            event.consume();
          }
          if ((!chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEWZEALAND)) && (typeRegion.getText().equalsIgnoreCase(""))) {
            // validate region if not in new zealand
            typeRegionError.setVisible(true);
            isValid = false;
            event.consume();
            event.consume();
          }
          if (enterCity.getText().equalsIgnoreCase("")) {
            // validate city
            enterCityErrorLabel.setVisible(true);
            isValid = false;
            event.consume();
          }
          if (!correctInputForm(timeOfDeath.getText())) {
            // validate time of death
            errorTimeOfDeath.setVisible(true);
            isValid = false;
            event.consume();
          }
          if (isValid) {
            // validate that date/time of death is not in the future, and not before dob
            LocalTime localTime = LocalTime.parse(timeOfDeath.getText());
            LocalDateTime localDateTime = LocalDateTime.of(date.getValue(), localTime);
            LocalDate localDate = localDateTime.toLocalDate();
            if (localDateTime.isAfter(LocalDateTime.now())) {
              errorLabel.setText("Date of death cannot be in the future");
              errorLabel.setVisible(true);
              event.consume();
            } else if (localDateTime.isBefore(LocalDateTime.of(selectedDonorReceiver.getDateOfBirth(), LocalTime.of(0, 0, 0)))) {
              errorLabel.setText("Date of death cannot be before date of birth");
              errorLabel.setVisible(true);
              event.consume();
            } else {
              // date/time is valid
              // if they died in new zealand, then region is set by the drop-down
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
              if (chooseCountry.getSelectionModel().getSelectedItem().equalsIgnoreCase(NEWZEALAND)) {
                  DeathDetails deathDetails = new DeathDetails(chooseCountry.getSelectionModel().getSelectedItem(),
                          chooseRegion.getSelectionModel().getSelectedItem(),enterCity.getText(),localDateTime);
                  TransplantWaitingList.updateDateOfDeath(selectedDonorReceiver,deathDetails);
              } else {
                // if they didn't die in new zealand, then region is set by text field
                  DeathDetails deathDetails = new DeathDetails(chooseCountry.getSelectionModel().getSelectedItem(),typeRegion.getText(),
                          enterCity.getText(),localDateTime);
                  TransplantWaitingList.updateDateOfDeath(selectedDonorReceiver,deathDetails);
              }
              // finally, close the death pop-up and remove organs
              closeDeathPopup(choose, date, yes, localTime);
            }
          }
        }
      });
      dialog.showAndWait();
    }
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



  /**
   * Reacts to the reason given for the organs removal from the transplant waiting list.
   *
   * @param choose The checkbox with the options in it.
   * @param date The date picker where the date of death is chosen.
   * @param yes The checkbox for whether the organ has been cured.
   */
  private void closeDeathPopup(ComboBox<String> choose, DatePicker date, CheckBox yes, LocalTime localTime) {
    if (choose.getSelectionModel().getSelectedItem().equals(deathOption)) {
      removeOrgansDeceased();
    } else if (choose.getSelectionModel().getSelectedItem().equals(curedOption)) {
      if (yes.isSelected()) {
        try {
          removeOrganCured("Yes");
        } catch (IOException exception) {
          openSelectedProfileError();
        }
      } else {
        try {
          removeOrganCured("No");
        } catch (IOException exception) {
          openSelectedProfileError();
        }
      }
    } else {
      removeOrganMistake();
    }
  }


  /**
   * Adds a listener to check if the combo box is on option 3, death
   *
   * @param choose The comboBox the listener is to be attached to
   * @param errorLabel Where the error messages are shown
   * @param curedQuestionError Another place where the error messages are shown
   * @param date The date picker for the date of death
   * @param dateLabel The label for the date picker for the date of death
   * @param curedQuestion The label questioning whether the account holder has been cured.
   * @param yes The yes checkbox for the date of death
   * @param no The no checkbox for the date of death
   */
  private void createDeathListener(ComboBox<String> choose, Label errorLabel,
                                   Label curedQuestionError,
                                   DatePicker date, Label dateLabel, Label curedQuestion, CheckBox yes, CheckBox no, ComboBox chooseRegion, Label errorTimeOfDeath, Label countryLabel,
                                   Label enterCityErrorLabel, Label regionLabel, Label cityLabel, ComboBox chooseCountry, TextField timeOfDeath, TextField cityOfDeath,
                                   TextField typeRegion) {
    choose.getSelectionModel().selectedItemProperty().addListener(
        (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
          if (newValue.equals(deathOption)) {
            chooseCountry.getSelectionModel().select(0);
            date.setVisible(true);
            dateLabel.setVisible(true);
            curedQuestion.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);
            chooseRegion.setVisible(true);
            countryLabel.setVisible(true);
            regionLabel.setVisible(true);
            cityLabel.setVisible(true);
            chooseCountry.setVisible(true);
            timeOfDeath.setVisible(true);
            cityOfDeath.setVisible(true);
          } else if (newValue.equals(curedOption)) {
            curedQuestion.setVisible(true);
            yes.setVisible(true);
            no.setVisible(true);
            date.setVisible(false);
            dateLabel.setVisible(false);
            chooseRegion.setVisible(false);
            errorTimeOfDeath.setVisible(false);
            countryLabel.setVisible(false);
            enterCityErrorLabel.setVisible(false);
            regionLabel.setVisible(false);
            cityLabel.setVisible(false);
            chooseCountry.setVisible(false);
            timeOfDeath.setVisible(false);
            cityOfDeath.setVisible(false);
            typeRegion.setVisible(false);
          } else {
            date.setVisible(false);
            dateLabel.setVisible(false);
            errorLabel.setVisible(false);
            curedQuestion.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);
            curedQuestionError.setVisible(false);
            chooseRegion.setVisible(false);
            errorTimeOfDeath.setVisible(false);
            countryLabel.setVisible(false);
            enterCityErrorLabel.setVisible(false);
            regionLabel.setVisible(false);
            cityLabel.setVisible(false);
            chooseCountry.setVisible(false);
            timeOfDeath.setVisible(false);
            cityOfDeath.setVisible(false);
            typeRegion.setVisible(false);
          }
        });
  }


  /**
   * Opens an alert if there was an error in opening the selected profile.
   */
  private void openSelectedProfileError() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Error Opening Profile");
    alert.setHeaderText(null);
    alert.setContentText("An error occurred while opening the selected profile.");
    alert.showAndWait();
  }


  /**
   * Removes all organs from the receiver, sets their date of death and logs all changes
   */
  private void removeOrgansDeceased() {
    TransplantWaitingList.removeOrganDeceased(selectedDonorReceiver);
    updateFiltering();

  }


  /**
   * Removes the record from the waiting list, creates a log and sets the organ to false
   *
   * @param option Whether or not the clinician wants to view the accounts current diseases
   * @throws IOException An error
   */
  private void removeOrganCured(String option) throws IOException {
    records.remove(selectedAccount);
    TransplantWaitingList.removeOrganMistake(selectedAccount, selectedDonorReceiver, true);
    if (option.equals("Yes")) {
      loadProfileWindowEdit(selectedDonorReceiver);
    }
    updateFiltering();
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }


  /**
   * Removes the record, changes the relevant organ to false in the account, and creates a relevant
   * log
   */
  private void removeOrganMistake() {
    records.remove(selectedAccount);
    TransplantWaitingList.removeOrganMistake(selectedAccount, selectedDonorReceiver, false);
    updateFiltering();
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }


  /**
   * Refresh the table when any account receives a change
   */
  static void triggerRefresh() {
    TableView transplantWaitingList = (TableView) App.getWindow().getScene()
        .lookup("#transplantWaitingList");
    if (transplantWaitingList == null) {
      // Handle case were accountsList is not initialised.
      throw new NullPointerException(
          "TableView object 'transplantWaitingList' is not initialised in the main window.");
    } else {
      // Trigger instantiated update.
      transplantWaitingList.refresh();
    }
  }

  //=================================================
  //Initialize functions

  /**
   * Sets the row factory for accountsList. This creates a tooltip for each populated row showing an
   * overview of the account. This code is based on a solution (https://stackoverflow.com/a/26221242)
   * posted by James_D in response to a question on Stack Overflow.
   *
   */
  private void setAccountsListRowFactory() {
    transplantWaitingList.setRowFactory(tableView -> new TableRow<ReceiverRecord>() {
      @Override
      public void updateItem(ReceiverRecord receiverRecord, boolean empty) {
        super.updateItem(receiverRecord, empty);

        if (receiverRecord == null || empty) {
          Tooltip overview = new Tooltip();
          setTooltip(overview);
          getStyleClass().remove("conflictingOrgans");
        } else {
          GetTask manyTask = new GetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR.getAddress() + receiverRecord.getNhi(), session.getToken());
          manyTask.setOnSucceeded(event -> {
            account = (DonorReceiver) manyTask.getValue();

            // Generate an account-specific tool tip.
            Tooltip overview = new Tooltip();
            overview.setText(account.generateOverview());
            setTooltip(overview);
            // Highlight the entry if they donor is also donating this organ that they are receiving
            String organ = receiverRecord.getOrgan();
            organ = TransplantWaitingList.organConversion(organ);
            Boolean isConflicting = account.getDonorOrganInventory().isDonating(organ);
            if (isConflicting) {
              setStyle("-fx-background-color:#ea9491");
              getStyleClass().add("conflictingOrgans");
            } else {
              setStyle(""); // Reset the style to prevent random rows from being highlighted
            }
          });
          manyTask.setOnFailed(event -> {

            setStyle(""); // Reset the style to prevent random rows from being highlighted
            throw new IllegalArgumentException("Failed to get donor ");
          });
          new Thread(manyTask).start();
        }
      }
    });

  }

  /**
   * Opens a view profile pane in a new window.
   *
   * @param donorReceiver The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  public void loadProfileWindow(DonorReceiver donorReceiver) throws IOException {

    // Only load new window if childWindowToFront fails.
    if (!App.childWindowToFront(donorReceiver)) {

      // Set the selected donorReceiver for the profile pane and confirm child.
//      ViewProfilePaneController.setAccount(donorReceiver);
      ViewProfilePaneController.setStaticUsername(donorReceiver.getUserName());

      ViewProfilePaneController.setIsChild(true);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader(getClass().getResource(PageNav.VIEW));
      StackPane profilePane = loader.load();

      // Create new scene.
      Scene profileScene = new Scene(profilePane);

      // Create new stage.
      Stage profileStage = new Stage();
      profileStage.setTitle("Profile for " + donorReceiver.getUserName());
      profileStage.setScene(profileScene);
      profileStage.show();

      // Place stage in center of main window.
      profileStage.setX(
              App.getWindow().getX() + ((App.getWindow().getWidth() - profileStage.getWidth()) / 2.0));
      profileStage.setY(
              App.getWindow().getY() + ((App.getWindow().getHeight() - profileStage.getHeight())
                      / 2.0));

      App.addChildWindow(profileStage, donorReceiver);
    }
  }


  /**
   * Opens a view profile pane in a new window, then switches to the edit pane so the clinician can
   * view the diseases
   *
   * @param account The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  private void loadProfileWindowEdit(DonorReceiver account) throws IOException {

    // Only load new window if childWindowToFront fails.
    if (!App.childWindowToFront(account)) {

      // Set the selected donorReceiver for the profile pane and confirm child.
      ViewProfilePaneController.setAccount(account);
      ViewProfilePaneController.setIsChild(true);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader();
      StackPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

      // Create new scene.
      Scene profileScene = new Scene(profilePane);

      // Create new stage.
      Stage profileStage = new Stage();
      profileStage.setTitle("Profile for " + account.getUserName());
      profileStage.setScene(profileScene);
      profileStage.show();
      profileStage.setX(App.getWindow().getX() + App.getWindow().getWidth());

      App.addChildWindow(profileStage, account);

      loadEditWindow(account, profilePane, profileStage);
    }
  }


  /**
   * Switches to the edit profile screen within the given window. Called from method
   * LoadProfileWindowEdit
   *
   * @throws IOException When the FXML cannot be retrieved.
   */
  private void loadEditWindow(DonorReceiver account, StackPane pane, Stage stage)
      throws IOException {

    // Set the selected account for the profile pane and confirm child.
    EditPaneController.setAccount(account);
    EditPaneController.setIsChild(true);

    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    StackPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));
    // Create new scene.
    Scene editScene = new Scene(editPane);
    TabPane tabPane = (TabPane) editPane.lookup("#mainTabPane");
    TabPane profileViewTabbedPane = (TabPane) pane.lookup("#profileViewTabbedPane");
    int tabIndex = profileViewTabbedPane.getSelectionModel().getSelectedIndex();
    tabIndex += 5;
    tabPane.getSelectionModel().clearAndSelect(tabIndex);
    // Retrieve current stage and set scene.
    Stage current = (Stage) stage.getScene().getWindow();
    current.setScene(editScene);

  }


  /**
   * Populates the organ combobox with a static list of possible organs
   */
  private void initializeOrganFilterComboBox() {
    organFilterComboBox.setItems(organs);
    organFilterComboBox.getItems()
        .addAll(organString, "Liver", "Kidneys", "Heart", "Lungs", "Intestine",
            "Corneas", "Middle Ears", "Skin", "Bone", "Bone Marrow", "Connective Tissue");
    organFilterComboBox.getSelectionModel().select(0);
  }


  /**
   * Populates the region combo box dynamically with the regions of those on the transplant waiting
   * list.
   */
  private void populateRegionFilterComboBox() {
    regions.add("Any Region");
    regions.addAll(RegionList.getInstance().getRegions("NZ"));
    regionFilterComboBox.getItems().addAll(regions);
    regionFilterComboBox.getSelectionModel().select(0);
  }


  /**
   * Initialize the list view whenever the pane is created.
   */
  @FXML
  public void initialize() {
    transplantWaitingList.setPlaceholder(new Label("No organ requests found"));
    removeOrgan.getStyleClass().add("deleteButton");
    donorButton.getStyleClass().add("backButton");
    selectedAccount = null;
    records.clear();
    super.pageControl = pageControl;
    for (int i = 0; i < 16; i++){
      ReceiverRecord receiverRecord = new ReceiverRecord("","","","");
      receiverRecordsBlank.add(receiverRecord);
    }
    getReceiverRecordsFromServer(App.getCurrentSession().getToken(), "", true, false);

  }

  /**
   * Helper method for 'initialize()' which sets the cell factory of each column in the TableView
   * object.
   */
  private void initializeColumns() {

    // nameColumn
    nameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getFullName()
    ));

    // ageColumn
    organColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getOrgan()));

    // genderColumn
    regionColumn.setCellValueFactory(record -> new
        ReadOnlyStringWrapper(record.getValue().getRegion()));

    // dateColumn. Depends on the given string
    dateColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper((record.getValue().getTimestamp())));

  }


  public void secondInit() {

    transplantWaitingList.setPlaceholder(new Label("No accounts found"));

    selectedAccount = null; // Reset selected account.
    setAccountsListRowFactory();
    initializeColumns(); // Set comparators.

    // Populate a master list with all Accounts, regardless of page.
    observableAccounts = FXCollections.observableArrayList();

    generateAccounts();

    filteredRecords = new FilteredList<ReceiverRecord>(observableAccounts);

    // Populate a master list with all Accounts, regardless of page.
    observableAccounts = FXCollections.observableArrayList();
    // Initialize filter combo boxes
    initializeOrganFilterComboBox();
    populateRegionFilterComboBox();

    organFilterComboBox.valueProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue ov, String t, String t1) {
        if (t1 != null) {
          updateFiltering();
        }
      }
    });

    regionFilterComboBox.valueProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue ov, String t, String t1) {
        if (t1 != null) {
          updateFiltering();
        }
      }
    });

    transplantWaitingList.setOnMouseClicked(click -> {
      ReceiverRecord record = (ReceiverRecord) transplantWaitingList.getSelectionModel()
              .getSelectedItem();
      selectedAccount = record;
      if (record != null) {

        generateDonor(click);
      }
    });

    sortedAccounts = new SortedList<>(filteredRecords);
    super.sortedAccounts = sortedAccounts;

    updatePageCount();

    SortedList<ReceiverRecord> pageDonorReceivers = new SortedList<>(
            FXCollections.observableArrayList(sortedAccounts));
    pageDonorReceivers.comparatorProperty().bind(transplantWaitingList.comparatorProperty());

    transplantWaitingList.setItems(pageDonorReceivers);
    pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
      updateTableView();
    });
    setAccountsListRowFactory();
    initializeColumns();

    updateMessage();
  }


  private void generateDonor(MouseEvent click) {
    GetTask manyTask = new GetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR.getAddress()+selectedAccount.getNhi(), session.getToken());
    manyTask.setOnSucceeded(event -> {
      DonorReceiver donorReceiver = (DonorReceiver) manyTask.getValue();
      selectedDonorReceiver = donorReceiver;
      if (click.getButton().equals(MouseButton.PRIMARY) &&
              click.getClickCount() == 2 && selectedDonorReceiver != null) {
        try {
          loadProfileWindow(selectedDonorReceiver);
        } catch (IOException exception) {
          openSelectedProfileError();
        }
      }
    });
    manyTask.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get donor ");
    });
    new Thread(manyTask).start();
  }


  /**
   * Gets the Donors from the serer and updates the TableView.
   *
   * @param token String authentication token of the current user.
   * @param parameters String parameters for GET request.
   * @param isInitialising boolean
   * @param isChangingPage boolean
   */
  private void getReceiverRecordsFromServer(String token, String parameters, boolean isInitialising, boolean isChangingPage) {
    PageNav.loading();
    String address = ADDRESSES.GET_TRANSPLANT_WAITING_LIST.getAddress() + "?amount=16" + parameters;
    GetTask manyTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token, true);
    manyTask.setOnSucceeded(event -> {
        if(!isInitialising){
            receiverRecordsServer.removeAll(receiverRecordsServer);
            generateAccounts();
        }


      ResponseEntity responseEntity = (ResponseEntity) manyTask.getValue();
      total = Integer.parseInt(responseEntity.getHeaders().get("total").get(0));
      List receiverRecordsList = (List) responseEntity.getBody();
      if (!receiverRecordsServer.isEmpty()){
        receiverRecordsServer.clear();
      }
      for(Object object: receiverRecordsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        ReceiverRecord receiverRecord = objectMapper.convertValue(object, ReceiverRecord.class);
        receiverRecordsServer.add(receiverRecord);
      }
      setAccountsListRowFactory();
      PageNav.loaded();
      updateMessage();
      if(isInitialising) {
        secondInit();
      } else {
        refresh(isChangingPage);
      }
    });
    manyTask.setOnFailed(event -> {

      PageNav.loaded();
      if(isInitialising) {
        secondInit();
      } else {
        refresh(isChangingPage);
      }

    });
    new Thread(manyTask).start();
  }

}
