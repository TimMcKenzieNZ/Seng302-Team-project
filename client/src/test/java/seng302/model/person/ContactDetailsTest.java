package seng302.model.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ContactDetailsTest {

  public ContactDetails contacts;

  @Before
  public void setUp() {
    Address address = new Address("20 Kirkwood Ave", "Erskine Building", "Ilam", "Christchurch",
        "Canterbury", "8041", "NZ");
    contacts = new ContactDetails(address, "0221234545", "031234545", "help@test.com");
  }

  @Test
  public void testConstructor() {
    assertEquals("20 Kirkwood Ave", contacts.getAddress().getStreetAddressLineOne());
    assertEquals("Erskine Building", contacts.getAddress().getStreetAddressLineTwo());
    assertEquals("Ilam", contacts.getAddress().getSuburbName());
    assertEquals("Christchurch", contacts.getAddress().getCityName());
    assertEquals("Canterbury", contacts.getAddress().getRegion());
    assertEquals("8041", contacts.getAddress().getPostCode());
    assertEquals("NZ", contacts.getAddress().getCountryCode());
    assertEquals("0221234545", contacts.getMobileNum());
    assertEquals("031234545", contacts.getHomeNum());
    assertEquals("help@test.com", contacts.getEmail());
  }

  @Test
  public void testToString() {
    String address = "Street Address Line 1: 20 Kirkwood Ave" +
        "\nStreet Address Line 2: Erskine Building" +
        "\nSuburb: Ilam" +
        "\nCity: Christchurch" +
        "\nRegion: Canterbury" +
        "\nPost Code: 8041" +
        "\nCountry Code: NZ";
    String expected = "Address:\n" + address
        + "\nMobile Number: 0221234545\nHome Number: 031234545\nEmail: help@test.com\n";
    System.out.println(expected);
    assertEquals(expected, contacts.toString());
  }

  @Test
  public void testUpdateMobileNumNonNumber() {
    String result = contacts.updateMobileNum("help");
    assertEquals("ERROR: Invalid value help. Mobile number must be numeric.\n", result);
    assertEquals("0221234545", contacts.getMobileNum());
  }

  @Test
  public void testUpdateMobileNumSuccess() {
    contacts.updateMobileNum("0229875656");
    assertEquals("0229875656", contacts.getMobileNum());
  }

  @Test
  public void testUpdateHomeNumNonNumber() {
    String result = contacts.updateHomeNum("help");
    assertEquals("031234545", contacts.getHomeNum());
    assertEquals("ERROR: Invalid value help. Home number must be numeric.\n", result);
  }

  @Test
  public void testUpdateHomeNumSuccess() {
    contacts.updateHomeNum("039875656");
    assertEquals("039875656", contacts.getHomeNum());
  }

  @Test
  public void testUpdateEmailNoAt() {
    String result = contacts.updateEmail("helptest.com");
    assertEquals("ERROR: Invalid value helptest.com. Please enter a proper email address.\n",
        result);
    assertEquals("help@test.com", contacts.getEmail());
  }

  @Test
  public void testUpdateEmailSuccess() {
    contacts.updateEmail("helping@testing.com");
    assertEquals("helping@testing.com", contacts.getEmail());
  }

  @Test
  public void testValidateEmailTrue() {
    assertTrue(contacts.validateEmail("email@domain.com"));
    assertTrue(contacts.validateEmail("firstname.lastname@domain.com"));
    assertTrue(contacts.validateEmail("email@subdomain.domain.com"));
    assertTrue(contacts.validateEmail("firstname+lastname@domain.com"));
    assertTrue(contacts.validateEmail("1234567890@domain.com"));
    assertTrue(contacts.validateEmail("email@domain-one.com"));
    assertTrue(contacts.validateEmail("_______@domain.com"));
    assertTrue(contacts.validateEmail("email@domain.co.jp"));
    assertTrue(contacts.validateEmail("firstname-lastname@domain.com"));
  }

  @Test
  public void testValidateEmailFalse() {
    assertFalse(contacts.validateEmail("plainaddress"));
    assertFalse(contacts.validateEmail("#@%^%#$@#$@#.com"));
    assertFalse(contacts.validateEmail("@domain.com"));
    assertFalse(contacts.validateEmail("Joe Smith <email@domain.com>"));
    assertFalse(contacts.validateEmail("email.domain.com"));
    assertFalse(contacts.validateEmail("email@domain@domain.com"));
    assertFalse(contacts.validateEmail(".email@domain.com"));
    assertFalse(contacts.validateEmail("email.@domain.com"));
    assertFalse(contacts.validateEmail("email..email@domain.com"));
    assertFalse(contacts.validateEmail("あいうえお@domain.com"));
    assertFalse(contacts.validateEmail("email@domain.com (Joe Smith)"));
    assertFalse(contacts.validateEmail("email@domain"));
    assertFalse(contacts.validateEmail("email@111.222.333.44444"));
    assertFalse(contacts.validateEmail("email@domain..com"));
  }

  @Test
  public void testUpdateContactUnknownAttribute() {
    String[] result = contacts.updateContact("help", "5");
    String expected = "Invalid command";
    assertEquals(expected, result[0]);
  }

  @Test
  public void testUpdateContactsMobileNumNonNumber() {
    String[] result = contacts.updateContact("mobileNum", "help");
    assertEquals("ERROR: Invalid value help. Mobile number must be numeric.\n", result[0]);
    assertEquals("0221234545", contacts.getMobileNum());
  }

  @Test
  public void testUpdateContactMobileNumSuccess() {
    contacts.updateContact("mobileNum", "0229875656");
    assertEquals("0229875656", contacts.getMobileNum());
  }

  @Test
  public void testUpdateContactsHomeNumNonNumber() {
    String[] result = contacts.updateContact("homeNum", "help");
    assertEquals("031234545", contacts.getHomeNum());
    assertEquals("ERROR: Invalid value help. Home number must be numeric.\n", result[0]);
  }

  @Test
  public void testUpdateContactsHomeNumSuccess() {
    contacts.updateContact("homeNum", "039875656");
    assertEquals("039875656", contacts.getHomeNum());
  }

  @Test
  public void testUpdateContactsEmailNoAt() {
    String[] result = contacts.updateContact("email", "helptest.com");
    assertEquals("ERROR: Invalid value helptest.com. Please enter a proper email address.\n",
        result[0]);
    assertEquals("help@test.com", contacts.getEmail());
  }

  @Test
  public void testUpdateContactEmailSuccess() {
    contacts.updateContact("email", "helping@testing.com");
    assertEquals("helping@testing.com", contacts.getEmail());
  }

  @Test
  public void updateContactAddress1Empty() {
    contacts.updateContact("addressLine1", "");
    assertEquals("", contacts.getAddress().getStreetAddressLineOne());
  }

  @Test
  public void updateContactAddress1NonEmpty() {
    contacts.updateContact("addressLine1", "1 Balgay");
    assertEquals("1 Balgay", contacts.getAddress().getStreetAddressLineOne());
  }

  @Test
  public void testUpdateContactAddress1Invalid() {
    String[] result = contacts.updateContact("addressLine1", "@@@@");
    assertEquals(
        "ERROR: Invalid value @@@@. Street address line 1 must be between 1 and 100 alphanumeric characters. Spaces, commas, apostrophes, and dashes are also allowed.\n", result[0]);
    assertEquals("20 Kirkwood Ave", contacts.getAddress().getStreetAddressLineOne());
  }

  @Test
  public void updateContactAddress2Empty() {
    contacts.updateContact("addressLine2", "");
    assertEquals("", contacts.getAddress().getStreetAddressLineTwo());
  }

  @Test
  public void updateContactAddress2NonEmpty() {
    contacts.updateContact("addressLine2", "1 Balgay");
    assertEquals("1 Balgay", contacts.getAddress().getStreetAddressLineTwo());
  }

  @Test
  public void testUpdateContactsAddressLine2Invalid() {
    String[] result = contacts.updateContact("addressLine2", "@@@@");
    assertEquals("ERROR: Invalid value @@@@. Street address line 2 must be between 0 and 100 alphanumeric characters. Spaces, commas, apostrophes, and dashes are also allowed.\n", result[0]);
    assertEquals("Erskine Building", contacts.getAddress().getStreetAddressLineTwo());
  }

  @Test
  public void updateContactSuburbEmpty() {
    contacts.updateContact("suburb", "");
    assertEquals("", contacts.getAddress().getSuburbName());
  }

  @Test
  public void updateContactSuburbNonEmpty() {
    contacts.updateContact("suburb", "1 Balgay");
    assertEquals("1 Balgay", contacts.getAddress().getSuburbName());
  }

  @Test
  public void testUpdateContactsSuburbInvalid() {
    String[] result = contacts.updateContact("suburb", "@@@@");
    assertEquals("ERROR: Invalid value @@@@. Suburb must have at most 100 alphabetical characters. Spaces, commas, apostrophes, and dashes are also allowed.\n", result[0]);
    assertEquals("Ilam", contacts.getAddress().getSuburbName());
  }

  @Test
  public void updateContactsCityNameEmpty() {
    contacts.updateContact("city", "");
    assertEquals("", contacts.getAddress().getCityName());
  }

  @Test
  public void updateContactsCityNameNonEmpty() {
    contacts.updateContact("city", "1 Balgay");
    assertEquals("1 Balgay", contacts.getAddress().getCityName());
  }

  @Test
  public void testUpdateContactsCityInvalid() {
    String[] result = contacts.updateContact("city", "@@@@");
    assertEquals("ERROR: Invalid value @@@@. City name must have at most 100 alphabetical characters. Spaces, commas, apostrophes, and dashes are also allowed.\n", result[0]);
    assertEquals("Christchurch", contacts.getAddress().getCityName());
  }

  @Test
  public void updateContactsRegionEmpty() {
    contacts.updateContact("region", "");
    assertEquals("", contacts.getAddress().getRegion());
  }

  @Test
  public void updateContactRegionNonEmpty() {
    contacts.updateContact("region", "1 Balgay");
    assertEquals("1 Balgay", contacts.getAddress().getRegion());
  }

  @Test
  public void testUpdateContactsRegionInvalid() {
    String[] result = contacts.updateContact("region", "@@@@");
    assertEquals("ERROR: Invalid value @@@@. Region must have at most 100 alphabetical characters. Spaces, commas, apostrophes, and dashes are also allowed.\n", result[0]);
    assertEquals("Canterbury", contacts.getAddress().getRegion());
  }

  @Test
  public void updateContactsCountryCodeEmpty() {
    contacts.updateContact("countryCode", "");
    assertEquals("NZ", contacts.getAddress().getCountryCode());
  }

  @Test
  public void updateContactsCountryCodeNonEmpty() {
    contacts.updateContact("countryCode", "1 Balgay");
    assertEquals("NZ", contacts.getAddress().getCountryCode());
  }

  @Test
  public void testUpdateContactsCountryCodeInvalid() {
    String[] result = contacts.updateContact("countryCode", "@@@@");
    assertEquals("Invalid command", result[0]);
    assertEquals("NZ", contacts.getAddress().getCountryCode());
  }

  @Test
  public void updateContactPostCodeEmpty() {
    contacts.updateContact("postCode", "");
    assertEquals("", contacts.getAddress().getPostCode());
  }

  @Test
  public void updateContactPostCodeNonEmpty() {
    contacts.updateContact("postCode", "8080");
    assertEquals("8080", contacts.getAddress().getPostCode());
  }

  @Test
  public void testUpdateContactsPostCodeInvalid() {
    String[] result = contacts.updateContact("postCode", "@@@@");
    assertEquals("ERROR: Invalid value @@@@. Post code must be a 4 digit integer.\n", result[0]);
    assertEquals("8041", contacts.getAddress().getPostCode());
  }

  @Test
  public void setNewAddress() {
    Address newAddress = new Address("line 1", "line 2", "suburb", "city", "region", "postcode",
        "country");
    contacts.setAddress(newAddress);
    assertEquals("line 1", contacts.getAddress().getStreetAddressLineOne());
  }
}
