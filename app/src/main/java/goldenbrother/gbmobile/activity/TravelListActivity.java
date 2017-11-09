package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.TravelListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.Travel;

public class TravelListActivity extends CommonActivity {

    // ui
    private ImageView iv_banner;
    private RecyclerView rv;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean isBannerShowing = false;
    // data
    private ArrayList<Travel> list_travel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_list);
        setUpBackToolbar(R.id.toolbar, R.string.travel);

        // ui reference
//        iv_banner = findViewById(R.id.iv_travel_list_banner);
        rv = findViewById(R.id.rv_travel_list);

        // init
        list_travel = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new TravelListRVAdapter(this, list_travel));

//        initBanner();
        getTravelList();
    }

    private void getTravelList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getTravelList");
            j.put("dormID", LaborModel.getInstance().getDormID());
            j.put("nationCode", LaborModel.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetTravelList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetTravelList extends IAsyncTask {

        GetTravelList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getTravelList(response, list_travel);
                    if (result == ApiResultHelper.SUCCESS) {
                        rv.getAdapter().notifyDataSetChanged();
                    } else {
                        t(R.string.fail);
                    }
                    break;
                default:
                    finish();
            }
        }
    }

    public void onItemClick(Travel item) {
        Bundle b = new Bundle();
        b.putInt("travelID", item.getTravelID());
        openActivity(TravelContentActivity.class, b);
    }

    private void initBanner() {
        // init
        handler = new Handler();
        list_banner = new ArrayList<>();
        list_banner.add(R.drawable.banner_activity1);
        list_banner.add(R.drawable.banner_activity2);
        list_banner.add(R.drawable.banner_activity3);
        startShowAdvertising();
    }

    private static final long REFRESH_BANNER_TIME = 4000;
    private int indexOfBanner = 0;

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (isBannerShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
                Picasso.with(TravelListActivity.this).load(list_banner.get(indexOfBanner)).resize(w, h).centerCrop().into(iv_banner);
                indexOfBanner = indexOfBanner + 1 >= list_banner.size() ? 0 : indexOfBanner + 1;
                handler.postDelayed(r, REFRESH_BANNER_TIME);
            } else {
                indexOfBanner = 0;
            }
        }
    };

    private void stopShowAdvertising() {
        isBannerShowing = false;
        if (handler != null)
            handler.removeCallbacks(r);
    }

    private void startShowAdvertising() {
        if (list_banner != null && !list_banner.isEmpty() && handler != null && !isBannerShowing) {
            isBannerShowing = true;
            handler.post(r);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopShowAdvertising();
    }
}
