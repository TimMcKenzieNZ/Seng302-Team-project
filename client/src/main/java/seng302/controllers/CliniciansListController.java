package seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.Clinician;
import seng302.model.person.UserSummary;
import seng302.services.DeleteTask;
import seng302.services.GetTask;

public class CliniciansListController extends ListController {

  @FXML
  public Label errorMessage;
  @FXML
  public Button backButton;
  @FXML
  public TextField idFilterTextField;
  @FXML
  public TextField nameFilterTextField;
  @FXML
  public TextField regionFilterTextField;
  @FXML
  public TableView<UserSummary> cliniciansTable;
  @FXML
  public TableColumn<UserSummary, String> idColumn;
  @FXML
  public TableColumn<UserSummary, String> nameColumn;
  @FXML
  public TableColumn<UserSummary, String> regionColumn;
  @FXML
  public Button newClinicianButton;
  @FXML
  public Button deleteButton;
  @FXML
  public Pagination clinicianPageControl;
  @FXML
  public Label numAccountsLabel;


  //========================================================
  //For the table

  /**
   * An account selected by the user on the table.
   */
  private static UserSummary selectedAccount;

  /**
   * The list of all administrators.
   */
  private ObservableList<UserSummary> observableAccounts;

  /**
   * The list of all administrators to be displayed in TableView that match the search criteria.
   */
  private List<UserSummary> clinicianList = new ArrayList<>();
  private ScheduledExecutorService schedule;
  private Runnable cancelTask;


  @FXML
  public void backButtonPressed() {
    schedule.schedule(cancelTask, 0, TimeUnit.SECONDS);
    PageNav.loadNewPage(PageNav.ADMINMENU);
  }

  @FXML
  public void newClinicianButtonPressed() {
    schedule.schedule(cancelTask, 0, TimeUnit.SECONDS);
    PageNav.loadNewPage(PageNav.CREATECLINICIAN);
  }


  /**
   * Opens a view profile pane in a new window.
   *
   * @param clinician The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  private void loadProfileWindow(Clinician clinician) throws IOException {
    // Only load new window if childWindowToFront fails.
    if (!App.childWindowToFront(clinician)) {

      // Set the selected donorReceiver for the profile pane and confirm child.
      ClinicianProfileController.setClinician(clinician);
      ClinicianProfileController.setIsChild(true);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader(getClass().getResource(PageNav.VIEWEDITCLINICIAN));
      StackPane profilePane = loader.load();

      // Create new scene.
      Scene profileScene = new Scene(profilePane);

      // Create new stage.
      Stage profileStage = new Stage();
      profileStage.setTitle("Profile for " + clinician.getUserName());
      profileStage.setScene(profileScene);
      profileStage.show();

      App.addChildWindow(profileStage, clinician);
    }
  }


  /**
   * Opens the profile of the clinician who was selected
   */
  @FXML
  public void openProfileButtonPressed() {
    if (selectedAccount == null) {
      noAccountSelectedAlert("Clinician");
    } else {
      GetTask task = new GetTask(Clinician.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_CLINICIAN.getAddress() + selectedAccount.getUsername(), App.getCurrentSession().getToken());
      task.setOnSucceeded(event -> {
        Clinician selectedClinician = (Clinician) task.getValue();
        try {
          loadProfileWindow(selectedClinician);
        } catch (IOException exception) {
          errorOpeningProfileAlert();
        }
      });
      task.setOnFailed(event -> errorOpeningProfileAlert());
      new Thread(task).start();
    }
  }


  /**
   * Runs through the procedure when the delete button is pressed
   */
  @FXML
  public void deleteButtonPressed() {
    if (selectedAccount == null) {
      // Warn user if no account was selected.
      noAccountSelectedAlert("Delete Clinician");
    } else {
      if (!selectedAccount.getUsername().equals(Clinician.defaultValue)) {
        makeSureOfDelete(selectedAccount.getUsername());
        PageNav.loadNewPage(PageNav.CLINICIANSLIST);
      }
      else {
        errorMessage.setTextFill(Color.web("red"));
        errorMessage.setText("Cannot delete the default Clinician!");
      }

    }
  }


  /**
   * Prompts the user to confirm delete of clinician
   * @param username the username of the selected clinician account to be deleted
   */
  private void makeSureOfDelete(String username) {
    Dialog<Boolean> dialog = new Dialog<>();
    dialog.setTitle("Delete");
    dialog.setHeaderText("Deletion of Clinician");
    dialog.setContentText(String.format("Are you sure you want to delete clinician %s ?", username));
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
    final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
    yesButton.addEventFilter(ActionEvent.ACTION, event -> deleteClinician(username));
    dialog.showAndWait();
  }


