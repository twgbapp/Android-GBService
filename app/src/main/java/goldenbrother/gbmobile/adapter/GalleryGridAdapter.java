package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.GalleryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by asus on 2016/7/5.
 */

public class GalleryGridAdapter extends SampleBaseAdapter {

    private ArrayList<Integer> selected;
    private ArrayList<GalleryModel> list;
    private int w;

    public GalleryGridAdapter(Context context, ArrayList<GalleryModel> list) {
        super(context);
        this.list = list;
        this.selected = new ArrayList<>();
        this.w = getContext().getResources().getDisplayMetrics().widthPixels / 3;
    }

    public ArrayList<Integer> getSeleted() {
        return selected;
    }

    public void setSelected(ArrayList<Integer> selected) {
        this.selected.clear();
        this.selected.addAll(selected);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int id) {
        return 0;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_grid_gallery, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final GalleryModel item = (GalleryModel) getItem(position);
        // set image
        Picasso.with(getContext()).load(item.getUri()).resize(w, w).centerCrop().into(tag.image);
        // set select or non-selected
        if (selected.contains(position)) {
            tag.background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            tag.background.setBackgroundColor(Color.TRANSPARENT);
        }
        // set listener
        tag.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = position;
                if (selected.contains(i)) { // seleted >> non selected
                    selected.remove(i);
                } else { // non selected >> selected
                    selected.add(i);
                }
                notifyDataSetChanged();
            }
        });

        return v;
    }



    class ViewHolder {

        RelativeLayout background;
        ImageView image;
        TextView order;

        ViewHolder(View v) {
            background = (RelativeLayout) v.findViewById(R.id.rl_item_grid_gallery_background);
            image = (ImageView) v.findViewById(R.id.iv_item_grid_gallery_image);
            order = (TextView) v.findViewById(R.id.tv_item_grid_gallery_order);
        }
    }
}
