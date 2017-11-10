package goldenbrother.gbmobile.model;

public class ServiceGroupMember {
    private int serviceGroupID;
    private String userID;

    public ServiceGroupMember() {

    }

    public ServiceGroupMember(int serviceGroupID, String userID) {
        this.serviceGroupID = serviceGroupID;
        this.userID = userID;
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
}
