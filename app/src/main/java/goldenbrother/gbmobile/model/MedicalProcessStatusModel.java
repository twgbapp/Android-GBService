package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/6/27.
 */

public class MedicalProcessStatusModel implements Parcelable{
    private int processingStatus; // 48~52
    private String processingStatusToHospitalID;
    private String processingStatusHospitalSNo;
    private String processingStatusOtherMemo;
    private String processingStatusMedicalCertificate;
    private String name;
    private String data;

    public MedicalProcessStatusModel() {

    }

    public MedicalProcessStatusModel(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public int getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(int processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getProcessingStatusToHospitalID() {
        return processingStatusToHospitalID;
    }

    public void setProcessingStatusToHospitalID(String processingStatusToHospitalID) {
        this.processingStatusToHospitalID = processingStatusToHospitalID;
    }

    public String getProcessingStatusHospitalSNo() {
        return processingStatusHospitalSNo;
    }

    public void setProcessingStatusHospitalSNo(String processingStatusHospitalSNo) {
        this.processingStatusHospitalSNo = processingStatusHospitalSNo;
    }

    public String getProcessingStatusOtherMemo() {
        return processingStatusOtherMemo;
    }

    public void setProcessingStatusOtherMemo(String processingStatusOtherMemo) {
        this.processingStatusOtherMemo = processingStatusOtherMemo;
    }

    public String getProcessingStatusMedicalCertificate() {
        return processingStatusMedicalCertificate;
    }

    public void setProcessingStatusMedicalCertificate(String processingStatusMedicalCertificate) {
        this.processingStatusMedicalCertificate = processingStatusMedicalCertificate;
    }

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

    protected MedicalProcessStatusModel(Parcel in) {
        processingStatus = in.readInt();
        processingStatusToHospitalID = in.readString();
        processingStatusHospitalSNo = in.readString();
        processingStatusOtherMemo = in.readString();
        processingStatusMedicalCertificate = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(processingStatus);
        parcel.writeString(processingStatusToHospitalID);
        parcel.writeString(processingStatusHospitalSNo);
        parcel.writeString(processingStatusOtherMemo);
        parcel.writeString(processingStatusMedicalCertificate);
        parcel.writeString(name);
        parcel.writeString(data);
    }
}
