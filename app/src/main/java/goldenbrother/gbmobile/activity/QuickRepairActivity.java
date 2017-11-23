package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RepairKindModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuickRepairActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_applicant, et_title, et_place, et_description;
    private TextView tv_area, tv_type, tv_item, tv_send;
    // extra
    private boolean isSupport; // 是否為需求支援
    // data
    private ArrayList<RepairKindModel> list_area, list_kind, list_detail, list_detail_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_repair);

        // extra
        isSupport = getIntent().getExtras().getBoolean("support", false);

        // ui reference
        et_applicant = findViewById(R.id.et_quick_repair_applicant);
        et_title = findViewById(R.id.et_quick_repair_title);
        et_place = findViewById(R.id.et_quick_repair_place);
        et_description = findViewById(R.id.et_quick_repair_description);
        tv_area = findViewById(R.id.tv_quick_repair_area);
        tv_type = findViewById(R.id.tv_quick_repair_type);
        tv_item = findViewById(R.id.tv_quick_repair_item);
        tv_send = findViewById(R.id.tv_quick_repair_send);
        tv_area.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        tv_item.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        // init
        setUpBackToolbar(R.id.toolbar, isSupport ? R.string.support : R.string.quick_repair);
        list_area = new ArrayList<>();
        list_kind = new ArrayList<>();
        list_detail = new ArrayList<>();
        list_detail_show = new ArrayList<>();
        et_applicant.setText(RoleInfo.getInstance().getUserName());
        getRepairArea();
    }

    private void getRepairArea() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairArea");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetRepairArea(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairArea extends IAsyncTask {

        GetRepairArea(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getRepairArea(response, list_area);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (list_area.size() >= 3) {
                            String area = isSupport ? list_area.get(2).getContent() : list_area.get(0).getContent();
                            tv_area.setText(area);
                            getRepairKind(getAreaId(area));
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
        for (RepairKindModel item : list_area) {
            if (item.getContent().equals(content)) {
                return item.getId();
            }
        }
        return -1;
    }

    private void showAreaDialog() {
        String[] items_repair = {list_area.get(0).getContent(), list_area.get(1).getContent()};
        String[] items_support = {list_area.get(2).getContent()};
        final String[] items = isSupport ? items_support : items_repair;
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_area.setText(items[i]);
                tv_type.setText("");
                tv_item.setText("");
                getRepairKind(getAreaId(items[i]));
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
            new GetRepairKind(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairKind extends IAsyncTask {

        GetRepairKind(Context context, JSONObject json) {
            super(context, json);
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
            j.put("description", description);
            j.put("place", place);
            j.put("eventKind", kind);
            j.put("areaNum", areaNum);
            j.put("dormID", RoleInfo.getInstance().getDormID());
            j.put("centerID", RoleInfo.getInstance().getCenterID());
            if (RoleInfo.getInstance().isLabor()) {
                j.put("customerNo", LaborModel.getInstance().getCustomerNo());
                j.put("flaborNo", LaborModel.getInstance().getFlaborNo());
            }
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            j.put("title", title);
            new AddRepair(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddRepair extends IAsyncTask {

        AddRepair(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.quick_repair_success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void addDemandSupport(int kind, String place, String title, String description, int areaNum) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addDemandSupport");
            j.put("centerID", RoleInfo.getInstance().getCenterID());
            j.put("dormID", RoleInfo.getInstance().getDormID());
            j.put("place", place);
            j.put("eventKind", kind);
            j.put("customerNo", LaborModel.getInstance().getCustomerNo());
            j.put("flaborNo", LaborModel.getInstance().getFlaborNo());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("description", description);
            j.put("areaNum", areaNum);
            j.put("writerID", RoleInfo.getInstance().getUserID());
            j.put("repairID", RoleInfo.getInstance().getUserID());
            j.put("title", title);
            j.put("logStatus", true);
            new AddDemandSupport(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddDemandSupport extends IAsyncTask {

        AddDemandSupport(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.quick_repair_success);
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
            case R.id.tv_quick_repair_area:
                showAreaDialog();
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
                if (isSupport) {
                    addDemandSupport(kind, place, title, description, 3);
                } else {
                    addRepair(kind, place, title, description, areaNum);
                }
                break;
        }
    }
}
