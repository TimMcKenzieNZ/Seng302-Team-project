package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A class to store the organs (as booleans) that a receiver needs, along with the date that these
 * organs were registered or deregistered.
 */
public class ReceiverOrganInventory extends DonorOrganInventory {

  ArrayList<Boolean> organsInList;

  /**
   * A timestamp for when the liver was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime liverTimeStamp;

  public void setLiverTimeStamp(LocalDateTime liverTimeStamp) {
    this.liverTimeStamp = liverTimeStamp;
  }

  public void setKidneysTimeStamp(LocalDateTime kidneysTimeStamp) {
    this.kidneysTimeStamp = kidneysTimeStamp;
  }

  public void setPancreasTimeStamp(LocalDateTime pancreasTimeStamp) {
    this.pancreasTimeStamp = pancreasTimeStamp;
  }

  public void setHeartTimeStamp(LocalDateTime heartTimeStamp) {
    this.heartTimeStamp = heartTimeStamp;
  }

  public void setLungsTimeStamp(LocalDateTime lungsTimeStamp) {
    this.lungsTimeStamp = lungsTimeStamp;
  }

  public void setIntestineTimeStamp(LocalDateTime intestineTimeStamp) {
    this.intestineTimeStamp = intestineTimeStamp;
  }

  public void setCorneasTimeStamp(LocalDateTime corneasTimeStamp) {
    this.corneasTimeStamp = corneasTimeStamp;
  }

  public void setMiddleEarsTimeStamp(LocalDateTime middleEarsTimeStamp) {
    this.middleEarsTimeStamp = middleEarsTimeStamp;
  }

  public void setSkinTimeStamp(LocalDateTime skinTimeStamp) {
    this.skinTimeStamp = skinTimeStamp;
  }

  public void setBoneTimeStamp(LocalDateTime boneTimeStamp) {
    this.boneTimeStamp = boneTimeStamp;
  }

  public void setBoneMarrowTimeStamp(LocalDateTime boneMarrowTimeStamp) {
    this.boneMarrowTimeStamp = boneMarrowTimeStamp;
  }

  public void setConnectiveTissueTimeStamp(LocalDateTime connectiveTissueTimeStamp) {
    this.connectiveTissueTimeStamp = connectiveTissueTimeStamp;
  }

  /**
   * A timestamp for when the kidneys was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime kidneysTimeStamp;

  /**
   * A timestamp for when the pancreas was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime pancreasTimeStamp;


  /**
   * A timestamp for when the heart was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime heartTimeStamp;

  /**
   * A timestamp for when the lungs was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime lungsTimeStamp;

  /**
   * A timestamp for when the intestines was registered or deregistered for this receiver. Null if
   * has never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime intestineTimeStamp;

  /**
   * A timestamp for when the corneas was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime corneasTimeStamp;

  /**
   * A timestamp for when the middle ear was registered or deregistered for this receiver. Null if
   * has never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime middleEarsTimeStamp;

  /**
   * A timestamp for when the skin was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime skinTimeStamp;

  /**
   * A timestamp for when the bone was registered or deregistered for this receiver. Null if has
   * never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime boneTimeStamp;

  /**
   * A timestamp for when the bone marrow was registered or deregistered for this receiver. Null if
   * has never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime boneMarrowTimeStamp;

  /**
   * A timestamp for when the connective tissue was registered or deregistered for this receiver.
   * Null if has never been registered.
   */
  @JsonDeserialize(using= LocalDateTimeDeserializer.class)
  private LocalDateTime connectiveTissueTimeStamp;


  @Override
  public void setLiver(boolean liver) {
    this.liver = liver;
    this.liverTimeStamp = LocalDateTime.now();

  }


