package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
    // ui
    private CircleImageView iv_picture;
    private EditText et_name, et_email;
    // take picture
    private Uri uriTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // ui reference
        iv_picture = findViewById(R.id.iv_profile_picture);
        et_name = findViewById(R.id.et_profile_name);
        et_email = findViewById(R.id.et_profile_email);
        findViewById(R.id.tv_profile_change_password).setOnClickListener(this);
        // get role instances
        RoleInfo r = RoleInfo.getInstance();
        String picturePath = r.getUserPicture();
        if (picturePath != null && !picturePath.isEmpty()) {
            int w = (int) getResources().getDimension(R.dimen.imageview_profile_picture_width);
            Picasso.with(this)
                    .load(picturePath)
                    .resize(w, w)
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv_picture);
        }
        iv_picture.setOnClickListener(this);
        // set name
        et_name.setText(r.getUserName());
        // set email
        et_email.setText(r.getUserEmail());
    }

    private void showImage(final Bitmap bmp) {
        final CircleImageView iv = new CircleImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPicture(bmp);
            }
        }, null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", RoleInfo.getInstance().getUserID());
            j.put("baseStr", BitmapHelper.bitmap2JPGBase64(bmp));
            j.put("url", SPHelper.getUrl(this));
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new UploadImageTask(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageTask extends IAsyncTask {
        private HashMap<String, String> map;

        UploadImageTask(Context context, JSONObject json) {
            super(context, json);
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
                        updatePicture(map.get("path"));
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
            j.put("path", path);
            j.put("logStatus", true);
            new UpdatePicture(this, j, path).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdatePicture extends IAsyncTask {

        private String path;

        UpdatePicture(Context context, JSONObject json, String path) {
            super(context, json);
            this.path = path;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set picture
                        RoleInfo.getInstance().setUserPicture(path);
                        // save user info
                        SPHelper.setUser(ProfileActivity.this, RoleInfo.getInstance().getJSONObject());
                        t(R.string.success);
                        // get role instances
                        RoleInfo r = RoleInfo.getInstance();
                        // set picture
                        String picturePath = r.getUserPicture();
                        if (picturePath != null && !picturePath.isEmpty()) {
                            int w = (int) getResources().getDimension(R.dimen.imageview_profile_picture_width);
                            Picasso.with(ProfileActivity.this)
                                    .load(picturePath)
                                    .resize(w, w)
                                    .centerCrop()
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(iv_picture);
                        }
                        setResult(RESULT_OK);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    public void choosePicture() {
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
                    uriTakePicture = FileProvider.getUriForFile(ProfileActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(ProfileActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    private void changePassword(String oldUserPassword, String newUserPassword) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "changePassword");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("oldUserPassword", EncryptHelper.md5(oldUserPassword));
            j.put("newUserPassword", EncryptHelper.md5(newUserPassword));
            j.put("logStatus", true);
            new ChangePassword(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChangePassword extends IAsyncTask {

        ChangePassword(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private AlertDialog ad;

    public void showChangePasswordDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_profile_change_password, null);
        final EditText et_old = v.findViewById(R.id.et_dialog_profile_change_password_old);
        final EditText et_new = v.findViewById(R.id.et_dialog_profile_change_password_new);
        final EditText et_confirm = v.findViewById(R.id.et_dialog_profile_change_password_confirm);
        v.findViewById(R.id.tv_dialog_profile_change_password_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
        v.findViewById(R.id.tv_dialog_profile_change_password_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                String pwd_old = et_old.getText().toString();
                String pwd_new = et_new.getText().toString();
                String pwd_confirm = et_confirm.getText().toString();
                // check
                if (!pwd_new.equals(pwd_confirm)) {
                    t(R.string.error_confirm_password);
                    return;
                }
                // changePassword
                changePassword(pwd_old, pwd_new);
            }
        });
        ad = alertWithView(v, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile_picture:
                choosePicture();
                break;
            case R.id.tv_profile_change_password:
                showChangePasswordDialog();
                break;
        }
    }

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
                showImage(BitmapHelper.resize(BitmapHelper.file2Bitmap(new File(data.getStringExtra("path"))), 300, 300));
                break;
        }
    }
}
