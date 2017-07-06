package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/13.
 */

public class EventUserModel {
    private String userID;
    private String userPicture;
    private String userName;
    private String title;
    private String areaNum;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAreaNum() {
        return areaNum;
    }

    public void setAreaNum(String areaNum) {
        this.areaNum = areaNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventUserModel that = (EventUserModel) o;

        return userID != null ? userID.equals(that.userID) : that.userID == null;

    }

    @Override
    public int hashCode() {
        return userID != null ? userID.hashCode() : 0;
    }
}
