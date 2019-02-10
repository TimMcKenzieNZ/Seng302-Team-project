package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicInformationPatch {
    private String givenName;
    private String middleName;
    private String lastName;
    private String nhi;
    private String password;
    private String version;


    public BasicInformationPatch(){
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonCreator
    public BasicInformationPatch(@JsonProperty("givenName") String givenName,
                                 @JsonProperty("middleName") String middleName,
                                 @JsonProperty("lastName") String lastName,
                                 @JsonProperty("nhi") String nhi,
                                 @JsonProperty("password") String password,
                                 @JsonProperty("version") String version) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nhi = nhi;
        this.password = password;
        this.version = version;
    }
}
