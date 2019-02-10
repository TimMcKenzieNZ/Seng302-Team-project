package seng302.model.comparitors;

import java.util.Comparator;
import seng302.model.ReceiverRecord;

public class RecNameAlphabeticalComparator implements Comparator<ReceiverRecord> {


  /**
   * Compares the name of two accounts and returns -1 if the first name is alphabetically ahead of
   * the second. 1 is returned if the opposite is true. If both names are alphabetically equal, 0 is
   * returned.
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if accountA should come before accountB, 1 if the opposite is true, or 0 if both
   * accountA and accountB are equal.
   */
  @Override
  public int compare(ReceiverRecord accountA, ReceiverRecord accountB) {

    // Convert lower case.
    String accountAName = accountA.getFullName().replaceAll("\\s", " ").toLowerCase();
    String accountBName = accountB.getFullName().replaceAll("\\s", " ").toLowerCase();

    // Return comparison.
    return accountAName.compareTo(accountBName);

  }
}
