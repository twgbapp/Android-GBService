package goldenbrother.gbmobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ServiceChatRVAdapter;
import goldenbrother.gbmobile.fcm.FCMNotice;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.ServiceGroupMember;
import goldenbrother.gbmobile.model.ServiceTimePointModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;
import goldenbrother.gbmobile.sqlite.DAOServiceGroupMember;
import goldenbrother.gbmobile.sqlite.DAOServiceTimePoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServiceFragment extends CommonFragment implements View.OnClickListener {

    // instance
    private FragmentActivity activity;
    // ui
    private RecyclerView rv;
    private EditText et_content;
    // data
    private int serviceGroupID;
    private ArrayList<ServiceChatModel> list_group_chat;

    public static ServiceFragment getInstance(int serviceGroupID) {
        ServiceFragment f = new ServiceFragment();
        Bundle b = new Bundle();
        b.putInt("serviceGroupID", serviceGroupID);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_chat, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        rv = v.findViewById(R.id.rv_service_chat);
        et_content = v.findViewById(R.id.et_service_chat_content);
        v.findViewById(R.id.tv_service_chat_send).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get extra
        serviceGroupID = getArguments().getInt("serviceGroupID", -1);
        // get activity
        activity = getActivity();
        // init
        FCMNotice.getInstance().setOnMessageReceivedListener(new FCMNotice.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(String s) {
                handler.sendEmptyMessage(0);
            }
        });
        // initListView
        list_group_chat = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv.setAdapter(new ServiceChatRVAdapter(activity, list_group_chat));
        // get Local Chat
        loadLocalChat();
        // get Cloud Chat
        loadCloudChat();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadCloudChat();
        }
    };

    private void addGroupChat() {
        String content = et_content.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        try {
            JSONObject j = new JSONObject();
            j.put("action", "addGroupChat");
            j.put("serviceGroupID", serviceGroupID);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("content", content);
            j.put("chatDate", TimeHelper.getStandard());
            j.put("logStatus", true);
            if (!isSending) {
                isSending = true;
                new AddServiceChat(activity, j, URLHelper.HOST, content).execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isSending = false;

    private class AddServiceChat extends IAsyncTask {

        private String content;

        AddServiceChat(Context context, JSONObject json, String url, String content) {
            super(context, json, url);
            this.content = content;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            isSending = false;
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // clear editText
                        et_content.setText("");
                        // reload
                        loadCloudChat();
                        // push message
                        pushMessage(content);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void pushMessage(String content) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "pushMessage");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("serviceGroupID", serviceGroupID);
            j.put("content", content);
            j.put("logStatus", true);
            new PushMessage(activity, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class PushMessage extends IAsyncTask {

        PushMessage(Context context, JSONObject json, String url) {
            super(context, json, url, false);
            setShow(false);
        }
    }

    private void loadCloudChat() {
        try {
            DAOServiceTimePoint daoGroupTimePoint = new DAOServiceTimePoint(activity);
            ServiceTimePointModel gtp = daoGroupTimePoint.get(serviceGroupID);
            if (gtp == null) {
                gtp = new ServiceTimePointModel(serviceGroupID, TimeHelper.getInitTime());
                daoGroupTimePoint.insertOrUpdate(gtp);
            }
            JSONObject j = new JSONObject();
            j.put("action", "getGroupChat");
            j.put("serviceGroupID", serviceGroupID);
            j.put("startChatDate", TimeHelper.addMinute(gtp.getTimePoint(), -10));
            j.put("endChatDate", TimeHelper.addMinute(TimeHelper.getStandard(), 10));
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadCloudChat(activity, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadCloudChat extends IAsyncTask {
        private ArrayList<ServiceChatModel> list;

        LoadCloudChat(Context context, JSONObject json, String url) {
            super(context, json, url);
            this.list = new ArrayList<>();
            setShow(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getServiceChat(response, list);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (!list.isEmpty()) {
                            insertServiceChat(list);
                            insertServiceGroupMember(list);
                            updateServiceTimePoint();
                            loadLocalChat();
                        }
                    }
                    break;
            }
        }

        private void insertServiceChat(ArrayList<ServiceChatModel> list) {
            DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
            for (ServiceChatModel gc : list) {
                daoGroupChat.insert(gc);
                LogHelper.d("CHAT:" + gc.getContent());
            }
        }

        private void insertServiceGroupMember(ArrayList<ServiceChatModel> list) {
            Set<String> userIds = new HashSet<>();
            userIds.add(RoleInfo.getInstance().getUserID());
            for (ServiceChatModel item : list) {
                userIds.add(item.getUserID());
                LogHelper.d("MEMBER:" + item.getUserID());
            }
            DAOServiceGroupMember daoServiceGroupMember = new DAOServiceGroupMember(activity);
            for (String userId : userIds) {
                if (daoServiceGroupMember.get(serviceGroupID, userId) == null) {
                    boolean su = daoServiceGroupMember.insert(new ServiceGroupMember(serviceGroupID, userId));
                    LogHelper.d("MEMBER2:" + su + "-" + serviceGroupID + "-" + userId);
                }
            }
        }

        private void updateServiceTimePoint() {
            try {
                DAOServiceTimePoint daoGroupTimePoint = new DAOServiceTimePoint(activity);
                ServiceTimePointModel item = daoGroupTimePoint.get(serviceGroupID);
                item.setTimePoint(TimeHelper.addMinute(getJSONObject().getString("endChatDate"), -10));
                LogHelper.d("TIME:" + item.getServiceGroupID() + "-" + item.getTimePoint());
                daoGroupTimePoint.insertOrUpdate(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalChat() {
        list_group_chat.clear();
        list_group_chat.addAll(new DAOServiceChat(activity).get(serviceGroupID));
        if (!RoleInfo.getInstance().isLabor() && !list_group_chat.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("serviceGroupID", serviceGroupID);
            intent.putExtra("lastChat", list_group_chat.get(list_group_chat.size() - 1).getContent());
            activity.setResult(Activity.RESULT_OK, intent);
        }
        updateAdapter();
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
        rv.scrollToPosition(list_group_chat.size() - 1);
    }

    @Override
    public void onClick(View v) {
        EnvironmentHelper.hideKeyBoard(activity, v);
        switch (v.getId()) {
            case R.id.tv_service_chat_send:
                if (!isSending) {
                    addGroupChat();
                } else {
                    t(R.string.sending);
                }
                break;
        }
    }
}