package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import seng302.model.person.UserValidator;


/**
 * A class for unit testing the methods of the UserAttributeCollection class.
 */
public class UserAttributeCollectionTest {


  private UserAttributeCollection userAppCol;

  @Before
  public void setUp() {

    userAppCol = new UserAttributeCollection(2.0, 51.0, "AB-", false, "130/80", false, 2.0, "");

  }

  /**
   * testing boundry cases for valid heights.
   */
  @Test
  public void validateHeightTest1() {
    assertTrue(UserValidator.validateHeight(1.0));
  }

  /**
   * testing boundry cases for invalid heights.
   */
  @Test
  public void validateHeightTest2() {
    assertFalse(UserValidator.validateHeight(-0.01));
  }

  /**
   * testing boundry cases for valid heights.
   */
  @Test
  public void validateHeightTest3() {
    assertTrue(UserValidator.validateHeight(3.99));
  }

  /**
   * testing boundry cases for invalid heights.
   */
  @Test
  public void validateHeightTest4() {
    assertFalse(UserValidator.validateHeight(4.0001));
  }


  /**
   * testing boundry cases for valid weights.
   */
  @Test
  public void validateWeightTest1() {
    assertTrue(UserValidator.validateWeight(50.0));
  }

  /**
   * testing boundry cases for invalid weights.
   */
  @Test
  public void validateWeightTest2() {
    assertFalse(UserValidator.validateWeight(-0.01));
  }

  /**
   * testing success cases for validateBloodType.
   */
  @Test
  public void validateBloodTypeTest1() {
    assertTrue(UserValidator.validateBloodType("A-"));
  }

  /**
   * testing success cases for validateBloodType.
   */
  @Test
  public void validateBloodTypeTest2() {
    assertTrue(UserValidator.validateBloodType("AB+"));
  }

  /**
   * testing failure cases for validateBloodType when string is in lower case.
   */
  @Test
  public void validateBloodTypeTest3() {
    assertFalse(UserValidator.validateBloodType("o+"));
  }

  /**
   * testing failure cases for validateBloodType - wrong letter.
   */
  @Test
  public void validateBloodTypeTest4() {
    assertFalse(UserValidator.validateBloodType("F-"));
  }

  /**
   * testing failure cases for validateBloodType - mis-ordered letters.
   */
  @Test
  public void validateBloodTypeTest5() {
    assertFalse(UserValidator.validateBloodType("BA+"));
  }

  /**
   * testing failure cases for validateBloodType - bad string.
   */
  @Test
  public void validateBloodTypeTest6() {
    assertFalse(UserValidator.validateBloodType("123+"));
  }

  /**
   * testing failure cases for validateBloodType - duplicated letters.
   */
  @Test
  public void validateBloodTypeTest7() {
    assertFalse(UserValidator.validateBloodType("OO+"));
  }

  /**
   * testing failure cases for validateBloodType - empty string.
   */
  @Test
  public void validateBloodTypeTest8() {
    assertFalse(UserValidator.validateBloodType(""));
  }


  /**
   * testing failure cases for validateBloodType - duplicated symbols.
   */
  @Test
  public void validateBloodTypeTest9() {
    assertFalse(UserValidator.validateBloodType("A++"));
  }

  /**
   * testing failure cases for validateBloodType - mis-ordered symbols.
   */
  @Test
  public void validateBloodTypeTest10() {
    assertFalse(UserValidator.validateBloodType("-B"));
  }

  /**
   * testing success cases for validateAlphanumericString - Address 1.
   */
  @Test
  public void validateAlphanumericStringTest1() {
    assertTrue(UserValidator.validateAlphanumericString(true, "666, Elm-Street'", 0, 100));
  }

  /**
   * testing boundry success cases for validateAlphanumericString - min string length.
   */
  @Test
  public void validateAlphanumericStringTest2() {
    assertTrue(UserValidator.validateAlphanumericString(true, "1", 0, 100));
  }

  /**
   * testing boundry success cases for validateAlphanumericString - max string length.
   */
  @Test
  public void validateAlphanumericStringTest3() {
    assertTrue(
        UserValidator.validateAlphanumericString(true, "fishyfishyfishyfishyfishyfishyfishy" +
            "fishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishy", 0, 100));
  }

  /**
   * testing success cases for validateAlphanumericString - alphabetic string.
   */
  @Test
  public void validateAlphanumericStringTest4() {
    assertTrue(UserValidator.validateAlphanumericString(false, "ten Moe's place", 0, 100));
  }

  /**
   * testing success cases for validateAlphanumericString - alphabetic country code of length = 2.
   */
  @Test
  public void validateAlphanumericStringTest5() {
    assertTrue(UserValidator.validateAlphanumericString(false, "nZ", 2, 2));
  }


  /**
   * testing failure cases for validateAlphanumericString - non alphabetic string.
   */
  @Test
  public void validateAlphanumericStringTest6() {
    assertFalse(UserValidator.validateAlphanumericString(false, "abc123", 0, 100));
  }


  /**
   * testing failure cases for validateAlphanumericString - minLength is greater than maxLength.
   */
  @Test
  public void validateAlphanumericStringTest7() {
    assertFalse(UserValidator.validateAlphanumericString(false, "abc123", 9, 8));
  }

  /**
   * testing failure cases for validateAlphanumericString - invalid characters.
   */
  @Test
  public void validateAlphanumericStringTest8() {
    assertFalse(UserValidator.validateAlphanumericString(true, "abc123*&^", 0, 100));
  }

  /**
   * testing failure cases for validateAlphanumericString - invalid length (empty string).
   */
  @Test
  public void validateAlphanumericStringTest9() {
    assertFalse(UserValidator.validateAlphanumericString(true, "", 1, 100));
  }

  /**
   * testing success cases for validateAlphanumericString - valid length (empty string).
   */
  @Test
  public void validateAlphanumericStringTest10() {
    assertTrue(UserValidator.validateAlphanumericString(true, "", 0, 100));
  }

  /**
   * testing failure cases for validateAlphanumericString - invalid boundry length (too long).
   */
  @Test
  public void validateAlphanumericStringTest11() {
    assertFalse(UserValidator.validateAlphanumericString(true, "fishyfishy1", 1, 10));
  }

  /**
   * test for success case when all the User attributes print out as expected.
   */
  @Test
  public void printAllAttributesTest() {
    String string = "User Attributes:\n\tHeight: 2.0m\n\tWeight: 51.0kg\n\tBlood Type: AB-\n\tBlood Pressure: 130/80\n\tSmoker: false\n\tAlcohol Consumption: 2.00 standard drinks per week\n";
    System.out.println(string);
    assertEquals(string, userAppCol.toString());
  }


}


