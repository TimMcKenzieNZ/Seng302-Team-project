package seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import seng302.model.person.Administrator;
import java.io.IOException;
import seng302.model.person.UserSummary;
import seng302.services.DeleteTask;
import seng302.services.GetTask;


public class AdminsListController extends ListController {

  @FXML
  public Button backButton;
  @FXML
  public TextField nameFilterTextField;
  @FXML
  public TextField usernameFilterTextField;
  @FXML
  public TableView<UserSummary> adminsTable;
  @FXML
  public TableColumn<UserSummary, String> nameColumn;
  @FXML
  public TableColumn<UserSummary, String> usernameColumn;
  @FXML
  public Button newAdminButton;
  @FXML
  public Button deleteButton;
  @FXML
  public Label numAccountsLabel;
  @FXML
  public Label errorMessage;

  //========================================================
  //For the table

  /**
   * Constant which determines the number of accounts on each table page.
   */
  private static int ACCOUNTS_PER_PAGE = 30;

  /**
   * An account selected by the user on the table.
   */
  private static UserSummary selectedAccount;
  public Pagination adminPageController;
  private List<UserSummary> visibleAdmins = new ArrayList<>();

  private ScheduledExecutorService schedule;
  private Runnable cancelTask;


  /**
   * The list of all administrators.
   */
  private ObservableList<UserSummary> observableAccounts;


  @FXML
  public void backButtonPressed() {
    schedule.schedule(cancelTask, 0, TimeUnit.SECONDS);
    PageNav.loadNewPage(PageNav.ADMINMENU);
  }


  /**
   * Handle the clicking of the "Add new admin" button and open a window that creates a new admin
   */
  @FXML
  public void newAdminButtonPressed() {
    schedule.schedule(cancelTask, 0, TimeUnit.SECONDS);
    PageNav.loadNewPage(PageNav.CREATEADMIN);
  }


