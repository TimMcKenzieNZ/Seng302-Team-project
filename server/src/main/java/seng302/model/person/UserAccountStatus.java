package seng302.model.person;

/**
 * Possible status's a user account can have depending on the validness of their attributes: VALID:
 * All of the user's attributes are valid. REPAIRED: Missing non-essential user attributes were
 * given default values. POOR: Some non-critical attributes are invalid. INVALID: One or more of the
 * critical attributes are invalid. EXISTS: The account already exists in the database. Critical
 * attributes include: username, password, first name, creation date, and last name. ERROR: An error
 * involving getting, creating or deleting the donor/receiver from the database.
 */
public enum UserAccountStatus {
  VALID, REPAIRED, POOR, INVALID, EXISTS, ERROR
}
