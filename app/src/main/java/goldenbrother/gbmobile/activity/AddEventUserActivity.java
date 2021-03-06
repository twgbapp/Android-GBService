package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.EventUserListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventUserModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class AddEventUserActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private ImageView iv_add_user;
    private ListView lv;
    //
    private int ServiceEventID;
    private ArrayList<EventUserModel> list_event_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_user);
        setUpBackToolbar(R.id.toolbar_add_event_user, R.id.tv_add_event_user_title, R.string.add_user_add_user);
        // ui reference
        iv_add_user = findViewById(R.id.iv_event_add_user_done);
        lv = findViewById(R.id.lv_add_event_user);
        // listener
        iv_add_user.setOnClickListener(this);
        // initListView
        list_event_user = new ArrayList<>();
        lv.setAdapter(new EventUserListAdapter(this, list_event_user));
        // get extra
        Intent intent = getIntent();
        ServiceEventID = intent.getIntExtra("serviceEventID", -1);
        // load user
        getEventUserList();
    }

    private void getEventUserList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getEventUserList");
            j.put("serviceEventID", ServiceEventID);
            j.put("dormID", RoleInfo.getInstance().getDormID());
            j.put("centerID", RoleInfo.getInstance().getCenterID());
            j.put("logStatus", false);
            new GetEventUserList(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class GetEventUserList extends IAsyncTask {


        GetEventUserList(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getEventUserList(response, list_event_user);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        t(R.string.empty);
                    }
                    break;
            }
        }
    }

    private void addEventGroup(HashSet<EventUserModel> userIDs) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addEventGroup");
            j.put("serviceEventID", ServiceEventID);
            JSONArray arr = new JSONArray();
            for (EventUserModel eu : userIDs) {
                arr.put(eu.getUserID());
            }
            j.put("userIDs", arr);
            j.put("logStatus", true);
            new AddEventGroup(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddEventGroup extends IAsyncTask {

        AddEventGroup(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
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

    private void updateAdapter() {
        EventUserListAdapter adapter = (EventUserListAdapter) lv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_event_add_user_done:
                HashSet<EventUserModel> set = ((EventUserListAdapter) lv.getAdapter()).getSelected();
                if (!set.isEmpty()) {
                    addEventGroup(set);
                }
                break;
        }
    }
}
