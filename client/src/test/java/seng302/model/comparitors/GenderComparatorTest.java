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
 * This file contains a series of tests for GenderComparator.
 */
public class GenderComparatorTest {


  // Class attributes.
  List<DonorReceiver> list;
  DonorReceiver donorReceiverA;
  DonorReceiver donorReceiverB;
  DonorReceiver donorReceiverC;
  DonorReceiver donorReceiverD;


  /**
   * Initialise variables before each test.
   */
  @Before
  public void setup() {

    donorReceiverA = new DonorReceiver("Francis", "Henry Dillon", "Ward", LocalDate.of(1851, 3, 13),
        "PNZ0017");
    donorReceiverA.setGender('M');

    donorReceiverB = new DonorReceiver("Jennifer", "Mary", "Shipley", LocalDate.of(1952, 2, 4),
        "PNZ0036");
    donorReceiverB.setGender('F');

    donorReceiverC = new DonorReceiver("Walter", "", "Nash", LocalDate.of(1882, 2, 12), "PNZ0027");
    donorReceiverC.setGender('U');

    donorReceiverD = new DonorReceiver("Sidney", "George", "Holland", LocalDate.of(1893, 10, 18),
        "PNZ0025");
    donorReceiverD.setGender('O');

    list = Arrays.asList(donorReceiverA, donorReceiverB, donorReceiverC, donorReceiverD);

  }


  /**
   * Set all variables to null.
   */
  @After
  public void tearDown() {

    list = null;
    donorReceiverA = null;
    donorReceiverB = null;
    donorReceiverC = null;
    donorReceiverD = null;

  }


  /**
   * Attempts to sort a list where every element is in the incorrect position by gender. The correct
   * order is donorReceiverB, donorReceiverA, donorReceiverD, donorReceiverC.
   */
  @Test
  public void compareTestWithIncorrectOrder() {

    list.sort(new GenderComparator());

    Assert.assertEquals(donorReceiverB, list.get(0));
    Assert.assertEquals(donorReceiverA, list.get(1));
    Assert.assertEquals(donorReceiverD, list.get(2));
    Assert.assertEquals(donorReceiverC, list.get(3));

  }


  /**
   * Attempts to sort a list of accounts which is already in the correct order by gender. The order
   * should not change.
   */
  @Test
  public void compareTestWithCorrectOrder() {

    // Place list elements in correct order.
    list = Arrays.asList(donorReceiverB, donorReceiverA, donorReceiverD, donorReceiverC);

    // Sort and evaluate.
    list.sort(new GenderComparator());

    Assert.assertEquals(donorReceiverB, list.get(0));
    Assert.assertEquals(donorReceiverA, list.get(1));
    Assert.assertEquals(donorReceiverD, list.get(2));
    Assert.assertEquals(donorReceiverC, list.get(3));

  }


}
