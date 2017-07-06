package goldenbrother.gbmobile.model;

import java.util.ArrayList;

/**
 * Created by asus on 2016/12/8.
 */

public class ServiceModel {
    private int ServiceGroupID;
    private String ServiceName;
    private String StaffID;
    private ArrayList<ServiceChatModel> list_GroupChat;

    public ServiceModel() {
        list_GroupChat = new ArrayList<>();
    }

    public int getServiceGroupID() {
        return ServiceGroupID;
    }

    public void setServiceGroupID(int serviceGroupID) {
        ServiceGroupID = serviceGroupID;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getStaffID() {
        return StaffID;
    }

    public void setStaffID(String staffID) {
        StaffID = staffID;
    }

    public ArrayList<ServiceChatModel> getList_GroupChat() {
        return list_GroupChat;
    }
}
