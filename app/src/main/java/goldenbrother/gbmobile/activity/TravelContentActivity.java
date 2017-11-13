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
import goldenbrother.gbmobile.model.Travel;

public class TravelContentActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_title, tv_expiration_date, tv_create_date;
    private WebView wv;
    // extra
    private int travelID;
    // data
    private Travel travel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_content);
        setUpBackToolbar(R.id.toolbar, R.string.travel);

        // ui reference
        tv_title = findViewById(R.id.tv_travel_content_title);
        tv_create_date = findViewById(R.id.tv_travel_content_create_date);
        tv_expiration_date = findViewById(R.id.tv_travel_content_expiration_date);
        wv = findViewById(R.id.wv_travel_content);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        tv_title.setOnClickListener(this);

        // extra
        travelID = getIntent().getExtras().getInt("travelID", -1);

        // init
        travel = new Travel();
        getTravel();
    }

    private void getTravel() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getTravel");
            j.put("travelID", travelID);
//            j.put("nationCode", LaborModel.getInstance().getUserNationCode());
            j.put("nationCode", "024");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new GetTravel(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetTravel extends IAsyncTask {

        GetTravel(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getTravel(response, travel);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set title
                        tv_title.setText(travel.getTitle());
                        // set create date
                        tv_create_date.setText(String.format(getString(R.string.ann_create_date) + " : %s", travel.getCreateDate()));
                        // set expiration date
                        tv_expiration_date.setText(String.format(getString(R.string.ann_expiration_date) + " : %s", travel.getExpirationDate()));
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
                        wv.loadDataWithBaseURL("", travel.getContent(), "text/html", "UTF-8", "");
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
        tv.setText(travel.getTitle());
        alertWithView(tv, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_travel_content_title:
                showTitleDialog();
                break;
        }
    }
}
