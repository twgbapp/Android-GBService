package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ClubPostMessageRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddClubPostMessageActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private ImageView iv_user_picture, iv_thumbnail_pic, iv_video_play;
    private TextView tv_user_name, tv_create_date, tv_content;
    private RecyclerView rv;
    private EditText et_message;
    // extra
    private ClubPostModel clubPost;
    // data
    private ArrayList<ClubPostMessageModel> list_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club_post_message);
        setUpBackToolbar(R.id.toolbar, R.string.comment);
        // ui reference
        iv_user_picture = (ImageView) findViewById(R.id.iv_add_club_post_message_user_picture);
        iv_thumbnail_pic = (ImageView) findViewById(R.id.iv_add_club_post_message_thumbnail_pic);
        iv_video_play = (ImageView) findViewById(R.id.iv_add_club_post_message_video_play);
        tv_user_name = (TextView) findViewById(R.id.tv_add_club_post_message_user_name);
        tv_create_date = (TextView) findViewById(R.id.tv_add_club_post_message_create_date);
        tv_content = (TextView) findViewById(R.id.tv_add_club_post_message_content);
        rv = (RecyclerView) findViewById(R.id.rv_add_club_post_message_comment);
        et_message = (EditText) findViewById(R.id.et_add_club_post_message_message);
        findViewById(R.id.iv_add_club_post_message_send).setOnClickListener(this);
        // init ListView
        list_message = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ClubPostMessageRVAdapter(this, list_message));
        // get extra
        Intent intent = getIntent();
        clubPost = intent.getParcelableExtra("clubPost");
        showClub();
        // load message
        loadClubPostMessage();
    }

    private void showClub() {
        int widthPixel = getResources().getDisplayMetrics().widthPixels;
        int heightPixel = (int) (getResources().getDisplayMetrics().density * 200);
        // set user picture
        if (clubPost.getPostUserPicture() != null && !clubPost.getPostUserPicture().isEmpty()) {
            int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
            Picasso.with(this).load(clubPost.getPostUserPicture()).resize(w, w).centerCrop().into(iv_user_picture);
        }
        // set user name
        tv_user_name.setText(clubPost.getPostUserName());
        // set create date
        tv_create_date.setText(clubPost.getCreateDate());
        // set content
        tv_content.setText(clubPost.getContent());
        // set media
        final ArrayList<ClubPostMediaModel> list_media = clubPost.getMedias();
        if (!list_media.isEmpty() && list_media.get(0).getThumbNailPath() != null && !list_media.get(0).getThumbNailPath().isEmpty()) {
            final ClubPostMediaModel m = list_media.get(0);
            final int type = m.getType();
            // show thumbnail pic
            iv_thumbnail_pic.setVisibility(View.VISIBLE);
            // is video ?
            iv_video_play.setVisibility(type == ClubPostMediaModel.VIDEO ? View.VISIBLE : View.GONE);
            Picasso.with(this).load(m.getThumbNailPath()).resize(widthPixel, heightPixel).centerCrop().into(iv_thumbnail_pic);
            iv_thumbnail_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = m.getUrlPath();
                    if (type == ClubPostMediaModel.VIDEO) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } else if (type == ClubPostMediaModel.IMAGE) {
                        Intent intent = new Intent();
                        intent.setClass(AddClubPostMessageActivity.this, ClubPostMediaActivity.class);
                        intent.putParcelableArrayListExtra("media", list_media);
                        startActivity(intent);
                    }
                }
            });
        } else {
            iv_video_play.setVisibility(View.GONE);
            iv_thumbnail_pic.setVisibility(View.GONE);
        }
    }

    public void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    private boolean sending = false;

    public void AddClubPostMessage(String message) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addClubPostMessage");
            j.put("clubPostID", clubPost.getClubPostID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("message", message);
            j.put("logStatus", true);
            if (!sending) {
                sending = true;
                new AddClubPostMessage(this, j, URLHelper.HOST).execute();
            } else {
                t(R.string.sending);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddClubPostMessage extends IAsyncTask {

        AddClubPostMessage(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            sending = false;
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        et_message.setText("");
                        loadClubPostMessage();
                    }
                    break;
            }
        }
    }

    public void loadClubPostMessage() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getClubPostMessage");
            j.put("clubPostID", clubPost.getClubPostID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadClubPostMessage(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadClubPostMessage extends IAsyncTask {

        LoadClubPostMessage(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadClubPostMessage(response, list_message);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                        rv.scrollToPosition(list_message.size() - 1);
                    } else {

                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_club_post_message_send:
                String msg = et_message.getText().toString();
                if (!msg.isEmpty()) {
                    AddClubPostMessage(msg);
                }
                break;
        }
    }
}