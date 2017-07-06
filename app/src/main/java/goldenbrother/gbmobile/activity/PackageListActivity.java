package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.PackageListAdapter;
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

public class PackageListActivity extends CommonActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // request
    public static final int REQUEST_TAKE_PHOTO = 12;
    public static final int REQUEST_QRCODE = 13;
    // ui
    private View tv_search;
    private ListView lv;
    // data
    private ArrayList<PackageModel> list_package;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        // ui reference
        tv_search = findViewById(R.id.tv_package_list_search);
        lv = (ListView) findViewById(R.id.lv_package_list);
        // listener
        tv_search.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        // init ListView
        list_package = new ArrayList<>();
        lv.setAdapter(new PackageListAdapter(this, list_package));
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
                        startActivityForResult(intent, REQUEST_QRCODE);
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
                                showReceivePackageDialog(p);
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

    // take picture
    private ImageView iv_tmp;
    private Uri tmp_uri; // tmp

    private void showReceivePackageDialog(final PackageModel p) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Package");
        final View v = LayoutInflater.from(this).inflate(R.layout.dialog_package_receive, null);
        final TextView tv_description = (TextView) v.findViewById(R.id.tv_dialog_package_receive_description);
        final TextView tv_arrive_date = (TextView) v.findViewById(R.id.tv_dialog_package_receive_arrive_date);
        final View bt_take_picture = v.findViewById(R.id.bt_dialog_package_receive_take_picture);
        iv_tmp = (ImageView) v.findViewById(R.id.iv_dialog_package_receive_pickpicture);
        // set description
        tv_description.setText(p.getDescription());
        // set arrive date
        tv_arrive_date.setText(p.getArriveDate());
        // listener
        bt_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // picture tmp file
                File file = new File(FileHelper.getAppDir(PackageListActivity.this) + "/receive_package.jpg");
                tmp_uri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tmp_uri);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        });
        b.setView(v);
        b.setPositiveButton("RECEIVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EnvironmentHelper.hideKeyBoard(PackageListActivity.this, v);
                if (tmp_uri == null) {
                    ToastHelper.t(PackageListActivity.this, "No Picture");
                    return;
                }
                try {
                    String baseStr = BitmapHelper.getUploadServerBitmapString(PackageListActivity.this, tmp_uri);
                    p.setBaseStr(baseStr);
                    receivePackage(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.show();
    }

    private void receivePackage(PackageModel p) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "receivePackage");
            j.put("packageID", p.getPackageID());
            j.put("baseStr", p.getBaseStr());
            j.put("userID", RoleInfo.getInstance().getUserID());
            new ReceivePackage(this, j, URLHelper.HOST, p).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ReceivePackage extends IAsyncTask {
        private PackageModel removed;

        ReceivePackage(Context context, JSONObject json, String url, PackageModel p) {
            super(context, json, url);
            removed = p;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.receivePackage(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        ToastHelper.t(PackageListActivity.this, "Success");
                        list_package.remove(removed);
                        updateAdapter();
                    } else {
                        ToastHelper.t(PackageListActivity.this, "Fail");
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        PackageListAdapter adapter = (PackageListAdapter) lv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showReceivePackageDialog(list_package.get(position));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_package_list_search:
                showSearchDialog();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    try {
                        Bitmap bmp = BitmapHelper.getUploadServerBitmap(this, tmp_uri);
                        if (iv_tmp != null) {
                            iv_tmp.setImageBitmap(bmp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_QRCODE:
                    String text = data.getStringExtra("text");
                    if (text.length() == 6) {
                        loadPackageList("", text);
                    } else {
                        ToastHelper.t(this, "PickNumber length must be 6");
                    }
                    break;
            }
        }
    }
}
