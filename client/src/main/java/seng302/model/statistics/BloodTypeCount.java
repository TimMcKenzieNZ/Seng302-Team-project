package seng302.model.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BloodTypeCount {

  public Integer aPos = 0;
  public Integer aNeg = 0;
  public Integer bPos = 0;
  public Integer bNeg = 0;
  public Integer aBPos = 0;
  public Integer aBNeg = 0;
  public Integer oPos = 0;
  public Integer oNeg = 0;


  @JsonCreator
  public BloodTypeCount (
      @JsonProperty("aPos") Integer aPos,
      @JsonProperty("aNeg") Integer aNeg,
      @JsonProperty("bPos") Integer bPos,
      @JsonProperty("bNeg") Integer bNeg,
      @JsonProperty("aBPos") Integer aBPos,
      @JsonProperty("aBNeg") Integer aBNeg,
      @JsonProperty("oPos") Integer oPos,
      @JsonProperty("oNeg") Integer oNeg) {

    this.aPos = aPos;
    this.aNeg = aNeg;
    this.bPos = bPos;
    this.bNeg = bNeg;
    this.aBPos = aBPos;
    this.aBPos = aBNeg;
    this.oPos = oPos;
    this.oNeg = oNeg;
  }

  public BloodTypeCount() {}
}
