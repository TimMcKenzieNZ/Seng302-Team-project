package seng302.model.import_export_strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import seng302.model.Marshal;
import seng302.model.person.User;

public class JSONImportTest {

  private UserImport importer;

  @Before
  public void setUp() {
    importer = new JSONImport();
  }

  @Test
  public void importGoodDonorReceiver() {
    try {
      Collection<User> user = importer.importer("donor/receiver", null,
          new File("../testData/jsonData/donorReceivers/LLL9898.json"));
      assertEquals(1, user.size());
    } catch (IOException e) {
      fail("could not find file");
    }
  }

  @Test
  public void importDonorReceiverWithBadField() {
    try {
      Collection<User> donorReceivers = importer.importer("donor/receiver", null,
          new File("../testData/jsonData/donorReceivers/IIIJ89.json"));
      assertEquals(0, donorReceivers.size());
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("Something went wrong");
    }
  }

  @Test
  public void importGoodClinicians() {
    try {
      Collection<User> clinician = importer
          .importer("clinician", null, new File("../testData/jsonData/clinicians/101.json"));
      assertEquals(1, clinician.size());
    } catch (IOException e) {
      fail("could not find file");
    }
  }

  @Test
  public void importBadClinicians() {
    try {
      Collection<User> clinicians = importer
          .importer("clinician", null, new File("../testData/jsonData/clinicians/102.json"));
      assertEquals(Marshal.importException.size(), 1);
      assertEquals(0, clinicians.size());
    } catch (IOException e) {
      fail("Something went wrong");
    }
  }

  @Test
  public void importGoodAdmins() {
    try {
      Collection<User> admins = importer
          .importer("admins", null, new File("../testData/jsonData/admins/Test.json"));
      assertEquals(1, admins.size());
    } catch (IOException e) {
      fail("could not find file");
    }
  }

  @Test
  public void importBadAdmins() {
    try {
      Collection<User> admins = importer
          .importer("admins", null, new File("../testData/jsonData/admins/Fail.json"));
      assertEquals(0, admins.size());
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("Something went wrong");
    }
  }
}