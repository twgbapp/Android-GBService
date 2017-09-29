package goldenbrother.gbmobile.fcm;

import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GBInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        LogHelper.d(token);
        save(token);
    }

    private void save(String token) {
        SPHelper.setFcmToken(getApplicationContext(), token);
    }
}