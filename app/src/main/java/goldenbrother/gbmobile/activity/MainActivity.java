package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.MainDrawerRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.EncryptHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.Discussion;
import goldenbrother.gbmobile.model.DrawerItem;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOEvent;
import goldenbrother.gbmobile.sqlite.DAOEventChat;
import goldenbrother.gbmobile.sqlite.DAOEventTimePoint;
import goldenbrother.gbmobile.sqlite.DAOService;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;
import goldenbrother.gbmobile.sqlite.DAOServiceTimePoint;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    // request
    public static final int REQUEST_PROFILE = 0;
    public static final int REQUEST_QUICK_REPAIR = 1;
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
    // take picture
    private Uri uriTakePicture;
    // ui
    private RecyclerView rv_drawer;
    private ImageView iv_banner,iv_picture;
    // banner
    private Handler handler;
    private ArrayList<Integer> list_banner;
    private boolean allowShowing = false;
    // data
    public static final String E_COMMERCE = "https://www.gbtake.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hockey  for APP Update Check
        checkForUpdates();
        // ui reference
        iv_picture = (ImageView) findViewById(R.id.iv_item_rv_main_drawer_head_picture);
        iv_banner = (ImageView) findViewById(R.id.iv_main_banner);
        findViewById(R.id.cv_main_mobile_service).setOnClickListener(this);
        findViewById(R.id.cv_main_life_information).setOnClickListener(this);
        findViewById(R.id.cv_main_e_commerce).setOnClickListener(this);
        findViewById(R.id.cv_main_satisfaction_survey).setOnClickListener(this);
        // init Toolbar
        initToolbar();
        // init Drawer
        initDrawer();
        // initBanner
        initBanner();
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void initToolbar() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // drawer connect
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initDrawer() {
        rv_drawer = (RecyclerView) findViewById(R.id.rv_main_navigation);
        rv_drawer.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<DrawerItem> list = new ArrayList<>();
        RoleInfo r = RoleInfo.getInstance();
        if (r.isLabor()) {
            list.add(new DrawerItem(R.drawable.ic_mobile_service, R.string.mobile_service, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_quick_repair, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_chat, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_event_list, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_life_information, R.string.main_drawer_life_information, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_announcement, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_club, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_satisfaction_survey, R.string.main_drawer_satisfaction_survey, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_e_commerce_big, R.string.main_drawer_e_commerce, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_language_w, R.string.language, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_exit, R.string.main_drawer_logout, DrawerItem.GROUP));
        } else {
            list.add(new DrawerItem(R.drawable.ic_mobile_service, R.string.mobile_service, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_event_list, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_online_setting, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_chart, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_repair_record, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_medical, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.discussion, DrawerItem.CHILD));
            list.add(new DrawerItem(R.drawable.ic_logout, R.string.main_drawer_package, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_life_information, R.string.main_drawer_life_information, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_club, R.string.main_drawer_club, DrawerItem.CHILD));

            list.add(new DrawerItem(R.drawable.ic_e_commerce_big, R.string.main_drawer_e_commerce, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_language_w, R.string.language, DrawerItem.GROUP));
            list.add(new DrawerItem(R.drawable.ic_exit, R.string.main_drawer_logout, DrawerItem.GROUP));
        }
        rv_drawer.setAdapter(new MainDrawerRVAdapter(this, list));
    }

    private void clearDB() {
        new DAOEvent(this).deleteAll();
        new DAOEventChat(this).deleteAll();
        new DAOEventTimePoint(this).deleteAll();
        new DAOService(this).deleteAll();
        new DAOServiceChat(this).deleteAll();
        new DAOServiceTimePoint(this).deleteAll();
    }

    private void showLanguageDialog() {
        String[] items = getResources().getStringArray(R.array.language);
        ad = alertCustomItems(0, null, items, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                ad.dismiss();
                alertWithView(null, getString(R.string.language_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setLanguage(position);
                    }
                }, null);
            }
        });
    }

    private void setLanguage(int i) {
        Locale locale = new Locale(new String[]{"en", "zh", "in", "vi", "th"}[i]);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        // restart
        Bundle b = new Bundle();
        openActivity(SplashActivity.class, b);
        finish();
    }

    public void onFunctionClick(String str) {
        Bundle b = new Bundle();
        if (str.equals(getString(R.string.main_drawer_event_list))) {
            b.putInt("position", 1);
            openActivity(MobileServiceActivity.class, b);
        } else if (str.equals(getString(R.string.main_drawer_chat))) {
            b.putInt("position", 0);
            openActivity(MobileServiceActivity.class, b);
        } else if (str.equals(getString(R.string.main_drawer_quick_repair))) {
            b.putBoolean("support", false);
            openActivityForResult(QuickRepairActivity.class, REQUEST_QUICK_REPAIR, b);
        } else if (str.equals(getString(R.string.support))) {
            b.putBoolean("support", true);
            openActivityForResult(QuickRepairActivity.class, REQUEST_QUICK_REPAIR, b);
        } else if (str.equals(getString(R.string.main_drawer_satisfaction_survey))) {
            openActivity(SatisfactionIssueActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_club))) {
            openActivity(ClubListActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_announcement))) {
            b.putInt("type", 0);
            b.putString("customerNo", LaborModel.getInstance().getCustomerNo());
            b.putString("flaborNo", LaborModel.getInstance().getFlaborNo());
            b.putString("nationCode", LaborModel.getInstance().getUserNationCode());
            openActivity(AnnouncementListActivity.class, b);
        } else if (str.equals(getString(R.string.main_drawer_logout))) {
            clearDB();
            b.putBoolean("isLogout", true);
            openActivity(SplashActivity.class, b);
            finish();
        } else if (str.equals(getString(R.string.main_drawer_online_setting))) {
            openActivity(OnLineSettingActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_package))) {
            openActivity(PackageListActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_chart))) {
            openActivity(ChartActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_repair_record))) {
            openActivity(RepairRecordActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_medical))) {
            openActivity(MedicalListActivity.class);
        } else if (str.equals(getString(R.string.discussion))) {
            openActivity(DiscussionListActivity.class);
        } else if (str.equals(getString(R.string.main_drawer_e_commerce))) {
            b.putString("url", E_COMMERCE);
            openActivity(WebViewActivity.class, b);
        } else if (str.equals(getString(R.string.language))) {
            showLanguageDialog();
        }
    }

    public void onDrawerItemClick(int strId) {
        onFunctionClick(getString(strId));
        closeDrawer();
    }

    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initBanner() {
        // init Data
        list_banner = new ArrayList<>();
        list_banner.add(R.drawable.banner1);
        list_banner.add(R.drawable.banner2);
        list_banner.add(R.drawable.banner3);
        // show
        allowShowing = true;
        handler = new Handler();
        handler.post(r);
    }

    private static final long REFRESH_BANNER_TIME = 4000;
    private int indexOfBanner = 0;

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (allowShowing && list_banner != null && !list_banner.isEmpty() && indexOfBanner < list_banner.size()) {
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) getResources().getDimension(R.dimen.imageview_main_top_height);
                Picasso.with(MainActivity.this).load(list_banner.get(indexOfBanner)).resize(w, h).centerCrop().into(iv_banner);
                indexOfBanner = indexOfBanner + 1 >= list_banner.size() ? 0 : indexOfBanner + 1;
                handler.postDelayed(r, REFRESH_BANNER_TIME);
            } else {
                indexOfBanner = 0;
            }
        }
    };

    private void stopShowAdvertising() {
        allowShowing = false;
    }

    private void startShowAdvertising() {
        if (list_banner != null && !list_banner.isEmpty() && handler != null) {
            allowShowing = true;
            handler.post(r);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startShowAdvertising();
    }

    @Override
    public void onPause() {
        stopShowAdvertising();
        if (handler != null)
            handler.removeCallbacks(r);
        super.onPause();
        unregisterManagers();//for Hockey(APP Update)
    }

    private void showMobileServiceDialog() {
        String[] items_flabor = {getString(R.string.main_drawer_chat),
                getString(R.string.main_drawer_quick_repair), getString(R.string.support), getString(R.string.main_drawer_event_list)};
        String[] items_manager = {getString(R.string.main_drawer_chat),
                getString(R.string.main_drawer_event_list), getString(R.string.main_drawer_online_setting),
                getString(R.string.main_drawer_chart), getString(R.string.main_drawer_repair_record),
                getString(R.string.main_drawer_medical),getString(R.string.discussion),
                getString(R.string.main_drawer_package)};
        final String[] items = RoleInfo.getInstance().isLabor() ? items_flabor : items_manager;
        ad = alertCustomItems(R.drawable.ic_mobile_service_big, getString(R.string.mobile_service), items, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ad.dismiss();
                onFunctionClick(items[i]);
            }
        });
    }

    private void showLifeInformationDialog() {
        String[] items_flabor = {getString(R.string.main_drawer_club), getString(R.string.main_drawer_announcement)};
        String[] items_manager = {getString(R.string.main_drawer_club)};
        final String[] items = RoleInfo.getInstance().isLabor() ? items_flabor : items_manager;
        ad = alertCustomItems(R.drawable.ic_life_information_big, getString(R.string.main_drawer_life_information), items, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ad.dismiss();
                onFunctionClick(items[i]);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.cv_main_mobile_service:
                showMobileServiceDialog();
                break;
            case R.id.cv_main_life_information:
                showLifeInformationDialog();
                break;
            case R.id.cv_main_e_commerce:
                b.putString("url", E_COMMERCE);
                openActivity(WebViewActivity.class, b);
                break;
            case R.id.cv_main_satisfaction_survey:
                if (RoleInfo.getInstance().isLabor()) {
                    openActivity(SatisfactionIssueActivity.class);
                } else {
                    t(R.string.main_drawer_satisfaction_survey);
                }
                break;
        }
    }

    private void changePassword(String oldUserPassword, String newUserPassword) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "changePassword");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("oldUserPassword", EncryptHelper.md5(oldUserPassword));
            j.put("newUserPassword", EncryptHelper.md5(newUserPassword));
            j.put("logStatus", true);
            new ChangePassword(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChangePassword extends IAsyncTask {

        ChangePassword(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private AlertDialog ad;

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPicture(BitmapHelper.resize(bmp, 300, 300));
            }
        }, null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", RoleInfo.getInstance().getUserID());
            j.put("baseStr", BitmapHelper.bitmap2JPGBase64(bmp));
            j.put("url", URLHelper.HOST);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new UploadImageTask(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageTask extends IAsyncTask {
        private HashMap<String, String> map;

        UploadImageTask(Context context, JSONObject json, String url) {
            super(context, json, url);
            map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.uploadPicture(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        updatePicture(map.get("path"));
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updatePicture(String path) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "updatePicture");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("path", path);
            j.put("logStatus", true);
            new UpdatePicture(this, j, URLHelper.HOST, path).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdatePicture extends IAsyncTask {

        private String path;

        UpdatePicture(Context context, JSONObject json, String url, String path) {
            super(context, json, url);
            this.path = path;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set picture
                        RoleInfo.getInstance().setUserPicture(path);
                        // save user info
                        SPHelper.getInstance(MainActivity.this).setUserInfo(RoleInfo.getInstance().getJSONObject());
                        t(R.string.success);
                        // get role instances
                        RoleInfo r = RoleInfo.getInstance();
                        // set picture
                        rv_drawer.getAdapter().notifyItemChanged(0);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    public void choosePicture() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(R.array.choose_picture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_FROM_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriTakePicture = FileProvider.getUriForFile(MainActivity.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(MainActivity.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    public void showChangePasswordDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_profile_change_password, null);
        final EditText et_old = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_old);
        final EditText et_new = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_new);
        final EditText et_confirm = (EditText) v.findViewById(R.id.et_dialog_profile_change_password_confirm);
        v.findViewById(R.id.tv_dialog_profile_change_password_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
        v.findViewById(R.id.tv_dialog_profile_change_password_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                String pwd_old = et_old.getText().toString();
                String pwd_new = et_new.getText().toString();
                String pwd_confirm = et_confirm.getText().toString();
                // check
                if (!pwd_new.equals(pwd_confirm)) {
                    t(R.string.error_confirm_password);
                    return;
                }
                // changePassword
                changePassword(pwd_old, pwd_new);
            }
        });
        ad = alertWithView(v, null, null);
    }

    public void openProfileActivity() {
        //closeDrawer();
        openActivityForResult(ProfileActivity.class, REQUEST_PROFILE);
        //showChangePasswordDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bundle b = new Bundle();
        switch (requestCode) {
            case REQUEST_PROFILE:
                rv_drawer.getAdapter().notifyItemChanged(0);
                break;
            case REQUEST_FROM_GALLERY:
                b.putString("uri", data.getData().toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_PHOTO:
                b.putString("uri", uriTakePicture.toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_CROP:
                showImage(BitmapHelper.file2Bitmap(new File(data.getStringExtra("path"))));
                break;
        }
    }

    public static final long DELAY_TIME = 2000L;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - lastBackPressTime < DELAY_TIME) {
                super.onBackPressed();
            } else {
                lastBackPressTime = System.currentTimeMillis();
                t(R.string.press_again_to_exit);
            }
        }
    }

}
