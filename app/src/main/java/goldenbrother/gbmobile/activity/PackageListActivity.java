package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.PackageListRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.PackageModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
        rv = (RecyclerView) findViewById(R.id.rv_package_list);
        // init RecyclerView
        list_package = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PackageListRVAdapter(this, list_package));
    }

    private void showSearchDialog() {
        final String[] items = {"Description", "PickNumber"};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showSearchByDescription();
                        break;
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(PackageListActivity.this, QRReaderActivity.class);
                        startActivityForResult(intent, REQUEST_QR_CODE);
                        break;
                }
            }
        });
        b.show();
    }

    private void showSearchByDescription() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Description");
        final EditText et = new EditText(this);
        b.setView(et);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EnvironmentHelper.hideKeyBoard(PackageListActivity.this, et);
                String keyword = et.getText().toString();
                if (keyword.isEmpty()) {
                    ToastHelper.t(PackageListActivity.this, "Can't be empty");
                    return;
                }
                loadPackageList(keyword, "");
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.show();
    }

    private void loadPackageList(String description, String pickNumber) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getPackageList");
            j.put("description", description);
            j.put("pickNumber", pickNumber);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadPackageList(this, j, URLHelper.HOST, !description.isEmpty()).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class LoadPackageList extends IAsyncTask {

        private boolean byDescription;

        LoadPackageList(Context context, JSONObject json, String url, boolean byDescription) {
            super(context, json, url);
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
                        ToastHelper.t(PackageListActivity.this, "No Match Package");
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
                    PackageModel mPackage = data.getParcelableExtra("package");
                    list_package.remove(mPackage);
                    updateAdapter();
                    break;
                case REQUEST_QR_CODE:
                    String text = data.getStringExtra("text");

                    if (text.length() != 6) {
                        t("Error:" + text);
                        return;
                    }

                    loadPackageList("", text);
                    break;
            }
        }
    }
}
