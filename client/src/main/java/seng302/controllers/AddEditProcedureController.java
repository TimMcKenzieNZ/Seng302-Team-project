package seng302.controllers;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seng302.model.person.DonorReceiver;
import seng302.model.MedicalProcedure;
import seng302.model.PageNav;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class AddEditProcedureController {

  private static MedicalProcedure medicalProcedure;
  // donorReceiver is the donorReceiver that the medical procedure belongs to.
  private static DonorReceiver donorReceiver;
  // Store whether the medical procedure is being edited (false if new procedure).
  private boolean editing;

  //============================//
  // FXML elements
  //============================//
  @FXML
  private TextArea summary;
  @FXML
  private DatePicker date;
  @FXML
  private CheckBox unknownDateCheck;
  @FXML
  private CheckBox liverCheck;
  @FXML
  private CheckBox kidneyCheck;
  @FXML
  private CheckBox heartCheck;
  @FXML
  private CheckBox lungCheck;
  @FXML
  private CheckBox intestinesCheck;
  @FXML
  private CheckBox corneaCheck;
  @FXML
  private CheckBox boneCheck;
  @FXML
  private CheckBox boneMarrowCheck;
  @FXML
  private CheckBox connectiveTissueCheck;
  @FXML
  private CheckBox pancreasCheck;
  @FXML
  private CheckBox middleEarCheck;
  @FXML
  private CheckBox skinCheck;
  @FXML
  private TextArea description;
  @FXML
  private Button cancelButton;
  @FXML
  private Button doneButton;

  public static void setDonorReceiver(DonorReceiver userAccount) {
    donorReceiver = userAccount;
  }

  public static void setMedicalProcedure(MedicalProcedure userMedicalProcedure) {
    medicalProcedure = userMedicalProcedure;
  }


  /**
   * Handles how page switching occurs, which is dependent on whether or not the current window is a
   * child window.
   */
  private void handlePageSwitching() {
    try {
      loadEditWindow();
    } catch (IOException exception) {
      System.out.println("Error loading the edit window from procedure.");
    }
  }

  /**
   * Switches to the edit profile screen within the given window.
   *
   * @throws IOException When the FXML cannot be retrieved.
   */
  private void loadEditWindow() throws IOException {
    // Set the selected donorReceiver for the profile pane and confirm child.
    EditPaneController.setAccount(donorReceiver);
    EditPaneController.setIsChild(true);
    // reset the medical procedure window
    donorReceiver = null;
    medicalProcedure = null;
    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    StackPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));
    // Create new scene.
    Scene editScene = new Scene(editPane);
    // Retrieve current stage and set scene.
    Stage current = (Stage) cancelButton.getScene().getWindow();
    current.setScene(editScene);
  }

  @FXML
  void cancelButtonPressed(ActionEvent event) {
    handlePageSwitching();
  }

  public void showBadInputMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Bad User Input");
    alert.setContentText(message);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  public void showSuccessMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Success");
    alert.setContentText(String.format(message));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  void createProcedure() {
    // check that summary has less than 100 characters
    // check that summary has at least one character
    //check that description has at least one character
    if (summary.getText().length() > 100) {
      showBadInputMessage("Summary must not have more than 100 characters.");
    } else if (summary.getText().length() < 1) {
      showBadInputMessage("Summary must not be empty.");
    } else if (description.getText().length() < 1) {
      showBadInputMessage("Description must not be empty.");
    } else {
      ArrayList<String> affectedOrgans = new ArrayList<>(findAffectedOrgans());
      String dateString = null;
      if (!unknownDateCheck.isSelected()) {
        try {
          dateString = date.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (NullPointerException e) {
          showBadInputMessage("The date you have entered is invalid");
          return;
        }
      }
      try {
        boolean updated = donorReceiver.addMedicalProcedure(summary.getText(),
            description.getText(), dateString, affectedOrgans);
        if (updated) {
          donorReceiver.extractPastProcedures();
          donorReceiver.extractPendingProcedures();
          handlePageSwitching();
        } else {
          showBadInputMessage("There was an error adding this medical procedure. Please try again.");
        }
      } catch (Exception ex) {
        if (ex.getMessage().equals(MedicalProcedure.procedureDateTooEarlyErrorMessage)) {
          showBadInputMessage(
              "The date of the procedure must not be more than 12 months before the patient's date of birth.");
        } else if (ex.getMessage().equals(DonorReceiver.PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE)) {
          showBadInputMessage("A procedure with the same details already exists for this patient.");
        }
      }
    }
  }

  private void editProcedure() {
    if (summary.getText().length() > 100) {
      showBadInputMessage("Summary must not have more than 100 characters.");
    } else if (summary.getText().length() < 1) {
      showBadInputMessage("Summary must not be empty.");
    } else if (description.getText().length() < 1) {
      showBadInputMessage("Description must not be empty.");
    } else {
      ArrayList<String> affectedOrgans = new ArrayList<>(findAffectedOrgans());
      String dateString = null;
      if (!unknownDateCheck.isSelected()) {
        try {
          dateString = date.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (NullPointerException e) {
          showBadInputMessage("The date you have entered is invalid");
          return;
        }
      }
      LocalDate dateThing;
      if (unknownDateCheck.isSelected()) {
        dateThing = null;
      } else {
        dateThing = date.getValue();
      }
      try {
        donorReceiver.extractPastProcedures();
        donorReceiver.extractPendingProcedures();
        String oldSummary = medicalProcedure.getSummary();
        PageNav.loading();
        MedicalProcedure newMedicalProcedure = new MedicalProcedure(summary.getText(), description.getText(), dateThing, affectedOrgans);
        boolean updated = donorReceiver.updateMedicalProcedure(newMedicalProcedure, oldSummary);
        PageNav.loaded();
        if (updated) {
          handlePageSwitching();
        } else {
          showBadInputMessage("There was an error editing this medical procedure. Please try again.");
        }
      } catch (Exception ex) {
        if (ex.getMessage().equals(MedicalProcedure.procedureDateTooEarlyErrorMessage)) {
          showBadInputMessage(
              "The date of the procedure must not be more than 12 months before the patient's date of birth.");
        } else if (ex.getMessage().equals(DonorReceiver.PROCEDURE_ALREADY_EXISTS_ERROR_MESSAGE)) {
          showBadInputMessage("A procedure with the same details already exists for this patient.");
        }
      }
    }
  }

  private ArrayList<String> findAffectedOrgans() {
    // find affected organs of procedure
    //"Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines", "Cornea",
    //"Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
    ArrayList<String> affectedOrgans = new ArrayList<>();
    if (liverCheck.isSelected()) {
      affectedOrgans.add("Liver");
    }
    if (kidneyCheck.isSelected()) {
      affectedOrgans.add("Kidney");
    }
    if (pancreasCheck.isSelected()) {
      affectedOrgans.add("Pancreas");
    }
    if (heartCheck.isSelected()) {
      affectedOrgans.add("Heart");
    }
    if (lungCheck.isSelected()) {
      affectedOrgans.add("Lung");
    }
    if (intestinesCheck.isSelected()) {
      affectedOrgans.add("Intestines");
    }
    if (corneaCheck.isSelected()) {
      affectedOrgans.add("Cornea");
    }
    if (middleEarCheck.isSelected()) {
      affectedOrgans.add("Middle Ear");
    }
    if (skinCheck.isSelected()) {
      affectedOrgans.add("Skin");
    }
    if (boneCheck.isSelected()) {
      affectedOrgans.add("Bone");
    }
    if (boneMarrowCheck.isSelected()) {
      affectedOrgans.add("Bone Marrow");
    }
    if (connectiveTissueCheck.isSelected()) {
      affectedOrgans.add("Connective Tissue");
    }
    return affectedOrgans;
  }

  @FXML
  void doneButtonPressed(ActionEvent event) {
    if (editing) {
      editProcedure();
    } else {
      createProcedure();
    }
  }

  @FXML
  void unknownDateClicked(ActionEvent event) {
    if (unknownDateCheck.isSelected()) {
      date.setDisable(true);
    } else {
      date.setDisable(false);
    }
  }

  private void populateProcedureData() {
    summary.setText(medicalProcedure.getSummary());
    if (medicalProcedure.getDate() == null) {
      unknownDateCheck.setSelected(true);
      date.setDisable(true);
    } else {
      date.setValue(medicalProcedure.getDate());
    }
    // set checkboxes for
    //"Liver", "Kidney", "Pancreas", "Heart", "Lung", "Intestines", "Cornea",
    //"Middle Ear", "Skin", "Bone", "Bone Marrow", and "Connective Tissue".
    ArrayList<String> affectedOrgans = medicalProcedure.getAffectedOrgans();
    if (affectedOrgans.contains("Liver")) {
      liverCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Kidney")) {
      kidneyCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Pancreas")) {
      pancreasCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Heart")) {
      heartCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Lung")) {
      lungCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Intestines")) {
      intestinesCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Cornea")) {
      corneaCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Middle Ear")) {
      middleEarCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Skin")) {
      skinCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Bone")) {
      boneCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Bone Marrow")) {
      boneMarrowCheck.setSelected(true);
    }
    if (affectedOrgans.contains("Connective Tissue")) {
      connectiveTissueCheck.setSelected(true);
    }
    description.setText(medicalProcedure.getDescription());
  }

  public void initialize() {
    cancelButton.getStyleClass().add("redButton");
    doneButton.getStyleClass().add("primaryButton");
    if (medicalProcedure == null) {
      editing = false;
    } else {
      editing = true;
      populateProcedureData();
    }
  }
}
