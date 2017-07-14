package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AddClubPostActivity;
import goldenbrother.gbmobile.activity.AddClubPostMessageActivity;
import goldenbrother.gbmobile.activity.ClubPostActivity;
import goldenbrother.gbmobile.activity.ClubPostMediaActivity;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.ServiceChatModel;

/**
 * Created by asus on 2017/1/21.
 */

public class ServiceChatRVAdapter extends SampleRVAdapter {

    // type
    private static final int OTHER = 0;
    private static final int SELF = 1;
    // data
    private ArrayList<ServiceChatModel> list;
    private String selfUserID;

    public ServiceChatRVAdapter(Context context, ArrayList<ServiceChatModel> list) {
        super(context);
        this.list = list;
        selfUserID = RoleInfo.getInstance().getUserID();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getWriterID().equals(selfUserID) ? SELF : OTHER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == OTHER) {
            return new OtherViewHolder(getInflater().inflate(R.layout.item_rv_service_chat_other, parent, false));
        } else if (viewType == SELF) {
            return new SelfViewHolder(getInflater().inflate(R.layout.item_rv_service_chat_self, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ServiceChatModel item = list.get(position);
        if (holder instanceof OtherViewHolder) {
            OtherViewHolder h = (OtherViewHolder) holder;
            Picasso.with(getContext()).load(item.getWriterPicture()).into(h.picture);
            h.date.setText(item.getChatDate());
            h.content.setText(item.getContent());
        } else if (holder instanceof SelfViewHolder) {
            SelfViewHolder h = (SelfViewHolder) holder;
            h.date.setText(item.getChatDate());
            h.content.setText(item.getContent());
        }
    }

    private class OtherViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView date;
        TextView content;

        OtherViewHolder(View v) {
            super(v);

        }
    }

    private class SelfViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView content;

        SelfViewHolder(View v) {
            super(v);

        }
    }
}
