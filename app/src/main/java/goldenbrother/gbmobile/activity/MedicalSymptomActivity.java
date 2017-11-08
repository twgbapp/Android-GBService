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
import java.util.Iterator;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalTreatmentCodeRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.MedicalSymptomModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalSymptomActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private RecyclerView rv;
    private EditText et_other;
    private ImageView iv_check;
    // extra
    private Medical medical;
    // data
    private ArrayList<MedicalSymptomModel> list_symptoms;
    private boolean otherChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_symptom);
        setUpBackToolbar(R.id.toolbar_medical_symptom, R.id.tv_medical_symptom_title, R.string.medical_symptoms);

        // ui reference
        findViewById(R.id.iv_medical_symptom_done).setOnClickListener(this);
        findViewById(R.id.ll_medical_symptom_other).setOnClickListener(this);
        rv = findViewById(R.id.rv_medical_symptom);
        et_other = findViewById(R.id.et_medical_symptom_other);
        iv_check = findViewById(R.id.iv_medical_symptom_check);

        // extra
        medical = getIntent().getExtras().getParcelable("medical");
        // init ListView
        list_symptoms = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MedicalTreatmentCodeRVAdapter(this, list_symptoms));

        // loadMedicalTreatmentCodeList
        getMedicalTreatmentCode();

        for (MedicalSymptomModel m : medical.getSymptom()) {
            LogHelper.d("SYM:" + m.getCode() + " " + m.getValue());
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
                        initSelected();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void initSelected() {
        // safety remove
        Iterator<MedicalSymptomModel> iterator = medical.getSymptom().iterator();
        while (iterator.hasNext()) {
            MedicalSymptomModel ms = iterator.next();
            if (ms.getCode().equals("425")) {
                et_other.setText(ms.getValue());
                otherChecked = true;
                updateOther();
                iterator.remove();
                break;
            }
        }
        // update
        ((MedicalTreatmentCodeRVAdapter) rv.getAdapter()).setSelected(medical.getSymptom());
    }

    private void updateOther() {
        iv_check.setImageResource(otherChecked ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_medical_symptom_done:
                //
                HashSet<MedicalSymptomModel> set = ((MedicalTreatmentCodeRVAdapter) rv.getAdapter()).getSelected();
                ArrayList<MedicalSymptomModel> lists = new ArrayList<>();
                lists.addAll(set);
                // other
                if (otherChecked && et_other.getText().toString().isEmpty()) {
                    t("其它欄位未填寫");
                    return;
                }
                if (!et_other.getText().toString().isEmpty()) {
                    String other = et_other.getText().toString();
                    MedicalSymptomModel m = new MedicalSymptomModel();
                    m.setCode("425");
                    m.setValue(other.isEmpty() ? "" : other);
                    lists.add(m);
                }
                medical.getSymptom().clear();
                medical.getSymptom().addAll(lists);
                Intent intent = new Intent();
                intent.putExtra("medical", medical);
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
