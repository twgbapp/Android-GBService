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

import goldenbrother.gbmobile.bean.Patient;

import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.MedicalTreatmentCodeModel;
import goldenbrother.gbmobile.model.RoleInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class MedicalRecordActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_INFO = 0;
    public static final int REQUEST_TREATMENT = 1;
    public static final int REQUEST_PROCESS_STATUS = 2;
    public static final int REQUEST_TRACK_PROCESS = 3;
    public static final int REQUEST_FILE_UPLOAD = 4;
    // ui
    private TextView tv_name, tv_gender, tv_birthday, tv_date;
    private TextView et_symptoms, et_processing_status, et_tracking_processing, tv_file_upload;
    // data
    private Patient patient;
    private ArrayList<MedicalTreatmentCodeModel> list_treatment_code;
    private ArrayList<MedicalProcessStatusModel> list_processing_status;
    private ArrayList<MedicalTrackProcessModel> list_track_process;
    private String diagnosticCertificatePath, serviceRecordPath, medicalCertificatePath, signaturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record);

        // ui reference
        tv_name = (TextView) findViewById(R.id.tv_medical_record_name);
        tv_gender = (TextView) findViewById(R.id.tv_medical_record_gender);
        tv_birthday = (TextView) findViewById(R.id.tv_medical_record_birthday);
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

        // init list
        list_treatment_code = new ArrayList<>();
        list_processing_status = new ArrayList<>();
        list_track_process = new ArrayList<>();
    }

    // 顯示病人資訊(如果有的話)
    private void setPatientText() {
        if (patient != null) {
            tv_name.setText(patient.getName());
            tv_gender.setText(patient.isGender() ? "Male男" : "Female女");
            tv_date.setText(patient.getJiuZhen_date());
            tv_birthday.setText(patient.getDate());
        } else {
            t("No patient");
        }
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
            j.put("bloodType", "1");
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
            for (MedicalTreatmentCodeModel m : list_treatment_code) {
                arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/null");
            }
            j.put("medicalTreatmentRecordDetail", arrTreatment);

            JSONArray arrProcessing = new JSONArray(); //處理狀況
            for (MedicalProcessStatusModel m : list_processing_status) {
                arrProcessing.put(m.getData());
            }

            j.put("medicalProcessingRecord", arrProcessing);


            JSONArray arrTrack = new JSONArray(); //追蹤與處理
            for (MedicalTrackProcessModel m : list_track_process) {
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
        private HashMap<String, Integer> map;

        AddMedicalRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
            this.map = new HashMap<>();
        }

        private Integer getData(String key) {
            if (map.containsKey(key)) {
                return map.get(key);
            } else {
                return 0;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addMedicalRecord(response, map);
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
                openActivityForResult(MedicalInfoActivity.class, REQUEST_INFO, b);
                break;
            case R.id.iv_medical_record_symptoms: // 症狀列表
                openActivityForResult(AddMedicalTreatmentCodeActivity.class, REQUEST_TREATMENT);
                break;
            case R.id.iv_medical_record_processing_status: // 處理狀況
                if (patient == null || patient.getDormID() == null || patient.getDormID().isEmpty()) {
                    t("Can't get dormID");
                    return;
                }
                b.putString("dormID", patient.getDormID());
                openActivityForResult(AddMedicalProcessStatusActivity.class, REQUEST_PROCESS_STATUS, b);
                break;
            case R.id.iv_medical_record_tracking_processing: // 追蹤與處理
                openActivityForResult(AddMedicalTrackProcessActivity.class, REQUEST_TRACK_PROCESS);
                break;
            case R.id.iv_medical_record_file_upload:
                openActivityForResult(AddMedicalFileUploadActivity.class, REQUEST_FILE_UPLOAD);
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
                setPatientText();
                break;
            case REQUEST_TREATMENT: // 症狀列表(回傳)
                ArrayList<MedicalTreatmentCodeModel> list = data.getExtras().getParcelableArrayList("mtrCodeGroup");
                list_treatment_code.clear();
                if (list != null) list_treatment_code.addAll(list);
                String str_symptoms = "";
                for (MedicalTreatmentCodeModel m : list_treatment_code) {
                    str_symptoms += (str_symptoms.isEmpty() ? "" : "\n") + m.getValue();
                }
                et_symptoms.setText(str_symptoms);
                break;
            case REQUEST_PROCESS_STATUS: // 處理狀況(回傳)
                ArrayList<MedicalProcessStatusModel> list1 = data.getExtras().getParcelableArrayList("processStatus");
                list_processing_status.clear();
                if (list1 != null) list_processing_status.addAll(list1);
                String str_processing_status = "";
                for (MedicalProcessStatusModel m : list_processing_status) {
                    str_processing_status += (str_processing_status.isEmpty() ? "" : "\n") + m.getName();
                }
                et_processing_status.setText(str_processing_status);
                break;
            case REQUEST_TRACK_PROCESS: // 追蹤與處理(回傳)
                ArrayList<MedicalTrackProcessModel> list2 = data.getExtras().getParcelableArrayList("trackProcess");
                list_track_process.clear();
                if (list2 != null) list_track_process.addAll(list2);
                String str_track_process = "";
                for (MedicalTrackProcessModel m : list_track_process) {
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

