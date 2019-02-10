package seng302.controllers;


import static seng302.controllers.BaseController.createBadPopUp;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.http.ResponseEntity;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.Session;
import seng302.model.enums.ADDRESSES;
import seng302.model.locationData.RegionList;
import seng302.model.person.*;
import seng302.services.DeleteTask;
import seng302.services.GetTask;


/**
 * This is a JavaFX controller for the ListView pane. This displays a list of accounts in a table
 * which can be sorted in ascending or descending order.
 */
public class DonorListController extends ListController {


  private boolean isDescending = false;

  private String sortBy = "";

  private int total = 0;

  private String lastSuccessfulQuery = "";


  /**
   * The TableView object in which each TableColumn object is contained.
   */
  @FXML
  private HeaderlessTable accountsList;

  /**
   * TableColumn object for displaying account names.
   */
  @FXML
  private TableColumn<DonorReceiver, String> nameColumn;

  /**
   * Button for changing name column sort order.
   */
  @FXML
  private Button nameOrder;

  /**
   * TableColumn for displaying account ages.
   */
  @FXML
  private TableColumn<DonorReceiver, Integer> ageColumn;

  /**
   * Button for changing age column sort order.
   */
  @FXML
  private Button ageOrder;

  /**
   * TableColumn object for displaying account genders.
   */
  @FXML
  private TableColumn<DonorReceiver, String> genderColumn;

  /**
   * Button for changing gender order.
   */
  @FXML
  private Button genderOrder;

  /**
   * TableColumn object for displaying account regions.
   */
  @FXML
  private TableColumn<DonorReceiver, String> regionColumn;

  /**
   * Button for changing region order.
   */
  @FXML
  private Button regionOrder;


  /**
   * Button for changing city order
   */
  @FXML
  private Button cityOrder;

  /**
   * TableColumn object for displaying account status (donor/receiver).
   */
  @FXML
  private TableColumn<DonorReceiver, String> statusColumn;

  /**
   * Button for changing account status (donor/receiver) order
   */
  @FXML
  private Button statusOrder;

  /**
   * A search field used by the user to narrow the number of accounts shown.
   */
  @FXML
  private TextField searchField;

  @FXML
  private Button createDonorButton;

  /**
   * A Button object which is used to delete the selected account.
   */
  @FXML
  private Button deleteButton;

  /**
   * A Button object which redirects the user to the main menu when clicked.
   */
  @FXML
  private Button backButton;


  /**
   * Label for displaying the number of matches during a search.
   */
  @FXML
  private Label matches;


  /**
   * CheckComboBox for filtering table entries by birth gender
   */
  @FXML
  private ComboBox filterBirthGender;


  /**
   * CheckComboBox for filtering of table entries by receiver organs
   */
  @FXML
  private ComboBox filterReceiverOrgans;


  /**
   * CheckComboBox for filtering of table entries by donor organs
   */
  @FXML
  private ComboBox filterDonorOrgans;


  /**
   * CheckComboBox for filtering of table entries by region
   */
  @FXML
  private ComboBox filterRegion;


  /**
   * CheckComboBox for filtering of table entries by receiver/donor status
   */
  @FXML
  private ComboBox filterStatus;


  /**
   * TextField for filtering of table entries by minimum age
   */
  @FXML
  private TextField filterMinAge;


  /**
   * TextField for filtering of table entries by maximum age
   */
  @FXML
  private TextField filterMaxAge;


  @FXML
  Pagination pageControl;

  public static Session session = App.getCurrentSession();


  /**
   * ObservableList for storing region filtering options
   */
  private ObservableList<String> regions = FXCollections.observableArrayList();


  /**
   * ObservableList for storing birth gender filtering options
   */
  private ObservableList<String> birthGenders = FXCollections
      .observableArrayList("Any Gender", "Male",
          "Female", "Other", "Unknown", "Unspecified");


  /**
   * ObservableList for storing receiver organ filtering options
   */
  private ObservableList<String> receiverOrgans = FXCollections
      .observableArrayList("Any Receiver Organ",
          "Liver", "Kidneys", "Pancreas", "Heart", "Lungs", "Intestine", "Corneas",
          "Middle Ears", "Skin", "Bone", "Bone marrow", "Connective Tissue");


