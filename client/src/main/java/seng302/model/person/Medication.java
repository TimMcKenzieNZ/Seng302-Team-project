package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Medication implements Serializable {
    private String name;
    private boolean isCurrent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @JsonCreator
    public Medication(@JsonProperty("name") String name,
                      @JsonProperty("isCurrent") boolean isCurrent) {
        this.name = name;
        this.isCurrent = isCurrent;
    }
}
