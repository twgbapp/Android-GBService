package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.SearchServiceListRVAdapter;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.PackageHelper;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.ServiceGroupMember;
import goldenbrother.gbmobile.sqlite.DAOServiceChat;
import goldenbrother.gbmobile.sqlite.DAOServiceGroupMember;

public class SearchServiceListActivity extends CommonActivity {

    //extra
    private String keyword;
    // ui
    private RecyclerView rv;
    // data
    private ArrayList<ServiceChatModel> list_service_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_service_list);

        // extra
        keyword = getIntent().getStringExtra("keyword");

        setUpBackToolbar(R.id.toolbar, R.id.toolbar_title, getString(R.string.search) + " : " + keyword);

        // ui reference
        rv = findViewById(R.id.rv_search_service_list);

        // init
        list_service_chat = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new SearchServiceListRVAdapter(this, list_service_chat));

        getLocalGroupList();
    }

    private void getLocalGroupList() {
        // find all your ServiceGroupID
        List<ServiceGroupMember> members = new DAOServiceGroupMember(this).filterByUserId(RoleInfo.getInstance().getUserID());
        if (!members.isEmpty()) {
            List<Integer> serviceGroupIds = new ArrayList<>();
            for (ServiceGroupMember item : members) {
                serviceGroupIds.add(item.getServiceGroupID());
            }
            List<ServiceChatModel> list = new DAOServiceChat(this).getLastChatList(serviceGroupIds);
            list_service_chat.clear();
            String kw = keyword.toLowerCase();
            for (ServiceChatModel item : list) {
                if (item.getUserName().toLowerCase().contains(kw)) {
                    list_service_chat.add(item);
                }
            }
            if (list_service_chat.isEmpty()) {
                t(R.string.empty);
            } else {
                updateAdapter();
            }
        } else {
            t(R.string.empty);
        }
    }

    public void onItemClick(ServiceChatModel item) {
        Intent intent = new Intent();
        intent.setClass(this, ServiceChatActivity.class);
        intent.putExtra("serviceGroupID", item.getServiceGroupID());
        intent.putExtra("userID", item.getUserID());
        intent.putExtra("userName", item.getUserName());
        startActivity(intent);
        // set read
        int chatUnReadCount = SPHelper.getChatUnReadCount(this) - item.getChatCount();
        SPHelper.setChatUnReadCount(this, chatUnReadCount);
        PackageHelper.setBadge(this, SPHelper.getUnReadCount(this));
        item.setChatCount(0);
        updateAdapter();
    }

    private void updateAdapter() {
        rv.getAdapter().notifyDataSetChanged();
    }
}
