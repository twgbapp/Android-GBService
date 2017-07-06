package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/8.
 */

public class EventTimePointModel {
    private int ServiceEventID;
    private String TimePoint;

    public EventTimePointModel() {

    }

    public EventTimePointModel(int serviceEventID, String timePoint) {
        ServiceEventID = serviceEventID;
        TimePoint = timePoint;
    }

    public int getServiceEventID() {
        return ServiceEventID;
    }

    public void setServiceEventID(int serviceEventID) {
        ServiceEventID = serviceEventID;
    }

    public String getTimePoint() {
        return TimePoint;
    }

    public void setTimePoint(String timePoint) {
        TimePoint = timePoint;
    }
}
