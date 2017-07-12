package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.NationListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.helper.UtilHelper;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_account, et_password, et_confirm_password, et_id_number, et_name, et_email, et_phone;
    private Spinner sp_national_code;
    private RadioButton rb_male, rb_female;
    private TextView tv_birthday;
    // data
    private String[] nation_name;
    private String[] nation_code;
    private HashMap<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // ui reference
        et_account = (EditText) findViewById(R.id.et_sign_up_account);
        et_password = (EditText) findViewById(R.id.et_sign_up_password);
        et_confirm_password = (EditText) findViewById(R.id.et_sign_up_confirm_password);
        et_id_number = (EditText) findViewById(R.id.et_sign_up_id_number);
        et_name = (EditText) findViewById(R.id.et_sign_up_name);
        et_email = (EditText) findViewById(R.id.et_sign_up_email);
        et_phone = (EditText) findViewById(R.id.et_sign_up_phone);
        sp_national_code = (Spinner) findViewById(R.id.sp_sign_up_national_code);
        rb_male = (RadioButton) findViewById(R.id.rb_sign_male);
        rb_female = (RadioButton) findViewById(R.id.rb_sign_female);
        tv_birthday = (TextView) findViewById(R.id.tv_sign_up_birthday);
        findViewById(R.id.tv_sign_up_do_sign_up).setOnClickListener(this);
        findViewById(R.id.tv_sign_up_check).setOnClickListener(this);
        // listener
        tv_birthday.setOnClickListener(this);
        // init Spinner
        nation_name = getResources().getStringArray(R.array.nation_name);
        nation_code = getResources().getStringArray(R.array.nation_code);
        sp_national_code.setAdapter(new NationListAdapter(this, nation_name));
    }

    public void showDatePickerDialog() {

        final DatePicker dp = new DatePicker(this);
        alertWithView(dp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String date = dp.getYear() + "-" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "-" + (dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth());
                tv_birthday.setText(date);
            }
        }, null);
    }

    private String getUserData(String key) {
        if (userData != null && userData.containsKey(key)) {
            return userData.get(key);
        } else {
            return "";
        }
    }

    private void doSignUp() {
        int userType = getUserData("userType").isEmpty() ? 1 : Integer.valueOf(getUserData("userType"));
        String account = et_account.getText().toString();
        String confirm_password = et_confirm_password.getText().toString();
        String password = et_password.getText().toString();
        String nationCode = nation_code[sp_national_code.getSelectedItemPosition()];
        String idNumber = et_id_number.getText().toString();
        String name = et_name.getText().toString();
        String sex = rb_male.isChecked() ? "0" : "1";
        String birthday = tv_birthday.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();

        // check empty
        if (account.isEmpty() || confirm_password.isEmpty() || password.isEmpty() ||
                nationCode.isEmpty() || idNumber.isEmpty() || name.isEmpty() ||
                sex.isEmpty() || birthday.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            t(R.string.can_not_be_empty);
            return;
        }

        // check password
        if (!confirm_password.equals(password)) {
            t(R.string.sign_up_error_confirm_password);
            return;
        }

        // do sign up
        try {
            JSONObject j = new JSONObject();
            j.put("action", "signUp");
            j.put("userType", userType);
            j.put("userID", account);
            j.put("userPassword", EncryptHelper.md5(password));
            j.put("userIDNumber", idNumber);
            j.put("userName", name);
            j.put("userSex", sex);
            j.put("userBirthday", birthday);
            j.put("userEmail", email);
            j.put("userPhone", phone);
            j.put("userNationCode", nationCode);
            j.put("areaNum", getUserData("areaNum"));
            if (userType == 2) {
                j.put("flaborNo", getUserData("flaborNo"));
                j.put("customerNo", getUserData("customerNo"));
            } else if (userType == 3) {
                j.put("title", getUserData("title"));
            }
            j.put("logStatus", false);
            new DoSignUp(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DoSignUp extends IAsyncTask {

        DoSignUp(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.doSignUp(response);
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

    private void checkIDNumber(String userIDNumber) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "checkUserIDNumber");
            j.put("userIDNumber", userIDNumber);
            new CheckIDNumber(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CheckIDNumber extends IAsyncTask {


        CheckIDNumber(Context context, JSONObject json, String url) {
            super(context, json, url);
            if (userData == null)
                userData = new HashMap<>();
            userData.clear();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.checkIDNumber(response, userData);
                    if (result == ApiResultHelper.SUCCESS) {
                        switch (Integer.parseInt(userData.get("userType"))) {
                            case 0:
                                t(R.string.sign_up_id_number_can_not_sign_up);
                                break;
                            case 1:
                                t(R.string.sign_up_id_number_can_sign_up);
                                break;
                            case 2:
                                t(R.string.sign_up_id_number_can_sign_up);
                                // move to target nation
                                int position = nation_code.length - 1;
                                for (int i = 0; i < nation_code.length; i++) {
                                    if (userData.get("userNationCode").equals(nation_code[i])) {
                                        position = i;
                                        break;
                                    }
                                }
                                sp_national_code.setSelection(position);
                                // set name
                                et_name.setText(userData.get("userName"));
                                // set sex
                                if (userData.get("userSex").equals("男")) {
                                    rb_male.setChecked(true);
                                } else {
                                    rb_female.setChecked(true);
                                }
                                // set birthday
                                tv_birthday.setText(userData.get("userBirthday"));
                                // set email
                                et_email.setText(userData.get("userEmail"));
                                // set phone
                                et_phone.setText(userData.get("userPhone"));
                                break;
                        }
                    } else {
                        t(R.string.server_error);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_up_check:
                String str = et_id_number.getText().toString();
                if (!str.isEmpty()) {
                    checkIDNumber(str);
                } else {
                    t(R.string.can_not_be_empty);
                }
                break;
            case R.id.tv_sign_up_birthday:
                showDatePickerDialog();
                break;
            case R.id.tv_sign_up_do_sign_up:
                UtilHelper.hideKeyBoard(this, v);
                doSignUp();
                break;
        }
    }
}
