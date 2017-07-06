package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.EventModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class EventListAdapter extends SampleBaseAdapter {

    private ArrayList<EventModel> list;

    public EventListAdapter(Context context, ArrayList<EventModel> list) {
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
            v = getInflater().inflate(R.layout.item_list_event, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final EventModel item = (EventModel) getItem(position);
        // set picture
        int wp = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
        if (item.getUserPicture() != null && !item.getUserPicture().isEmpty()) {
            Picasso.with(getContext()).load(item.getUserPicture()).placeholder(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        }
        // set name
        String workerNo = item.getWorkerNo();
        if (workerNo != null && !workerNo.isEmpty()) {
            tag.name.setText("(" + workerNo + ")" + item.getUserName());
        } else {
            tag.name.setText(item.getUserName());
        }
        // set description
        tag.description.setText(item.getEventDescription());
        // set read
        if (item.getChatCount() > 0) {
            tag.read.setVisibility(View.VISIBLE);
            tag.read.setText(String.format("%d", item.getChatCount()));
        } else {
            tag.read.setVisibility(View.GONE);
        }
        return v;
    }

    private static class ViewHolder {
        CircleImageView picture;
        TextView name;
        TextView description;
        TextView read;

        ViewHolder(View v) {
            picture = (CircleImageView) v.findViewById(R.id.iv_item_list_event_picture);
            name = (TextView) v.findViewById(R.id.tv_item_list_event_name);
            description = (TextView) v.findViewById(R.id.tv_item_list_event_description);
            read = (TextView) v.findViewById(R.id.tv_item_list_event_read);
        }
    }
}
