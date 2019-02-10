package seng302.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.Clinician;
import seng302.services.GetTask;

import java.io.ByteArrayInputStream;


public class MainMenuController {

  // JavaFX attributes referenced in 'MainMenu.fxml'.
  @FXML
  public Label mainMenuTitle;
  @FXML
  public Label bigClinicianLabel;
  @FXML
  public Button viewEditClinicianButton;
  @FXML
  public Button viewAllDonorsButton;
  @FXML
  public Button addNewDonorButton;
  @FXML
  public Button transplantWaitingListButton;
  @FXML
  public Button graphButton;
  @FXML
  public Button logoutButton;
  @FXML
  public Button createClinicianButton;
  @FXML
  ImageView profileImage;

  private static String searchValue = "";

  /**
   * Loads the view/edit clinician page when the 'view/edit clinician' button is pressed in the
   * GUI.
   */
  @FXML
  void clinicianButtonPressed() {
    PageNav.loadNewPage(PageNav.VIEWEDITCLINICIAN);
  }


  /**
   * Changes the GUI pane to the create clinician pane when the "create clinician" button is pressed
   * during runtime.
   */
  @FXML
  private void createClinician() {
    PageNav.isAdministrator = false;
    PageNav.loadNewPage(PageNav.CREATECLINICIAN);
  }


  /**
   * Switches to the list view screen with no search criteria.
   */
  @FXML
  private void viewAllDonorsSelected() {
    searchValue = "";
    PageNav.searchValue = searchValue;
    PageNav.loadNewPage(PageNav.LISTVIEW);
  }


  /**
   * Switches to the add new donor screen.
   */
  @FXML
  private void addNewDonorSelected() {
    CreateUserPaneController.lastScreen = PageNav.MAINMENU;
    PageNav.loadNewPage(PageNav.CREATE);
  }

  /**
   * Logs the user out and returns to the login screen.
   */
  @FXML
  private void logoutSelected() {
    ClinicianProfileController.setClinician(null);
    AccountManager.setCurrentUser(null);
    PageNav.loadNewPage(PageNav.LOGIN);
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
    });
    task.setOnFailed(event -> {
      Image img = new Image("/images/default.jpg");
      profileImage.setImage(img);

    });
    new Thread(task).start();
  }

  /**
   * Resets the GUI whenever the main menu screen is loaded.
   */
  @FXML
  public void initialize() {
    searchValue = "";

    transplantWaitingListButton.getStyleClass().add("actionButton");
    viewAllDonorsButton.getStyleClass().add("actionButton");
    graphButton.getStyleClass().add("actionButton");
    getPhoto(App.getCurrentSession().getUsername());
    logoutButton.getStyleClass().add("backButton");
    createClinicianButton.getStyleClass().add("createButton");
    getClinician(App.getCurrentSession().getUsername());
  }


  public void getClinician(String username) {
    String endpoint = ADDRESSES.GET_CLINICIAN.getAddress() + App.getCurrentSession().getUsername() ;
    GetTask task = new GetTask(Clinician.class, ADDRESSES.SERVER.getAddress(), endpoint, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      Clinician clinician = (Clinician) task.getValue();
      bigClinicianLabel.setText(clinician.getFirstName() + " " + clinician.getLastName());
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get clinician");
    });
    new Thread(task).start();

  }


  /**
   * Switches back to the Donor list
   */
  @FXML
  private void transplantSelected() {
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }

  @FXML
  private void graphSelected() {
    PageNav.loadNewPage(PageNav.GRAPHS);
  }

}
