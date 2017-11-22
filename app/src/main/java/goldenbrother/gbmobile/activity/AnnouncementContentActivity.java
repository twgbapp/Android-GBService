package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class AnnouncementContentActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_title, tv_expiration_date, tv_create_date;
    private WebView wv;
    // extra
    private int announcementID;
    // data
    private AnnouncementModel announcement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_content);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_announcement);

        // ui reference
        tv_title = findViewById(R.id.tv_announcement_content_title);
        tv_create_date = findViewById(R.id.tv_announcement_content_create_date);
        tv_expiration_date = findViewById(R.id.tv_announcement_content_expiration_date);
        wv = findViewById(R.id.wv_announcement_content);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        tv_title.setOnClickListener(this);

        // extra
        announcementID = getIntent().getExtras().getInt("announcementID", -1);

        announcement = new AnnouncementModel();
        loadAnnouncement();
    }

    private void loadAnnouncement() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getAnnouncement");
            j.put("announcementId", announcementID);
//            j.put("nationCode", LaborModel.getInstance().getUserNationCode());
            j.put("nationCode", "024");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new LoadAnnouncement(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadAnnouncement extends IAsyncTask {

        LoadAnnouncement(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadAnnouncement(response, announcement);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set title
                        tv_title.setText(announcement.getTitle());
                        // set create date
                        tv_create_date.setText(String.format(getString(R.string.ann_create_date) + " : %s", announcement.getCreateDate()));
                        // set expiration date
                        tv_expiration_date.setText(String.format(getString(R.string.ann_expiration_date) + " : %s", announcement.getExpirationDate()));
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
                        wv.loadDataWithBaseURL("", announcement.getContent(), "text/html", "UTF-8", "");
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
        tv.setText(announcement.getTitle());
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