  /**
   * Opens a view profile pane in a new window.
   *
   * @param account The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  private void loadProfileWindow(Administrator account) throws IOException {

    // Only load new window if childWindowToFront fails.
    if (!App.childWindowToFront(account)) {

      // Set the selected account for the profile pane and confirm child.
      AdministratorViewProfileController.setSelectedAdmin(account);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader(getClass().getResource(PageNav.VIEWADMIN));
      StackPane profilePane = loader.load();

      // Create new scene.
      Scene profileScene = new Scene(profilePane);

      // Create new stage.
      Stage profileStage = new Stage();
      profileStage.setTitle("Viewing an Administrator");
      profileStage.setScene(profileScene);
      profileStage.show();

      App.addChildWindow(profileStage, account);
    }
  }


  /**
   * Opens the profile of the administrator who was selected
   */
  @FXML
  public void openProfileButtonPressed() {
    if (selectedAccount == null) {
      noAccountSelectedAlert("Administrator");
    } else {
      GetTask task = new GetTask(Administrator.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_ADMIN.getAddress() + selectedAccount.getUsername(), App.getCurrentSession().getToken());
      task.setOnSucceeded(event -> {
        Administrator selectedAdministrator = (Administrator) task.getValue();
        try {
          loadProfileWindow(selectedAdministrator);
        } catch (IOException e) {
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
      noAccountSelectedAlert("Delete Administrator");
    } else {
      String username = selectedAccount.getUsername();
      if (!username.equals(Administrator.defaultAdmin)) {
        makeSureOfDelete(username);
        PageNav.loadNewPage(PageNav.ADMINSLIST);
      }
      else {
        errorMessage.setTextFill(Color.web("red"));
        errorMessage.setText("Cannot delete the default Administrator!");
      }

    }
  }


  /**
   * Prompts the user to confirm delete of administrator
   * @param username the username of the selected administrator account to be deleted
   */
  private void makeSureOfDelete(String username) {
    Dialog<Boolean> dialog = new Dialog<>();
    dialog.setTitle("Delete");
    dialog.setHeaderText("Deletion of Administrator");
    dialog.setContentText(String.format("Are you sure you want to delete this administrator %s?", username));
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
    final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
    yesButton.addEventFilter(ActionEvent.ACTION, event -> deleteAdmin(username));
    dialog.showAndWait();
  }


  /**
   * Sends a delete request for the currently selected administrator in the admin list. Will not delete the default admin.
   * @param username String of the username of the administrator account to be deleted
   */
  private void deleteAdmin(String username) {
    String token = App.getCurrentSession().getToken();
    if (!username.equals(Administrator.defaultAdmin)) {
      DeleteTask deleteAdminsTask = new DeleteTask(ADDRESSES.SERVER.getAddress(), ADDRESSES.DELETE_ADMIN.getAddress() + username, token);
      deleteAdminsTask.setOnSucceeded(event -> {
        AccountManager.getStatusUpdates().add("Administrator " + username + " deleted.");
        PageNav.loaded();
      });
      deleteAdminsTask.setOnFailed(event -> {
        errorMessage.setTextFill(Color.web("red"));
        errorMessage.setText(String.format("Failed to delete administrator %s", username));
        PageNav.loaded();
      });
      new Thread(deleteAdminsTask).start();
    } else {
      errorMessage.setTextFill(Color.web("red"));
      errorMessage.setText("Cannot delete the default Administrator");
    }

  }


  /**
   * Using criteria specified in the search field, an ArrayList of matching accounts is retrieved
   * from the account manager.
   */
  private void generateAccounts() {
    // Clear and repopulate observableAccounts.
    observableAccounts.removeAll(observableAccounts);
    observableAccounts.addAll(visibleAdmins);
  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    numAccountsLabel.setText(getMatchesMessage("account"));

    // Retrieve start and end index of current page.
    int startIndex = adminPageController.getCurrentPageIndex() * ACCOUNTS_PER_PAGE;
    int endIndex = Math.min(startIndex + ACCOUNTS_PER_PAGE, observableAccounts.size());

    // Create new sublist.
    SortedList<UserSummary> pageAdministrators = new SortedList<>(
            FXCollections.observableArrayList(observableAccounts.subList(startIndex, endIndex)));
    pageAdministrators.comparatorProperty().bind(adminsTable.comparatorProperty());

    adminsTable.setItems(pageAdministrators);
    adminsTable.getSelectionModel().select(selectedAccount);
  }


  /**
   * Initialize the list view whenever the pane is created.
   */
  @FXML
  public void initialize() {
    newAdminButton.getStyleClass().add("createButton");
    backButton.getStyleClass().add("backButton");
    deleteButton.getStyleClass().add("deleteButton");
    super.pageControl = adminPageController;
    usernameColumn.setSortable(false);
    nameColumn.setSortable(false);
    PageNav.loading();
    getAdmins();
    adminsTable.setRowFactory( tv -> {
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
    adminsTable.setPlaceholder(new Label("No accounts found"));
    deleteButton.setDisable(true);
    initializeColumns();
    observableAccounts = FXCollections.observableArrayList();
    generateAccounts();
    adminsTable.setItems(observableAccounts);

    ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(visibleAdmins);
    FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
    sortedAccounts = new SortedList<>(filtered);

    updatePageCount();
    updateTableView();

    adminPageController.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
      updateTableView();
    });

    adminsTable.setOnMouseClicked(click -> {
      UserSummary record = adminsTable.getSelectionModel().getSelectedItem();
      if (record != null) {
        selectedAccount = record;
        deleteButton.setDisable(false);
      } else {
        selectedAccount = null;
        deleteButton.setDisable(true);
      }
    });

    nameFilterTextField.textProperty().addListener((observable, oldValue, newValue) ->
            renewAdmins(nameFilterTextField.getText(), usernameFilterTextField.getText()));

    usernameFilterTextField.textProperty().addListener((observable, oldValue, newValue) ->
            renewAdmins(nameFilterTextField.getText(), usernameFilterTextField.getText()));

    schedule = Executors.newScheduledThreadPool(1);
    Runnable runnable = this::refreshAdmins;
    Future<?> refresh = schedule.scheduleAtFixedRate(runnable, 2, 2, TimeUnit.SECONDS);
    cancelTask = () -> refresh.cancel(true);
  }


  /**
   * Refreshes the list of admins if there is a change to the admins on the server
   */
  private void refreshAdmins() {
    String name = nameFilterTextField.getText();
    String username = usernameFilterTextField.getText();

    String address = createAddress(name, username);
    String token = App.getCurrentSession().getToken();

    GetTask getAdminsTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getAdminsTask.setOnSucceeded(event -> {
      List newAdminsList = (ArrayList) getAdminsTask.getValue();
      if (!UserSummary.userSummaryListEquals(visibleAdmins, newAdminsList)) {
        UserSummary selectedAdmin = selectedAccount;
        adminTableUpdate(getAdminsTask, pageControl.getCurrentPageIndex());
        adminsTable.getSelectionModel().select(selectedAdmin);
      }
    });
    new Thread(getAdminsTask).start();
  }


  private void updateAdminPageView(int page) {
    pageControl.setCurrentPageIndex(page);
    pageControl.setPageCount(Math.max(1, (int) Math.ceil((float) sortedAccounts.size() /
        accountsPerPage)));
  }



  private void adminTableUpdate(GetTask getAdminsTask, int currentPageIndex) {
    visibleAdmins.clear();
    List retrievedAdmins = (ArrayList) getAdminsTask.getValue();
    for (Object admin : retrievedAdmins) {
      ObjectMapper objectMapper = new ObjectMapper();
      UserSummary summary = objectMapper.convertValue(admin, UserSummary.class);
      visibleAdmins.add(summary);
    }
    initializeColumns();
    observableAccounts = FXCollections.observableArrayList();
    generateAccounts();
    adminsTable.setItems(observableAccounts);

    ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(visibleAdmins);
    FilteredList<UserSummary> filteredList = new FilteredList<>(userSummaries);
    sortedAccounts = new SortedList<>(filteredList);

    updateAdminPageView(currentPageIndex);
    updateTableView();
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
                    record.getValue().getLastName()).replaceAll("\\s+", " ")));

    // usernameColumn
    usernameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper((
            record.getValue().getUsername())));
  }


  /**
   * Creates an address for the task to be executed on
   * @param name The name the user is searching for
   * @param username The username the user is searching for
   * @return The string address the system should query for the information needed
   */
  private String createAddress(String name, String username) {
    String address = ADDRESSES.GET_ADMINS.getAddress();
    if (!name.equals("") && username.equals("")) {
      address += "?name=" + name;
    } else if (name.equals("") && !username.equals("")) {
      address += "?username=" + username;
    } else if (!name.equals("") && !username.equals("")) {
      address += "?name=" + name + "&username=" + username;
    }
    return address;
  }


  /**
   * Gets the administrators from the server
   */
  private void getAdmins() {
    String address = createAddress("", "");
    String token = App.getCurrentSession().getToken();
    GetTask getAdminsTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getAdminsTask.setOnSucceeded(event -> {
      List adminsList = (ArrayList) getAdminsTask.getValue();
      for (Object admin : adminsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserSummary summary = objectMapper.convertValue(admin, UserSummary.class);
        visibleAdmins.add(summary);
      }
      loadTable();
      PageNav.loaded();
    });
    getAdminsTask.setOnFailed(event -> {
      PageNav.loaded();
      adminsTable.setPlaceholder(new Label("System Error: No Admins Found"));
    });
    new Thread(getAdminsTask).start();
  }


  /**
   * Gets the admins from the system when a search term is entered into the fields
   * @param name The name the user is looking for
   * @param username The username the user is looking for
   */
  private void renewAdmins(String name, String username) {
    visibleAdmins.clear();
    String address = createAddress(name, username);
    String token = App.getCurrentSession().getToken();
    GetTask getAdminsTask = new GetTask(Object.class, ADDRESSES.SERVER.getAddress(), address, token);
    getAdminsTask.setOnSucceeded(event -> {
      List adminsList = (ArrayList) getAdminsTask.getValue();
      for (Object admin : adminsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserSummary summary = objectMapper.convertValue(admin, UserSummary.class);
        visibleAdmins.add(summary);
      }
      selectedAccount = null;
      initializeColumns();
      observableAccounts = FXCollections.observableArrayList();
      generateAccounts();
      adminsTable.setItems(observableAccounts);

      ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(visibleAdmins);
      FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
      sortedAccounts = new SortedList<>(filtered);

      updatePageCount();
      updateTableView();
      PageNav.loaded();
    });
    getAdminsTask.setOnFailed(event -> {
      visibleAdmins.clear();
      PageNav.loaded();

      initializeColumns();
      observableAccounts = FXCollections.observableArrayList();
      generateAccounts();
      adminsTable.setItems(observableAccounts);

      ObservableList<UserSummary> userSummaries = FXCollections.observableArrayList(visibleAdmins);
      FilteredList<UserSummary> filtered = new FilteredList<>(userSummaries);
      sortedAccounts = new SortedList<>(filtered);

      updatePageCount();
      updateTableView();
    });
    new Thread(getAdminsTask).start();
  }
}
