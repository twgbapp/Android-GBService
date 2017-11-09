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
import goldenbrother.gbmobile.activity.TravelListActivity;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.Travel;
import goldenbrother.gbmobile.model.Travel;

public class TravelListRVAdapter extends SampleRVAdapter {

    private ArrayList<Travel> list;

    public TravelListRVAdapter(Context context, ArrayList<Travel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_travel_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            final Travel item = list.get(position);
            h.date.setText(TimeHelper.getYMDTime(item.getCreateDate()));
            h.title.setText(item.getTitle());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TravelListActivity) getContext()).onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView title;
        TextView description;

        ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.tv_item_rv_travel_list_date);
            title = v.findViewById(R.id.tv_item_rv_travel_list_title);
            description = v.findViewById(R.id.tv_item_rv_travel_list_description);
        }
    }


}
