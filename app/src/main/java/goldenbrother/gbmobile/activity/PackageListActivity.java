package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.PackageListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.PackageModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PackageListActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_PACKAGE_RESULT = 0;
    public static final int REQUEST_QR_CODE = 1;
    // ui
    private RecyclerView rv;
    // data
    private ArrayList<PackageModel> list_package;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        setUpBackToolbar(R.id.toolbar_package_list, R.id.tv_package_list_title, R.string.main_drawer_package);

        // ui reference
        findViewById(R.id.iv_package_list_search).setOnClickListener(this);
        rv = findViewById(R.id.rv_package_list);

        // init
        list_package = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PackageListRVAdapter(this, list_package));

        loadPackageList("", "");
    }

    private void showSearchDialog() {
        final String[] items = {getString(R.string.keyword), getString(R.string.qr_code)};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        inputKeywordDialog();
                        break;
                    case 1:
                        openActivityForResult(QRReaderActivity.class, REQUEST_QR_CODE);
                        break;
                }
            }
        });
        b.show();
    }

    private void inputKeywordDialog() {
        final EditText et_keyword = new EditText(this);

        alertWithView(et_keyword, getString(R.string.keyword), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EnvironmentHelper.hideKeyBoard(PackageListActivity.this, et_keyword);
                String keyword = et_keyword.getText().toString();
                if (keyword.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                loadPackageList(keyword, "");
            }
        }, null);
    }

    private void loadPackageList(String description, String pickNumber) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getPackageList");
            j.put("description", description);
            j.put("pickNumber", pickNumber);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadPackageList(this, j, description.isEmpty()).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class LoadPackageList extends IAsyncTask {

        private boolean byDescription;

        LoadPackageList(Context context, JSONObject json, boolean byDescription) {
            super(context, json);
            this.byDescription = byDescription;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadPackageList(response, list_package);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (byDescription) {

                        } else {
                            try {
                                PackageModel p = list_package.get(0);
                                p.setPickNumber(getJSONObject().getString("pickNumber"));
                                Bundle b = new Bundle();
                                b.putParcelable("package", p);
                                openActivityForResult(PackageResultActivity.class, REQUEST_PACKAGE_RESULT, b);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            list_package.clear();
                        }
                        updateAdapter();
                    } else {
                        t(R.string.empty);
                    }
                    break;
            }
        }
    }


    public void onItemClick(PackageModel item) {
        Bundle b = new Bundle();
        b.putParcelable("package", item);
        openActivityForResult(PackageResultActivity.class, REQUEST_PACKAGE_RESULT, b);
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_package_list_search:
                showSearchDialog();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PACKAGE_RESULT:
                    loadPackageList("", "");
                    break;
                case REQUEST_QR_CODE:
                    String code = data.getStringExtra("text");

                    if (code.length() != 6) {
                        t("Error : " + code);
                        return;
                    }

                    loadPackageList("", code);
                    break;
            }
        }
    }
}
