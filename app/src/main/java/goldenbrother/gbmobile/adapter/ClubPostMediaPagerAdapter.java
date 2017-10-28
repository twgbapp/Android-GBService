package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.ClubPostMediaActivity;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by asus on 2017/1/4.
 */

public class ClubPostMediaPagerAdapter extends SamplePagerAdapter {

    private ArrayList<ClubPostMediaModel> list;

    public ClubPostMediaPagerAdapter(Context context, ArrayList<ClubPostMediaModel> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View v = getInflater().inflate(R.layout.item_pager_club_post_media, container, false);
        ViewHolder tag = new ViewHolder(v);
        v.setTag(tag);
        final ClubPostMediaModel item = list.get(position);
        if (item.getThumbNailPath() != null && !item.getThumbNailPath().isEmpty()) {
            Picasso.with(getContext()).load(item.getThumbNailPath()).into(tag.picture);
        }
        // listener
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ClubPostMediaActivity) getContext()).fullScreen();
            }
        });
        container.addView(v);
        return v;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class ViewHolder {

        ImageView picture;

        ViewHolder(View v) {
            picture = v.findViewById(R.id.iv_item_pager_club_post_media_image);
        }
    }
}
