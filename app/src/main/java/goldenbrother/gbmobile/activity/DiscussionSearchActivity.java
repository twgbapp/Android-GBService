package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Center;
import goldenbrother.gbmobile.model.Customer;
import goldenbrother.gbmobile.model.Dorm;
import goldenbrother.gbmobile.model.RoleInfo;

public class DiscussionSearchActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_center, tv_dorm, tv_customer;
    // data
    private ArrayList<Center> list_center;
    private ArrayList<Dorm> list_dorm;
    private ArrayList<Customer> list_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_search);

        // ui reference
        tv_center = (TextView) findViewById(R.id.tv_discussion_search_center);
        tv_dorm = (TextView) findViewById(R.id.tv_discussion_search_dorm);
        tv_customer = (TextView) findViewById(R.id.tv_discussion_search_customer);
        tv_center.setOnClickListener(this);
        tv_dorm.setOnClickListener(this);
        tv_customer.setOnClickListener(this);

        // init
        list_center = new ArrayList<>();
        list_dorm = new ArrayList<>();
        list_customer = new ArrayList<>();

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
                        t(R.string.success);
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
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void getCustomerInfo(String centerId) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getCustomerInfo");
            j.put("centerId", centerId);
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
                        t(R.string.success);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_discussion_search_center:
                showCenterDialog();
                break;
            case R.id.tv_discussion_search_dorm:
                showDormDialog();
                break;
            case R.id.tv_discussion_search_customer:
                showCustomerDialog();
                break;
        }
    }
}
