package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.BasicUser;
import goldenbrother.gbmobile.model.BasicUser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnCallManagerListAdapter extends SampleBaseAdapter {

    private ArrayList<BasicUser> list;
    private int selectedPosition = -1;

    public OnCallManagerListAdapter(Context context, ArrayList<BasicUser> list) {
        super(context);
        this.list = list;
    }

    public int getSelectedPosition() {
        return selectedPosition;
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
    public View getView(final int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list_on_call_manager, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final BasicUser item = (BasicUser) getItem(position);
        // set picture
        int wp = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
        if (item.getUserPicture() != null && !item.getUserPicture().isEmpty()) {
            Picasso.with(getContext())
                    .load(item.getUserPicture())
                    .placeholder(R.drawable.ic_person_replace)
                    .resize(wp, wp)
                    .centerCrop()
                    .into(tag.picture);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.ic_person_replace)
                    .resize(wp, wp)
                    .centerCrop()
                    .into(tag.picture);
        }
        // set name
        tag.name.setText(item.getUserID());
        // set check
        tag.check.setVisibility(selectedPosition == position ? View.VISIBLE : View.GONE);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
        return v;
    }

    private static class ViewHolder {
        CircleImageView picture;
        TextView name;
        View check;

        ViewHolder(View v) {
            picture = v.findViewById(R.id.iv_item_list_on_call_manager_picture);
            name = v.findViewById(R.id.iv_item_list_on_call_manager_name);
            check = v.findViewById(R.id.rl_item_list_on_call_manager_check);
        }
    }
}
