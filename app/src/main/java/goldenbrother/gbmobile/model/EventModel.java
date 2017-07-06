package goldenbrother.gbmobile.model;

import android.util.Log;

/**
 * Created by asus on 2016/12/8.
 */

public class EventModel {
    private int ServiceEventID;
    private String UserPicture;
    private int EventScore;
    private int chatCount;
    private String EventDescription;
    private String UserName;
    private String workerNo;
    private String UserID;
    private int RoleID;

    public String getUserPicture() {
        return UserPicture;
    }

    public void setUserPicture(String userPicture) {
        UserPicture = userPicture;
    }

    public int getServiceEventID() {
        return ServiceEventID;
    }

    public void setServiceEventID(int serviceEventID) {
        ServiceEventID = serviceEventID;
    }

    public int getEventScore() {
        return EventScore;
    }

    public void setEventScore(int eventScore) {
        EventScore = eventScore;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public void setEventDescription(String eventDescription) {
        EventDescription = eventDescription;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getRoleID() {
        return RoleID;
    }

    public void setRoleID(int roleID) {
        RoleID = roleID;
    }


}
