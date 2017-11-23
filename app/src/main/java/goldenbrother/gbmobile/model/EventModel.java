package goldenbrother.gbmobile.model;

public class EventModel {
    private int serviceEventID;
    private String userID;
    private String eventDescription;
    private String eventKind;
    private String staffID;
    //
    private String userName;
    private String UserPicture;
    private String workerNo;
    private String eventKindValue;
    private int eventScore;
    private int chatCount;
    private int roleID;

    public String getEventKindValue() {
        return eventKindValue;
    }

    public void setEventKindValue(String eventKindValue) {
        this.eventKindValue = eventKindValue;
    }

    public int getServiceEventID() {
        return serviceEventID;
    }

    public void setServiceEventID(int serviceEventID) {
        this.serviceEventID = serviceEventID;
    }

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

    public String getEventKind() {
        return eventKind;
    }

    public void setEventKind(String eventKind) {
        this.eventKind = eventKind;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPicture() {
        return UserPicture;
    }

    public void setUserPicture(String userPicture) {
        UserPicture = userPicture;
    }

    public int getEventScore() {
        return eventScore;
    }

    public void setEventScore(int eventScore) {
        this.eventScore = eventScore;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }
}
