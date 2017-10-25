package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haojun on 2017/6/27.
 */

public class MedicalProcessStatusModel implements Parcelable{
    private int processingStatus;
    private String processingStatusHospitalSNo;
    private String processingStatusToHospitalID;
    private String processingStatusMedicalCertificate;
    private String processingStatusOtherMemo;
    //
    private String content;

    public MedicalProcessStatusModel() {

    }

    public MedicalProcessStatusModel(int processingStatus, String processingStatusHospitalSNo, String processingStatusToHospitalID, String processingStatusMedicalCertificate, String processingStatusOtherMemo, String content) {
        this.processingStatus = processingStatus;
        this.processingStatusHospitalSNo = processingStatusHospitalSNo;
        this.processingStatusToHospitalID = processingStatusToHospitalID;
        this.processingStatusMedicalCertificate = processingStatusMedicalCertificate;
        this.processingStatusOtherMemo = processingStatusOtherMemo;
        this.content = content;
    }

    public int getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(int processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getProcessingStatusHospitalSNo() {
        return processingStatusHospitalSNo;
    }

    public void setProcessingStatusHospitalSNo(String processingStatusHospitalSNo) {
        this.processingStatusHospitalSNo = processingStatusHospitalSNo;
    }

    public String getProcessingStatusToHospitalID() {
        return processingStatusToHospitalID;
    }

    public void setProcessingStatusToHospitalID(String processingStatusToHospitalID) {
        this.processingStatusToHospitalID = processingStatusToHospitalID;
    }

    public String getProcessingStatusMedicalCertificate() {
        return processingStatusMedicalCertificate;
    }

    public void setProcessingStatusMedicalCertificate(String processingStatusMedicalCertificate) {
        this.processingStatusMedicalCertificate = processingStatusMedicalCertificate;
    }

    public String getProcessingStatusOtherMemo() {
        return processingStatusOtherMemo;
    }

    public void setProcessingStatusOtherMemo(String processingStatusOtherMemo) {
        this.processingStatusOtherMemo = processingStatusOtherMemo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected MedicalProcessStatusModel(Parcel in) {
        processingStatus = in.readInt();
        processingStatusHospitalSNo = in.readString();
        processingStatusToHospitalID = in.readString();
        processingStatusMedicalCertificate = in.readString();
        processingStatusOtherMemo = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(processingStatus);
        dest.writeString(processingStatusHospitalSNo);
        dest.writeString(processingStatusToHospitalID);
        dest.writeString(processingStatusMedicalCertificate);
        dest.writeString(processingStatusOtherMemo);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
