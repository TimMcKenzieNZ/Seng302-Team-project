package seng302.model.patches;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import seng302.model.DonorOrganInventory;
import java.time.LocalDateTime;
import java.util.ArrayList;


/**
 * A class to store the organs (as booleans) that a receiver needs, along with the date that these
 * organs were registered or deregistered.
 */
public class ReceiverOrganInventoryPatch extends DonorOrganInventory {

    /**
     * A timestamp for when the liver was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String liverTimeStamp;

    /**
     * A timestamp for when the kidneys was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String kidneysTimeStamp;

    public String getKidneysTimeStamp() {
        return kidneysTimeStamp;
    }

    public void setKidneysTimeStamp(String kidneysTimeStamp) {
        this.kidneysTimeStamp = kidneysTimeStamp;
    }

    public String getPancreasTimeStamp() {
        return pancreasTimeStamp;
    }

    public void setPancreasTimeStamp(String pancreasTimeStamp) {
        this.pancreasTimeStamp = pancreasTimeStamp;
    }

    public String getHeartTimeStamp() {
        return heartTimeStamp;
    }

    public void setHeartTimeStamp(String heartTimeStamp) {
        this.heartTimeStamp = heartTimeStamp;
    }

    public String getLungsTimeStamp() {
        return lungsTimeStamp;
    }

    public void setLungsTimeStamp(String lungsTimeStamp) {
        this.lungsTimeStamp = lungsTimeStamp;
    }

    public String getIntestineTimeStamp() {
        return intestineTimeStamp;
    }

    public void setIntestineTimeStamp(String intestineTimeStamp) {
        this.intestineTimeStamp = intestineTimeStamp;
    }

    public String getCorneasTimeStamp() {
        return corneasTimeStamp;
    }

    public void setCorneasTimeStamp(String corneasTimeStamp) {
        this.corneasTimeStamp = corneasTimeStamp;
    }

    public String getMiddleEarsTimeStamp() {
        return middleEarsTimeStamp;
    }

    public void setMiddleEarsTimeStamp(String middleEarsTimeStamp) {
        this.middleEarsTimeStamp = middleEarsTimeStamp;
    }

    public String getSkinTimeStamp() {
        return skinTimeStamp;
    }

    public void setSkinTimeStamp(String skinTimeStamp) {
        this.skinTimeStamp = skinTimeStamp;
    }

    public String getBoneTimeStamp() {
        return boneTimeStamp;
    }

    public void setBoneTimeStamp(String boneTimeStamp) {
        this.boneTimeStamp = boneTimeStamp;
    }

    public String getBoneMarrowTimeStamp() {
        return boneMarrowTimeStamp;
    }

    public void setBoneMarrowTimeStamp(String boneMarrowTimeStamp) {
        this.boneMarrowTimeStamp = boneMarrowTimeStamp;
    }

    public String getConnectiveTissueTimeStamp() {
        return connectiveTissueTimeStamp;
    }

    public void setConnectiveTissueTimeStamp(String connectiveTissueTimeStamp) {
        this.connectiveTissueTimeStamp = connectiveTissueTimeStamp;
    }

    /**

     * A timestamp for when the pancreas was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String pancreasTimeStamp;


    /**
     * A timestamp for when the heart was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String heartTimeStamp;

    /**
     * A timestamp for when the lungs was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String lungsTimeStamp;

    /**
     * A timestamp for when the intestines was registered or deregistered for this receiver. Null if
     * has never been registered.
     */
    private String intestineTimeStamp;

    /**
     * A timestamp for when the corneas was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String corneasTimeStamp;

    /**
     * A timestamp for when the middle ear was registered or deregistered for this receiver. Null if
     * has never been registered.
     */
    private String middleEarsTimeStamp;

    /**
     * A timestamp for when the skin was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String skinTimeStamp;

    /**
     * A timestamp for when the bone was registered or deregistered for this receiver. Null if has
     * never been registered.
     */
    private String boneTimeStamp;

    /**
     * A timestamp for when the bone marrow was registered or deregistered for this receiver. Null if
     * has never been registered.
     */
    private String boneMarrowTimeStamp;

