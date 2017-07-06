package goldenbrother.gbmobile.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

public class AddMedicalFileUploadActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_CHOOSE_PHOTO = 11;
    public static final int REQUEST_TAKE_PHOTO = 12;
    public static final int REQUEST_CROP_PHOTO = 13;
    // ui
    private ImageView iv_1, iv_2, iv_3, iv_4;
    private ImageView iv_clicked;
    // extra
    private int MTRSNo;
    // take picture
    private File file;
    // data
    private String path1, path2, path3, path4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_file_upload);

        // ui reference
        findViewById(R.id.iv_add_medical_file_upload_done).setOnClickListener(this);
        iv_1 = (ImageView) findViewById(R.id.iv_add_medical_file_upload_picture_1);
        iv_2 = (ImageView) findViewById(R.id.iv_add_medical_file_upload_picture_2);
        iv_3 = (ImageView) findViewById(R.id.iv_add_medical_file_upload_picture_3);
        iv_4 = (ImageView) findViewById(R.id.iv_add_medical_file_upload_picture_4);
        iv_1.setOnClickListener(this);
        iv_2.setOnClickListener(this);
        iv_3.setOnClickListener(this);
        iv_4.setOnClickListener(this);
        // extra
        MTRSNo = getIntent().getExtras().getInt("MTRSNo");
    }


    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadPicture(BitmapHelper.getLimitBitmap(bmp, 300, 300));
            }
        },null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", BitmapHelper.getRandomName());
            j.put("url", URLHelper.HOST);
            j.put("baseStr", BitmapHelper.getStringImage(bmp));
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
                        TextView tv = new TextView(AddMedicalFileUploadActivity.this);
                        tv.setText(map.get("path"));
                        alertWithView(tv,null,null);
//                        map.get("path")
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updatePicture(String path) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "updatePicture");
            j.put("userID", RoleInfo.getInstance().getUserID());
            //j.put("DiagnosticCertificate", "DiagnosticCertificate");
            j.put("path", path);
            new UpdatePicture(this, j, URLHelper.HOST, path).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdatePicture extends IAsyncTask {

        private String path;

        UpdatePicture(Context context, JSONObject json, String url, String path) {
            super(context, json, url);
            this.path = path;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.updatePicture(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (iv_clicked != null) {
                            // update ImageView
                            int w = (int) getResources().getDimension(R.dimen.imageview_navigation_picture_width);
                            Picasso.with(AddMedicalFileUploadActivity.this).load(path).resize(w, w).centerCrop().into(iv_clicked);
                            // set path
                            savePath(iv_clicked,path);
                        }
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void savePath(View v, String path) {
        switch (v.getId()) {
            case R.id.iv_add_medical_file_upload_picture_1:
                path1 = path;
                break;
            case R.id.iv_add_medical_file_upload_picture_2:
                path2 = path;
                break;
            case R.id.iv_add_medical_file_upload_picture_3:
                path3 = path;
                break;
            case R.id.iv_add_medical_file_upload_picture_4:
                path4 = path;
                break;
        }
    }

    private void choosePictureIntent() {
        final String[] items = getResources().getStringArray(R.array.choose_picture);
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = new File(FileHelper.getAppDir(AddMedicalFileUploadActivity.this) + "/medical.jpg");
                    // put Uri as extra in intent object
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_medical_file_upload_done:
                finish();
                break;
            case R.id.iv_add_medical_file_upload_picture_1:
            case R.id.iv_add_medical_file_upload_picture_2:
            case R.id.iv_add_medical_file_upload_picture_3:
            case R.id.iv_add_medical_file_upload_picture_4:
                choosePictureIntent();
                iv_clicked = (ImageView) v;
                break;
        }
    }


    private void doCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_PHOTO);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            ToastHelper.t(this, "Your device does't support crop");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHOOSE_PHOTO:
                    Uri uriChoosePhoto = data.getData();
                    doCrop(uriChoosePhoto);
                    break;
                case REQUEST_TAKE_PHOTO:
                    Uri uriTakePhoto = Uri.fromFile(file);
                    doCrop(uriTakePhoto);
                    break;
                case REQUEST_CROP_PHOTO:
                    // get the returned data
                    Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    Bitmap bmp = extras.getParcelable("data");
                    // show
                    showImage(bmp);
                    break;
            }
        }
    }
}
