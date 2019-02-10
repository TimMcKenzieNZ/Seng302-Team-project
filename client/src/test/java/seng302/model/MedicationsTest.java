package seng302.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MedicationsTest {

  private Medications meds;

  @Before
  public void setUp() {
    meds = new Medications();
    meds.addMedicationAlphabetically(meds.getCurrentMedications(), "Panadol", "New Medication");
    meds.addMedicationAlphabetically(meds.getCurrentMedications(), "Antibiotics", "New Medication");
    meds.addMedicationAlphabetically(meds.getCurrentMedications(), "Insulin", "New Medication");
  }

  /**
   * Testing whether the medications added in @before have been added correctly
   */
  @Test
  public void addMedicationAlphabeticallyTest() {
    assertEquals(3, meds.getCurrentMedications().size());
  }

  /**
   * Testing if a duplicate medication is attempted to be added, then the transaction fails
   */
  @Test
  public void checkDuplicationTest() {
    assertEquals(3, meds.getCurrentMedications().size());
    meds.addMedicationAlphabetically(meds.getCurrentMedications(), "Panadol", "New Medication");
    assertEquals(3, meds.getCurrentMedications().size());
  }

  /**
   * Removes the given medication from the given list
   */
  @Test
  public void removeMedicationTest() {
    meds.removeMedication(meds.getCurrentMedications(), "Panadol");
    assertEquals(2, meds.getCurrentMedications().size());
  }

  /**
   * Adds a new log to the medication Change log, tests for the change in size.
   */
  @Test
  public void addMedicationChangeLogTest() {
    meds.addMedicationChangeLog("Panadol", "current", "previous");
    assertEquals(4, meds.getMedicationLog().size());
  }

  /**
   * Tests whether or not the a log is removed from the medicationLog
   */
  @Test
  public void removeMedicationChangeLogTest() {
    meds.addMedicationChangeLog("Panadol", "current", "previous");

    assertEquals(4, meds.getMedicationLog().size());
    meds.removeMedicationChangeLog();
    assertEquals(3, meds.getMedicationLog().size());
  }

}
