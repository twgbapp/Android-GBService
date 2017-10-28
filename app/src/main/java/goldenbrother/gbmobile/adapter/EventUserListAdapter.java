package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.EventKindModel;
import goldenbrother.gbmobile.model.EventUserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class EventUserListAdapter extends SampleBaseAdapter {

    private ArrayList<EventUserModel> list;
    private HashSet<EventUserModel> set;

    public EventUserListAdapter(Context context, ArrayList<EventUserModel> list) {
        super(context);
        this.list = list;
        this.set = new HashSet<>();
    }


    public HashSet<EventUserModel> getSelected() {
        String ss = "";
        for (EventUserModel e : set) {
            ss += e.getUserID() + " ";
        }
        Log.d("xxx", ss);
        return set;
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
            v = getInflater().inflate(R.layout.item_list_event_user, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final EventUserModel item = (EventUserModel) getItem(position);
        // set picture
        int wp = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
        if (item.getUserPicture() != null && !item.getUserPicture().isEmpty()) {
            Picasso.with(getContext()).load(item.getUserPicture()).placeholder(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        }
        // set name
        tag.name.setText(item.getUserID());
        // set title
        tag.title.setText(item.getTitle());
        // set check
        tag.check.setVisibility(set.contains(item) ? View.VISIBLE : View.GONE);
        // listener
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (set.contains(item)) {
                    set.remove(item);
                } else {
                    set.add(item);
                }
                notifyDataSetChanged();
            }
        });
        return v;
    }

    private static class ViewHolder {
        CircleImageView picture;
        TextView name;
        TextView title;
        ImageView check;

        ViewHolder(View v) {
            picture = v.findViewById(R.id.iv_item_list_event_user_picture);
            name = v.findViewById(R.id.tv_item_list_event_user_name);
            title = v.findViewById(R.id.tv_item_list_event_title);
            check = v.findViewById(R.id.iv_item_list_event_check);
        }
    }
}
