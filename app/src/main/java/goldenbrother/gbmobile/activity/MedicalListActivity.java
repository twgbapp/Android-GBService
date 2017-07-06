package goldenbrother.gbmobile.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import goldenbrother.gbmobile.R;


public class MedicalListActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private RecyclerView rv;
    // data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_list);

        // ui reference
        findViewById(R.id.iv_medical_list_search).setOnClickListener(this);
        findViewById(R.id.iv_medical_list_add).setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv_medical_list);

        // init RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));

        // getMedicalList
    }

    private void showSearchDialog() {
        EditText et = new EditText(this);
        alertWithView(et, "Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }, null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_medical_list_search:
                showSearchDialog();
                break;
            case R.id.iv_medical_list_add:
                openActivity(MedicalRecordActivity.class);
                break;
        }
    }

}

