package seng302.controllers;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import seng302.App;
import seng302.model.AccountManager;

/**
 * A listController class that you should inherit from if you intend to use a table that displays a
 * SortedList and must conform to story 30 AC-6
 */
class ListController {

  int accountsPerPage = 16; // The default number of accounts per page is 15
  protected AccountManager accountManager = App.getDatabase();
  Pagination pageControl;
  SortedList sortedAccounts;

  void updatePageCount() {
    pageControl.setCurrentPageIndex(0);
    pageControl.setPageCount(Math.max(1, (int) Math.ceil((float) sortedAccounts.size() /
        accountsPerPage)));
  }

  /**
   * A method to generate the matches message underneath the pagination control based on the type of
   * account passed in
   *
   * @param accountName The name of the account so we can specify between "record and records",
   * "account and accounts", etc.
   * @return The string to display as the message above the pagination control
   */
  String getMatchesMessage(String accountName) {
    int number = sortedAccounts.size();

    if (number == 1) {
      return "\n" +
          number + " matching " + accountName;
    } else {
      int startNumber = pageControl.getCurrentPageIndex() * accountsPerPage + 1;
      int endNumber = (pageControl.getCurrentPageIndex() + 1) * accountsPerPage;

      endNumber = (endNumber < number) ? endNumber : number; // Set the endNumber to the lower value

      startNumber = (endNumber == 0) ? 0
          : startNumber; // Set the start number to 0 if there are no results to display
      return accountName.substring(0, 1).toUpperCase() + accountName.substring(1) + "s "
          + startNumber + "-" + endNumber + "\n" +
          number + " matching " + accountName + "s";
    }
  }

  /**
   * Generates an alert asking the user to select an account with a title specified by the parameter
   * title.
   *
   * @param title The title of the alert.
   */
  void noAccountSelectedAlert(String title) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText("Please select an account.");
    alert.showAndWait();
  }

  /**
   * Generates an alert that specifies that something went wrong when opening the profile
   */
  void errorOpeningProfileAlert() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Error Opening Profile");
    alert.setHeaderText(null);
    alert.setContentText("An error occurred while opening the selected profile.");
    alert.showAndWait();
  }
}
