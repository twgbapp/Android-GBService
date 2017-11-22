package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    // sum moon dormID, centerID
    public static final String SUM_MOON_DORM_ID = "2013";
    public static final String SUM_MOON_CENTER_ID = "88";
    // ui
    private EditText et_account, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ui reference
        et_account = findViewById(R.id.et_login_account);
        et_password = findViewById(R.id.et_login_password);
        findViewById(R.id.iv_login_change_language).setOnClickListener(this);
        findViewById(R.id.tv_login_dologn).setOnClickListener(this);
        findViewById(R.id.cv_login_signup).setOnClickListener(this);
    }

    private void doLogin(String userID, String userPassword) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "login");
            j.put("userID", userID);
            j.put("userPassword", EncryptHelper.md5(userPassword));
            j.put("logStatus", true);
            new DoLogin(this, j, userID, userPassword).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DoLogin extends IAsyncTask {
        private String userID;
        private String userPassword;

        DoLogin(Context context, JSONObject json, String userID, String userPassword) {
            super(context, json);
            this.userID = userID;
            this.userPassword = userPassword;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.login(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        String dormID = RoleInfo.getInstance().getDormID();
                        String centerID = RoleInfo.getInstance().getCenterID();
                        if (dormID.equals(SUM_MOON_DORM_ID) && centerID.equals(SUM_MOON_CENTER_ID)) {
                            loginToSunMoon(userID, userPassword);
                        } else {
                            registerGCMID();
                        }
                    } else {
                        t(R.string.login_error_account_or_password_not_exist);
                    }
                    break;
            }
        }
    }

    private void loginToSunMoon(String userID, String userPassword) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "login");
            j.put("userID", userID);
            j.put("userPassword", EncryptHelper.md5(userPassword));
            j.put("logStatus", true);
            new LoginToSunMoon(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoginToSunMoon extends IAsyncTask {

        LoginToSunMoon(Context context, JSONObject json) {
            super(context, json, URLHelper.HOST_SUN_MOON);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.login(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        SPHelper.setUrl(LoginActivity.this, URLHelper.HOST_SUN_MOON);
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
            j.put("registerID", SPHelper.getFcmToken(this));
            j.put("status", 1);
            j.put("logStatus", true);
            new RegisterGCMID(this, j).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class RegisterGCMID extends IAsyncTask {

        RegisterGCMID(Context context, JSONObject json) {
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
                        // save user info
                        SPHelper.setUser(LoginActivity.this, RoleInfo.getInstance().getJSONObject());
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

    private AlertDialog ad;

    private void showLanguageDialog() {
        String[] items = getResources().getStringArray(R.array.language);
        ad = alertCustomItems(0, null, items, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                ad.dismiss();
                alertWithView(null, getString(R.string.language_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setLanguage(position);
                    }
                }, null);
            }
        });
    }

    private void setLanguage(int i) {
        String[] languages = {"en", "zh", "in", "vi", "th"};
        SPHelper.setLanguage(this, languages[i]);
        // restart
        openActivity(SplashActivity.class, new Bundle());
        finish();
    }

    private void signUpAndApplyAccountDialog() {
        final String[] items = {getString(R.string.sign_up), getString(R.string.apply_account)};
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openActivity(SignUpActivity.class);
                } else {
                    openActivity(ApplyAccountActivity.class);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.iv_login_change_language:
                showLanguageDialog();
                break;
            case R.id.tv_login_dologn:
                String userID = et_account.getText().toString();
                String userPassword = et_password.getText().toString();

                if (userID.isEmpty()) {
                    et_account.setError("");
                    return;
                }

                if (userPassword.isEmpty()) {
                    et_password.setError("");
                    return;
                }
                doLogin(userID, userPassword);
                break;
            case R.id.cv_login_signup:
                signUpAndApplyAccountDialog();
                break;
        }
    }
}
