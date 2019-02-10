package seng302.model;

import static seng302.model.person.DonorReceiver.formatDateTimeToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class Medications {

  private ArrayList<String> previousMedications;
  private ArrayList<String> currentMedications;
  private ArrayList<String> medicationLog;

  public ArrayList<String> getCurrentMedications() {
    return currentMedications;
  }

  public ArrayList<String> getPreviousMedications() {
    return previousMedications;
  }

  public ArrayList<String> getMedicationLog() {
    return medicationLog;
  }

  /**
   * Constructer for the medications class. Initialises three empty arrays.
   */
  public Medications() {
    previousMedications = new ArrayList<>();
    currentMedications = new ArrayList<>();
    medicationLog = new ArrayList<>();
  }

  @JsonCreator
  public Medications(@JsonProperty("currentMedications") ArrayList<String> CurrentMedications,
      @JsonProperty("previousMedications") ArrayList<String> PreviousMedications,
      @JsonProperty("medicationLog") ArrayList<String> MedicationLog) {
    currentMedications = CurrentMedications;
    previousMedications = PreviousMedications;
    medicationLog = MedicationLog;
  }

  /**
   * Adds a medication to the chosen list (Alphabetically) if that medication does not already exist
   * in the list
   *
   * @param list The arrayList to add the medication to
   * @param medication The medication to be added to the list
   * @param previousPosition Where this medication was previously in the application
   */
  public void addMedicationAlphabetically(ArrayList<String> list, String medication,
      String previousPosition) {
    if (checkDuplication(list, medication)) {
      list.add(medication);
      if (list == previousMedications) {
        if (previousPosition == null) {
          addMedicationLog(medication, "Previous");
        } else {
          addMedicationChangeLog(medication, "Current Medications", "Previous Medications");
          currentMedications.remove(medication);
        }

      } else {
        if (previousPosition == null) {
          addMedicationLog(medication, "Current");
        } else {
          addMedicationChangeLog(medication, "Previous Medications", "Current Medications");
          previousMedications.remove(medication);
        }
      }
      Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    }
  }

  /**
   * Checks if the medicine being added is already in the list.
   *
   * @param list The arrayList to be checked
   * @param medication the medication to be checked within the arrayList
   * @return true or false depending on whether the medication is in the list or not.
   */
  public boolean checkDuplication(ArrayList<String> list, String medication) {
    int i = list.indexOf(medication);
    if (i == -1) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Removes the given medication from the given list if it exists.
   *
   * @param list The arrayList to remove the medication from
   * @param medication The medication to be removed from the list
   */
  public void removeMedication(ArrayList<String> list, String medication) {
    list.remove(medication);
    Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    if (list == previousMedications) {
      removeMedicationLog(medication, "Previous Medications");
    } else {
      removeMedicationLog(medication, "Current Medications");
    }
  }

  /**
   * Creates and stores a medication removed log in the medicationChangeLog
   *
   * @param medicine The medicine that has been removed
   * @param listType Which list the medicine was removed from
   */
  public void removeMedicationLog(String medicine, String listType) {
    String log = String.format("\t'%s' removed from '%s'. ", medicine, listType);
    String time = formatDateTimeToString(LocalDateTime.now());
    log += String.format("Change made at: %s\n", time);
    medicationLog.add(log);
  }

  /**
   * Creates and stores a medication created log in the medicationChangeLog
   *
   * @param medicine The medication that has been created
   * @param listType Which the list the medication was added to
   */
  public void addMedicationLog(String medicine, String listType) {
    String log = String.format("\t'%s' added to %s Medications. ", medicine, listType);
    String time = formatDateTimeToString(LocalDateTime.now());
    log += String.format("Change made at: %s\n", time);
    medicationLog.add(log);
  }

  /**
   * Creates and stores a log for future reference in the medicationChangeLog
   *
   * @param medicine The medication that has been moved
   * @param oldValue The list the medication was previously in
   * @param newValue The list the medication is being moved to
   */
  public void addMedicationChangeLog(String medicine, String oldValue, String newValue) {
    String log = String.format("\t'%s' changed from '%s' to '%s'. ", medicine, oldValue, newValue);
    String time = formatDateTimeToString(LocalDateTime.now());
    log += String.format("Change made at: %s\n", time);
    medicationLog.add(log);
  }

  /**
   * Removes the most recent change from the medication log. Created for Undo functionality.
   */
  public void removeMedicationChangeLog() {
    medicationLog.remove(medicationLog.size() - 1);
  }
}
