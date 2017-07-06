package goldenbrother.gbmobile.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import goldenbrother.gbmobile.R;

/**
 * Created by asus on 2016/7/12.
 */
public abstract class IAsyncTask extends AsyncTask<JSONObject, Integer, String> {
    // progress dialog
//    private static final String MSG = "Loading...";
    private ProgressDialog pd;
    private String msg = "";
    private boolean isShow = true;
    // content
    private final Context context;
    // params
    private JSONObject json;
    // target url
    private String url;
    // handle common
    private boolean common = true;
    // result code
    private int result;

    public IAsyncTask(Context context, JSONObject json, String url) {
        this.context = context;
        this.json = json;
        this.url = url;
        this.msg = context.getString(R.string.loading);
    }

    public IAsyncTask(Context context, JSONObject json, String url, boolean common) {
        this(context, json, url);
        this.common = common;
    }

    public void setMessage(String message) {
        pd.setMessage(message);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isShow) {
            pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setMessage(msg);
            pd.show();
        }
    }

    public JSONObject getJSONObject() {
        return json;
    }

    public int getResult() {
        return result;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        if (!EnvironmentHelper.isNetWork(context)) {
            JSONObject j = new JSONObject();
            try {
                j.put("success", ApiResultHelper.NO_NETWORK);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return j.toString();
        }
        return OkHttpHelper.getInstance().postJSON(url, json);
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (isShow) {
            pd.dismiss();
        }
        if (common)
            result = ApiResultHelper.common(context, response);
    }
}