package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalBloodTypeListAdapter;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.Patient;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.BloodType;
import goldenbrother.gbmobile.model.RoleInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MedicalPatientInfoActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_arc_id_number;
    private TextView tv_name, tv_gender, tv_birthday, tv_date, tv_blood_type;
    // extra
    private Medical medical;
    // data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_patient_info);
        // ui reference
        et_arc_id_number = (EditText) findViewById(R.id.et_medical_patient_info_arc_id_number);
        tv_name = (TextView) findViewById(R.id.tv_medical_patient_info_name);
        tv_gender = (TextView) findViewById(R.id.tv_medical_patient_info_sex);
        tv_birthday = (TextView) findViewById(R.id.tv_medical_patient_info_birthday);
        tv_date = (TextView) findViewById(R.id.tv_medical_patient_info_date);
        tv_blood_type = (TextView) findViewById(R.id.tv_medical_patient_info_blood_type);
        findViewById(R.id.tv_medical_patient_info_check).setOnClickListener(this);
        findViewById(R.id.iv_medical_patient_info_done).setOnClickListener(this);
        tv_date.setOnClickListener(this);
        //tv_gender.setOnClickListener(this);
        tv_blood_type.setOnClickListener(this);
        // extra
        medical = getIntent().getExtras().getParcelable("medical");
        setPatientInfo();
        // init Date
        tv_birthday.setText(TimeHelper.getYMD());
        tv_blood_type.setText(getBloodTypeName(medical.getPatient().getBloodType()));
        tv_date.setText(TimeHelper.getYMD());
    }

    private void setPatientInfo() {
        tv_name.setText(medical.getPatient().getName());
        tv_gender.setText(getString(medical.getPatient().isGender() ? R.string.male : R.string.female));
        tv_birthday.setText(medical.getPatient().getDate());
        tv_date.setText(medical.getPatient().getRecordDate());
        tv_blood_type.setText(medical.getPatient().getBloodType());
        et_arc_id_number.setText(medical.getPatient().getId1());
    }

    private void getDormUserInfo(String userIDNumber) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getDormUserInfo");
            j.put("arc", userIDNumber);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetDormUserInfo(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDormUserInfo extends IAsyncTask {
        private HashMap<String, String> map;

        GetDormUserInfo(Context context, JSONObject json, String url) {
            super(context, json, url);
            map = new HashMap<>();
        }

        private String getData(String key) {
            if (map.containsKey(key)) {
                return map.get(key);
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getDormUserInfo(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        medical.getPatient().setGender(getData("userSex").equals("男"));
                        medical.getPatient().setCustomerNo(getData("customerNo"));
                        medical.getPatient().setFlaborNo(getData("flaborNo"));
                        medical.getPatient().setCustomerNo(getData("customerNo"));
                        medical.getPatient().setDormID(getData("dormID"));
                        medical.getPatient().setRoomID(getData("roomID"));
                        medical.getPatient().setCenterDirectorID(getData("centerDirectorID"));

                        tv_name.setText(getData("userName"));
                        tv_gender.setText(getString(getData("userSex").equals("男") ? R.string.male : R.string.female));
                        tv_birthday.setText(getData("userBirthday"));
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void showGenderDialog() {
        final String[] items = {getString(R.string.male), getString(R.string.female)};
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                tv_gender.setText(items[which]);
            }
        });
    }

    private void showBloodTypeDialog() {
        final String[] items = getResources().getStringArray(R.array.blood_type_name);
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                tv_blood_type.setText(items[which]);
            }
        });
    }

    private int getAge(String birthday) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(birthday);
            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            dob.setTime(d);
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getBloodTypeCode(String bloodType) {
        String[] codes = getResources().getStringArray(R.array.blood_type_code);
        String[] names = getResources().getStringArray(R.array.blood_type_name);
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(bloodType)) {
                return codes[i];
            }
        }
        return "";
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

    private void saveInfo(String name, boolean male, String birthday, String bloodType, int age, String arcIdNumber, String date) {
        medical.getPatient().setName(name);
        medical.getPatient().setGender(male);
        medical.getPatient().setDate(birthday);
        medical.getPatient().setBloodType(bloodType);
        medical.getPatient().setAge(age);
        medical.getPatient().setId1(arcIdNumber);
        medical.getPatient().setRecordDate(date);
        Intent intent = new Intent();
        intent.putExtra("medical", medical);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showDatePicker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        final Calendar c_result = Calendar.getInstance();
        c.setTime(TimeHelper.getYMD2Date(tv.getText().toString()));

        DatePicker datePicker = new DatePicker(this);
        datePicker.init(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c_result.set(year, monthOfYear, dayOfMonth);
                    }
                });


        alertWithView(datePicker, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(TimeHelper.getDate2TMD(c_result.getTime()));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        EnvironmentHelper.hideKeyBoard(this, v);
        switch (v.getId()) {
            case R.id.tv_medical_patient_info_check:
                String idNumber = et_arc_id_number.getText().toString();
                if (idNumber.isEmpty()) return;
                getDormUserInfo(idNumber);
                break;
            /*case R.id.tv_medical_patient_info_sex:
                showGenderDialog();
                break;*/
            case R.id.tv_medical_patient_info_blood_type:
                showBloodTypeDialog();
                break;
            case R.id.iv_medical_patient_info_done:
                String name = tv_name.getText().toString();
                boolean male = tv_gender.getText().toString().equals(getString(R.string.male))?true:false;
                String birthday = tv_birthday.getText().toString();
                String bloodType = tv_blood_type.getText().toString();
                String arcIdNumber = et_arc_id_number.getText().toString();
                String date = tv_date.getText().toString();

                int age = getAge(birthday);
                String bloodTypeCode = getBloodTypeCode(bloodType);
                if (name.isEmpty() || birthday.isEmpty() || date.isEmpty()||bloodTypeCode.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                saveInfo(name, male, birthday, bloodTypeCode, age, arcIdNumber, date);
                break;
            case R.id.tv_medical_patient_info_date:
                showDatePicker(tv_date);
                break;
        }
    }
}

