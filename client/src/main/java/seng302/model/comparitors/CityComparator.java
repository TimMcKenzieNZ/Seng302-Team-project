package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.DonorReceiver;


/**
 * CityComparator is an implementation of comparator used to compare two accounts by the city they
 * reside in. It is used to sort accounts.
 */
public class CityComparator implements Comparator<DonorReceiver> {

  /**
   * Compares the name of the city two accounts and returns -1 if the first name is alphabetically
   * ahead of the second. 1 is returned if the opposite is true. If both names are alphabetically
   * equal, 0 is returned.
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if accountA should come before accountB, 1 if the opposite is true, or 0 if both
   * accountA and accountB are equal.
   */
  @Override
  public int compare(DonorReceiver accountA, DonorReceiver accountB) {

    // Get cities.
    String accountACity = accountA.getAddressCity();
    String accountBCity = accountB.getAddressCity();

    // Handle null cities.
    if ((accountACity == null) && (accountBCity == null)) {
      return 0;
    } else if (accountACity == null) {
      return 1;
    } else if (accountBCity == null) {
      return -1;
    } else {
      // Return comparison.
      return accountACity.compareToIgnoreCase(accountBCity);
    }
  }
}
