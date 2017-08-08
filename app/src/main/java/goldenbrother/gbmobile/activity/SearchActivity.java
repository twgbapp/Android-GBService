package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Center;
import goldenbrother.gbmobile.model.Customer;
import goldenbrother.gbmobile.model.Dorm;
import goldenbrother.gbmobile.model.DormUser;
import goldenbrother.gbmobile.model.RoleInfo;

public class SearchActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_center, tv_dorm, tv_customer;
    private View ll_flabor;
    private EditText et_worker_no;
    private TextView tv_flabor_name;
    // extra
    private boolean isFLabor;
    // data
    private ArrayList<Center> list_center;
    private ArrayList<Dorm> list_dorm;
    private ArrayList<Customer> list_customer;
    private DormUser dormUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // ui reference
        tv_center = (TextView) findViewById(R.id.tv_search_center);
        tv_dorm = (TextView) findViewById(R.id.tv_search_dorm);
        tv_customer = (TextView) findViewById(R.id.tv_search_customer);
        ll_flabor = findViewById(R.id.ll_search_flabor);
        et_worker_no = (EditText) findViewById(R.id.et_search_worker_no);
        tv_flabor_name = (TextView) findViewById(R.id.tv_search_flabor_name);
        findViewById(R.id.tv_search_flabor_search).setOnClickListener(this);
        findViewById(R.id.tv_search_confirm).setOnClickListener(this);
        tv_center.setOnClickListener(this);
        tv_dorm.setOnClickListener(this);
        tv_customer.setOnClickListener(this);

        // extra
        isFLabor = getIntent().getExtras().getBoolean("isFLabor", false);

        // init
        list_center = new ArrayList<>();
        list_dorm = new ArrayList<>();
        list_customer = new ArrayList<>();
        dormUser = new DormUser();
        ll_flabor.setVisibility(isFLabor ? View.VISIBLE : View.GONE);
        // getCenterInfo
        getCenterInfo();
    }

    private void getCenterInfo() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getCenterInfo");
            j.put("userID", RoleInfo.getInstance().getUserID());
            new GetCenterInfo(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetCenterInfo extends IAsyncTask {

        GetCenterInfo(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getCenterInfo(response, list_center);
                    if (result == ApiResultHelper.SUCCESS) {
//                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void getDormInfo(String centerId) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getDormInfo");
            j.put("centerId", centerId);
            new GetDormInfo(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDormInfo extends IAsyncTask {

        GetDormInfo(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getDormInfo(response, list_dorm);
                    if (result == ApiResultHelper.SUCCESS) {
//                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void getCustomerInfo(String dormId) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getCustomerInfo");
            j.put("dormID", dormId);
            new GetCustomerInfo(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetCustomerInfo extends IAsyncTask {

        GetCustomerInfo(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.getCustomerInfo(response, list_customer);
                    if (result == ApiResultHelper.SUCCESS) {
//                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void showCenterDialog() {
        String[] items = new String[list_center.size()];
        for (int i = 0; i < list_center.size(); i++) {
            items[i] = list_center.get(i).getCenterName();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_center.setText(list_center.get(i).getCenterName());
                tv_dorm.setText(getString(R.string.select));
                tv_customer.setText(getString(R.string.select));
                getDormInfo(getCenterIdByCenterName(list_center.get(i).getCenterName()));
            }
        });
    }

    private void showDormDialog() {
        String[] items = new String[list_dorm.size()];
        for (int i = 0; i < list_dorm.size(); i++) {
            items[i] = list_dorm.get(i).getDormName();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_dorm.setText(list_dorm.get(i).getDormName());
                tv_customer.setText(getString(R.string.select));
                getCustomerInfo(getDormIdByDormName(list_dorm.get(i).getDormName()));
            }
        });
    }

    private void showCustomerDialog() {
        String[] items = new String[list_customer.size()];
        for (int i = 0; i < list_customer.size(); i++) {
            items[i] = list_customer.get(i).getCustomerName();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_customer.setText(list_customer.get(i).getCustomerName());
            }
        });
    }

    private String getCenterIdByCenterName(String centerName) {
        for (Center c : list_center) {
            if (c.getCenterName().equals(centerName)) {
                return c.getCenterID();
            }
        }
        return null;
    }

    private String getDormIdByDormName(String dormName) {
        for (Dorm d : list_dorm) {
            if (d.getDormName().equals(dormName)) {
                return d.getDormID();
            }
        }
        return null;
    }

    private String getCustomerNoByCustomerName(String customerName) {
        for (Customer c : list_customer) {
            if (c.getCustomerName().equals(customerName)) {
                return c.getCustomerNo();
            }
        }
        return null;
    }

    private void getDormUserInfo(String customerNo, String workerNo) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getDormUserInfo");
            j.put("customerNo", customerNo);
            j.put("workerNo", workerNo);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetDormUserInfo(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDormUserInfo extends IAsyncTask {

        GetDormUserInfo(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getDormUserInfo(response, dormUser);
                    if (result == ApiResultHelper.SUCCESS) {
                        tv_flabor_name.setText(dormUser.getUserName());
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        String centerName = tv_center.getText().toString();
        String dormName = tv_dorm.getText().toString();
        String customerName = tv_customer.getText().toString();
        String centerId = getCenterIdByCenterName(centerName);
        String dormId = getDormIdByDormName(dormName);
        String customerNo = getCustomerNoByCustomerName(customerName);
        switch (v.getId()) {
            case R.id.tv_search_center:
                showCenterDialog();
                break;
            case R.id.tv_search_dorm:
                if (!tv_center.getText().toString().equals(getString(R.string.select)))
                    showDormDialog();
                break;
            case R.id.tv_search_customer:
                if (!tv_dorm.getText().toString().equals(getString(R.string.select)))
                    showCustomerDialog();
                break;
            case R.id.tv_search_flabor_search:
                String workerNo = et_worker_no.getText().toString();
                if (centerId == null || dormId == null || customerNo == null || workerNo.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                getDormUserInfo(customerNo, workerNo);
                break;
            case R.id.tv_search_confirm:
                if (centerId == null || dormId == null || customerNo == null) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                if (isFLabor && dormUser.getFlaborNo() == null) {
                    t(R.string.can_not_be_empty);
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("centerName", centerName);
                intent.putExtra("dormName", dormName);
                intent.putExtra("customerName", customerName);

                intent.putExtra("centerId", centerId);
                intent.putExtra("dormId", dormId);
                intent.putExtra("customerNo", customerNo);

                if (isFLabor) {
                    intent.putExtra("flaborNo", dormUser.getFlaborNo());
                    intent.putExtra("flaborName", tv_flabor_name.getText().toString());
                    intent.putExtra("roomId", dormUser.getRoomID());
                    intent.putExtra("centerDirectorId", dormUser.getCenterDirectorID());
                    intent.putExtra("birthday", dormUser.getUserBirthday());
                    intent.putExtra("sex", dormUser.getUserSex());
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
