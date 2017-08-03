package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.DiscussionListRVAdapter;
import goldenbrother.gbmobile.helper.LogHelper;
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

        // ui reference
        findViewById(R.id.iv_discussion_list_search).setOnClickListener(this);
        findViewById(R.id.iv_discussion_list_add).setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv_discussion_list);

        // init RecyclerView
        list_discussion = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new DiscussionListRVAdapter(this, list_discussion));

        // getMedicalList
//        getMedicalFlaborList();
    }

    public void onItemClick(Discussion item) {
        Bundle b = new Bundle();
        b.putParcelable("discussion", item);
        openActivity(DiscussionRecordActivity.class, b);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.iv_discussion_list_search:
                b.putBoolean("isSearchFlabor", true);
                openActivityForResult(SearchCustomerActivity.class, REQUEST_SEARCH, b);
                break;
            case R.id.iv_discussion_list_add:
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
                LogHelper.d(data.getStringExtra("dormId"));
                LogHelper.d(data.getStringExtra("customerNo"));
                LogHelper.d(data.getStringExtra("flaborNo"));
                break;
        }
    }
}
