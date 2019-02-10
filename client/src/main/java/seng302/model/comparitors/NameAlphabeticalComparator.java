package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.DonorReceiver;


/**
 * Simplified version of NameComparator which sorts names alphabetically.
 */
public class NameAlphabeticalComparator implements Comparator<DonorReceiver> {


  /**
   * Compares the name of two accounts and returns -1 if the first name is alphabetically ahead of
   * the second. 1 is returned if the opposite is true. If both names are alphabetically equal, 0 is
   * returned.
   *
   * @param donorReceiverA The first account to be examined.
   * @param donorReceiverB The second account to be examined.
   * @return -1 if donorReceiverA should come before donorReceiverB, 1 if the opposite is true, or 0
   * if both donorReceiverA and donorReceiverB are equal.
   */
  @Override
  public int compare(DonorReceiver donorReceiverA, DonorReceiver donorReceiverB) {
    String accountAName;
    String accountBName;
    try {
      accountAName = (donorReceiverA.getFirstName() + donorReceiverA.getMiddleName()
          + donorReceiverA.getLastName()).replaceAll("\\s", " ").toLowerCase();
    } catch (NullPointerException e) {
      accountAName = "";
    }
    try {
      accountBName = (donorReceiverB.getFirstName() + donorReceiverB.getMiddleName()
          + donorReceiverB.getLastName()).replaceAll("\\s", " ").toLowerCase();
    } catch (NullPointerException e) {
      accountBName = "";
    }
    // Return comparison.
    return accountAName.compareTo(accountBName);

  }


}
