package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;

import org.json.JSONException;
import org.json.JSONObject;

public class AnnouncementContentActivity extends CommonActivity {

    // ui
    private TextView tv_title, tv_expiration_date, tv_create_date;
    private WebView wv;
    // data
    private AnnouncementModel announcement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_content);
        // ui reference
        tv_title = (TextView) findViewById(R.id.tv_announcement_content_title);
        tv_create_date = (TextView) findViewById(R.id.tv_announcement_content_create_date);
        tv_expiration_date = (TextView) findViewById(R.id.tv_announcement_content_expiration_date);
        wv = (WebView) findViewById(R.id.wv_announcement_content);
        // extra && init announcement object
        announcement = new AnnouncementModel();
        announcement.setAnnouncementID(getIntent().getExtras().getInt("announcementID", -1));
        announcement.setNationCode(getIntent().getExtras().getString("nationCode"));
        // LoadAnnouncement
        loadAnnouncement();
    }

    private void loadAnnouncement() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getAnnouncement");
            j.put("announcementId", announcement.getAnnouncementID());
            j.put("nationCode", announcement.getNationCode());
            new LoadAnnouncement(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadAnnouncement extends IAsyncTask {

        LoadAnnouncement(Context context, JSONObject json, String url) {
            super(context, json, url);
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
                        tv_create_date.setText(String.format(getString(R.string.ann_create_date)+" : %s", announcement.getCreateDate()));
                        // set expiration date
                        tv_expiration_date.setText(String.format(getString(R.string.ann_expiration_date)+" : %s", announcement.getExpirationDate()));
                        // set content
                        wv.getSettings().setJavaScriptEnabled(true);
                        wv.getSettings().setBuiltInZoomControls(true);
                        wv.getSettings().setDisplayZoomControls(false);
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
}
