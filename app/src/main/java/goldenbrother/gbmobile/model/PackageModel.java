package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017/2/13.
 */

public class PackageModel implements Parcelable{
    private int packageID;
    private String description;
    private String pickNumber;
    private String arriveDate;
    private String baseStr;

    public PackageModel(){

    }

    protected PackageModel(Parcel in) {
        packageID = in.readInt();
        description = in.readString();
        pickNumber = in.readString();
        arriveDate = in.readString();
        baseStr = in.readString();
    }

    public static final Creator<PackageModel> CREATOR = new Creator<PackageModel>() {
        @Override
        public PackageModel createFromParcel(Parcel in) {
            return new PackageModel(in);
        }

        @Override
        public PackageModel[] newArray(int size) {
            return new PackageModel[size];
        }
    };

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(String pickNumber) {
        this.pickNumber = pickNumber;
    }

    public String getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getBaseStr() {
        return baseStr;
    }

    public void setBaseStr(String baseStr) {
        this.baseStr = baseStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(packageID);
        parcel.writeString(description);
        parcel.writeString(pickNumber);
        parcel.writeString(arriveDate);
        parcel.writeString(baseStr);
    }
}
