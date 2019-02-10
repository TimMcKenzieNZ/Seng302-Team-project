package seng302.model.person;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Class to store the death details of a donor or a receiver.
 */
public class DeathDetails {

    private String region;

    private String country;

    private LocalDateTime doD;

    private String city;

    public DeathDetails() {
        this.country = "";
        this.region = "";
        this.city = "";
        this.doD = null;
    }


    /**
     * Default constructor for Death Details.
     * @param country String of the country of death
     * @param region String of the region of death
     * @param city String of the city of death
     * @param doD LocalDateTime of the date and time of death
     */
    @JsonCreator
    public DeathDetails(@JsonProperty("country") String country,
                        @JsonProperty("region") String region,
                        @JsonProperty("city") String city,
                        @JsonProperty("doD") LocalDateTime doD) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.doD = doD;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public LocalDateTime getDoD() {
        return doD;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDoD(LocalDateTime doD) {
        this.doD = doD;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void clear() {
        region = "";
        country = "";
        doD = null;
        city = "";
    }

    public void update(String country, String region, String city, LocalDateTime doD) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.doD = doD;
    }


}
