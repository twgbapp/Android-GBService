package goldenbrother.gbmobile.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalFileUploadActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_SIGNATURE = 0;
    public static final int REQUEST_CHOOSE_PHOTO = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final int REQUEST_CROP_PHOTO = 3;
    // ui
    private ImageView iv_signature, iv_medical, iv_diagnostic, iv_service;
    private ImageView iv_clicked;
    // take picture
    private File file;
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
                    file = new File(FileHelper.getAppDir(MedicalFileUploadActivity.this) + "/medical.jpg");
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
            t("Your device does't support crop");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SIGNATURE:
                    Bitmap bitmap = BitmapHelper.byte2Bitmap(data.getByteArrayExtra("bitmap"));
                    uploadPicture(bitmap);
                    LogHelper.d(bitmap.getWidth() + " " + bitmap.getHeight());
                    break;
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
