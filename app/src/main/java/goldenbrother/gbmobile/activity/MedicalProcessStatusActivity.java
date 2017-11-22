package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalHospitalListAdapter;
import goldenbrother.gbmobile.adapter.MedicalPersonListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.HospitalModel;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.Patient;
import goldenbrother.gbmobile.model.PersonalPickUpModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalProcessStatusActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_other;
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5;
    private TextView tv_hospital, tv_person;
    private RadioButton rb_yes, rb_no;
    // extra
    private Medical medical;
    // data
    private boolean isCheck1, isCheck2, isCheck3, isCheck4, isCheck5;
    private String[] array_process_status;
    private ArrayList<HospitalModel> list_hospital;
    private ArrayList<PersonalPickUpModel> list_personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_process_status);
        setUpBackToolbar(R.id.toolbar, R.string.medical_process);
        // ui reference
        findViewById(R.id.tv_medical_process_status_done).setOnClickListener(this);
        iv_1 = findViewById(R.id.iv_medical_process_status_1);
        iv_2 = findViewById(R.id.iv_medical_process_status_2);
        iv_3 = findViewById(R.id.iv_medical_process_status_3);
        iv_4 = findViewById(R.id.iv_medical_process_status_4);
        iv_5 = findViewById(R.id.iv_medical_process_status_5);
        et_other = findViewById(R.id.et_medical_process_status_other);
        tv_hospital = findViewById(R.id.tv_medical_process_hospital);
        tv_person = findViewById(R.id.tv_medical_process_person);
        rb_yes = findViewById(R.id.rb_medical_process_yes);
        rb_no = findViewById(R.id.rb_medical_process_no);
        tv_hospital.setOnClickListener(this);
        tv_person.setOnClickListener(this);
        iv_1.setOnClickListener(this);
        iv_2.setOnClickListener(this);
        iv_3.setOnClickListener(this);
        iv_4.setOnClickListener(this);
        iv_5.setOnClickListener(this);
        // extra
        medical = getIntent().getExtras().getParcelable("medical");

        // init
        array_process_status = getResources().getStringArray(R.array.medical_process_status);

        // init Spinner
        list_hospital = new ArrayList<>();
        list_personal = new ArrayList<>();
    }

    private void getHospitalPickUp(boolean showHospital) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getHospitalPickUp");
            j.put("dormID", medical.getPatient().getDormID());
            j.put("customerNo", medical.getPatient().getCustomerNo());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetHospitalPickUp(this, j, showHospital).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetHospitalPickUp extends IAsyncTask {

        private boolean showHospital;

        GetHospitalPickUp(Context context, JSONObject json, boolean showHospital) {
            super(context, json);
            this.showHospital = showHospital;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getHospitalPickUp(response, list_hospital, list_personal);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (showHospital) {
                            showHospitalDialog();
                        } else {
                            showPersonDialog();
                        }
                    } else {
                        t(String.format(getString(R.string.fail) + "(%s)", "GetHospitalPickUp"));
                    }
                    break;
            }
        }
    }

    private void updateCheck() {
        iv_1.setImageResource(isCheck1 ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
        iv_2.setImageResource(isCheck2 ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
        iv_3.setImageResource(isCheck3 ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
        iv_4.setImageResource(isCheck4 ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
        iv_5.setImageResource(isCheck5 ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
    }

    private String getIdByHospital(String name) {
        for (HospitalModel h : list_hospital) {
            if (h.getName().equals(name)) {
                return h.getCode();
            }
        }
        return "";
    }

    private String getIdByPerson(String name) {
        for (PersonalPickUpModel p : list_personal) {
            if (p.getName().equals(name)) {
                return p.getUserId();
            }
        }
        return "";
    }

    private void showHospitalDialog() {
        final String[] items = new String[list_hospital.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = list_hospital.get(i).getName();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_hospital.setText(items[i]);
            }
        });
    }

    private void showPersonDialog() {
        final String[] items = new String[list_personal.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = list_personal.get(i).getName();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_person.setText(items[i]);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_medical_process_hospital:
                if (list_hospital.isEmpty()) {
                    getHospitalPickUp(true);
                } else {
                    showHospitalDialog();
                }
                break;
            case R.id.tv_medical_process_person:
                if (list_personal.isEmpty()) {
                    getHospitalPickUp(false);
                } else {
                    showPersonDialog();
                }
                break;
            case R.id.iv_medical_process_status_1:
                isCheck1 = !isCheck1;
                updateCheck();
                break;
            case R.id.iv_medical_process_status_2:
                isCheck2 = !isCheck2;
                updateCheck();
                break;
            case R.id.iv_medical_process_status_3:
                isCheck3 = !isCheck3;
                updateCheck();
                break;
            case R.id.iv_medical_process_status_4:
                isCheck4 = !isCheck4;
                updateCheck();
                break;
            case R.id.iv_medical_process_status_5:
                isCheck5 = !isCheck5;
                updateCheck();
                break;
            case R.id.tv_medical_process_status_done:
                ArrayList<MedicalProcessStatusModel> list = new ArrayList<>();
                if (isCheck1)
                    list.add(new MedicalProcessStatusModel(0, "null", "null", "null", "null", array_process_status[0]));
                if (isCheck2) {
                    String hospitalName = tv_hospital.getText().toString();
                    String personName = tv_person.getText().toString();
                    String hospitalCode = hospitalName.isEmpty() ? "null" : getIdByHospital(hospitalName);
                    String userId = personName.isEmpty() ? "null" : getIdByPerson(personName);
                    list.add(new MedicalProcessStatusModel(1, hospitalCode, userId, "null", "null", array_process_status[1]));
                }
                if (isCheck3)
                    list.add(new MedicalProcessStatusModel(2, "null", "null", "null", "null", array_process_status[2]));
                if (isCheck4) {
                    boolean yes = rb_yes.isChecked();
                    boolean no = rb_no.isChecked();
                    if (yes || no) {
                        String yesNo = yes ? "1" : "0";
                        list.add(new MedicalProcessStatusModel(3, "null", "null", yesNo, "null", array_process_status[3]));
                    } else {
                        t(R.string.can_not_be_empty);
                        return;
                    }
                }
                if (isCheck5) {
                    String other = et_other.getText().toString();
                    other = other.replace("/", "\\");
                    list.add(new MedicalProcessStatusModel(4, "null", "null", "null", (other.isEmpty() ? "null" : other), other));
                }
                medical.getProcessingStatus().clear();
                medical.getProcessingStatus().addAll(list);
                Intent intent = new Intent();
                intent.putExtra("medical", medical);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
