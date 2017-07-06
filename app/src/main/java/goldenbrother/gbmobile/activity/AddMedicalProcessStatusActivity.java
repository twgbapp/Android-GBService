package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.PersonnelPickUpModel;

public class AddMedicalProcessStatusActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_other;
    private CheckBox cb_1, cb_2, cb_3, cb_4, cb_5;
    private Spinner sp_hospital, sp_person;
    private RadioButton rb_yes, rb_no;
    // extra
    private String dormID;
    // data
    private String[] array_process_status;
    private ArrayList<HospitalModel> list_hospital;
    private ArrayList<PersonnelPickUpModel> list_personal_pick_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_process_status);

        // ui reference
        findViewById(R.id.iv_add_medical_process_status_done).setOnClickListener(this);
        cb_1 = (CheckBox) findViewById(R.id.cb_add_medical_process_status_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_add_medical_process_status_2);
        cb_3 = (CheckBox) findViewById(R.id.cb_add_medical_process_status_3);
        cb_4 = (CheckBox) findViewById(R.id.cb_add_medical_process_status_4);
        cb_5 = (CheckBox) findViewById(R.id.cb_add_medical_process_status_5);
        et_other = (EditText) findViewById(R.id.et_add_medical_process_status_other);
        sp_hospital = (Spinner) findViewById(R.id.sp_add_medical_process_hospital);
        sp_person = (Spinner) findViewById(R.id.sp_add_medical_process_person);
        rb_yes = (RadioButton) findViewById(R.id.rb_add_medical_process_yes);
        rb_no = (RadioButton) findViewById(R.id.rb_add_medical_process_no);

        // extra
        dormID = getIntent().getExtras().getString("dormID");
        // init
        array_process_status = getResources().getStringArray(R.array.medical_process_status);
        // init Spinner
        list_hospital = new ArrayList<>();
        list_personal_pick_up = new ArrayList<>();
        list_hospital.add(new HospitalModel("0", getString(R.string.select)));
        list_personal_pick_up.add(new PersonnelPickUpModel("0", getString(R.string.select)));
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
            j.put("dormID", dormID);
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
                        t(String.format(getString(R.string.fail)+"(%s)","GetHospitalPickUp"));
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_medical_process_status_done:
                ArrayList<MedicalProcessStatusModel> list = new ArrayList<>();
                if (cb_1.isChecked())
                    list.add(new MedicalProcessStatusModel(array_process_status[0], "0/null/null/null/null"));
                if (cb_2.isChecked()) {
                    String hospitalCode = (sp_hospital.getSelectedItemPosition() == 0 ? "null" : list_hospital.get(sp_hospital.getSelectedItemPosition()).getCode());
                    String userId = (sp_person.getSelectedItemPosition() == 0 ? "null" : list_personal_pick_up.get(sp_person.getSelectedItemPosition()).getUserId());
                    list.add(new MedicalProcessStatusModel(array_process_status[1], "1/" + hospitalCode + "/" + userId + "/null/null"));
                }
                if (cb_3.isChecked())
                    list.add(new MedicalProcessStatusModel(array_process_status[2], "2/null/null/null/null"));
                if (cb_4.isChecked()) {
                    boolean yes = rb_yes.isChecked();
                    boolean no = rb_no.isChecked();
                    String yesNo = (!yes && !no) ? "null" : (yes ? "1" : "0");
                    list.add(new MedicalProcessStatusModel(array_process_status[3], "3/null/null/" + yesNo + "/null"));
                }
                if (cb_5.isChecked()) {
                    String other = et_other.getText().toString();
                    other = other.replace("/", "\\");
                    list.add(new MedicalProcessStatusModel(other, "4/null/null/null/" + (other.isEmpty() ? "null" : other)));
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putParcelableArrayList("processStatus", list);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
