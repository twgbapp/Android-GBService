package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.SatisfactionIssueRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.SatisfactionIssueModel;
import goldenbrother.gbmobile.model.SatisfactionQuestionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SatisfactionIssueActivity extends CommonActivity {

    // ui
    private SwipeRefreshLayout srl;
    private RecyclerView rv;
    // data
    private ArrayList<SatisfactionIssueModel> list_issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satisfaction_issue);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_satisfaction_survey);
        // ui reference
        srl = (SwipeRefreshLayout) findViewById(R.id.srl_satisfaction_issue);
        rv = (RecyclerView) findViewById(R.id.rv_satisfaction_issue);
        // init Swipe Refresh Layout
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                getSatisfactionIssueList();
            }
        });
        srl.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // init RecyclerView
        list_issue = new ArrayList<>();
//        list_issue.addAll(getData());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new SatisfactionIssueRVAdapter(this, list_issue));
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
        // getSatisfactionIssueList
        getSatisfactionIssueList();
    }

    private ArrayList<SatisfactionIssueModel> getData() {
        ArrayList<SatisfactionIssueModel> list = new ArrayList<>();
        SatisfactionIssueModel si = new SatisfactionIssueModel();
        si.setName("Room");
        si.setCreateDate("2017-01-01 00:00:00");
        si.getQuestions().add(new SatisfactionQuestionModel("Light"));
        si.getQuestions().add(new SatisfactionQuestionModel("Air"));
        si.getQuestions().add(new SatisfactionQuestionModel("Bed"));
        list.add(si);
        return list;
    }

    private void getSatisfactionIssueList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getSatisfactionIssueList");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new GetSatisfactionIssueList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetSatisfactionIssueList extends IAsyncTask {

        GetSatisfactionIssueList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getSatisfactionIssueList(response, list_issue);
                    if (result == ApiResultHelper.SUCCESS) {
                        // refresh
                        updateAdapter();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }
}
