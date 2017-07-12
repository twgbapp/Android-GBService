package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.helper.UtilHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_account, et_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ui reference
        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        // listener
        findViewById(R.id.iv_login_change_language).setOnClickListener(this);
        findViewById(R.id.tv_login_dologn).setOnClickListener(this);
        findViewById(R.id.tv_login_signup).setOnClickListener(this);
    }

    private void doLogin() {
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();
        if (account.isEmpty()) {
            t(R.string.can_not_be_empty_account);
            return;
        }
        if (password.isEmpty()) {
            t(R.string.can_not_be_empty_password);
            return;
        }

        try {
            JSONObject j = new JSONObject();
            j.put("action", "login");
            j.put("userID", account);
            j.put("userPassword", EncryptHelper.md5(password));
            j.put("logStatus", true);
            new DoLogin(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DoLogin extends IAsyncTask {

        DoLogin(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.login(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        registerGCMID();
                    } else {
                        t(R.string.login_error_account_or_password_not_exist);
                    }
                    break;
            }
        }
    }

    private void registerGCMID() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "registerGCMID");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("registerID", SPHelper.getInstance(this).getGCMID());
            j.put("status", 1);
            j.put("logStatus", true);
            new RegisterGCMID(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class RegisterGCMID extends IAsyncTask {

        RegisterGCMID(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.registerGCMID(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // save user info
                        SPHelper.getInstance(LoginActivity.this).setUserInfo(RoleInfo.getInstance().getJSONObject());
                        // open main screen
                        openActivity(MainActivity.class);
                        finish();
                    } else {
                        t(R.string.login_error_fcm);
                    }
                    break;
            }
        }
    }

    private void showChangeLanguageDialog() {
        String[] items = getResources().getStringArray(R.array.language);
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setLanguage(i);
            }
        });
    }

    private void setLanguage(int i) {
        String lang = "";
        switch (i) {
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "zh";
                break;
            case 2:
                lang = "in";
                break;
            case 3:
                lang = "vi";
                break;
            case 4:
                lang = "th";
                break;
        }
        Locale locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        // restart
        Bundle b = new Bundle();
        openActivity(LoginActivity.class, b);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_login_change_language:
                showChangeLanguageDialog();
                break;
            case R.id.tv_login_dologn:
                UtilHelper.hideKeyBoard(this, v);
                doLogin();
                break;
            case R.id.tv_login_signup:
                openActivity(SignUpActivity.class);
                break;
        }
    }
}
