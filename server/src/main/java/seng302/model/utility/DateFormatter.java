package seng302.model.utility;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * The DateFormatter class provides a set of utility methods for formatting dates.
 */
public class DateFormatter {


  /**
   * Private constructor for the DateFormatter class which does nothing. Its sole purpose is to hide
   * the default constructor from other classes, preventing instantiation.
   */
  private DateFormatter() { }

  /**
   * The date format used to create localDate objects from date strings in the .csv files.
   */
  private static final DateTimeFormatter dateFormat1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

  /**
   * The date format used to create localDate objects from date strings in the .csv files.
   */
  private static final DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("M/dd/yyyy");


  /**
   * Formats the creation date of an donorReceiver into a readable value
   *
   * @param time The LocalDatetime to be formatted
   * @return A string of format dd-MM-yyyy HH:mm:ss which represents the creation date of the
   * donorReceiver
   */
  public static String formatCreationDate(LocalDateTime time) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    return time.format(formatter);

  }

  /**
   * Parses the given date string into a LocalDate object. If the format of the date is incorrect,
   * it returns null instead.
   *
   * @param dateString a string of a date that should be of the form "MM/dd/yyyy"
   * @return a LocalDate object based on the given string or null
   */
  public static LocalDate parseStringToDate(String dateString) {
    LocalDate date;
    try {
      date = LocalDate.parse(dateString, dateFormat1);
    } catch (DateTimeParseException e) {
      try {
        date = LocalDate.parse(dateString, dateFormat2);
      } catch (DateTimeParseException e2) {
        return null;
      }
    }
    return date;
  }


}
