package goldenbrother.gbmobile.fcm;

import android.util.Log;

import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by asus on 2016/12/10.
 */

public class GBInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        LogHelper.d(token);
        save(token);
    }

    private void save(String token) {
        SPHelper.getInstance(getApplicationContext()).setGCMID(token);
    }
}