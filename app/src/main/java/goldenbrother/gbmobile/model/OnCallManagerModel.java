package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/19.
 */

public class OnCallManagerModel {
    private String userPicture;
    private String userName;
    private String userID;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OnCallManagerModel that = (OnCallManagerModel) o;

        if (userPicture != null ? !userPicture.equals(that.userPicture) : that.userPicture != null)
            return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null)
            return false;
        return userID != null ? userID.equals(that.userID) : that.userID == null;

    }

    @Override
    public int hashCode() {
        int result = userPicture != null ? userPicture.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }
}
