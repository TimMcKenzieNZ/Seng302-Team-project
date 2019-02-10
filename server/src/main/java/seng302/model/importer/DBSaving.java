package seng302.model.importer;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import seng302.model.*;
import seng302.model.DonorOrganInventory;
import seng302.model.ReceiverOrganInventory;
import seng302.model.person.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBSaving {

  private StringBuilder userExporterString = new StringBuilder();
  private StringBuilder donorExporterString = new StringBuilder();
  private StringBuilder clinicianExporterString = new StringBuilder();
  private StringBuilder adminExporterString = new StringBuilder();
  private StringBuilder logEntryExporterString = new StringBuilder();

  private StringBuilder contactDetailsExporterString = new StringBuilder();
  private StringBuilder medicalProceduresExporterString = new StringBuilder();
  private StringBuilder illnessExporterString = new StringBuilder();
  private StringBuilder currentMedicationsExporterString = new StringBuilder();
  private StringBuilder previousMedicationsExporterString = new StringBuilder();
  private StringBuilder medicationLogExporterString = new StringBuilder();
  private StringBuilder affectedOrgansExporterString = new StringBuilder();

  public void exportDonorToDatabase(Connection connection, DonorReceiver donorReceiver) throws SQLException{
    String insertString = "INSERT INTO ";

    StringBuilder[] typeStrings = {userExporterString, donorExporterString, logEntryExporterString,
        contactDetailsExporterString, medicalProceduresExporterString, illnessExporterString,
      currentMedicationsExporterString, previousMedicationsExporterString,
        medicalProceduresExporterString, medicationLogExporterString, affectedOrgansExporterString};

    userExporterString.append(insertString).append("Users (`username`, `active`, "
        + "`creationDate`, `firstName`, `middleName`, `lastName`, `password`, `version`, `userType`) VALUES ");

    donorExporterString.append(insertString)
        .append("DonorReceivers (`username`, ").append(
        "`preferredName`, `livedInUKFlag`, `activeFlag`, `dateOfDeath`, ")
        .append("`dateOfBirth`, `birthGender`, `title`, `gender`, `height`, `weight`, `bloodType`, ")
        .append("`bloodPressure`, `smoker`, `alcoholConsumption`, `bodyMassIndexFlag`, `dLiver`, ")
        .append("`dKidneys`, `dPancreas`, `dHeart`, `dLungs`, `dIntestine`, `dCorneas`, ")
        .append("`dMiddleEars`, `dSkin`, `dBone`, `dBoneMarrow`, `dConnectiveTissue`, ")
        .append("`rLiver`, `rKidneys`, `rPancreas`, `rHeart`, `rLungs`, `rIntestine`, ")
        .append("`rCorneas`, `rMiddleEars`, `rSkin`, `rBone`, `rBoneMarrow`, `rConnectiveTissue`, ")
        .append("`rTimeLiver`, `rTimeKidneys`, `rTimePancreas`, `rTimeHeart`, ")
        .append("`rTimeLungs`, `rTimeIntestine`, `rTimeCorneas`, `rTimeMiddleEars`, ")
        .append("`rTimeSkin`, `rTimeBone`, `rTimeBoneMarrow`, `rTimeConnectiveTissue`) VALUES ");

    logEntryExporterString.append(insertString)
        .append("LogEntries (`username`, ")
        .append("`valChanged`, `changeTime`, `modifyingAccount`, `accountModified`, ")
        .append("`originalVal`, `changedVal`) VALUES ");

    contactDetailsExporterString.append(insertString)
        .append("ContactDetails (")
        .append("`mobileNumber`, `username`, `homeNumber`, `email`, `streetAddressLineOne`, ")
        .append("`streetAddressLineTwo`, `suburb`, `city`, `region`, `postCode`, `countryCode`, ")
        .append("`emergency`) VALUES ");

    medicalProceduresExporterString.append(insertString)
        .append("MedicalProcedures ")
        .append("(`summary`, `date`, `username`, `description`) VALUES ");

    illnessExporterString.append(insertString)
        .append("Illnesses (`username`, `name`, ")
        .append("`date`, `cured`, `chronic`) VALUES ");

    currentMedicationsExporterString.append(insertString)
        .append("CurrentMedications (").append("`username`, `medication`) VALUES ");

    previousMedicationsExporterString.append(insertString)
        .append("PreviousMedications (").append("`username`, `medication`) VALUES ");

    medicationLogExporterString.append(insertString)
        .append("MedicationLogs (").append("`username`, `medicationLog`) VALUES ");

    affectedOrgansExporterString.append(insertString)
        .append("AffectedOrgans (`username`, ")
        .append("`affectedOrganName`, `summary`, `date`) VALUES ");

    exportUsers(donorReceiver);
    exportDonorReceiver(donorReceiver);
    exportContactDetails(donorReceiver);

    try (Statement statement = connection.createStatement()) {
      for (StringBuilder sqlStringBuilder : typeStrings) {
        String sqlString = sqlStringBuilder.substring(0, sqlStringBuilder.length() - 1) + ";";
        if (sqlString.substring(sqlString.length() - 7).equals("VALUES;")) {
          continue;
        }
        statement.addBatch(sqlString);
      }

      int[] results = statement.executeBatch();
      for(int i:results) {
        if(i < 0) {
          throw new SQLException("Couldn't add donor to database.");
        }
      }

    }

  }

  /**
   * Adds the basic user to the user string to be added to the database
   * @param user The user to be saved to the database.
   */
  private void exportUsers(User user) {
    String creationDateString = DonorReceiver.formatDateTimeToString(user.getCreationDate());
    userExporterString.append("('")
        .append(user.getUserName()).append("', ")
        .append(user.isActive()).append(", '")
        .append(creationDateString).append("', '")
        .append(user.getFirstName()).append("', '")
        .append(user.getMiddleName()).append("', '")
        .append(user.getLastName()).append("', '")
        .append(user.getPassword()).append("', '")
        .append(user.getVersion()).append("', '")
        .append("donor").append("'),");
  }

  private String handleNullDate(LocalDate date) {
    if(date == null) {
      return "NULL";
    }
    return String.format("'%s'",date.toString());
  }

  private String handleNullDateTime(LocalDateTime date) {
    if(date == null) {
      return "NULL";
    }
    return String.format("'%s'",date.toString());
  }


  /**
   * Exports the donorReceiver to the database
   * @param user The donor / receiver to be saved to the database
   */
  private void exportDonorReceiver(DonorReceiver user) {
    UserAttributeCollection attributeCollection = user.getUserAttributeCollection();
    DonorOrganInventory dOrganInventory = user.getDonorOrganInventory();
    ReceiverOrganInventory rOrganInventory = user.getRequiredOrgans();
    donorExporterString
        .append("('").append(user.getUserName())
        .append("', '").append(user.getPreferredName())
        .append("', ").append(user.getLivedInUKFlag() ? 1 : 0)
        .append(", ").append(user.getActiveFlag() ? 1 : 0)
        .append(", ").append("NULL")
//        .append(", ").append(handleNullDate(user.getDateOfDeath()))
        .append(", '").append(user.getDateOfBirth())
        .append("', '").append(user.getBirthGender())
        .append("', '").append(user.getTitle())
        .append("', '").append(user.getGender())
        .append("', '").append(attributeCollection.getHeight())
        .append("', '").append(attributeCollection.getWeight())
        .append("', '").append(attributeCollection.getBloodType())
        .append("', '").append(attributeCollection.getBloodPressure())
        .append("', '").append(attributeCollection.getSmoker() ? 1 : 0)
        .append("', '").append(attributeCollection.getAlcoholConsumption())
        .append("', '").append(attributeCollection.getBodyMassIndexFlag() ? 1 : 0)
        .append("', '").append(dOrganInventory.getLiver() ? 1 : 0)
        .append("', '").append(dOrganInventory.getKidneys() ? 1 : 0)
        .append("', '").append(dOrganInventory.getPancreas() ? 1 : 0)
        .append("', '").append(dOrganInventory.getHeart() ? 1 : 0)
        .append("', '").append(dOrganInventory.getLungs() ? 1 : 0)
        .append("', '").append(dOrganInventory.getIntestine() ? 1 : 0)
        .append("', '").append(dOrganInventory.getCorneas() ? 1 : 0)
        .append("', '").append(dOrganInventory.getMiddleEars() ? 1 : 0)
        .append("', '").append(dOrganInventory.getSkin() ? 1 : 0)
        .append("', '").append(dOrganInventory.getBone() ? 1 : 0)
        .append("', '").append(dOrganInventory.getBoneMarrow() ? 1 : 0)
        .append("', '").append(dOrganInventory.getConnectiveTissue() ? 1 : 0)
        .append("', '").append(rOrganInventory.getLiver() ? 1 : 0)
        .append("', '").append(rOrganInventory.getKidneys() ? 1 : 0)
        .append("', '").append(rOrganInventory.getPancreas() ? 1 : 0)
        .append("', '").append(rOrganInventory.getHeart() ? 1 : 0)
        .append("', '").append(rOrganInventory.getLungs() ? 1 : 0)
        .append("', '").append(rOrganInventory.getIntestine() ? 1 : 0)
        .append("', '").append(rOrganInventory.getCorneas() ? 1 : 0)
        .append("', '").append(rOrganInventory.getMiddleEars() ? 1 : 0)
        .append("', '").append(rOrganInventory.getSkin() ? 1 : 0)
        .append("', '").append(rOrganInventory.getBone() ? 1 : 0)
        .append("', '").append(rOrganInventory.getBoneMarrow() ? 1 : 0)
        .append("', '").append(rOrganInventory.getConnectiveTissue() ? 1 : 0)
        .append("', ").append(handleNullDateTime(rOrganInventory.getLiverTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getKidneysTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getPancreasTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getHeartTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getLungsTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getIntestineTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getCorneasTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getMiddleEarsTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getSkinTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getBoneTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getBoneMarrowTimeStamp()))
        .append(", ").append(handleNullDateTime(rOrganInventory.getConnectiveTissueTimeStamp())).append("),");

    for (LogEntry logEntry : user.getUpdateLog()) {
      logEntryExporterString.append("('").append(user.getUserName()).append("', '")
          .append(logEntry.getValChanged()).append("', '").append(logEntry.getChangeTime())
          .append("', '").append(logEntry.getModifyingAccount()).append("', '")
          .append(logEntry.getAccountModified()).append("', '").append(logEntry.getOriginalVal())
          .append("', '").append(logEntry.getChangedVal()).append("'),");
    }
    exportEmergencyContactDetails(user);
    exportMedicalProcedures(user);
    exportIllnesses(user);
    exportCurrentMedication(user);
    exportPastMedications(user);
    exportMedicationLog(user);
  }


  /**
   * Export a clinician to the database
   * @param user The clinician to be exported.
   */
  private void exportClinician(Clinician user) {
    clinicianExporterString.append("('").append(user.getUserName()).append("'),");
  }


  /**
   * Export an administrator to the database
   * @param user The administrator to be exported
   */
  private void exportAdministrator(Administrator user) {
    adminExporterString.append("('").append(user.getUserName()).append("'),");
  }


  /**
   * Exports the contact details to the database
   * @param user The user whose contact information is being exported
   */
  private void exportContactDetails(User user) {
    ContactDetails contactDetails = user.getContactDetails();
    contactDetailsExporterString.append("('").append(contactDetails.getMobileNum()).append("', '")
        .append(user.getUserName()).append("', '").append(contactDetails.getHomeNum())
        .append("', '").append(contactDetails.getEmail()).append("', '")
        .append(contactDetails.getAddress().getStreetAddressLineOne()).append("', '")
        .append(contactDetails.getAddress().getStreetAddressLineTwo()).append("', '")
        .append(contactDetails.getAddress().getSuburb()).append("', '")
        .append(contactDetails.getAddress().getCity()).append("', '")
        .append(contactDetails.getAddress().getRegion()).append("', '")
        .append(contactDetails.getAddress().getPostCode()).append("', '")
        .append(contactDetails.getAddress().getCountryCode()).append("', '").append(0) // 0 refers to contact details not being emergency
        .append("'),");
  }


  /**
   * Exports the emergency contact details to the database
   * @param user The user whose emergency contact details are being added to the database
   */
  private void exportEmergencyContactDetails(DonorReceiver user) {
    ContactDetails contactDetails = user.getEmergencyContactDetails();
    contactDetailsExporterString.append("('").append(contactDetails.getMobileNum()).append("', '")
        .append(user.getUserName()).append("', '").append(contactDetails.getHomeNum())
        .append("', '").append(contactDetails.getEmail()).append("', '")
        .append(contactDetails.getAddress().getStreetAddressLineOne()).append("', '")
        .append(contactDetails.getAddress().getStreetAddressLineTwo()).append("', '")
        .append(contactDetails.getAddress().getSuburb()).append("', '")
        .append(contactDetails.getAddress().getCity()).append("', '")
        .append(contactDetails.getAddress().getRegion()).append("', '")
        .append(contactDetails.getAddress().getPostCode()).append("', '")
        .append(contactDetails.getAddress().getCountryCode()).append("', ").append(1) // 1 refers to contact details being emergency
        .append("),");
  }


  /**
   * Exports medical procedures to the database
   * @param user The user whose medical procedures are to be added to the database
   */
  private void exportMedicalProcedures(DonorReceiver user) {
    List<MedicalProcedure> medicalProcedures = user.getMedicalProcedures();
    for (MedicalProcedure medicalProcedure : medicalProcedures) {
      medicalProceduresExporterString.append("('").append(medicalProcedure.getSummary())
          .append("', '").append(medicalProcedure.getDate()).append("', '")
          .append(user.getUserName()).append("', '").append(medicalProcedure.getDescription())
          .append("'),");
      for (String affectedOrgan : medicalProcedure.getAffectedOrgans()) {
        affectedOrgansExporterString.append("('").append(user.getUserName()).append("', '")
            .append(affectedOrgan).append("', '").append(medicalProcedure.getSummary())
            .append("', '").append(medicalProcedure.getDate()).append("'),");
      }
    }
  }


  /**
   * Exporting the illnesses to the database
   * @param user The user to be exported to the database.
   */
  private void exportIllnesses(DonorReceiver user) {
    List<Illness> masterIllnessList = user.getMasterIllnessList();
    for (Illness illness : masterIllnessList) {
      illnessExporterString.append("('").append(user.getUserName()).append("', '")
          .append(illness.getName()).append("', '").append(DonorReceiver.formatDateToString(illness.getDate()))
          .append("', '")
          .append(illness.isCured() ? 1:0).append("', '").append(illness.isChronic() ? 1:0).append("'),");
    }
  }


  /**
   * Exports the current medications of a given user
   * @param user The user to be exported
   */
  private void exportCurrentMedication(DonorReceiver user) {
    ArrayList<String> currentMedications = user.getMedications().getCurrentMedications();
    for (String medication: currentMedications) {
      currentMedicationsExporterString.append("('").append(user.getUserName()).append("', '")
          .append(medication).append("'),");
    }
  }


  /**
   * Exports the past medications of a given user
   * @param user The user whose past medications are being exported
   */
  private void exportPastMedications(DonorReceiver user) {
    ArrayList<String> pastMedications = user.getMedications().getPreviousMedications();
    for (String medication : pastMedications) {
      previousMedicationsExporterString.append("('").append(user.getUserName()).append("', '")
          .append(medication).append("'),");
    }
  }


  /**
   * Exports the medication log of a given user
   * @param user The user who's medication logs are being exported
   */
  private void exportMedicationLog(DonorReceiver user) {
    List<String> medicationLogs = user.getMedications().getMedicationLog();
    for (String medicationLog : medicationLogs) {
      medicationLogExporterString.append("('").append(user.getUserName()).append("', '")
          .append(medicationLog).append("'),");
    }
  }
}