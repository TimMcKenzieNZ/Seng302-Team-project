package seng302;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.controllers.CreateEditController;
import seng302.controllers.MenuBarController;
import seng302.controllers.childWindows.ChildWindow;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.controllers.childWindows.ChildWindowType;
import seng302.model.*;
import seng302.model.person.User;

import static seng302.model.enums.ADDRESSES.setLocalServer;
import static seng302.model.enums.ADDRESSES.setVmServer;


public class App extends Application {

  // Class attributes.
  private static Stage window;
  private static ArrayList<ChildWindow> childWindows = new ArrayList<>();
  private static AccountManager Database;
  public static ChildWindowManager childWindowManager = ChildWindowManager.getChildWindowManager();
  private static CommandLine commandLine;
  private static boolean launchedGUIBefore = false;
  private static UndoableManager undoableManager = new UndoableManager();
  private final Set<KeyCode> pressedKeys = new HashSet<>();
  private static boolean unsavedChanges = false;
  private static boolean saveInProgress = false;
  private static DrugInteractionsCache drugInteractionsCache;
  private static final Session currentSession = new Session();

  public static UndoableManager getUndoableManager() {
    return undoableManager;
  }


  /**
   * Gets the main stage of the GUI..
   *
   * @return The main stage of the GUI.
   */
  public static Stage getWindow() {

    return App.window;

  }

  /**
   * Returns the instantiated account manager Database.
   *
   * @return An AccountManager instance.
   */
  public static Session getCurrentSession() {
    return currentSession;

  }



  /**
   * Returns the instantiated account manager Database.
   *
   * @return An AccountManager instance.
   */
  public static AccountManager getDatabase() {
    if (Database == null) {
      Database = new AccountManager();
      Database.importAccounts();
      Database.importClinicians();
      Database.importAdmins();
//      Database.addClinicianIfNoneExists();
//      Database.addDefaultAdminIfNoneExists();
    }
    return Database;

  }

  /**
   * Returns the instantiated cache
   *
   * @return A cache instance.
   */
  public static DrugInteractionsCache getDrugInteractionsCache() {
    if (drugInteractionsCache == null) {
      drugInteractionsCache = new DrugInteractionsCache();
    }
    return drugInteractionsCache;

  }


  /**
   * Sets window to the specified JavaFX stage and shows the stage to the user.
   *
   * @param stage The stage to set window to.
   * @throws IOException An exception occurred.
   */
  @Override
  public void start(Stage stage) throws IOException {
    window = stage;

    // Set close request so that all windows are closed with the main window.
    window.setOnCloseRequest(request -> {

      closeChildWindows();

    });

    childWindowManager = ChildWindowManager.getChildWindowManager();

    stage.setTitle("Organ Donation Analysis System");
    stage.setScene(createScene(loadMainPane()));
    Image image = new Image("images/icon.png");
    stage.getIcons().add(image);
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    stage.setX(primaryScreenBounds.getMinX() + 100);
    stage.setY(primaryScreenBounds.getMinY() + 10);
    stage.show();
    stage.setMinWidth(900.0);
    stage.setMinHeight(850.0);


  }


  /**
   * Attaches on onHidden handler method defined in controller to stage if the controller is an
   * implementation of the CreateEditController interface. If not, a default close request will be
   * used. This should be used ONLY with controllers operating on the main window.
   * @param stage The Stage object to which the onHidden handler is to be attached.
   * @param controller An Object from which the handler method is to be sourced.
   */
  public static void setWindowOnHidden(Stage stage, Object controller) {

    if (controller instanceof CreateEditController) {

      stage.setOnCloseRequest(event -> {

        ((CreateEditController) controller).closeWindow(event);
        closeChildWindows();

      });

    } else {

      stage.setOnCloseRequest(event -> {
        Platform.exit();
        System.exit(0);
      });

    }

  }


  /**
   * Loads the main pane from an FXML file.
   *
   * @return The main pane.
   * @throws IOException When the main pane cannot be retrieved from FXML.
   */
  private Pane loadMainPane() throws IOException {

    FXMLLoader loader = new FXMLLoader();

    Pane mainPane = loader.load(getClass().getResourceAsStream(PageNav.MENUBAR));
    MenuBarController menuBarControl = loader.getController();

    PageNav.setMenuBarController(menuBarControl);
    PageNav.loadNewPage(PageNav.LOGIN);

    return mainPane;
  }


  /**
   * Closes all child windows.
   */
  public static void closeChildWindows() {

    childWindowManager.closeAllChildWindows();

  }


  /**
   * Adds a new stage to childWindows. This is done so that the new window can be closed with the
   * main window.
   *
   * @param window The window to be added to the childWindow list.
   * @param user The user object to be associated with the window.
   */
  public static void addChildWindow(Stage window, User user) {

    childWindowManager.addChildWindow(window, user);

  }


  /**
   * Adds a child window without an associated account.
   *
   * @param window The window to be added.
   */
  public static void addChildWindow(Stage window, ChildWindowType type) {

    childWindowManager.addChildWindow(window, type);

  }


  /**
   * Searches for a child window associated with the given account. If one exists, it moves it to
   * the front and returns true. If it does not, the method returns false signifying failure.
   *
   * @param account The account object associated with the window that is to
   * @return True if a window was brought forward. False otherwise.
   */
  public static boolean childWindowToFront(User account) {

    return childWindowManager.childWindowToFront(account);

  }


  /**
   * Searches for a child window of the given type. If one exists, it is moved to the front and the
   * method returns true. Otherwise, it returns false.
   *
   * @param type The ChildWindow type.
   */
  public static boolean childWindowToFront(ChildWindowType type) {

    return childWindowManager.childWindowToFront(type);

  }


  /**
   * Creates a scene using the specified pane.
   *
   * @param mainPane The pane to create the scene with.
   * @return The new scene.
   */
  private Scene createScene(Pane mainPane) {

    Scene scene = new Scene(mainPane);
    return scene;

  }


  public static void setSaveInProgress(boolean value) {
    saveInProgress = value;
  }



  /**
   * Returns true if the application is in an unsaved state, or false if it is not.
   *
   * @return The value of the variable unsavedChanges.
   */
  public static boolean unsavedChangesExist() {

    return unsavedChanges;

  }

  /**
   * Returns true if the application is in an unsaved state, or false if it is not.
   *
   * @return The value of the variable unsavedChanges.
   */
  public static boolean saveInProgress() {

    return saveInProgress;

  }


  /**
   * The main method of the application.
   *
   * @param args Command line arguments represented as an array of String objects.
   */
  public static void main(String[] args) {
     setVmServer();
//     setLocalServer();
    childWindowManager = ChildWindowManager.getChildWindowManager();
    getDrugInteractionsCache();
    Database = new AccountManager();
    Database.setSystemLog(Marshal.importSystemLog());
    Database.setStatusUpdates(new ArrayList<>());
//    Database.importAccounts();
//    Database.importClinicians();
//    Database.importAdmins();
//    Database.exportAdmins();
//    Database.exportClinicians();
    launch(args);
  }
}
