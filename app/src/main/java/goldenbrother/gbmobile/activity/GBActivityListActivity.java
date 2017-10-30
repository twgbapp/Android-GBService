package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.GBActivityListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.GBActivity;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class GBActivityListActivity extends CommonActivity implements View.OnClickListener {

    // gb activity type
    public static final int COM = 0;
    public static final int CLUB = 1;
    // ui
    private RecyclerView rv;
    // data
    private ArrayList<GBActivity> list_gb_activity, list_gb_activity_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gb_activity_list);
        setUpBackToolbar(R.id.toolbar, R.string.activity);

        // ui reference
        rv = findViewById(R.id.rv_activity_list);
        findViewById(R.id.ll_activity_list_com).setOnClickListener(this);
        findViewById(R.id.ll_activity_list_club).setOnClickListener(this);

        // init
        list_gb_activity = new ArrayList<>();
        list_gb_activity_show = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new GBActivityListRVAdapter(this, list_gb_activity_show));

        getActivityList();
    }

    private void getActivityList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getActivityList");
            j.put("customerNo", LaborModel.getInstance().getCustomerNo());
            j.put("nationCode", LaborModel.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetActivityList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetActivityList extends IAsyncTask {

        GetActivityList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getActivityList(response, list_gb_activity);
                    if (result == ApiResultHelper.SUCCESS) {
                        list_gb_activity_show.clear();
                        list_gb_activity_show.addAll(list_gb_activity);
                        rv.getAdapter().notifyDataSetChanged();
                    } else {
                        t(R.string.fail);
                    }
                    break;
                default:
                    finish();
            }
        }
    }

    public void onItemClick(GBActivity item) {
        Bundle b = new Bundle();
        b.putInt("activityID", item.getActivityID());
        openActivity(GBActivityContentActivity.class, b);
    }

    private void filter(int type) {
        list_gb_activity_show.clear();
        for (GBActivity item : list_gb_activity) {
            if (item.getType() == type)
                list_gb_activity_show.add(item);
        }
        rv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_activity_list_com:
//                filter(COM);
                break;
            case R.id.ll_activity_list_club:
//                filter(CLUB);
                break;
        }
    }
}
