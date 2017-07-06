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
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventUserModel;

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
        // ui reference
        iv_add_user = (ImageView) findViewById(R.id.iv_event_add_user_done);
        lv = (ListView) findViewById(R.id.lv_add_event_user);
        // listener
        iv_add_user.setOnClickListener(this);
        // initListView
        list_event_user = new ArrayList<>();
        lv.setAdapter(new EventUserListAdapter(this, list_event_user));
        // get extra
        Intent intent = getIntent();
        ServiceEventID = intent.getIntExtra("serviceEventID", -1);
        // load user
        loadEventUserList();
    }

    private void loadEventUserList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getEventUserList");
            j.put("serviceEventID", ServiceEventID);
            new LoadEventUserList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class LoadEventUserList extends IAsyncTask {


        LoadEventUserList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadEventUserList(response, list_event_user);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        ToastHelper.t(AddEventUserActivity.this, "empty");
                    }
                    break;
            }
        }
    }

    private void addEventGroup(String userID) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getEventUserList");
            j.put("serviceEventID", ServiceEventID);
            j.put("userID", userID);
            new AddEventGroup(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class AddEventGroup extends IAsyncTask {

        AddEventGroup(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.addEventGroup(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        ToastHelper.t(AddEventUserActivity.this, "Add User Success");
                        finish();
                    } else {
                        ToastHelper.t(AddEventUserActivity.this, "Add User Fail");
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
                EventUserListAdapter adapter = (EventUserListAdapter) lv.getAdapter();
                if (adapter != null) {
                    HashSet<EventUserModel> set = adapter.getSelected();
                    int size = set.size();
                    String userID = "[";
                    int count = 0;
                    for (EventUserModel e : set) {
                        userID += (count == 0 ? "" : ",") + e.getUserID();
                        count++;
                    }
                    userID += "]";
                    if (size != 0) {
                        addEventGroup(userID);
                    }
                }
                break;
        }
    }
}
