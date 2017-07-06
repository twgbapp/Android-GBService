package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.RepairRecordRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RepairRecordModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RepairRecordActivity extends CommonActivity {

    // ui
    private RecyclerView rv;
    // data
    private ArrayList<RepairRecordModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_record);
        // ui reference
        rv = (RecyclerView) findViewById(R.id.rv_repair_record);
        // init ListView
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RepairRecordRVAdapter(this, list));
        // get repair record
        getRepairRecordList();
    }

    public void getRepairRecordList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairRecordList");
            j.put("userID", RoleInfo.getInstance().getUserID());
            new GetRepairRecordList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairRecordList extends IAsyncTask {


        GetRepairRecordList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getRepairRecordList(response, list);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        ToastHelper.t(RepairRecordActivity.this, "empty");
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        RepairRecordRVAdapter adapter = (RepairRecordRVAdapter) rv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
