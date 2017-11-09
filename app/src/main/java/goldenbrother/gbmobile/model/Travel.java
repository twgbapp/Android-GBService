package goldenbrother.gbmobile.model;

import java.util.ArrayList;

public class Travel {
    private int travelID;
    private String title;
    private String content;
    private String createDate;
    private String expirationDate;
    //
    private ArrayList<Travel> travelList;

    public Travel() {
        travelList = new ArrayList<>();
    }

    public ArrayList<Travel> getTravelList() {
        return travelList;
    }

    public int getTravelID() {
        return travelID;
    }

    public void setTravelID(int travelID) {
        this.travelID = travelID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
