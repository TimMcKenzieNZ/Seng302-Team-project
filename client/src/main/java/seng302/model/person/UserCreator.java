package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserCreator {

  //For the post clinician endpoint
  @JsonCreator
  public UserCreator(@JsonProperty("username") String username,
      @JsonProperty("givenName") String first,
      @JsonProperty("middleName") String middle,
      @JsonProperty("lastName") String last,
      @JsonProperty("password") String password,
      @JsonProperty("region") String region,
      @JsonProperty("streetAddress") String streetAddress,
      @JsonProperty("modifyingAccount") String modifyingAccount) {
    this.username = username;
    this.first = first;
    this.middle = middle;
    this.last = last;
    this.password = password;
    this.region = region;
    this.streetAddress = streetAddress;
    this.entries = new ArrayList<>();
    this.entry = new LogEntry(this.username, modifyingAccount, "created", "", "created", LocalDateTime.now());
  }



  private String username;
  private String first;
  private String middle;
  private String last;
  private String password;
  private String region;
  private LogEntry entry;
  private ArrayList<LogEntry> entries;
  private String mobileNumber;
  private String streetAddress;


  public String getFirst() {
    return first;
  }

  public String getLast() {
    return last;
  }

  public String getMiddle() {
    return middle;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  public LogEntry getEntry() {
    return entry;
  }

  public String getRegion() {
    return region;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public String getMobileNumber() {return mobileNumber; }

  public ArrayList<LogEntry> getEntries() {
    return entries;
  }

    public void setUsername(String username) {
        this.username = username;
    }
}
