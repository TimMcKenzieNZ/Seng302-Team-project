package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.DonorReceiver;


/**
 * RegionComparator is a standalone comparator used to compare the region of two accounts. It is
 * used to sort accounts by region.
 */
public class RegionComparator implements Comparator<DonorReceiver> {


  /**
   * Compares the address region of two accounts and returns -1 if the region of the first account
   * is before the region of the second if ordered alphabetically. 1 is returned if the opposite is
   * true. If both regions are equal, 0 is returned.
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if donorReceiverA should come before donorReceiverB, 1 if the opposite is true, or 0
   * if both donorReceiverA and donorReceiverB are equal.
   */
  @Override
  public int compare(DonorReceiver accountA, DonorReceiver accountB) {

    // Get cities.
    String accountARegion = accountA.getAddressRegion();
    String accountBRegion = accountB.getAddressRegion();

    // Handle null regions.
    if ((accountARegion == null) && (accountBRegion == null)) {
      return 0;
    } else if (accountARegion == null) {
      return 1;
    } else if (accountBRegion == null) {
      return -1;
    } else {
      // Return comparison.
      return accountARegion.compareToIgnoreCase(accountBRegion);
    }
  }
}
