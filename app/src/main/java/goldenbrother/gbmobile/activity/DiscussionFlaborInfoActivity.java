package goldenbrother.gbmobile.activity;

import android.os.Bundle;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.Discussion;

public class DiscussionFlaborInfoActivity extends CommonActivity {

    // extra
    private Discussion discussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_flabor_info);

        // ui

        // extra
        discussion = getIntent().getExtras().getParcelable("discussion");

    }
}
