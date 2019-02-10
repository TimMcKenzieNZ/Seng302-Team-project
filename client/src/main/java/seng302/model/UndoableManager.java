package seng302.model;


import java.time.LocalDate;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * Manage and Create Undoables to be added to the undo/redo stack
 */
public class UndoableManager {

  private CommandStack commandStack;

  /**
   * Create an Undoable and add to stack of the checkbox edited
   *
   * @param event CheckBox edited in GUI event
   */
  public void createDonationUndoable(ActionEvent event) {

    Undoable organDonation = new Undoable() {
      @Override
      public void undo() {
        CheckBox checkBox = (CheckBox) event.getSource();
        checkBox.setSelected(!checkBox.isSelected());
      }

      @Override
      public void redo() {
        CheckBox checkBox = (CheckBox) event.getSource();
        checkBox.setSelected(!checkBox.isSelected());
      }

      @Override
      public String getUndoRedoName() {
        return "Organ Donation";
      }
    };
    commandStack.addCommand(organDonation);
  }

  /**
   * Create choicebox undoable and add to command stack
   *
   * @param choiceBox choiceBox edited
   * @param oldValue Original index of choiceBox
   * @param newValue New index of choiceBox
   */
  public void createChoiceBoxUndoable(ChoiceBox choiceBox, Number oldValue, Number newValue) {
    Undoable choiceBoxUndoable = new Undoable() {
      @Override
      public void undo() {
        choiceBox.getSelectionModel().select((int) oldValue);
      }

      @Override
      public void redo() {
        choiceBox.getSelectionModel().select((int) newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Choice Box";
      }
    };
    commandStack.addCommand(choiceBoxUndoable);
  }

  /**
   * Create datepicker undoable and add to command stack
   *
   * @param datePicker datePicker edited
   * @param oldValue Original Date of the datepicker
   * @param newValue New Date of the datepicker
   */
  public void createDatePickerChange(DatePicker datePicker, LocalDate oldValue,
      LocalDate newValue) {
    Undoable datePickerUndoable = new Undoable() {
      @Override
      public void undo() {
        datePicker.valueProperty().setValue(oldValue);
      }

      @Override
      public void redo() {
        datePicker.valueProperty().setValue(newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Date Picker";
      }
    };
    commandStack.addCommand(datePickerUndoable);
  }


  /**
   * Create textfield undoable and add to the command stack
   *
   * @param textField TextField edited
   * @param oldValue Original value of TextField
   * @param newValue New value of TextField
   */
  public void createTextFieldChange(TextField textField, String oldValue, String newValue) {
    Undoable textFieldUndoable = new Undoable() {
      @Override
      public void undo() {
        textField.textProperty().setValue(oldValue);
      }

      @Override
      public void redo() {
        textField.textProperty().setValue(newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Text Field";
      }
    };
    commandStack.addCommand(textFieldUndoable);
  }

  /**
   * Create ComboBox undoable and add to the command stack
   *
   * @param comboBox ComboBox edited
   * @param oldValue Original value of TextField
   * @param newValue New value of TextField
   */
  public void createComboBoxChange(ComboBox comboBox, Number oldValue, Number newValue) {
    Undoable comboBoxUndoable = new Undoable() {
      @Override
      public void undo() {
        comboBox.getSelectionModel().select((int) oldValue);
      }

      @Override
      public void redo() {
        comboBox.getSelectionModel().select((int) newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Combo Box";
      }
    };
    commandStack.addCommand(comboBoxUndoable);
  }

  /**
   * Create a Medication Added Undoable and add to the command stack
   *
   * @param listView ListView with new medication
   * @param medication New medication added
   */
  public void createMedicationAddChange(ListView listView, String medication) {
    Undoable createMedicationUndoable = new Undoable() {
      @Override
      public void undo() {
        listView.getItems().remove(medication);
        listView.refresh();
      }

      @Override
      public void redo() {
        listView.getItems().add(medication);
        listView.refresh();
      }

      @Override
      public String getUndoRedoName() {
        return "Medication Add";
      }
    };
    commandStack.addCommand(createMedicationUndoable);
  }

  /**
   * Create a Medication Removed Undoable and add to the command stack
   *
   * @param listView ListView where the medication was removed from
   * @param medication Medication that was removed
   * @param isCurrent Whether the medication is current
   * @param removedMedications All medications currently removed
   */
  public void createMedicationRemoveChange(ListView listView, String medication, Boolean isCurrent,
      ArrayList removedMedications) {
    Undoable removeMedicationUndoable = new Undoable() {
      @Override
      public void undo() {
        listView.getItems().add(medication);
        if (isCurrent) {
          removedMedications.remove("C" + medication);
        } else {
          removedMedications.remove("P" + medication);
        }
      }

      @Override
      public void redo() {
        listView.getItems().remove(medication);
        if (isCurrent) {
          removedMedications.add("C" + medication);
        } else {
          removedMedications.add("P" + medication);
        }
      }

      @Override
      public String getUndoRedoName() {
        return "Medication Remove";
      }
    };
    commandStack.addCommand(removeMedicationUndoable);
  }


  /**
   * Create a Medication moved from current to previous Undoable
   *
   * @param currentMedications ListView with current medications
   * @param previousMedications ListView with previous medications
   * @param medication Medication that was moved
   */
  public void createMedicationCurrentToPreviousChange(ListView currentMedications,
      ListView previousMedications, String medication) {
    Undoable currentToPreviousUndoable = new Undoable() {
      @Override
      public void undo() {
        previousMedications.getItems().remove(medication);
        currentMedications.getItems().add(medication);
        currentMedications.refresh();
        previousMedications.refresh();
      }

      @Override
      public void redo() {
        previousMedications.getItems().add(medication);
        currentMedications.getItems().remove(medication);
        currentMedications.refresh();
        previousMedications.refresh();
      }

      @Override
      public String getUndoRedoName() {
        return "Medication current to previous";
      }
    };
    commandStack.addCommand(currentToPreviousUndoable);
  }


  /**
   * Create a Medication moved from previous to current Undoable
   *
   * @param currentMedications ListView of current medications
   * @param previousMedications ListView of previous medications
   * @param medication Medication that was moved
   */
  public void createMedicationPreviousToCurrentChange(ListView currentMedications,
      ListView previousMedications, String medication) {
    Undoable previousToCurrentUndoable = new Undoable() {
      @Override
      public void undo() {
        previousMedications.getItems().add(medication);
        currentMedications.getItems().remove(medication);
        currentMedications.refresh();
        previousMedications.refresh();

      }

      @Override
      public void redo() {
        previousMedications.getItems().remove(medication);
        currentMedications.getItems().add(medication);
        currentMedications.refresh();
        previousMedications.refresh();
      }

      @Override
      public String getUndoRedoName() {
        return "Medication previous to current";
      }
    };
    commandStack.addCommand(previousToCurrentUndoable);
  }

  public void createLabelUndoable(Label label, String oldValue, String newValue) {
    Undoable labelUndoable = new Undoable() {
      @Override
      public void undo() {
        label.setText(oldValue);
      }

      @Override
      public void redo() {
        label.setText(newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Label";
      }
    };
    commandStack.addCommand(labelUndoable);
  }

  public void createTextAreaUndoable(TextArea area, String oldValue, String newValue) {
    Undoable textAreaUndoable = new Undoable() {
      @Override
      public void undo() {
        area.setText(oldValue);
      }

      @Override
      public void redo() {
        area.setText(newValue);
      }

      @Override
      public String getUndoRedoName() {
        return "Text Area";
      }
    };
    commandStack.addCommand(textAreaUndoable);
  }

  public CommandStack getCommandStack() {
    return commandStack;
  }

  public UndoableManager() {
    this.commandStack = new CommandStack();
  }
}
