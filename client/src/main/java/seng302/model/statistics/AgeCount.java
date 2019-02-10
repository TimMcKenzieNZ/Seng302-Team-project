package seng302.model.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgeCount {

  public Integer zeroToTwelve = 0;
  public Integer twelveToTwenty = 0;
  public Integer twentyToThirty = 0;
  public Integer thirtyToForty = 0;
  public Integer fortyToFifty = 0;
  public Integer fiftyToSixty = 0;
  public Integer sixtyToSeventy = 0;
  public Integer seventyToEighty = 0;
  public Integer eightyToNinety = 0;
  public Integer ninetyPlus = 0;

  @JsonCreator
  public AgeCount (
      @JsonProperty("zeroToTwelve") Integer zeroToTwelve,
      @JsonProperty("twelveToTwenty") Integer twelveToTwenty,
      @JsonProperty("twentyToThirty") Integer twentyToThirty,
      @JsonProperty("thirtyToForty") Integer thirtyToForty,
      @JsonProperty("fortyToFifty") Integer fortyToFifty,
      @JsonProperty("fiftyToSixty") Integer fiftyToSixty,
      @JsonProperty("sixtyToSeventy") Integer sixtyToSeventy,
      @JsonProperty("seventyToEighty") Integer seventyToEighty,
      @JsonProperty("eightyToNinety") Integer eightyToNinety,
      @JsonProperty("ninetyPlus") Integer ninetyPlus) {

    this.zeroToTwelve = zeroToTwelve;
    this.twelveToTwenty = twelveToTwenty;
    this.twentyToThirty = twentyToThirty;
    this.thirtyToForty = thirtyToForty;
    this.fortyToFifty = fortyToFifty;
    this.fiftyToSixty = fiftyToSixty;
    this.sixtyToSeventy = sixtyToSeventy;
    this.seventyToEighty = seventyToEighty;
    this.eightyToNinety = eightyToNinety;
    this.ninetyPlus = ninetyPlus;
  }

  public AgeCount() {}
}