  /**
   * ObservableList for storing donor organ filtering options
   */
  private ObservableList<String> donorOrgans = FXCollections
      .observableArrayList("Any Donor Organ", "Liver",
          "Kidneys", "Pancreas", "Heart", "Lungs", "Intestine", "Corneas",
          "Middle Ears", "Skin", "Bone", "Bone marrow", "Connective Tissue");


  /**
   * ObservableList for storing donor/receiver status filtering options
   */
  private ObservableList<String> donorReceiverStatus = FXCollections
      .observableArrayList("Any Status",
          "Donor/Receiver", "Donor", "Receiver", "Neither");


  /**
   * ArrayList for storing CheckComboBox filtering elements
   */
  private ArrayList<ComboBox> comboBoxes = new ArrayList<>();


  /**
   * ArrayList for storing TextField filtering elements
   */
  private ArrayList<TextField> textFields = new ArrayList<>();


  /**
   * An account selected by the user on the table.
   */
  private static DonorReceiver selectedAccount;


  /**
   * The list of all accounts.
   */
  private ObservableList<DonorReceiver> observableAccounts;

  /**
   * The list of all accounts to be displayed in TableView that match the search criteria.
   */
  private FilteredList<DonorReceiver> filteredAccounts;


  private List<DonorReceiver> donorReceiversServer = new ArrayList<>();

  /**
   * Enumeration for referencing column sort order.
   */
  private enum Order {
    DEFAULT, NAME_ASC, NAME_DESC, AGE_ASC, AGE_DESC,
    GENDER_ASC, GENDER_DESC, REGION_ASC, REGION_DESC, CITY_ASC, CITY_DESC, STATUS_ASC, STATUS_DESC
  }



  /**
   * The currently selected sort order.
   */
  private Order sortOrder = Order.DEFAULT;

  /**
   * A constant for the ascending button text.
   */
  private final String ASC_TEXT = " (Ascending)";

  /**
   * A constant for the descending button text.
   */
  private final String DESC_TEXT = " (Descending)";


  /**
   * Using criteria specified in the search field, an ArrayList of matching accounts is retrieved
   * from the account manager.
   */
  private void generateAccounts() {

    // Clear and repopulate observableAccounts.
    observableAccounts.removeAll(observableAccounts);
//    observableAccounts.addAll(accountManager.getAccountsByName(""));
    observableAccounts.addAll(donorReceiversServer);

  }


