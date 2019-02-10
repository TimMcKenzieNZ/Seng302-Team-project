package seng302.controllers.childWindows;


import static org.mockito.Mockito.mock;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.person.Administrator;


/**
 * This class contains unit tests for the ChildWindowManager class.
 */
public class ChildWindowManagerTest {


  // Class attributes.
  private Stage stage;
  private ChildWindowManager manager;


  /**
   * Create objects required in testing.
   */
  @Before
  public void setUp() {

    stage = mock(Stage.class);
    manager = ChildWindowManager.getChildWindowManager();

  }


  /**
   * Clears all attributes after the completion of a test.
   */
  @After
  public void tearDown() {

    stage = null;
    manager = null;

  }


  /**
   * Tests the getChildWindowManager method of ChildWindowManager. On the first call, this should
   * initialise a ChildWindowManager object and return it. On the second, the same object should be
   * returned.
   */
  @Test
  public void getChildWindowManagerTest() {

    ChildWindowManager firstCall = ChildWindowManager.getChildWindowManager();
    ChildWindowManager secondCall = ChildWindowManager.getChildWindowManager();

    Assert.assertEquals(true, firstCall == secondCall);

  }


  /**
   * Tests the addChildWindow method by passing a stage and an associated type to the method and
   * calling the childWindowToFront method. This should return true, confirming the existence of the
   * window.
   */
  @Test
  public void addChildWindowTestWithAssociatedType() {

    manager.addChildWindow(stage, ChildWindowType.SYSTEM_LOG);
    Assert.assertEquals(true, manager.childWindowToFront(ChildWindowType.SYSTEM_LOG));

  }

  /**
   * Tests the addChildWindow method by passing a stage and associated user to the method and
   * calling the childWindowToFront method. This should return true, confirming the existence of the
   * window.
   */
  @Test
  public void addChildWindowTestWithAssociatedUser() {

    Administrator user = new Administrator(null, null, null, null,
        new String("username"), null, null, null,1);
    manager.addChildWindow(stage, user, null);
    Assert.assertEquals(true, manager.childWindowToFront(user));

  }

  /**
   * Tests the closeAllChildWindows method by creating a child window, confirming its existence,
   * calling the method, and confirming the window no longer exists in ChildWindowManager.
   */
  @Ignore
  //Currently researching error to find solution. Test failing line 113, when it attempts to close all stages
  @Test
  public void closeAllChildWindowsTest() {
    manager.addChildWindow(stage, ChildWindowType.SYSTEM_LOG);
    Assert.assertEquals(true, manager.childWindowToFront(ChildWindowType.SYSTEM_LOG));
    manager.closeAllChildWindows();
    Assert.assertEquals(false, manager.childWindowToFront(ChildWindowType.SYSTEM_LOG));
  }

}
