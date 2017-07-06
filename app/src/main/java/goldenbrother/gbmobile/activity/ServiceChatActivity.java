package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.fragment.ServiceFragment;
import goldenbrother.gbmobile.model.RoleInfo;

import java.util.ArrayList;
import java.util.List;

public class ServiceChatActivity extends CommonActivity implements View.OnClickListener {
    // ui
    private TextView tv_title;
    private ImageView iv_add_event;
    private ViewPager viewPager;
    // extra
    private int serviceGroupID;
    private String userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_chat);
        // extra
        serviceGroupID = getIntent().getIntExtra("serviceGroupID", -1);
        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        // ui reference
        tv_title = (TextView) findViewById(R.id.tv_service_chat_title);
        iv_add_event = (ImageView) findViewById(R.id.iv_service_chat_add_event);
        viewPager = (ViewPager) findViewById(R.id.vp_service_chat);
        // initTitle
        tv_title.setText(userName);
        // initImageView
        iv_add_event.setOnClickListener(this);
        iv_add_event.setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        // init viewpager
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(ServiceFragment.getInstance(serviceGroupID), getString(R.string.mobile_service_tab_chat));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_service_chat_add_event:
                Intent intent = new Intent();
                intent.setClass(this, AddEventActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                break;
        }
    }


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

}