  @Override
  public void setKidneys(boolean kidneys) {
    this.kidneys = kidneys;
    this.kidneysTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setPancreas(boolean pancreas) {
    this.pancreas = pancreas;
    this.pancreasTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setHeart(boolean heart) {
    this.heart = heart;
    this.heartTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setLungs(boolean lungs) {
    this.lungs = lungs;
    this.lungsTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setIntestine(boolean intestine) {
    this.intestine = intestine;
    this.intestineTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setCorneas(boolean corneas) {
    this.corneas = corneas;
    this.corneasTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setMiddleEars(boolean middleEars) {
    this.middleEars = middleEars;
    this.middleEarsTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setSkin(boolean skin) {
    this.skin = skin;
    this.skinTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setBone(boolean bone) {
    this.bone = bone;
    this.boneTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setBoneMarrow(boolean boneMarrow) {
    this.boneMarrow = boneMarrow;
    this.boneMarrowTimeStamp = LocalDateTime.now();
  }


  @Override
  public void setConnectiveTissue(boolean connectiveTissue) {
    this.connectiveTissue = connectiveTissue;
    this.connectiveTissueTimeStamp = LocalDateTime.now();
  }

  /**
   * Blank constructor that sets all organ donations to 'false'
   */
  public ReceiverOrganInventory() {
    this.liver = false;
    this.kidneys = false;
    this.heart = false;
    this.lungs = false;
    this.intestine = false;
    this.corneas = false;
    this.middleEars = false;
    this.skin = false;
    this.bone = false;
    this.boneMarrow = false;
    this.connectiveTissue = false;
    this.pancreas = false;
  }

  @JsonCreator
  public ReceiverOrganInventory(@JsonProperty("liver") boolean liver,
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
      @JsonProperty("connectiveTissue") boolean connectiveTissue,
      @JsonProperty("liverTimeStamp") LocalDateTime liverTimeStamp,
      @JsonProperty("kidneysTimeStamp") LocalDateTime kidneysTimeStamp,
      @JsonProperty("pancreasTimeStamp") LocalDateTime pancreasTimeStamp,
      @JsonProperty("heartTimeStamp") LocalDateTime heartTimeStamp,
      @JsonProperty("lungsTimeStamp") LocalDateTime lungsTimeStamp,
      @JsonProperty("intestineTimeStamp") LocalDateTime intestineTimeStamp,
      @JsonProperty("corneasTimeStamp") LocalDateTime corneasTimeStamp,
      @JsonProperty("middleEarsTimeStamp") LocalDateTime middleEarsTimeStamp,
      @JsonProperty("skinTimeStamp") LocalDateTime skinTimeStamp,
      @JsonProperty("boneTimeStamp") LocalDateTime boneTimeStamp,
      @JsonProperty("boneMarrowTimeStamp") LocalDateTime boneMarrowTimeStamp,
      @JsonProperty("connectiveTissueTimeStamp") LocalDateTime connectiveTissueTimeStamp) {
    super(liver, kidneys, pancreas, heart, lungs, intestine, corneas, middleEars, skin, bone,
        boneMarrow, connectiveTissue);
    this.liverTimeStamp = liverTimeStamp;
    this.kidneysTimeStamp = kidneysTimeStamp;
    this.pancreasTimeStamp = pancreasTimeStamp;
    this.heartTimeStamp = heartTimeStamp;
    this.lungsTimeStamp = lungsTimeStamp;
    this.intestineTimeStamp = intestineTimeStamp;
    this.corneasTimeStamp = corneasTimeStamp;
    this.middleEarsTimeStamp = middleEarsTimeStamp;
    this.skinTimeStamp = skinTimeStamp;
    this.boneTimeStamp = boneTimeStamp;
    this.boneMarrowTimeStamp = boneMarrowTimeStamp;
    this.connectiveTissueTimeStamp = connectiveTissueTimeStamp;
    this.liver = liver;
    this.kidneys = kidneys;
    this.heart = heart;
    this.lungs = lungs;
    this.intestine = intestine;
    this.corneas = corneas;
    this.middleEars = middleEars;
    this.skin = skin;
    this.bone = bone;
    this.boneMarrow = boneMarrow;
    this.connectiveTissue = connectiveTissue;
    this.pancreas = pancreas;
    this.organsInList = getOrgansInList();
  }

  public LocalDateTime getLiverTimeStamp() {
    return liverTimeStamp;
  }


  public LocalDateTime getKidneysTimeStamp() {
    return kidneysTimeStamp;
  }


  public LocalDateTime getPancreasTimeStamp() {
    return pancreasTimeStamp;
  }


  public LocalDateTime getHeartTimeStamp() {
    return heartTimeStamp;
  }


  public LocalDateTime getLungsTimeStamp() {
    return lungsTimeStamp;
  }


  public LocalDateTime getIntestineTimeStamp() {
    return intestineTimeStamp;
  }


  public LocalDateTime getCorneasTimeStamp() {
    return corneasTimeStamp;
  }


  public LocalDateTime getMiddleEarsTimeStamp() {
    return middleEarsTimeStamp;
  }


  public LocalDateTime getSkinTimeStamp() {
    return skinTimeStamp;
  }


  public LocalDateTime getBoneTimeStamp() {
    return boneTimeStamp;
  }


  public LocalDateTime getBoneMarrowTimeStamp() {
    return boneMarrowTimeStamp;
  }


  public LocalDateTime getConnectiveTissueTimeStamp() {
    return connectiveTissueTimeStamp;
  }

  public ArrayList<Boolean> getOrgansInList() {
    ArrayList<Boolean> organs = new ArrayList<>();
    organs.add(getLiver());
    organs.add(getKidneys());
    organs.add(getHeart());
    organs.add(getLungs());
    organs.add(getIntestine());
    organs.add(getCorneas());
    organs.add(getMiddleEars());
    organs.add(getSkin());
    organs.add(getBone());
    organs.add(getBoneMarrow());
    organs.add(getConnectiveTissue());
    organs.add(getPancreas());
    return organs;
  }


  /**
   * Returns a string of all the organs the donor wishes to receive as a list if the booleans are
   * not null or 'false'. If no organs are being received, string displays 'no organs to receive'.
   *
   * @return a string list of organs.
   */
  @Override
  public String toString() {
    boolean check = false;
    String organString = "Organs to receive:\n";
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
      organString += "No organs to receive";
    }
    return organString + "\n\n";
  }

  /**
   * Updates the account DonorOrganInventory attribute if it is valid to do so and logs the result.
   *
   * @param donReceiv defines whether the transaction is foir a receiver or donor
   * @param attribute the organ to be updated
   * @param value a string 'true' or 'false'.
   * @return a string log of the result of the operation.
   */
  @Override
  public String[] updateOrganDonation(String donReceiv, String attribute, String value) {
    boolean choice;
    String badMessage;
    try {
      choice = Boolean.parseBoolean(value);
    } catch (Exception e) {
      return new String[]{"ERROR: Unknown value " + value + ". Value should be 'true' or 'false'\n",
          null, null, null};
    }
    choice = Boolean.parseBoolean(value);
    if (donReceiv.equals("receiver")) {
      badMessage = "ERROR: Receiver is already listed to receive " + attribute + ".\n";
    } else {
      badMessage = "ERROR: Donor is already donating " + attribute + ".\n";
    }
    String[] success = {"", null, null, null};
    if (value.equalsIgnoreCase("false")) {
      return new String[]{"To remove a receiving organ, please go to the transplant waiting list.",
          null, null, null};
    }

    switch (attribute) {
      case "liver": {
        if (choice && liver) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "liverReceiving", String.valueOf(liver), value};
          setLiver(choice);
        }
        break;
      }
      case "kidneys": {
        if (choice && kidneys) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "kidneysReceiving", String.valueOf(kidneys), value};
          setKidneys(choice);
        }
        break;
      }
      case "pancreas": {
        if (choice && pancreas) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "pancreasReceiving", String.valueOf(pancreas), value};
          setPancreas(choice);
        }
        break;
      }
      case "heart": {
        if (choice && heart) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "heartReceiving", String.valueOf(heart), value};
          setHeart(choice);
        }
        break;
      }
      case "lungs": {
        if (choice && lungs) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "lungsReceiving", String.valueOf(lungs), value};
          setLungs(choice);
        }
        break;
      }
      case "intestines": {
        if (choice && intestine) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "intestineReceiving", String.valueOf(intestine), value};
          setIntestine(choice);
        }
        break;
      }
      case "corneas": {
        if (choice && corneas) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "corneasReceiving", String.valueOf(corneas), value};
          setCorneas(choice);
        }
        break;
      }
      case "middleEars": {
        if (choice && middleEars) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "middleEarsReceiving", String.valueOf(middleEars),
              value};
          setMiddleEars(choice);
        }
        break;
      }
      case "bone": {
        if (choice && bone) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "boneReceiving", String.valueOf(bone), value};
          setBone(choice);
        }
        break;
      }
      case "boneMarrow": {
        if (choice && boneMarrow) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "boneMarrowReceiving", String.valueOf(boneMarrow),
              value};
          setBoneMarrow(choice);
        }
        break;
      }
      case "skin": {
        if (choice && skin) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "skinReceiving", String.valueOf(skin), value};
          setSkin(choice);
        }
        break;
      }
      case "connectiveTissue": {
        if (choice && connectiveTissue) {
          return new String[]{badMessage, null, null, null};
        } else {
          success = new String[]{"success", "connectiveTissueReceiving",
              String.valueOf(connectiveTissue), value};
          setConnectiveTissue(choice);
        }
        break;
      }
      case "all": {
        if (choice) {
          setAllOrgans(true);
          success = new String[]{"success", "All organ's receiving", "unknown", value};
        } else {
          setAllOrgans(false);
          success = new String[]{"success", "All organ's receiving", "unknown", value};
        }
      }
    }
    return success;
  }

}

