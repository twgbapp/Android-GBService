package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ClubPostRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClubPostActivity extends CommonActivity {
    // request
    public static final int REQUEST_ADD_POST = 0;
    public static final int REQUEST_ADD_POST_MESSAGE = 1;
    // ui
    private SwipeRefreshLayout srl;
    private RecyclerView rv;
    // data
    private ClubModel club;
    private ArrayList<Integer> list_club_id;
    private ArrayList<ClubPostModel> list_club_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_post);

        // ui reference
        srl = (SwipeRefreshLayout) findViewById(R.id.srl_club_post);
        rv = (RecyclerView) findViewById(R.id.rv_club_post);
        // get extra
        Intent intent = getIntent();
        club = intent.getExtras().getParcelable("club");
        // init Swipe Refresh Layout
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                loadAllClubPostID();
            }
        });
        srl.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // init RecyclerView
        list_club_id = new ArrayList<>();
        list_club_post = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new ClubPostRVAdapter(this, club, list_club_post));
        // listener
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItem = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                onLoadMore(totalItem, lastVisibleItem);
            }
        });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    srl.setEnabled(true);
                } else {
                    srl.setEnabled(false);
                }
            }
        });
        // load all club post ID
        loadAllClubPostID();
    }

    private void updateAdapter() {
        ClubPostRVAdapter adapter = (ClubPostRVAdapter) rv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setMessageCount(int clubPostID, ArrayList<ClubPostMessageModel> list, int messageCount) {
        for (ClubPostModel m : list_club_post) {
            if (m.getClubPostID() == clubPostID) {
                m.getMessages().clear();
                m.getMessages().addAll(list);
                m.setMessageCount(messageCount);
                break;
            }
        }
        updateAdapter();
    }


    private void loadAllClubPostID() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getAllClubPostID");
            j.put("clubID", club.getClubID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new LoadAllClubPostID(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadAllClubPostID extends IAsyncTask {

        LoadAllClubPostID(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_club_id.clear();
            list_club_post.clear();
            updateAdapter();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadAllClubPostID(response, list_club_id);
                    if (result == ApiResultHelper.SUCCESS) {
                        list_club_post.clear();
                        index = -1;
                        loadClubPostList();
                    }else{
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void loadClubPostList() {
        try {
            ArrayList<Integer> nos = nextLoadNos();
            JSONObject j = new JSONObject();
            j.put("action", "getClubPost");
            JSONArray arr = new JSONArray();
            for (Integer no : nos) {
                arr.put(no);
            }
            j.put("clubPostIDs", arr.toString());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            if (!nos.isEmpty()) {
                loading = true;
                new LoadClubPostList(this, j, URLHelper.HOST).execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadClubPostList extends IAsyncTask {

        LoadClubPostList(Context context, JSONObject json, String url) {
            super(context, json, url);
            setShow(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            loading = false;
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadClubPostList(response, list_club_post);
                    if (result == ApiResultHelper.SUCCESS) {
                        index = index + onceLoadCount > list_club_id.size() - 1 ? list_club_id.size() - 1 : index + onceLoadCount;
                        updateAdapter();
                    }
                    break;
            }
        }
    }

    // load more
    private static final int remainToLoadMore = 1; //
    private static final int onceLoadCount = 10;
    private boolean loading = false;
    private int index = -1;

    private void onLoadMore(int totalItemCount, int lastVisibleItem) {
        /*
        *       1.total不能為0
        *       2.當進入"剩餘範圍(1)"
        *       3.不能正在載入狀態
        *       4.id數不等於總數(代表所有id都載完了)
        * */
        if (totalItemCount != 0 && (lastVisibleItem + remainToLoadMore) >= totalItemCount &&
                !loading && list_club_id.size() != totalItemCount) {
            loadClubPostList();
        }
    }

    private ArrayList<Integer> nextLoadNos() {
        ArrayList<Integer> r = new ArrayList<>();
        int start = index + 1;
        int end = index + onceLoadCount > list_club_id.size() - 1 ? list_club_id.size() - 1 : index + onceLoadCount;
        for (int i = start; i <= end; i++) {
            r.add(list_club_id.get(i));
        }
        return r;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_POST:
                    loadAllClubPostID();
                    break;
                case REQUEST_ADD_POST_MESSAGE:
                    int clubPostID = data.getIntExtra("clubPostID", -1);
                    ArrayList<ClubPostMessageModel> list_message = data.getParcelableArrayListExtra("lastMessage");
                    int messageCount = data.getIntExtra("messageCount", -1);
                    setMessageCount(clubPostID, list_message, messageCount);
                    break;
            }
        }
    }
}
