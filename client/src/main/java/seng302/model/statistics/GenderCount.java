package seng302.model.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenderCount {

  public int maleCount = 0;
  public int femaleCount = 0;
  public int otherCount = 0;
  public int unspecifiedCount = 0;

  @JsonCreator
  public GenderCount(
      @JsonProperty("maleCount") Integer maleCount,
      @JsonProperty("femaleCount") Integer femaleCount,
      @JsonProperty("otherCount") Integer otherCount,
      @JsonProperty("unspecifiedCount") Integer unspecifiedCount
  ) {
    this.maleCount = maleCount;
    this.femaleCount = femaleCount;
    this.otherCount = otherCount;
    this.unspecifiedCount = unspecifiedCount;
  }

  public GenderCount() {}

}
