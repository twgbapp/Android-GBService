package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2017/3/13.
 */

public class RepairRecordModel {
    private int rrsNo;
    private String centerId;
    private String dormID;
    private String happenDate;
    private String dutyID;
    private String deadLineDate;
    private String place;
    private String eventKindStr;
    private String eventDesc;
    private String procResult;
    private String sourceDesc;
    private int sourceEventID;
    private String customerNo;
    private String flaborNo;
    private String status;

    public RepairRecordModel() {

    }

    public RepairRecordModel(int rrsNo, String happenDate, String eventDesc, String status) {
        this.rrsNo = rrsNo;
        this.happenDate = happenDate;
        this.eventDesc = eventDesc;
        this.status = status;
    }

    public int getRrsNo() {
        return rrsNo;
    }

    public void setRrsNo(int rrsNo) {
        this.rrsNo = rrsNo;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getDormID() {
        return dormID;
    }

    public void setDormID(String dormID) {
        this.dormID = dormID;
    }

    public String getHappenDate() {
        return happenDate;
    }

    public void setHappenDate(String happenDate) {
        this.happenDate = happenDate;
    }

    public String getDutyID() {
        return dutyID;
    }

    public void setDutyID(String dutyID) {
        this.dutyID = dutyID;
    }

    public String getDeadLineDate() {
        return deadLineDate;
    }

    public void setDeadLineDate(String deadLineDate) {
        this.deadLineDate = deadLineDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEventKindStr() {
        return eventKindStr;
    }

    public void setEventKindStr(String eventKindStr) {
        this.eventKindStr = eventKindStr;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getProcResult() {
        return procResult;
    }

    public void setProcResult(String procResult) {
        this.procResult = procResult;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public int getSourceEventID() {
        return sourceEventID;
    }

    public void setSourceEventID(int sourceEventID) {
        this.sourceEventID = sourceEventID;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getFlaborNo() {
        return flaborNo;
    }

    public void setFlaborNo(String flaborNo) {
        this.flaborNo = flaborNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
