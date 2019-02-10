package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.Test;

public class MedicalProcedureTest {

  @Test
  public void successCreateProcedureWithDate() {
    MedicalProcedure appendectomy;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/04/2018", dob, affectedOrgans);
    } catch (Exception ex) {
      throw new Error("expected not to throw", ex);
    }
    assertNotNull(appendectomy);
  }

  @Test
  public void successCreateProcedureNoDate() {
    MedicalProcedure appendectomy;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix", null,
          dob, affectedOrgans);
    } catch (Exception ex) {
      throw new Error("expected not to throw", ex);
    }
    assertNotNull(appendectomy);
    assertNull(appendectomy.getDate());
  }

  @Test
  public void failCreateProcedureNonIntegerDate() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "12.1/2/45", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureBadFormatDate() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "26-04-2018", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureInvalidDate() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "29/02/2018", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureNoDOB() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/02/2018", null, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.nullDOBErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureTooEarly() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "25/05/1996", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.procedureDateTooEarlyErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureIncorrectOrganName() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      affectedOrgans.add("Haert");
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/04/2020", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.invalidOrgansErrorMessage))) {
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void failCreateProcedureExtraOrgan() {
    MedicalProcedure appendectomy = null;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      affectedOrgans.add("Brain");
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/04/2020", dob, affectedOrgans);
    } catch (Exception ex) {
      if (!(ex.getMessage().equals(MedicalProcedure.invalidOrgansErrorMessage))) {
        System.out.println(ex.getMessage());
        throw new Error("expected not to throw", ex);
      }
    }
    assertNull(appendectomy);
  }

  @Test
  public void successSetAffectedOrgans() {
    MedicalProcedure appendectomy;
    try {
      ArrayList<String> affectedOrgans = new ArrayList<>();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/04/2020", dob, affectedOrgans);
      affectedOrgans.add("Heart");
      affectedOrgans.add("Lung");
      affectedOrgans.add("Skin");
      appendectomy.setAffectedOrgans(affectedOrgans);
    } catch (Exception ex) {
      throw new Error("expected not to throw", ex);
    }
    assertTrue(appendectomy.getAffectedOrgans().contains("Heart"));
    assertTrue(appendectomy.getAffectedOrgans().contains("Lung"));
    assertTrue(appendectomy.getAffectedOrgans().contains("Skin"));
  }

  @Test
  public void gettersAndSettersTest() {
    MedicalProcedure appendectomy;
    try {
      ArrayList<String> affectedOrgans = MedicalProcedure.getOrganNames();
      LocalDate dob = LocalDate.of(1997, 5, 26);
      appendectomy = new MedicalProcedure("Appendectomy", "Surgical removal of the appendix",
          "18/04/2020", dob, affectedOrgans);

      assertEquals("Surgical removal of the appendix", appendectomy.getDescription());
      appendectomy.setDescription("The new description");
      assertEquals("The new description", appendectomy.getDescription());

      assertEquals("Appendectomy", appendectomy.getSummary());
      appendectomy.setSummary("Heart Surgery");
      assertEquals("Heart Surgery", appendectomy.getSummary());
    } catch (Exception ex) {
      throw new Error("expected not to throw", ex);
    }
  }
}
