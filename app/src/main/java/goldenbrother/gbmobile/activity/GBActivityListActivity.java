package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Handler;
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
import goldenbrother.gbmobile.adapter.GBActivityListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.GBActivity;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class GBActivityListActivity extends CommonActivity implements View.OnClickListener {

    // gb activity type
    public static final int NEWS = 1;
    public static final int COM = 2;
    public static final int DORM = 3;
    // ui
    private ImageView iv_banner;
    private RecyclerView rv;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean isBannerShowing = false;
    // data
    private ArrayList<GBActivity> list_gb_activity, list_gb_activity_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gb_activity_list);
        setUpBackToolbar(R.id.toolbar, R.string.activity);

        // ui reference
        iv_banner = findViewById(R.id.iv_activity_list_banner);
        rv = findViewById(R.id.rv_activity_list);
        findViewById(R.id.ll_activity_list_news).setOnClickListener(this);
        findViewById(R.id.ll_activity_list_com).setOnClickListener(this);
        findViewById(R.id.ll_activity_list_dorm).setOnClickListener(this);

        // init
        list_gb_activity = new ArrayList<>();
        list_gb_activity_show = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new GBActivityListRVAdapter(this, list_gb_activity_show));

        initBanner();
        getActivityList();
    }

    private void getActivityList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getActivityList");
            if (RoleInfo.getInstance().isLabor()) {
                j.put("customerNo", LaborModel.getInstance().getCustomerNo());
            }
            j.put("nationCode", RoleInfo.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetActivityList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetActivityList extends IAsyncTask {

        GetActivityList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getActivityList(response, list_gb_activity);
                    if (result == ApiResultHelper.SUCCESS) {
                        list_gb_activity_show.clear();
                        list_gb_activity_show.addAll(list_gb_activity);
                        rv.getAdapter().notifyDataSetChanged();
                    } else {
                        t(R.string.no_announcement);
                    }
                    break;
                default:
                    finish();
            }
        }
    }

    public void onItemClick(GBActivity item) {
        Bundle b = new Bundle();
        b.putInt("activityID", item.getActivityID());
        openActivity(GBActivityContentActivity.class, b);
    }

    private void filter(int type) {
        list_gb_activity_show.clear();
        if (type == NEWS) {
            list_gb_activity_show.addAll(list_gb_activity);
        } else {
            for (GBActivity item : list_gb_activity) {
                if (item.getType() == type)
                    list_gb_activity_show.add(item);
            }
        }
        rv.getAdapter().notifyDataSetChanged();
    }

    private void initBanner() {
        // init
        handler = new Handler();
        list_banner = new ArrayList<>();
        list_banner.add(R.drawable.banner_activity1);
        list_banner.add(R.drawable.banner_activity2);
        list_banner.add(R.drawable.banner_activity3);
    }

    private static final long REFRESH_BANNER_TIME = 4000;
    private int indexOfBanner = 0;

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (isBannerShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
                Picasso.with(GBActivityListActivity.this)
                        .load(list_banner.get(indexOfBanner))
                        .resize(w, h)
                        .centerCrop()
                        .into(iv_banner);
                indexOfBanner = indexOfBanner + 1 >= list_banner.size() ? 0 : indexOfBanner + 1;
                handler.postDelayed(r, REFRESH_BANNER_TIME);
            } else {
                indexOfBanner = 0;
            }
        }
    };

    private void stopPlayBanner() {
        isBannerShowing = false;
        if (handler != null)
            handler.removeCallbacks(r);
    }

    private void startPlayBanner() {
        if (list_banner != null && !list_banner.isEmpty() && handler != null && !isBannerShowing) {
            isBannerShowing = true;
            handler.post(r);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlayBanner();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayBanner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_activity_list_news:
                filter(NEWS);
                break;
            case R.id.ll_activity_list_com:
                filter(COM);
                break;
            case R.id.ll_activity_list_dorm:
                filter(DORM);
                break;
        }
    }
}
