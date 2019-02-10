package seng302.model.person;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class PersonTest {

  Person p;
  Person p2;
  Person p3;
  ContactDetails contacts;
  ContactDetails contacts2 = new ContactDetails(null, "0273669919", null, null);

  @Before
  public void setUp() {
    p = new Person("Bill", "", "Gates", contacts);
    p2 = new Person("Steve", "Paul", "Jobs", contacts);
    p3 = new Person("Trump", "", "", null);
  }

  @Test
  public void constructorTest() {
    assertEquals(p.getFirstName(), "Bill");
    assertEquals(p.getLastName(), "Gates");
    assertEquals(p.getMiddleName(), "");

    assertEquals(p2.getFirstName(), "Steve");
    assertEquals(p2.getLastName(), "Jobs");
    assertEquals(p2.getMiddleName(), "Paul");

    assertEquals("", p3.getContactDetails().getEmail());
    assertEquals("", p3.getContactDetails().getHomeNum());
    assertEquals("", p3.getContactDetails().getMobileNum());
    assertEquals("", p3.getContactDetails().getAddress().getRegion());
    assertEquals("", p3.getContactDetails().getAddress().getStreetAddressLineOne());
    assertEquals("", p3.getContactDetails().getAddress().getPostCode());
    assertEquals("", p3.getContactDetails().getAddress().getCityName());
    assertEquals("", p3.getContactDetails().getAddress().getStreetAddressLineTwo());
    assertEquals("", p3.getContactDetails().getAddress().getCountryCode());
    assertEquals("", p3.getContactDetails().getAddress().getSuburbName());
  }

  @Test
  public void fullNameTest() {
    assertEquals("Steve Paul Jobs", p2.fullName());
    assertEquals("Bill Gates", p.fullName());
    assertEquals("Trump", p3.fullName());
  }

  @Test
  public void changeDataTest() {
    p.setFirstName("Apple");
    p.setMiddleName("Steve");
    p.setLastName("Windows");
    p.setContactDetails(contacts2);

    assertEquals("Apple", p.getFirstName());
    assertEquals("Steve", p.getMiddleName());
    assertEquals("Windows", p.getLastName());
    assertEquals("0273669919", p.getContactDetails().getMobileNum());

  }
}
