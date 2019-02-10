package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * The LogEntry class is intended to make the logs associated with accounts consistent, and enable
 * the log string structure to be edited easily. The LogEntry class also enables the log to contain
 * both the user being modified, as well as the user doing the modifying.
 */
public class LogEntry {

  private String accountModified;
  private String modifyingAccount;
  private String valChanged;
  private String originalVal;
  private String changedVal;
  private LocalDateTime changeTime;

  /**
   * Constructor for a log entry. If the log entry is for the creation of a user, then set
   * valChanged to "created", originalVal and changedVal will be ignored. If the log entry is for
   * the deletion of a user, then set valChanged to "deleted", originalVal and changedVal will be
   * ignored.
   *
   * @param accountModified The User that is being modified (or created/deleted). SHOULD NOT BE
   * NULL, NullPointerException will be thrown if the toString method is called and accountModified
   * == null.
   * @param modifyingAccount The User that is modifying the user. SHOULD NOT BE NULL,
   * NullPointerException will be thrown if the toString method is called and modifyingAccount ==
   * null.
   * @param valChanged The name of the value which is being changed, (use "created" for user
   * creation, and "deleted" for account deletion). SHOULD NOT BE NULL, NullPointerException will be
   * thrown if the toString method is called and valChanged == null.
   * @param originalVal The original value of the attribute which was being changed.
   * @param changedVal The new value of the attribute which was being changed.
   */
  public LogEntry(User accountModified, User modifyingAccount,
      String valChanged, String originalVal, String changedVal) {
    this.accountModified = constructUserDetails(accountModified);
    this.modifyingAccount = constructUserDetails(modifyingAccount);
    this.valChanged = valChanged;
    this.originalVal = originalVal;
    this.changedVal = changedVal;
    this.changeTime = LocalDateTime.now();
  }

  /**
   * JSON Constructor for a LogEntry instance.
   *
   * @param accountModified The user being modified.
   * @param modifyingAccount The user that is modifying the user.
   * @param valChanged The name of the value which was changed.
   * @param originalVal The original value of the attribute which was changed.
   * @param changedVal The new value of the attribute which was changed.
   * @param changeTime The LocalDateTime at which the change occurred.
   */
  @JsonCreator
  public LogEntry(@JsonProperty("accountModified") String accountModified,
      @JsonProperty("modifyingAccount") String modifyingAccount,
      @JsonProperty("valChanged") String valChanged,
      @JsonProperty("originalVal") String originalVal,
      @JsonProperty("changedVal") String changedVal,
      @JsonProperty("changeTime") LocalDateTime changeTime) {
    this.accountModified = accountModified;
    this.modifyingAccount = modifyingAccount;
    this.valChanged = valChanged;
    this.originalVal = originalVal;
    this.changedVal = changedVal;
    this.changeTime = changeTime;
  }


  /**
   * Used for constructing a string of the user's name, and their identifier (and user type for
   * Clinicians and Administrators).
   *
   * @param account The account that the string is being constructed for.
   * @return A string containing the user's name, identifier, and user type (for Clinicians and
   * Administrators).
   */
  public static String constructUserDetails(User account) {
    String accountDetails = account.getFirstName();
    if (account.getMiddleName() != null && !account.getMiddleName().equals("")) {
      accountDetails += " " + account.getMiddleName();
    }
    if (account.getLastName() != null && !account.getLastName().equals("")) {
      accountDetails += " " + account.getLastName();
    }
    if (account instanceof DonorReceiver) {
      accountDetails += " (NHI: " + account.getUserName() + ")";
    } else if (account instanceof Clinician) {
      accountDetails += " (Clinician, Staff ID: " + account.getUserName() + ")";
    } else if (account instanceof Administrator) {
      accountDetails += " (Administrator, username: " + account.getUserName() + ")";
    }
    return accountDetails;
  }


  /**
   * Returns a string representation of this LogEntry.
   *
   * @return a string representation of this LogEntry.
   */
  @Override
  public String toString() {
    String logDetails = "";
    if (originalVal == null) {
      originalVal = "";
    }
    if (changedVal == null) {
      changedVal = "";
    }
    switch (valChanged) {
      case "created":
        logDetails += "User Created: " + accountModified;
        logDetails += ", Created by: " + modifyingAccount;
        logDetails += ", at " + changeTime + ".";
        break;
      case "deleted":
        logDetails += "User Deleted: " + accountModified;
        logDetails += ", Deleted by: " + modifyingAccount;
        logDetails += ", at " + changeTime + ".";
        break;
      case "import":
        logDetails += modifyingAccount + " imported " + changedVal + " at " + changeTime + ".";
        break;
      case "save":
        logDetails +=
            modifyingAccount + " " + originalVal + " " + changedVal + " at " + changeTime + ".";
        break;
      default:
        logDetails += "User Being Modified: " + accountModified;
        logDetails += ", Changed by User: " + modifyingAccount;
        logDetails +=
            ", " + valChanged + " changed from '" + originalVal + "' to '" + changedVal + "' at "
                + changeTime + ".";
        break;
    }
    return logDetails;
  }

  public String getAccountModified() {
    return accountModified;
  }

  public void setAccountModified(String accountModified) {
    this.accountModified = accountModified;
  }

  public String getModifyingAccount() {
    return modifyingAccount;
  }

  public void setModifyingAccount(String modifyingAccount) {
    this.modifyingAccount = modifyingAccount;
  }

  public String getValChanged() {
    return valChanged;
  }

  public void setValChanged(String valChanged) {
    this.valChanged = valChanged;
  }

  public String getOriginalVal() {
    return originalVal;
  }

  public void setOriginalVal(String originalVal) {
    this.originalVal = originalVal;
  }

  public String getChangedVal() {
    return changedVal;
  }

  public void setChangedVal(String changedVal) {
    this.changedVal = changedVal;
  }

  public LocalDateTime getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(LocalDateTime changeTime) {
    this.changeTime = changeTime;
  }
}
