package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicalSymptomModel implements Parcelable {

    private String code;
    private String value;

    public MedicalSymptomModel(){

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected MedicalSymptomModel(Parcel in) {
        code = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MedicalSymptomModel> CREATOR = new Creator<MedicalSymptomModel>() {
        @Override
        public MedicalSymptomModel createFromParcel(Parcel in) {
            return new MedicalSymptomModel(in);
        }

        @Override
        public MedicalSymptomModel[] newArray(int size) {
            return new MedicalSymptomModel[size];
        }
    };
}
