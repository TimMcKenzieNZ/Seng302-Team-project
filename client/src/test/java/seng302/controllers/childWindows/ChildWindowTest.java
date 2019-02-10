package seng302.controllers.childWindows;


import static org.mockito.Mockito.mock;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.Administrator;


/**
 * This class contains unit tests for the ChildWindow class.
 */
public class ChildWindowTest {


  // Class attributes.
  private Stage stage;
  private ChildWindow typeWindow;
  private ChildWindow userWindow;


  /**
   * Create objects required in testing.
   */
  @Before
  public void setUp() {

    stage = mock(Stage.class);
    typeWindow = new ChildWindow(stage, ChildWindowType.CONSOLE);
    userWindow = new ChildWindow(stage, new Administrator(null, null, null,
        null, new String("username"), null, null, null,1));

  }


  /**
   * Clears all attributes after the completion of a test.
   */
  @After
  public void tearDown() {

    stage = null;
    typeWindow = null;
    userWindow = null;

  }


  /**
   * Tests the userIsIdentical method of ChildWindow with an identical user. The result of the
   * method should be true.
   */
  @Test
  public void userIsIdenticalTestWithIdenticalUser() {

    boolean result = userWindow.UserIsIdentical(new Administrator(null,
        null, null, null, new String("username"), null, null, null,1));
    Assert.assertEquals(true, result);

  }


  /**
   * Tests the userIsIdentical method of ChildWindow with a null parameter. The result of the method
   * should be false.
   */
  @Test
  public void userIsIdenticalTestWithNullUser() {

    boolean result = userWindow.UserIsIdentical(null);
    Assert.assertEquals(false, result);

  }


  /**
   * Tests the userIsIdentical method of ChildWindow for an object which does not have an associated
   * user (e.g. one with a type). The method should return false.
   */
  @Test
  public void userIsIdenticalTestForObjectWithNoType() {

    boolean result = typeWindow.UserIsIdentical(new Administrator(null,
        null, null, null, new String("username"), null, null, null,1));
    Assert.assertEquals(false, result);

  }


  /**
   * Tests the typeIsIdentical method in ChildWindow with an identical type. The result of the
   * method should be true.
   */
  @Test
  public void typeIsIdenticalTestWithIdenticalType() {

    boolean result = typeWindow.TypeIsIdentical(ChildWindowType.CONSOLE);
    Assert.assertEquals(true, result);


  }


  /**
   * Tests the typeIsIdentical method in ChildWindow with a null type. The result should be false.
   */
  @Test
  public void typeIsIdenticalTestWithNullType() {

    boolean result = typeWindow.TypeIsIdentical(null);
    Assert.assertEquals(false, result);


  }


  /**
   * Tests the typeIsIdentical method of ChildWindow for an object which does not have an associated
   * type (e.g. one with a user). The method should return false.
   */
  @Test
  public void typeIsIdenticalTestForObjectWithNoType() {

    boolean result = userWindow.TypeIsIdentical(ChildWindowType.SYSTEM_LOG);
    Assert.assertEquals(false, result);

  }

}
