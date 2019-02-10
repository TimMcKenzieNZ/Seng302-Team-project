package seng302.model.utility;


import seng302.model.person.Medication;

import java.time.LocalDateTime;

import static seng302.model.person.DonorReceiver.formatDateTimeToString;


/**
 * Utility class for generating medication logs.
 */
public class MedicationLogger {
    private static final String CURRENT = "current medications";
    private static final String PREVIOUS = "previous medications";
    private static final String TIME = "Change made at: %s%n";


    private MedicationLogger() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Generates a log entry (string) for removal of given medication by given user.
     *
     * @param medication       The medication that is being removed.
     * @param userMakingChange The username of the user that removed the medication.
     * @return log entry (string).
     */
    public static String removeMedicationLogEntry(Medication medication, String userMakingChange) {
        String listType;
        if (medication.isCurrent()) {
            listType = CURRENT;
        } else {
            listType = PREVIOUS;
        }
        String log = String.format("\t'%s' removed from '%s' by '%s'. ", medication.getName(), listType, userMakingChange);
        String time = formatDateTimeToString(LocalDateTime.now());
        log += String.format(TIME, time);
        return log;
    }


    /**
     * Generates a log entry (string) for addition of given medication by given user.
     *
     * @param medication       The medication that is being added.
     * @param userMakingChange The username of the user that added the medication.
     * @return log entry (string).
     */
    public static String addMedicationLogEntry(Medication medication, String userMakingChange) {
        String listType;
        if (medication.isCurrent()) {
            listType = CURRENT;
        } else {
            listType = PREVIOUS;
        }
        String log = String.format("\t'%s' added to %s by '%s'. ", medication.getName(), listType, userMakingChange);
        String time = formatDateTimeToString(LocalDateTime.now());
        log += String.format(TIME, time);
        return log;
    }


    /**
     * Generates a log entry (string) for change of status(current/previous) of given medication by given user.
     *
     * @param medication       The medication that is being that's status has changed.
     * @param userMakingChange The username of the user that changed the medication.
     * @return log entry (string).
     */
    public static String changeMedicationLogEntry(Medication medication, String userMakingChange) {
        String oldValue;
        String newValue;
        if (medication.isCurrent()) {
            oldValue = PREVIOUS;
            newValue = CURRENT;
        } else {
            oldValue = CURRENT;
            newValue = PREVIOUS;
        }
        String log = String.format("\t'%s' changed from '%s' to '%s' by '%s'. ", medication.getName(), oldValue, newValue, userMakingChange);
        String time = formatDateTimeToString(LocalDateTime.now());
        log += String.format(TIME, time);
        return log;
    }
}
