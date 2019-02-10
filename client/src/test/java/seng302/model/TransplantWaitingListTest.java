package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

public class TransplantWaitingListTest {


  @Test
  public void updateAccountReceivingTest() {
    DonorReceiver donorReceiver = new DonorReceiver("Steve", "Paul", "Jobs", LocalDate.now(),
        "AAA1111");
    assertFalse(donorReceiver.getReceiver());
    donorReceiver.updateOrgan(donorReceiver, "receiver", "liver", "true");
    TransplantWaitingList.updateAccountReceiving(donorReceiver);
    assertTrue(donorReceiver.getReceiver());
  }


  @Test
  public void formatDateToStringTest() {
    LocalDate localDate = LocalDate.of(2018, 5, 13);
    String date = TransplantWaitingList.formatDateToString(localDate);
    assertEquals("20180513", date);


  }

  @Test
  public void formatCreationDateTest() {
    LocalDateTime localDateTime = LocalDateTime.of(2018, 5, 13, 12, 30);
    String date = TransplantWaitingList.formatCreationDate(localDateTime);
    assertEquals("13-05-2018 12:30:00", date);

    localDateTime = null;
    date = TransplantWaitingList.formatCreationDate(localDateTime);
    assertEquals("", date);
  }



  @Test
  public void organConversionTest() {
    String organ = TransplantWaitingList.organConversion("middle ear");
    assertEquals("middleEars", organ);

    organ = TransplantWaitingList.organConversion("bone marrow");
    assertEquals("boneMarrow", organ);

    organ = TransplantWaitingList.organConversion("connective tissue");
    assertEquals("connectiveTissue", organ);
  }



}
