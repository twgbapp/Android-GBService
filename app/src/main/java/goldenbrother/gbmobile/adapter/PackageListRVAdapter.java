package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.PackageListActivity;
import goldenbrother.gbmobile.model.PackageModel;

public class PackageListRVAdapter extends SampleRVAdapter {

    private ArrayList<PackageModel> list;

    public PackageListRVAdapter(Context context, ArrayList<PackageModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_package_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            final PackageModel item = list.get(position);
            h.description.setText(item.getDescription());
            h.date.setText(item.getArriveDate());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PackageListActivity) getContext()).onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        TextView date;

        ViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.tv_item_rv_package_list_description);
            date = itemView.findViewById(R.id.tv_item_rv_package_list_date);
        }
    }


}
