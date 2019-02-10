package seng302.model;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * BooleanExtensionTest contains a series of JUnit tests for the BooleanExtension class.
 */
public class BooleanExtensionTest {


  // Class attributes.
  Boolean nullBoolean;
  Boolean trueBoolean;
  Boolean falseBoolean;


  /**
   * Initialise boolean objects before test.
   */
  @Before
  public void before() {

    nullBoolean = null;
    trueBoolean = true;
    falseBoolean = false;

  }


  /**
   * Destroy boolean objects after test.
   */
  @After
  public void after() {

    nullBoolean = null;
    trueBoolean = null;
    falseBoolean = null;

  }


  /**
   * Test getBoolean with a null Boolean object. Should return null.
   */
  @Test
  public void getBooleanTestWithNullBoolean() {

    Boolean result = BooleanExtension.getBoolean(nullBoolean);
    Assert.assertEquals(false, result);

  }


  /**
   * Test getBoolean with a true Boolean object. Should return true.
   */
  @Test
  public void getBooleanTestWithTrueBoolean() {

    Boolean result = BooleanExtension.getBoolean(trueBoolean);
    Assert.assertEquals(true, result);

  }


  /**
   * Test getBoolean with a false object. Should return false.
   */
  @Test
  public void getBooleanWithFalseBoolean() {

    Boolean result = BooleanExtension.getBoolean(falseBoolean);
    Assert.assertEquals(false, result);

  }


}
