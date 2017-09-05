package goldenbrother.gbmobile.fcm;

/**
 * Created by haojun on 2017/9/5.
 */

public class FCMNotice {
    private static final FCMNotice ourInstance = new FCMNotice();

    public static FCMNotice getInstance() {
        return ourInstance;
    }

    private FCMNotice() {
    }

    private OnMessageReceivedListener mOnMessageReceivedListener;
    public interface OnMessageReceivedListener{
        void onMessageReceived(String s);
    }
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener){
        mOnMessageReceivedListener = listener;
    }
    public void notifyOnMessageReceived(String s){
        if(mOnMessageReceivedListener != null){
            mOnMessageReceivedListener.onMessageReceived(s);
        }
    }
}
