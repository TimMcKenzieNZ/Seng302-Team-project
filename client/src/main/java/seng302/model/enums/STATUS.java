package seng302.model.enums;

/**
 * Enum that stores the server and endpoint addresses.
 */
public enum STATUS {

  NONE("none"),
  SUCCEEDED("succeeded"),
  FAILED("failed"),
  CANCELLED("cancelled");

  private String statusString;

  STATUS(String status) {
    this.statusString = status;
  }

  public String getStatus() {
    return this.statusString;
  }
}