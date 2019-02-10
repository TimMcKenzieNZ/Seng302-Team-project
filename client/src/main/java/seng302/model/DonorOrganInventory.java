package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to store the organs (as booleans) that a donor wishes to donate.
 */
public class DonorOrganInventory {

  /**
   * A boolean to show whether the donor is willing to donate their liver, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean liver = false;

  /**
   * A boolean to show whether the donor is willing to donate their kidneys, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean kidneys = false;

  /**
   * A boolean to show whether the donor is willing to donate their pancreas, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean pancreas = false;

  /**
   * A boolean to show whether the donor is willing to donate their heart, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean heart = false;

  /**
   * A boolean to show whether the donor is willing to donate their lungs, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean lungs = false;

  /**
   * A boolean to show whether the donor is willing to donate their intestine, 'true' means they
   * are, 'false' they do not.
   */
  protected boolean intestine = false;

  /**
   * A boolean to show whether the donor is willing to donate their corneas, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean corneas = false;

  /**
   * A boolean to show whether the donor is willing to donate their middleEars, 'true' means they
   * are, 'false' they do not.
   */
  protected boolean middleEars = false;

  /**
   * A boolean to show whether the donor is willing to donate their skin, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean skin = false;

  /**
   * A boolean to show whether the donor is willing to donate their bone, 'true' means they are,
   * 'false' they do not.
   */
  protected boolean bone = false;

  /**
   * A boolean to show whether the donor is willing to donate their boneMarrow, 'true' means they
   * are, 'false' they do not.
   */
  protected boolean boneMarrow = false;

  /**
   * A boolean to show whether the donor is willing to donate their connectiveTissue, 'true' means
   * they are, 'false' they do not.
   */
  protected boolean connectiveTissue = false;


  /**
   * Blank constructor that sets all organ donations to 'false'
   */
  public DonorOrganInventory() {
  }


  /**
   * Full constructor that allows specification of all boolean attributes.
   *
   * @param liver boolean controlling whether a donor will donate their liver
   * @param kidneys boolean controlling whether a donor will donate their kidneys
   * @param pancreas boolean controlling whether a donor will donate their pancreas
   * @param heart boolean controlling whether a donor will donate their heart
   * @param lungs boolean controlling whether a donor will donate their lungs
   * @param intestine boolean controlling whether a donor will donate their intestine
   * @param corneas boolean controlling whether a donor will donate their corneas
   * @param middleEars boolean controlling whether a donor will donate their middle ear
   * @param skin boolean controlling whether a donor will donate their skin
   * @param bone boolean controlling whether a donor will donate their bone
   * @param boneMarrow boolean controlling whether a donor will donate their bone marrow
   * @param connectiveTissue boolean controlling whether a donor will don't their connective tissue
   */
  @JsonCreator
  public DonorOrganInventory(@JsonProperty("liver") boolean liver,
      @JsonProperty("kidneys") boolean kidneys,
      @JsonProperty("pancreas") boolean pancreas,
      @JsonProperty("heart") boolean heart,
      @JsonProperty("lungs") boolean lungs,
      @JsonProperty("intestine") boolean intestine,
      @JsonProperty("corneas") boolean corneas,
      @JsonProperty("middleEars") boolean middleEars,
      @JsonProperty("skin") boolean skin,
      @JsonProperty("bone") boolean bone,
      @JsonProperty("boneMarrow") boolean boneMarrow,
      @JsonProperty("connectiveTissue") boolean connectiveTissue) {
    this.liver = liver;
    this.kidneys = kidneys;
    this.pancreas = pancreas;
    this.heart = heart;
    this.lungs = lungs;
    this.intestine = intestine;
    this.corneas = corneas;
    this.middleEars = middleEars;
    this.skin = skin;
    this.bone = bone;
    this.boneMarrow = boneMarrow;
    this.connectiveTissue = connectiveTissue;
  }

  public boolean getLiver() {
    return liver;
  }

  public boolean getKidneys() {
    return kidneys;
  }

  public boolean getPancreas() {
    return pancreas;
  }

  public boolean getHeart() {
    return heart;
  }

  public boolean getLungs() {
    return lungs;
  }

  public boolean getIntestine() {
    return intestine;
  }

  public boolean getCorneas() {
    return corneas;
  }

  public boolean getMiddleEars() {
    return middleEars;
  }

