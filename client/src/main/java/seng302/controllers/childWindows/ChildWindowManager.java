package seng302.controllers.childWindows;


import java.util.HashSet;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng302.controllers.CreateEditController;
import seng302.model.person.User;

/**
 * ChildWindowManager is designed to track a set of ChildWindow instances and provides a number of
 * methods which operate on this set. ChildWindow instances must be added manually using the
 * addChildWindow method.
 */
public class ChildWindowManager {


  // Class attributes.
  private static ChildWindowManager childWindowManager;
  private Set<ChildWindow> childWindows;


  /**
   * Initialises an instance of the ChildWindowManager class. This constructor is private as
   * ChildWindowManager is implemented as a singleton.
   */
  private ChildWindowManager() {

    childWindows = new HashSet<ChildWindow>();

  }


  /**
   * Returns the same unique instance of ChildWindowManager after every call except the first where
   * it initialises the instance and then returns it.
   *
   * @return The sole existing instance of ChildWindowManager.
   */
  public static ChildWindowManager getChildWindowManager() {

    if (childWindowManager == null) {

      childWindowManager = new ChildWindowManager();

    }

    return childWindowManager;

  }


  /**
   * Adds a new ChildWindow instance to the set of tracked instances.
   *
   * @return True if the resulting ChildWindow instance did not already exist.
   */
  public boolean addChildWindow(Stage stage, ChildWindowType type) {

    ChildWindow childWindow = new ChildWindow(stage, type);
    addRemoveOnCloseListener(childWindow, null);
    return childWindows.add(childWindow);

  }


  /**
   * Creates a new ChildWindow instance from a Stage and User. This should be used if the window
   * displays data related to a person.
   *
   * @param stage The Stage object to appear in the child window.
   * @param account The User object associated with the window.
   * @return True if the resulting ChildWindow instance did not already exist.
   */
  public boolean addChildWindow(Stage stage, User account, Object controller) {

    ChildWindow childWindow = new ChildWindow(stage, account);
    addRemoveOnCloseListener(childWindow, controller);
    return childWindows.add(childWindow);

  }

  /**
   * Creates a new ChildWindow instance from a Stage and User. This should be used if the window
   * displays data related to a person.
   *
   * @param stage The Stage object to appear in the child window.
   * @param account The User object associated with the window.
   * @return True if the resulting ChildWindow instance did not already exist.
   */
  public boolean addChildWindow(Stage stage, User account) {

    ChildWindow childWindow = new ChildWindow(stage, account);
    addRemoveOnCloseListener(childWindow, null);
    return childWindows.add(childWindow);

  }


  /**
   * Adds a listener to a ChildWindow object's stage so that it is removed from the set of
   * ChildWindow objects when closed.
   *
   * @param childWindow The ChildWindow the listener is to be added to.
   */
  private void addRemoveOnCloseListener(ChildWindow childWindow, Object controller) {

    EventHandler<WindowEvent> event;

    if (controller instanceof CreateEditController) {

      event = windowEvent -> {

        if (((CreateEditController) controller).closeWindow(windowEvent)) {
          childWindows.remove(childWindow);

        }

      };

    } else {

      event = windowEvent -> {

        childWindows.remove(childWindow);

      };

    }

    childWindow.addCloseEvent(event);

  }


  /**
   * Iterates through the set of tracked ChildWindow instances and brings the instance associated
   * with the account provided to the front. Returns false if no instance is associated with the
   * given account.
   *
   * @param user The User associated with the window to be brought forward.
   * @return True if an associated window exists. False otherwise.
   */
  public boolean childWindowToFront(User user) {

    boolean exists = false;

    for (ChildWindow childWindow : childWindows) {

      if (childWindow.UserIsIdentical(user)) {

        exists = true;
        childWindow.stageToFront();
        break;

      }

    }

    return exists;

  }


  /**
   * Iterates through the set of tracked ChildWindow instances and brings the instance with the
   * associated ChildWindowType to the front. Returns false if no window of this type already
   * exists.
   *
   * @param type The type associated with the window to be brought forward.
   * @return True if an associated window exists. False otherwise.
   */
  public boolean childWindowToFront(ChildWindowType type) {

    boolean exists = false;

    for (ChildWindow childWindow : childWindows) {

      if (childWindow.TypeIsIdentical(type)) {

        exists = true;
        childWindow.stageToFront();
        break;

      }

    }

    return exists;

  }


  /**
   * Closes all ChildWindow instances tracked by ChildWindowManager and clears the set of tracked
   * ChildWindow instances.
   */
  public void closeAllChildWindows() {

    for (ChildWindow childWindow : childWindows) {

      childWindow.closeStage();

    }

    childWindows.clear();

  }


}
