package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.SatisfactionQuestionActivity;
import goldenbrother.gbmobile.model.SatisfactionIssueModel;

import java.util.ArrayList;

/**
 * Created by asus on 2016/6/22.
 */
public class SatisfactionIssueRVAdapter extends SampleRVAdapter {

    private ArrayList<SatisfactionIssueModel> list;

    public SatisfactionIssueRVAdapter(Context context, ArrayList<SatisfactionIssueModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_satisfaction_issue,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SatisfactionIssueModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            // set name
            h.name.setText(item.getName());
            // listener
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), SatisfactionQuestionActivity.class);
                    intent.putExtra("issue",item);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_item_rv_satisfaction_issue_name);
        }
    }


}
