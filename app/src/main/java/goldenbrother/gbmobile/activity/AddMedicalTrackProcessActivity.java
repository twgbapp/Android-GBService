package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;

public class AddMedicalTrackProcessActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private CheckBox cb_1, cb_2, cb_3, cb_4;
    private EditText et_1, et_2, et_3;
    // data
    private String[] array_track_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_track_process);
        // ui reference
        cb_1 = (CheckBox) findViewById(R.id.cb_add_medical_track_process_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_add_medical_track_process_2);
        cb_3 = (CheckBox) findViewById(R.id.cb_add_medical_track_process_3);
        cb_4 = (CheckBox) findViewById(R.id.cb_add_medical_track_process_4);
        et_1 = (EditText) findViewById(R.id.et_add_medical_track_process_1);
        et_2 = (EditText) findViewById(R.id.et_add_medical_track_process_2);
        et_3 = (EditText) findViewById(R.id.et_add_medical_track_process_3);
        findViewById(R.id.iv_add_medical_track_process_done).setOnClickListener(this);

        // init
        array_track_process = getResources().getStringArray(R.array.medical_track_process);
    }

    private String checkSymbol(String str) {
        return str.replace("/", "\\");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_add_medical_track_process_done:
                ArrayList<MedicalTrackProcessModel> list = new ArrayList<>();
                if (cb_1.isChecked()) {
                    String str = et_1.getText().toString();
                    MedicalTrackProcessModel m = new MedicalTrackProcessModel();
                    m.setName(array_track_process[0] + " : " + str);
                    m.setData("0/" + (str.isEmpty() ? "null" : checkSymbol(str)));
                    list.add(m);
                }
                if (cb_2.isChecked()) {
                    String str = et_2.getText().toString();
                    MedicalTrackProcessModel m = new MedicalTrackProcessModel();
                    m.setName(array_track_process[1] + " : " + str);
                    m.setData("1/" + (str.isEmpty() ? "null" : checkSymbol(str)));
                    list.add(m);
                }
                if (cb_3.isChecked()) {
                    String str = et_3.getText().toString();
                    MedicalTrackProcessModel m = new MedicalTrackProcessModel();
                    m.setName(array_track_process[2] + " : " + str);
                    m.setData("2/" + (str.isEmpty() ? "null" : checkSymbol(str)));
                    list.add(m);
                }
                if (cb_4.isChecked()) {
                    MedicalTrackProcessModel m = new MedicalTrackProcessModel();
                    m.setName(array_track_process[3]);
                    m.setData("3/null");
                    list.add(m);
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putParcelableArrayList("trackProcess", list);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
