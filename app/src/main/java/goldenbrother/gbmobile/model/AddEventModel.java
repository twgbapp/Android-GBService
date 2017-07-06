package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2017/1/18.
 */

public class AddEventModel {
    private String userID;
    private String eventDescription;
    private int eventKind;
    private String kindContent; // for show
    private String staffID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getEventKind() {
        return eventKind;
    }

    public void setEventKind(int eventKind) {
        this.eventKind = eventKind;
    }

    public String getKindContent() {
        return kindContent;
    }

    public void setKindContent(String kindContent) {
        this.kindContent = kindContent;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
}
