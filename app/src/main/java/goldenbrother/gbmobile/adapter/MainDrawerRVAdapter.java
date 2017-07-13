package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.MainActivity;
import goldenbrother.gbmobile.model.DrawerItem;
import goldenbrother.gbmobile.model.RoleInfo;

/**
 * Created by asus on 2016/6/22.
 */
public class MainDrawerRVAdapter extends SampleRVAdapter {

    // type
    private static final int HEAD = -1;
    private static final int GROUP = 0;
    private static final int CHILD = 1;
    // data
    private ArrayList<DrawerItem> list;

    public MainDrawerRVAdapter(Context context, ArrayList<DrawerItem> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return HEAD;
        return list.get(position - 1).getType() == DrawerItem.GROUP ? GROUP : CHILD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadViewHolder(getInflater().inflate(R.layout.item_rv_main_drawer_head, parent, false));
        } else if (viewType == GROUP) {
            return new GroupViewHolder(getInflater().inflate(R.layout.item_rv_main_drawer_group, parent, false));
        } else if (viewType == CHILD) {
            return new ChildViewHolder(getInflater().inflate(R.layout.item_rv_main_drawer_child, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder h = (HeadViewHolder) holder;
            h.name.setText(RoleInfo.getInstance().getUserName());
            h.email.setText(RoleInfo.getInstance().getUserEmail());
            Picasso.with(getContext()).load(RoleInfo.getInstance().getUserPicture()).into(h.picture);
            h.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getContext()).openProfileActivity();
                }
            });
        } else if (holder instanceof GroupViewHolder) {
            final DrawerItem item = list.get(position - 1);
            GroupViewHolder h = (GroupViewHolder) holder;
            h.icon.setImageResource(item.getIcon());
            h.name.setText(item.getName());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getContext()).onDrawerItemClick(item.getName());
                }
            });
        } else if (holder instanceof ChildViewHolder) {
            final DrawerItem item = list.get(position - 1);
            ChildViewHolder h = (ChildViewHolder) holder;
//            h.icon.setImageResource(item.getIcon());
            h.icon.setBackgroundColor(Color.WHITE);
            h.name.setText(item.getName());
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getContext()).onDrawerItemClick(item.getName());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1 + list.size();
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name;
        TextView email;
        View edit;

        HeadViewHolder(View v) {
            super(v);
            picture = (ImageView) v.findViewById(R.id.iv_item_rv_main_drawer_head_picture);
            name = (TextView) v.findViewById(R.id.tv_item_rv_main_drawer_head_name);
            email = (TextView) v.findViewById(R.id.tv_item_rv_main_drawer_head_email);
            edit = v.findViewById(R.id.tv_item_rv_main_drawer_head_edit);
        }
    }

    private class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        GroupViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.iv_item_rv_main_drawer_group_icon);
            name = (TextView) v.findViewById(R.id.tv_item_rv_main_drawer_group_name);
        }
    }

    private class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        ChildViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.iv_item_rv_main_drawer_child_icon);
            name = (TextView) v.findViewById(R.id.tv_item_rv_main_drawer_child_name);
        }
    }

}
