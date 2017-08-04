package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/8/2.
 */

public class Discussion implements Parcelable {
    private int drsNo;
    private String department;
    private String centerName;
    private String dormName;
    private String customerName;
    private String flaborName;
    private String centerId;
    private String dormId;
    private String customerNo;
    private String fLaborNo;
    private String workerNo;
    private String discussionDate;
    private String discussionReason;
    private String discussionPlace;
    private String discussionDesc;
    private String serviceRecordPath;
    private String signaturePath;

    public Discussion() {

    }

    protected Discussion(Parcel in) {
        drsNo = in.readInt();
        department = in.readString();
        centerName = in.readString();
        dormName = in.readString();
        customerName = in.readString();
        flaborName = in.readString();
        centerId = in.readString();
        dormId = in.readString();
        customerNo = in.readString();
        fLaborNo = in.readString();
        workerNo = in.readString();
        discussionDate = in.readString();
        discussionReason = in.readString();
        discussionPlace = in.readString();
        discussionDesc = in.readString();
        serviceRecordPath = in.readString();
        signaturePath = in.readString();
    }

    public static final Creator<Discussion> CREATOR = new Creator<Discussion>() {
        @Override
        public Discussion createFromParcel(Parcel in) {
            return new Discussion(in);
        }

        @Override
        public Discussion[] newArray(int size) {
            return new Discussion[size];
        }
    };

    public int getDrsNo() {
        return drsNo;
    }

    public void setDrsNo(int drsNo) {
        this.drsNo = drsNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getDormName() {
        return dormName;
    }

    public void setDormName(String dormName) {
        this.dormName = dormName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFlaborName() {
        return flaborName;
    }

    public void setFlaborName(String flaborName) {
        this.flaborName = flaborName;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getDormId() {
        return dormId;
    }

    public void setDormId(String dormId) {
        this.dormId = dormId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getfLaborNo() {
        return fLaborNo;
    }

    public void setfLaborNo(String fLaborNo) {
        this.fLaborNo = fLaborNo;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getDiscussionDate() {
        return discussionDate;
    }

    public void setDiscussionDate(String discussionDate) {
        this.discussionDate = discussionDate;
    }

    public String getDiscussionReason() {
        return discussionReason;
    }

    public void setDiscussionReason(String discussionReason) {
        this.discussionReason = discussionReason;
    }

    public String getDiscussionPlace() {
        return discussionPlace;
    }

    public void setDiscussionPlace(String discussionPlace) {
        this.discussionPlace = discussionPlace;
    }

    public String getDiscussionDesc() {
        return discussionDesc;
    }

    public void setDiscussionDesc(String discussionDesc) {
        this.discussionDesc = discussionDesc;
    }

    public String getServiceRecordPath() {
        return serviceRecordPath;
    }

    public void setServiceRecordPath(String serviceRecordPath) {
        this.serviceRecordPath = serviceRecordPath;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(drsNo);
        parcel.writeString(department);
        parcel.writeString(centerName);
        parcel.writeString(dormName);
        parcel.writeString(customerName);
        parcel.writeString(flaborName);
        parcel.writeString(centerId);
        parcel.writeString(dormId);
        parcel.writeString(customerNo);
        parcel.writeString(fLaborNo);
        parcel.writeString(workerNo);
        parcel.writeString(discussionDate);
        parcel.writeString(discussionReason);
        parcel.writeString(discussionPlace);
        parcel.writeString(discussionDesc);
        parcel.writeString(serviceRecordPath);
        parcel.writeString(signaturePath);
    }
}
