package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.chart.MPUtil;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ManagerModel;
import goldenbrother.gbmobile.model.RepairKindNumberModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChartActivity extends CommonActivity {

    // ui
    BarChart chatView;
    //
    private String[] dataListName;
    private ArrayList<Float> dataList = new ArrayList<>();
    // data
    private ArrayList<RepairKindNumberModel> list_repair_kind_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        setUpBackToolbar(R.id.toolbar, R.string.main_drawer_chart);
        // ui reference
        chatView = findViewById(R.id.cv_chart);
        // init
        list_repair_kind_number = new ArrayList<>();
        // load
        loadRepairList(ManagerModel.getInstance().getUserNationCode(), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    }

    private void loadRepairList(String nationCode, int... repairKindIDs) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairKindNumber");
            String startDate = "2017-01-01";
            String endDateStr = TimeHelper.getStandard().substring(0, 10);
            j.put("startDateStr", startDate);
            j.put("endDateStr", endDateStr);
            j.put("nationCode", nationCode);
            JSONArray arr = new JSONArray();
            for (int id : repairKindIDs) {
                arr.put(id);
            }
            j.put("repairKindIDs", arr.toString());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadRepairList(this, j).execute();//t
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadRepairList extends IAsyncTask {

        LoadRepairList(Context context, JSONObject json) {
            super(context, json);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadRepairKindNumber(response, list_repair_kind_number);
                    if (result == ApiResultHelper.SUCCESS) {
                        dataListName = new String[list_repair_kind_number.size()];
                        int countX = 0;
                        for (RepairKindNumberModel m : list_repair_kind_number) {
                            dataListName[countX] = m.getParentContent();//X軸
                            dataList.add((float) m.getNumber());//Y軸
                            countX++;
                        }
                        BarData barData2 = new BarData(dataListName, MPUtil.getDataSet(ChartActivity.this, dataList));
                        MPUtil.drawChart(ChartActivity.this, chatView, barData2);
                    }
                    break;
            }
        }
    }
}
