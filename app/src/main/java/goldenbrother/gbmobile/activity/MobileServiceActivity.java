package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.fragment.ServiceFragment;
import goldenbrother.gbmobile.fragment.EventListFragment;
import goldenbrother.gbmobile.fragment.ServiceListFragment;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;

import java.util.ArrayList;
import java.util.List;

public class MobileServiceActivity extends CommonActivity implements View.OnClickListener {
    // request
    public static final int REQUEST_SERVICE_CHAT = 11;
    // ui
    private View iv_search, iv_close;
    private TextView tv_title;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_service);
        setUpBackToolbar(R.id.toolbar_service, R.id.tv_mobile_service_title, R.string.mobile_service);
        // ui reference
        iv_search = findViewById(R.id.tv_mobile_service_search);
        iv_close = findViewById(R.id.tv_mobile_service_close);
        tv_title = (TextView) findViewById(R.id.tv_mobile_service_title);
        viewPager = (ViewPager) findViewById(R.id.vp_service);
        // listener
        iv_search.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        // visibility of search icon
        iv_search.setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        // init viewpager
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        if (RoleInfo.getInstance().isLabor()) {
            pagerAdapter.addFragment(ServiceFragment.getInstance(LaborModel.getInstance().getServiceGroupID()), getString(R.string.mobile_service_tab_chat));
        } else {
            pagerAdapter.addFragment(ServiceListFragment.createInstance(), getString(R.string.mobile_service_tab_chat));
        }
        pagerAdapter.addFragment(EventListFragment.createInstance(), getString(R.string.mobile_service_tab_event));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == POSITION_GROUP) {
//                    tv_title.setText(RoleInfo.getInstance().isLabor() ? R.string.mobile_service : R.string.mobile_service);
                    tv_title.setText(R.string.mobile_service);
                } else if (position == POSITION_EVENT) {
                    tv_title.setText(R.string.event_list);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Message msg = new Message();
        msg.what = getIntent().getExtras().getInt("position", 0);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(msg.what, true);
        }
    };

    public void onAddEventSuccess(int ServiceEventID) {
        // set viewpager
        viewPager.setCurrentItem(POSITION_EVENT, true);
        // load cloud event list
        ((EventListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(POSITION_EVENT)).loadCloudEventList(ServiceEventID);
    }

    public void changeSearchTitle(String title) {
        // change icon
        iv_close.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.GONE);
        // change title
        tv_title.setText(String.format(getString(R.string.search) + " : %s", title));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mobile_service_search:
                switch (viewPager.getCurrentItem()) {
                    case POSITION_GROUP:
//                        ((ServiceListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(POSITION_GROUP)).showSearchDialog();
                        break;
                    case POSITION_EVENT:
                        ((EventListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(POSITION_EVENT)).showSearchDialog();
                        break;
                }
                break;
            case R.id.tv_mobile_service_close:
                // change icon
                iv_close.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
                // get current page
                int position = viewPager.getCurrentItem();
                if (position == POSITION_GROUP) {
                    tv_title.setText(R.string.mobile_service);
//                    ((ServiceListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(POSITION_GROUP)).clearSearchFilter();
                } else if (position == POSITION_EVENT) {
                    tv_title.setText(R.string.event_list);
                    ((EventListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(POSITION_EVENT)).clearSearchFilter();
                }

                break;
        }
    }

    private static final int POSITION_GROUP = 0;
    private static final int POSITION_EVENT = 1;


    private static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SERVICE_CHAT) {
                if (RoleInfo.getInstance().isLabor()) return;
                int serviceGroupID = data.getIntExtra("serviceGroupID", -1);
                String lastChat = data.getStringExtra("lastChat");
                if (serviceGroupID != -1 && lastChat != null && !lastChat.isEmpty()) {
                    ((ServiceListFragment) ((PagerAdapter) viewPager.getAdapter()).getItem(0)).updateChat(serviceGroupID, lastChat);
                }
            }
        }
    }
}
