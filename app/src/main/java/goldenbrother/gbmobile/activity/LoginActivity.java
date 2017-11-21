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

public class LoginActivity extends CommonActivity implements View.OnClickListener, View.OnLongClickListener {

    // ui
    private EditText et_account, et_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ui reference
        et_account = findViewById(R.id.et_login_account);
        //MaterialEditText et_account = (MaterialEditText) findViewById(R.id.et_login_account);
        //et_account.addValidator(new RegexpValidator("Only Integer Valid!", "\\d+"));
        et_password = findViewById(R.id.et_login_password);
        // listener
        findViewById(R.id.iv_login_change_language).setOnClickListener(this);
        findViewById(R.id.tv_login_dologn).setOnClickListener(this);
        findViewById(R.id.cv_login_signup).setOnClickListener(this);
        findViewById(R.id.cv_login_signup).setOnLongClickListener(this);
    }

    private void doLogin() {
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();
        /*if (account.isEmpty() || password.isEmpty()) {
            t(R.string.can_not_be_empty);
            return;
        }*/
        if (account.isEmpty()) {
            et_account.setError("");
            return;
        }

        if (password.isEmpty()) {
            et_password.setError("");
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

    @Override
    public boolean onLongClick(View v) {
        openActivity(ApplyAccountActivity.class);
        return false;
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
            j.put("registerID", SPHelper.getFcmToken(this));
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

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.iv_login_change_language:
                showLanguageDialog();
                break;
            case R.id.tv_login_dologn:
                doLogin();
                break;
            case R.id.cv_login_signup:
                openActivity(SignUpActivity.class);
                break;
        }
    }
}
