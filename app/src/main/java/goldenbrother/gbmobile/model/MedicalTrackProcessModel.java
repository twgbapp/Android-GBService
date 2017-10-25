package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/6/27.
 */

public class MedicalTrackProcessModel implements Parcelable{
    private int treatmentStatus;
    private String treatmentMemo;
    //
    private String content;

    public MedicalTrackProcessModel(){

    }

    public MedicalTrackProcessModel(int treatmentStatus, String treatmentMemo, String content) {
        this.treatmentStatus = treatmentStatus;
        this.treatmentMemo = treatmentMemo;
        this.content = content;
    }

    public int getTreatmentStatus() {
        return treatmentStatus;
    }

    public void setTreatmentStatus(int treatmentStatus) {
        this.treatmentStatus = treatmentStatus;
    }

    public String getTreatmentMemo() {
        return treatmentMemo;
    }

    public void setTreatmentMemo(String treatmentMemo) {
        this.treatmentMemo = treatmentMemo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected MedicalTrackProcessModel(Parcel in) {
        treatmentStatus = in.readInt();
        treatmentMemo = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(treatmentStatus);
        dest.writeString(treatmentMemo);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
