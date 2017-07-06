package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends CommonActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    // request
    public static final int REQUEST_PROFILE = 0;
    public static final int REQUEST_QUICK_REPAIR = 1;
    // ui
    private ImageView iv_banner;
    private CircleImageView iv_user_picture;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean allowShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ui reference
        iv_banner = (ImageView) findViewById(R.id.iv_main_banner);
        findViewById(R.id.iv_main_service).setOnClickListener(this);
        // init Navigation
        initDrawer();
        // initBanner
        initBanner();
    }

    private void initDrawer() {
        // initToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // connect ToolBar & Navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // init Navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
        if (navigationView != null) {
            // get role instances
            RoleInfo r = RoleInfo.getInstance();
            if (r.isLabor()) {
                navigationView.inflateMenu(R.menu.main_drawer_labor);
            } else {
                navigationView.inflateMenu(R.menu.main_drawer_manager);
            }

            navigationView.setNavigationItemSelectedListener(this);
            View nav = navigationView.getHeaderView(0);
            // init Navigation Header
            LinearLayout ll_header = (LinearLayout) nav.findViewById(R.id.ll_main_nav_header);
            iv_user_picture = (CircleImageView) nav.findViewById(R.id.iv_main_user_picture);
            TextView tv_user_name = (TextView) nav.findViewById(R.id.tv_main_user_name);
            TextView tv_user_email = (TextView) nav.findViewById(R.id.tv_main_user_email);
            // set listener
            ll_header.setOnClickListener(this);
            // set picture
            if (r.getUserPicture() != null && r.getUserPicture().length() > 0) {
                int w = (int) getResources().getDimension(R.dimen.imageview_navigation_picture_width);
                Picasso.with(this).load(r.getUserPicture()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(w, w).centerCrop().into(iv_user_picture);
            } else {
                iv_user_picture.setImageResource(R.drawable.ic_person_white);
            }
            // set name
            tv_user_name.setText(r.getUserName());
            // set email
            tv_user_email.setText(r.getUserEmail());
        }
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
            if (allowShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
                Picasso.with(MainActivity.this).load(list_banner.get(indexOfBanner)).resize(w, h).centerCrop().into(iv_banner);
                indexOfBanner = indexOfBanner + 1 >= list_banner.size() ? 0 : indexOfBanner + 1;
                handler.postDelayed(r, REFRESH_BANNER_TIME);
            } else {
                indexOfBanner = 0;
            }
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
            case R.id.iv_main_service:
                Bundle b = new Bundle();
                b.putInt("position", 0);
                openActivity(MobileServiceActivity.class, b);
                break;
            case R.id.ll_main_nav_header:
                openActivityForResult(ProfileActivity.class, REQUEST_PROFILE);
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle b = new Bundle();

        switch (item.getItemId()) {
            case R.id.nav_labor_event_list:
            case R.id.nav_manager_event_list:
                b.putInt("position", 1);
                openActivity(MobileServiceActivity.class, b);
                break;
            case R.id.nav_labor_quick_repair:
                openActivityForResult(QuickRepairActivity.class, REQUEST_QUICK_REPAIR);
                break;
            case R.id.nav_labor_satisfaction_survey:
                openActivity(SatisfactionIssueActivity.class);
                break;
            case R.id.nav_labor_club:
            case R.id.nav_manager_club:
                openActivity(ClubListActivity.class);
                break;
            case R.id.nav_labor_announcement:
                b.putInt("type", 0);
                b.putString("customerNo", LaborModel.getInstance().getCustomerNo());
                b.putString("flaborNo", LaborModel.getInstance().getFlaborNo());
                b.putString("nationCode", LaborModel.getInstance().getUserNationCode());
                openActivity(AnnouncementListActivity.class, b);
                break;
            case R.id.nav_labor_logout:
            case R.id.nav_manager_logout:
                b.putBoolean("isLogout", true);
                openActivity(LoginActivity.class, b);
                finish();
                break;
            case R.id.nav_manager_on_line_setting:
                openActivity(OnLineSettingActivity.class);
                break;
            case R.id.nav_manager_package:
                openActivity(PackageListActivity.class);
                break;
            case R.id.nav_manager_chart:
                openActivity(ChartActivity.class);
                break;
            case R.id.nav_manager_repair_record:
                openActivity(RepairRecordActivity.class);
                break;
            case R.id.nav_manager_record:
                openActivity(MedicalListActivity.class);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PROFILE:
                    // get role instances
                    RoleInfo r = RoleInfo.getInstance();
                    if (r.getUserPicture() != null && !r.getUserPicture().isEmpty()) {
                        int w = (int) getResources().getDimension(R.dimen.imageview_navigation_picture_width);
                        Picasso.with(this).load(r.getUserPicture()).placeholder(R.drawable.ic_person_white).memoryPolicy(MemoryPolicy.NO_CACHE).resize(w, w).centerCrop().into(iv_user_picture);
                    } else {
                        Picasso.with(this).load(R.drawable.ic_person_white).into(iv_user_picture);
                    }
                    break;
            }
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