  /**
   * Sends a delete request for the currently selected clinician in the clinicians list. Will not delete the default clinician
   * @param username String of the username of the clinician account to be deleted
   */
  private void deleteClinician(String username) {
    String token = App.getCurrentSession().getToken();
    if (!username.equals(Clinician.defaultValue)) {
      DeleteTask deleteClinicianTask = new DeleteTask(ADDRESSES.SERVER.getAddress(), ADDRESSES.DELETE_CLINICIAN.getAddress() + username, token);
      deleteClinicianTask.setOnSucceeded(event -> {
        AccountManager.getStatusUpdates().add("Deleted Clinician " + username);
        PageNav.loaded();
      });
      deleteClinicianTask.setOnFailed(event -> {
        errorMessage.setTextFill(Color.web("red"));
        errorMessage.setText(String.format("Failed to delete Clinician %s!", username));
        PageNav.loaded();
      });
      new Thread(deleteClinicianTask).start();
    } else {
      errorMessage.setTextFill(Color.web("red"));
      errorMessage.setText("Cannot delete the default Clinician!");
    }
  }


  /**
   * Using criteria specified in the search field, an ArrayList of matching accounts is retrieved
   * from the account manager.
   */
  private void generateAccounts() {

    // Clear and repopulate observableAccounts.
    observableAccounts.removeAll(observableAccounts);
    observableAccounts.addAll(clinicianList);
  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    numAccountsLabel.setText(getMatchesMessage("account"));

    // Retrieve start and end index of current page.
    int startIndex = clinicianPageControl.getCurrentPageIndex() * accountsPerPage;
    int endIndex = Math.min(startIndex + accountsPerPage, observableAccounts.size());

    // Create new sublist.
    SortedList<UserSummary> pageClinician = new SortedList<>(
        FXCollections.observableArrayList(observableAccounts.subList(startIndex, endIndex)));
    pageClinician.comparatorProperty().bind(cliniciansTable.comparatorProperty());

    cliniciansTable.setItems(pageClinician);
    cliniciansTable.getSelectionModel().select(selectedAccount);
  }


