package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import goldenbrother.gbmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.helper.LogHelper;
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
    private TextView et_symptoms, et_processing_status, et_tracking_processing;
    private ImageView iv_signature, iv_medical, iv_diagnosis, iv_service;
    // extra
    private Medical medical;
    // data
    private ArrayList<MedicalSymptomModel> list_symptoms;

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
//        tv_file_upload = (TextView) findViewById(R.id.tv_medical_record_file_upload);
        iv_signature = (ImageView) findViewById(R.id.iv_medical_record_signature_path);
        iv_medical = (ImageView) findViewById(R.id.iv_medical_record_medical_path);
        iv_diagnosis = (ImageView) findViewById(R.id.iv_medical_record_diagnosis_path);
        iv_service = (ImageView) findViewById(R.id.iv_medical_record_service_path);
        findViewById(R.id.iv_medical_record_info).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_symptoms).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_processing_status).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_tracking_processing).setOnClickListener(this);
        findViewById(R.id.iv_medical_record_file_upload).setOnClickListener(this);
        findViewById(R.id.tv_medical_record_save).setOnClickListener(this);

        // extra
        medical = getIntent().getExtras().getParcelable("medical");
        if (medical != null && medical.getMtrsno() != 0) {
            getMedicalRecord(medical.getMtrsno());
        } else {
            medical = new Medical();
            getMedicalTreatmentCode();
        }
    }

    private void getMedicalTreatmentCode() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getMedicalTreatmentCode");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetMedicalTreatmentCode(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetMedicalTreatmentCode extends IAsyncTask {

        private boolean isAdd;
        private ArrayList<MedicalSymptomModel> list_first;
        private ArrayList<MedicalSymptomModel> list_second;

        GetMedicalTreatmentCode(Context context, JSONObject json, String url) {
            super(context, json, url);
            isAdd = medical.getMtrsno() == 0;
            if (list_symptoms == null) list_symptoms = new ArrayList<>();
            list_first = new ArrayList<>();
            list_second = new ArrayList<>();
        }

        private void sortSymptoms() {
            list_symptoms.clear();
            for (MedicalSymptomModel m : list_first) {
                list_symptoms.add(m);
                for (MedicalSymptomModel mm : list_second) {
                    if (mm.getCode().startsWith(m.getCode())) {
                        list_symptoms.add(mm);
                    }
                }
            }
        }

        private void syncMedicalSymptom() {
            for (MedicalSymptomModel ms : medical.getSymptom()) {
                for (MedicalSymptomModel ms2 : list_symptoms) {
                    if (ms.getCode().equals(ms2.getCode())) {
                        ms.setValue(ms2.getValue());
                        break;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getMedicalTreatmentCode(response, list_first, list_second);
                    if (result == ApiResultHelper.SUCCESS) {
                        sortSymptoms();
                        if (isAdd) {

                        } else {
                            syncMedicalSymptom();
                            showMedical();
                        }
                    } else {
                        t(getString(R.string.fail) + "GetMedicalTreatmentCode");
                        finish();
                    }
                    break;
            }
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
                    int result = ApiResultHelper.getMedicalRecord(response, medical);
                    if (result == ApiResultHelper.SUCCESS) {
//                        t(R.string.success);
                        getMedicalTreatmentCode();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void showMedical() {
        showPatientInfo();
        showSymptom();
        showProcessStatus();
        showTrackProcess();
        showUploadFile();
    }

    private void showPatientInfo() {
        if (medical.getPatient() != null) {
            tv_name.setText(String.format(getString(R.string.name) + " : %s", medical.getPatient().getName()));
            tv_gender.setText(String.format(getString(R.string.sex) + " : %s", getString(medical.getPatient().isGender() ? R.string.male : R.string.female)));
            tv_date.setText(String.format(getString(R.string.medical_fill_out_date) + " : %s", medical.getPatient().getRecordDate()));
            tv_birthday.setText(String.format(getString(R.string.room_id) + " : %s", medical.getPatient().getRoomID()));
            tv_blood_type.setText(String.format(getString(R.string.medical_blood_type) + " : %s", getBloodTypeName(medical.getPatient().getBloodType())));
        }
    }

    private void showSymptom() {
        String result = "";
        for (MedicalSymptomModel ms : medical.getSymptom()) {
            result += (result.length() == 0 ? "" : "\n") + ms.getValue();
        }
        et_symptoms.setText(result);
    }

    private void showProcessStatus() {
        String result = "";
        for (MedicalProcessStatusModel m : medical.getProcessingStatus()) {
            result += (result.isEmpty() ? "" : "\n") + m.getName();
        }
        et_processing_status.setText(result);
    }

    private void showTrackProcess() {
        String result = "";
        for (MedicalTrackProcessModel m : medical.getTrackProcess()) {
            result += (result.isEmpty() ? "" : "\n") + m.getName();
        }
        et_tracking_processing.setText(result);
    }

    private void showUploadFile() {
        String signaturePath = medical.getSignaturePath();
        String medicalPath = medical.getMedicalCertificatePath();
        String diagnosisPath = medical.getDiagnosticCertificatePath();
        String servicePath = medical.getServiceRecordPath();

        if (signaturePath != null && signaturePath.trim().length() != 0)
            Picasso.with(this).load(medical.getSignaturePath()).into(iv_signature);
        if (medicalPath != null && medicalPath.trim().length() != 0)
            Picasso.with(this).load(medical.getMedicalCertificatePath()).into(iv_medical);
        if (diagnosisPath != null && diagnosisPath.trim().length() != 0)
            Picasso.with(this).load(medical.getDiagnosticCertificatePath()).into(iv_diagnosis);
        if (servicePath != null && servicePath.trim().length() != 0)
            Picasso.with(this).load(medical.getServiceRecordPath()).into(iv_service);
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
            j.put("recordDate", medical.getPatient().getRecordDate()); //recordDate 就診日期
            j.put("customerNo", medical.getPatient().getCustomerNo());
            j.put("flaborNo", medical.getPatient().getFlaborNo());
            j.put("roomID", medical.getPatient().getRoomID()); //getDormUserInfo406
            j.put("dormID", medical.getPatient().getDormID()); //getDormUserInfo5412
            j.put("bloodType", medical.getPatient().getBloodType());
            j.put("age", medical.getPatient().getAge());
            j.put("centerDirectorID", medical.getPatient().getCenterDirectorID()); //getDormUserInfo
            j.put("createId", RoleInfo.getInstance().getUserID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("createTime", TimeHelper.getStandard());
            j.put("diagnosticCertificate", medical.getDiagnosticCertificatePath());
            j.put("serviceRecord", medical.getServiceRecordPath());
            j.put("medicalCertificate", medical.getMedicalCertificatePath());
            j.put("signature", medical.getSignaturePath());

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
                    int result = ApiResultHelper.commonCreate(response);
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

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        b.putParcelable("medical", medical);
        switch (v.getId()) {
            case R.id.iv_medical_record_info: // 查詢外勞
                openActivityForResult(MedicalPatientInfoActivity.class, REQUEST_INFO, b);
                break;
            case R.id.iv_medical_record_symptoms: // 症狀列表
                openActivityForResult(MedicalSymptomActivity.class, REQUEST_TREATMENT, b);
                break;
            case R.id.iv_medical_record_processing_status: // 處理狀況
                if (medical.getPatient() == null || medical.getPatient().getDormID() == null || medical.getPatient().getDormID().isEmpty()) {
                    t(R.string.can_not_get_patient);
                    return;
                }
                openActivityForResult(MedicalProcessStatusActivity.class, REQUEST_PROCESS_STATUS, b);
                break;
            case R.id.iv_medical_record_tracking_processing: // 追蹤與處理
                openActivityForResult(MedicalTrackProcessActivity.class, REQUEST_TRACK_PROCESS, b);
                break;
            case R.id.iv_medical_record_file_upload:
                openActivityForResult(MedicalFileUploadActivity.class, REQUEST_FILE_UPLOAD, b);
                break;
            case R.id.tv_medical_record_save: // 新增醫療紀錄
                if (medical.getPatient().getName() == null ) {
                    t(R.string.can_not_get_patient);
                    return;
                }
                if(medical.getSymptom().isEmpty()){
                    t(R.string.can_not_get_symptom);
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
        medical = data.getParcelableExtra("medical");
        switch (requestCode) {
            case REQUEST_INFO:
                showPatientInfo();
                break;
            case REQUEST_TREATMENT:
                showSymptom();
                break;
            case REQUEST_PROCESS_STATUS:
                showProcessStatus();
                break;
            case REQUEST_TRACK_PROCESS:
                showTrackProcess();
                break;
            case REQUEST_FILE_UPLOAD:
                showUploadFile();
                break;
        }
    }
}

