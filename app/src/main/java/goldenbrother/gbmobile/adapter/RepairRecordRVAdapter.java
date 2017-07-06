package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.RepairRecordContentActivity;
import goldenbrother.gbmobile.model.RepairRecordModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/6/22.
 */
public class RepairRecordRVAdapter extends SampleRVAdapter {


    // type
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_BODY = 1;
    // data
    private ArrayList<RepairRecordModel> list;

    public RepairRecordRVAdapter(Context context, ArrayList<RepairRecordModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEAD : TYPE_BODY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(getInflater().inflate(R.layout.item_rv_repair_record_head, parent, false));
        } else if (viewType == TYPE_BODY) {
            return new BodyViewHolder(getInflater().inflate(R.layout.item_rv_repair_record_body, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BodyViewHolder) {
            final RepairRecordModel item = list.get(position - 1);
            BodyViewHolder h = (BodyViewHolder) holder;
            // set date
            h.date.setText(item.getHappenDate());
            // set item
            h.desc.setText(item.getEventDesc());
            // set status
            if (item.getStatus().equals("0")) { // red
                h.status.setImageResource(R.color.status_red);
            } else if (item.getStatus().equals("1")) { // yellow
                h.status.setImageResource(R.color.status_yellow);
            } else if (item.getStatus().equals("2")) { // green
                h.status.setImageResource(R.color.status_green);
            }
            // set background
            h.itemView.setBackgroundColor(position % 2 != 0 ? ContextCompat.getColor(getContext(), R.color.background_layout) : Color.WHITE);
            // listener
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), RepairRecordContentActivity.class);
                    intent.putExtra("rrsNo",list.get(position).getRrsNo());
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1 + list.size();
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class BodyViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView desc;
        CircleImageView status;

        BodyViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.tv_item_rv_repair_record_date);
            desc = (TextView) v.findViewById(R.id.tv_item_rv_repair_record_desc);
            status = (CircleImageView) v.findViewById(R.id.iv_item_rv_repair_record_status);
        }
    }


}
