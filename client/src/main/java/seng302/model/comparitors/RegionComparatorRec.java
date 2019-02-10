package seng302.model.comparitors;

import java.util.Comparator;
import seng302.model.ReceiverRecord;

public class RegionComparatorRec implements Comparator<ReceiverRecord> {


  /**
   * Compares the address region of two accounts and returns -1 if the region of the first account
   * is before the region of the second if ordered alphabetically. 1 is returned if the opposite is
   * true. If both regions are equal, 0 is returned.
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if accountA should come before accountB, 1 if the opposite is true, or 0 if both
   * accountA and accountB are equal.
   */
  @Override
  public int compare(ReceiverRecord accountA, ReceiverRecord accountB) {

    // Convert lower case.
    String regionA = accountA.getRegion().toLowerCase();
    String regionB = accountB.getRegion().toLowerCase();

    // Return comparison.
    return regionA.compareTo(regionB);

  }
}
