package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MedicalListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.RoleInfo;

public class MedicalListActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private RecyclerView rv;
    // data
    private ArrayList<Medical> list_medical_flabor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_list);

        // ui reference
        findViewById(R.id.iv_medical_list_search).setOnClickListener(this);
        findViewById(R.id.iv_medical_list_add).setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv_medical_list);

        // init RecyclerView
        list_medical_flabor = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MedicalListRVAdapter(this, list_medical_flabor));

        // getMedicalList
        getMedicalFlaborList(TimeHelper.getYMD(), TimeHelper.getYMD());
    }

    private void getMedicalFlaborList(String startRecordDate, String endRecordDate) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getMedicalFlaborList");
            j.put("startRecordDate", startRecordDate);
            j.put("endRecordDate", endRecordDate);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new GetMedicalFlaborList(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetMedicalFlaborList extends IAsyncTask {

        GetMedicalFlaborList(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getMedicalFlaborList(response, list_medical_flabor);
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                    } else {
                        t("Empty");
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    public void onItemClick(Medical item) {
        Bundle b = new Bundle();
        b.putParcelable("medical", item);
        openActivity(MedicalRecordActivity.class, b);
    }

    private void showSearchDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_medical_list_search, null);
        final TextView tv_start_date = (TextView) v.findViewById(R.id.tv_dialog_medical_list_search_start_date);
        final TextView tv_end_date = (TextView) v.findViewById(R.id.tv_dialog_medical_list_search_end_date);
        tv_start_date.setText(TimeHelper.getYMD());
        tv_end_date.setText(TimeHelper.getYMD());
        tv_start_date.setOnClickListener(showDatePickerListener);
        tv_end_date.setOnClickListener(showDatePickerListener);
        alertWithView(v, getString(R.string.search), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String startDate = tv_start_date.getText().toString();
                String endDate = tv_end_date.getText().toString();
                if (TimeHelper.getYMD2Date(startDate).after(TimeHelper.getYMD2Date(endDate))) {
                    t("StartDate can't after EndDate");
                    return;
                }
                getMedicalFlaborList(startDate, endDate);
            }
        }, null);
    }

    private View.OnClickListener showDatePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDatePicker((TextView) v);
        }
    };

    private void showDatePicker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        final Calendar c_result = Calendar.getInstance();
        c.setTime(TimeHelper.getYMD2Date(tv.getText().toString()));

        DatePicker dp = new DatePicker(this);
        dp.init(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c_result.set(year, monthOfYear, dayOfMonth);
                    }
                });
        // set min date
        c.add(Calendar.MONTH, -1);
        dp.setMinDate(c.getTimeInMillis());
        // set max date
        c.add(Calendar.MONTH, 1);
        dp.setMaxDate(c.getTimeInMillis());
        // show
        alertWithView(dp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(TimeHelper.getDate2TMD(c_result.getTime()));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_medical_list_search:
                showSearchDialog();
                break;
            case R.id.iv_medical_list_add:
                openActivity(MedicalRecordActivity.class);
                break;
        }
    }


}

