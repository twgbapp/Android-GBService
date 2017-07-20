package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import goldenbrother.gbmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.Patient;

import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.MedicalTrackProcessModel;
import goldenbrother.gbmobile.model.MedicalSymptomModel;
import goldenbrother.gbmobile.model.RoleInfo;

import java.util.ArrayList;

public class MedicalRecordActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_INFO = 0;
    public static final int REQUEST_TREATMENT = 1;
    public static final int REQUEST_PROCESS_STATUS = 2;
    public static final int REQUEST_TRACK_PROCESS = 3;
    public static final int REQUEST_FILE_UPLOAD = 4;
    // ui
    private TextView tv_name, tv_gender, tv_birthday, tv_blood_type, tv_date;
    private TextView et_symptoms, et_processing_status, et_tracking_processing, tv_file_upload;
    // extra
    private Medical medical;
    // data
    private Patient patient;

    private String diagnosticCertificatePath, serviceRecordPath, medicalCertificatePath, signaturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record);

        // ui reference
        tv_name = (TextView) findViewById(R.id.tv_medical_record_name);
        tv_gender = (TextView) findViewById(R.id.tv_medical_record_gender);
        tv_birthday = (TextView) findViewById(R.id.tv_medical_record_birthday);
        tv_blood_type = (TextView) findViewById(R.id.tv_medical_record_blood_type);
        tv_date = (TextView) findViewById(R.id.tv_medical_record_date);
        et_symptoms = (TextView) findViewById(R.id.tv_medical_record_symptoms);
        et_processing_status = (TextView) findViewById(R.id.tv_medical_record_processing_status);
        et_tracking_processing = (TextView) findViewById(R.id.tv_medical_record_tracking_processing);
        tv_file_upload = (TextView) findViewById(R.id.tv_medical_record_file_upload);
        findViewById(R.id.iv_medical_record_info).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_symptoms).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_processing_status).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_tracking_processing).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_file_upload).setOnClickListener(this);
        findViewById(R.id.tv_medical_record_save).setOnClickListener(this);

        // extra
        medical = getIntent().getExtras().getParcelable("medical");
        if (medical != null && medical.getMtrsno() != 0) {
            tv_name.setText(medical.getFlaborName());
            getMedicalRecord(medical.getMtrsno());
        } else {
            medical = new Medical();
        }
    }

    private void getMedicalRecord(int mtrsNo) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getMedicalRecord");
            j.put("mtrsno", mtrsNo);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new GetMedicalRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetMedicalRecord extends IAsyncTask {

        GetMedicalRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getMedicalRecord(response, medical, patient);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }


    private void showPatientInfo() {
        if (patient != null) {
            tv_name.setText(String.format(getString(R.string.name) + " : %s", patient.getName()));
            tv_gender.setText(String.format(getString(R.string.sex) + " : %s", getString(patient.isGender() ? R.string.male : R.string.female)));
            tv_date.setText(String.format(getString(R.string.medical_fill_out_date) + " : %s", patient.getJiuZhen_date()));
            tv_birthday.setText(String.format(getString(R.string.birthday) + " : %s", patient.getDate()));
            tv_blood_type.setText(String.format(getString(R.string.medical_blood_type) + " : %s", getBloodTypeName(patient.getBloodType())));
        }
    }

    private String getBloodTypeName(String code) {
        String[] codes = getResources().getStringArray(R.array.blood_type_code);
        String[] names = getResources().getStringArray(R.array.blood_type_name);
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(code)) {
                return names[i];
            }
        }
        return "";
    }

    private void addMedicalRecord() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addMedicalRecord");
            j.put("recordDate", patient.getJiuZhen_date()); //recordDate JiuZhen_dat 就診日期
            j.put("customerNo", patient.getCustomerNo());
            j.put("flaborNo", patient.getFlaborNo());
            j.put("roomID", patient.getRoomID()); //getDormUserInfo406
            j.put("dormID", patient.getDormID()); //getDormUserInfo5412
            j.put("bloodType", patient.getBloodType());
            j.put("age", patient.getAge());
            j.put("centerDirectorID", patient.getCenterDirectorID()); //getDormUserInfo
            j.put("createId", RoleInfo.getInstance().getUserID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("createTime", TimeHelper.getStandard());
            j.put("diagnosticCertificate", diagnosticCertificatePath);
            j.put("serviceRecord", serviceRecordPath);
            j.put("medicalCertificate", medicalCertificatePath);
            j.put("signature", signaturePath);

            JSONArray arrTreatment = new JSONArray();//症狀
            for (MedicalSymptomModel m : medical.getSymptom()) {
                //arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/null");
                if (m.getCode().equals("425")) {
                    arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/" + m.getValue());
                } else {
                    arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/null");
                }
            }
            j.put("medicalTreatmentRecordDetail", arrTreatment);

            JSONArray arrProcessing = new JSONArray(); //處理狀況
            for (MedicalProcessStatusModel m : medical.getProcessingStatus()) {
                arrProcessing.put(m.getData());
            }

            j.put("medicalProcessingRecord", arrProcessing);


            JSONArray arrTrack = new JSONArray(); //追蹤與處理
            for (MedicalTrackProcessModel m : medical.getTrackProcess()) {
                arrTrack.put(m.getData());
            }
            j.put("medicalTreatmentProcessingRecord", arrTrack);
            j.put("logStatus", true);

            new AddMedicalRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddMedicalRecord extends IAsyncTask {

        AddMedicalRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addMedicalRecord(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updateUploadFileString() {
        StringBuilder sb = new StringBuilder("FilePath");
        if (!signaturePath.isEmpty())
            sb.append("\nSignature:").append(signaturePath);
        if (!medicalCertificatePath.isEmpty())
            sb.append("\nMedical:").append(medicalCertificatePath);
        if (!diagnosticCertificatePath.isEmpty())
            sb.append("\nDiagnostic:").append(diagnosticCertificatePath);
        if (!serviceRecordPath.isEmpty())
            sb.append("\nService:").append(serviceRecordPath);
        tv_file_upload.setText(sb.toString());
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.iv_medical_record_info: // 查詢外勞
                b.putParcelable("patient", patient);
                openActivityForResult(MedicalPatientInfoActivity.class, REQUEST_INFO, b);
                break;
            case R.id.iv_medical_record_symptoms: // 症狀列表
                openActivityForResult(MedicalSymptomActivity.class, REQUEST_TREATMENT);
                break;
            case R.id.iv_medical_record_processing_status: // 處理狀況
                if (patient == null || patient.getDormID() == null || patient.getDormID().isEmpty()) {
                    t("Can't get dormID");
                    return;
                }
                b.putParcelable("patient", patient);
                openActivityForResult(MedicalProcessStatusActivity.class, REQUEST_PROCESS_STATUS, b);
                break;
            case R.id.iv_medical_record_tracking_processing: // 追蹤與處理
                openActivityForResult(MedicalTrackProcessActivity.class, REQUEST_TRACK_PROCESS);
                break;
            case R.id.iv_medical_record_file_upload:
                openActivityForResult(MedicalFileUploadActivity.class, REQUEST_FILE_UPLOAD);
                break;
            case R.id.tv_medical_record_save: // 新增醫療紀錄
                if (patient == null) {
                    t("No patient");
                    return;
                }
                addMedicalRecord();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_INFO: // 查詢外勞(回傳)
                patient = data.getExtras().getParcelable("patient");
                showPatientInfo();
                break;
            case REQUEST_TREATMENT: // 症狀列表(回傳)
                ArrayList<MedicalSymptomModel> list = data.getExtras().getParcelableArrayList("mtrCodeGroup");
                medical.getSymptom().clear();
                if (list != null) medical.getSymptom().addAll(list);
                String str_symptoms = "";
                for (MedicalSymptomModel m : medical.getSymptom()) {
                    str_symptoms += (str_symptoms.isEmpty() ? "" : "\n") + m.getValue();
                }
                et_symptoms.setText(str_symptoms);
                break;
            case REQUEST_PROCESS_STATUS: // 處理狀況(回傳)
                ArrayList<MedicalProcessStatusModel> list1 = data.getExtras().getParcelableArrayList("processStatus");
                medical.getProcessingStatus().clear();
                if (list1 != null) medical.getProcessingStatus().addAll(list1);
                String str_processing_status = "";
                for (MedicalProcessStatusModel m : medical.getProcessingStatus()) {
                    str_processing_status += (str_processing_status.isEmpty() ? "" : "\n") + m.getName();
                }
                et_processing_status.setText(str_processing_status);
                break;
            case REQUEST_TRACK_PROCESS: // 追蹤與處理(回傳)
                ArrayList<MedicalTrackProcessModel> list2 = data.getExtras().getParcelableArrayList("trackProcess");
                medical.getTrackProcess().clear();
                if (list2 != null) medical.getTrackProcess().addAll(list2);
                String str_track_process = "";
                for (MedicalTrackProcessModel m : medical.getTrackProcess()) {
                    str_track_process += (str_track_process.isEmpty() ? "" : "\n") + m.getName();
                }
                et_tracking_processing.setText(str_track_process);
                break;
            case REQUEST_FILE_UPLOAD:
                signaturePath = data.getStringExtra("signaturePath");
                medicalCertificatePath = data.getStringExtra("medicalPath");
                diagnosticCertificatePath = data.getStringExtra("diagnosticPath");
                serviceRecordPath = data.getStringExtra("servicePath");
                updateUploadFileString();
                break;
        }
    }
}