  public boolean getSkin() {
    return skin;
  }

  public boolean getBone() {
    return bone;
  }

  public boolean getBoneMarrow() {
    return boneMarrow;
  }

  public boolean getConnectiveTissue() {
    return connectiveTissue;
  }

  public void setLiver(boolean liver) {
    this.liver = liver;
  }

  public void setKidneys(boolean kidneys) {
    this.kidneys = kidneys;
  }

  public void setPancreas(boolean pancreas) {
    this.pancreas = pancreas;
  }

  public void setHeart(boolean heart) {
    this.heart = heart;
  }

  public void setLungs(boolean lungs) {
    this.lungs = lungs;
  }

  public void setIntestine(boolean intestine) {
    this.intestine = intestine;
  }

  public void setCorneas(boolean corneas) {
    this.corneas = corneas;
  }

  public void setMiddleEars(boolean middleEars) {
    this.middleEars = middleEars;
  }

  public void setSkin(boolean skin) {
    this.skin = skin;
  }

  public void setBone(boolean bone) {
    this.bone = bone;
  }

  public void setBoneMarrow(boolean boneMarrow) {
    this.boneMarrow = boneMarrow;
  }

  public void setConnectiveTissue(boolean connectiveTissue) {
    this.connectiveTissue = connectiveTissue;
  }


  /**
   * Returns the organ string if the given boolean value is 'true', otherwise returns an empty
   * string.
   *
   * @param organ a string representation of an organ.
   * @param value a boolean value.
   * @return returns the organ string or an empty string.
   */
  public String booleanToString(String organ, boolean value) {
    if (value) {
      return "\t" + organ + "\n";
    } else {
      return "";
    }
  }


  /**
   * Returns a string of all the organs the donor wishes to donate as a list if the booleans are not
   * null or 'false'. If no organs are donated, string displays 'no organs to donate'.
   *
   * @return a string list of organs.
   */
  @Override
  public String toString() {
    boolean check = false;
    String organString = "Organs to donate:\n";
    if (Boolean.TRUE.equals(liver)) {
      organString += booleanToString("Liver", liver);
      check = true;
    }
    if (Boolean.TRUE.equals(kidneys)) {
      organString += booleanToString("Kidneys", kidneys);
      check = true;
    }
    if (Boolean.TRUE.equals(pancreas)) {
      organString += booleanToString("Pancreas", pancreas);
      check = true;
    }
    if (Boolean.TRUE.equals(heart)) {
      organString += booleanToString("Heart", heart);
      check = true;
    }
    if (Boolean.TRUE.equals(lungs)) {
      organString += booleanToString("Lungs", lungs);
      check = true;
    }
    if (Boolean.TRUE.equals(intestine)) {
      organString += booleanToString("Intestine", intestine);
      check = true;
    }
    if (Boolean.TRUE.equals(corneas)) {
      organString += booleanToString("Corneas", corneas);
      check = true;
    }
    if (Boolean.TRUE.equals(middleEars)) {
      organString += booleanToString("Middle Ears", middleEars);
      check = true;
    }
    if (Boolean.TRUE.equals(skin)) {
      organString += booleanToString("Skin", skin);
      check = true;
    }
    if (Boolean.TRUE.equals(bone)) {
      organString += booleanToString("Bone", bone);
      check = true;
    }
    if (Boolean.TRUE.equals(boneMarrow)) {
      organString += booleanToString("Bone marrow", boneMarrow);
      check = true;
    }
    if (Boolean.TRUE.equals(connectiveTissue)) {
      organString += booleanToString("Connective Tissue", connectiveTissue);
      check = true;
    }
    if (check == false) {
      organString += "No organs to donate";
    }
    return organString + "\n\n";
  }


  /**
   * Sets all DonorOrganInventory attribute values to 'true' if the given bool is 'true', 'false'
   * otherwise.
   *
   * @param bool a boolean.
   */
  public void setAllOrgans(boolean bool) {
    if (bool) {
      liver = true;
      kidneys = true;
      pancreas = true;
      heart = true;
      lungs = true;
      intestine = true;
      corneas = true;
      middleEars = true;
      bone = true;
      boneMarrow = true;
      skin = true;
      connectiveTissue = true;
    } else {
      liver = false;
      kidneys = false;
      pancreas = false;
      heart = false;
      lungs = false;
      intestine = false;
      corneas = false;
      middleEars = false;
      bone = false;
      boneMarrow = false;
      skin = false;
      connectiveTissue = false;
    }
  }

