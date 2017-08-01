package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.AddEventModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;

/**
 * Created by asus on 2016/6/22.
 */
public class ClubPostMessageRVAdapter extends SampleRVAdapter {

    private ArrayList<ClubPostMessageModel> list;

    public ClubPostMessageRVAdapter(Context context, ArrayList<ClubPostMessageModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_list_club_post_message,parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            final ClubPostMessageModel item = list.get(position);
            // set picture
            int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
            if (item.getUserPicture() != null && !item.getUserPicture().isEmpty()) {
                Picasso.with(getContext()).load(item.getUserPicture()).resize(w, w).centerCrop().into(h.picture);
            } else {
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(h.picture);
            }
            // set name
            h.name.setText(item.getUserName());
            // set message
            h.message.setText(item.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name;
        TextView message;

        ViewHolder(View v) {
            super(v);
            picture = (ImageView) v.findViewById(R.id.iv_item_list_club_post_message_picture);
            name = (TextView) v.findViewById(R.id.tv_item_list_club_post_message_name);
            message = (TextView) v.findViewById(R.id.tv_item_list_club_post_message_message);
        }
    }


}
