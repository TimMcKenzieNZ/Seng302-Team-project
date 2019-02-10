package seng302.model.patches;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng302.model.StringExtension;
import seng302.model.person.UserValidator;

/**
 * S Stores the Donor's attributes such as height, weight, and current address.
 */
public class UserAttributeCollectionPatch {

    /**
     * The current height of the Donor (in meters).
     */
    private Object height;

    /**
     * The current weight of the Donor (in kilograms).
     */
    private Object weight;

    /**
     * A string of the Donor's blood type. It is a code beginning with {A, B, AB, O} followed by
     * either a '-' or '+' symbol.
     */
    private String bloodType;


    /**
     * The blood pressure of the donor expressed as a String object showing systolic pressure over
     * diastolic pressure.
     */
    private String bloodPressure;

    /**
     * Whether or not the donor is a smoker.
     */
    private Boolean smoker;

    /**
     * The alcohol consumption of the donor expressed in standard drinks per week(a standard drink
     * contains 10 grams of alcohol).
     */
    private double alcoholConsumption;

    /**
     * A string used to store any chronic diseases the donor suffers from.
     */
    private String chronicDiseases;

    /**
     * A boolean which is 'true' if the donor has a BMI too high to make them eligible, 'false'
     * otherwise. In the public health system, a BMI > 30 is considered obese. The BMI is calculated
     * from the given height and weight.
     */
    private boolean bodyMassIndexFlag;

    private double bmi;

    private String version;

    /**
     * Constructor for a blank UserAttributeCollection.
     */
    public UserAttributeCollectionPatch() {
    }

    /**
     * Constructor for a complete UserAttributeCollection with json.
     *
     * @param height The height of the donor in meters
     * @param weight the weight of the donor in kgs
     * @param bloodType the bloodType of the donor
     * @param bmi the bmi of the account holder
     * @param smoker Whether or not the donor is a smoker
     * @param bloodPressure The donor's blood pressure
     * @param alcoholConsumption The alcohol consumption of the donor
     * @param chronicDiseases The chronic diseases the donor has
     */
    @JsonCreator
    public UserAttributeCollectionPatch(@JsonProperty("height") Object height,
                                   @JsonProperty("weight") Object weight,
                                   @JsonProperty("bloodType") String bloodType,
                                   @JsonProperty("bodyMassIndexFlag") boolean bmi,
                                   @JsonProperty("smoker") Boolean smoker,
                                   @JsonProperty("bloodPressure") String bloodPressure,
                                   @JsonProperty("alcoholConsumption") Double alcoholConsumption,
                                   @JsonProperty("chronicDiseases") String chronicDiseases,
                                   @JsonProperty("version") String version) {
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        bodyMassIndexFlag = bmi;
        this.smoker = smoker;
        this.alcoholConsumption = alcoholConsumption;
        this.bloodPressure = bloodPressure;
        this.chronicDiseases = chronicDiseases;
        if (bloodPressure == null) {
            this.bloodPressure = "0/0";
        } else {
            this.bloodPressure = bloodPressure;
        }
        if (chronicDiseases == null) {
            this.chronicDiseases = "";
        } else {
            this.chronicDiseases = chronicDiseases;
        }
        this.version = version;
    }

    public Object getHeight() {
        return height;
    }

    public void setHeight(Object height) {
        this.height = height;
    }

    public Object getWeight() {
        return weight;
    }

    public void setWeight(Object weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Boolean getSmoker() {
        return smoker;
    }

    public void setSmoker(Boolean smoker) {
        this.smoker = smoker;
    }

    public double getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(double alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public boolean isBodyMassIndexFlag() {
        return bodyMassIndexFlag;
    }

    public void setBodyMassIndexFlag(boolean bodyMassIndexFlag) {
        this.bodyMassIndexFlag = bodyMassIndexFlag;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}


