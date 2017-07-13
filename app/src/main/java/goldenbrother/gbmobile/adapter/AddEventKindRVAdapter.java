package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AddEventActivity;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.model.AddEventModel;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by asus on 2016/6/22.
 */
public class AddEventKindRVAdapter extends SampleRVAdapter {

    private ArrayList<AddEventModel> list;

    public AddEventKindRVAdapter(Context context, ArrayList<AddEventModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_list_add_event,parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AddEventModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            // set kind
            h.kind.setText(item.getKindContent());
            // set description
            h.description.setText(item.getEventDescription());
            // listener
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete")
                            .setMessage("Delete it ?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = h.getAdapterPosition();
                                    // remove
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();

                }
            });
        }
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
            kind = (TextView) itemView.findViewById(R.id.tv_item_list_add_event_kind);
            description = (TextView) itemView.findViewById(R.id.tv_item_list_add_event_description);
        }
    }


}
