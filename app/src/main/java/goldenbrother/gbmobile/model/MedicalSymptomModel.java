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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicalSymptomModel that = (MedicalSymptomModel) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
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
