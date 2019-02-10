package seng302.model.statistics;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class OrgansByRegionCount {
    public OrganCount getNorthland() {
        return Northland;
    }

    public void setNorthland(OrganCount northland) {
        Northland = northland;
    }

    public OrganCount getAuckland() {
        return Auckland;
    }

    public void setAuckland(OrganCount auckland) {
        Auckland = auckland;
    }

    public OrganCount getWaikato() {
        return Waikato;
    }

    public void setWaikato(OrganCount waikato) {
        Waikato = waikato;
    }

    public OrganCount getBayOfPlenty() {
        return BayOfPlenty;
    }

    public void setBayOfPlenty(OrganCount bayOfPlenty) {
        BayOfPlenty = bayOfPlenty;
    }

    public OrganCount getGisbourne() {
        return Gisbourne;
    }

    public void setGisbourne(OrganCount gisbourne) {
        Gisbourne = gisbourne;
    }

    public OrganCount getHawkesBay() {
        return HawkesBay;
    }

    public void setHawkesBay(OrganCount hawkesBay) {
        HawkesBay = hawkesBay;
    }

    public OrganCount getTaranaki() {
        return Taranaki;
    }

    public void setTaranaki(OrganCount taranaki) {
        Taranaki = taranaki;
    }

    public OrganCount getManawatuWanganui() {
        return ManawatuWanganui;
    }

    public void setManawatuWanganui(OrganCount manawatuWanganui) {
        ManawatuWanganui = manawatuWanganui;
    }

    public OrganCount getWellington() {
        return Wellington;
    }

    public void setWellington(OrganCount wellington) {
        Wellington = wellington;
    }

    public OrganCount getTasman() {
        return Tasman;
    }

    public void setTasman(OrganCount tasman) {
        Tasman = tasman;
    }

    public OrganCount getNelson() {
        return Nelson;
    }

    public void setNelson(OrganCount nelson) {
        Nelson = nelson;
    }

    public OrganCount getMalborough() {
        return Malborough;
    }

    public void setMalborough(OrganCount malborough) {
        Malborough = malborough;
    }

    public OrganCount getWestCoast() {
        return WestCoast;
    }

    public void setWestCoast(OrganCount westCoast) {
        WestCoast = westCoast;
    }

    public OrganCount getCanterbury() {
        return Canterbury;
    }

    public void setCanterbury(OrganCount canterbury) {
        Canterbury = canterbury;
    }

    public OrganCount getOtago() {
        return Otago;
    }

    public void setOtago(OrganCount otago) {
        Otago = otago;
    }

    public OrganCount getSouthland() {
        return Southland;
    }

    public void setSouthland(OrganCount southland) {
        Southland = southland;
    }

    public OrganCount getChathamIslands() {
        return ChathamIslands;
    }

    public void setChathamIslands(OrganCount chathamIslands) {
        ChathamIslands = chathamIslands;
    }

    private OrganCount Northland;
    private OrganCount Auckland;
    private OrganCount Waikato;
    private OrganCount BayOfPlenty;
    private OrganCount Gisbourne;
    private OrganCount HawkesBay;
    private OrganCount Taranaki;
    private OrganCount ManawatuWanganui;
    private OrganCount Wellington;
    private OrganCount Tasman;
    private OrganCount Nelson;
    private OrganCount Malborough;
    private OrganCount WestCoast;
    private OrganCount Canterbury;
    private OrganCount Otago;
    private OrganCount Southland;
    private OrganCount ChathamIslands;


    @JsonCreator
    public OrgansByRegionCount(@JsonProperty("Northland") OrganCount northlandCount,
                               @JsonProperty("Auckland") OrganCount aucklandCount,
                               @JsonProperty("Waikato") OrganCount waikatoCount,
                               @JsonProperty("Bay Of Plenty") OrganCount bayOfPlentyCount,
                               @JsonProperty("Gisbourne") OrganCount gisbourneCount,
                               @JsonProperty("Hawke's Bay") OrganCount hawkesBayCount,
                               @JsonProperty("Taranaki") OrganCount tarankaiCount,
                               @JsonProperty("Manawatu-Wanganui") OrganCount manawatuWanganuiCount,
                               @JsonProperty("Wellington") OrganCount welligtonCount,
                               @JsonProperty("Tasman") OrganCount tasmanCount,
                               @JsonProperty("Nelson") OrganCount nelsonCount,
                               @JsonProperty("Malborough") OrganCount malboroughCount,
                               @JsonProperty("West Coast") OrganCount westCoastCount,
                               @JsonProperty("Canterbury") OrganCount canterburyCount,
                               @JsonProperty("Otago") OrganCount otagoCount,
                               @JsonProperty("Southland") OrganCount southlandCount,
                               @JsonProperty("Chatham Islands") OrganCount chathamIslandsCount


                               ) {

        this.Northland = northlandCount;
        this.Auckland = aucklandCount;
        this.Waikato = waikatoCount;
        this.BayOfPlenty = bayOfPlentyCount;
        this.Gisbourne = gisbourneCount;
        this.HawkesBay = hawkesBayCount;
        this.Taranaki = tarankaiCount;
        this.ManawatuWanganui = manawatuWanganuiCount;
        this.Wellington = welligtonCount;
        this.Tasman = tasmanCount;
        this.Nelson = nelsonCount;
        this.Malborough = malboroughCount;
        this.WestCoast = westCoastCount;
        this.Canterbury = canterburyCount;
        this.Otago = otagoCount;
        this.Southland = southlandCount;
        this.ChathamIslands = chathamIslandsCount;
    }

    public OrgansByRegionCount(){

    }



}
