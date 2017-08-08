package goldenbrother.gbmobile.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.LogHelper;

public class PermissionActivity extends CommonActivity {

    // request
    public static final int PERMISSION = 0;
    // permission
    public static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Bundle b = new Bundle();
            openActivity(SplashActivity.class, b);
            finish();
            return;
        }
        checkPermission();
    }

    private void checkPermission() {
        // check
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION);
                return;
            }
        }
        // pass
        Bundle b = new Bundle();
        openActivity(SplashActivity.class, b);
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                t(R.string.permission_denied);
                finish();
                return;
            }
        }
        Bundle b = new Bundle();
        openActivity(SplashActivity.class, b);
        finish();
    }
}