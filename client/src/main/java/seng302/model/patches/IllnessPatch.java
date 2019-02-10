package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IllnessPatch {
    String date;
    String name;
    boolean cured;
    boolean chronic;
    String version;

    @JsonCreator
    public IllnessPatch(@JsonProperty("date") String date,
                        @JsonProperty("name") String name,
                        @JsonProperty("cured") boolean cured,
                        @JsonProperty("chronic") boolean chronic,
                        @JsonProperty("version") String version) {
        this.date = date;
        this.name = name;
        this.cured = cured;
        this.chronic = chronic;
        this.version = version;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCured() {
        return cured;
    }

    public void setCured(boolean cured) {
        this.cured = cured;
    }

    public boolean isChronic() {
        return chronic;
    }

    public void setChronic(boolean chronic) {
        this.chronic = chronic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
