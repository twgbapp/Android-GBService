package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class ServiceGroupListAdapter extends SampleBaseAdapter {

    private ArrayList<ServiceChatModel> list;

    public ServiceGroupListAdapter(Context context, ArrayList<ServiceChatModel> list) {
        super(context);
        this.list = list;
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
            v = getInflater().inflate(R.layout.item_list_service, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final ServiceChatModel item = (ServiceChatModel) getItem(position);
        // set picture
        int wp = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
        if (!item.getWriterPicture().isEmpty()) {
            Picasso.with(getContext()).load(item.getWriterPicture()).placeholder(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        } else {
            Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(wp, wp).centerCrop().into(tag.picture);
        }
        // set user name
        String workerNo = item.getWorkerNo();
        if (workerNo != null && !workerNo.isEmpty()) {
            tag.userName.setText("(" + workerNo + ")" + item.getWriterName());
        } else {
            tag.userName.setText(item.getWriterName());
        }
        // set content
        tag.content.setText(getContent(item.getContent()));
        // set chat date
        if (!item.getChatDate().isEmpty())
            tag.chatDate.setText(TimeHelper.getContentTime(item.getChatDate()));
        // set read
        if (item.getChatCount() > 0) {
            tag.read.setVisibility(View.VISIBLE);
            tag.read.setText(String.format("%d", item.getChatCount()));
        } else {
            tag.read.setVisibility(View.GONE);
        }
        return v;
    }

    private CharSequence getContent(String content) {
        if (content.contains(Constant.QR)) {
            return "You have a package.";
        }
        return content;
    }

    private static class ViewHolder {
        CircleImageView picture;
        TextView userName;
        TextView content;
        TextView chatDate;
        TextView read;

        ViewHolder(View v) {
            picture = (CircleImageView) v.findViewById(R.id.iv_item_list_group_picture);
            userName = (TextView) v.findViewById(R.id.tv_item_list_group_user_name);
            content = (TextView) v.findViewById(R.id.tv_item_list_group_content);
            chatDate = (TextView) v.findViewById(R.id.tv_item_list_group_chat_date);
            read = (TextView) v.findViewById(R.id.tv_item_list_group_read);
        }
    }
}
