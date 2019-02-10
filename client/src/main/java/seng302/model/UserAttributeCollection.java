package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng302.model.person.UserValidator;

/**
 * S Stores the Donor's attributes such as height, weight, and current address.
 */
public class UserAttributeCollection {

  /**
   * The current height of the Donor (in meters).
   */
  private double height;

  /**
   * The current weight of the Donor (in kilograms).
   */
  private double weight;

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

  /**
   * Constructor for a blank UserAttributeCollection.
   */
  public UserAttributeCollection() {
  }

  /**
   * Constructor for a complete UserAttributeCollection.
   *
   * @param height The height of the donor in meters
   * @param weight the weight of the donor in kgs
   * @param bloodType the bloodType of the donor
   * @param bmi the bmi of the account holder
   * @param bloodPressure The blood pressure of the donor represented as 'systolic
   * pressure/diastolic pressure'
   * @param smoker True if the donor is a smoker, false otherwise.
   * @param alcoholConsumption The amount of alcohol the donor usually consumes in standard drinks
   * (10 grams of alcohol) per week.
   * @param chronicDiseases Any chronic diseases the donor has.
   */
  public UserAttributeCollection(double height, double weight, String bloodType, boolean bmi,
      String bloodPressure,
      boolean smoker, double alcoholConsumption, String chronicDiseases) {
    this.height = height;
    this.weight = weight;
    this.bloodType = bloodType;
    this.bodyMassIndexFlag = bmi;
    this.smoker = smoker;
    this.alcoholConsumption = alcoholConsumption;
    this.bloodPressure = bloodPressure;
    this.chronicDiseases = chronicDiseases;
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
  public UserAttributeCollection(@JsonProperty("height") double height,
      @JsonProperty("weight") double weight,
      @JsonProperty("bloodType") String bloodType,
      @JsonProperty("bodyMassIndexFlag") boolean bmi,
      @JsonProperty("smoker") Boolean smoker,
      @JsonProperty("bloodPressure") String bloodPressure,
      @JsonProperty("alcoholConsumption") Double alcoholConsumption,
      @JsonProperty("chronicDiseases") String chronicDiseases) {
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
  }

  public boolean getBodyMassIndexFlag() {
    return bodyMassIndexFlag;
  }

  public Double getHeight() {
    return height;
  }

  public Double getWeight() {
    return weight;
  }

  public String getBloodType() {
    return bloodType;
  }

  public String getBloodPressure() {
    return bloodPressure;
  }

  public Boolean getSmoker() {
    return smoker;
  }

  public Double getAlcoholConsumption() {
    return alcoholConsumption;
  }

  public String getChronicDiseases() {
    return chronicDiseases;
  }

  public Double getBMI() {
    return bmi;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public void setBloodType(String bloodType) {
    this.bloodType = StringExtension.toUpperCase(bloodType);
  }

  public void setBloodPressure(String pressure) {
    this.bloodPressure = pressure;
  }

  public void setSmoker(boolean smoker) {
    this.smoker = smoker;
  }

  public void setAlcoholConsumption(Double alcoholConsumption) {
    this.alcoholConsumption = alcoholConsumption;
  }

  public void setChronicDiseases(String chronicDiseases) {
    this.chronicDiseases = chronicDiseases;
  }

  public void setBodyMassIndexFlag(boolean bodyMassIndexFlag) {
    this.bodyMassIndexFlag = bodyMassIndexFlag;
  }


  /**
   * Creates an ordered string of all the user's attributes if the attributes are not null.
   *
   * @return returns a string of all the user's attributes.
   */
  @Override
  public String toString() {
    String attributeString = "User Attributes:\n";
    if (height != 0.0) {
      attributeString += "\t" + String.format("Height: %sm\n", height);
    }
    if (weight != 0.0) {
      attributeString += "\t" + String.format("Weight: %skg\n", weight);
    }
    if (bloodType != null) {
      attributeString += "\t" + String.format("Blood Type: %s\n", bloodType);
    }
    if (bloodPressure != null) {
      attributeString += "\t" + "Blood Pressure: " + bloodPressure + "\n";
    }
    if (smoker != null) {
      attributeString += "\t" + "Smoker: " + smoker + "\n";
    }
    if (alcoholConsumption != 0.0) {
      attributeString += "\t" + String
          .format("Alcohol Consumption: %.2f standard drinks per week\n", alcoholConsumption);
    }
    if (chronicDiseases != null && !chronicDiseases.equals("")) {
      attributeString += "\tChronic Diseases: " + chronicDiseases + "\n";
    }
    return attributeString;
  }


  /**
   * Updates the account UserAttributeCollection height attribute if it is valid and logs the
   * result.
   *
   * @param value a string representation of the new height.
   * @return a string log of the result of the operation.
   */
  public String updateHeight(String value) {
    Double h;
    String message = "ERROR: Invalid value " + value
        + ". Height should be of type double and be between 0.0m and 4.0m.\n";
    if (!value.equals("")) {
      try {
        h = Double.parseDouble(value.trim());
      } catch (Exception e) {
        return message;
      }
      if (UserValidator.validateHeight(h)) {
        message = "updated";
        setHeight(h);
      }
    } else {
      message = "updated";
      setHeight(0.0);
    }
    return message;
  }


  /**
   * Updates the account UserAttributeCollection weight attribute if it is valid and logs the
   * result.
   *
   * @param value a string representation of the new weight.
   * @return a string log of the result of the operation.
   */
  public String updateWeight(String value) {
    Double w;
    String message = "ERROR: Invalid value " + value
        + ". Weight should be of type double and greater than 0.0kg.\n";
    if (!value.equals("")) {
      try {
        w = Double.parseDouble(value.trim());
      } catch (Exception e) {
        return message;
      }
      w = Double.parseDouble(value.trim());
      if (UserValidator.validateWeight(w)) {
        message = "updated";
        setWeight(w);
      }
    } else {
      message = "updated";
      setWeight(0.0);
    }
    return message;
  }


  /**
   * Updates the account UserAttributeCollection bloodType attribute if it is valid and logs the
   * result.
   *
   * @param value a string representation of the new bloodType.
   * @return a string log of the result of the operation.
   */
  public String updateBloodType(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Blood Type should be one of 'A-', 'A+', 'B+', 'B-', 'AB+', " +
        "'AB-', 'O+', or 'O-'.\n";
    if (UserValidator.validateBloodType(value.trim()) || value.equals("")) {
      message = "updated";
      setBloodType(value.trim());
    }
    return message;
  }


  /**
   * Updates the account UserAttributeCollection smoker attribute if it is valid and logs the
   * result.
   *
   * @param value A string representing a boolean value.
   * @return A string showing the result of the operation.
   */
  public String updateSmoker(String value) {
    String message = "ERROR: Invalid value '" + value + "'. Smoker " +
        "should be set to either 'true' or 'false'.";
    Boolean validatedValue = UserValidator.validateBoolean(value.trim());
    if (validatedValue != null) {
      message = "updated";
      setSmoker(validatedValue);
    }
    return message;
  }


  /**
   * Updates the UserAttributeCollection attribute bloodPressure if it is valid and logs the
   * result.
   *
   * @param value The new value for bloodPressure.
   * @return The result of the operation described in a String.
   */
  public String updateBloodPressure(String value) {
    String message = "ERROR: Invalid value '" + value + "'. Blood " +
        "pressure should be in the format " +
        "'<systolic pressure>/<diastolic pressure>'.";
    if (UserValidator.validateBloodPressure(value) || value.equals("")) {
      message = "updated";
      setBloodPressure(value);
    }
    return message;
  }

  /**
   * Updates the UserAttributeCollection attribute chronicDiseases and logs the result.
   *
   * @param value The new value for chronicDiseases.
   * @return The result of the update operation described in a String.
   */
  public String updateChronicDiseases(String value) {
    String message = "updated";
    setChronicDiseases(value);
    return message;
  }


  public String updateAlcoholConsumption(String value) {
    String message = "ERROR: Invalid value '" + value + "'. Alcohol " +
        "consumption should be a DOUBLE greater than or equal to 0, e.g. '3.4'.";
    if (UserValidator.validatePositiveDouble(value)) {
      message = "updated";
      setAlcoholConsumption(Double.parseDouble(value));
    }
    return message;
  }

}


