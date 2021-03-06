package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.DiscussionListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Discussion;

public class DiscussionListActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_SEARCH = 0;
    // ui
    private RecyclerView rv;
    // data
    private ArrayList<Discussion> list_discussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_list);
        setUpBackToolbar(R.id.toolbar_discussion_list, R.id.tv_discussion_list_title, R.string.discussion_list);
        // ui reference
        findViewById(R.id.iv_discussion_list_search).setOnClickListener(this);
        findViewById(R.id.iv_discussion_list_add).setOnClickListener(this);
        rv = findViewById(R.id.rv_discussion_list);

        // init RecyclerView
        list_discussion = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new DiscussionListRVAdapter(this, list_discussion));
    }

    private void getDiscussionFlaborList(String dormId, String customerNo, String flaborNo) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getDiscussionFlaborList");
            j.put("startRecordDate", "2017-05-01");
            j.put("endRecordDate", TimeHelper.date());
            j.put("dormID", dormId);
            j.put("customerNo", customerNo);
            if (flaborNo != null)
                j.put("flaborNo", flaborNo);

            new GetDiscussionFlaborList(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDiscussionFlaborList extends IAsyncTask {

        GetDiscussionFlaborList(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getDiscussionFlaborList(response, list_discussion);
                    if (result == ApiResultHelper.SUCCESS) {
                        // sort
                        Collections.sort(list_discussion, new Comparator<Discussion>() {
                            @Override
                            public int compare(Discussion o1, Discussion o2) {
                                return o2.getDiscussionDate().compareTo(o1.getDiscussionDate());
                            }
                        });
                    } else {
                        list_discussion.clear();
                    }
                    rv.getAdapter().notifyDataSetChanged();
                    break;
            }
        }
    }

    public void onItemClick(Discussion item) {
        Bundle b = new Bundle();
        b.putParcelable("discussion", item);
        b.putBoolean("isAdd", false);
        openActivity(DiscussionRecordActivity.class, b);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.iv_discussion_list_search:
                b.putBoolean("isFLabor", true);
                openActivityForResult(SearchActivity.class, REQUEST_SEARCH, b);
                break;
            case R.id.iv_discussion_list_add:
                b.putBoolean("isAdd", true);
                openActivity(DiscussionRecordActivity.class, b);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_SEARCH:
                getDiscussionFlaborList(data.getStringExtra("dormId"), data.getStringExtra("customerNo"), data.getStringExtra("flaborNo"));
                break;
        }
    }
}
