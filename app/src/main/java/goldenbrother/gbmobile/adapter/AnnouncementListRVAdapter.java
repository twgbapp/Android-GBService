package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AnnouncementListActivity;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.AnnouncementModel;

/**
 * Created by asus on 2016/6/22.
 */
public class AnnouncementListRVAdapter extends SampleRVAdapter {

    private ArrayList<AnnouncementModel> list;

    public AnnouncementListRVAdapter(Context context, ArrayList<AnnouncementModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_announcement_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            final AnnouncementModel item = list.get(position);
            h.type.setBackgroundColor(getTypeColor(item.getType()));
            h.date.setText(TimeHelper.getYMDTime(item.getCreateDate()));
            h.title.setText(item.getTitle());
//            h.description.setText(item.getContent());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AnnouncementListActivity) getContext()).onItemClick(item);
                }
            });
        }
    }

    private int getTypeColor(int type) {
        switch (type) {
            case 1:
                return ContextCompat.getColor(getContext(), R.color.announcement_dorm);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.announcement_com);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.announcement_gov);
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

        ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.iv_item_rv_announcement_list_type);
            date = itemView.findViewById(R.id.tv_item_rv_announcement_list_date);
            title = itemView.findViewById(R.id.tv_item_rv_announcement_list_title);
            description = itemView.findViewById(R.id.tv_item_rv_announcement_list_description);
        }
    }


}
