package seng302.model.comparitors;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.Address;
import seng302.model.person.DonorReceiver;


/**
 * This file contains a series of JUnit tests for RegionComparator.
 */
public class RegionComparatorTest {


  // Class attributes.
  List<DonorReceiver> list;
  DonorReceiver donorReceiverA;
  DonorReceiver donorReceiverB;


  /**
   * Initialise variables before each test.
   */
  @Before
  public void setup() {

    donorReceiverA = new DonorReceiver("Daniel", "", "Pollen", LocalDate.of(1813, 6, 2), "PNZ0009");
    donorReceiverB = new DonorReceiver("Frederick", "Aloysius", "Weld", LocalDate.of(1823, 5, 9),
        "PNZ0006");

    list = Arrays.asList(donorReceiverA, donorReceiverB);

  }


  /**
   * Set all variables to null.
   */
  @After
  public void tearDown() {

    list = null;
    donorReceiverA = null;
    donorReceiverB = null;

  }


  /**
   * Tests regionComparator with two accounts where donorReceiverB should be before donorReceiverA
   * in an alphabetic ordering.
   */
  @Test
  public void compareTestWithAccountBBeforeAccountA() {

    // Set account regions.
    donorReceiverA.getContactDetails()
        .setAddress(new Address(
            donorReceiverA.getContactDetails().getAddress().getStreetAddressLineOne(),
            donorReceiverA.getContactDetails().getAddress().getStreetAddressLineTwo(),
            donorReceiverA.getContactDetails().getAddress().getSuburbName(),
            donorReceiverA.getContactDetails().getAddress().getCityName(),
            "Otago",
            donorReceiverA.getContactDetails().getAddress().getPostCode(),
            donorReceiverA.getContactDetails().getAddress().getCountryCode()));

    donorReceiverB.getContactDetails()
        .setAddress(new Address(
            donorReceiverB.getContactDetails().getAddress().getStreetAddressLineOne(),
            donorReceiverB.getContactDetails().getAddress().getStreetAddressLineTwo(),
            donorReceiverB.getContactDetails().getAddress().getSuburbName(),
            donorReceiverB.getContactDetails().getAddress().getCityName(),
            "Auckland",
            donorReceiverB.getContactDetails().getAddress().getPostCode(),
            donorReceiverB.getContactDetails().getAddress().getCountryCode()));

    // Perform sorting operation.
    list.sort(new RegionComparator());
    Assert.assertEquals(donorReceiverB, list.get(0));
  }


  /**
   * Tests regionComparator with two accounts where donorReceiverB should be before donorReceiverA
   * in an alphabetic ordering.
   */
  @Test
  public void compareTestWithAccountABeforeAccountB() {

    // Set account regions.
    donorReceiverA.getContactDetails()
        .setAddress(new Address(
            donorReceiverA.getContactDetails().getAddress().getStreetAddressLineOne(),
            donorReceiverA.getContactDetails().getAddress().getStreetAddressLineTwo(),
            donorReceiverA.getContactDetails().getAddress().getSuburbName(),
            donorReceiverA.getContactDetails().getAddress().getCityName(),
            "Bay of Plenty",
            donorReceiverA.getContactDetails().getAddress().getPostCode(),
            donorReceiverA.getContactDetails().getAddress().getCountryCode()));

    donorReceiverB.getContactDetails()
        .setAddress(new Address(
            donorReceiverB.getContactDetails().getAddress().getStreetAddressLineOne(),
            donorReceiverB.getContactDetails().getAddress().getStreetAddressLineTwo(),
            donorReceiverB.getContactDetails().getAddress().getSuburbName(),
            donorReceiverB.getContactDetails().getAddress().getCityName(),
            "Canterbury",
            donorReceiverB.getContactDetails().getAddress().getPostCode(),
            donorReceiverB.getContactDetails().getAddress().getCountryCode()));

    // Perform sorting operation.
    list.sort(new RegionComparator());
    Assert.assertEquals(donorReceiverA, list.get(0));

  }


}
