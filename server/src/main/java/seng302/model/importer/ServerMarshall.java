package seng302.model.importer;

import org.springframework.beans.BeanUtils;
import seng302.model.person.DonorReceiver;


public class ServerMarshall {

  private ServerMarshall() {

  }


  /**
   * Copies some of the given original user's attributes with the new user and return the merged
   * user. This is for .csv imported users who are missing certain attributes and are duplicates of
   * existing users in the database.
   *
   * @param original the original existing DonorReceiver in the database.
   * @param nova the new DonorReceiver who is the recipient of the original user's attributes.
   * @return the merged DonorReceiver with copied attributes
   */
  public static DonorReceiver replaceOriginalDonorWithNewDonor(DonorReceiver original, DonorReceiver nova) {
    try {
      DonorReceiver combinedDonor = new DonorReceiver(null,null,null,null,null);
      BeanUtils.copyProperties(original, combinedDonor);
      BeanUtils.copyProperties(nova, combinedDonor);
      return combinedDonor;
    }
    catch (Exception e) {
      return null;
    }

  }
}
