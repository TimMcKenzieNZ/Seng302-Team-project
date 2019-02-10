package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.DonorReceiver;


/**
 * DonorReceiverStatusComparator is an implementation of comparator used to compare two accounts by
 * their donor/receiver status. It is used to sort accounts.
 */
public class DonorReceiverStatusComparator implements Comparator<DonorReceiver> {

  /**
   * Compares the donor/receiver status of two accounts. The order is: Donor/Receiver greater than
   * Receiver greater than Donor greater than Neither (DESC order) and the reverse for ASC order
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if accountA should come before accountB, 1 if the opposite is true, or 0 if both
   * accountA and accountB are equal.
   */
  @Override
  public int compare(DonorReceiver accountA, DonorReceiver accountB) {

    // This isnt stored as a bool in account, so we store it locally
    Boolean accountAIsDonating = true;
    Boolean accountBIsDonating = true;

    // Check if A is a donor
    if (accountA.getDonorOrganInventory().toString()
        .equals("Organs to donate:\nNo organs to donate\n\n")) {
      accountAIsDonating = false;
    }
    // Check if B is a donor
    if (accountB.getDonorOrganInventory().toString()
        .equals("Organs to donate:\nNo organs to donate\n\n")) {
      accountBIsDonating = false;
    }

    if ((accountA.getReceiver() == accountB.getReceiver()) && (accountAIsDonating
        == accountBIsDonating)) {
      return 0;
    } else if (accountA.getReceiver() && accountAIsDonating) {
      return 1;
    } else if (accountB.getReceiver() && accountBIsDonating) {
      return -1;
    } else if (accountA.getReceiver()) {
      return 1;
    } else if (accountB.getReceiver()) {
      return -1;
    } else if (accountAIsDonating) {
      return 1;
    } else {
      return -1;
    }
  }
}
