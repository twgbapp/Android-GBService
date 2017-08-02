package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/8/2.
 */

public class Discussion implements Parcelable{
    private int drsNo;
    private String centerName;
    private String dormName;
    private String customerName;
    private String flaborName;
    private String fLaborNo;
    private String discussionPlace;
    private String discussionDate;

    public Discussion(){

    }

    protected Discussion(Parcel in) {
        drsNo = in.readInt();
        centerName = in.readString();
        dormName = in.readString();
        customerName = in.readString();
        flaborName = in.readString();
        fLaborNo = in.readString();
        discussionPlace = in.readString();
        discussionDate = in.readString();
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

    public String getfLaborNo() {
        return fLaborNo;
    }

    public void setfLaborNo(String fLaborNo) {
        this.fLaborNo = fLaborNo;
    }

    public String getDiscussionPlace() {
        return discussionPlace;
    }

    public void setDiscussionPlace(String discussionPlace) {
        this.discussionPlace = discussionPlace;
    }

    public String getDiscussionDate() {
        return discussionDate;
    }

    public void setDiscussionDate(String discussionDate) {
        this.discussionDate = discussionDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(drsNo);
        parcel.writeString(centerName);
        parcel.writeString(dormName);
        parcel.writeString(customerName);
        parcel.writeString(flaborName);
        parcel.writeString(fLaborNo);
        parcel.writeString(discussionPlace);
        parcel.writeString(discussionDate);
    }
}
