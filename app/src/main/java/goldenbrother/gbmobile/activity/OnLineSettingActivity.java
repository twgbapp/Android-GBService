package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.OnCallManagerListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.BasicUser;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnLineSettingActivity extends CommonActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    // onCallStatus
    private static final String ON_LINE = "1";
    private static final String OFF_LINE = "0";
    // ui
    private Switch sw_online;
    private LinearLayout ll;
    private ListView lv_staff;
    // data
    private ArrayList<BasicUser> list_on_call_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line_setting);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_online_setting);

        // ui reference
        sw_online = findViewById(R.id.sw_on_line_setting);
        ll = findViewById(R.id.ll_on_line_setting);
        lv_staff = findViewById(R.id.lv_on_line_setting_staff);
        findViewById(R.id.tv_on_line_setting_confirm).setOnClickListener(this);
        sw_online.setOnCheckedChangeListener(this);

        // init
        list_on_call_manager = new ArrayList<>();
        lv_staff.setAdapter(new OnCallManagerListAdapter(this, list_on_call_manager));

        getOnCallManage();
        getOnCallStatus();
        viewToOnLine();
    }

    private void getOnCallStatus() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getOnCallStatus");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new getOnCallStatus(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class getOnCallStatus extends IAsyncTask {

        private Map<String, String> map;

        getOnCallStatus(Context context, JSONObject json, String url) {
            super(context, json, url);
            this.map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getOnCallStatus(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        switch (map.get("onCallStatus")) {
                            case ON_LINE:
                                viewToOnLine();
                                break;
                            case OFF_LINE:
                                viewToOffLine();
                                break;
                        }
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void getOnCallManage() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getOnCallManage");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetOnCallManage(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetOnCallManage extends IAsyncTask {

        GetOnCallManage(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getOnCallManage(response, list_on_call_manager);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        t(R.string.empty);
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        ((OnCallManagerListAdapter) lv_staff.getAdapter()).notifyDataSetChanged();
    }

    private void changeOnCallStatus(String staffID, String onCallStatus) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "changeOnCallStatus");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("staffID", staffID);
            j.put("onCallStatus", onCallStatus);
            j.put("logStatus", true);
            new ChangeOnCallStatus(this, j, URLHelper.HOST, onCallStatus).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChangeOnCallStatus extends IAsyncTask {
        private String onCallStatus;

        ChangeOnCallStatus(Context context, JSONObject json, String url, String onCallStatus) {
            super(context, json, url);
            this.onCallStatus = onCallStatus;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (onCallStatus.equals(OFF_LINE)) {
                            t(R.string.offline);
                            viewToOffLine();
                        } else if (onCallStatus.equals(ON_LINE)) {
                            viewToOnLine();
                            t(R.string.online);
                        }
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void viewToOnLine(){
        sw_online.setChecked(true);
        sw_online.setText(R.string.online);
        ll.setVisibility(View.GONE);
    }

    private void viewToOffLine(){
        sw_online.setChecked(false);
        sw_online.setText(R.string.offline);
        ll.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            viewToOnLine();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.online)
                    .setMessage(R.string.turn_to_online)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // online
                            changeOnCallStatus("", ON_LINE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewToOffLine();
                        }
                    })
                    .show();
        } else {
            viewToOffLine();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_on_line_setting_confirm:
                int selectedPosition = ((OnCallManagerListAdapter) lv_staff.getAdapter()).getSelectedPosition();
                if (selectedPosition != -1) {
                    changeOnCallStatus(list_on_call_manager.get(selectedPosition).getUserID(), OFF_LINE);
                } else {
                    t(R.string.select_nothing);
                }
                break;
        }
    }
}
