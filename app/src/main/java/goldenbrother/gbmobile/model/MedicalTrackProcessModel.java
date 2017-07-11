package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/6/27.
 */

public class MedicalTrackProcessModel implements Parcelable{
    private String name;
    private String data;


    public MedicalTrackProcessModel() {

    }

    public MedicalTrackProcessModel(String name, String data) {
        this.name = name;
        this.data = data;
    }

    protected MedicalTrackProcessModel(Parcel in) {
        name = in.readString();
        data = in.readString();
    }

    public static final Creator<MedicalTrackProcessModel> CREATOR = new Creator<MedicalTrackProcessModel>() {
        @Override
        public MedicalTrackProcessModel createFromParcel(Parcel in) {
            return new MedicalTrackProcessModel(in);
        }

        @Override
        public MedicalTrackProcessModel[] newArray(int size) {
            return new MedicalTrackProcessModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(data);
    }
}
