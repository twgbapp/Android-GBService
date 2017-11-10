package goldenbrother.gbmobile.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.MobileServiceActivity;
import goldenbrother.gbmobile.activity.ServiceChatActivity;
import goldenbrother.gbmobile.adapter.ServiceGroupRVAdapter;
import goldenbrother.gbmobile.fcm.FCMNotice;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.PackageHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.ServiceGroupMember;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;
import goldenbrother.gbmobile.sqlite.DAOServiceGroupMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceListFragment extends CommonFragment {
    // activity
    private MobileServiceActivity activity;
    // ui
    private SwipeRefreshLayout srl;
    private RecyclerView rv;
    // data
    private ArrayList<Integer> list_service_group_id;
    private ArrayList<ServiceChatModel> list_service_chat;

    public static ServiceListFragment createInstance() {
        return new ServiceListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_list, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        srl = v.findViewById(R.id.srl_service_list);
        rv = v.findViewById(R.id.rv_service_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get activity
        activity = (MobileServiceActivity) getActivity();
        // init
        FCMNotice.getInstance().setOnMessageReceivedListener(new FCMNotice.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(String s) {
                handler.sendEmptyMessage(0);
            }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                getGroupListNos();
            }
        });
        srl.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // init ListView
        list_service_group_id = new ArrayList<>();
        list_service_chat = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new ServiceGroupRVAdapter(activity, list_service_chat, this));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItem = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                onLoadMore(totalItem, lastVisibleItem);
            }
        });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    srl.setEnabled(true);
                } else {
                    srl.setEnabled(false);
                }
            }
        });
        getLocalGroupList();
        getGroupListNos();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getGroupListNos();
        }
    };

    public void onItemClick(ServiceChatModel item) {
        // open
        Intent intent = new Intent();
        intent.setClass(activity, ServiceChatActivity.class);
        intent.putExtra("serviceGroupID", item.getServiceGroupID());
        intent.putExtra("userID", item.getUserID());
        intent.putExtra("userName", item.getUserName());
        activity.startActivityForResult(intent, MobileServiceActivity.REQUEST_SERVICE_CHAT);
        // set read
        int chatUnReadCount = SPHelper.getChatUnReadCount(activity) - item.getChatCount();
        SPHelper.setChatUnReadCount(activity, chatUnReadCount);
        PackageHelper.setBadge(activity, SPHelper.getUnReadCount(activity));
        item.setChatCount(0);
        updateAdapter();
    }

    public void updateChat(int serviceGroupID, String lastChat) {
        if (list_service_chat == null) return;
        for (ServiceChatModel gc : list_service_chat) {
            if (gc.getServiceGroupID() == serviceGroupID) {
                gc.setContent(lastChat);
                updateAdapter();
                break;
            }
        }
    }

    private void countRead() {
        int unreadCount = 0;
        DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
        for (ServiceChatModel gc : list_service_chat) {
            int chatCount = daoGroupChat.getCount(gc.getServiceGroupID());
            if (gc.getChatCount() != 0) {
                unreadCount += gc.getChatCount() - chatCount;
                gc.setChatCount(gc.getChatCount() - chatCount);
            }
        }
        SPHelper.setChatUnReadCount(activity, unreadCount);
        PackageHelper.setBadge(activity, SPHelper.getUnReadCount(activity));
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    private void getLocalGroupList() {

        // find all your ServiceGroupID
        List<ServiceGroupMember> members = new DAOServiceGroupMember(activity).filterByUserId(RoleInfo.getInstance().getUserID());
        List<Integer> serviceGroupIds = new ArrayList<>();
        for (ServiceGroupMember item : members){
            serviceGroupIds.add(item.getServiceGroupID());
        }
        list_service_chat.clear();
        list_service_chat.addAll(new DAOServiceChat(activity).getLastChatList(serviceGroupIds));
        updateAdapter();
    }

    private void getGroupListNos() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getGroupListNos");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetGroupListNos(activity, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetGroupListNos extends IAsyncTask {

        GetGroupListNos(Context context, JSONObject json, String url) {
            super(context, json, url);
            setShow(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_service_group_id.clear();
            list_service_chat.clear();
            updateAdapter();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getGroupListNos(response, list_service_group_id);
                    if (result == ApiResultHelper.SUCCESS) {
                        index = -1;
                        getGroupList();
                    } else {
                        t(R.string.empty);
                    }
                    break;
            }
        }
    }

    private void getGroupList() {
        try {
            ArrayList<Integer> nos = nextLoadNos();
            JSONObject j = new JSONObject();
            j.put("action", "getGroupList");
            JSONArray arr = new JSONArray();
            for (Integer no : nos) {
                arr.put(no);
            }
            j.put("serviceGroupIDs", arr);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            if (!nos.isEmpty()) {
                loading = true;
                new GetGroupList(activity, j, URLHelper.HOST).execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetGroupList extends IAsyncTask {

        GetGroupList(Context context, JSONObject json, String url) {
            super(context, json, url);
            setShow(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            loading = false;
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getGroupList(response, list_service_chat);
                    if (result == ApiResultHelper.SUCCESS) {
                        countRead();
                        index = index + onceLoadCount > list_service_group_id.size() - 1 ? list_service_group_id.size() - 1 : index + onceLoadCount;
                        updateAdapter();
                    } else {
                        t(R.string.empty);
                    }
                    break;
            }
        }
    }

    public void showSearchDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setTitle(R.string.search);
        final EditText et = new EditText(activity);
        b.setView(et);
        b.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                // keyword
//                String keyword = et.getText().toString();
//                if (keyword.isEmpty()) {
//                    Toast.makeText(activity, R.string.can_not_be_empty, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // list of contain 'keyword'
//                ArrayList<ServiceChatModel> list = new ArrayList<>();
//                for (ServiceChatModel gm : list_service_chat) {
//                    if (gm.getWorkerNo().contains(keyword) || gm.getUserName().contains(keyword)) {
//                        list.add(gm);
//                    }
//                }
//                list_service_chat_show.clear();
//                list_service_chat_show.addAll(list);
//                updateAdapter();
//                // change title
//                activity.changeSearchTitle(keyword);
            }
        });
        b.setNegativeButton(R.string.cancel, null);
        b.show();
    }

    // load more
    private static final int remainToLoadMore = 1; //
    private static final int onceLoadCount = 10;
    private boolean loading = false;
    private int index = -1;

    private void onLoadMore(int totalItemCount, int lastVisibleItem) {
        /*
        *       1.total不能為0
        *       2.當進入"剩餘範圍(1)"
        *       3.不能正在載入狀態
        *       4.id數不等於總數(代表所有id都載完了)
        * */
        if (totalItemCount != 0 && (lastVisibleItem + remainToLoadMore) >= totalItemCount &&
                !loading && list_service_group_id.size() != totalItemCount) {
            getGroupList();
        }
    }

    private ArrayList<Integer> nextLoadNos() {
        ArrayList<Integer> r = new ArrayList<>();
        int start = index + 1;
        int end = index + onceLoadCount > list_service_group_id.size() - 1 ? list_service_group_id.size() - 1 : index + onceLoadCount;
        for (int i = start; i <= end; i++) {
            r.add(list_service_group_id.get(i));
        }
        return r;
    }
}