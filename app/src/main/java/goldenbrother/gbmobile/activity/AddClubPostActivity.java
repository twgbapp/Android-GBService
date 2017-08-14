package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.theartofdev.edmodo.cropper.CropImage;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.GenericFileProvider;
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
import java.util.UUID;


public class AddClubPostActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
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
                    int result = ApiResultHelper.commonCreate(response);
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

    private void choosePicture() {
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
                    uriTakePicture = FileProvider.getUriForFile(AddClubPostActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(AddClubPostActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    private void inputVideoPath() {
        final EditText et = new EditText(this);
        et.setHint(R.string.club_support_url);
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
                if (content.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                addClubPost(et_content.getText().toString());
                break;
            case R.id.iv_add_club_post_choose_image:
                choosePicture();
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
        Bundle b = new Bundle();
        switch (requestCode) {
            case REQUEST_FROM_GALLERY:
                b.putString("uri", data.getData().toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_PHOTO:
                b.putString("uri", uriTakePicture.toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_CROP:
                File file = new File(data.getStringExtra("path"));
                ClubPostMediaModel m = new ClubPostMediaModel();
                m.setType(ClubPostMediaModel.IMAGE);
                m.setFile(file);
                m.setPic(BitmapHelper.bitmap2JPGBase64(BitmapHelper.resize(BitmapHelper.file2Bitmap(file))));
                m.setName(UUID.randomUUID().toString());
                list_media.add(m);
                updateAdapter();
                break;
        }
    }
}
