package goldenbrother.gbmobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ServiceGroupChatListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.helper.UtilHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.ServiceTimePointModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;
import goldenbrother.gbmobile.sqlite.DAOServiceTimePoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by asus on 2016/10/3.
 */

public class ServiceFragment extends CommonFragment implements View.OnClickListener {
    // instance
    private FragmentActivity activity;
    public static ServiceFragment f;
    // ui
    private ListView lv_chat;
    private EditText et_content;
    private ImageView iv_send;
    // data
    private int serviceGroupID;
    private ArrayList<ServiceChatModel> list_group_chat;

    public static ServiceFragment getInstance(int serviceGroupID) {
        f = new ServiceFragment();
        Bundle b = new Bundle();
        b.putInt("serviceGroupID", serviceGroupID);
        f.setArguments(b);
        return f;
    }

    public static ServiceFragment getInstance() {
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_chat, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        lv_chat = (ListView) v.findViewById(R.id.lv_service_chat);
        et_content = (EditText) v.findViewById(R.id.et_service_chat_content);
        iv_send = (ImageView) v.findViewById(R.id.iv_service_chat_send);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get extra
        serviceGroupID = getArguments().getInt("serviceGroupID", -1);
        // get activity
        activity = getActivity();
        // initListView
        list_group_chat = new ArrayList<>();
        lv_chat.setAdapter(new ServiceGroupChatListAdapter(activity, list_group_chat));
        // listener
        iv_send.setOnClickListener(this);
        // get Local Chat
        loadLocalChat();
        // get Cloud Chat
        loadCloudChat();
    }

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
            new AddServiceChat(activity, j, URLHelper.HOST, content).execute();
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
        protected void onPreExecute() {
            super.onPreExecute();
            isSending = true;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            isSending = false;
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addGroupChat(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // clear editText
                        et_content.setText("");
                        // reload
                        loadCloudChat();
                        // push message
                        pushMessage(content);
                    } else {
                        ToastHelper.t(activity, "Add Chat Fail");
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
            super(context, json, url,false);
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
                            // save chat to local
                            DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
                            for (ServiceChatModel gc : list) {
                                daoGroupChat.insert(gc);
                            }
                            // save GroupTimePoint
                            try {
                                DAOServiceTimePoint daoGroupTimePoint = new DAOServiceTimePoint(activity);
                                ServiceTimePointModel gtp = daoGroupTimePoint.get(serviceGroupID);
                                gtp.setTimePoint(TimeHelper.addMinute(getJSONObject().getString("endChatDate"), -10));
                                daoGroupTimePoint.insertOrUpdate(gtp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // reload
                            loadLocalChat();
                        }
                    }
                    break;
            }
        }
    }

    private void loadLocalChat() {
        DAOServiceChat daoServiceChat = new DAOServiceChat(activity);
        list_group_chat.clear();
        list_group_chat.addAll(daoServiceChat.get(serviceGroupID));
        if (!RoleInfo.getInstance().isLabor() && !list_group_chat.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("serviceGroupID", serviceGroupID);
            intent.putExtra("lastChat", list_group_chat.get(list_group_chat.size() - 1).getContent());
            activity.setResult(Activity.RESULT_OK, intent);
        }

        updateAdapter();
    }

    public void receiveMessage(String content) {
        loadCloudChat();
    }


    private void updateAdapter() {
        ServiceGroupChatListAdapter adapter = (ServiceGroupChatListAdapter) lv_chat.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            lv_chat.setSelection(list_group_chat.size() - 1);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_service_chat_send:
                if (!isSending) {
                    EnvironmentHelper.hideKeyBoard(activity, view);
                    addGroupChat();
                } else {
                    t("Sending...");
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (f == null)
            f = this;
    }

    @Override
    public void onPause() {
        if (f != null)
            f = null;
        super.onPause();
    }
}