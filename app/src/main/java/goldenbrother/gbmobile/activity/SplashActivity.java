package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.model.RoleInfo;

public class SplashActivity extends CommonActivity {

    // enter type
    public static final int LOG_OUT = 0;
    public static final int LOG_IN = 1;
    // ui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Version
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText("Version：" + PackageUtil.getVersionName(this));
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
