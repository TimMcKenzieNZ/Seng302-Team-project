package seng302.controllers;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

/**
 * A class with static methods for alert prompts and other shared methods between controllers
 */
public class BaseController {

  /**
   * Creates a pop up for the user saying there was an error getting data from the server
   */
  public static void createBadPopUp() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Unable to load from server");
    alert.setContentText(
        "We were unable to load some information, please restart the application and try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }
}
