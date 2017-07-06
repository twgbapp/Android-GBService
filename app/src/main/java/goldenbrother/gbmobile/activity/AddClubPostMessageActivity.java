package goldenbrother.gbmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.ClubPostMessageListAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddClubPostMessageActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private TextView tv_no_comment;
    private ListView lv_comment;
    private EditText et_message;
    // extra
    private int clubPostID;
    // data
    private ArrayList<ClubPostMessageModel> list_message;
    private int messageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club_post_message);
        // ui reference
        tv_no_comment = (TextView) findViewById(R.id.tv_add_club_post_message_no_comment);
        lv_comment = (ListView) findViewById(R.id.lv_add_club_post_message_comment);
        et_message = (EditText) findViewById(R.id.et_add_club_post_message_message);
        findViewById(R.id.iv_add_club_post_message_send).setOnClickListener(this);
        // init ListView
        list_message = new ArrayList<>();
        lv_comment.setAdapter(new ClubPostMessageListAdapter(this, list_message));
        // get extra
        Intent intent = getIntent();
        clubPostID = intent.getIntExtra("clubPostID", -1);
        // load message
        loadClubPostMessage();
    }

    public void updateAdapter() {
        ClubPostMessageListAdapter adapter = (ClubPostMessageListAdapter) lv_comment.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private static final int lastMessageCount = 3;

    private ArrayList<ClubPostMessageModel> getLastMessages() {
        ArrayList<ClubPostMessageModel> list = new ArrayList<>();
        int count = 0;
        for (int i = list_message.size() - 1; i >= 0; i--) {
            count++;
            list.add(list_message.get(i));
            if (count == lastMessageCount) break;
        }
        return list;
    }

    private boolean sending = false;

    public void AddClubPostMessage(String message) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addClubPostMessage");
            j.put("clubPostID", clubPostID);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("message", message);
            if (!sending) {
                sending = true;
                new AddClubPostMessage(this, j, URLHelper.HOST).execute();
            } else {
                t(R.string.sending);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddClubPostMessage extends IAsyncTask {

        AddClubPostMessage(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            sending = false;
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.addClubPostMessage(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        et_message.setText("");
                        loadClubPostMessage();
                    }
                    break;
            }
        }
    }

    public void loadClubPostMessage() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getClubPostMessage");
            j.put("clubPostID", clubPostID);
            new LoadClubPostMessage(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadClubPostMessage extends IAsyncTask {

        LoadClubPostMessage(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.loadClubPostMessage(response, list_message);
                    messageCount = list_message.size();
                    if (result == ApiResultHelper.SUCCESS) {
                        updateAdapter();
                        lv_comment.smoothScrollToPosition(list_message.size() - 1);
                        Intent intent = new Intent();
                        intent.putExtra("clubPostID", clubPostID);
                        intent.putExtra("lastMessage", getLastMessages());
                        intent.putExtra("messageCount", messageCount);
                        setResult(RESULT_OK, intent);
                        tv_no_comment.setVisibility(View.GONE);
                        lv_comment.setVisibility(View.VISIBLE);
                    } else {
                        tv_no_comment.setVisibility(View.VISIBLE);
                        lv_comment.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_club_post_message_send:
                String msg = et_message.getText().toString();
                if (!msg.isEmpty()) {
                    AddClubPostMessage(msg);
                }
                break;
        }
    }
}