package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.PackageModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class PackageResultActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_FROM_GALLERY = 11;
    public static final int REQUEST_TAKE_PHOTO = 12;
    public static final int REQUEST_QR_CODE = 1;
    // ui
    private TextView tv_name, tv_arrive_date;
    private View rl_take_picture;
    private ImageView iv_picture;
    // extra
    private PackageModel p;
    // take picture
    private Uri uriTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_result);

        // ui reference
        tv_name = (TextView) findViewById(R.id.tv_package_result_name);
        tv_arrive_date = (TextView) findViewById(R.id.tv_package_result_arrive_date);
        iv_picture = (ImageView) findViewById(R.id.iv_package_result_picture);
        rl_take_picture = findViewById(R.id.rl_package_result_take_picture);
        findViewById(R.id.tv_package_result_receive).setOnClickListener(this);
        rl_take_picture.setOnClickListener(this);
        iv_picture.setOnClickListener(this);
        // extra
        p = getIntent().getExtras().getParcelable("package");

        // init
        if (p != null) {
            tv_name.setText(p.getDescription());
            tv_arrive_date.setText(p.getArriveDate());
        }

    }

    private void receivePackage(PackageModel p) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "receivePackage");
            j.put("url", URLHelper.HOST);
            j.put("packageID", p.getPackageID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("baseStr", p.getBaseStr());
            j.put("logStatus", true);
            new ReceivePackage(this, j, URLHelper.HOST, p).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ReceivePackage extends IAsyncTask {

        ReceivePackage(Context context, JSONObject json, String url, PackageModel p) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.receivePackage(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        Intent intent = new Intent();
                        intent.putExtra("package", p);
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
                    uriTakePicture = FileProvider.getUriForFile(PackageResultActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getAppDir(PackageResultActivity.this) + "/take_picture.jpg"));
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
            case R.id.iv_package_result_picture:
                chooseImage();
                break;
            case R.id.rl_package_result_take_picture:
                chooseImage();
                break;
            case R.id.tv_package_result_receive:
                if (uriTakePicture == null) {
                    t("No Picture");
                    return;
                }
                receivePackage(p);
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
                    Bitmap bmp = BitmapHelper.resize(BitmapHelper.uri2Bitmap(this, CropImage.getActivityResult(data).getUri()), BitmapHelper.MAX_WIDTH, BitmapHelper.MAX_HEIGHT);
                    iv_picture.setImageBitmap(bmp);
                    String baseStr = BitmapHelper.bitmap2String(bmp);
                    p.setBaseStr(baseStr);
                    rl_take_picture.setVisibility(View.GONE);
                    iv_picture.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
