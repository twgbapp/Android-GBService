package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ClubGridAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClubListActivity extends CommonActivity implements AdapterView.OnItemClickListener {

    private GridView gv;
    private ArrayList<ClubModel> list_club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);
        setUpBackToolbar(R.id.toolbar, R.string.club_title);
        // ui reference
        gv = findViewById(R.id.gv_club);
        // init GridView
        list_club = new ArrayList<>();
        gv.setAdapter(new ClubGridAdapter(this, list_club));
        gv.setOnItemClickListener(this);
        // load Club List
        loadClubList();
    }

    private void loadClubList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getClubList");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new LoadClubList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadClubList extends IAsyncTask {

        LoadClubList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadClubList(response, list_club);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        ClubGridAdapter adapter = (ClubGridAdapter) gv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Bundle b = new Bundle();
        b.putParcelable("club", list_club.get(position));
        openActivity(ClubPostActivity.class, b);
    }
}
