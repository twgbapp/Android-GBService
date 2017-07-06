package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/6/27.
 */

public class MedicalProcessStatusModel implements Parcelable{
    private String name;
    private String data;

    public MedicalProcessStatusModel(){

    }

    public MedicalProcessStatusModel(String name, String data) {
        this.name = name;
        this.data = data;
    }

    protected MedicalProcessStatusModel(Parcel in) {
        name = in.readString();
        data = in.readString();
    }

    public static final Creator<MedicalProcessStatusModel> CREATOR = new Creator<MedicalProcessStatusModel>() {
        @Override
        public MedicalProcessStatusModel createFromParcel(Parcel in) {
            return new MedicalProcessStatusModel(in);
        }

        @Override
        public MedicalProcessStatusModel[] newArray(int size) {
            return new MedicalProcessStatusModel[size];
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
