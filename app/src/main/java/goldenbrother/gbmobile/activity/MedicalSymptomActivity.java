package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalTreatmentCodeRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.MedicalTreatmentCodeModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalSymptomActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private RecyclerView rv;
    private EditText et_other;
    private ImageView iv_check;
    // data
    private ArrayList<MedicalTreatmentCodeModel> list_symptoms;
    private boolean otherChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_symptom);

        // ui reference
        findViewById(R.id.iv_event_medical_symptom_done).setOnClickListener(this);
        findViewById(R.id.ll_medical_symptom_other).setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv_medical_symptom);
        et_other = (EditText) findViewById(R.id.et_medical_symptom_other);
        iv_check = (ImageView) findViewById(R.id.iv_medical_symptom_check);

        // init ListView
        list_symptoms = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MedicalTreatmentCodeRVAdapter(this, list_symptoms));

        // loadMedicalTreatmentCodeList
        getMedicalTreatmentCode();
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


        private ArrayList<MedicalTreatmentCodeModel> list_first;
        private ArrayList<MedicalTreatmentCodeModel> list_second;

        GetMedicalTreatmentCode(Context context, JSONObject json, String url) {
            super(context, json, url);
            list_first = new ArrayList<>();
            list_second = new ArrayList<>();
        }

        private void sortSymptoms() {
            list_symptoms.clear();
            for (MedicalTreatmentCodeModel m : list_first) {
                list_symptoms.add(m);
                for (MedicalTreatmentCodeModel mm : list_second) {
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
                        updateAdapter();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }


    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    private void updateOther() {
        iv_check.setImageResource(otherChecked ? R.drawable.ic_radio_button_checked_b : R.drawable.ic_radio_button_unchecked_b);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_event_medical_symptom_done:
                //
                HashSet<MedicalTreatmentCodeModel> set = ((MedicalTreatmentCodeRVAdapter) rv.getAdapter()).getSelected();
                ArrayList<MedicalTreatmentCodeModel> lists = new ArrayList<>();
                lists.addAll(set);
                // other
                if (otherChecked) {
                    String other = et_other.getText().toString();
                    MedicalTreatmentCodeModel m = new MedicalTreatmentCodeModel();
                    m.setCode("425");
                    m.setValue(other.isEmpty() ? "null" : other);
                    lists.add(m);
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putParcelableArrayList("mtrCodeGroup", lists);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.ll_medical_symptom_other:
                otherChecked = !otherChecked;
                updateOther();
                break;
        }
    }
}
