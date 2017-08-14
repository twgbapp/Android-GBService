package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.MedicalTrackProcessModel;
import goldenbrother.gbmobile.model.MedicalSymptomModel;
import goldenbrother.gbmobile.model.Patient;
import goldenbrother.gbmobile.model.RoleInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class MedicalRecordActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_SEARCH = 0;
    public static final int REQUEST_TREATMENT = 1;
    public static final int REQUEST_PROCESS_STATUS = 2;
    public static final int REQUEST_TRACK_PROCESS = 3;
    public static final int REQUEST_SIGNATURE = 11;
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
    // ui
    private TextView tv_name, tv_blood_type, tv_room_id, tv_date;
    private TextView et_symptoms, et_processing_status, et_tracking_processing;
    private ImageView iv_signature, iv_medical, iv_diagnosis, iv_service, iv_clicked;
    // take picture
    private Uri uriTakePicture;
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
        tv_blood_type = (TextView) findViewById(R.id.tv_medical_record_blood_type);
        tv_room_id = (TextView) findViewById(R.id.tv_medical_record_room_id);
        tv_date = (TextView) findViewById(R.id.tv_medical_record_date);
        et_symptoms = (TextView) findViewById(R.id.tv_medical_record_symptoms);
        et_processing_status = (TextView) findViewById(R.id.tv_medical_record_processing_status);
        et_tracking_processing = (TextView) findViewById(R.id.tv_medical_record_tracking_processing);
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
        tv_name.setOnClickListener(this);
        tv_room_id.setOnClickListener(this);
        tv_blood_type.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        iv_signature.setOnClickListener(this);
        iv_medical.setOnClickListener(this);
        iv_diagnosis.setOnClickListener(this);
        iv_service.setOnClickListener(this);

        // extra
        medical = getIntent().getExtras().getParcelable("medical");
        if (medical != null && medical.getMtrsno() != 0) {
            getMedicalTreatmentCode();
        } else {
            medical = new Medical();
            medical.getPatient().setBloodType("0"); // A
            medical.getPatient().setRecordDate(TimeHelper.date());
            tv_date.setText(TimeHelper.date());
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

        private ArrayList<MedicalSymptomModel> list_first;
        private ArrayList<MedicalSymptomModel> list_second;

        GetMedicalTreatmentCode(Context context, JSONObject json, String url) {
            super(context, json, url);
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

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getMedicalTreatmentCode(response, list_first, list_second);
                    if (result == ApiResultHelper.SUCCESS) {
                        sortSymptoms();
                        if (medical != null && medical.getMtrsno() != 0) {
                            getMedicalRecord(medical.getMtrsno());
                        }
                    } else {
                        t(getString(R.string.fail));
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
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getMedicalRecord(response, medical);
                    if (result == ApiResultHelper.SUCCESS) {
                        syncMedicalSymptom();
                        showPatientInfo();
                        showSymptom();
                        showProcessStatus();
                        showUploadFile();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void showPatientInfo() {
        Patient p = medical.getPatient();
        tv_name.setText(p.getName());
        tv_blood_type.setText(getBloodTypeName(p.getBloodType()));
        tv_room_id.setText(p.getRoomID());
        tv_date.setText(p.getRecordDate());
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
        return code;
    }

    private void addMedicalRecord() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addMedicalRecord");
            j.put("recordDate", medical.getPatient().getRecordDate());
            j.put("customerNo", medical.getPatient().getCustomerNo());
            j.put("flaborNo", medical.getPatient().getFlaborNo());
            j.put("roomID", medical.getPatient().getRoomID());
            j.put("dormID", medical.getPatient().getDormID());
            j.put("bloodType", medical.getPatient().getBloodType());
            j.put("age", medical.getPatient().getAge());
            j.put("centerDirectorID", medical.getPatient().getCenterDirectorID());
            j.put("createId", RoleInfo.getInstance().getUserID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("createTime", TimeHelper.now());
            j.put("diagnosticCertificate", medical.getDiagnosticCertificatePath());
            j.put("serviceRecord", medical.getServiceRecordPath());
            j.put("medicalCertificate", medical.getMedicalCertificatePath());
            j.put("signature", medical.getSignaturePath());

            JSONArray arrTreatment = new JSONArray(); // 症狀
            for (MedicalSymptomModel m : medical.getSymptom()) {
                //arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/null");
                if (m.getCode().equals("425")) {
                    arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/" + m.getValue());
                } else {
                    arrTreatment.put(m.getCode().substring(0, 1) + "/" + m.getCode().substring(1, 3) + "/null");
                }
            }
            j.put("medicalTreatmentRecordDetail", arrTreatment);

            JSONArray arrProcessing = new JSONArray(); // 處理狀況
            for (MedicalProcessStatusModel m : medical.getProcessingStatus()) {
                arrProcessing.put(m.getData());
            }
            j.put("medicalProcessingRecord", arrProcessing);

            JSONArray arrTrack = new JSONArray(); // 追蹤與處理
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

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            j.put("fileName", UUID.randomUUID().toString());
            j.put("url", URLHelper.HOST);
            j.put("baseStr", BitmapHelper.bitmap2JPGBase64(bmp));
            new UploadImageTask(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageTask extends IAsyncTask {
        private HashMap<String, String> map;

        UploadImageTask(Context context, JSONObject json, String url) {
            super(context, json, url);
            map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.uploadPicture(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        savePath(iv_clicked, map.get("path"));
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void savePath(View v, String path) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.iv_medical_record_signature_path:
                medical.setSignaturePath(path);
                Picasso.with(this).load(path).into(iv_signature);
                break;
            case R.id.iv_medical_record_medical_path:
                medical.setMedicalCertificatePath(path);
                Picasso.with(this).load(path).into(iv_medical);
                break;
            case R.id.iv_medical_record_diagnosis_path:
                medical.setDiagnosticCertificatePath(path);
                Picasso.with(this).load(path).into(iv_diagnosis);
                break;
            case R.id.iv_medical_record_service_path:
                medical.setServiceRecordPath(path);
                Picasso.with(this).load(path).into(iv_service);
                break;
        }
    }

    private void choosePicture() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(R.array.choose_picture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_FROM_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriTakePicture = FileProvider.getUriForFile(MedicalRecordActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(MedicalRecordActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadPicture(BitmapHelper.resize(bmp, 300, 300));
            }
        }, null);
    }

    private void showBloodTypeDialog() {
        final String[] items = getResources().getStringArray(R.array.blood_type_name);
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                medical.getPatient().setBloodType(which + "");
                tv_blood_type.setText(items[which]);
            }
        });
    }

    private void showDatePicker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        c.setTime(TimeHelper.getYMD2Date(tv.getText().toString()));

        final DatePicker datePicker = new DatePicker(this);
        datePicker.init(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH), null);

        alertWithView(datePicker, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                int date = datePicker.getDayOfMonth();
                String text = year + "-" + (month < 10 ? "0" + month : month) + "-" + (date < 10 ? "0" + date : date);
                medical.getPatient().setRecordDate(text);
                tv.setText(text);
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.tv_medical_record_blood_type:
                showBloodTypeDialog();
                break;
            case R.id.tv_medical_record_date:
                showDatePicker(tv_date);
                break;
            case R.id.tv_medical_record_name:
            case R.id.tv_medical_record_room_id:
            case R.id.iv_medical_record_info: // 查詢外勞
                b.putBoolean("isFLabor", true);
                openActivityForResult(SearchActivity.class, REQUEST_SEARCH, b);
                break;
            case R.id.iv_medical_record_symptoms: // 症狀列表
                b.putParcelable("medical", medical);
                openActivityForResult(MedicalSymptomActivity.class, REQUEST_TREATMENT, b);
                break;
            case R.id.iv_medical_record_processing_status: // 處理狀況
                b.putParcelable("medical", medical);
                if (medical.getPatient() == null || medical.getPatient().getDormID() == null || medical.getPatient().getDormID().isEmpty()) {
                    t(R.string.can_not_get_patient);
                    return;
                }
                openActivityForResult(MedicalProcessStatusActivity.class, REQUEST_PROCESS_STATUS, b);
                break;
            case R.id.iv_medical_record_tracking_processing: // 追蹤與處理
                b.putParcelable("medical", medical);
                openActivityForResult(MedicalTrackProcessActivity.class, REQUEST_TRACK_PROCESS, b);
                break;
            case R.id.iv_medical_record_file_upload:

                break;
            case R.id.iv_medical_record_signature_path:
                iv_clicked = (ImageView) v;
                openActivityForResult(SignatureActivity.class, REQUEST_SIGNATURE);
                break;
            case R.id.iv_medical_record_medical_path:
                iv_clicked = (ImageView) v;
                choosePicture();
                break;
            case R.id.iv_medical_record_diagnosis_path:
                iv_clicked = (ImageView) v;
                choosePicture();
                break;
            case R.id.iv_medical_record_service_path:
                iv_clicked = (ImageView) v;
                choosePicture();
                break;
            case R.id.tv_medical_record_save: // 新增醫療紀錄
                if (medical.getPatient().getName() == null) {
                    t(R.string.can_not_get_patient);
                    return;
                }
                if (medical.getSymptom().isEmpty()) {
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
        Bundle b = new Bundle();
        switch (requestCode) {
            case REQUEST_SEARCH:
                Patient p = medical.getPatient();
                p.setName(data.getStringExtra("flaborName"));
                p.setDate(data.getStringExtra("birthday"));
                p.setAge(TimeHelper.getAge(p.getDate()));
                p.setFlaborNo(data.getStringExtra("flaborNo"));
                p.setCustomerNo(data.getStringExtra("customerNo"));
                p.setDormID(data.getStringExtra("dormId"));
                p.setRoomID(data.getStringExtra("roomId"));
                p.setCenterDirectorID(data.getStringExtra("centerDirectorId"));
                tv_name.setText(p.getName());
                tv_room_id.setText(p.getRoomID());
                break;
            case REQUEST_TREATMENT:
                medical = data.getParcelableExtra("medical");
                showSymptom();
                break;
            case REQUEST_PROCESS_STATUS:
                medical = data.getParcelableExtra("medical");
                showProcessStatus();
                break;
            case REQUEST_TRACK_PROCESS:
                medical = data.getParcelableExtra("medical");
                showTrackProcess();
                break;
            case REQUEST_SIGNATURE:
                Bitmap bitmap = BitmapHelper.byteArrayToBitmap(data.getByteArrayExtra("bitmap"));
                showImage(bitmap);
                break;
            case REQUEST_FROM_GALLERY:
                b.putString("uri", data.getData().toString());
                b.putInt("ratioX", 0);
                b.putInt("ratioY", 0);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_PHOTO:
                b.putString("uri", uriTakePicture.toString());
                b.putInt("ratioX", 0);
                b.putInt("ratioY", 0);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_CROP:
                showImage(BitmapHelper.file2Bitmap(new File(data.getStringExtra("path"))));
                break;
        }
    }
}

