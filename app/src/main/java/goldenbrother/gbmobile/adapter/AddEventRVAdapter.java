package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.EventModel;

import java.util.ArrayList;

public class AddEventRVAdapter extends SampleRVAdapter {

    private ArrayList<EventModel> list;

    public AddEventRVAdapter(Context context, ArrayList<EventModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_list_add_event, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EventModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            h.kind.setText(item.getEventKindValue());
            h.description.setText(item.getEventDescription());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(item);
                }
            });
        }
    }

    private void deleteDialog(final EventModel item) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(item);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, null);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView kind;
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            kind = itemView.findViewById(R.id.tv_item_list_add_event_kind);
            description = itemView.findViewById(R.id.tv_item_list_add_event_description);
        }
    }


}
