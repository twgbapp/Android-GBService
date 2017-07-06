package goldenbrother.gbmobile.activity;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ClubPostMediaPagerAdapter;
import goldenbrother.gbmobile.model.ClubPostMediaModel;

import java.util.ArrayList;

public class ClubPostMediaActivity extends CommonActivity {

    // ui
    private TextView tv_count;
    private ViewPager vp;
    // extra
    private ArrayList<ClubPostMediaModel> list_media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_post_media);
        // ui reference
        tv_count = (TextView) findViewById(R.id.tv_club_post_media_count);
        vp = (ViewPager) findViewById(R.id.vp_club_post_media);
        // extra
        list_media = getIntent().getParcelableArrayListExtra("media");
        // init Count
        tv_count.setText(String.format("%d/%d", 1, list_media.size()));
        // init ViewPager
        vp.setAdapter(new ClubPostMediaPagerAdapter(this, list_media));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_count.setText(String.format("%d/%d", position + 1, list_media.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 19) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
