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
import android.widget.TextView;

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
    private RecyclerView rv;
    // extra
    private String userID;
    // data
    private ArrayList<AddEventModel> list_add_event;
    private ArrayList<RepairKindModel> list_area1, list_area2, list_kind, list_detail, list_detail_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // ui reference
        findViewById(R.id.iv_add_event_done).setOnClickListener(this);
        findViewById(R.id.fab_add_event).setOnClickListener(this);
        rv = findViewById(R.id.rv_add_event);

        // extra
        userID = getIntent().getStringExtra("userID");

        // init
        list_add_event = new ArrayList<>();
        list_area1 = new ArrayList<>();
        list_area2 = new ArrayList<>();
        list_kind = new ArrayList<>();
        list_detail = new ArrayList<>();
        list_detail_show = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AddEventKindRVAdapter(this, list_add_event));

        getRepairArea();
    }

    private void getRepairArea() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairArea");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
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
                    int result = ApiResultHelper.getRepairArea(response, list_area1, list_area2);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (list_area1.size() != 2 || list_area2.size() != 2) {
                            t(R.string.fail);
                            finish();
                        }
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
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
    private TextView tv_area, tv_type, tv_item;

    private void showAddEventDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_service_add_event, null);
        tv_area = v.findViewById(R.id.tv_dialog_service_add_event_area);
        tv_type = v.findViewById(R.id.tv_dialog_service_add_event_type);
        tv_item = v.findViewById(R.id.tv_dialog_service_add_event_item);
        tv_area.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        tv_item.setOnClickListener(this);
        final EditText et_description = v.findViewById(R.id.et_dialog_service_add_event_description);
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
                int areaNum = getAreaId(tv_area.getText().toString());
                int kind = getItemId(tv_item.getText().toString());
                String kindContent = tv_area.getText().toString() + "-" + tv_type.getText().toString() + "-" + tv_item.getText().toString();
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
        b.setView(v);
        ad = b.show();
    }

    private int getAreaId(String content) {
        for (RepairKindModel rk : list_area1) {
            if (rk.getContent().equals(content)) {
                return rk.getId();
            }
        }
        for (RepairKindModel rk : list_area2) {
            if (rk.getContent().equals(content)) {
                return rk.getId();
            }
        }
        return -1;
    }

    private void showAreaDialog(final int stage) { // 1 or 2
        if (list_area1.size() != 2 || list_area2.size() != 2) return;
        final String[] items1 = {list_area1.get(0).getContent(), list_area1.get(1).getContent()};
        final String[] items2 = {list_area2.get(0).getContent(), list_area2.get(1).getContent()};
        final String[] items = stage == 1 ? items1 : items2;
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (stage == 1 && i == 0) {
                    showAreaDialog(2);
                } else {
                    tv_area.setText(items[i]);
                    tv_type.setText("");
                    tv_item.setText("");
                    getRepairKind(getAreaId(items[i]));
                }
            }
        });
    }

    private int getTypeId(String content) {
        for (RepairKindModel rk : list_kind) {
            if (rk.getContent().equals(content)) {
                return rk.getId();
            }
        }
        return -1;
    }

    private void showTypeDialog() {
        final String[] items = new String[list_kind.size()];
        for (int i = 0; i < list_kind.size(); i++) {
            items[i] = list_kind.get(i).getContent();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_type.setText(items[i]);
                tv_item.setText("");
            }
        });
    }

    private int getItemId(String content) {
        for (RepairKindModel rk : list_detail_show) {
            if (rk.getContent().equals(content)) {
                return rk.getId();
            }
        }
        return -1;
    }

    private void showItemDialog(String type) {
        int parentId = getTypeId(type);
        list_detail_show.clear();
        for (RepairKindModel rm_detail : list_detail) {
            if (rm_detail.getParentId() == parentId) {
                list_detail_show.add(rm_detail);
            }
        }
        final String[] items = new String[list_detail_show.size()];
        for (int i = 0; i < list_detail_show.size(); i++) {
            items[i] = list_detail_show.get(i).getContent();
        }

        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_item.setText(items[i]);
            }
        });
    }

    private void getRepairKind(int areaNum) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairKind");
            j.put("areaNum", areaNum);
            j.put("nationCode", RoleInfo.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
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

                    } else {
                        t(String.format(getString(R.string.fail) + "(%s)", "GetRepairKind"));
                        finish();
                    }
                    break;
            }
        }
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

        AddEvent(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
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
            case R.id.tv_dialog_service_add_event_area:
                showAreaDialog(1);
                break;
            case R.id.tv_dialog_service_add_event_type:
                showTypeDialog();
                break;
            case R.id.tv_dialog_service_add_event_item:
                showItemDialog(tv_type.getText().toString());
                break;
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
