package goldenbrother.gbmobile.helper;


import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by asus on 2016/10/21.
 */

public class OkHttpHelper {
    public static final String TAG = "OkHttpManager$TAG";
    // const
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // instance
    private static OkHttpHelper manager;
    // client
    private OkHttpClient client;

    private OkHttpHelper() {
        client = new OkHttpClient();
        client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpHelper getInstance() {
        if (manager == null) {
            manager = new OkHttpHelper();
        }
        return manager;
    }

    public String postJSON(String url, JSONObject j) {
        return postJSON(url, j.toString());
    }

    public String postJSON(String url, String json) {
        try {
            LogHelper.d("params:"+json);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            LogHelper.d("response:"+str);
            return str;
        } catch (SocketTimeoutException se) {
            return "{\"success\":" + ApiResultHelper.TIME_OUT + "}";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getUUID() {
        return RoleInfo.getInstance().getUserID() + "-" + UUID.randomUUID().toString();
    }
}
