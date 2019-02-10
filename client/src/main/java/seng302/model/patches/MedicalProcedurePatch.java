package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Medical Procedure
 */
public class MedicalProcedurePatch {

    private String summary;
    private String description;
    private LocalDate date;

    public ArrayList<String> getAffectedOrgans() {
        return affectedOrgans;
    }

    public void setAffectedOrgans(ArrayList<String> affectedOrgans) {
        this.affectedOrgans = affectedOrgans;
    }

    private ArrayList<String> affectedOrgans;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOldSummary() {
        return oldSummary;
    }

    public void setOldSummary(String oldSummary) {
        this.oldSummary = oldSummary;
    }

    private String version;
    private String oldSummary;
    private static ArrayList<String> organNames = new ArrayList<>(
            Arrays.asList("Liver", "Kidney", "Pancreas", "Heart",
                    "Lung", "Intestines", "Cornea", "Middle Ear", "Skin", "Bone", "Bone Marrow",
                    "Connective Tissue"));
    public static String invalidDateErrorMessage = "Invalid date";
    public static String nullDOBErrorMessage = "DOB cannot be null";
    public static String procedureDateTooEarlyErrorMessage = "Date of procedure more than 12 months before DOB";
    public static String invalidOrgansErrorMessage = "Invalid organ names";


    /**
     * The constructor for a medical procedure object using JSON.
     *
     * @param summary A string containing a short summary of the procedure.
     * @param description A string containing a longer description of the procedure.
     * @param date The LocalDate on which the procedure was/will be performed.
     * @param affectedOrgans An ArrayList containing the names of the organs that are affected by the
     * procedure.
     */
    @JsonCreator
    public MedicalProcedurePatch(
                            @JsonProperty("oldSummary") String oldSummary,
                            @JsonProperty("summary") String summary,
                            @JsonProperty("description") String description,
                            @JsonProperty("date") LocalDate date,
                            @JsonProperty("affectedOrgans") ArrayList<String> affectedOrgans,
                            @JsonProperty("version") String version) {
        this.oldSummary = oldSummary;
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.affectedOrgans = affectedOrgans;
        this.version = version;
    }

    /**
     * The getter method for the summary of the procedure.
     *
     * @return The summary of the procedure.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * The setter method for the summary of the procedure.
     *
     * @param summary The summary that the procedure will be set to have.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * The getter method for the description of the procedure.
     *
     * @return The description of the procedure.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The setter method for the description of the procedure.
     *
     * @param description The description that the procedure will be set to have.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The getter method for the date of the procedure.
     *
     * @return The date of the procedure.
     */
    public LocalDate getDate() {
        return date;
    }
}
