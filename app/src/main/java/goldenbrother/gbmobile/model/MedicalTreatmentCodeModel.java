package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class MedicalTreatmentCodeModel implements Parcelable {

    private String columnName;
    private String code;
    private String value;
    public MedicalTreatmentCodeModel(){

    }

    protected MedicalTreatmentCodeModel(Parcel in) {
        columnName = in.readString();
        code = in.readString();
        value = in.readString();
    }

    public static final Creator<MedicalTreatmentCodeModel> CREATOR = new Creator<MedicalTreatmentCodeModel>() {
        @Override
        public MedicalTreatmentCodeModel createFromParcel(Parcel in) {
            return new MedicalTreatmentCodeModel(in);
        }

        @Override
        public MedicalTreatmentCodeModel[] newArray(int size) {
            return new MedicalTreatmentCodeModel[size];
        }
    };

    public MedicalTreatmentCodeModel(String columnName, String code, String value) {
        this.columnName = columnName;
        this.code = code;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    @Override
    public String toString() {
        return "MedicalTreatmentCodeModel{" +
                "columnName='" + columnName + '\'' +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(columnName);
        dest.writeString(code);
        dest.writeString(value);
    }
}
