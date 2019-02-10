package seng302.model.comparitors;

import java.util.Comparator;
import seng302.model.person.Clinician;

public class ClinicianNameComparator implements Comparator<Clinician> {

  /**
   * The search value against which account names are compared.
   */
  private static String searchValue = "";


  /**
   * Sets the String object against which account names are compared.
   *
   * @param newSearchValue The new search value as a String object.
   */
  public static void setSearchValue(String newSearchValue) {

    // Remove all whitespace from search value and convert to lower case.
    searchValue = newSearchValue.replaceAll("\\s", "").toLowerCase();
    ;

  }


  /**
   * Compares the full name of two accounts against the search criteria. The account name with the
   * longest common substring will be given priority. If both accounts have an equally long common
   * substring, then alphabetic order will be used for prioritisation.
   *
   * @param clinicianA The first account to be examined.
   * @param clinicianB The second account to be examined.
   * @return -1 if 'a' has higher priority than 'b', 1 if the opposite is true, and 0 if both have
   * equal priority.
   */
  @Override
  public int compare(Clinician clinicianA, Clinician clinicianB) {

    // Retrieve names, remove whitespace, and convert to lower case.
    String accountAName = (clinicianA.getFirstName() + clinicianA.getMiddleName()
        + clinicianA.getLastName()).replaceAll("\\s", " ").toLowerCase();
    String accountBName = (clinicianB.getFirstName() + clinicianB.getMiddleName()
        + clinicianB.getLastName()).replaceAll("\\s", " ").toLowerCase();

    // Calculate length of longest common substring for each account.
    int accountALCSLength = longestCommonSubstringLength(accountAName,
        searchValue);
    int accountBLCSLength = longestCommonSubstringLength(accountBName,
        searchValue);

    if (accountALCSLength > accountBLCSLength) {

      // Name of account A is closer to search value.
      return -1;

    } else if (accountALCSLength < accountBLCSLength) {

      // Name of account B is closer to search value.
      return 1;

    } else {

      // Both are equal. Use lexicographic order.
      return accountAName.compareTo(accountBName);

    }

  }


  /**
   * Returns the length of the longest substring shared by two String objects. Adapted from
   * 'EditDistance.java' by Robert Bruce.
   *
   * @param stringA The first String to be examined.
   * @param stringB The second String to be examined.
   * @return The length of the longest common substring of stringA and stringB.
   */
  private int longestCommonSubstringLength(String stringA, String stringB) {

    int table[][] = new int[stringA.length()][stringB.length()];
    int longestCommonSubstringLength = 0;

    // Visit each row in the table.
    for (int row = 0; row < stringA.length(); row++) {

      // Visit each column in the table.
      for (int column = 0; column < stringB.length(); column++) {

        if (stringA.charAt(row) == stringB.charAt(column)) {

          if (row == 0 || column == 0) {

            table[row][column] = 1;

          } else {

            table[row][column] = table[row - 1][column - 1] + 1;

          }

          if (longestCommonSubstringLength < table[row][column]) {

            longestCommonSubstringLength = table[row][column];

          }
        }
      }

    }

    return longestCommonSubstringLength;

  }
}
