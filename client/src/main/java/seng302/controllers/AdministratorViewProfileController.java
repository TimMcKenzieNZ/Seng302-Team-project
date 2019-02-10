package seng302.controllers;

import javafx.fxml.FXML;
import seng302.App;
import seng302.model.PageNav;
import seng302.model.enums.ADDRESSES;
import seng302.model.person.Administrator;
import seng302.services.GetTask;


/**
 * AdministratorViewProfileController is an extension of the AdministratorMainMenuAreaController
 * class. It provides means of displaying and editing administrators other than the currently
 * logged in administrator using functionality shared with its parent.
 */
public class AdministratorViewProfileController extends AdministratorMainMenuAreaController {


  // Class Attributes
  private static Administrator selectedAdmin;
  private Administrator selectedAdminInstance;



  /**
   * Initialisation method called by JavaFX once injection is complete. Overrides parent method of
   * the same name, and stores a copy of the current selected admin.
   */
  @FXML
  @Override
  public void initialize() {

    selectedAdminInstance = selectedAdmin;
    PageNav.loading();
    editSwitchModeToView();
    usernameTextField.setDisable(true);
    getAdmin(true);
  }



  /**
   * Switches the editable text fields of the administrator to display in the GUI and hides the view
   * labels for these attributes from view
   */
  @Override
  public void viewSwitchModeToEdit() {
    isEditing = true;
    givenNameTextField.setVisible(true);
    otherNameTextField.setVisible(true);
    lastNameTextField.setVisible(true);
    usernameTextField.setVisible(true);
    passwordField.setVisible(true);
    confirmPasswordField.setVisible(true);
    confirmPasswordLabel.setVisible(true);
    passwordLabel.setVisible(true);
    cancelButton.setVisible(true);
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    givenNameLabel.setVisible(false);
    otherNameLabel.setVisible(false);
    lastNameLabel.setVisible(false);
    usernameLabel.setVisible(false);
    editButton.setText("Done");
    editButton.getStyleClass().add("primaryButton");
    cancelButton.getStyleClass().add("redButton");
    informationLabel.setVisible(false);
    if(!App.getCurrentSession().getUsername().equals(selectedAdminInstance.getUserName())) {
      passwordField.setDisable(true);
      confirmPasswordField.setDisable(true);
    }
    clearInformationLabel();

    //Remove all error labeling
    errorLabel.setText("");
    givenNameTextField.setStyle(VALID_STYLE);
    otherNameTextField.setStyle(VALID_STYLE);
    lastNameTextField.setStyle(VALID_STYLE);
    usernameTextField.setStyle(VALID_STYLE);
    passwordField.setStyle(VALID_STYLE);
    confirmPasswordField.setStyle(VALID_STYLE);


  }

  /**
   * Retrieves the administrator account associated with this AdministratorViewProfileController
   * instance. The account is taken from the server.
   * @param setup If true, the setup method will be called after an admin is retrieved.
   */
  @Override
  protected void getAdmin(boolean setup){
    String endpoint = ADDRESSES.GET_ADMIN.getAddress() + selectedAdminInstance.getUserName();
    GetTask task = new GetTask(Administrator.class, ADDRESSES.SERVER.getAddress(), endpoint, session.getToken());
    task.setOnSucceeded(event -> {
      admin = (Administrator) task.getValue();
      if (setup) {
        setup();
      }
    });
    task.setOnFailed(event -> {
      throw new IllegalArgumentException("Failed to get admin");
    });
    new Thread(task).start();

  }


  /**
   * Sets the selected admin.
   * @param admin The new selected admin.
   */
  public static void setSelectedAdmin(Administrator admin) {

    selectedAdmin = admin;

  }


}
