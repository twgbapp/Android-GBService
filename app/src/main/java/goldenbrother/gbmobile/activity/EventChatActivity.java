package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.EventChatRVAdapter;
import goldenbrother.gbmobile.fcm.FCMNotice;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventChatModel;
import goldenbrother.gbmobile.model.EventModel;
import goldenbrother.gbmobile.model.EventTimePointModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.sqlite.DAOEvent;
import goldenbrother.gbmobile.sqlite.DAOEventChat;
import goldenbrother.gbmobile.sqlite.DAOEventTimePoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventChatActivity extends CommonActivity implements View.OnClickListener {
    // ui
    private TextView tv_title;
    private RecyclerView rv;
    private EditText et_content;
    // extra
    private int serviceEventID;
    private EventModel event;
    private ArrayList<EventChatModel> list_event_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chat);
        setUpBackToolbar(R.id.toolbar_event_chat, R.id.tv_event_chat_title, "");
        // ui reference
        tv_title = findViewById(R.id.tv_event_chat_title);
        rv = findViewById(R.id.rv_event_chat);
        et_content = findViewById(R.id.et_event_chat_content);
        findViewById(R.id.iv_event_rating).setOnClickListener(this);
        findViewById(R.id.iv_event_add_user).setOnClickListener(this);
        findViewById(R.id.tv_event_chat_send).setOnClickListener(this);
        // extra
        Intent intent = getIntent();
        serviceEventID = intent.getIntExtra("serviceEventID", -1);
        // init
        FCMNotice.getInstance().setOnMessageReceivedListener(new FCMNotice.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(String s) {
                handler.sendEmptyMessage(0);
            }
        });
        findViewById(R.id.iv_event_rating).setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        findViewById(R.id.iv_event_add_user).setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        // initListView
        list_event_chat = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new EventChatRVAdapter(this, list_event_chat));
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

    private void addEventChat(String content) {
        if (content.isEmpty()) {
            t(R.string.can_not_be_empty);
            return;
        }
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addEventChat");
            j.put("serviceEventID", serviceEventID);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("content", content);
            j.put("chatDate", TimeHelper.getStandard());
            j.put("logStatus", true);
            new AddEventChat(this, j, URLHelper.HOST, content).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean isSending = false;

    private class AddEventChat extends IAsyncTask {
        private String content;

        AddEventChat(Context context, JSONObject json, String url, String content) {
            super(context, json, url);
            this.content = content;
            setShow(content.equals(Constant.RATING));
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
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // clear editText
                        et_content.setText("");
                        // reload
                        loadCloudChat();
                        // pushEventMessage
                        pushEventMessage(content);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void pushEventMessage(String content) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "pushEventMessage");
            j.put("serviceEventID", serviceEventID);
            j.put("content", content);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new PushEventMessage(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class PushEventMessage extends IAsyncTask {

        PushEventMessage(Context context, JSONObject json, String url) {
            super(context, json, url, false);
            setShow(false);
        }
    }

    private void loadCloudChat() {
        try {
            // get event time point model
            DAOEventTimePoint daoEventTimePoint = new DAOEventTimePoint(this);
            EventTimePointModel etp = daoEventTimePoint.get(serviceEventID);
            // if first
            if (etp == null) {
                etp = new EventTimePointModel(serviceEventID, TimeHelper.getInitTime());
                daoEventTimePoint.insertOrUpdate(etp);
            }
            JSONObject j = new JSONObject();
            j.put("action", "getEventChat");
            j.put("serviceEventID", serviceEventID);
            j.put("startChatDate", TimeHelper.addMinute(etp.getTimePoint(), -10));
            j.put("endChatDate", TimeHelper.addMinute(TimeHelper.getStandard(), 10));
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new LoadCloudChat(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadCloudChat extends IAsyncTask {
        private ArrayList<EventChatModel> list;

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
                    int result = ApiResultHelper.getEventChat(response, list, serviceEventID);
                    if (result == ApiResultHelper.SUCCESS && !list.isEmpty()) {
                        // save chat to local (if can)
                        DAOEventChat daoGroupChat = new DAOEventChat(EventChatActivity.this);
                        for (EventChatModel gc : list) {
                            daoGroupChat.insert(gc);
                        }
                        // save event time point
                        try {
                            DAOEventTimePoint daoEventTimePoint = new DAOEventTimePoint(EventChatActivity.this);
                            EventTimePointModel etp = daoEventTimePoint.get(serviceEventID);
                            etp.setTimePoint(TimeHelper.addMinute(getJSONObject().getString("endChatDate"), -10));
                            daoEventTimePoint.insertOrUpdate(etp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // reload
                        loadLocalChat();
                    }
                    break;
            }
        }
    }

    private void loadLocalChat() {
        DAOEvent daoEvent = new DAOEvent(this);
        event = daoEvent.get(serviceEventID);
        DAOEventChat daoEventChat = new DAOEventChat(this);
        list_event_chat.clear();
        list_event_chat.addAll(daoEventChat.get(serviceEventID));
        // set title
        tv_title.setText(event.getUserName());
        // update local chat data to view
        updateAdapter();
    }

    private void ratingEvent(int score) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "scoreEvent");
            j.put("serviceEventID", serviceEventID);
            j.put("score", score);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new RatingEvent(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class RatingEvent extends IAsyncTask {

        RatingEvent(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private AlertDialog ad_rating;

    public void showRatingDialog() {
        if (!event.getUserID().equals(RoleInfo.getInstance().getUserID())) {
            t(R.string.can_not_rating);
            return;
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_event_rating, null);
        final RatingBar rb = v.findViewById(R.id.rb_dialog_event_rating);
        final View tv_close = v.findViewById(R.id.tv_dialog_event_rating_close);
        // init RatingBar
        rb.setRating(event.getEventScore());
        // listener
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                new AlertDialog.Builder(EventChatActivity.this)
                        .setTitle(R.string.rating)
                        .setMessage(R.string.confirm_to_rating)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ratingEvent((int) rb.getRating());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_rating.dismiss();
            }
        });
        b.setView(v);
        ad_rating = b.show();
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
        rv.scrollToPosition(list_event_chat.size() - 1);
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.tv_event_chat_send:
                if (!isSending) {
                    addEventChat(et_content.getText().toString());
                } else {
                    t(R.string.sending);
                }
                break;
            case R.id.iv_event_add_user:
                Bundle b = new Bundle();
                b.putInt("serviceEventID", serviceEventID);
                openActivity(AddEventUserActivity.class, b);
                break;
            case R.id.iv_event_rating:
                addEventChat(Constant.RATING);
                break;
        }
    }
}
