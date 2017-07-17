package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalFileUploadActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_SIGNATURE = 10;
    public static final int REQUEST_FROM_GALLERY = 11;
    public static final int REQUEST_TAKE_PHOTO = 12;
    // ui
    private ImageView iv_signature, iv_medical, iv_diagnostic, iv_service;
    private ImageView iv_clicked;
    // take picture
    private Uri uriTakePicture;
    // data
    private String signaturePath = "", medicalPath = "", diagnosticPath = "", servicePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_file_upload);

        // ui reference
        findViewById(R.id.iv_medical_file_upload_done).setOnClickListener(this);
        iv_signature = (ImageView) findViewById(R.id.iv_medical_file_upload_picture_signature);
        iv_medical = (ImageView) findViewById(R.id.iv_medical_file_upload_picture_medical);
        iv_diagnostic = (ImageView) findViewById(R.id.iv_medical_file_upload_picture_diagnostic);
        iv_service = (ImageView) findViewById(R.id.iv_medical_file_upload_picture_service);
        iv_signature.setOnClickListener(this);
        iv_medical.setOnClickListener(this);
        iv_diagnostic.setOnClickListener(this);
        iv_service.setOnClickListener(this);
    }

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadPicture(BitmapHelper.getLimitBitmap(bmp, 300, 300));
            }
        }, null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", BitmapHelper.getRandomName());
            j.put("url", URLHelper.HOST);
            j.put("baseStr", BitmapHelper.bitmap2String(bmp));
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
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
            case R.id.iv_medical_file_upload_picture_signature:
                signaturePath = path;
                Picasso.with(this).load(path).into(iv_signature);
                break;
            case R.id.iv_medical_file_upload_picture_medical:
                medicalPath = path;
                Picasso.with(this).load(path).into(iv_medical);
                break;
            case R.id.iv_medical_file_upload_picture_diagnostic:
                diagnosticPath = path;
                Picasso.with(this).load(path).into(iv_diagnostic);
                break;
            case R.id.iv_medical_file_upload_picture_service:
                servicePath = path;
                Picasso.with(this).load(path).into(iv_service);
                break;
        }
    }

    private void choosePictureIntent() {
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
                    uriTakePicture = FileProvider.getUriForFile(MedicalFileUploadActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getAppDir(MedicalFileUploadActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_medical_file_upload_done:
                Intent intent = new Intent();
                intent.putExtra("signaturePath", signaturePath);
                intent.putExtra("medicalPath", medicalPath);
                intent.putExtra("diagnosticPath", diagnosticPath);
                intent.putExtra("servicePath", servicePath);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_medical_file_upload_picture_signature:
                iv_clicked = (ImageView) v;
                openActivityForResult(SignatureActivity.class, REQUEST_SIGNATURE);
                break;
            case R.id.iv_medical_file_upload_picture_medical:
            case R.id.iv_medical_file_upload_picture_diagnostic:
            case R.id.iv_medical_file_upload_picture_service:
                iv_clicked = (ImageView) v;
                choosePictureIntent();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_SIGNATURE:
                Bitmap bitmap = BitmapHelper.byte2Bitmap(data.getByteArrayExtra("bitmap"));
                uploadPicture(bitmap);
                break;
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
                    Bitmap bmp = BitmapHelper.uri2Bitmap(this, CropImage.getActivityResult(data).getUri());
                    showImage(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
