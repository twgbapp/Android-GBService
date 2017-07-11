package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.AddEventKindRVAdapter;
import goldenbrother.gbmobile.adapter.RepairKindListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.AddEventModel;
import goldenbrother.gbmobile.model.RepairKindModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

// add multi event
public class AddEventActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private View iv_done, fab_add_event;
    private RecyclerView rv;
    // extra
    private String userID;
    // data
    private ArrayList<AddEventModel> list_add_event;
    private ArrayList<RepairKindModel> list_area, list_kind, list_detail, list_detail_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // ui reference
        iv_done = findViewById(R.id.iv_add_event_done);
        fab_add_event = findViewById(R.id.fab_add_event);
        rv = (RecyclerView) findViewById(R.id.rv_add_event);
        // listener
        iv_done.setOnClickListener(this);
        fab_add_event.setOnClickListener(this);
        // extra
        userID = getIntent().getStringExtra("userID");
        // init AddEventList
        list_add_event = new ArrayList<>();
        // init EventKind
        list_area = new ArrayList<>();
        list_area.add(0, getDefaultKind());
        list_kind = new ArrayList<>();
        list_kind.add(0, getDefaultKind());
        list_detail = new ArrayList<>();
        list_detail_show = new ArrayList<>();
        list_detail_show.add(0, getDefaultKind());
        // init RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AddEventKindRVAdapter(this, list_add_event));
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
                        list_area.add(0, getDefaultKind());
                    } else {
                        t(String.format(getString(R.string.fail) + "(%s)", "GetRepairArea"));
                        finish();
                    }
                    break;
            }
        }
    }

    private RepairKindModel getDefaultKind() {
        return new RepairKindModel(-1, -1, getString(R.string.select) + "...");
    }

    private void updateAdapter() {
        AddEventKindRVAdapter adapter = (AddEventKindRVAdapter) rv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // add event dialog
    private AlertDialog ad;
    // dialog ui
    private Spinner sp_area, sp_kind, sp_detail;

    private void showAddEventDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_service_add_event, null);
        sp_area = (Spinner) v.findViewById(R.id.sp_dialog_service_add_event_area);
        sp_kind = (Spinner) v.findViewById(R.id.sp_dialog_service_add_event_kind);
        sp_detail = (Spinner) v.findViewById(R.id.sp_dialog_service_add_event_detail);
        final EditText et_description = (EditText) v.findViewById(R.id.et_dialog_service_add_event_description);
        // cancel listener
        v.findViewById(R.id.tv_dialog_service_add_event_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null) ad.dismiss();
            }
        });
        // ok listener
        v.findViewById(R.id.tv_dialog_service_add_event_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                String description = et_description.getText().toString();
                int kind = list_detail_show.get(sp_detail.getSelectedItemPosition()).getId();
                String kindContent = list_area.get(sp_area.getSelectedItemPosition()).getContent() + "-" + list_kind.get(sp_kind.getSelectedItemPosition()).getContent() + "-" + list_detail_show.get(sp_detail.getSelectedItemPosition()).getContent();
                // check
                if (description.isEmpty() || kind == -1) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                // add model
                AddEventModel m = new AddEventModel();
                m.setUserID(userID);
                m.setEventKind(kind);
                m.setKindContent(kindContent);
                m.setEventDescription(description);
                m.setStaffID(RoleInfo.getInstance().getUserID());
                list_add_event.add(m);
                // update
                updateAdapter();
                // dismiss
                if (ad != null) ad.dismiss();
            }
        });
        // init Spinner
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
        b.setView(v);
        ad = b.show();
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
                        t(String.format(getString(R.string.fail) + "(%s)", "GetRepairKind"));
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

    private void showConfirmAddEventDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_event_add_event))
                .setMessage(getString(R.string.add_event_message))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addEvent();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void addEvent() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addEvent");
            // add to array
            JSONArray arr = new JSONArray();
            for (AddEventModel m : list_add_event) {
                JSONObject jo = new JSONObject();
                jo.put("userID", m.getUserID());
                jo.put("eventDescription", m.getEventDescription());
                jo.put("eventKind", m.getEventKind());
                jo.put("staffID", m.getStaffID());
                arr.put(jo);
            }
            j.put("events", arr.toString());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new AddEvent(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddEvent extends IAsyncTask {
        private HashMap<String, Integer> map;

        AddEvent(Context context, JSONObject json, String url) {
            super(context, json, url);
            this.map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addEvent(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_event_done:
                if (list_add_event.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    break;
                }
                showConfirmAddEventDialog();
                break;
            case R.id.fab_add_event:
                showAddEventDialog();
                break;
        }
    }
}
