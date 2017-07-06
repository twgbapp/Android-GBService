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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by asus on 2016/10/3.
 */

public class ServiceListFragment extends CommonFragment {
    // activity
    private MobileServiceActivity activity;
    public static ServiceListFragment f;
    // ui
    private SwipeRefreshLayout srl;
    private ListView lv;
    // data
    private List<ServiceChatModel> list_service_chat;
    private ArrayList<ServiceChatModel> list_service_chat_show;

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

    // srl_service_list
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
                list_service_chat.clear();
                list_service_chat_show.clear();
                updateAdapter();
                loadCloudGroupList();
            }
        });
        srl.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // init ListView
        list_service_chat = new ArrayList<>();
        list_service_chat_show = new ArrayList<>();
        lv.setAdapter(new ServiceGroupListAdapter(activity, list_service_chat_show));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open
                Intent intent = new Intent();
                intent.setClass(activity, ServiceChatActivity.class);
                intent.putExtra("serviceGroupID", list_service_chat_show.get(position).getServiceGroupID());
                intent.putExtra("userID", list_service_chat_show.get(position).getWriterID());
                intent.putExtra("userName", list_service_chat_show.get(position).getWriterName());
                activity.startActivityForResult(intent, MobileServiceActivity.REQUEST_SERVICE_CHAT);
                // set read
                list_service_chat_show.get(position).setChatCount(0);
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
        loadCloudGroupList();
    }

    public void updateChat(int serviceGroupID, String lastChat) {
        for (ServiceChatModel gc : list_service_chat_show) {
            if (gc.getServiceGroupID() == serviceGroupID) {
                gc.setContent(lastChat);
                updateAdapter();
                break;
            }
        }
    }

    private void countRead() {
        DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
        for (ServiceChatModel gc : list_service_chat_show) {
            int chatCount = daoGroupChat.getCount(gc.getServiceGroupID());
            if (gc.getChatCount() != 0) {
                gc.setChatCount(gc.getChatCount() - chatCount);
            }
        }
    }

    public void receiveMessage(String content) {
        loadCloudGroupList();
    }

    private void updateAdapter() {
        ServiceGroupListAdapter adapter = (ServiceGroupListAdapter) lv.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadLocalGroupList() {
        DAOServiceChat daoGroupChat = new DAOServiceChat(activity);
        list_service_chat_show.clear();
        list_service_chat_show.addAll(daoGroupChat.getAllGroupBy());
        updateAdapter();
    }

    public void loadCloudGroupList() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getGroupList");
            j.put("userID", RoleInfo.getInstance().getUserID());
            new LoadCloudGroupList(activity, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadCloudGroupList extends IAsyncTask {

        LoadCloudGroupList(Context context, JSONObject json, String url) {
            super(context, json, url);
            setShow(false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadCloudGroupList(response, list_service_chat);
                    if (result == ApiResultHelper.SUCCESS) {
                        // sort
                        Collections.sort(list_service_chat, new Comparator<ServiceChatModel>() {
                            @Override
                            public int compare(ServiceChatModel a, ServiceChatModel b) {
                                return TimeHelper.compare(b.getChatDate(), a.getChatDate());
                            }
                        });
                        // reset
                        list_service_chat_show.clear();
                        list_service_chat_show.addAll(list_service_chat);
                        // count read
                        countRead();
                        // refresh
                        updateAdapter();
                    } else {
                        ToastHelper.t(activity, "empty");
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
                // keyword
                String keyword = et.getText().toString();
                if (keyword.isEmpty()) {
                    Toast.makeText(activity, R.string.can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                // list of contain 'keyword'
                ArrayList<ServiceChatModel> list = new ArrayList<>();
                for (ServiceChatModel gm : list_service_chat) {
                    if (gm.getWorkerNo().contains(keyword) || gm.getWriterName().contains(keyword)) {
                        list.add(gm);
                    }
                }
                list_service_chat_show.clear();
                list_service_chat_show.addAll(list);
                updateAdapter();
                // change title
                activity.changeSearchTitle(keyword);
            }
        });
        b.setNegativeButton(R.string.cancel, null);
        b.show();
    }

    public void clearSearchFilter() {
        list_service_chat_show.clear();
        list_service_chat_show.addAll(list_service_chat);
        updateAdapter();
    }
}