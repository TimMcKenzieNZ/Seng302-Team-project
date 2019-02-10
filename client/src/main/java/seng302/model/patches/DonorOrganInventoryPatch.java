package seng302.model.patches;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to store the organs (as booleans) that a donor wishes to donate.
 */
public class DonorOrganInventoryPatch {

    /**
     * A boolean to show whether the donor is willing to donate their liver, 'true' means they are,
     * 'false' they do not.
     */
    boolean liver = false;

    /**
     * A boolean to show whether the donor is willing to donate their kidneys, 'true' means they are,
     * 'false' they do not.
     */
    boolean kidneys = false;

    /**
     * A boolean to show whether the donor is willing to donate their pancreas, 'true' means they are,
     * 'false' they do not.
     */
    boolean pancreas = false;

    /**
     * A boolean to show whether the donor is willing to donate their heart, 'true' means they are,
     * 'false' they do not.
     */
    boolean heart = false;

    /**
     * A boolean to show whether the donor is willing to donate their lungs, 'true' means they are,
     * 'false' they do not.
     */
    boolean lungs = false;

    /**
     * A boolean to show whether the donor is willing to donate their intestine, 'true' means they
     * are, 'false' they do not.
     */
    boolean intestine = false;

    /**
     * A boolean to show whether the donor is willing to donate their corneas, 'true' means they are,
     * 'false' they do not.
     */
    boolean corneas = false;

    /**
     * A boolean to show whether the donor is willing to donate their middleEars, 'true' means they
     * are, 'false' they do not.
     */
    boolean middleEars = false;

    /**
     * A boolean to show whether the donor is willing to donate their skin, 'true' means they are,
     * 'false' they do not.
     */
    boolean skin = false;

    /**
     * A boolean to show whether the donor is willing to donate their bone, 'true' means they are,
     * 'false' they do not.
     */
    boolean bone = false;

    /**
     * A boolean to show whether the donor is willing to donate their boneMarrow, 'true' means they
     * are, 'false' they do not.
     */
    boolean boneMarrow = false;

    /**
     * A boolean to show whether the donor is willing to donate their connectiveTissue, 'true' means
     * they are, 'false' they do not.
     */
    boolean connectiveTissue = false;

    private String version;


    /**
     * Blank constructor that sets all organ donations to 'false'
     */
    public DonorOrganInventoryPatch() {
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
    public DonorOrganInventoryPatch(@JsonProperty("liver") boolean liver,
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
                               @JsonProperty("version") String version) {
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
        this.version = version;
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


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
