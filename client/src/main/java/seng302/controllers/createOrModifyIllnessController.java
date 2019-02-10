package seng302.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.CommandStack;
import seng302.model.Illness;
import seng302.model.PageNav;
import seng302.model.UndoableManager;
import seng302.model.enums.ADDRESSES;
import seng302.model.patches.IllnessPatch;
import seng302.model.person.DonorReceiver;
import seng302.services.PatchTask;
import seng302.services.PostTask;

/**
 * Controller class for handling all GUI events relating to the create/update Illness fxml pane.
 */
public class createOrModifyIllnessController {


  /**
   * An Illness object of the current Illness object being created or updated.
   */
  private static Illness illness;


  /**
   * An DonorReceiver object of the current donor being edited.
   */
  private static DonorReceiver donor;

  /**
   * Sets the donor attribute to the given DonorReceiver.
   *
   * @param donorReceiver An DonorReceiver object to be edited.
   */
  public static void setDonor(DonorReceiver donorReceiver) {
    donor = donorReceiver;
  }

  /**
   * Sets the illness attribute to the given Illness.
   *
   * @param newIllness An Illness object to be edited.
   */
  public static void setIllness(Illness newIllness) {
    illness = newIllness;
  }

  /**
   * A boolean flag which determines whether an Illness object is being created or an existing one
   * is being edited.
   */
  private boolean editing;
  private boolean undoRedoChoiceBox = false;
  private boolean undoRedoDatePicker = false;
  private boolean undoRedoTextField = false;

  private UndoableManager undoableManager = App.getUndoableManager();

  // The following attributes correspond to FXML GUI artifacts.
  @FXML
  private TextField nameOfDiagnoses;

  @FXML
  private CheckBox chronicCheckBox;

  @FXML
  private CheckBox curedCheckBox;

  @FXML
  private DatePicker dateOfDiagnoses;


  @FXML
  private Button Cancel;


  @FXML
  private Button Done;


  @FXML
  /**
   * Closes the create/update Illness window in the GUI when the 'cancel' button is pressed and loads the ProfileView page.
   */
  void cancelButtonPressed(ActionEvent event) {
    illness = null;
    donor.populateIllnessLists();
    EditPaneController.setAccount(donor);
    goBackToEditPane();
  }


  /**
   * Loads the parent edit pane controller for the current edited donorReceiver.
   */
  public void goBackToEditPane() {
    try {
      // Set child status.
      EditPaneController.setIsChild(true);
      EditPaneController.setAccount(donor);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader();
      StackPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));

      // Create new scene.
      Scene editScene = new Scene(editPane);

      // Retrieve current stage and set scene.
      Stage current = (Stage) nameOfDiagnoses.getScene().getWindow();
      current.setScene(editScene);
    } catch (IOException exception) {
      System.out.println("Error loading edit window from illness");
    }
    //PageNav.loadNewPage(EDIT);
  }



  @FXML
  /**
   * Calls either the editIllness() function if an existing Illness is being edited. Otherwise calls the createIllness() function.
   */
  void doneButtonPressed() {
    if (editing) {
      editIllnessRemote();
    } else {
      createIllnessRemote();
    }
  }

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
   * Calls the undo event when editing an illness
   */
  @FXML
  private void undo() {
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
   * Calls the redo event when editing an illness
   */
  @FXML
  private void redo() {
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


  /**
   * Initializes the create/update Illness GUI with default text values in each field. If the
   * illness attribute is not null, then these values will be those of the Illness object selected
   * in the medical History(diseases) Illness list for editing.
   */
  public void initialize() {
    Cancel.getStyleClass().add("redButton");
    Done.getStyleClass().add("primaryButton");
    if (illness == null) {
      illness = new Illness("", false, false);
      editing = false;
    } else {
      editing = true;
    }
    nameOfDiagnoses.setText(illness.getName());
    dateOfDiagnoses.setValue(illness.getDate());
    chronicCheckBox.setSelected(illness.isChronic());
    curedCheckBox.setSelected(illness.isCured());
    if (illness.isChronic()) {
      curedCheckBox.setDisable(true);
    } else if (illness.isCured()) {
      chronicCheckBox.setDisable(true);
    }

    // Undo and Redo components for illness changes
    chronicCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        if (newValue == true) {
          curedCheckBox.setSelected(false);
          curedCheckBox.setDisable(true);
        } else {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Deregister Disease");
          alert.setHeaderText("The currently selected disease is longer chronic anymore");
          alert.setContentText("Are you ok with this?");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
              curedCheckBox.setDisable(false);
            } else {
              undo();
            }
          }
        }
      }
    });
    curedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        if (newValue == true) {
          chronicCheckBox.setSelected(false);
          chronicCheckBox.setDisable(true);
        } else {
          chronicCheckBox.setDisable(false);
        }
      }
    });
    nameOfDiagnoses.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (!undoRedoTextField) {
          undoableManager.createTextFieldChange(nameOfDiagnoses, oldValue, newValue);
        }
      }
    });
    dateOfDiagnoses.valueProperty().addListener(new ChangeListener<LocalDate>() {
      @Override
      public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
          LocalDate newValue) {
        if (!undoRedoDatePicker) {
          undoableManager.createDatePickerChange(dateOfDiagnoses, oldValue, newValue);
        }
        if (dateOfDiagnoses.getValue().isAfter(LocalDate.now())) {
          dateOfDiagnoses.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        } else {
          dateOfDiagnoses.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        }
      }
    });
  }


  private void editIllnessRemote() {
    EditPaneController.CURRENT_VERSION++;
    IllnessPatch illnessPatch = new IllnessPatch(dateOfDiagnoses.getValue().toString(), nameOfDiagnoses.getText(), curedCheckBox.isSelected(), chronicCheckBox.isSelected(), String.valueOf(EditPaneController.CURRENT_VERSION));
    Map<String, String> customheaders = new HashMap<String, String>();
    customheaders.put("illness", illness.getName());

    PatchTask task = new PatchTask(DonorReceiver.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_ILLNESSES.getAddress(), EditPaneController.staticAccount.getUserName()), illnessPatch, App.getCurrentSession().getToken(), customheaders);
    task.setOnSucceeded(event -> {
      illness = null;
      goBackToEditPane();
    });
    task.setOnFailed(event -> {
      if(task.getException().getMessage().contains("Updated")){
        illness = null;
        donor.populateIllnessLists();
        EditPaneController.setAccount(donor);
        goBackToEditPane();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();

  }

  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Save failed");
    alert.setContentText("Whoops, looks like something went wrong. Please try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  private void createIllnessRemote() {
    Illness illness = new Illness(nameOfDiagnoses.getText(), dateOfDiagnoses.getValue(), curedCheckBox.isSelected(), chronicCheckBox.isSelected());
    PostTask task = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.USER_ILLNESSES.getAddress(), EditPaneController.staticAccount.getUserName()), illness, App.getCurrentSession().getToken());
    task.setOnSucceeded(event -> {
      goBackToEditPane();
    });
    task.setOnFailed(event -> {
      if(task.getException().getMessage().contains("Added")){
        goBackToEditPane();
      } else {
        showBadSaveError();
      }
    });
    new Thread(task).start();
  }
}