  /**
   * Updates the account DonorOrganInventory attribute if it is valid to do so and logs the result.
   *
   * @param donReceiv defines whether the transaction is foir a receiver or donor
   * @param attribute the organ to be updated
   * @param value a string 'true' or 'false'.
   * @return a string log of the result of the operation.
   */
  public String[] updateOrganDonation(String donReceiv, String attribute, String value) {
    boolean choice;
    String badMessage;
    String DonReceiver;
    try {
      choice = Boolean.parseBoolean(value);
    } catch (Exception e) {
      return new String[]{"ERROR: Unknown value " + value + ". Value should be 'true' or 'false'\n",
          null, null, null};
    }
    choice = Boolean.parseBoolean(value);
    if (donReceiv.equals("receiver")) {
      DonReceiver = "Receiving: " + attribute;
      badMessage = "ERROR: Receiver is already listed to receive " + attribute + ".\n";
    } else {
      DonReceiver = "Donating: " + attribute;
      badMessage = "ERROR: Donor is already donating " + attribute + ".\n";
    }
    String[] success = {"", null, null, null};
    switch (attribute) {
      case "liver": {
        if (choice && liver) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "liverDonation", String.valueOf(liver), value};
          setLiver(choice);
        }
        break;
      }
      case "kidneys": {
        if (choice && kidneys) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "kidneysDonation", String.valueOf(kidneys), value};
          setKidneys(choice);
        }
        break;
      }
      case "pancreas": {
        if (choice && pancreas) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "pancreasDonation", String.valueOf(pancreas), value};
          setPancreas(choice);
        }
        break;
      }
      case "heart": {
        if (choice && heart) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "heartDonation", String.valueOf(heart), value};
          setHeart(choice);
        }
        break;
      }
      case "lungs": {
        if (choice && lungs) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "lungsDonation", String.valueOf(lungs), value};
          setLungs(choice);
        }
        break;
      }
      case "intestines": {
        if (choice && intestine) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "intestineDonation", String.valueOf(intestine), value};
          setIntestine(choice);
        }
        break;
      }
      case "corneas": {
        if (choice && corneas) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "corneasDonation", String.valueOf(corneas), value};
          setCorneas(choice);
        }
        break;
      }
      case "middleEars": {
        if (choice && middleEars) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "middleEarsDonation", String.valueOf(middleEars),
              value};
          setMiddleEars(choice);
        }
        break;
      }
      case "bone": {
        if (choice && bone) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "boneDonation", String.valueOf(bone), value};
          setBone(choice);
        }
        break;
      }
      case "boneMarrow": {
        if (choice && boneMarrow) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "boneMarrowDonation", String.valueOf(boneMarrow),
              value};
          setBoneMarrow(choice);
        }
        break;
      }
      case "skin": {
        if (choice && skin) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "skinDonation", String.valueOf(skin), value};
          setSkin(choice);
        }
        break;
      }
      case "connectiveTissue": {
        if (choice && connectiveTissue) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "connectiveTissueDonation",
              String.valueOf(connectiveTissue), value};
          setConnectiveTissue(choice);
        }
        break;
      }
      case "all": {
        if (choice) {
          setAllOrgans(true);
          success = new String[]{"success", "all organ donation", "unknown", value};
        } else {
          setAllOrgans(false);
          success = new String[]{"success", "no organ donations", "unknown", value};
        }
      }
    }
    return success;
  }

  /**
   * Returns the organ donation status of an organ
   *
   * @param organ The organ to donate
   * @return The donation status of the organ that was specified as a string and returns false if
   * not a valid organ
   */
  public boolean isDonating(String organ) {
    switch (organ) {
      case "liver":
        return liver;
      case "kidneys":
        return kidneys;
      case "pancreas":
        return pancreas;
      case "heart":
        return heart;
      case "lungs":
        return lungs;
      case "intestine":
        return intestine;
      case "corneas":
        return corneas;
      case "middleEars":
        return middleEars;
      case "skin":
        return skin;
      case "bone":
        return bone;
      case "boneMarrow":
        return boneMarrow;
      case "connectiveTissue":
        return connectiveTissue;
      default:
        return false;
    }
  }
}