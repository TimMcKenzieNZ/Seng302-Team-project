package seng302.model;

import static seng302.controllers.BaseController.createBadPopUp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.Pagination;
import seng302.App;
import seng302.model.enums.ADDRESSES;
import seng302.model.patches.ReceiverOrganInventoryPatch;
import seng302.model.person.DeathDetails;
import seng302.model.person.DonorReceiver;
import seng302.services.PatchTask;
import seng302.services.PostTask;

/**
 * Contains the model methods for transplantWaitingListController
 */
public class TransplantWaitingList {
  public static Session session = App.getCurrentSession();
  public static int CURRENT_VERSION;

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
   * Creates a log and updates the receivers account to reflect that the organ being placed on the
   * waiting list was a mistake
   *
   * @param record The record of the organ on the waiting list
   * @param account The account being changed
   */
  public static void removeOrganMistake(ReceiverRecord record,
      DonorReceiver account, boolean cured) {
    String organ = record.getOrgan();
    organ = organConversion(organ);

    ReceiverOrganInventory receiverOrganInventory = account.getRequiredOrgans();
    CURRENT_VERSION = account.getVersion() + 1;
    String currentVersion;
    if (cured) {
      currentVersion = String.format("T:C:%d", CURRENT_VERSION);
    } else {
      currentVersion = String.format("T:M:%d", CURRENT_VERSION);
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    ReceiverOrganInventoryPatch receiverOrganInventoryPatch = new ReceiverOrganInventoryPatch(receiverOrganInventory.getLiver(),
            receiverOrganInventory.getKidneys(),receiverOrganInventory.getPancreas(),receiverOrganInventory.getHeart(),receiverOrganInventory.getLungs(),
            receiverOrganInventory.getIntestine(),receiverOrganInventory.getCorneas(),receiverOrganInventory.getMiddleEars(),
            receiverOrganInventory.getSkin(), receiverOrganInventory.getBone(), receiverOrganInventory.getBoneMarrow(), receiverOrganInventory.getConnectiveTissue(),
            receiverOrganInventory.getLiverTimeStamp().format(formatter), receiverOrganInventory.getKidneysTimeStamp().format(formatter), receiverOrganInventory.getPancreasTimeStamp().format(formatter),
            receiverOrganInventory.getHeartTimeStamp().format(formatter), receiverOrganInventory.getLungsTimeStamp().format(formatter), receiverOrganInventory.getIntestineTimeStamp().format(formatter),
            receiverOrganInventory.getCorneasTimeStamp().format(formatter), receiverOrganInventory.getMiddleEarsTimeStamp().format(formatter), receiverOrganInventory.getSkinTimeStamp().format(formatter),
            receiverOrganInventory.getBoneTimeStamp().format(formatter), receiverOrganInventory.getBoneMarrowTimeStamp().format(formatter), receiverOrganInventory.getConnectiveTissueTimeStamp().format(formatter),
            currentVersion
            );

    receiverOrganInventoryPatch.setOrgan(organ);

    updateDatabase(account.getUserName(), receiverOrganInventoryPatch);
  }

  /**
   * updates the db.
   * @param nhi the nhi of the donor/receiver
   * @param receiverOrganInventory the inventory
   */
  private static void updateDatabase(String nhi, ReceiverOrganInventoryPatch receiverOrganInventory) {
    PatchTask manyTask = new PatchTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.PATCH_RECEIVER_ORGANS.getAddress(), nhi), receiverOrganInventory, session.getToken());
    manyTask.setOnSucceeded(event -> {
      String[] reason = receiverOrganInventory.getVersion().split(":");
      if (reason[1].equalsIgnoreCase("m")) {
        AccountManager.getStatusUpdates().add("User " + nhi + "'s entry removed from transplant waiting list.");
      } else if (reason[1].equalsIgnoreCase("c")) {
        AccountManager.getStatusUpdates().add("User " + nhi + " cured, organ removed from transplant waiting list.");
      } else if (reason[1].equalsIgnoreCase("d")) {
        AccountManager.getStatusUpdates().add("User " + nhi + " died, removed from transplant waiting list.");
      }
      PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
      PageNav.loaded();
    });
    manyTask.setOnFailed(event -> {
      createBadPopUp();
      throw new IllegalArgumentException("Failed to remove record");
    });
      new Thread(manyTask).start();
  }


  public static void removeOrganDeceased(DonorReceiver account) {

      ReceiverOrganInventory receiverOrganInventory = account.getRequiredOrgans();
      CURRENT_VERSION = account.getVersion() + 1;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS");
      String currentVersion = String.format("T:D:%d", CURRENT_VERSION);
      ReceiverOrganInventoryPatch receiverOrganInventoryPatch = new ReceiverOrganInventoryPatch(receiverOrganInventory.getLiver(),
              receiverOrganInventory.getKidneys(),receiverOrganInventory.getPancreas(),receiverOrganInventory.getHeart(),receiverOrganInventory.getLungs(),
              receiverOrganInventory.getIntestine(),receiverOrganInventory.getCorneas(),receiverOrganInventory.getMiddleEars(),
              receiverOrganInventory.getSkin(), receiverOrganInventory.getBone(), receiverOrganInventory.getBoneMarrow(), receiverOrganInventory.getConnectiveTissue(),
              receiverOrganInventory.getLiverTimeStamp().format(formatter), receiverOrganInventory.getKidneysTimeStamp().format(formatter), receiverOrganInventory.getPancreasTimeStamp().format(formatter),
              receiverOrganInventory.getHeartTimeStamp().format(formatter), receiverOrganInventory.getLungsTimeStamp().format(formatter), receiverOrganInventory.getIntestineTimeStamp().format(formatter),
              receiverOrganInventory.getCorneasTimeStamp().format(formatter), receiverOrganInventory.getMiddleEarsTimeStamp().format(formatter), receiverOrganInventory.getSkinTimeStamp().format(formatter),
              receiverOrganInventory.getBoneTimeStamp().format(formatter), receiverOrganInventory.getBoneMarrowTimeStamp().format(formatter), receiverOrganInventory.getConnectiveTissueTimeStamp().format(formatter),
              currentVersion
      );

      receiverOrganInventoryPatch.setOrgan("all");

      updateDatabase(account.getUserName(), receiverOrganInventoryPatch);

  }

  public static void updateDateOfDeath(DonorReceiver account, DeathDetails deathDetails) {
      PostTask manyTask = new PostTask(String.class, ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.POST_DEATH_DETAILS.getAddress(), account.getUserName()), deathDetails, session.getToken());
      manyTask.setOnSucceeded(event -> {
          PageNav.loaded();
      });
      manyTask.setOnFailed(event -> {
          createBadPopUp();
          throw new IllegalArgumentException("Failed to set death details");
      });
      new Thread(manyTask).start();
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
