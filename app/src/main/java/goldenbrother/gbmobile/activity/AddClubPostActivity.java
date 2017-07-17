package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.adapter.AddClubPostMediaRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ThumbNailHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AddClubPostActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_FROM_GALLERY = 11;
    public static final int REQUEST_TAKE_PHOTO = 12;
    // ui
    private EditText et_content;
    private RecyclerView rv_media;
    // extra
    private int clubID;
    // data
    private ArrayList<ClubPostMediaModel> list_media;
    // take picture
    private Uri uriTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club_post);
        // ui reference
        et_content = (EditText) findViewById(R.id.et_add_club_post_content);
        rv_media = (RecyclerView) findViewById(R.id.rv_add_club_post_media);
        // listener
        findViewById(R.id.iv_add_club_post_done).setOnClickListener(this);
        findViewById(R.id.iv_add_club_post_input_video_path).setOnClickListener(this);
        findViewById(R.id.iv_add_club_post_choose_image).setOnClickListener(this);
        // get extra
        Intent intent = getIntent();
        clubID = intent.getIntExtra("clubID", -1);
        initMediaView();
    }

    private void initMediaView() {
        // init recycler view
        rv_media.setMinimumHeight(getResources().getDisplayMetrics().widthPixels / 3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_media.setLayoutManager(layoutManager);
        list_media = new ArrayList<>();
        rv_media.setAdapter(new AddClubPostMediaRVAdapter(this, list_media));
    }

    private void updateAdapter() {
        AddClubPostMediaRVAdapter adapter = (AddClubPostMediaRVAdapter) rv_media.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void addClubPost(String content) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addClubPost");
            j.put("clubID", clubID);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("content", content);
            j.put("url", URLHelper.HOST);
            JSONArray arr = new JSONArray();
            for (ClubPostMediaModel m : list_media) {
                JSONObject o = new JSONObject();
                o.put("type", m.getType());
                o.put("position", m.getPosition());
                switch (m.getType()) {
                    case ClubPostMediaModel.IMAGE:
                        o.put("pic", m.getPic());
                        o.put("name", m.getName());
                        break;
                    case ClubPostMediaModel.VIDEO:
                        o.put("thumbNailPath", m.getThumbNailPath());
                        o.put("urlPath", m.getUrlPath());
                        break;
                }
                arr.put(o);
            }
            j.put("media", arr.toString());
            j.put("logStatus", true);
            new AddClubPost(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddClubPost extends IAsyncTask {

        AddClubPost(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.addClubPost(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void chooseImage() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(R.array.choose_picture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_FROM_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriTakePicture = FileProvider.getUriForFile(AddClubPostActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getAppDir(AddClubPostActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    private void inputVideoPath() {
        final EditText et = new EditText(this);
        alertWithView(et, getString(R.string.club_video), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String urlPath = et.getText().toString();
                String thumbNailPath = ThumbNailHelper.getThumbNail(urlPath);
                if (thumbNailPath != null) {
                    ClubPostMediaModel m = new ClubPostMediaModel();
                    m.setType(ClubPostMediaModel.VIDEO);
                    m.setThumbNailPath(thumbNailPath);
                    m.setUrlPath(urlPath);
                    m.setPosition(list_media.size());
                    list_media.add(m);
                    updateAdapter();
                } else {
                    t(R.string.club_support_url);
                }
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_club_post_done:
                String content = et_content.getText().toString();
                if (!content.isEmpty())
                    addClubPost(et_content.getText().toString());
                break;
            case R.id.iv_add_club_post_choose_image:
                chooseImage();
                break;
            case R.id.iv_add_club_post_input_video_path:
                inputVideoPath();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_FROM_GALLERY:
                Uri uriChoosePhoto = data.getData();
                CropImage.activity(uriChoosePhoto)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
            case REQUEST_TAKE_PHOTO:
                CropImage.activity(uriTakePicture)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    Uri uri = CropImage.getActivityResult(data).getUri();
                    ClubPostMediaModel m = new ClubPostMediaModel();
                    m.setType(ClubPostMediaModel.IMAGE);
                    m.setUri(uri);
                    m.setPic(BitmapHelper.bitmap2String(BitmapHelper.resize(BitmapHelper.uri2Bitmap(this, uri), BitmapHelper.MAX_WIDTH, BitmapHelper.MAX_HEIGHT)));
                    m.setName(BitmapHelper.getRandomName());
                    list_media.add(m);
                    updateAdapter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}
