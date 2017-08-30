package goldenbrother.gbmobile.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.MobileServiceActivity;
import goldenbrother.gbmobile.activity.ServiceChatActivity;
import goldenbrother.gbmobile.adapter.ServiceGroupListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServiceListFragment extends CommonFragment {
    // activity
    private MobileServiceActivity activity;
    public static ServiceListFragment f;
    // ui
    private SwipeRefreshLayout srl;
    private ListView lv;
    // data
    private ArrayList<Integer> list_service_group_id;
    private ArrayList<ServiceChatModel> list_service_chat;

    public static ServiceListFragment createInstance() {
        f = new ServiceListFragment();
        return f;
    }

    public static ServiceListFragment getInstance() {
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_list, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        srl = (SwipeRefreshLayout) v.findViewById(R.id.srl_service_list);
        lv = (ListView) v.findViewById(R.id.lv_service_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get activity
        activity = (MobileServiceActivity) getActivity();
        // init
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
        lv.setAdapter(new ServiceGroupListAdapter(activity, list_service_chat));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open
                Intent intent = new Intent();
                intent.setClass(activity, ServiceChatActivity.class);
                intent.putExtra("serviceGroupID", list_service_chat.get(position).getServiceGroupID());
                intent.putExtra("userID", list_service_chat.get(position).getUserID());
                intent.putExtra("userName", list_service_chat.get(position).getUserName());
                activity.startActivityForResult(intent, MobileServiceActivity.REQUEST_SERVICE_CHAT);
                // set read
                list_service_chat.get(position).setChatCount(0);
                updateAdapter();
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    srl.setEnabled(true);
                } else {
                    srl.setEnabled(false);
                }
            }
        });
        // load local group list
        loadLocalGroupList();
        // load cloud group list
        getGroupListNos();
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
        DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
        for (ServiceChatModel gc : list_service_chat) {
            int chatCount = daoGroupChat.getCount(gc.getServiceGroupID());
            if (gc.getChatCount() != 0) {
                gc.setChatCount(gc.getChatCount() - chatCount);
            }
        }
    }

    public void receiveMessage(String content) {
        getGroupListNos();
    }

    private void updateAdapter() {
        ((ServiceGroupListAdapter) lv.getAdapter()).notifyDataSetChanged();
    }

    private void loadLocalGroupList() {
        list_service_chat.clear();
        list_service_chat.addAll(new DAOServiceChat(activity).getAllGroupBy());
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