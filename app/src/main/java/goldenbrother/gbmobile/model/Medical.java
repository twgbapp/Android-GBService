package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by haojun on 2017/7/11.
 */

public class Medical implements Parcelable {
    private int mtrsno;
    private Patient patient;
    private ArrayList<MedicalSymptomModel> list_symptom;
    private ArrayList<MedicalProcessStatusModel> list_processing_status;
    private ArrayList<MedicalTrackProcessModel> list_track_process;
    private String signaturePath;
    private String medicalCertificatePath;
    private String diagnosticCertificatePath;
    private String serviceRecordPath;
    private String createId;
    private String createTime;

    public Medical() {
        patient = new Patient();
        list_symptom = new ArrayList<>();
        list_processing_status = new ArrayList<>();
        list_track_process = new ArrayList<>();
    }

    protected Medical(Parcel in) {
        mtrsno = in.readInt();
        patient = in.readParcelable(Patient.class.getClassLoader());
        list_symptom = in.createTypedArrayList(MedicalSymptomModel.CREATOR);
        list_processing_status = in.createTypedArrayList(MedicalProcessStatusModel.CREATOR);
        list_track_process = in.createTypedArrayList(MedicalTrackProcessModel.CREATOR);
        diagnosticCertificatePath = in.readString();
        serviceRecordPath = in.readString();
        medicalCertificatePath = in.readString();
        signaturePath = in.readString();
        createId = in.readString();
        createTime = in.readString();
    }

    public static final Creator<Medical> CREATOR = new Creator<Medical>() {
        @Override
        public Medical createFromParcel(Parcel in) {
            return new Medical(in);
        }

        @Override
        public Medical[] newArray(int size) {
            return new Medical[size];
        }
    };

    public int getMtrsno() {
        return mtrsno;
    }

    public void setMtrsno(int mtrsno) {
        this.mtrsno = mtrsno;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getDiagnosticCertificatePath() {
        return diagnosticCertificatePath;
    }

    public void setDiagnosticCertificatePath(String diagnosticCertificatePath) {
        this.diagnosticCertificatePath = diagnosticCertificatePath;
    }

    public String getServiceRecordPath() {
        return serviceRecordPath;
    }

    public void setServiceRecordPath(String serviceRecordPath) {
        this.serviceRecordPath = serviceRecordPath;
    }

    public String getMedicalCertificatePath() {
        return medicalCertificatePath;
    }

    public void setMedicalCertificatePath(String medicalCertificatePath) {
        this.medicalCertificatePath = medicalCertificatePath;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public ArrayList<MedicalSymptomModel> getSymptom() {
        return list_symptom;
    }

    public ArrayList<MedicalProcessStatusModel> getProcessingStatus() {
        return list_processing_status;
    }

    public ArrayList<MedicalTrackProcessModel> getTrackProcess() {
        return list_track_process;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mtrsno);
        parcel.writeParcelable(patient, i);
        parcel.writeTypedList(list_symptom);
        parcel.writeTypedList(list_processing_status);
        parcel.writeTypedList(list_track_process);
        parcel.writeString(diagnosticCertificatePath);
        parcel.writeString(serviceRecordPath);
        parcel.writeString(medicalCertificatePath);
        parcel.writeString(signaturePath);
        parcel.writeString(createId);
        parcel.writeString(createTime);
    }
}