    /**
     * A timestamp for when the connective tissue was registered or deregistered for this receiver.
     * Null if has never been registered.
     */
    private String connectiveTissueTimeStamp;

    private String version;



    /**
     * Blank constructor that sets all organ donations to 'false'
     */
    public ReceiverOrganInventoryPatch() {
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
    public ReceiverOrganInventoryPatch(@JsonProperty("liver") boolean liver,
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
                                  @JsonProperty("liverTimeStamp") String liverTimeStamp,
                                  @JsonProperty("kidneysTimeStamp") String kidneysTimeStamp,
                                  @JsonProperty("pancreasTimeStamp") String pancreasTimeStamp,
                                  @JsonProperty("heartTimeStamp") String heartTimeStamp,
                                  @JsonProperty("lungsTimeStamp") String lungsTimeStamp,
                                  @JsonProperty("intestineTimeStamp") String intestineTimeStamp,
                                  @JsonProperty("corneasTimeStamp") String corneasTimeStamp,
                                  @JsonProperty("middleEarsTimeStamp") String middleEarsTimeStamp,
                                  @JsonProperty("skinTimeStamp") String skinTimeStamp,
                                  @JsonProperty("boneTimeStamp") String boneTimeStamp,
                                  @JsonProperty("boneMarrowTimeStamp") String boneMarrowTimeStamp,
                                  @JsonProperty("connectiveTissueTimeStamp") String connectiveTissueTimeStamp,
                                  @JsonProperty("version") String version) {
//        super(liver, kidneys, pancreas, heart, lungs, intestine, corneas, middleEars, skin, bone,
//                boneMarrow, connectiveTissue);
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
        this.version = version;
    }


    public void setOrgan(String organ) {
        switch(organ) {
            case "liver": {
                this.liver = false;
                this.liverTimeStamp = "";
                break;}
            case "lungs": {
                this.lungs = false;
                this.lungsTimeStamp = "";
                break;}
            case "kidneys": {
                this.kidneys = false;
                this.kidneysTimeStamp = "";
                break;}
            case "pancreas": {
                this.pancreas = false;
                this.pancreasTimeStamp = "";
                break; }
            case "heart": {
                this.heart = false;
                this.heartTimeStamp = "";
                break; }
            case "intestine": {
                this.intestine = false;
                this.intestineTimeStamp = "";
                break; }
            case "corneas": {
                this.corneas = false;
                this.corneasTimeStamp = "";
                break; }
            case "middleEars": {
                this.middleEars = false;
                this.middleEarsTimeStamp = "";
                break; }
            case "skin": {
                this.skin = false;
                this.skinTimeStamp = "";
                break; }
            case "bone": {
                this.bone = false;
                this.boneTimeStamp = "";
                break; }
            case "boneMarrow": {
                this.boneMarrow = false;
                this.boneMarrowTimeStamp = "";
                break; }
            case "connectiveTissue": {
                this.connectiveTissue = false;
                this.connectiveTissueTimeStamp = "";
                break; }
            case "all": {
                this.liver = false;
                this.liverTimeStamp = "";
                this.lungs = false;
                this.lungsTimeStamp = "";
                this.kidneys = false;
                this.kidneysTimeStamp = "";
                this.pancreas = false;
                this.pancreasTimeStamp = "";
                this.heart = false;
                this.heartTimeStamp = "";
                this.intestine = false;
                this.intestineTimeStamp = "";
                this.corneas = false;
                this.corneasTimeStamp = "";
                this.middleEars = false;
                this.middleEarsTimeStamp = "";
                this.skin = false;
                this.skinTimeStamp = "";
                this.bone = false;
                this.boneTimeStamp = "";
                this.boneMarrow = false;
                this.boneMarrowTimeStamp = "";
                this.connectiveTissue = false;
                this.connectiveTissueTimeStamp = "";
            }

        }

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLiverTimeStamp() {
        return liverTimeStamp;
    }

    public void setLiverTimeStamp(String liverTimeStamp) {
        this.liverTimeStamp = liverTimeStamp;
    }
}

