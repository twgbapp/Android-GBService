package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.fragment.ServiceListFragment;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.AddEventModel;
import goldenbrother.gbmobile.model.ServiceChatModel;

public class ServiceListRVAdapter extends SampleRVAdapter {

    private ArrayList<ServiceChatModel> list;
    private ServiceListFragment callBack;

    public ServiceListRVAdapter(Context context, ArrayList<ServiceChatModel> list, ServiceListFragment callBack) {
        super(context);
        this.list = list;
        this.callBack = callBack;
    }

    public void setServiceChatList(ArrayList<ServiceChatModel> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_service_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ServiceChatModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            // set picture
            int wp = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
            String picturePath = item.getUserPicture();
            if (picturePath != null && !picturePath.isEmpty()) {
                Picasso.with(getContext()).load(item.getUserPicture()).resize(wp, wp).centerCrop().into(h.picture);
            } else {
                h.picture.setImageResource(R.drawable.ic_person_replace);
            }
            // set user name
            String workerNo = item.getWorkerNo();
            if (workerNo != null && !workerNo.isEmpty()) {
                h.userName.setText(String.format("(%s)%s", workerNo, item.getUserName()));
            } else {
                h.userName.setText(item.getUserName());
            }
            // set content
            h.content.setText(getContent(item.getContent()));
            // set chat date
            if (!item.getChatDate().isEmpty())
                h.chatDate.setText(TimeHelper.getContentTime(item.getChatDate()));
            // set read
            if (item.getChatCount() > 0) {
                h.read.setVisibility(View.VISIBLE);
                h.read.setText(String.format("%d", item.getChatCount()));
            } else {
                h.read.setVisibility(View.GONE);
            }
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null)
                        callBack.onItemClick(item);
                }
            });
        }
    }

    private CharSequence getContent(String content) {
        if (content.contains(Constant.QR)) {
            return getResources().getString(R.string.package_request);
        }
        return content;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView picture;
        TextView userName;
        TextView content;
        TextView chatDate;
        TextView read;

        ViewHolder(View v) {
            super(v);
            picture = v.findViewById(R.id.iv_item_list_group_picture);
            userName = v.findViewById(R.id.tv_item_list_group_user_name);
            content = v.findViewById(R.id.tv_item_list_group_content);
            chatDate = v.findViewById(R.id.tv_item_list_group_chat_date);
            read = v.findViewById(R.id.tv_item_list_group_read);
        }
    }


}
