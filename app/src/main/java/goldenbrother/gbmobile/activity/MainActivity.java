package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MainDrawerRVAdapter;
import goldenbrother.gbmobile.model.DrawerItem;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

import java.util.ArrayList;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_PROFILE = 0;
    public static final int REQUEST_QUICK_REPAIR = 1;
    // ui
    private RecyclerView rv_drawer;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean allowShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ui reference
        findViewById(R.id.cv_main_mobile_service).setOnClickListener(this);
        findViewById(R.id.cv_main_life_information).setOnClickListener(this);
        findViewById(R.id.cv_main_e_commerce).setOnClickListener(this);
        findViewById(R.id.cv_main_satisfaction_survey).setOnClickListener(this);
        // init Toolbar
        initToolbar();
        // init Drawer
        initDrawer();
        // initBanner
        initBanner();
    }

    private void initToolbar() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // drawer connect
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initDrawer() {
        rv_drawer = (RecyclerView) findViewById(R.id.rv_main_navigation);
        rv_drawer.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<DrawerItem> list = new ArrayList<>();
        RoleInfo r = RoleInfo.getInstance();
        if (r.isLabor()) {
            list.add(new DrawerItem(R.drawable.ic_mobile_service, R.string.mobile_service, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_quick_repair, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_club, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_life_information, R.string.main_drawer_life_information, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_event_list, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_announcement, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_e_commerce, R.string.main_drawer_e_commerce, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_satisfaction_survey, R.string.main_drawer_satisfaction_survey, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_exit, R.string.main_drawer_logout, DrawerItem.GROUP));
        } else {
            list.add(new DrawerItem(R.drawable.ic_mobile_service, R.string.mobile_service, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_club, R.string.main_drawer_club, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_online_setting, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_package, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_repair_record, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_medical, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_chart, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_life_information, R.string.main_drawer_life_information, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_event_list, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_e_commerce, R.string.main_drawer_e_commerce, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_exit, R.string.main_drawer_logout, DrawerItem.GROUP));
        }
        rv_drawer.setAdapter(new MainDrawerRVAdapter(this, list));
    }

    public void onDrawerItemClick(int strId) {
        Bundle b = new Bundle();
        switch (strId) {
            case R.string.main_drawer_event_list:
                b.putInt("position", 1);
                openActivity(MobileServiceActivity.class, b);
                break;
            case R.string.main_drawer_quick_repair:
                openActivityForResult(QuickRepairActivity.class, REQUEST_QUICK_REPAIR);
                break;
            case R.string.main_drawer_satisfaction_survey:
                openActivity(SatisfactionIssueActivity.class);
                break;
            case R.string.main_drawer_club:
                openActivity(ClubListActivity.class);
                break;
            case R.string.main_drawer_announcement:
                b.putInt("type", 0);
                b.putString("customerNo", LaborModel.getInstance().getCustomerNo());
                b.putString("flaborNo", LaborModel.getInstance().getFlaborNo());
                b.putString("nationCode", LaborModel.getInstance().getUserNationCode());
                openActivity(AnnouncementListActivity.class, b);
                break;
            case R.string.main_drawer_logout:
                b.putBoolean("isLogout", true);
                openActivity(SplashActivity.class, b);
                finish();
                break;
            case R.string.main_drawer_online_setting:
                openActivity(OnLineSettingActivity.class);
                break;
            case R.string.main_drawer_package:
                openActivity(PackageListActivity.class);
                break;
            case R.string.main_drawer_chart:
                openActivity(ChartActivity.class);
                break;
            case R.string.main_drawer_repair_record:
                openActivity(RepairRecordActivity.class);
                break;
            case R.string.main_drawer_medical:
                openActivity(MedicalListActivity.class);
                break;
        }
        closeDrawer();
    }

    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initBanner() {
        // init Data
        list_banner = new ArrayList<>();
        list_banner.add(R.drawable.banner1);
        list_banner.add(R.drawable.banner2);
        list_banner.add(R.drawable.banner3);
        // show
        allowShowing = true;
        handler = new Handler();
        handler.post(r);
    }

    private static final long REFRESH_BANNER_TIME = 4000;
    private int indexOfBanner = 0;

    final Runnable r = new Runnable() {
        @Override
        public void run() {
//            if (allowShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
//                int w = getResources().getDisplayMetrics().widthPixels;
//                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
////                Picasso.with(MainActivity.this).load(list_banner.get(indexOfBanner)).resize(w, h).centerCrop().into(iv_banner);
//                indexOfBanner = indexOfBanner + 1 >= list_banner.size() ? 0 : indexOfBanner + 1;
//                handler.postDelayed(r, REFRESH_BANNER_TIME);
//            } else {
//                indexOfBanner = 0;
//            }
        }
    };

    private void stopShowAdvertising() {
        allowShowing = false;
    }

    private void startShowAdvertising() {
        if (list_banner != null && !list_banner.isEmpty() && handler != null) {
            allowShowing = true;
            handler.post(r);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startShowAdvertising();
    }

    @Override
    public void onPause() {
        stopShowAdvertising();
        if (handler != null)
            handler.removeCallbacks(r);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_main_mobile_service:
                Bundle b = new Bundle();
                b.putInt("position", 0);
                openActivity(MobileServiceActivity.class, b);
                break;
            case R.id.cv_main_life_information:
                t(R.string.main_drawer_life_information);
                break;
            case R.id.cv_main_e_commerce:
                t(R.string.main_drawer_e_commerce);
                break;
            case R.id.cv_main_satisfaction_survey:
                t(R.string.main_drawer_satisfaction_survey);
                break;
        }
    }

    public void openProfileActivity() {
        closeDrawer();
        openActivityForResult(ProfileActivity.class, REQUEST_PROFILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_PROFILE:
                rv_drawer.getAdapter().notifyItemChanged(0);
                break;
        }

    }

    public static final long DELAY_TIME = 2000L;
    private long lastBackPressTime = 0;


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - lastBackPressTime < DELAY_TIME) {
                super.onBackPressed();
            } else {
                lastBackPressTime = System.currentTimeMillis();
                t(R.string.press_again_to_exit);
            }

        }
    }

}
