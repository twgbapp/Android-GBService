package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.RepairKindListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RepairKindModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuickRepairActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_applicant, et_place, et_description;
    private Spinner sp_area, sp_kind, sp_detail;
    private TextView tv_send;
    // data
    private ArrayList<RepairKindModel> list_area, list_kind, list_detail, list_detail_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_repair);
        // ui reference
        et_applicant = (EditText) findViewById(R.id.et_quick_repair_applicant);
        et_place = (EditText) findViewById(R.id.et_quick_repair_place);
        et_description = (EditText) findViewById(R.id.et_quick_repair_description);
        sp_area = (Spinner) findViewById(R.id.sp_quick_repair_area);
        sp_kind = (Spinner) findViewById(R.id.sp_quick_repair_kind);
        sp_detail = (Spinner) findViewById(R.id.sp_quick_repair_kind_content);
        tv_send = (TextView) findViewById(R.id.tv_quick_repair_send);
        // listener
        tv_send.setOnClickListener(this);
        // init Spinner
        list_area = new ArrayList<>();
        list_area.add(0, getDefaultKind());
        list_kind = new ArrayList<>();
        list_kind.add(0, getDefaultKind());
        list_detail = new ArrayList<>();
        list_detail_show = new ArrayList<>();
        list_detail_show.add(0, getDefaultKind());
        sp_area.setAdapter(new RepairKindListAdapter(this, list_area));
        sp_kind.setAdapter(new RepairKindListAdapter(this, list_kind));
        sp_detail.setAdapter(new RepairKindListAdapter(this, list_detail_show));
        // listener
        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    getRepairKind(list_area.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RepairKindModel rm_kind = list_kind.get(position);
                if (rm_kind.getId() != -1) {
                    list_detail_show.clear();
                    list_detail_show.add(getDefaultKind());
                    for (RepairKindModel rm_detail : list_detail) {
                        if (rm_detail.getParentId() == rm_kind.getId()) {
                            list_detail_show.add(rm_detail);
                        }
                    }
                    updateKindAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // set applicant
        et_applicant.setText(RoleInfo.getInstance().getUserName());
        // load area
        getRepairArea();
    }

    private void getRepairArea() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairArea");
            new GetRepairArea(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairArea extends IAsyncTask {

        GetRepairArea(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getRepairArea(response, list_area);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAreaAdapter();
                    } else {
                        ToastHelper.t(QuickRepairActivity.this, "Failed to get area");
                        finish();
                    }
                    break;
            }
        }
    }

    private RepairKindModel getDefaultKind() {
        return new RepairKindModel(-1, -1, "Select...");
    }

    private void updateAreaAdapter() {
        list_area.add(0, getDefaultKind());
        ((RepairKindListAdapter) sp_area.getAdapter()).notifyDataSetChanged();
    }

    private void getRepairKind(int areaNum) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairKind");
            j.put("areaNum", areaNum);
            j.put("nationCode", RoleInfo.getInstance().getUserNationCode());
            new GetRepairKind(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairKind extends IAsyncTask {

        GetRepairKind(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getRepairKind(response, list_kind, list_detail);
                    if (result == ApiResultHelper.SUCCESS) {
                        list_kind.add(0, getDefaultKind());
                        list_detail_show.clear();
                        list_detail_show.add(0, getDefaultKind());
                        updateKindAdapter();
                    } else {
                        ToastHelper.t(QuickRepairActivity.this, "Failed to get area");
                        finish();
                    }
                    break;
            }
        }
    }

    private void updateKindAdapter() {
        ((RepairKindListAdapter) sp_kind.getAdapter()).notifyDataSetChanged();
        ((RepairKindListAdapter) sp_detail.getAdapter()).notifyDataSetChanged();
    }

    private void addRepair(int kind, String place, String description, int areaNum) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addRepair");
            j.put("originSystem", "0");
            j.put("serviceEventID", "");
            j.put("writerID", RoleInfo.getInstance().getUserID());
            j.put("repairID", RoleInfo.getInstance().getUserID());
            j.put("eventKind", kind);
            j.put("place", place);
            j.put("description", description);
            j.put("areaNum", areaNum);
            new AddRepair(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddRepair extends IAsyncTask {

        AddRepair(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addRepair(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        ToastHelper.t(QuickRepairActivity.this, "Add Repair Success");
                        finish();
                    } else {
                        ToastHelper.t(QuickRepairActivity.this, "Add Repair Fail");
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_quick_repair_send:
                int areaNum = list_area.get(sp_area.getSelectedItemPosition()).getId();
                int kind = list_detail_show.get(sp_detail.getSelectedItemPosition()).getId();
                String place = et_place.getText().toString();
                String description = et_description.getText().toString();
                if (place.isEmpty() || description.isEmpty() || kind == -1) {
                    ToastHelper.t(this, "Can't be empty");
                    break;
                }
                // add repair
                addRepair(kind, place, description, areaNum);
                break;
        }
    }
}
