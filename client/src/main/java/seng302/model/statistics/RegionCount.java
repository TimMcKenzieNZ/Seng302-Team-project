package seng302.model.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegionCount {

  public Integer northlandCount = 0;
  public Integer aucklandCount = 0;
  public Integer waikatoCount = 0;
  public Integer bayOfPlentyCount = 0;
  public Integer gisborneCount = 0;
  public Integer hawkesBayCount = 0;
  public Integer taranakiCount = 0;
  public Integer manawatuWanganuiCount = 0;
  public Integer wellingtonCount = 0;
  public Integer tasminCount = 0;
  public Integer nelsonCount = 0;
  public Integer marlboroughCount = 0;
  public Integer westCoastCount = 0;
  public Integer canterburyCount = 0;
  public Integer otagoCount = 0;
  public Integer southlandCount = 0;
  public Integer chathamIslandsCount = 0;

  @JsonCreator
  public RegionCount(
      @JsonProperty("northlandCount") Integer northlandCount,
      @JsonProperty("aucklandCount") Integer aucklandCount,
      @JsonProperty("waikatoCount") Integer waikatoCount,
      @JsonProperty("bayOfPlentyCount") Integer bayOfPlentyCount,
      @JsonProperty("gisborneCount") Integer gisborneCount,
      @JsonProperty("hawkesBayCount") Integer hawkesBayCount,
      @JsonProperty("taranakiCount") Integer taranakiCount,
      @JsonProperty("manawatuWanganuiCount") Integer manawatuWanganuiCount,
      @JsonProperty("wellingtonCount") Integer wellingtonCount,
      @JsonProperty("tasminCount") Integer tasminCount,
      @JsonProperty("nelsonCount") Integer nelsonCount,
      @JsonProperty("marlboroughCount") Integer marlboroughCount,
      @JsonProperty("westCoastCount") Integer westCoastCount,
      @JsonProperty("canterburyCount") Integer canterburyCount,
      @JsonProperty("otagoCount") Integer otagoCount,
      @JsonProperty("southlandCount") Integer southlandCount,
      @JsonProperty("chathamIslandsCount") Integer chathamIslandsCount
  ) {
    this.northlandCount = northlandCount;
    this.aucklandCount = aucklandCount;
    this.waikatoCount = waikatoCount;
    this.bayOfPlentyCount = bayOfPlentyCount;
    this.gisborneCount = gisborneCount;
    this.hawkesBayCount = hawkesBayCount;
    this.taranakiCount = taranakiCount;
    this.manawatuWanganuiCount = manawatuWanganuiCount;
    this.wellingtonCount = wellingtonCount;
    this.tasminCount = tasminCount;
    this.nelsonCount = nelsonCount;
    this.marlboroughCount = marlboroughCount;
    this.westCoastCount = westCoastCount;
    this.canterburyCount = canterburyCount;
    this.otagoCount = otagoCount;
    this.southlandCount = southlandCount;
    this.chathamIslandsCount = chathamIslandsCount;
  }

  public RegionCount() {}

}
