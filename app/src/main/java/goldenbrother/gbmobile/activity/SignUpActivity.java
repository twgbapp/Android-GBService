package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.helper.UtilHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private EditText et_account, et_password, et_confirm_password, et_id_number, et_name, et_email, et_phone;
    private TextView tv_sex, tv_nation;
    private TextView tv_birthday;
    // data
    private String[] nation_name;
    private String[] nation_code;
    private HashMap<String, String> userData;
    private  Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setUpBackToolbar(R.id.toolbar, R.string.sign_up_create_account);
        // ui reference
        et_account = (EditText) findViewById(R.id.et_sign_up_account);
        et_password = (EditText) findViewById(R.id.et_sign_up_password);
        et_confirm_password = (EditText) findViewById(R.id.et_sign_up_confirm_password);
        et_id_number = (EditText) findViewById(R.id.et_sign_up_id_number);
        et_name = (EditText) findViewById(R.id.et_sign_up_name);
        et_email = (EditText) findViewById(R.id.et_sign_up_email);
        et_phone = (EditText) findViewById(R.id.et_sign_up_phone);
        tv_sex = (TextView) findViewById(R.id.tv_sign_up_sex);
        tv_nation = (TextView) findViewById(R.id.tv_sign_up_nation);
        tv_birthday = (TextView) findViewById(R.id.tv_sign_up_birthday);
        findViewById(R.id.tv_sign_up_do_sign_up).setOnClickListener(this);
        findViewById(R.id.tv_sign_up_check).setOnClickListener(this);
        // listener
        tv_sex.setOnClickListener(this);
        tv_nation.setOnClickListener(this);
        tv_birthday.setOnClickListener(this);
        // init Spinner
        nation_name = getResources().getStringArray(R.array.nation_name);
        nation_code = getResources().getStringArray(R.array.nation_code);
    }

    public void showSexDialog() {
        final String[] items = {getString(R.string.male), getString(R.string.female)};
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_sex.setText(items[i]);
            }
        });
    }

    public void showNationDialog() {
        alertWithItems(nation_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_nation.setText(nation_name[i]);
            }
        });
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

    private String getNationCode(String name) {
        for (int i = 0; i < nation_name.length; i++) {
            if (nation_name[i].equals(name)) {
                return nation_code[i];
            }
        }
        return nation_code[0];
    }

    private void doSignUp() {
        int userType = getUserData("userType").isEmpty() ? 1 : Integer.valueOf(getUserData("userType"));
        String account = et_account.getText().toString();
        String confirm_password = et_confirm_password.getText().toString();
        String password = et_password.getText().toString();
        String nationCode = getNationCode(tv_nation.getText().toString());
        String idNumber = et_id_number.getText().toString();
        String name = et_name.getText().toString();
        String sex = tv_sex.getText().toString().equals(getString(R.string.male)) ? "0" : "1";
        String birthday = tv_birthday.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
//        idNumber.isEmpty() ||
        // check empty
        if (account.isEmpty() || confirm_password.isEmpty() || password.isEmpty() ||
                nationCode.isEmpty() || name.isEmpty() ||
                sex.isEmpty() || birthday.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            t(R.string.can_not_be_empty);
            return;
        }

        // check password
        if (!confirm_password.equals(password)) {
            t(R.string.error_confirm_password);
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
                j.put("workerNo", getUserData("workerNo"));
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
                                        tv_nation.setText(nation_name[i]);
                                        break;
                                    }
                                }
                                // set name
                                et_name.setText(userData.get("userName"));
                                // set sex
                                tv_sex.setText(userData.get("userSex").equals("男") ? getString(R.string.male) : getString(R.string.female));
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
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.tv_sign_up_check:
                String str = et_id_number.getText().toString();
                if (!str.isEmpty()) {
                    checkIDNumber(str);
                } else {
                    t(R.string.can_not_be_empty);
                }
                break;
            case R.id.tv_sign_up_sex:
                showSexDialog();
                break;
            case R.id.tv_sign_up_nation:
                showNationDialog();
                break;
            case R.id.tv_sign_up_birthday:
                showDatePickerDialog();
                break;
            case R.id.tv_sign_up_do_sign_up:
                doSignUp();
                break;
        }
    }
}
