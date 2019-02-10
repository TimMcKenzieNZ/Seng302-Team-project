package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ReceiverRecord {

  String fullName;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @JsonCreator
  public ReceiverRecord(@JsonProperty("fullName") String fullName,
                        @JsonProperty("username") String nhi,
                        @JsonProperty("region") String region,
                        @JsonProperty("timeStamp") String timestamp,
                        @JsonProperty("organ") String organ) {

    this.fullName = fullName;
    this.nhi = nhi;
    this.region = region;
    this.timestamp = timestamp;
    this.organ = organ;
  }

  String nhi;
  String region;
  String timestamp;
  String organ;

  public String getNhi() {
    return nhi;
  }

  public void setNhi(String nhi) {
    this.nhi = nhi;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getOrgan() {
    return organ;
  }

  public void setOrgan(String organ) {
    this.organ = organ;
  }
}
