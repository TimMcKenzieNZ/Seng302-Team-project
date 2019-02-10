package seng302.model;

/**
 * This class was created to store methods which operate on boolean primitives and may be required
 * anywhere in the application.
 */
public class BooleanExtension {

  /**
   * Takes a Boolean object. If it is null, the method returns false. Otherwise, the method returns
   * the Boolean object. This is best used to prevent null pointer exceptions when a null Boolean
   * object represents unknown information.
   *
   * @param value The Boolean object to be processed.
   * @return False if value is null, or value itself.
   */
  public static Boolean getBoolean(Boolean value) {
    if (value == null) {
      return false;
    } else {
      return value;
    }
  }
}
