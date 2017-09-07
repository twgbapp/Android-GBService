package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.SatisfactionQuestionRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.SatisfactionIssueModel;
import goldenbrother.gbmobile.model.SatisfactionQuestionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SatisfactionQuestionActivity extends CommonActivity {

    // ui
    private RecyclerView rv;
    // extra
    private SatisfactionIssueModel issue;
    // data
    private ArrayList<SatisfactionQuestionModel> list_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satisfaction_question);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_satisfaction_survey);
        // ui reference
        rv = (RecyclerView) findViewById(R.id.rv_satisfaction_question);
        // extra
        issue = getIntent().getParcelableExtra("issue");
        // init RecyclerView
        list_question = new ArrayList<>();
        list_question.addAll(issue.getQuestions());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new SatisfactionQuestionRVAdapter(this, list_question));
    }

    public void addSatisfactionIssueRecord(int siqNo, int rating) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addSatisfactionIssueRecord");
            j.put("siqNo", siqNo);
            j.put("customerNo", LaborModel.getInstance().getCustomerNo());
            j.put("flaborNo", LaborModel.getInstance().getFlaborNo());
            j.put("createDateStr", TimeHelper.getStandard());
            j.put("rating", rating);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new AddSatisfactionIssueRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddSatisfactionIssueRecord extends IAsyncTask {

        AddSatisfactionIssueRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.addSatisfactionIssueRecord(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

}
