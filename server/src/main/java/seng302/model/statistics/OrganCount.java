package seng302.model.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganCount {

    private Integer liverCount = 0;
    private Integer kidneyCount = 0;
    private Integer pancreasCount = 0;
    private Integer heartCount = 0;
    private Integer lungCount = 0;
    private Integer intestineCount = 0;
    private Integer corneasCount =0;
    private Integer middleEarsCount =0;
    private Integer skinCount =0;
    private Integer boneCount =0;
    private Integer boneMarrowCount =0;
    private Integer connectiveTissueCount =0;

    @JsonCreator
    public OrganCount(@JsonProperty("liverCount") Integer liverCount,
                      @JsonProperty("kidneyCount") Integer kidneyCount,
                      @JsonProperty("pancreasCount") Integer pancreasCount,
                      @JsonProperty("heartCount") Integer heartCount,
                      @JsonProperty("lungCount") Integer lungCount,
                      @JsonProperty("intestineCount") Integer intestineCount,
                      @JsonProperty("corneasCount") Integer corneasCount,
                      @JsonProperty("middleEarsCount") Integer middleEarsCount,
                      @JsonProperty("skinCount") Integer skinCount,
                      @JsonProperty("boneCount") Integer boneCount,
                      @JsonProperty("boneMarrowCount") Integer boneMarrowCount,
                      @JsonProperty("connectiveTissueCount") Integer connectiveTissueCount) {

        this.liverCount = liverCount;
        this.kidneyCount = kidneyCount;
        this.pancreasCount = pancreasCount;
        this.heartCount = heartCount;
        this.lungCount = lungCount;
        this.intestineCount = intestineCount;
        this.corneasCount = corneasCount;
        this.middleEarsCount = middleEarsCount;
        this.skinCount = skinCount;
        this.boneCount = boneCount;
        this.boneMarrowCount = boneMarrowCount;
        this.connectiveTissueCount = connectiveTissueCount;
    }

    public OrganCount(){

    }

    public Integer getLiverCount() {
        return liverCount;
    }

    public void setLiverCount(Integer liverCount) {
        this.liverCount = liverCount;
    }

    public Integer getKidneyCount() {
        return kidneyCount;
    }

    public void setKidneyCount(Integer kidneyCount) {
        this.kidneyCount = kidneyCount;
    }

    public Integer getPancreasCount() {
        return pancreasCount;
    }

    public void setPancreasCount(Integer pancreasCount) {
        this.pancreasCount = pancreasCount;
    }

    public Integer getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(Integer heartCount) {
        this.heartCount = heartCount;
    }

    public Integer getLungCount() {
        return lungCount;
    }

    public void setLungCount(Integer lungCount) {
        this.lungCount = lungCount;
    }

    public Integer getIntestineCount() {
        return intestineCount;
    }

    public void setIntestineCount(Integer intestineCount) {
        this.intestineCount = intestineCount;
    }

    public Integer getCorneasCount() {
        return corneasCount;
    }

    public void setCorneasCount(Integer corneasCount) {
        this.corneasCount = corneasCount;
    }

    public Integer getMiddleEarsCount() {
        return middleEarsCount;
    }

    public void setMiddleEarsCount(Integer middleEarsCount) {
        this.middleEarsCount = middleEarsCount;
    }

    public Integer getSkinCount() {
        return skinCount;
    }

    public void setSkinCount(Integer skinCount) {
        this.skinCount = skinCount;
    }

    public Integer getBoneCount() {
        return boneCount;
    }

    public void setBoneCount(Integer boneCount) {
        this.boneCount = boneCount;
    }

    public Integer getBoneMarrowCount() {
        return boneMarrowCount;
    }

    public void setBoneMarrowCount(Integer boneMarrowCount) {
        this.boneMarrowCount = boneMarrowCount;
    }

    public Integer getConnectiveTissueCount() {
        return connectiveTissueCount;
    }

    public void setConnectiveTissueCount(Integer connectiveTissueCount) {
        this.connectiveTissueCount = connectiveTissueCount;
    }

    @Override
    public String toString() {
        return "OrganCount{" +
            "liverCount=" + liverCount +
            ", kidneyCount=" + kidneyCount +
            ", pancreasCount=" + pancreasCount +
            ", heartCount=" + heartCount +
            ", lungCount=" + lungCount +
            ", intestineCount=" + intestineCount +
            ", corneasCount=" + corneasCount +
            ", middleEarsCount=" + middleEarsCount +
            ", skinCount=" + skinCount +
            ", boneCount=" + boneCount +
            ", boneMarrowCount=" + boneMarrowCount +
            ", connectiveTissueCount=" + connectiveTissueCount +
            '}';
    }

    public int totalCount() {
        return liverCount + kidneyCount + pancreasCount +
        heartCount + lungCount + intestineCount +
        corneasCount + middleEarsCount + skinCount +
        boneCount + boneMarrowCount + connectiveTissueCount;
    }
}
