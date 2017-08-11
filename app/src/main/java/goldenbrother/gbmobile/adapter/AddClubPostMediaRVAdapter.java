package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddClubPostMediaRVAdapter extends SampleRVAdapter {

    // data
    private ArrayList<ClubPostMediaModel> list;
    private final int width;

    public AddClubPostMediaRVAdapter(Context context, ArrayList<ClubPostMediaModel> list) {
        super(context);
        this.list = list;
        this.width = getResources().getDisplayMetrics().widthPixels / 3;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_club_post_media, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ClubPostMediaModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            switch (item.getType()) {
                case ClubPostMediaModel.IMAGE:
                    Picasso.with(getContext()).load(item.getFile()).resize(width, width).centerCrop().into(h.image);
                    break;
                case ClubPostMediaModel.VIDEO:
                    Picasso.with(getContext()).load(item.getThumbNailPath()).resize(width, width).centerCrop().into(h.image);
                    break;
            }
            h.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete)
                            .setMessage(R.string.confirm_delete)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // remove
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_item_rv_club_post_media_image);
        }
    }


}
