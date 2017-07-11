package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.OnCallManagerListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.OnCallManagerModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OnLineSettingActivity extends CommonActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    // ui
    private Switch sw_online;
    private LinearLayout ll;
    private ListView lv_staff;
    private TextView tv_confirm;
    //
    private ArrayList<OnCallManagerModel> list_on_call_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line_setting);

        // ui reference
        sw_online = (Switch) findViewById(R.id.sw_on_line_setting);
        ll = (LinearLayout) findViewById(R.id.ll_on_line_setting);
        lv_staff = (ListView) findViewById(R.id.lv_on_line_setting_staff);
        tv_confirm = (TextView) findViewById(R.id.tv_on_line_setting_confirm);
        // listener
        sw_online.setOnCheckedChangeListener(this);
        tv_confirm.setOnClickListener(this);
        // initListView
        list_on_call_manager = new ArrayList<>();
        lv_staff.setAdapter(new OnCallManagerListAdapter(this, list_on_call_manager));
        // loadOnCallManager
        loadOnCallManager();
    }

    private void loadOnCallManager() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getOnCallManage");
            j.put("userID", RoleInfo.getInstance().getUserID());
            new LoadOnCallManager(this, j,URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadOnCallManager extends IAsyncTask {

        LoadOnCallManager(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadOnCallManager(response, list_on_call_manager);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        ToastHelper.t(OnLineSettingActivity.this, "empty");
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        OnCallManagerListAdapter adapter = (OnCallManagerListAdapter) lv_staff.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void changeOnCallStatus(String staffID, String onCallStatus) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "changeOnCallStatus");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("staffID", staffID);
            j.put("onCallStatus", onCallStatus);
            j.put("logStatus", true);
            new ChangeOnCallStatus(this, j,URLHelper.HOST,onCallStatus).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChangeOnCallStatus extends IAsyncTask {
        private String onCallStatus;

        ChangeOnCallStatus(Context context, JSONObject json, String url,String onCallStatus) {
            super(context, json, url);
            this.onCallStatus=onCallStatus;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.changeOnCallStatus(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (onCallStatus.equals("0")) { // offline
                            ToastHelper.t(OnLineSettingActivity.this, "off line");
                        } else if (onCallStatus.equals("1")) { // online
                            ToastHelper.t(OnLineSettingActivity.this, "on line");
                        }
                    } else {
                        ToastHelper.t(OnLineSettingActivity.this, "Change Status Fail");
                    }
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // show or not show staff list
        if (isChecked) {
            ll.setVisibility(View.GONE);
            new AlertDialog.Builder(this)
                    .setTitle("OnLine")
                    .setMessage("Turn to OnLine ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // online
                            changeOnCallStatus("", "1");
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        } else {
            ll.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_on_line_setting_confirm:
                // get selected
                OnCallManagerListAdapter adapter = (OnCallManagerListAdapter) lv_staff.getAdapter();
                if (adapter != null) {
                    // params
                    String staffID = adapter.getSelectedUserID();
                    // offline
                    changeOnCallStatus(staffID, "0");
                }
                break;
        }
    }
}
