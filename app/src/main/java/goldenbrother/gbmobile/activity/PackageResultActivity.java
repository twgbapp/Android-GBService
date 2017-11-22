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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.PackageModel;
import goldenbrother.gbmobile.model.RoleInfo;

public class PackageResultActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
    // ui
    private TextView tv_name, tv_arrive_date;
    private View rl_take_picture;
    private ImageView iv_picture;
    // extra
    private PackageModel mPackage;
    // take picture
    private Uri uriTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_result);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_package);

        // ui reference
        tv_name = findViewById(R.id.tv_package_result_name);
        tv_arrive_date = findViewById(R.id.tv_package_result_arrive_date);
        iv_picture = findViewById(R.id.iv_package_result_picture);
        rl_take_picture = findViewById(R.id.rl_package_result_take_picture);
        findViewById(R.id.tv_package_result_receive).setOnClickListener(this);
        rl_take_picture.setOnClickListener(this);
        iv_picture.setOnClickListener(this);

        // extra
        mPackage = getIntent().getExtras().getParcelable("package");

        // init
        tv_name.setText(mPackage.getDescription());
        tv_arrive_date.setText(mPackage.getArriveDate());

    }

    private void receivePackage() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "receivePackage");
            j.put("url", SPHelper.getUrl(this));
            j.put("packageID", mPackage.getPackageID());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("baseStr", mPackage.getBaseStr());
            j.put("logStatus", true);
            new ReceivePackage(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ReceivePackage extends IAsyncTask {

        ReceivePackage(Context context, JSONObject json) {
            super(context, json);
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
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void chooseImage() {
        alertWithItems(getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_FROM_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriTakePicture = FileProvider.getUriForFile(PackageResultActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(PackageResultActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
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
                if (mPackage.getBaseStr() == null) {
                    t("No Picture");
                    return;
                }
                receivePackage();
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
                Bitmap bitmap = BitmapHelper.file2Bitmap(new File(data.getStringExtra("path")));
                bitmap = BitmapHelper.resize(bitmap, 1024, 1024);
                iv_picture.setImageBitmap(bitmap);
                mPackage.setBaseStr(BitmapHelper.bitmap2JPGBase64(bitmap));
                rl_take_picture.setVisibility(View.GONE);
                iv_picture.setVisibility(View.VISIBLE);
                break;
        }
    }
}
