package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfilePatch {
    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthGender() {
        return birthGender;
    }

    public void setBirthGender(String birthGender) {
        this.birthGender = birthGender;
    }

    public boolean isLivedInUKFlag() {
        return livedInUKFlag;
    }

    public void setLivedInUKFlag(boolean livedInUKFlag) {
        this.livedInUKFlag = livedInUKFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String preferredName;
    private String title;
    private String dateOfBirth;
    private String gender;
    private String birthGender;
    private boolean livedInUKFlag;
    private String version;


    /**
     * Profile for patch request
     * @param preferredName Preferred name of the donor
     * @param title title of the donor
     * @param dateOfBirth date of birth of the donor
     * @param gender gender of the donor
     * @param birthGender birth gender of the donor
     * @param livedInUKFlag whether the donor has lived in the uk
     * @param version version of the donor
     */
    @JsonCreator
    public ProfilePatch(@JsonProperty("preferredName") String preferredName,
                        @JsonProperty("title") String title,
                        @JsonProperty("dateOfBirth") String dateOfBirth,
                        @JsonProperty("gender") String gender,
                        @JsonProperty("birthGender") String birthGender,
                        @JsonProperty("livedInUKFlag") boolean livedInUKFlag,
                        @JsonProperty("version") String version) {
        this.preferredName = preferredName;
        this.title = title;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.birthGender = birthGender;
        this.livedInUKFlag = livedInUKFlag;
        this.version = version;
    }

    public ProfilePatch(){}
}
