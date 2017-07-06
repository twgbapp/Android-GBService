package goldenbrother.gbmobile.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.activity.MobileServiceActivity;
import goldenbrother.gbmobile.adapter.EventListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOEvent;
import goldenbrother.gbmobile.sqlite.DAOEventChat;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by asus on 2016/10/3.
 */

public class EventListFragment extends Fragment {
    // activity
    private MobileServiceActivity activity;
    public static EventListFragment f;
    // ui
    private SwipeRefreshLayout srl;
    private ListView lv_event;
    private BottomBar bottomBar;
    //
    private ArrayList<EventModel> list_event;
    private ArrayList<EventModel> list_event_show;
    private boolean bottom_F = true;

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
        bottomBar = (BottomBar) v.findViewById(R.id.bb_service_event_list);
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
                // select tab
                bottomBar.selectTabAtPosition(STATUS_ALL);
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
        lv_event.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // open
                Intent intent = new Intent();
                intent.setClass(activity, EventChatActivity.class);
                intent.putExtra("serviceEventID", list_event_show.get(position).getServiceEventID());
                startActivity(intent);
                // set read
                list_event_show.get(position).setChatCount(0);
                updateAdapter();
            }
        });
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
        // initBottomBar
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                // return first time
                if (bottom_F) {
                    bottom_F = false;
                    return;
                }

                // get statas
                switch (tabId) {
                    case R.id.tab_all:
                        getStatusEventList(STATUS_ALL);
                        break;
                    case R.id.tab_ing:
                        getStatusEventList(STATUS_ING);
                        break;
                    case R.id.tab_completed:
                        getStatusEventList(STATUS_COMPLETED);
                        break;
                }
            }
        });
        // loadLocalEventList
        loadLocalEventList();
        // loadCloudEventList
        loadCloudEventList(0);
    }


    public static final int STATUS_ALL = 0;
    public static final int STATUS_ING = 1;
    public static final int STATUS_COMPLETED = 2;

    private void getStatusEventList(int status) {
        list_event_show.clear();
        switch (status) {
            case STATUS_ALL:
                list_event_show.addAll(list_event);
                break;
            case STATUS_ING:
                for (EventModel em : list_event) {
                    if (em.getEventScore() == 0) {
                        list_event_show.add(em);
                    }
                }
                break;
            case STATUS_COMPLETED:
                for (EventModel em : list_event) {
                    if (em.getEventScore() != 0) {
                        list_event_show.add(em);
                    }
                }
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
            new LoadCloudEventList(activity, j, URLHelper.HOST, serviceGroupID).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                            // reload local
                            //loadLocalEventList();

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
        DAOEventChat daoEventChat = new DAOEventChat(activity);
        for (EventModel em : list_event) {
            int chatCount = daoEventChat.getCount(em.getServiceEventID());
            if (em.getChatCount() != 0) {
                em.setChatCount(em.getChatCount() - chatCount);
            }
        }
    }

    public void receiveMessage(String content) {
        loadCloudEventList(0);
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
        EventListAdapter adapter = (EventListAdapter) lv_event.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void clearSearchFilter() {
        list_event_show.clear();
        list_event_show.addAll(list_event);
        updateAdapter();
    }
}