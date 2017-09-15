package goldenbrother.gbmobile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.PackageHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.model.RoleInfo;

public class SplashActivity extends CommonActivity {

    // enter type
    public static final int LOG_OUT = 0;
    public static final int LOG_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // ani
        Message msg = new Message();
        msg.what = getIntent().getExtras().getBoolean("isLogout", false) ? LOG_OUT : LOG_IN;
        handler.sendMessageDelayed(msg, 1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOG_IN:
                    String info = SPHelper.getInstance(SplashActivity.this).getUserInfo();
                    if (!info.isEmpty()) { // auto login
                        try {
                            JSONObject j = new JSONObject(info);
                            // get roleID from local
                            int roleID = j.getInt("roleID");
                            // set roleID to Instance
                            RoleInfo.getInstance().setRoleID(roleID);
                            // set JSONObject to Labor or Manager Instance
                            RoleInfo.getInstance().setJSONObject(j);
                            // open main screen
                            openActivity(MainActivity.class);
                            finish();
                        } catch (JSONException e) { // occur exception
                            e.printStackTrace();
                            // clear user info
                            SPHelper.getInstance(SplashActivity.this).clearUserInfo();
                            // open Login
                            openActivity(LoginActivity.class);
                            finish();
                        }
                    } else { // first login
                        // open Login
                        openActivity(LoginActivity.class);
                        finish();
                    }
                    break;
                case LOG_OUT: // log out
                    // clear user info
                    SPHelper.getInstance(SplashActivity.this).clearUserInfo();
                    // open Login
                    openActivity(LoginActivity.class);
                    finish();
                    break;
            }
        }
    };
}
