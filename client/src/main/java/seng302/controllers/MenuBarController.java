package seng302.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;

import java.io.IOException;
import java.util.HashMap;

public class MenuBarController {

  private AccountManager db = App.getDatabase();


  /**
   * The status bar.
   */
  @FXML
  private AnchorPane statusBar;

  private final static String directory = "/src/main/resources";

  private HashMap<String, Runnable> helpCommands = new HashMap<>();

  @FXML
  public void initialize() {
    helpCommands.put("About", this::showAboutDialog);
    helpCommands.put("/LoginPane.fxml", this::loginHelpSelected);
    helpCommands.put("/MainMenu.fxml", this::menuHelpSelected);
    helpCommands.put("/createUserPane.fxml", this::createHelpSelected);
    helpCommands.put("/ClinicianProfileView.fxml", this::searchHelpSelected);
    helpCommands.put("/viewProfilePane.fxml", this::viewHelpSelected);
    helpCommands.put("/editPane.fxml", this::editHelpSelected);
    helpCommands.put("/DonorListView.fxml", this::listViewHelp);
    helpCommands.put("/clinicianCreation.fxml", this::clinicianCreationHelp);
    helpCommands.put("/clinicianCreationChildPane.fxml", this::clinicianCreationHelp);
    helpCommands.put("/transplantWaitingListPane.fxml", this::transplantWaitingHelp);
    helpCommands.put("/createOrModifyIllnessPane.fxml", this::createOrModifyIllnessHelp);
    helpCommands.put("/addEditProcedure.fxml", this::procedureHelp);
    helpCommands.put("/administratorMainMenu.fxml", this::adminMainHelp);
    helpCommands.put("/cliniciansList.fxml", this::clinicianListHelp);
    helpCommands.put("/adminsList.fxml", this::adminListHelp);
    helpCommands.put("/systemLog.fxml", this::systemLogHelp);
    helpCommands.put("/administratorCreation.fxml", this::adminCreateHelp);
    helpCommands.put("/administratorViewProfile.fxml", this::adminViewHelp);
    helpCommands.put("/cliTab.fxml", this::cliHelp);
  }

  /**
   * Creates a help pop up for profile list view
   */
  private void listViewHelp() {
    createInformationDialog("List View Help",
        "The list view screen displays all the donor profile accounts. " +
            "This view allows you to search the profiles and filter by age. " +
            "These profiles can also be sorted by each of their attributes. " +
            "When double clicking an account you will be taken to the profile. You can also hover an account to see what organs they are receiving or donating. Deleting an account is as simple as selecting it and clicking the 'Delete' button. "
            +
            "\n\nYou can create a new donor profile by selecting the 'Create Donor' button in the bottom left corner and reach the Transplant Waiting List by selecting the button to the left");
  }

  /**
   * Creates a help pop up for clinician creation
   */
  private void clinicianCreationHelp() {
    createInformationDialog("Create Clinician Help", "Creating a clinician is simple. " +
        "Simply enter all the personal details of the clinician and select done.");
  }

  /**
   * Creates a help pop for the transplant waiting list
   */
  private void transplantWaitingHelp() {
    createInformationDialog("Transplant Waiting List Help",
        "The transplant waiting list shows who needs what organs. " +
            "This view shows the date the organ request was added to the waiting list, the region and the person needing the donation. "
            +
            "You can double click the account to gain more information on the user");
  }

  /**
   * Creates a help pop up for modifying illness
   */
  private void createOrModifyIllnessHelp() {
    createInformationDialog("Illness Help",
        "In the create illness view you can create an illness that a person might have. " +
            "An illness can either be cured or chronic, but not both.");
  }

  /**
   * Creates a help pop up for procedures
   */
  private void procedureHelp() {
    createInformationDialog("Procedure Help",
        "Procedures are used to show when someone has had a procedure performed on them. " +
            "You can show what was performed and what organs were affected as well as the date of the procedure.");
  }

  /**
   * Creates a help pop up on the admin main menu
   */
  private void adminMainHelp() {
    createInformationDialog("Admin Main Menu Help", "Welcome to the administrator main menu. " +
        "There are a number of actions that an admin can perform.\n\n" +
        "Command Line\nThis is a command line interface where you can perform tasks such as updating or creating accounts on the fly. "
        +
        "This is very useful when you need to perform changes to accounts in a short amount of time. You can type in 'help all' to get started\n\n"
        +
        "System Log\nThe system log contains all the information about what has been performed by who in the system. This includes information such as account editing and "
        +
        "when accounts were created.\n\n" +
        "Import Data\nThis is used to import data from JSON or CSV files.\n\n" +
        "View Accounts\nEach type of account has their own view. This includes admins, clinicians and donors. You can create these accounts in each respective view or in the command line interface.");
  }

  /**
   * Creates a help pop up in the clinician list view
   */
  private void clinicianListHelp() {
    createInformationDialog("Clinician List Help",
        "In the clinician view you can view and edit clinicians. This can be done by selecting the account and clicking 'Open Profile'");
  }

  /**
   * Creates a help pop up in the admin list view
   */
  private void adminListHelp() {
    createInformationDialog("Admin List Help",
        "In the admin list view you can view and edit an administrator. This can be done by selecting the account and pressing the 'Open Profile' button");
  }

  /**
   * Creates a help pop up when viewing the system log
   */
  private void systemLogHelp() {
    createInformationDialog("System Log Help",
        "The system log contains all the information about what has been performed by who in the system. This includes information such as account editing and "
            +
            "when accounts were created.");
  }

  /**
   * Creates a help pop up when creating an admin account
   */
  private void adminCreateHelp() {
    createInformationDialog("Admin Creation Help",
        "To create an administrator you need to simply fill out the necessary details and select done.");
  }

