package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.AnnouncementListAdapter;
import goldenbrother.gbmobile.adapter.AnnouncementListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnnouncementListActivity extends CommonActivity {

    // ui
    private RecyclerView rv;
    // data
    private ArrayList<AnnouncementModel> list_announcement;
    // extra
    private int type;
    private String customerNo;
    private String flaborNo;
    private String nationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_list);
        // ui reference
        rv = (RecyclerView) findViewById(R.id.rv_announcement_list);
        // init ListView
        list_announcement = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AnnouncementListRVAdapter(this, list_announcement));
        // extra
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        customerNo = intent.getStringExtra("customerNo");
        flaborNo = intent.getStringExtra("flaborNo");
        nationCode = intent.getStringExtra("nationCode");
        // LoadAnnouncementList
        loadAnnouncementList();
    }

    private void loadAnnouncementList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getAnnouncementList");
            j.put("type", type);
            j.put("customerNo", customerNo);
            j.put("flaborNo", flaborNo);
            j.put("nationCode", nationCode);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new LoadAnnouncementList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadAnnouncementList extends IAsyncTask {

        LoadAnnouncementList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadAnnouncementList(response, list_announcement);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        t(R.string.fail);
                    }
                    break;
                default:
                    finish();
            }
        }
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    public void onItemClick(AnnouncementModel item) {
        Bundle b = new Bundle();
        b.putInt("announcementID", item.getAnnouncementID());
        b.putString("nationCode", nationCode);
        openActivity(AnnouncementContentActivity.class, b);
    }
}
