package seng302.controllers.childWindows;

/**
 * Simple enum which contains a set of possible window types. This is used by ChildWindowManager to
 * identify if a window, such as a system log window, has been already opened by the user.
 */
public enum ChildWindowType {

  SYSTEM_LOG,
  CONSOLE

}
