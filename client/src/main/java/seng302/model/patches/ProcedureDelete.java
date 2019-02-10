package seng302.model.patches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcedureDelete {

    private String summary;


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }



    @JsonCreator
    public ProcedureDelete(@JsonProperty("summary") String summary) {
        this.summary = summary;
    }
}
