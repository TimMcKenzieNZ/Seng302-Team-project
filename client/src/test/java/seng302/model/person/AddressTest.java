package seng302.model.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AddressTest {

  private Address address;

  @Before
  public void setUp() {
    address = new Address("20 Kirkwood Ave", "", "Upper Riccarton", "Christchurch", "Canterbury",
        "8041", "NZ");
  }

  @Test
  public void testToString() {
    address.updateStreetAddressLineTwo("Erskine Building");
    String expected = "Street Address Line 1: 20 Kirkwood Ave" +
        "\nStreet Address Line 2: Erskine Building" +
        "\nSuburb: Upper Riccarton" +
        "\nCity: Christchurch" +
        "\nRegion: Canterbury" +
        "\nPost Code: 8041" +
        "\nCountry Code: NZ";
    assertEquals(expected, address.toString());
  }

  //------------------------------------------------------------------------------------------------------------
  // Validate Post Code Tests
  //------------------------------------------------------------------------------------------------------------
  @Test
  public void postCodeIsValidEmpty() {
    assertFalse(Address.postCodeIsValid(""));
  }

  @Test
  public void postCodeIsValidExtraSpace() {
    assertFalse(Address.postCodeIsValid("8041 "));
  }

  @Test
  public void postCodeIsValidPasses() {
    assertTrue(Address.postCodeIsValid("8041"));
  }

  @Test
  public void postCodeIsValidNegative() {
    assertFalse(Address.postCodeIsValid("-1040"));
  }

  @Test
  public void postCodeIsValidSingleLetter() {
    assertFalse(Address.postCodeIsValid("4o76"));
  }

  @Test
  public void postCodeIsValidAllLetters() {
    assertFalse(Address.postCodeIsValid("four"));
  }

  @Test
  public void postCodeIsValidNull() {
    assertFalse(Address.postCodeIsValid(null));
  }

  //------------------------------------------------------------------------------------------------------------
  // Set Post Code Tests
  //------------------------------------------------------------------------------------------------------------
  @Test
  public void setPostCodeSuccessEmpty() {
    address.updatePostCode("");
    assertEquals("", address.getPostCode());
  }

  @Test
  public void setPostCodeSuccessNonEmpty() {
    address.updatePostCode("0000");
    assertEquals("0000", address.getPostCode());
  }

  @Test
  public void setPostCodeFailInvalidCode() {
    address.updatePostCode("8o41");
    assertEquals("8041", address.getPostCode());
  }

  //------------------------------------------------------------------------------------------------------------
  // update Address Line 1 Tests
  //------------------------------------------------------------------------------------------------------------

  @Test
  public void updateAddress1Empty() {
    address.updateStreetAddressLineOne("");
    assertEquals("", address.getStreetAddressLineOne());
  }

  @Test
  public void updateAddress1NonEmpty() {
    address.updateStreetAddressLineOne("1 Balgay");
    assertEquals("1 Balgay", address.getStreetAddressLineOne());
  }

  //------------------------------------------------------------------------------------------------------------
  // update Address Line 2 Tests
  //------------------------------------------------------------------------------------------------------------

  @Test
  public void updateAddress2Empty() {
    address.updateStreetAddressLineTwo("");
    assertEquals("", address.getStreetAddressLineTwo());
  }

  @Test
  public void updateAddress2NonEmpty() {
    address.updateStreetAddressLineTwo("1 Balgay");
    assertEquals("1 Balgay", address.getStreetAddressLineTwo());
  }

  //------------------------------------------------------------------------------------------------------------
  // update Suburb Name Tests
  //------------------------------------------------------------------------------------------------------------
  @Test
  public void updateSuburbEmpty() {
    address.updateSuburb("");
    assertEquals("", address.getSuburbName());
  }

  @Test
  public void updateSuburbNonEmpty() {
    address.updateSuburb("1 Balgay");
    assertEquals("1 Balgay", address.getSuburbName());
  }

  //------------------------------------------------------------------------------------------------------------
  // update City Name Tests
  //------------------------------------------------------------------------------------------------------------
  @Test
  public void updateCityNameEmpty() {
    address.updateCityName("");
    assertEquals("", address.getCityName());
  }

  @Test
  public void updateCityNameNonEmpty() {
    address.updateCityName("1 Balgay");
    assertEquals("1 Balgay", address.getCityName());
  }

  //------------------------------------------------------------------------------------------------------------
  // update Region Tests
  //------------------------------------------------------------------------------------------------------------

  @Test
  public void updateRegionEmpty() {
    address.updateRegion("");
    assertEquals("", address.getRegion());
  }

  @Test
  public void updateRegionNonEmpty() {
    address.updateRegion("1 Balgay");
    assertEquals("1 Balgay", address.getRegion());
  }

  //------------------------------------------------------------------------------------------------------------
  // update post code Tests
  //------------------------------------------------------------------------------------------------------------

  @Test
  public void updatePostCodeEmpty() {
    address.updatePostCode("");
    assertEquals("", address.getPostCode());
  }

  @Test
  public void updatePostCodeNonEmpty() {
    address.updatePostCode("8080");
    assertEquals("8080", address.getPostCode());
  }

  @Test
  public void updatePostCodeInvalid() {
    address.updatePostCode("help");
    assertEquals("8041", address.getPostCode());
  }

  //------------------------------------------------------------------------------------------------------------
  // update Country Code Tests
  //------------------------------------------------------------------------------------------------------------

  @Test
  public void updateCountryCodeEmpty() {
    address.updateCountryCode("");
    assertEquals("", address.getCountryCode());
  }

  @Test
  public void updateCountryCodeNonEmpty() {
    address.updateCountryCode("1 Balgay");
    assertEquals("1 Balgay", address.getCountryCode());
  }
}