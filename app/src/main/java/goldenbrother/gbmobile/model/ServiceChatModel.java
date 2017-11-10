package goldenbrother.gbmobile.model;

public class ServiceChatModel{
    private int SGCNo;
    private int serviceGroupID;
    private String userID;
    private String workerNo;
    private String userPicture;
    private String userName;
    private String content;
    private String chatDate;
    private int chatCount;
    private String staffID;
    private String customerNo;

    public int getSGCNo() {
        return SGCNo;
    }

    public void setSGCNo(int SGCNo) {
        this.SGCNo = SGCNo;
    }

    public int getServiceGroupID() {
        return serviceGroupID;
    }

    public void setServiceGroupID(int serviceGroupID) {
        this.serviceGroupID = serviceGroupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }


}
