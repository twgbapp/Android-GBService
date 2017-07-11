package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;

import java.util.ArrayList;

/**
 * Created by asus on 2016/11/16.
 */

public class AnnouncementListAdapter extends SampleBaseAdapter {

    private ArrayList<AnnouncementModel> list;

    public AnnouncementListAdapter(Context context, ArrayList<AnnouncementModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list_announcement_list, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final AnnouncementModel item = (AnnouncementModel) getItem(position);
        // set create date
        tag.createDate.setText(TimeHelper.getYMDTime(item.getCreateDate()));
        // set type
        changeType(tag.type, item.getType());
        // set title
        tag.title.setText(item.getTitle());
        return v;
    }

    private void changeType(TextView tv, int type) {
        String name = "";
        int color = 0;
        switch (type) {
            case 1:
                name = "Dorm";
                color = Color.RED;
                break;
            case 2:
                name = "Company";
                color = Color.GREEN;
                break;
            case 3:
                name = "Government";
                color = Color.BLUE;
                break;
        }
        tv.setText(name);
        tv.setTextColor(color);
    }

    private static class ViewHolder {
        TextView createDate;
        TextView type;
        TextView title;

        ViewHolder(View v) {
            createDate = (TextView) v.findViewById(R.id.tv_item_list_announcement_list_create_date);
            type = (TextView) v.findViewById(R.id.tv_item_list_announcement_list_type);
            title = (TextView) v.findViewById(R.id.tv_item_list_announcement_list_title);
        }
    }
}
