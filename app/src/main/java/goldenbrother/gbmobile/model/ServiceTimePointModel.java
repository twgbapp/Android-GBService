package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/8.
 */

public class ServiceTimePointModel {
    private int ServiceGroupID;
    private String TimePoint;

    public ServiceTimePointModel(){

    }

    public ServiceTimePointModel(int serviceGroupID, String timePoint) {
        ServiceGroupID = serviceGroupID;
        TimePoint = timePoint;
    }

    public int getServiceGroupID() {
        return ServiceGroupID;
    }

    public void setServiceGroupID(int serviceGroupID) {
        ServiceGroupID = serviceGroupID;
    }

    public String getTimePoint() {
        return TimePoint;
    }

    public void setTimePoint(String timePoint) {
        TimePoint = timePoint;
    }
}
