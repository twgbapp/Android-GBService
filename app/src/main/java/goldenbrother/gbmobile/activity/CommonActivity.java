package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import goldenbrother.gbmobile.R;


public class CommonActivity extends AppCompatActivity {

    private ProgressDialog pd;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected AlertDialog alertWithView(View v, DialogInterface.OnClickListener posi, DialogInterface.OnClickListener nega) {
        return alertWithView(v, null, posi, nega);
    }

    protected AlertDialog alertWithView(View v, String title, DialogInterface.OnClickListener posi, DialogInterface.OnClickListener nega) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        if (title != null) b.setTitle(title);
        b.setView(v);
        if (posi != null || nega != null) {
            b.setPositiveButton(R.string.confirm, posi);
            b.setNegativeButton(R.string.cancel, nega);
        }
        return b.show();
    }

    protected AlertDialog alertWithItems(String[] items, DialogInterface.OnClickListener click) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(items, click);
        return b.show();
    }

    protected void showLoadingDialog() {
        showLoadingDialog(null);
    }

    protected void showLoadingDialog(String message) {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
        }
        pd.setMessage(message != null ? message : getString(R.string.loading));
        pd.show();
    }

    protected void dismissLoadingDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    protected void openActivity(Class activityClass) {
        openActivity(activityClass, null);
    }

    protected void openActivity(Class activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void openActivityForResult(Class activityClass, int request) {
        openActivityForResult(activityClass, request, null);
    }

    protected void openActivityForResult(Class activityClass, int request, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, request);
    }

    protected void t(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void t(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
