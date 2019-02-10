package seng302.model;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * StringExtensionTest contains a series of tests for the StringExtension class.
 */
public class StringExtensionTest {


  // Class attributes.
  private String nullString;
  private String stringA;
  private String stringB;


  /**
   * Initialise class attributes before each test.
   */
  @Before
  public void before() {

    nullString = null;
    stringA = "Honshu";
    stringB = "Hokkaido";

  }


  /**
   * Destroy class attributes after each test.
   */
  @After
  public void after() {

    nullString = null;
    stringA = null;
    stringB = null;

  }


  /**
   * Tests the nullCompare method with two null String objects. This should return true, signifying
   * equality.
   */
  @Test
  public void nullCompareTestWithTwoNullStrings() {

    boolean result = StringExtension.nullCompare(nullString, nullString);
    Assert.assertEquals(true, result);

  }


  /**
   * Tests the nullCompare method with a null String as the first argument, and a non-null String as
   * the second. This should return false.
   */
  @Test
  public void nullCompareTestWithStringANull() {

    boolean result = StringExtension.nullCompare(nullString, stringB);
    Assert.assertEquals(false, result);

  }


  /**
   * Tests the nullCompare method with a nullString as the second argument, and a non-null String as
   * the first. This should return false.
   */
  @Test
  public void nullCompareTestWithStringBNull() {

    boolean result = StringExtension.nullCompare(stringA, nullString);
    Assert.assertEquals(false, result);

  }


  /**
   * Tests the nullCompare method with the same String object as both arguments. This should return
   * true.
   */
  @Test
  public void nullCompareTestWithTwoIdenticalStrings() {

    boolean result = StringExtension.nullCompare(stringA, stringA);
    Assert.assertEquals(true, result);

  }


  /**
   * Tests the nullCompare method with two distinct Strings as arguments. This should return false.
   */
  @Test
  public void nullCompareTestWithTwoDistinctStrings() {

    boolean result = StringExtension.nullCompare(stringA, stringB);
    Assert.assertEquals(false, result);

  }

}
