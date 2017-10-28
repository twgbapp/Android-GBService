package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.RepairRecordModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class RepairRecordContentActivity extends CommonActivity {

    // ui
    private View rl_picture;
    private ImageView iv_picture;
    private TextView tv_proc_result;
    // data
    private RepairRecordModel repairRecordModel;
    // extra
    private int rrsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_record_content);
        // ui
        rl_picture = findViewById(R.id.rl_repair_record_content_picture);
        iv_picture = findViewById(R.id.iv_repair_record_content_picture);
        tv_proc_result = findViewById(R.id.tv_repair_record_content_proc_result);
        // extra
        rrsNo = getIntent().getIntExtra("rrsNo", -1);
        // get record content
        getRepairRecord(rrsNo);
    }

    public void getRepairRecord(int rrsno) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getRepairRecord");
            j.put("rrsno", rrsno);
            j.put("nationCode", RoleInfo.getInstance().getUserNationCode());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetRepairRecord(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetRepairRecord extends IAsyncTask {

        GetRepairRecord(Context context, JSONObject json, String url) {
            super(context, json, url);
            repairRecordModel = new RepairRecordModel();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getRepairRecord(response, repairRecordModel);
                    if (result == ApiResultHelper.SUCCESS) {
                        tv_proc_result.setText(repairRecordModel.getProcResult());
                    } else {
                        ToastHelper.t(RepairRecordContentActivity.this, "empty");
                    }
                    break;
            }
        }
    }
}
