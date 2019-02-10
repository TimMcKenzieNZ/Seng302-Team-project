package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.DonorReceiver;


/**
 * GenderComparator is an implementation of comparator used to compare two accounts by gender. It is
 * used to sort accounts.
 */
public class GenderComparator implements Comparator<DonorReceiver> {


  /**
   * Compares the gender of two accounts and returns 0 if they are equal. A value less than 0 is
   * returned if the first account should be ordered before the second, or greater than 0 if the
   * reverse is true.
   *
   * @param donorReceiverA The first account to be examined.
   * @param donorReceiverB The second account to be examined.
   * @return 0 if both accounts have equal ordering, less than 0 if donorReceiverA should come
   * before donorReceiverB, and greater than 0 if donorReceiverB should come before donorReceiverA.
   */
  @Override
  public int compare(DonorReceiver donorReceiverA, DonorReceiver donorReceiverB) {

    return donorReceiverA.getGender() - donorReceiverB.getGender();

  }


}
