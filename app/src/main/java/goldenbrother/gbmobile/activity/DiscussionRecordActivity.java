package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.Discussion;

public class DiscussionRecordActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_PROFILE = 0;
    // ui

    // extra
    private Discussion discussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_record);

        // ui reference
        findViewById(R.id.iv_discussion_record_profile).setOnClickListener(this);

        // extra
        discussion = getIntent().getExtras().getParcelable("discussion");
        if (discussion != null && discussion.getDrsNo() != 0) { // update

        } else { // create
            discussion = new Discussion();

        }
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        b.putParcelable("discussion", discussion);
        switch (v.getId()) {
            case R.id.iv_discussion_record_profile:
                openActivityForResult(DiscussionFlaborInfoActivity.class, REQUEST_PROFILE, b);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        discussion = data.getParcelableExtra("discussion");
        switch (requestCode) {
            case REQUEST_PROFILE:

                break;
        }
    }
}
