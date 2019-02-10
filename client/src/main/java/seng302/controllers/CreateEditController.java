package seng302.controllers;

import javafx.event.Event;


/**
 * CreateEditController is an abstract class which requires all implementations to have a close
 * window method. This is called in an on-close-request.
 */
public  interface CreateEditController {

  /**
   * Handles window closing.
   */
  boolean closeWindow(Event event);

}
