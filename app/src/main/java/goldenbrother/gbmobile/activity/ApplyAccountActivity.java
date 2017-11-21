package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;

public class ApplyAccountActivity extends CommonActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_account);
        setUpBackToolbar(R.id.toolbar, R.string.apply_account);

        // ui reference
        findViewById(R.id.tv_apply_account_apply).setOnClickListener(this);
        findViewById(R.id.tv_apply_account_user_sex).setOnClickListener(this);
        findViewById(R.id.tv_apply_account_nation).setOnClickListener(this);
        findViewById(R.id.tv_apply_account_user_birthday).setOnClickListener(this);
    }

    private void applyAccount(String userID, String userPassword, String nationCode,
                              String userIDNumber, String companyName, String userName,
                              String userSex, String userBirthday, String userEmail,
                              String userPhone) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "signUp");
            j.put("userID", userID);
            j.put("userPassword", EncryptHelper.md5(userPassword));
            j.put("userNationCode", nationCode);
            j.put("userIDNumber", userIDNumber);
            j.put("companyName", companyName);
            j.put("userName", userName);
            j.put("userSex", userSex);
            j.put("userBirthday", userBirthday);
            j.put("userEmail", userEmail);
            j.put("userPhone", userPhone);
            j.put("logStatus", false);
            new ApplyAccount(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ApplyAccount extends IAsyncTask {

        ApplyAccount(Context context, JSONObject json, String url) {
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
                        t(R.string.success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }


    public void showSexDialog() {
        final String[] items = {getString(R.string.male), getString(R.string.female)};
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((TextView) findViewById(R.id.tv_apply_account_user_sex)).setText(items[i]);
            }
        });
    }

    public String getSex(String name) {
        String male = getString(R.string.male);
        return name.equals(male) ? "男" : "女";
    }

    public void showNationDialog() {
        final String[] items = getResources().getStringArray(R.array.nation_name);
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((TextView) findViewById(R.id.tv_apply_account_nation)).setText(items[i]);
            }
        });
    }

    private String getNationCode(String name) {
        final String[] names = getResources().getStringArray(R.array.nation_name);
        final String[] codes = getResources().getStringArray(R.array.nation_code);
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return codes[i];
            }
        }
        return codes[0];
    }

    public void showDatePickerDialog() {
        final DatePicker dp = new DatePicker(this);
        alertWithView(dp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String date = dp.getYear() + "-" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "-" + (dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth());
                ((TextView) findViewById(R.id.tv_apply_account_user_birthday)).setText(date);
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_apply_account_user_sex:
                showSexDialog();
                break;
            case R.id.tv_apply_account_nation:
                showNationDialog();
                break;
            case R.id.tv_apply_account_user_birthday:
                showDatePickerDialog();
                break;
            case R.id.tv_apply_account_apply:
                String userID = ((EditText) findViewById(R.id.et_apply_account_user_id)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.et_apply_account_user_password)).getText().toString();
                String confirmPassword = ((EditText) findViewById(R.id.et_apply_account_confirm_password)).getText().toString();
                String nation = ((TextView) findViewById(R.id.tv_apply_account_nation)).getText().toString();
                String userIDNumber = ((EditText) findViewById(R.id.et_apply_account_user_id_number)).getText().toString();
                String companyName = ((EditText) findViewById(R.id.et_apply_account_company_name)).getText().toString();
                String userName = ((EditText) findViewById(R.id.et_apply_account_user_name)).getText().toString();
                String userSex = ((TextView) findViewById(R.id.tv_apply_account_user_sex)).getText().toString();
                String userBirthday = ((TextView) findViewById(R.id.tv_apply_account_user_birthday)).getText().toString();
                String userEmail = ((EditText) findViewById(R.id.et_apply_account_user_email)).getText().toString();
                String userPhone = ((EditText) findViewById(R.id.et_apply_account_user_phone)).getText().toString();

                // check empty
                if (userID.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty() ||
                        nation.isEmpty() || userIDNumber.isEmpty() || companyName.isEmpty() ||
                        userName.isEmpty() || userSex.isEmpty() || userBirthday.isEmpty() ||
                        userEmail.isEmpty() || userPhone.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }

                // check password
                if (!userPassword.equals(confirmPassword)) {
                    t(R.string.error_confirm_password);
                    return;
                }

                applyAccount(userID, userPassword, getNationCode(nation),
                        userIDNumber, companyName, userName,
                        getSex(userSex), userBirthday, userEmail,
                        userPhone);
                break;
        }
    }
}
