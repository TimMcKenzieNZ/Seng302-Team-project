package seng302;

import static org.junit.Assert.assertTrue;
import static seng302.model.enums.ADDRESSES.setLocalServer;

import org.junit.Test;
import seng302.model.Marshal;

/**
 * Unit test for simple App.
 */
public class AppTest {

  @Test
  public void testApp() {
    Marshal.setTestImport();
    setLocalServer();
    assertTrue(true);
  }
}
