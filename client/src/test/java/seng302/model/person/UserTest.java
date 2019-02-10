package seng302.model.person;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * UserTest contains a series of unit tests for the User class.
 */
public class UserTest {


  // Class attributes.
  private Administrator baseUser;
  private Administrator identicalUser;
  private Administrator differentUser;
  private String wrongObjectType;
  private String nullObjectType;


  /**
   * Creates object instances which will be used in JUnit tests. Note, Administrator instances are
   * used here as User is abstract and cannot be instantiated. This will be run before each test.
   */
  @Before
  public void setUp() {

    baseUser = new Administrator(null, null, null, null, new String("username"), null, null, null,1);
    identicalUser = new Administrator(null, null, null, null, new String("username"), null, null,
        null,1);
    differentUser = new Administrator(null, null, null, null, "differentUsername", null, null,
        null,1);
    wrongObjectType = "";

  }


  /**
   * Clears all instances after the completion of a test.
   */
  @After
  public void tearDown() {

    baseUser = null;
    identicalUser = null;
    differentUser = null;

  }


  /**
   * Tests the equals method of the User class with two objects that share the same user name. The
   * equals method should return false.
   */
  @Test
  public void identicalEqualsTest() {

    Assert.assertEquals(true, baseUser.equals(identicalUser));

  }


  /**
   * Tests the equals method of the User class with two objects which do not share the same user
   * name. The equals method should return false.
   */
  @Test
  public void differentEqualsTest() {

    Assert.assertEquals(false, baseUser.equals(differentUser));

  }


  /**
   * Tests the equals method of the User class with two objects. The object passed to the equals
   * method is of the wrong type, and the method should return a false value accordingly.
   */
  @Test
  public void wrongObjectTypeEqualsTest() {

    Assert.assertEquals(false, baseUser.equals(wrongObjectType));

  }


  /**
   * Tests the equals method of the User class with two objects. The object passed to the equals
   * method is null, and the method should return false.
   */
  @Test
  public void nullObjectTypeEqualsTest() {

    Assert.assertEquals(false, baseUser.equals(nullObjectType));

  }


  /**
   * Tests the hashCode method of the User class with two objects considered equal. The value
   * returned by both should be identical.
   */
  @Test
  public void hashCodeOfEqualObjectsTest() {

    int baseUserHashCode = baseUser.hashCode();
    int identicalUserHashCode = identicalUser.hashCode();
    Assert.assertEquals(true, baseUserHashCode == identicalUserHashCode);

  }

}
