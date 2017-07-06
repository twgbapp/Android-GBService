package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.EventChatListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.EnvironmentHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
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
    private static EventChatActivity instance;
    // ui
    private TextView tv_title;
    private ImageView iv_send, iv_add_user, iv_rating;
    private ListView lv_chat;
    private EditText et_content;
    // extra
    private int serviceEventID;
    private EventModel event;
    private ArrayList<EventChatModel> list_EventChat;

    public static EventChatActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chat);
        instance = this;
        // ui reference
        tv_title = (TextView) findViewById(R.id.tv_event_chat_title);
        iv_add_user = (ImageView) findViewById(R.id.iv_event_add_user);
        lv_chat = (ListView) findViewById(R.id.lv_event_chat);
        et_content = (EditText) findViewById(R.id.et_event_content);
        iv_rating = (ImageView) findViewById(R.id.iv_event_rating);
        iv_send = (ImageView) findViewById(R.id.iv_event_send);
        // get extra
        Intent intent = getIntent();
        serviceEventID = intent.getIntExtra("serviceEventID", -1);
        // initImageView
        iv_rating.setOnClickListener(this);
        iv_add_user.setOnClickListener(this);
        iv_rating.setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        iv_add_user.setVisibility(RoleInfo.getInstance().isLabor() ? View.GONE : View.VISIBLE);
        // initListView
        list_EventChat = new ArrayList<>();
        lv_chat.setAdapter(new EventChatListAdapter(this, list_EventChat));
        // listener
        iv_send.setOnClickListener(this);
        // get Local Chat
        loadLocalChat();
        // get Cloud Chat
        loadCloudChat();
    }

    private void addEventChat(String content) {
        // empty
        if (content.isEmpty()) {
            return;
        }

        try {
            JSONObject j = new JSONObject();
            j.put("action", "addEventChat");
            j.put("serviceEventID", serviceEventID);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("content", content);
            j.put("chatDate", TimeHelper.getStandard());
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
                    int result = ApiResultHelper.addEventChat(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // clear editText
                        et_content.setText("");
                        // reload
                        loadCloudChat();
                        // pushEventMessage
                        pushEventMessage(content);
                    } else {
                        ToastHelper.t(EventChatActivity.this, "Add Event Chat Fail");
                    }
                    break;
            }
        }
    }

    private void pushEventMessage(String content) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "pushEventMessage");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("serviceEventID", serviceEventID);
            j.put("content", content);
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
        list_EventChat.clear();
        list_EventChat.addAll(daoEventChat.get(serviceEventID));
        // set title
        tv_title.setText(event.getUserName());
        // update local chat data to view
        updateAdapter();
    }

    public void receiveMessage(String content) {
        loadCloudChat();
    }

    private void ratingEvent(int score) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "scoreEvent");
            j.put("serviceEventID", serviceEventID);
            j.put("score", score);
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
                    int result = ApiResultHelper.ratingEvent(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        ToastHelper.t(EventChatActivity.this, "Rating Success.\nEvent is end.");
                    } else {
                        ToastHelper.t(EventChatActivity.this, "Rating Fail");
                    }
                    break;
            }
        }
    }

    private AlertDialog ad_rating;

    public void showRatingDialog() {
        if (!event.getUserID().equals(RoleInfo.getInstance().getUserID())) {
            ToastHelper.t(this, "You can't do this.");
            return;
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_event_rating, null);
        final RatingBar rb = (RatingBar) v.findViewById(R.id.rb_dialog_event_rating);
        final View tv_close = v.findViewById(R.id.tv_dialog_event_rating_close);
        // init ratingbar
        rb.setRating(event.getEventScore());
        // listener
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                new AlertDialog.Builder(EventChatActivity.this)
                        .setTitle("Rating")
                        .setMessage("Are you sure to rating ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ratingEvent((int) rb.getRating());
                            }
                        })
                        .setNegativeButton("CANCEL", null)
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
        EventChatListAdapter adapter = (EventChatListAdapter) lv_chat.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            lv_chat.setSelection(list_EventChat.size() - 1);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_event_send:
                if (!isSending) {
                    EnvironmentHelper.hideKeyBoard(this, view);
                    addEventChat(et_content.getText().toString());
                } else {
                    t("Sending...");
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

    @Override
    protected void onResume() {
        super.onResume();
        if (instance == null)
            instance = this;
    }

    @Override
    protected void onPause() {
        if (instance != null)
            instance = null;
        super.onPause();
    }
}
