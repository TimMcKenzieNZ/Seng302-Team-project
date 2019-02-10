package seng302.controllers.childWindows;


import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng302.model.person.User;


/**
 * ChildWindow is a simple container for a Stage, User, and ChildWindowType object. Of the latter
 * two, only one will be initialised in each instance.
 */
public class ChildWindow {


  // Class attributes.
  private Stage stage;
  private User user;
  private ChildWindowType type;


  /**
   * Creates a ChildWindow instance with the given stage and an associated ChildWindowType enum.
   * Neither should be null.
   *
   * @param stage The JavaFX stage displayed in the child window.
   * @param type The ChildWindowType object associated with the window.
   */
  ChildWindow(Stage stage, ChildWindowType type) {

    if (stage == null || type == null) {

      throw new IllegalArgumentException("Stage and ChildWindowType " +
          "objects passed to ChildWindow should not be null.");

    }

    this.stage = stage;
    this.type = type;

  }


  /**
   * Creates a ChildWindowInstance with the given stage and an associated User object.
   *
   * @param stage The JavaFX stage displayed in the ChildWindow.
   * @param user The User object associated with the window.
   */
  ChildWindow(Stage stage, User user) {

    if (stage == null || user == null) {

      throw new IllegalArgumentException("Stage and User objects " +
          "passed to ChildWindow should not be null.");

    }

    this.stage = stage;
    this.user = user;

  }


  /**
   * Compares the User object associated with the current ChildWindow instance to a second User
   * object. Returns true if they are the same. Returns false otherwise, or if user is null.
   *
   * @param otherUser The second User object to be compared.
   * @return True if otherPerson is identical to person. False otherwise.
   */
  boolean UserIsIdentical(User otherUser) {

    if (user != null) {

      return user.equals(otherUser);

    } else {

      return false;

    }

  }


  /**
   * Returns true if the provided ChildWindowType object is the same as the type specified in the
   * current instance. Otherwise, returns false. If type is null, this method returns false by
   * default.
   *
   * @param otherType The ChildWindow type to be compared.
   * @return True if otherType is equal to type. False otherwise.
   */
  boolean TypeIsIdentical(ChildWindowType otherType) {

    if (type != null) {

      return type.equals(otherType);

    } else {

      return false;

    }

  }


  /**
   * Moves stage to the front so that it appears above all other windows.
   */
  void stageToFront() {

    stage.toFront();

  }


  /**
   * Closes the stage associated with the current ChildWindow instance.
   */
  void closeStage() {

    stage.close();

  }


  /**
   * Adds an event which is triggered when the window is closed.
   *
   * @param event The event to trigger when the window is closed.
   */
  void addCloseEvent(EventHandler<WindowEvent> event) {

    stage.setOnCloseRequest(event);

  }


}
