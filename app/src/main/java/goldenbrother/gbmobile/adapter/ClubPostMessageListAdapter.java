package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.EventModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class ClubPostMessageListAdapter extends SampleBaseAdapter {

    private ArrayList<ClubPostMessageModel> list;

    public ClubPostMessageListAdapter(Context context, ArrayList<ClubPostMessageModel> list) {
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
            v = getInflater().inflate(R.layout.item_list_club_post_message, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final ClubPostMessageModel item = (ClubPostMessageModel) getItem(position);
        // set picture
        int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
        if (item.getUserPicture() != null && !item.getUserPicture().isEmpty()) {
            Picasso.with(getContext()).load(item.getUserPicture()).resize(w, w).centerCrop().into(tag.picture);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(tag.picture);
        }
        // set name
        tag.name.setText(item.getUserName());
        // set message
        tag.message.setText(item.getMessage());
        return v;
    }

    private static class ViewHolder {
        ImageView picture;
        TextView name;
        TextView message;

        ViewHolder(View v) {
            picture = (ImageView) v.findViewById(R.id.iv_item_list_club_post_message_picture);
            name = (TextView) v.findViewById(R.id.tv_item_list_club_post_message_name);
            message = (TextView) v.findViewById(R.id.tv_item_list_club_post_message_message);
        }
    }
}
