package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.AnnouncementListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnnouncementListActivity extends CommonActivity implements View.OnClickListener {

    // announcement type
    public static final int DORM = 1;
    public static final int COM = 2;
    public static final int GOV = 3;
    // ui
    private ImageView iv_banner;
    private RecyclerView rv;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean isBannerShowing = false;
    // data
    private ArrayList<AnnouncementModel> list_announcement, list_announcement_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_list);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_announcement);

        // ui reference
        iv_banner = findViewById(R.id.iv_announcement_list_banner);
        rv = findViewById(R.id.rv_announcement_list);
        findViewById(R.id.ll_announcement_list_com).setOnClickListener(this);
        findViewById(R.id.ll_announcement_list_gov).setOnClickListener(this);
        findViewById(R.id.ll_announcement_list_dorm).setOnClickListener(this);

        // init
        list_announcement = new ArrayList<>();
        list_announcement_show = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AnnouncementListRVAdapter(this, list_announcement_show));

        initBanner();
        loadAnnouncementList();
    }

    private void loadAnnouncementList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getAnnouncementList");
            j.put("type", 0);
//            j.put("customerNo", RoleInfo.getInstance().isLabor()?LaborModel.getInstance().getCustomerNo():"");
//            j.put("flaborNo", RoleInfo.getInstance().isLabor()?LaborModel.getInstance().getFlaborNo():"");
//            j.put("nationCode", RoleInfo.getInstance().getUserNationCode());
            j.put("customerNo", "F04135");
            j.put("flaborNo", "N9998");
            j.put("nationCode", "024");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new LoadAnnouncementList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadAnnouncementList extends IAsyncTask {

        LoadAnnouncementList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadAnnouncementList(response, list_announcement);
                    if (result == ApiResultHelper.SUCCESS) {
                        list_announcement_show.clear();
                        list_announcement_show.addAll(list_announcement);
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

    public void onItemClick(AnnouncementModel item) {
        Bundle b = new Bundle();
        b.putInt("announcementID", item.getAnnouncementID());
        openActivity(AnnouncementContentActivity.class, b);
    }

    private void filter(int type) {
        list_announcement_show.clear();
        for (AnnouncementModel item : list_announcement) {
            if (item.getType() == type)
                list_announcement_show.add(item);
        }
        rv.getAdapter().notifyDataSetChanged();
    }

    private void initBanner() {
        // init
        handler = new Handler();
        list_banner = new ArrayList<>();
        list_banner.add(R.drawable.banner_announcement1);
    }

    private static final long REFRESH_BANNER_TIME = 4000;
    private int indexOfBanner = 0;

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (isBannerShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
                Picasso.with(AnnouncementListActivity.this).load(list_banner.get(indexOfBanner)).resize(w, h).centerCrop().into(iv_banner);
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
            case R.id.ll_announcement_list_com:
                filter(COM);
                break;
            case R.id.ll_announcement_list_gov:
                filter(GOV);
                break;
            case R.id.ll_announcement_list_dorm:
                filter(DORM);
                break;
        }
    }
}
