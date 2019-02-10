package seng302.model.comparitors;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.DonorReceiver;


/**
 * This file contains a series of JUnit tests for the NameComparator class.
 */
public class NameComparatorTest {


  // Class attributes.
  List<DonorReceiver> list;
  DonorReceiver donorReceiverA;
  DonorReceiver donorReceiverB;


  /**
   * Initialise variables before each test.
   */
  @Before
  public void setup() {

    donorReceiverA = new DonorReceiver("Richard", "John", "Seddon", LocalDate.of(1845, 6, 22),
        "PNZ0015");
    donorReceiverB = new DonorReceiver("Robert", "", "Stout", LocalDate.of(1844, 9, 28), "PNZ0013");

    list = Arrays.asList(donorReceiverA, donorReceiverB);

  }


  /**
   * Set all variables to null.
   */
  @After
  public void tearDown() {

    list = null;
    donorReceiverA = null;
    donorReceiverB = null;

  }


  /**
   * Tests NameComparator with an example where donorReceiverA has a greater longest common
   * substring with the search value than donorReceiverB.
   */
  @Test
  public void compareTestWhereAHasGreaterCommonSubstringLength() {

    // Set search value for name comparator.
    NameComparator.setSearchValue("hn sed");

    reverseOrder(); // Reverse default list order.
    list.sort(new NameComparator());
    Assert.assertEquals(donorReceiverA, list.get(0));

  }


  /**
   * Tests NameComparator with an example where donorReceiverB has a greater longest common
   * substring with the search value than donorReceiverA.
   */
  @Test
  public void compareTestWhereBHasGreaterCommonSubstringLength() {

    // Set search value for name comparator.
    NameComparator.setSearchValue("ert sto");

    list.sort(new NameComparator());
    Assert.assertEquals(donorReceiverB, list.get(0));

  }


  /**
   * Tests NameComparator with an example where donorReceiverA and donorReceiverB share the same
   * common substring length with the search value, but A is ordered first alphabetically.
   */
  @Test
  public void compareTestWhereAIsAlphabeticallyLower() {

    // Set value for name comparator.
    NameComparator.setSearchValue("o");

    reverseOrder();
    list.sort(new NameComparator());
    Assert.assertEquals(donorReceiverA, list.get(0));

  }


  /**
   * Tests NameComparator with an example where donorReceiverA and donorReceiverB share the same
   * common substring length with the search value, but B is ordered first alphabetically.
   */
  @Test
  public void compareTestWhereBIsAlphabeticallyLower() {

    NameComparator.setSearchValue("o");
    donorReceiverB.setFirstName("Arthur"); // Change first name.

    list.sort(new NameComparator());
    Assert.assertEquals(donorReceiverB, list.get(0));

  }


  /**
   * Tests NameComparator with two equal accounts. Practically, this only examines if an exception
   * will occur.
   */
  @Test
  public void compareTestWhereBothAccountsAreEqual() {

    NameComparator.setSearchValue("o");
    list = Arrays.asList(donorReceiverA, donorReceiverA);

    list.sort(new NameComparator());
    Assert.assertEquals(donorReceiverA, list.get(0));

  }


  /**
   * Recreates list with donorReceiverB in position 0. This method is used to ensure that a
   * re-ordering does occur when donorReceiverA should be in position 0 after a sort operation on
   * list.
   */
  private void reverseOrder() {

    list = Arrays.asList(donorReceiverB, donorReceiverA);

  }


}
