package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

public class ReceiverOrganInventoryTest {

  private ReceiverOrganInventory reqOrgans;
  private AccountManager accMan;

  @Before
  public void setUp() {
    LocalDateTime organUpdateTime = LocalDate.of(2018, 5, 3).atTime(14, 15);
    reqOrgans = new ReceiverOrganInventory(true, false, false, false, false, false, false, false,
        false, false, false, false, organUpdateTime, organUpdateTime, organUpdateTime,
        organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime,
        organUpdateTime, organUpdateTime, organUpdateTime, organUpdateTime);
  }

  /**
   * tests that the given output of toString() matches the expected output.
   */
  @Test
  public void toStringTest() {
    String expectedOutput = "Organs to receive:\n" +
        "\tLiver\n" +
        "\n" +
        "\n";
    assertTrue(reqOrgans.toString().equals(expectedOutput));
  }

  /**
   * Tests when an organ for a receiver or donor is changed.
   */
  @Test
  public void updateOrganDonationTest() {
    String[] message = reqOrgans.updateOrganDonation("receiver", "pancreas", "true");
    assertEquals("success", message[0]);
  }

  /**
   * Tests when an organ for a receiver or donor is changed.
   */
  @Test
  public void updateOrganDonationTest2() {
    String[] message = reqOrgans.updateOrganDonation("receiver", "liver", "false");
    assertEquals("To remove a receiving organ, please go to the transplant waiting list.",
        message[0]);
  }


}
