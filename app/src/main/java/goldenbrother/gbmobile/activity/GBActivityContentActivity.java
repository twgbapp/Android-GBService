package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.GBActivity;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class GBActivityContentActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_title, tv_expiration_date, tv_create_date;
    private WebView wv;
    // extra
    private int activityID;
    // data
    private GBActivity gbActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gb_activity_content);
        setUpBackToolbar(R.id.toolbar, R.string.activity);
        // ui reference
        tv_title = findViewById(R.id.tv_gb_activity_content_title);
        tv_create_date = findViewById(R.id.tv_gb_activity_content_create_date);
        tv_expiration_date = findViewById(R.id.tv_gb_activity_content_expiration_date);
        wv = findViewById(R.id.wv_gb_activity_content);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        tv_title.setOnClickListener(this);

        // extra
        activityID = getIntent().getExtras().getInt("activityID", -1);

        getActivity();
    }

    private void getActivity() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getActivity");
            j.put("activityID", activityID);
            j.put("nationCode", LaborModel.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new GetActivity(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetActivity extends IAsyncTask {

        GetActivity(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getActivity(response, gbActivity);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set title
                        tv_title.setText(gbActivity.getTitle());
                        // set create date
                        tv_create_date.setText(String.format(getString(R.string.ann_create_date) + " : %s", gbActivity.getCreateDate()));
                        // set expiration date
                        tv_expiration_date.setText(String.format(getString(R.string.ann_expiration_date) + " : %s", gbActivity.getExpirationDate()));
                        // set content
                        WebSettings webSettings = wv.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setUseWideViewPort(true);
                        webSettings.setLoadWithOverviewMode(true);
                        webSettings.setBuiltInZoomControls(true);
                        webSettings.setDisplayZoomControls(false);
                        wv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return true;
                            }
                        });
                        wv.setLongClickable(false);
                        wv.loadDataWithBaseURL("", gbActivity.getContent(), "text/html", "UTF-8", "");
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void showTitleDialog() {
        TextView tv = new TextView(this);
        tv.setTextSize(24f);
        tv.setPadding(20, 20, 20, 20);
        tv.setText(gbActivity.getTitle());
        alertWithView(tv, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_announcement_content_title:
                showTitleDialog();
                break;
        }
    }
}
