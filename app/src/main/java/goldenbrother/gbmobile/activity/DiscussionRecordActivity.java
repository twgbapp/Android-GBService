package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Discussion;
import goldenbrother.gbmobile.model.RoleInfo;

public class DiscussionRecordActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_SEARCH = 0;
    public static final int REQUEST_SIGNATURE = 1;
    public static final int REQUEST_FROM_GALLERY = 2;
    public static final int REQUEST_TAKE_PHOTO = 3;
    public static final int REQUEST_TAKE_CROP = 4;
    // ui
    private TextView tv_department, tv_name, tv_date;
    private EditText et_reason, et_place, et_description;
    private ImageView iv_service, iv_signature, iv_clicked;
    // take picture
    private Uri uriTakePicture;
    // extra
    private Discussion discussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_record);

        // ui reference
        tv_department = (TextView) findViewById(R.id.tv_discussion_record_department);
        tv_name = (TextView) findViewById(R.id.tv_discussion_record_name);
        tv_date = (TextView) findViewById(R.id.tv_discussion_record_date);
        et_reason = (EditText) findViewById(R.id.et_discussion_record_reason);
        et_place = (EditText) findViewById(R.id.et_discussion_record_place);
        et_description = (EditText) findViewById(R.id.et_discussion_record_description);
        iv_service = (ImageView) findViewById(R.id.iv_discussion_record_service_record);
        iv_signature = (ImageView) findViewById(R.id.iv_discussion_record_signature);
        findViewById(R.id.iv_discussion_record_profile).setOnClickListener(this);
        findViewById(R.id.tv_discussion_record_save).setOnClickListener(this);
        tv_department.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        iv_service.setOnClickListener(this);
        iv_signature.setOnClickListener(this);
        // extra
        discussion = getIntent().getExtras().getParcelable("discussion");
        if (discussion != null && discussion.getDrsNo() != 0) { // update
            tv_date.setText(discussion.getDiscussionDate());
            getDiscussionRecord(discussion.getDrsNo());
        } else { // create
            discussion = new Discussion();
            tv_date.setText(TimeHelper.getYMD());
        }
    }

    private void getDiscussionRecord(int drsno) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getDiscussionRecord");
            j.put("drsno", drsno);
            new GetDiscussionRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDiscussionRecord extends IAsyncTask {

        GetDiscussionRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getDiscussionRecord(response, discussion);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        tv_department.setText(discussion.getDepartment());
                        tv_name.setText(discussion.getFlaborName());
                        tv_date.setText(discussion.getDiscussionDate());
                        et_reason.setText(discussion.getDiscussionReason());
                        et_place.setText(discussion.getDiscussionPlace());
                        et_description.setText(discussion.getDiscussionDesc());
                        if (discussion.getServiceRecordPath() != null && !discussion.getServiceRecordPath().isEmpty())
                            Picasso.with(DiscussionRecordActivity.this).load(discussion.getServiceRecordPath()).into(iv_service);
                        if (discussion.getSignaturePath() != null && !discussion.getSignaturePath().isEmpty())
                            Picasso.with(DiscussionRecordActivity.this).load(discussion.getSignaturePath()).into(iv_signature);
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void addDiscussionRecord(String date, String reason, String place, String desc) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addDiscussionRecord");
            j.put("centerId", discussion.getCenterId());
            j.put("dormID", discussion.getDormId());
            j.put("customerNo", discussion.getCustomerNo());
            j.put("flaborNo", discussion.getfLaborNo());
            j.put("discussionDate", date);
            j.put("discussionReason", reason);
            j.put("discussionPlace", place);
            j.put("discussionDesc", desc);
            j.put("createId", RoleInfo.getInstance().getUserID());
            j.put("createDate", TimeHelper.date() + "T" + TimeHelper.time());
            j.put("serviceRecord", discussion.getServiceRecordPath());
            j.put("signature", discussion.getSignaturePath());

            new AddDiscussionRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddDiscussionRecord extends IAsyncTask {

        AddDiscussionRecord(Context context, JSONObject json, String url) {
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
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updateDiscussionRecord(String date, String reason, String place, String desc) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "updateDiscussionRecord");
            j.put("drsno", discussion.getDrsNo());
            j.put("discussionDate", date);
            j.put("discussionReason", reason);
            j.put("discussionPlace", place);
            j.put("discussionDesc", desc);
            j.put("serviceRecord", discussion.getServiceRecordPath());
            j.put("signature", discussion.getSignaturePath());

            new UpdateDiscussionRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdateDiscussionRecord extends IAsyncTask {

        UpdateDiscussionRecord(Context context, JSONObject json, String url) {
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
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            j.put("fileName", UUID.randomUUID().toString());
            j.put("url", URLHelper.HOST);
            j.put("baseStr", BitmapHelper.bitmap2JPGBase64(bmp));
            new UploadImageTask(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageTask extends IAsyncTask {
        private HashMap<String, String> map;

        UploadImageTask(Context context, JSONObject json, String url) {
            super(context, json, url);
            map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.uploadPicture(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        savePath(iv_clicked, map.get("path"));
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void savePath(View v, String path) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.iv_discussion_record_service_record:
                discussion.setServiceRecordPath(path);
                Picasso.with(this).load(path).into(iv_service);
                break;
            case R.id.iv_discussion_record_signature:
                discussion.setSignaturePath(path);
                Picasso.with(this).load(path).into(iv_signature);
                break;
        }
    }


    private void showDatePicker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        final Calendar c_result = Calendar.getInstance();
        c.setTime(TimeHelper.getYMD2Date(tv.getText().toString()));

        DatePicker datePicker = new DatePicker(this);
        datePicker.init(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c_result.set(year, monthOfYear, dayOfMonth);
                    }
                });


        alertWithView(datePicker, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(TimeHelper.getDate2TMD(c_result.getTime()));
            }
        }, null);
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
                    uriTakePicture = FileProvider.getUriForFile(DiscussionRecordActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(DiscussionRecordActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadPicture(BitmapHelper.resize(bmp, 300, 300));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        b.putParcelable("discussion", discussion);
        switch (v.getId()) {
            case R.id.tv_discussion_record_date:
                showDatePicker(tv_date);
                break;
            case R.id.tv_discussion_record_department:
            case R.id.tv_discussion_record_name:
            case R.id.iv_discussion_record_profile:
                b.putBoolean("isFLabor", true);
                openActivityForResult(SearchActivity.class, REQUEST_SEARCH, b);
                break;
            case R.id.iv_discussion_record_service_record:
                iv_clicked = (ImageView) v;
                choosePicture();
                break;
            case R.id.iv_discussion_record_signature:
                iv_clicked = (ImageView) v;
                openActivityForResult(SignatureActivity.class, REQUEST_SIGNATURE);
                break;
            case R.id.tv_discussion_record_save:
                String date = tv_date.getText().toString();
                String reason = et_reason.getText().toString();
                String place = et_place.getText().toString();
                String description = et_description.getText().toString();

                if (date.isEmpty() || reason.isEmpty() || place.isEmpty() || description.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                if (discussion.getDrsNo() == 0) {
                    addDiscussionRecord(date, reason, place, description);
                } else {
                    updateDiscussionRecord(date, reason, place, description);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bundle b = new Bundle();
        switch (requestCode) {
            case REQUEST_SEARCH:
                discussion.setCenterId(data.getStringExtra("centerId"));
                discussion.setDormId(data.getStringExtra("dormId"));
                discussion.setCustomerNo(data.getStringExtra("customerNo"));
                discussion.setfLaborNo(data.getStringExtra("flaborNo"));
                tv_name.setText(data.getStringExtra("flaborName"));
                break;
            case REQUEST_SIGNATURE:
                Bitmap bitmap = BitmapHelper.byteArrayToBitmap(data.getByteArrayExtra("bitmap"));
                showImage(bitmap);
                break;
            case REQUEST_FROM_GALLERY:
                b.putString("uri", data.getData().toString());
                b.putInt("ratioX", 0);
                b.putInt("ratioY", 0);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_PHOTO:
                b.putString("uri", uriTakePicture.toString());
                b.putInt("ratioX", 0);
                b.putInt("ratioY", 0);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_CROP:
                showImage(BitmapHelper.file2Bitmap((File) data.getSerializableExtra("file")));
                break;
        }
    }
}
