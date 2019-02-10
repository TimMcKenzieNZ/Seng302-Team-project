package seng302.model.comparitors;


import java.util.Comparator;
import seng302.model.person.LogEntry;

/**
 * Comparator used to compare Log objects by date in descending order.
 */
public class LogComparator implements Comparator<LogEntry> {


  /**
   * Calls the compareTo method of logA's changeTime attribute using logB's changeTime attribute as
   * a parameter and returns the result multiplied by -1. This is done to achieve descending order.
   *
   * @param logA The first Log instance to be compared.
   * @param logB The second Log instance to be compared.
   * @return int of compare
   */
  public int compare(LogEntry logA, LogEntry logB) {

    return logA.getChangeTime().compareTo(logB.getChangeTime()) * -1;

  }


}
