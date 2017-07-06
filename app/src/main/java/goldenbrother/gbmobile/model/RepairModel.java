package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RepairModel implements Parcelable { //t

    private int RepairNumber;
    private String RepairContent;
    private int RepairKind;

    public RepairModel() {

    }


    private RepairModel(Parcel in) {
        RepairNumber = in.readInt();
        RepairContent = in.readString();
        RepairKind = in.readInt();
    }

    public static final Creator<RepairModel> CREATOR = new Creator<RepairModel>() {
        @Override
        public RepairModel createFromParcel(Parcel in) {
            return new RepairModel(in);
        }

        @Override
        public RepairModel[] newArray(int size) {
            return new RepairModel[size];
        }
    };

    public int getRepairNumber() {
        return RepairNumber;
    }

    public void setRepairNumber(int repairNumber) {
        RepairNumber = repairNumber;
    }

    public String getRepairContent() {
        return RepairContent;
    }

    public void setRepairContent(String repairContent) {
        RepairContent = repairContent;
    }

    public int getRepairKind() {
        return RepairKind;
    }

    public void setRepairKind(int repairKind) {
        RepairKind = repairKind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RepairNumber);
        dest.writeString(RepairContent);
        dest.writeInt(RepairKind);
    }
}
