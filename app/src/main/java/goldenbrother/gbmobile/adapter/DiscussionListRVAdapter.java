package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.DiscussionListActivity;
import goldenbrother.gbmobile.activity.MedicalListActivity;
import goldenbrother.gbmobile.model.Discussion;
import goldenbrother.gbmobile.model.Medical;

/**
 * Created by asus on 2016/6/22.
 */
public class DiscussionListRVAdapter extends SampleRVAdapter {

    private ArrayList<Discussion> list;

    public DiscussionListRVAdapter(Context context, ArrayList<Discussion> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_discussion_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            final Discussion item = list.get(position);
            h.name.setText(item.getFlaborName());
            h.date.setText(item.getDiscussionDate());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((DiscussionListActivity)getContext()).onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_rv_discussion_list_name);
            date = itemView.findViewById(R.id.tv_item_rv_discussion_list_date);
        }
    }


}
