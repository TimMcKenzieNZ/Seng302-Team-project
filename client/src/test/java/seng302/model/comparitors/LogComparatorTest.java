package seng302.model.comparitors;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;


/**
 * LogComparatorTest provides a series of JUnit tests for the LogComparator class and defines pre
 * and post test functionality for them.
 */
public class LogComparatorTest {

  LogComparator comparator;
  LogEntry earlierLogEntry;
  LogEntry laterLogEntry;
  LogEntry duplicateLogEntry;
  DonorReceiver sampleAccount;


  /**
   * Create objects needed in testing.
   */
  @Before
  public void setUp() {

    sampleAccount = new DonorReceiver("Michael", "Joseph", "Savage", LocalDate.of(1872, 3, 23),
        "PNZ0023");
    comparator = new LogComparator();

    earlierLogEntry = new LogEntry(sampleAccount, sampleAccount, "", "", "");
    earlierLogEntry.setChangeTime(LocalDateTime.of(1935, 12, 17, 12, 0));

    laterLogEntry = new LogEntry(sampleAccount, sampleAccount, "", "", "");
    laterLogEntry.setChangeTime(LocalDateTime.of(1940, 3, 27, 12, 0));

    duplicateLogEntry = new LogEntry(sampleAccount, sampleAccount, "", "", "");
    duplicateLogEntry.setChangeTime(LocalDateTime.of(1940, 3, 27, 12, 0));

  }


  /**
   * Tests the result of LogComparator's compare method when an earlier log entry is the first
   * parameter and a later log entry is the second. The result should be greater than 0, signifying
   * descending order.
   */
  @Test
  public void comparatorTestWithEarlierBeforeLater() {

    int result = comparator.compare(earlierLogEntry, laterLogEntry);
    assertTrue(result > 0);

  }


  /**
   * Tests the result of LogComparator's compare method when a later log entry is the first
   * parameter and an earlier log entry is the second. The result should be less than 0,
   * corresponding to descending order.
   */
  @Test
  public void comparatorTestWithLaterBeforeEarlier() {

    int result = comparator.compare(laterLogEntry, earlierLogEntry);
    assertTrue(result < 0);


  }


  /**
   * Tests the result of LogComparator's compare method with
   */
  @Test
  public void comparatorTestWithLaterAndDuplicate() {

    int result = comparator.compare(laterLogEntry, duplicateLogEntry);
    assertEquals(0, result);

  }


  /**
   *
   */
  @After
  public void tearDown() {

    comparator = null;

    earlierLogEntry = null;
    laterLogEntry = null;
    duplicateLogEntry = null;

  }

}
