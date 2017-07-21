package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalHospitalListAdapter;
import goldenbrother.gbmobile.adapter.MedicalPersonListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
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
    private Spinner sp_hospital, sp_person;
    private RadioButton rb_yes, rb_no;
    // extra
    private Medical medical;
    // data
    private boolean isCheck1, isCheck2, isCheck3, isCheck4, isCheck5;
    private String[] array_process_status;
    private ArrayList<HospitalModel> list_hospital;
    private ArrayList<PersonalPickUpModel> list_personal_pick_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_process_status);

        // ui reference
        findViewById(R.id.tv_medical_process_status_done).setOnClickListener(this);
        iv_1 = (ImageView) findViewById(R.id.iv_medical_process_status_1);
        iv_2 = (ImageView) findViewById(R.id.iv_medical_process_status_2);
        iv_3 = (ImageView) findViewById(R.id.iv_medical_process_status_3);
        iv_4 = (ImageView) findViewById(R.id.iv_medical_process_status_4);
        iv_5 = (ImageView) findViewById(R.id.iv_medical_process_status_5);
        et_other = (EditText) findViewById(R.id.et_medical_process_status_other);
        sp_hospital = (Spinner) findViewById(R.id.sp_medical_process_hospital);
        sp_person = (Spinner) findViewById(R.id.sp_medical_process_person);
        rb_yes = (RadioButton) findViewById(R.id.rb_medical_process_yes);
        rb_no = (RadioButton) findViewById(R.id.rb_medical_process_no);
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
        list_personal_pick_up = new ArrayList<>();
        list_hospital.add(new HospitalModel("0", getString(R.string.select)));
        list_personal_pick_up.add(new PersonalPickUpModel("0", getString(R.string.select)));
        sp_hospital.setAdapter(new MedicalHospitalListAdapter(this, list_hospital));
        sp_person.setAdapter(new MedicalPersonListAdapter(this, list_personal_pick_up));

        // getHospitalPickUp
        getHospitalPickUp();
    }

    private void updateAdapter() {
        ((MedicalHospitalListAdapter) sp_hospital.getAdapter()).notifyDataSetChanged();
        ((MedicalPersonListAdapter) sp_person.getAdapter()).notifyDataSetChanged();
    }

    private void getHospitalPickUp() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getHospitalPickUp");
            j.put("dormID", medical.getPatient().getDormID());
            j.put("customerNo", medical.getPatient().getCustomerNo());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetHospitalPickUp(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetHospitalPickUp extends IAsyncTask {

        GetHospitalPickUp(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getHospitalPickUp(response, list_hospital, list_personal_pick_up);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                    list.add(new MedicalProcessStatusModel(array_process_status[0], "0/null/null/null/null"));
                if (isCheck2) {
                    String hospitalCode = (sp_hospital.getSelectedItemPosition() == 0 ? "null" : list_hospital.get(sp_hospital.getSelectedItemPosition()).getCode());
                    String userId = (sp_person.getSelectedItemPosition() == 0 ? "null" : list_personal_pick_up.get(sp_person.getSelectedItemPosition()).getUserId());
                    list.add(new MedicalProcessStatusModel(array_process_status[1], "1/" + hospitalCode + "/" + userId + "/null/null"));
                }
                if (isCheck3)
                    list.add(new MedicalProcessStatusModel(array_process_status[2], "2/null/null/null/null"));
                if (isCheck4) {
                    boolean yes = rb_yes.isChecked();
                    boolean no = rb_no.isChecked();
                    String yesNo = (!yes && !no) ? "null" : (yes ? "1" : "0");
                    list.add(new MedicalProcessStatusModel(array_process_status[3], "3/null/null/" + yesNo + "/null"));
                }
                if (isCheck5) {
                    String other = et_other.getText().toString();
                    other = other.replace("/", "\\");
                    list.add(new MedicalProcessStatusModel(other, "4/null/null/null/" + (other.isEmpty() ? "null" : other)));
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
