package seng302.model;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import seng302.App;
import seng302.controllers.MenuBarController;

/**
 * Class for other controllers to use to load other pages in the app. Based on the PageNavigator
 * class from seng202 team 8 (2017).
 */
public class PageNav {

  public static String searchValue = ""; // The search term to keep consistent between searches
  public static Boolean isAdministrator = false;

  public static final String MAINMENU = "/MainMenu.fxml";
  public static final String EDIT = "/editPane.fxml";
  public static final String VIEW = "/viewProfilePane.fxml";
  public static final String LOGIN = "/LoginPane.fxml";
  public static final String MENUBAR = "/menuBar.fxml";
  public static final String LISTVIEW = "/DonorListView.fxml";
  public static final String CREATE = "/createUserPane.fxml";
  public static final String CREATECLINICIAN = "/clinicianCreation.fxml";
  public static final String VIEWEDITCLINICIAN = "/ClinicianProfileView.fxml";
  public static final String TRANSPLANTLIST = "/transplantWaitingListPane.fxml";

  public static final String ILLNESS = "/createOrModifyIllnessPane.fxml";
  public static final String PROCEDURE = "/addEditProcedure.fxml";

  public static final String ADMINMENU = "/newAdminMainMenu.fxml";
  public static final String CLINICIANSLIST = "/cliniciansList.fxml";
  public static final String ADMINSLIST = "/adminsList.fxml";
  public static final String SYSTEMLOG = "/systemLog.fxml";
  public static final String CREATEADMIN = "/administratorCreation.fxml";
  public static final String VIEWADMIN = "/administratorViewProfile.fxml";
  public static final String COMMANDLINE = "/cliTab.fxml";
  public static final String IMPORTDATA = "/ImportUsersSetup.fxml";
  public static final String IMPORTREPORT = "/ImportUsersReport.fxml";

  public static final String COUNTRYLIST = "/countryEditer.fxml";
  public static final String GRAPHS = "/graphs.fxml";

  public static Object controller;


  /**
   * The app main controller to use.
   */
  private static MenuBarController menuBarController;

  private static String currentNav;


  /**
   * Keeps the menuBar controller as the back layer when navigating to other pages
   *
   * @param menuBarController The menu bar controller to be set for this instance of the class
   * PageNav
   */
  public static void setMenuBarController(MenuBarController menuBarController) {
    PageNav.menuBarController = menuBarController;
  }

  /**
   * Change the current page with reference to the last tab in tabbed pane
   *
   * @param page Page to go to
   */
  public static void loadNewPage(String page) {
    try {
      System.getProperty("user.dir");

      FXMLLoader loader = new FXMLLoader(PageNav.class.getResource(page));
      Node node = loader.load();
      controller = loader.getController();
      App.setWindowOnHidden(App.getWindow(), controller);

      menuBarController.setNewPage(node);
      currentNav = page;
    } catch (IOException e) {
      // We can't find the resources we need.
      System.getProperty("user.dir");
      e.printStackTrace();
      System.out.println("ERROR: Something went wrong with opening the FXML.");
    }

  }

  /**
   * Change the current page with reference to the last tab in tabbed pane
   *
   * @param page Page to go to
   * @param tabIndex Tab previously used
   * @param tabString String of tab
   */
  public static void loadNewPage(String page, int tabIndex, String tabString) {
    try {
      System.getProperty("user.dir");
      FXMLLoader loader = new FXMLLoader(PageNav.class.getResource(page));
      Node node = loader.load();
      controller = loader.getController();
      App.setWindowOnHidden(App.getWindow(), controller);
      menuBarController.setNewPage(node);
      currentNav = page;
      TabPane tabPane = (TabPane) App.getWindow().getScene()
          .lookup("#" + tabString); // Get tab in edit tab
      if (tabIndex < 3) { // Tabs containing clinician only information
        tabPane.getSelectionModel().clearAndSelect(tabIndex);
      } else if (tabIndex == 3 && tabString.equals(
          "profileViewTabbedPane")) { // Other medical history which can be edited by the user
        tabPane.getSelectionModel().clearAndSelect(5);
      } else if (tabIndex == 5 && tabString
          .equals("mainTabPane")) { // Other medical history which can be edited by the user
        tabPane.getSelectionModel().clearAndSelect(3);
      }
    } catch (IOException e) { // we can't find the resources we need
      System.getProperty("user.dir");
    }
  }

  public static String getCurrentNav() {
    return currentNav;
  }


  public static void loading() {
    menuBarController.loading();
  }

  public static void loaded() {
    menuBarController.loaded();
  }
}