  /**
   * Deletes the selected account.
   */
  @FXML
  private void deleteSelected() {

    if (selectedAccount != null) {

      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete DonorReceiver");
      alert.setHeaderText("You are going to delete " +
          selectedAccount.getUserName() + " (" +
          selectedAccount.getFirstName() + " " +
          selectedAccount.getMiddleName() + " " +
          selectedAccount.getLastName() + ")");
      alert.setContentText("Do you wish to proceed?");

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent()) {
        if (result.get() == ButtonType.OK) {

          deleteDonor(selectedAccount.getUserName());
          PageNav.loadNewPage(PageNav.LISTVIEW);

        }
      }
    } else {

      // Warn user if no account was selected.
      noAccountSelectedAlert("Delete DonorReceiver");

    }


  }

  /**
   * Sends a delete request for the currently selected clinician in the clinicians list. Will not delete the default clinician
   * @param username String of the username of the clinician account to be deleted
   */
  private void deleteDonor(String username) {
    String token = App.getCurrentSession().getToken();
    DeleteTask deleteDonorTask = new DeleteTask(ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR.getAddress() + username, token);
    deleteDonorTask.setOnSucceeded(event -> {
      AccountManager.getStatusUpdates().add("User " + username + " deleted.");
      PageNav.loaded();
    });
    deleteDonorTask.setOnFailed(event -> {
      createBadPopUp();
      PageNav.loaded();
    });
    new Thread(deleteDonorTask).start();
  }


  /**
   * Switches back to the main menu screen.
   */
  @FXML
  private void backSelected() {
    if (session.getUserType().equalsIgnoreCase("admin")) {
      PageNav.loadNewPage(PageNav.ADMINMENU);
    } else {
      PageNav.loadNewPage(PageNav.MAINMENU);
    }
  }



  /**
   * Updates the text of all order buttons on the list view pane
   * and sets the value of sortBy and isDescending which are sent
   * as parameters in the GET request to the server.
   */
  private void updateOrderButtons() {

    // Reset button names.
    nameOrder.setText("Name");
    ageOrder.setText("Age");
    genderOrder.setText("Gender");
    regionOrder.setText("Region");
    statusOrder.setText("Donor/Receiver");

    if (sortOrder == Order.NAME_ASC) {

      nameOrder.setText(nameOrder.getText() + ASC_TEXT);
      sortBy="name";
      isDescending=false;

    } else if (sortOrder == Order.NAME_DESC) {

      nameOrder.setText(nameOrder.getText() + DESC_TEXT);
      sortBy="name";
      isDescending=true;

    } else if (sortOrder == Order.AGE_ASC) {

      ageOrder.setText(ageOrder.getText() + ASC_TEXT);
      sortBy="age";
      isDescending=false;

    } else if (sortOrder == Order.AGE_DESC) {

      ageOrder.setText(ageOrder.getText() + DESC_TEXT);
      sortBy="age";
      isDescending=true;

    } else if (sortOrder == Order.GENDER_ASC) {

      genderOrder.setText(genderOrder.getText() + ASC_TEXT);
      sortBy="gender";
      isDescending=false;

    } else if (sortOrder == Order.GENDER_DESC) {

      genderOrder.setText(genderOrder.getText() + DESC_TEXT);
      sortBy="gender";
      isDescending=true;

    } else if (sortOrder == Order.REGION_ASC) {

      regionOrder.setText(regionOrder.getText() + ASC_TEXT);
      sortBy="region";
      isDescending=false;

    } else if (sortOrder == Order.REGION_DESC) {

      regionOrder.setText(regionOrder.getText() + DESC_TEXT);
      sortBy="region";
      isDescending=true;

    } else if (sortOrder == Order.DEFAULT) {

      // Revert to default order based on name.
      sortBy="";
      isDescending=false;

    }

    updateFiltering();

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
  private void updateAgeOrder(ActionEvent event) {

    if (sortOrder == Order.AGE_ASC) {

      sortOrder = Order.AGE_DESC;

    } else if (sortOrder == Order.AGE_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.AGE_ASC;

    }

    updateOrderButtons();

  }


  /**
   * Event handler for the genderOrder button.
   *
   * @param event An action which occurred on the genderOrder button.
   */
  @FXML
  private void updateGenderOrder(ActionEvent event) {

    if (sortOrder == Order.GENDER_ASC) {

      sortOrder = Order.GENDER_DESC;

    } else if (sortOrder == Order.GENDER_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.GENDER_ASC;

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



  /**
   * Event handler for the statusOrder button.
   *
   * @param event An action which occurred on the statusOrder button.
   */
  @FXML
  private void updateStatusOrder(ActionEvent event) {

    if (sortOrder == Order.STATUS_ASC) {

      sortOrder = Order.STATUS_DESC;

    } else if (sortOrder == Order.STATUS_DESC) {

      sortOrder = Order.DEFAULT;

    } else {

      sortOrder = Order.STATUS_ASC;

    }

    updateOrderButtons();

  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    matches.setTextAlignment(TextAlignment.CENTER);
    matches.setText(getMatchesMessage("account"));

    // Retrieve start and end index of current page.
    int startIndex = pageControl.getCurrentPageIndex() * accountsPerPage;

    String address = lastSuccessfulQuery + "&index=" + startIndex;

    donorReceiversServer.clear();
    getDonorsFromServer(session.getToken(), address, false, true);

  }


  /**
   * Updates the page numbers and account numbers displayed at the bottom
   * of the page.
   */
  private void updateMessage(){
    // Update label showing number of matches.
    matches.setTextAlignment(TextAlignment.CENTER);
    matches.setText(getMatchesMessage("account"));
  }



  /**
   * Sets the row factory for accountsList. This creates a tooltip for each populated row showing an
   * overview of the account. This code is based on a solution (https://stackoverflow.com/a/26221242)
   * posted by James_D in response to a question on Stack Overflow.
   */
  private void setAccountsListRowFactory() {

    accountsList.setRowFactory(tableView -> new TableRow<DonorReceiver>() {

      @Override
      public void updateItem(DonorReceiver donorReceiver, boolean empty) {

        super.updateItem(donorReceiver, empty);
        getStyleClass().remove("conflictingOrgans");
        if (donorReceiver == null) {

          // If no donorReceiver is associated with row, remove tool tip.
          setStyle("");
          setTooltip(null);

        } else {

          // Otherwise, generate donorReceiver-specific tool tip.
          Tooltip overview = new Tooltip();
          String conflictingOrgans = donorReceiver.isReceivingDonatingOrgans();
          // Highlight the row if the donorReceiver is donating an organ they're receiving
          if (conflictingOrgans != null) {
            setStyle("-fx-background-color:#ea9491");
            getStyleClass().add("conflictingOrgans");
            overview.setText(donorReceiver.generateOverview()
                    + '\n' + donorReceiver.isReceivingDonatingOrgans());
          } else if (donorReceiver.getDeathDetails().getDoD() != null) {
            setStyle("-fx-background-color:#ea9491");
            getStyleClass().add("conflictingOrgans");
          } else {
            overview.setText(donorReceiver.generateOverview());
            setStyle(""); // Reset the style to prevent random rows from being higlighted
          }
          setTooltip(overview);
        }
      }

    });
  }

  /**
   * Refresh the table when any account receives a changeF
   *
   * @throws NullPointerException an exception
   */
  public static void triggerRefresh() throws NullPointerException {

    TableView accountsList = (TableView) App.getWindow().getScene().lookup("#accountsList");

    if (accountsList == null) {

      // Handle case were accountsList is not initialised.
      throw new NullPointerException(
              "TableView object 'accountsList' is not initialised in the main window.");

    } else {

      // Trigger instantiated update.
      accountsList.refresh();

    }

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
      ViewProfilePaneController.setAccount(donorReceiver);
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
   * Initialize the list view whenever the pane is created.
   */
  @FXML
  public void initialize() {
    super.pageControl = pageControl;
    deleteButton.setDisable(true);
    createDonorButton.getStyleClass().add("createButton");
    deleteButton.getStyleClass().add("deleteButton");
    backButton.getStyleClass().add("backButton");
    getDonorsFromServer(App.getCurrentSession().getToken(), "", true, false);
    accountsList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    statusOrder.setDisable(true);
  }

  public void secondInit() {


    accountsList.setPlaceholder(new Label("No accounts found"));




    selectedAccount = null; // Reset selected account.
    setAccountsListRowFactory();
    initializeColumns(); // Set comparators.

    // Populate a master list with all Accounts, regardless of page.
    observableAccounts = FXCollections.observableArrayList();

    generateAccounts();

    filteredAccounts = new FilteredList<>(observableAccounts);

    populateFilterOptions();
    setupFilterListeners();

    sortedAccounts = new SortedList<>(filteredAccounts);
    //sortedAccounts.setComparator(new NameComparator());
    super.sortedAccounts = sortedAccounts;

    updatePageCount();

    SortedList<DonorReceiver> pageDonorReceivers = new SortedList<>(
            FXCollections.observableArrayList(sortedAccounts));
    pageDonorReceivers.comparatorProperty().bind(accountsList.comparatorProperty());

    accountsList.setItems(pageDonorReceivers);
    pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
      updateTableView();
    });
    // Event handler for account selection.
    accountsList.setOnMouseClicked(click -> {
      selectedAccount = (DonorReceiver) accountsList.getSelectionModel().getSelectedItem();
      deleteButton.setDisable(false);
      if (click.getButton().equals(MouseButton.PRIMARY) &&
              click.getClickCount() == 2 && selectedAccount != null) {
        String endpoint = ADDRESSES.GET_DONOR.getAddress() + selectedAccount.getUserName();
        GetTask task = new GetTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
        task.setOnSucceeded(event -> {
          DonorReceiver donorReceiver = (DonorReceiver) task.getValue();
          try {
            PageNav.isAdministrator = true;
            loadProfileWindow(donorReceiver);
          } catch (IOException e) {
            errorOpeningProfileAlert();
          }
        });
        new Thread(task).start();
      }
    });
    searchField.setText(PageNav.searchValue); // Set the search text to what the user entered.
    // NOTE: This has to be set after the listener is added otherwise the filteredlist doesn't update the
    updateMessage();
  }



  private void refresh(boolean pageChange){



    observableAccounts.removeAll(observableAccounts);

    generateAccounts();

    filteredAccounts = new FilteredList<>(observableAccounts);

    sortedAccounts = new SortedList<>(filteredAccounts);

    super.sortedAccounts = sortedAccounts;
    // Create new sublist.
    SortedList<DonorReceiver> pageDonorReceivers = new SortedList<>(
            FXCollections.observableArrayList(sortedAccounts));
    pageDonorReceivers.comparatorProperty().bind(accountsList.comparatorProperty());

    accountsList.setItems(pageDonorReceivers);
    if(!pageChange) {
      updatePageCount();
    }
    accountsList.refresh();
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
   * Helper method for 'initialize()' which sets the cell factory of each column in the TableView
   * object.
   */
  private void initializeColumns() {

    // nameColumn
    nameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        (
            record.getValue().getFirstName() + " " +
                record.getValue().getMiddleName() + " " +
                record.getValue().getLastName()
        ).replaceAll("\\s+", " ")
    ));

    statusColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        getAccountStatus(record.getValue())
    ));

    // ageColumn
    ageColumn.setCellValueFactory(record -> new SimpleIntegerProperty(
        record.getValue().calculateAge()).asObject());

    // genderColumn
    genderColumn.setCellValueFactory(record -> new
        ReadOnlyObjectWrapper<String>(record.getValue().genderString()));

    // regionColumn
    regionColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getContactDetails().getAddress().getRegion()));


  }


  /**
   * Checks the status of a donor
   */
  public String getAccountStatus(DonorReceiver account) {
    Boolean isDonating = true;
    Boolean isReceiving = false;
    try {
      String donatingString = account.getDonorOrganInventory().toString();
      ArrayList receivingString = account.getRequiredOrgans().getOrgansInList();

      for (Object organ: receivingString) {
          if ((boolean) organ) {
              isReceiving = true;
              break;
          }
      }

      if (donatingString.equals("Organs to donate:\nNo organs to donate\n\n")) {
        isDonating = false;
      }

      if (isDonating && isReceiving) {
        return "Donor/Receiver";
      }
      if (isDonating) {
        return "Donor";
      }
      if (isReceiving) {
        return "Receiver";
      }
      return "";
    } catch (NullPointerException e) {
      return ""; // If status is null then return empty status
    }
  }


  /**
   * Returns true if the given account matches any of the given genders. False otherwise.
   *
   * @param account the account to filter
   * @param birthGenders the genders that are accepted
   * @return true if matching, false otherwise
   */
  boolean accountBirthGender(DonorReceiver account, ObservableList<String> birthGenders) {
    try {
      if (birthGenders.contains("Any Gender")) {
        return true;
      } else {
        return birthGenders.contains(account.genderString());
      }
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Returns true if the given accounts receiver organs matches any of the given organs. False
   * otherwise.
   *
   * @param account the account to filter
   * @param receiverOrgans the organs that are accepted
   * @return true if matching, false otherwise
   */
  boolean accountReceiverOrgans(DonorReceiver account, ObservableList<String> receiverOrgans) {
    try {
      if (receiverOrgans.contains("Any Receiver Organ")) {
        return true;
      } else {
        for (String organ : receiverOrgans) {
          if (organ.equalsIgnoreCase("bone")) {
            if (account.getRequiredOrgans().getBone()) {
              return true;
            }
          } else {
            if (account.getRequiredOrgans().toString().contains(organ)) {
              return true;
            }
          }
        }
        return false;
      }
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Returns true if the given accounts donor organs matches any of the given organs. False
   * otherwise.
   *
   * @param account the account to filter
   * @param donorOrgans the organs that are accepted
   * @return true if matching, false otherwise
   */
  public boolean accountDonorOrgans(DonorReceiver account, ObservableList<String> donorOrgans) {
    try {
      if (donorOrgans.contains("Any Donor Organ")) {
        return true;
      } else {
        for (String organ : donorOrgans) {
          if (organ.equalsIgnoreCase("bone")) {
            if (account.getDonorOrganInventory().getBone()) {
              return true;
            }
          } else {
            if (account.getDonorOrganInventory().toString().contains(organ)) {
              return true;
            }
          }
        }
        return false;
      }
    } catch (NullPointerException e) {
      return false;
    }
  }


  /**
   * Uses TextFormatter to only allow numbers in the given TextField
   *
   * @param numberField TextField to restrict
   */
  private void numberOnlyTextField(TextField numberField) {
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
      String input = change.getText();
      if (input.matches("[0-9]*")) {
        return change;
      }
      return null;
    };
    numberField.setTextFormatter(new TextFormatter<String>(integerFilter));
  }



  /**
   * Used to filter the list, called by filter and search event listeners
   */
  private void updateFiltering() {

    String address = "";

    if (filterRegion.getSelectionModel().getSelectedIndex() != 0){
      address += "&region=";
      address += filterRegion.getSelectionModel().getSelectedItem().toString();
    }
    if (filterBirthGender.getSelectionModel().getSelectedIndex() != 0){
      address += "&gender=";
      address += filterBirthGender.getSelectionModel().getSelectedItem().toString().toLowerCase();
    }
    if (filterDonorOrgans.getSelectionModel().getSelectedIndex() != 0){
      address += "&donations=";
      address += filterDonorOrgans.getSelectionModel().getSelectedItem().toString().toLowerCase().replaceAll(" ", "");
    }
    if (filterReceiverOrgans.getSelectionModel().getSelectedIndex() != 0){
      address += "&receiving=";
      address += filterReceiverOrgans.getSelectionModel().getSelectedItem().toString().toLowerCase().replaceAll(" ", "");
    }
    if (filterStatus.getSelectionModel().getSelectedIndex() != 0){
      address += "&status=";
      address += filterStatus.getSelectionModel().getSelectedItem().toString().toLowerCase();
    }
    if (!searchField.getText().isEmpty()){
      address += "&q=";
      address += searchField.getText();
    }
    if (!filterMinAge.getText().isEmpty()){
      address += "&minAge=";
      address += filterMinAge.getText();
    }
    if (!filterMaxAge.getText().isEmpty()){
      address += "&maxAge=";
      address += filterMaxAge.getText();
    }
    if (!sortBy.equalsIgnoreCase("")){
      address += "&sortBy=";
      address += sortBy;
      address += "&isDescending=";
      if (isDescending){
        address += "true";
      } else {
        address += "false";
      }
    }
    lastSuccessfulQuery = address;
    donorReceiversServer.clear();
    getDonorsFromServer(session.getToken(), address, false, false);

  }


  /**
   * Populates the filter options and adds the filter elements to ArrayLists so that they can have
   * event listeners added iteratively.
   */
  private void populateFilterOptions() {


    regions.add("Any Region");
    regions.addAll(RegionList.getInstance().getRegions("NZ"));
    filterRegion.getItems().addAll(regions);

    // Populate the filters that have static options
    filterDonorOrgans.getItems().addAll(donorOrgans);
    filterReceiverOrgans.getItems().addAll(receiverOrgans);
    filterBirthGender.getItems().addAll(birthGenders);
    filterStatus.getItems().addAll(donorReceiverStatus);

    // Set all ComboBoxes to the default value
    filterBirthGender.getSelectionModel().selectFirst();
    filterDonorOrgans.getSelectionModel().selectFirst();
    filterReceiverOrgans.getSelectionModel().selectFirst();
    filterStatus.getSelectionModel().selectFirst();
    filterRegion.getSelectionModel().selectFirst();
  }


  /**
   * Iteratively adds listeners to the elements used for filtering.
   */
  private void setupFilterListeners() {

    comboBoxes.add(filterBirthGender);
    comboBoxes.add(filterDonorOrgans);
    comboBoxes.add(filterReceiverOrgans);
    comboBoxes.add(filterRegion);
    comboBoxes.add(filterStatus);
    comboBoxes.add(filterBirthGender);

    //Add an event listener to each comboBox
    comboBoxes.forEach(comboBox -> {
      comboBox.valueProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue ov, String t, String t1) {
          if (t1 != null) {
            updateFiltering();
          }
        }
      });

      });


    // Only allow integers in min and max age
    numberOnlyTextField(filterMinAge);
    numberOnlyTextField(filterMaxAge);


    textFields.add(filterMaxAge);
    textFields.add(filterMinAge);
    textFields.add(searchField);


    //Add an event listener to each TextField
    textFields.forEach(textField -> {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        updateFiltering();

      });
    });
  }

  @FXML
  private void createDonorPressed() {
    PageNav.loadNewPage(PageNav.CREATE);
  }


  /**
   * Gets the Donors from the serer and updates the TableView.
   *
   * @param token String authentication token of the current user.
   * @param parameters String parameters for GET request.
   * @param isInitialising boolean
   * @param isChangingPage boolean
   */
  private void getDonorsFromServer(String token, String parameters, boolean isInitialising, boolean isChangingPage) {
    PageNav.loading();
    String address = ADDRESSES.GET_DONORS.getAddress() + "?amount=16" + parameters;
    GetTask manyTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token, true);
    manyTask.setOnSucceeded(event -> {
      ResponseEntity responseEntity = (ResponseEntity) manyTask.getValue();
      total = Integer.parseInt(responseEntity.getHeaders().get("total").get(0));
      List donorReceiverList = (List) responseEntity.getBody();
      if (!donorReceiversServer.isEmpty()){
        donorReceiversServer.clear();
      }
      for(Object object: donorReceiverList) {
        ObjectMapper objectMapper = new ObjectMapper();
        DonorReceiverSummary donorReceiverSummary = objectMapper.convertValue(object, DonorReceiverSummary.class);
        donorReceiversServer.add(createDonorReceiver(donorReceiverSummary));
      }
      updateMessage();
      PageNav.loaded();
      if(isInitialising) {
        secondInit();
      } else {
        refresh(isChangingPage);
      }
    });
    manyTask.setOnFailed(event -> {
      createBadPopUp();
      PageNav.loaded();
      if(isInitialising) {
        secondInit();
      } else {
        refresh(isChangingPage);
      }

    });
    new Thread(manyTask).start();
  }




  /**
   * Creates a DonorReceiver and inserts into the account manager
   * @param donorReceiverSummary Summary of DonorReceiver
   */
  private DonorReceiver createDonorReceiver(DonorReceiverSummary donorReceiverSummary) {
    DonorReceiver donorReceiver = new DonorReceiver(donorReceiverSummary.getGivenName(), donorReceiverSummary.getMiddleName(), donorReceiverSummary.getLastName(), donorReceiverSummary.getDateOfBirth(), donorReceiverSummary.getNhi());
    donorReceiver.setDonorOrganInventory(donorReceiverSummary.getDonorOrganInventory());
    donorReceiver.setRequiredOrgans(donorReceiverSummary.getReceiverOrganInventory());
    Address address = new Address("","","",donorReceiverSummary.getCity(),donorReceiverSummary.getRegion(),"","NZ");
    ContactDetails contactDetails = new ContactDetails(address, "", "", "");
    donorReceiver.setContactDetails(contactDetails);
    donorReceiver.setGender(donorReceiverSummary.getGender().charAt(0));
    return donorReceiver;
  }


}

