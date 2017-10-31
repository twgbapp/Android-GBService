package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AnnouncementListActivity;
import goldenbrother.gbmobile.activity.GBActivityListActivity;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;
import goldenbrother.gbmobile.model.GBActivity;

public class GBActivityListRVAdapter extends SampleRVAdapter {

    private ArrayList<GBActivity> list;

    public GBActivityListRVAdapter(Context context, ArrayList<GBActivity> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_gb_activity_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            final GBActivity item = list.get(position);
            h.type.setBackgroundColor(getTypeColor(item.getType()));
            h.date.setText(TimeHelper.getYMDTime(item.getCreateDate()));
            h.title.setText(item.getTitle());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GBActivityListActivity) getContext()).onItemClick(item);
                }
            });
        }
    }

    private int getTypeColor(int type) {
        switch (type) {
            case GBActivityListActivity.NEWS:
                return ContextCompat.getColor(getContext(), R.color.activity_news);
            case GBActivityListActivity.COM:
                return ContextCompat.getColor(getContext(), R.color.activity_com);
            case GBActivityListActivity.CLUB:
                return ContextCompat.getColor(getContext(), R.color.activity_club);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView type;
        TextView date;
        TextView title;
        TextView description;

        ViewHolder(View v) {
            super(v);
            type = v.findViewById(R.id.iv_item_rv_gb_activity_list_type);
            date = v.findViewById(R.id.tv_item_rv_gb_activity_list_date);
            title = v.findViewById(R.id.tv_item_rv_gb_activity_list_title);
            description = v.findViewById(R.id.tv_item_rv_gb_activity_list_description);
        }
    }


}
