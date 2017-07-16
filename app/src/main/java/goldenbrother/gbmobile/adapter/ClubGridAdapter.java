package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.ClubModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class ClubGridAdapter extends SampleBaseAdapter {

    private final int w;
    private ArrayList<ClubModel> list;

    public ClubGridAdapter(Context context, ArrayList<ClubModel> list) {
        super(context);
        this.list = list;
        this.w = (int) (100 * getResources().getDisplayMetrics().density);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_grid_club, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final ClubModel item = (ClubModel) getItem(position);
        // set name
        tag.name.setText(item.getClubName());
        // set picture
        if (item.getClubPicture() != null && !item.getClubPicture().isEmpty()) {
            Picasso.with(getContext()).load(item.getClubPicture()).resize(w, w).centerCrop().into(tag.picture);
        }
        return v;
    }

    private static class ViewHolder {

        ImageView picture;
        TextView name;

        ViewHolder(View v) {
            picture = (ImageView) v.findViewById(R.id.iv_item_grid_club_picture);
            name = (TextView) v.findViewById(R.id.tv_item_grid_club_name);
        }
    }
}
