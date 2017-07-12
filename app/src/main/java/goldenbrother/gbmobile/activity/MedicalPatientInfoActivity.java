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
    private TextView tv_name, tv_birthday, tv_date;
    private RadioButton rb_male, rb_female;
    private Spinner sp_blood_type;
    // extra
    private Patient patient;
    // data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_patient_info);

        // ui reference
        et_arc_id_number = (EditText) findViewById(R.id.et_medical_patient_info_arc_id_number);
        tv_name = (TextView) findViewById(R.id.tv_medical_patient_info_name);
        tv_birthday = (TextView) findViewById(R.id.tv_medical_patient_info_birthday);
        tv_date = (TextView) findViewById(R.id.tv_medical_patient_info_date);
        rb_male = (RadioButton) findViewById(R.id.rb_medical_patient_info_male);
        rb_female = (RadioButton) findViewById(R.id.rb_medical_patient_info_female);
        sp_blood_type = (Spinner) findViewById(R.id.sp_medical_patient_info_blood_type);
        findViewById(R.id.tv_medical_patient_info_check).setOnClickListener(this);
        findViewById(R.id.iv_medical_patient_info_done).setOnClickListener(this);
        tv_date.setOnClickListener(this);

        // extra
        patient = getIntent().getExtras().getParcelable("patient");
        if (patient == null) patient = new Patient();
        setPatientInfo();
        // init Spinner
        sp_blood_type.setAdapter(new MedicalBloodTypeListAdapter(this));
        // init Date
        tv_date.setText(TimeHelper.getYMD());
    }

    private void setPatientInfo() {
        tv_name.setText(patient.getName());
        rb_male.setChecked(patient.isGender());
        rb_female.setChecked(!patient.isGender());
        tv_birthday.setText(patient.getDate());
        tv_date.setText(patient.getJiuZhen_date());
        et_arc_id_number.setText(patient.getId1());
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
                        patient.setGender(getData("userSex").equals("男"));
                        patient.setCustomerNo(getData("customerNo"));
                        patient.setFlaborNo(getData("flaborNo"));
                        patient.setCustomerNo(getData("customerNo"));
                        patient.setDormID(getData("dormID"));
                        patient.setRoomID(getData("roomID"));
                        patient.setCenterDirectorID(getData("centerDirectorID"));
                        // set name
                        tv_name.setText(getData("userName"));
                        if (getData("userSex").equals("男")) {
                            rb_male.setChecked(true);
                        } else {
                            rb_female.setChecked(true);
                        }
                        tv_birthday.setText(getData("userBirthday"));
                        rb_male.setChecked(patient.isGender());
                        rb_female.setChecked(!patient.isGender());
                    } else {
                        t("Fail(CheckARCIDNumber)");
                    }
                    break;
            }
        }
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
            case R.id.iv_medical_patient_info_done:
                String name = tv_name.getText().toString();
                boolean male = rb_male.isChecked();
                String birthday = tv_birthday.getText().toString();
                String bloodType = ((BloodType) sp_blood_type.getAdapter().getItem(sp_blood_type.getSelectedItemPosition())).getCode();
                int age = getAge(birthday);
                String arcIdNumber = et_arc_id_number.getText().toString();
                String date = tv_date.getText().toString();
                if (name.isEmpty() || birthday.isEmpty() || date.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                saveInfo(name, male, birthday, bloodType, age, arcIdNumber, date);
                break;
            case R.id.tv_medical_patient_info_date:
                showDatePicker(tv_date);
                break;
        }
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

    private void saveInfo(String name, boolean male, String birthday, String bloodType, int age, String arcIdNumber, String date) {
        patient.setName(name);
        patient.setGender(male);
        patient.setDate(birthday);
        patient.setBloodType(bloodType);
        patient.setAge(age);
        patient.setId1(arcIdNumber);
        patient.setJiuZhen_date(date);
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putParcelable("patient", patient);
        intent.putExtras(b);
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


}