  @FXML
  public void initialize() {
    backButton.getStyleClass().add("backButton");
    newClinicianButton.getStyleClass().add("createButton");
    deleteButton.getStyleClass().add("deleteButton");
    super.pageControl = clinicianPageControl;
    PageNav.loading();
    nameColumn.setSortable(false);
    idColumn.setSortable(false);
    regionColumn.setSortable(false);
    getClinicians();
    cliniciansTable.setRowFactory( tv -> {
      TableRow<UserSummary> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
          openProfileButtonPressed();
        }
      });
      return row ;
    });
  }


  /**
   * Loads the table with the wanted information
   */
  private void loadTable() {
    cliniciansTable.setPlaceholder(new Label("No accounts found"));
    deleteButton.setDisable(true);
    initializeColumns();
    observableAccounts = FXCollections.observableArrayList();
    generateAccounts();
    cliniciansTable.setItems(observableAccounts);

    ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(clinicianList);
    FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
    sortedAccounts = new SortedList<>(filtered);

    updatePageCount();
    updateTableView();

    clinicianPageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
      updateTableView();
    });

    cliniciansTable.setOnMouseClicked(click -> {
      UserSummary record = cliniciansTable.getSelectionModel().getSelectedItem();
      if (record != null) {
        selectedAccount = record;
        deleteButton.setDisable(false);
      } else {
        selectedAccount = null;
        deleteButton.setDisable(true);
      }
    });

    nameFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      renewClinicians(
          nameFilterTextField.getText(),
          idFilterTextField.getText(),
          regionFilterTextField.getText(),
          0);
      selectedAccount = null;
    });

    idFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      renewClinicians(
          nameFilterTextField.getText(),
          idFilterTextField.getText(),
          regionFilterTextField.getText(),
          0);
      selectedAccount = null;
    });

    regionFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      renewClinicians(
          nameFilterTextField.getText(),
          idFilterTextField.getText(),
          regionFilterTextField.getText(),
          0);
      selectedAccount = null;
    });

    schedule = Executors.newScheduledThreadPool(1);
    Runnable runnable = this::refreshClinicians;
    Future<?> refresh = schedule.scheduleAtFixedRate(runnable, 2, 2, TimeUnit.SECONDS);
    cancelTask = () -> refresh.cancel(true);
  }


  /**
   * Refreshes the list of clinicians if there is a change to the clinicians on the server
   */
  private void refreshClinicians() {
    String name = nameFilterTextField.getText();
    String username = idFilterTextField.getText();
    String region = regionFilterTextField.getText();

    String address = createClinicianAddress(name, username, region);
    String token = App.getCurrentSession().getToken();

    GetTask getCliniciansTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getCliniciansTask.setOnSucceeded(event -> {
      List newClinicianList = (ArrayList) getCliniciansTask.getValue();
      if (!UserSummary.userSummaryListEquals(clinicianList, newClinicianList)) {
        UserSummary selectedClinician = selectedAccount;
        clinicianTableUpdate(getCliniciansTask, pageControl.getCurrentPageIndex());
        cliniciansTable.getSelectionModel().select(selectedClinician);
      }
    });
    new Thread(getCliniciansTask).start();
  }



  /**
   * Creates an address for the task to be executed on
   * @param name The name the user is searching for
   * @param username The username the user is searching for
   * @return The string address the system should query for the information needed
   */
  private String createClinicianAddress(String name, String username, String region) {
    String address = ADDRESSES.GET_CLINICIANS.getAddress();

    boolean beforeField = false;

    if (!name.equals("")) {
      address += "?name=" + name;
      beforeField = true;
    }

    if (!username.equals("")) {
      if (beforeField) {
        address += "&";
      } else {
        address += "?";
      }
      address += "id=" + username;
      beforeField = true;
    }

    if (!region.equals("")) {
      if (beforeField) {
        address += "&";
      } else {
        address += "?";
      }
      address += "region=" + region;
    }

    return address;
  }


  /**
   * Gets the administrators from the server
   */
  private void getClinicians() {
    String address = createClinicianAddress("", "", "");
    String token = App.getCurrentSession().getToken();
    GetTask getClinicianTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getClinicianTask.setOnSucceeded(event -> {
      List cliniciansList = (ArrayList) getClinicianTask.getValue();
      for (Object clinician : cliniciansList) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserSummary summary = objectMapper.convertValue(clinician, UserSummary.class);
        clinicianList.add(summary);
      }
      loadTable();
      PageNav.loaded();
    });
    getClinicianTask.setOnFailed(event -> {
      PageNav.loaded();
      cliniciansTable.setPlaceholder(new Label("System Error: No Clinicians Found"));
    });
    new Thread(getClinicianTask).start();
  }


  private void updateClinicianPageView(int page) {
    pageControl.setCurrentPageIndex(page);
    pageControl.setPageCount(Math.max(1, (int) Math.ceil((float) sortedAccounts.size() /
        accountsPerPage)));
  }


  /**
   * Updates the contents of the clinicians table once a new list has been retrieved
   * @param getCliniciansTask The task used to get the new list of clinicians
   * @param currentPageIndex The current page index the user was looking at
   */
  private void clinicianTableUpdate(GetTask getCliniciansTask, int currentPageIndex) {
    clinicianList.clear();
    List retrievedClinicians = (ArrayList) getCliniciansTask.getValue();
    for (Object clinician : retrievedClinicians) {
      ObjectMapper objectMapper = new ObjectMapper();
      UserSummary summary = objectMapper.convertValue(clinician, UserSummary.class);
      clinicianList.add(summary);
    }
    initializeColumns();
    observableAccounts = FXCollections.observableArrayList();
    generateAccounts();
    cliniciansTable.setItems(observableAccounts);

    ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(clinicianList);
    FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
    sortedAccounts = new SortedList<>(filtered);

    updateClinicianPageView(currentPageIndex);
    updateTableView();
  }


  /**
   * Gets the admins from the system when a search term is entered into the fields
   * @param name The name the user is looking for
   * @param username The username the user is looking for
   */
  private void renewClinicians(String name, String username, String region, int currentPageIndex) {
    clinicianList.clear();
    String address = createClinicianAddress(name, username, region);
    String token = App.getCurrentSession().getToken();

    GetTask getCliniciansTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getCliniciansTask.setOnSucceeded(event -> {
      clinicianTableUpdate(getCliniciansTask, currentPageIndex);
    });

    getCliniciansTask.setOnFailed(event -> {
      clinicianList.clear();
      PageNav.loaded();

      initializeColumns();
      observableAccounts = FXCollections.observableArrayList();
      generateAccounts();
      cliniciansTable.setItems(observableAccounts);

      ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(clinicianList);
      FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
      sortedAccounts = new SortedList<>(filtered);

      updatePageCount();
      updateTableView();
    });

    new Thread(getCliniciansTask).start();
  }


  /**
   * Helper method for 'initialize()' which sets the cell factory of each column in the TableView
   * object.
   */
  private void initializeColumns() {

    // nameColumn
    nameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper((
            record.getValue().getFirstName() + " " +
                    record.getValue().getMiddleName() + " " +
                    record.getValue().getLastName()).replaceAll("\\s+", " ")
    ));

    // Region Column
    regionColumn.setCellValueFactory(record ->
            new ReadOnlyStringWrapper(record.getValue().getRegion()));

    idColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper((
            record.getValue().getUsername())));
    idColumn.setCellValueFactory(
            record -> new ReadOnlyObjectWrapper<String>(record.getValue().getUsername()));

  }
}
