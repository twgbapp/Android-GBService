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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import com.squareup.picasso.MemoryPolicy;
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
    public static final int REQUEST_FROM_GALLERY = 11;
    public static final int REQUEST_TAKE_PHOTO = 12;
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
        iv_picture = (CircleImageView) findViewById(R.id.iv_profile_picture);
        et_name = (EditText) findViewById(R.id.et_profile_name);
        et_email = (EditText) findViewById(R.id.et_profile_email);
        findViewById(R.id.tv_profile_change_password).setOnClickListener(this);
        // get role instances
        RoleInfo r = RoleInfo.getInstance();
        // set picture
        if (r.getUserPicture() != null && !r.getUserPicture().isEmpty()) {
            int w = (int) getResources().getDimension(R.dimen.imageview_navigation_picture_width);
            Picasso.with(this).load(r.getUserPicture()).placeholder(R.drawable.ic_person_white).memoryPolicy(MemoryPolicy.NO_CACHE).resize(w, w).centerCrop().into(iv_picture);
        } else {
            iv_picture.setImageResource(R.drawable.ic_person_white);
        }
        iv_picture.setOnClickListener(this);
        // set name
        et_name.setText(r.getUserName());
        // set email
        et_email.setText(r.getUserEmail());
    }

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPicture(BitmapHelper.getLimitBitmap(bmp, 300, 300));
            }
        }, null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", RoleInfo.getInstance().getUserID());
            j.put("baseStr", BitmapHelper.bitmap2String(bmp));
            j.put("url", URLHelper.HOST);
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
                        // set picture
                        RoleInfo.getInstance().setUserPicture(path);
                        // save user info
                        SPHelper.getInstance(ProfileActivity.this).setUserInfo(RoleInfo.getInstance().getJSONObject());
                        t(R.string.success);
                        // get role instances
                        RoleInfo r = RoleInfo.getInstance();
                        // set picture
                        if (r.getUserPicture() != null && !r.getUserPicture().isEmpty()) {
                            int w = (int) getResources().getDimension(R.dimen.imageview_navigation_picture_width);
                            Picasso.with(ProfileActivity.this).load(r.getUserPicture()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(w, w).centerCrop().into(iv_picture);
                            setResult(RESULT_OK);
                        } else {
                            Picasso.with(ProfileActivity.this).load(R.drawable.ic_person_white).into(iv_picture);
                        }
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
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
                    uriTakePicture = FileProvider.getUriForFile(ProfileActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getAppDir(ProfileActivity.this) + "/take_picture.jpg"));
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
            new ChangePassword(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChangePassword extends IAsyncTask {

        ChangePassword(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.changePassword(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void showChangePasswordDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_profile_change_password, null);
        final EditText et_old = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_old);
        final EditText et_new = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_new);
        final EditText et_confirm = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_confirm);
        alertWithView(v, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pwd_old = et_old.getText().toString();
                String pwd_new = et_new.getText().toString();
                String pwd_confirm = et_confirm.getText().toString();
                // check
                if (!pwd_new.equals(pwd_confirm)) {
                    t(R.string.profile_error_confirm_password);
                    return;
                }
                // changePassword
                changePassword(pwd_old, pwd_new);
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_profile_picture:
                choosePictureIntent();
                break;
            case R.id.tv_profile_change_password:
                showChangePasswordDialog();
                break;
        }
    }

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
                    Bitmap bmp = BitmapHelper.uri2Bitmap(this, CropImage.getActivityResult(data).getUri());
                    showImage(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
