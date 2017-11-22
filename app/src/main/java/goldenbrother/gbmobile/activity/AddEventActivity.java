package goldenbrother.gbmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.AddEventRVAdapter;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.IAsyncTask;
import goldenbrother.gbmobile.helper.URLHelper;
import goldenbrother.gbmobile.model.EventKindModel;
import goldenbrother.gbmobile.model.EventModel;
import goldenbrother.gbmobile.model.RoleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// add multi event
public class AddEventActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private RecyclerView rv;
    // extra
    private String userID;
    // data
    private ArrayList<EventModel> list_event;
    private ArrayList<EventKindModel> list_event_kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // ui reference
        findViewById(R.id.iv_add_event_done).setOnClickListener(this);
        findViewById(R.id.fab_add_event).setOnClickListener(this);
        rv = findViewById(R.id.rv_add_event);

        // extra
        userID = getIntent().getStringExtra("userID");

        // init
        list_event = new ArrayList<>();
        list_event_kind = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AddEventRVAdapter(this, list_event));

        getEventKind();
    }

    private void getEventKind() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "getEventKind");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", false);
            new GetEventKind(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetEventKind extends IAsyncTask {

        GetEventKind(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.getEventKind(response, list_event_kind);
                    if (result == ApiResultHelper.SUCCESS) {

                    } else {
                        t(R.string.fail);
                        finish();
                    }
                    break;
            }
        }
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }

    // add event dialog
    private AlertDialog ad;
    // dialog ui
    private TextView tv_type;

    private void showAddEventDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_service_add_event, null);
        tv_type = v.findViewById(R.id.tv_dialog_service_add_event_type);
        tv_type.setOnClickListener(this);
        final EditText et_description = v.findViewById(R.id.et_dialog_service_add_event_description);
        // cancel listener
        v.findViewById(R.id.tv_dialog_service_add_event_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null) ad.dismiss();
            }
        });
        // ok listener
        v.findViewById(R.id.tv_dialog_service_add_event_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                String description = et_description.getText().toString();
                String eventKindValue = getEventKindValue();
                String eventKindCode = getEventKindCode(eventKindValue);
                // check
                if (description.isEmpty() || eventKindCode.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    return;
                }
                // add model
                EventModel item = new EventModel();
                item.setUserID(userID);
                item.setEventDescription(description);
                item.setEventKindValue(eventKindValue);
                item.setEventKind(eventKindCode);
                item.setStaffID(RoleInfo.getInstance().getUserID());
                list_event.add(item);
                // update
                updateAdapter();
                // dismiss
                if (ad != null) ad.dismiss();
            }
        });
        b.setView(v);
        ad = b.show();
    }

    private String getEventKindValue() {
        if (tv_type == null) return "";
        String str = tv_type.getText().toString();
        if (str.isEmpty()) return "";
        return str;
    }

    private String getEventKindCode(String value) {
        if (value.isEmpty()) return "";
        for (EventKindModel item : list_event_kind) {
            if (value.equals(item.getValue())) {
                return item.getCode();
            }
        }
        return "";
    }

    private void showConfirmAddEventDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_event_add_event))
                .setMessage(getString(R.string.add_event_message))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addEvent();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void addEvent() {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "addEvent");
            // add to array
            JSONArray arr = new JSONArray();
            for (EventModel item : list_event) {
                JSONObject jo = new JSONObject();
                jo.put("userID", item.getUserID());
                jo.put("eventDescription", item.getEventDescription());
                jo.put("eventKind", item.getEventKind());
                jo.put("staffID", item.getStaffID());
                arr.put(jo);
            }
            j.put("events", arr.toString());
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new AddEvent(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AddEvent extends IAsyncTask {

        AddEvent(Context context, JSONObject json, String url) {
            super(context, json, url);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        t(R.string.success);
                        finish();
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void showTypeDialog() {
        final String[] items = new String[list_event_kind.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = list_event_kind.get(i).getValue();
        }
        alertWithItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_type.setText(items[which]);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_service_add_event_type:
                showTypeDialog();
                break;
            case R.id.iv_add_event_done:
                if (list_event.isEmpty()) {
                    t(R.string.can_not_be_empty);
                    break;
                }
                showConfirmAddEventDialog();
                break;
            case R.id.fab_add_event:
                showAddEventDialog();
                break;
        }
    }
}
