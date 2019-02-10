package seng302.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Pagination;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

/**
 * Contains the model methods for transplantWaitingListController
 */
public class TransplantWaitingList {


  /**
   * Update the receiving status of an account by checking if they're still receiving any organs
   *
   * @param account The account we're checking
   */
  public static void updateAccountReceiving(DonorReceiver account) {
    ReceiverOrganInventory organInventory = account.getRequiredOrgans();
    account.setReceiver(false);
    boolean[] organPresent = {
        organInventory.getLiver(), organInventory.getKidneys(), organInventory.getHeart(),
        organInventory.getLungs(), organInventory.getIntestine(), organInventory.getCorneas(),
        organInventory.getMiddleEars(), organInventory.getSkin(), organInventory.getBone(),
        organInventory.getBoneMarrow(), organInventory.getConnectiveTissue(),
        organInventory.getPancreas()
    };
    for (boolean organNeeded : organPresent) {
      if (organNeeded) {
        account.setReceiver(true);
        break;
      }
    }
  }

  /**
   * Creates a date string from the given LocalDate object formatted to "CCYYMMDD".
   *
   * @param time a LocalDate object.
   * @return returns a formatted string of the form "CCYYMMDD".
   */
  public static String formatDateToString(LocalDate time) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    return time.format(dateFormat);
  }

  /**
   * Formats the creation date of an account into a readable value
   *
   * @param time The LocalDatetime to be formatted
   * @return A string of format dd-MM-yyyy HH:mm:ss which represents the creation date of the
   * account
   */
  public static String formatCreationDate(LocalDateTime time) {
    if (time == null) {
      return "";
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    String creationDate = time.format(formatter);
    return creationDate;
  }

  /**
   * Returns true is the given account is from the given region
   *
   * @param record the record to check
   * @param region the string name of the given region
   * @return true if from region, false if not
   */
  public static boolean receiverFromRegion(ReceiverRecord record, String region) {
    if (region.equalsIgnoreCase("Any Region")) {
      return true;
    } else {
      return record.getRegion().equalsIgnoreCase(region);
    }

  }





  /**
   * Generates a message which indicates the number of accounts matched in a search. The number
   * corresponds to the size of sortedRecords.
   *
   * @return A message indicating the size of sortedRecords.
   */
  public static String getMatchesMessage(SortedList<ReceiverRecord> receiverRecords,
      Pagination pageControl) {
    int number = receiverRecords.size();
    if (number == 1) {
      return "\n" +
          number + " matching records";
    } else {
      int startNumber = pageControl.getCurrentPageIndex() * 16 + 1;
      int endNumber = (pageControl.getCurrentPageIndex() + 1) * 16;
      endNumber =
          (endNumber < number) ? endNumber : number;  // Set the endNumber to the lower value
      return "Records " + startNumber + "-" + endNumber + "\n" +
          number + " matching records";
    }
  }

  /**
   * Remove spaces from organ name;
   *
   * @param organ The name of the organ to be checked for spaces.
   * @return The name of the organ without spaces.
   */
  public static String organConversion(String organ) {
    organ = organ.toLowerCase();
    switch (organ) {
      case "middle ear":
        return "middleEars";
      case "bone marrow":
        return "boneMarrow";
      case "connective tissue":
        return "connectiveTissue";
      case "intestines":
        return "intestine";
    }
    return organ;
  }

  /**
   * Returns true is the given account is a receiver for the given organ
   *
   * @param record the record to check
   * @param organ the string name of the given organ
   * @return true if receiver of organ, false if not
   */
  public static boolean receiverRequiresOrgan(ReceiverRecord record, String organ,
      String organString) {
    String[] organList = {"Liver", "Kidneys", "Heart", "Lungs", "Intestines",
        "Corneas", "Middle Ear", "Skin", "Bone", "Bone Marrow", "Connective Tissue"};
    if (organ.equalsIgnoreCase(organString)) {
      return true;
    } else {
      for (String singleOrgan : organList) {
        if ((organ.equalsIgnoreCase(singleOrgan))
            && (record.getOrgan().equalsIgnoreCase(singleOrgan))) {
          return true;
        }
      }
      return false;
    }
  }


}
