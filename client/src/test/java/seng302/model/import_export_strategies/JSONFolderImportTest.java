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

public class JSONFolderImportTest {

  private UserImport importer;

  @Before
  public void setUp() throws Exception {
    importer = new JSONFolderImport();
  }


  @Test
  public void importGoodDonorReceivers() {
    try {
      Collection<User> users = importer
          .importer("donor/receiver", null, new File("../testData/jsonData/goodDonorReceivers"));
      assertEquals(users.size(), 1);
    } catch (IOException e) {
      fail("file not found");
    }
  }

  @Test
  public void importBadDonorReceivers() {
    try {
      Collection<User> users = importer
          .importer("donor/receiver", null, new File("../testData/jsonData/badDonorReceivers"));
      assertEquals(users.size(), 1);
      assertEquals(Marshal.importException.size(), 2);
    } catch (IOException e) {
      fail("file not found");
    }
  }

  @Test
  public void importGoodClinicians() {
    try {
      Collection<User> users = importer
          .importer("clinician", null, new File("../testData/jsonData/goodClinicians"));
      assertEquals(users.size(), 2);
    } catch (IOException e) {
      fail("file not found");
    }
  }

  @Test
  public void importBadClinicians() {
    try {
      Collection<User> users = importer
          .importer("clinician", null, new File("../testData/jsonData/badClinicians"));
      assertEquals(users.size(), 1);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("file not found");
    }
  }

  @Test
  public void importGoodAdmins() {
    try {
      Collection<User> users = importer
          .importer("admins", null, new File("../testData/jsonData/goodAdmins"));
      assertEquals(users.size(), 2);
    } catch (IOException e) {
      fail("file not found");
    }
  }

  @Test
  public void importBadAdmins() {
    try {
      Collection<User> users = importer
          .importer("admins", null, new File("../testData/jsonData/badAdmins"));
      assertEquals(users.size(), 1);
      assertEquals(Marshal.importException.size(), 1);
    } catch (IOException e) {
      fail("file not found");
    }
  }
}