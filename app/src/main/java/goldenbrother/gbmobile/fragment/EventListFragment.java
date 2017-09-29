package goldenbrother.gbmobile.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.activity.MobileServiceActivity;
import goldenbrother.gbmobile.adapter.EventListAdapter;
import goldenbrother.gbmobile.fcm.FCMNotice;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.PackageHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventChatModel;
import goldenbrother.gbmobile.model.EventModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOEvent;
import goldenbrother.gbmobile.sqlite.DAOEventChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by asus on 2016/10/3.
 */

public class EventListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    // activity
    private MobileServiceActivity activity;
    public static EventListFragment f;
    // ui
    private SwipeRefreshLayout srl;
    private ListView lv_event;
    private LinearLayout ll_all, ll_processing, ll_finish;
    //
    private ArrayList<EventModel> list_event;
    private ArrayList<EventModel> list_event_show;

    public static EventListFragment createInstance() {
        f = new EventListFragment();
        return f;
    }

    public static EventListFragment getInstance() {
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        srl = (SwipeRefreshLayout) v.findViewById(R.id.srl_event_list);
        lv_event = (ListView) v.findViewById(R.id.lv_service_event_list);
        ll_all = (LinearLayout) v.findViewById(R.id.ll_service_event_list_all);
        ll_processing = (LinearLayout) v.findViewById(R.id.ll_service_event_list_processing);
        ll_finish = (LinearLayout) v.findViewById(R.id.ll_service_event_list_finish);

        ll_all.setOnClickListener(this);
        ll_processing.setOnClickListener(this);
        ll_finish.setOnClickListener(this);
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
                // clear
                list_event.clear();
                list_event_show.clear();
                updateAdapter();
                // load
                loadCloudEventList(0);
            }
        });
        srl.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // initListView
        list_event = new ArrayList<>();
        list_event_show = new ArrayList<>();
        lv_event.setAdapter(new EventListAdapter(activity, list_event_show));
        lv_event.setOnItemClickListener(this);
        lv_event.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        // loadLocalEventList
        loadLocalEventList();
        // loadCloudEventList
        loadCloudEventList(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadCloudEventList(0);
        }
    };


    public static final int ALL = 0;
    public static final int PROCESSING = 1;
    public static final int FINISH = 2;

    private void getStatusEventList(int status) {
        list_event_show.clear();
        switch (status) {
            case ALL:
                // data
                list_event_show.addAll(list_event);
                // ui
                ll_all.setBackgroundResource(R.drawable.layout_corner_round_left_selected);
                ll_processing.setBackgroundColor(ContextCompat.getColor(activity, R.color.tab_not_selected));
                ll_finish.setBackgroundResource(R.drawable.layout_corner_round_right_not_selected);
                break;
            case PROCESSING:
                // data
                for (EventModel em : list_event) {
                    if (em.getEventScore() == 0) {
                        list_event_show.add(em);
                    }
                }
                // ui
                ll_all.setBackgroundResource(R.drawable.layout_corner_round_left_not_selected);
                ll_processing.setBackgroundColor(ContextCompat.getColor(activity, R.color.tab_selected));
                ll_finish.setBackgroundResource(R.drawable.layout_corner_round_right_not_selected);
                break;
            case FINISH:
                // data
                for (EventModel em : list_event) {
                    if (em.getEventScore() != 0) {
                        list_event_show.add(em);
                    }
                }
                // ui
                ll_all.setBackgroundResource(R.drawable.layout_corner_round_left_not_selected);
                ll_processing.setBackgroundColor(ContextCompat.getColor(activity, R.color.tab_not_selected));
                ll_finish.setBackgroundResource(R.drawable.layout_corner_round_right_selected);
                break;
        }
        updateAdapter();
    }

    private void loadLocalEventList() {
        DAOEvent daoEvent = new DAOEvent(activity);
        list_event.clear();
        list_event.addAll(daoEvent.getAll());
        list_event_show.clear();
        list_event_show.addAll(list_event);
        updateAdapter();
    }

    public void loadCloudEventList(int serviceGroupID) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getEventList");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadCloudEventList(activity, j, URLHelper.HOST, serviceGroupID).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventModel item = list_event_show.get(position);
        // open
        Intent intent = new Intent();
        intent.setClass(activity, EventChatActivity.class);
        intent.putExtra("serviceEventID", list_event_show.get(position).getServiceEventID());
        startActivity(intent);
        // set read
        int eventUnReadCount = SPHelper.getEventUnReadCount(activity) - item.getChatCount();
        SPHelper.setEventUnReadCount(activity, eventUnReadCount);
        PackageHelper.setBadge(activity, SPHelper.getUnReadCount(activity));
        item.setChatCount(0);
        updateAdapter();
    }

    private class LoadCloudEventList extends IAsyncTask {

        private int serviceGroupID = 0;

        LoadCloudEventList(Context context, JSONObject json, String url, int serviceGroupID) {
            super(context, json, url);
            setShow(false);
            this.serviceGroupID = serviceGroupID;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            srl.setRefreshing(false);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getEventList(response, list_event);
                    if (result == ApiResultHelper.SUCCESS) {
                        if (!list_event.isEmpty()) {
                            // save or update to local
                            DAOEvent daoEvent = new DAOEvent(activity);
                            for (EventModel e : list_event) {
                                daoEvent.insertOrUpdate(e);
                            }
                            // sort
                            Collections.sort(list_event, new Comparator<EventModel>() {
                                @Override
                                public int compare(EventModel a, EventModel b) {
                                    return b.getServiceEventID() > a.getServiceEventID() ? 1 : -1;
                                }
                            });
                            // reset
                            list_event_show.clear();
                            list_event_show.addAll(list_event);
                            // count read
                            countRead();
                            // refresh
                            updateAdapter();
                            // to All
                            getStatusEventList(ALL);
                            // auto open (for add event)
                            if (serviceGroupID != 0) {
                                Intent intent = new Intent();
                                intent.setClass(activity, EventChatActivity.class);
                                intent.putExtra("serviceEventID", serviceGroupID);
                                startActivity(intent);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void countRead() {
        int unreadCount = 0;
        DAOEventChat daoEventChat = new DAOEventChat(activity);
        for (EventModel em : list_event) {
            int chatCount = daoEventChat.getCount(em.getServiceEventID());
            if (em.getChatCount() != 0) {
                unreadCount += em.getChatCount() - chatCount;
                em.setChatCount(em.getChatCount() - chatCount);
            }
        }
        SPHelper.setEventUnReadCount(activity, unreadCount);
        PackageHelper.setBadge(activity, SPHelper.getUnReadCount(activity));
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
                ArrayList<EventModel> list = new ArrayList<>();
                for (EventModel em : list_event_show) {
                    if (em.getUserName().contains(keyword)) {
                        list.add(em);
                    }
                }
                list_event_show.clear();
                list_event_show.addAll(list);
                updateAdapter();
                // change title
                activity.changeSearchTitle(keyword);
            }
        });
        b.setNegativeButton(R.string.cancel, null);
        b.show();
    }

    private void updateAdapter() {
        ((EventListAdapter) lv_event.getAdapter()).notifyDataSetChanged();
    }

    public void clearSearchFilter() {
        list_event_show.clear();
        list_event_show.addAll(list_event);
        updateAdapter();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_service_event_list_all:
                getStatusEventList(ALL);
                break;
            case R.id.ll_service_event_list_processing:
                getStatusEventList(PROCESSING);
                break;
            case R.id.ll_service_event_list_finish:
                getStatusEventList(FINISH);
                break;

        }
    }
}