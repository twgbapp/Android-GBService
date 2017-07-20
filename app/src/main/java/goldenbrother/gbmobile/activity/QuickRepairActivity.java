package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
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
    private EditText et_applicant, et_title, et_place, et_description;
    private TextView tv_area, tv_type, tv_item, tv_send;
    // data
    private ArrayList<RepairKindModel> list_area1, list_area2, list_kind, list_detail, list_detail_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_repair);
        // ui reference
        et_applicant = (EditText) findViewById(R.id.et_quick_repair_applicant);
        et_title = (EditText) findViewById(R.id.et_quick_repair_title);
        et_place = (EditText) findViewById(R.id.et_quick_repair_place);
        et_description = (EditText) findViewById(R.id.et_quick_repair_description);
        tv_area = (TextView) findViewById(R.id.tv_quick_repair_area);
        tv_type = (TextView) findViewById(R.id.tv_quick_repair_type);
        tv_item = (TextView) findViewById(R.id.tv_quick_repair_item);
        tv_send = (TextView) findViewById(R.id.tv_quick_repair_send);
        // listener
        tv_area.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        tv_item.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        // init Spinner
        list_area1 = new ArrayList<>();
        list_area2 = new ArrayList<>();
        list_kind = new ArrayList<>();
        list_detail = new ArrayList<>();
        list_detail_show = new ArrayList<>();
        // set applicant
        et_applicant.setText(RoleInfo.getInstance().getUserName());
        // load area
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
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void addRepair(int kind, String place, String title, String description, int areaNum) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addRepair");
            j.put("originSystem", "0");
            j.put("serviceEventID", "");
            j.put("writerID", RoleInfo.getInstance().getUserID());
            j.put("repairID", RoleInfo.getInstance().getUserID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("eventKind", kind);
            j.put("place", place);
            j.put("title", title);
            j.put("description", description);
            j.put("areaNum", areaNum);
            j.put("logStatus", true);
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
        int id = v.getId();
        switch (id) {
            case R.id.tv_quick_repair_area:
                showAreaDialog(1);
                break;
            case R.id.tv_quick_repair_type:
                showTypeDialog();
                break;
            case R.id.tv_quick_repair_item:
                showItemDialog(tv_type.getText().toString());
                break;
            case R.id.tv_quick_repair_send:
                int areaNum = getAreaId(tv_area.getText().toString());
                int kind = getItemId(tv_item.getText().toString());
                String place = et_place.getText().toString();
                String title = et_title.getText().toString();
                String description = et_description.getText().toString();
                if (place.isEmpty() || title.isEmpty() || description.isEmpty() || areaNum == -1 || kind == -1) {
                    t(R.string.can_not_be_empty);
                    break;
                }
                // add repair
                addRepair(kind, place, title, description, areaNum);
                break;
        }
    }
}