  /**
   * Creates a help pop up when viewing all admins
   */
  private void adminViewHelp() {
    createInformationDialog("Admin View Help", "");
  }

  /**
   * Creates a help pop up when viewing th command line interface
   */
  private void cliHelp() {
    createInformationDialog("Command Line Interface Help", "");
  }

  @FXML
  private void showAboutDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About");
    alert.setHeaderText(
        "Sapioculture, the favourite New Zealand organ donation registration application.");
    alert.setContentText(String.format(
        "Sapioculture is a Java application for easily registering as a New Zealand organ donor and"
            +
            " viewing information about other donors.\n\nSapioculture is brought to you by Team AB+:"
            +
            "\nRobert Bruce" +
            "\nAlan Brook" +
            "\nQuentin McKenzie" +
            "\nTimothy McKenzie" +
            "\nRebecka Cox" +
            "\nLucy Turner" +
            "\nImas Neupane" +
            "\nCameron Auld" +
            "\nBenjamin Schmidt"

    ));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  /**
   * swaps out the current page with the new page.
   *
   * @param page the new page to be displayed
   * @throws IOException io exception
   */
  public void setNewPage(Node page) throws IOException {

    statusBar = StatusBarController.injectNode(statusBar, page);


  }


  /**
   * Shows login help in an alert.
   */
  private void loginHelpSelected() {

    createInformationDialog("Login", "The login screen is where you can " +
        "login to the application.\n\nIf you have created an " +
        "account, all you have to do is enter your NHI number into " +
        "field shown and click the 'login' button. You will be taken" +
        "to the main menu screen.\n\nIf you still need to create an " +
        "account, click the 'create new user button'. This will take " +
        "you to the create account screen.");

  }


  /**
   * Shows main menu help in an alert.
   */
  private void menuHelpSelected() {

    createInformationDialog("Main Menu", "The main menu screen is " +
        "presented after you login. If you wish to logout, a button " +
        "at the top right corner of the screen will return you to " +
        "the login screen.\n\nThe search area the screen helps you " +
        "find existing accounts. You can either search by NHI number " +
        "or name by selecting the corresponding radio button. Once " +
        "you have entered some text into the search field, clicking " +
        "the search button will take you to the search screen. " +
        "If you do not enter anything into the field, then you will " +
        "search for all donors.\n\nThe 'View All Donors' button will " +
        "search for all accounts.\n\nThe 'Add New Donor' button will " +
        "take you to the create account screen where you add a new " +
        "donor to the application.");

  }

  /**
   * Shows create account help in an alert.
   */
  private void createHelpSelected() {

    createInformationDialog("Create DonorReceiver", "The create account screen " +
        "is where you enter all of the information required to add a " +
        "donor to the database. This includes a National Health " +
        "Index number, a given name, a last name, and a date of " +
        "birth. The NHI number must be unique.\n\nClicking the " +
        "'Done' button will add the account to the application. " +
        "Please note that it will not be stored permanently until " +
        "you save. This is accomplished by selecting 'File' in the " +
        "menu bar and then 'Save'.\n\nThe 'Back' button will return " +
        "you to the previous screen.");

  }

  /**
   * Shows search help in alert.
   */
  private void searchHelpSelected() {

    createInformationDialog("Search", "A clinician can search for a " +
        "donor profile. From the clinician’s main menu, clicking " +
        "‘Search’ or ‘View All Donors’ will transition the " +
        "application to the search screen. This presents a search " +
        "field, a table, a page controller, and a set of buttons. " +
        "The number of profiles displayed in the table is limited " +
        "to 30 per page. The page controller, immediately below the " +
        "table, allows you to select a page.\n\nEntering text into " +
        "the search field will limit the number of profiles " +
        "displayed. If the name of a profile contains the text " +
        "entered in the search field, it will be shown.\n\nProfiles " +
        "within the table can be sorted by name, age, gender, or " +
        "region. This is accomplished by clicking the appropriate " +
        "column header. Clicking several times will loop through " +
        "default, ascending, and descending sort.\n\nThe buttons at " +
        "the bottom of the screen can be used to view, edit, or " +
        "delete a profile selected in the table. The back button " +
        "will return you to the clinician’s main menu.\n\nIf you wish " +
        "to open a profile in a seperate window, double click on a " +
        "profile in the table. Multiple windows can be opened at once.");

  }

  private void viewHelpSelected() {

    createInformationDialog("View", "The view screen is " +
        "where you can see all of your attributes" +
        "\n\nThis is a read-only view, which means you " +
        "cannot change any attributes. If you wish to edit any " +
        "information, click the 'Edit' button");

  }

  private void editHelpSelected() {

    createInformationDialog("Edit", "The edit screen is " +
        "where you can edit the attributes of an account. The only " +
        "required fields are 'Given Name', 'Last Name' and 'Date of " +
        "Birth'.\n\nWhen you are satisfied with your changes, press " +
        "'Done' to return to the previous screen.\n\nPlease note " +
        "that changes will only be saved if you select File -> Save " +
        "in the menu bar.");

  }

  /**
   * Creates a help information dialog with given title and content.
   *
   * @param title The header text of the alert.
   * @param content The content of the alert.
   */
  private void createInformationDialog(String title, String content) {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Help");
    alert.setHeaderText(title);
    alert.setContentText(content);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  @FXML
  private void getHelp() {
    String currentPage = PageNav.getCurrentNav();
    Runnable runnable = helpCommands.get(currentPage);
    runnable.run();
  }

  public void loading() {
    StatusBarController.loadingIndicator(statusBar);
  }

  public void loaded() {
    StatusBarController.loadedIndicator(statusBar);
  }
}