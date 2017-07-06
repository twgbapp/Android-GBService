package goldenbrother.gbmobile.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

public class CommonFragment extends Fragment {

    private ProgressDialog pd;

    protected AlertDialog alertWithView(View v, DialogInterface.OnClickListener posi, DialogInterface.OnClickListener nega) {
        return alertWithView(v, null, posi, nega);
    }

    protected AlertDialog alertWithView(View v, String title, DialogInterface.OnClickListener posi, DialogInterface.OnClickListener nega) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        if (title != null) b.setTitle(title);
        b.setView(v);
        if (posi != null || nega != null) {
            b.setPositiveButton("OK", posi);
            b.setNegativeButton("CANCEL", nega);
        }
        return b.show();
    }

    protected AlertDialog alertWithItems(String[] items, DialogInterface.OnClickListener click) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setItems(items, click);
        return b.show();
    }

    protected void showLoadingDialog() {
        showLoadingDialog(null);
    }

    protected void showLoadingDialog(String message) {
        if (pd == null) {
            pd = new ProgressDialog(getActivity());
            pd.setIndeterminate(true);
            pd.setCancelable(false);
        }
        pd.setMessage(message != null ? message : "Loading...");
        pd.show();
    }

    protected void dismissLoadingDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    protected void t(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
