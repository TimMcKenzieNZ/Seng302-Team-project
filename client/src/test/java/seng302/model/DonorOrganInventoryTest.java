package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DonorOrganInventoryTest {

  private DonorOrganInventory donOrgInv;
  private AccountManager accMan;


  @Before
  public void setUp() {
    donOrgInv = new DonorOrganInventory(true, false, true, true,
        false, false, true,
        false, true, false, true, true);
  }

  /**
   * tests successful case where the given string is returned when the given boolean is 'true'
   */
  @Test
  public void booleanToStringTest1() {
    assertTrue(donOrgInv.booleanToString("Liver", true).equals("\tLiver\n"));
  }

  /**
   * tests successful case where an empty string is returned when the given boolean is 'false'
   */
  @Test
  public void booleanToStringTest2() {
    assertTrue(donOrgInv.booleanToString("Liver", false).equals(""));
  }

  /**
   * tests failure case when the returned string should be empty.
   */
  @Test
  public void booleanToStringTest3() {
    assertFalse(donOrgInv.booleanToString("Liver", false).equals("\tLiver\n"));
  }

  /**
   * tests failure case when the returned string should be "Liver".
   */
  @Test
  public void booleanToStringTest4() {
    assertFalse(donOrgInv.booleanToString("Liver", true).equals(""));
  }

  /**
   * tests that the given output of toString() matches the expected output.
   */
  @Test
  public void toStringTest() {
    String expectedOutput = "Organs to donate:\n" +
        "\tLiver\n" +
        "\tPancreas\n" +
        "\tHeart\n" +
        "\tCorneas\n" +
        "\tSkin\n" +
        "\tBone marrow\n" +
        "\tConnective Tissue\n" +
        "\n" +
        "\n";
    assertTrue(donOrgInv.toString().equals(expectedOutput));
  }

  /**
   * Tests when an organ for a receiver or donor is changed.
   */
  @Test
  public void updateOrganDonationTest() {
    String[] message = donOrgInv.updateOrganDonation("donor", "liver", "false");
    assertEquals("success", message[0]);
  }

  /**
   * Tests when an organ for a receiver or donor is changed.
   */
  @Test
  public void updateOrganDonationTest2() {
    String[] message = donOrgInv.updateOrganDonation("receiver", "liver", "false");
    assertEquals("success", message[0]);
  }

}
