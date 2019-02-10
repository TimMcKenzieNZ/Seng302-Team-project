package seng302.model.utility;

/**
 * This class was created to store methods which operate on strings that may be required anywhere in
 * the application.
 */
public class StringExtension {


  /**
   * Private constructor for the StringExtension class. Its sole purpose is to hide the default
   * constructor from other classes, preventing instantiation elsewhere.
   */
  private StringExtension() { }


  /**
   * Returns a new string by converting 'input' to upper case. Returns null if 'input' is null,
   * rather than generating a NullPointerException.
   *
   * @param input The string to be converted to upper case
   * @return the uppercase string, or null if input was empty
   */
  public static String toUpperCase(String input) {

    String result;

    try {

      result = input.toUpperCase();
      return result;

    } catch (NullPointerException exception) {

      result = null;
      return result;

    }

  }


  /**
   * Compares two String objects for equality in a null-safe manner.
   *
   * @param a The first String to be examined.
   * @param b The second String to be examined.
   * @return True if a and b are null, false one is null and the other is not, or the result of the
   * standard .equals() method if neither is null.
   */
  public static boolean nullCompare(String a, String b) {

    if (a == null && b == null) {

      // Both are null, therefore equal.
      return true;

    } else if ((a == null) ^ (b == null)) {

      // One is false, and the other is not. Therefore, not equal.
      return false;

    } else {

      // Both are not null, therefore use standard comparison.
      return a.equals(b);

    }

  }


  /**
   * Takes a String object and either returns object in its original state, or returns an empty
   * string if that object is null.
   *
   * @param input The String object to be filtered.
   * @return A String object which is either the object originally passed to the method, or an empty
   * String if the original object was null.
   */
  public static String nullFilter(String input) {

    if (input == null) {

      return "";

    } else {

      return input;

    }

  }


  /**
   * Takes a string object and converts empty strings to null objects. Meant to undo the function nullFilter
   * @param input The string object to be unfiltered
   * @return The string passed in unless it was an empty string in which case null will be returned
   */
  public static String reverseNullFilter(String input) {

    if (input.equals("")) {

      return null;

    }

    return input;
  }


}
