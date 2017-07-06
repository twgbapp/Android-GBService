package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.EventChatModel;
import goldenbrother.gbmobile.model.RoleInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class EventChatListAdapter extends SampleBaseAdapter {

    private ArrayList<EventChatModel> list;
    private String SelfUserID;

    public EventChatListAdapter(Context context, ArrayList<EventChatModel> list) {
        super(context);
        this.list = list;
        this.SelfUserID = RoleInfo.getInstance().getUserID();
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
            v = getInflater().inflate(R.layout.item_list_event_chat, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final EventChatModel item = (EventChatModel) getItem(position);

        if (item.getWriterID().equals(SelfUserID)) { // self
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.END;
            tag.ll_main.setLayoutParams(p);
            // set time
            tag.leftTime.setVisibility(View.VISIBLE);
            tag.rightTime.setVisibility(View.GONE);
            tag.leftTime.setText(TimeHelper.getTodayTime(item.getChatDate()));
            tag.rightTime.setText("");
            // set picture
            tag.leftPicture.setVisibility(View.GONE);
            tag.rightPicture.setVisibility(View.VISIBLE);
            if (item.getWriterPicture() != null && !item.getWriterPicture().isEmpty()) {
                int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
                Picasso.with(getContext()).load(item.getWriterPicture()).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(tag.leftPicture);
                Picasso.with(getContext()).load(item.getWriterPicture()).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(tag.rightPicture);
            } else {
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).into(tag.leftPicture);
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).into(tag.rightPicture);
            }
            // set name gravity
            tag.name.setLayoutParams(p);
            // set content
            tag.content.setText(item.getContent());
            tag.content.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_chat_self));
            tag.content.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.START;
            tag.ll_main.setLayoutParams(p);
            // set time
            tag.rightTime.setVisibility(View.VISIBLE);
            tag.leftTime.setVisibility(View.GONE);
            tag.rightTime.setText(TimeHelper.getTodayTime(item.getChatDate()));
            tag.leftTime.setText("");
            // show picture
            tag.leftPicture.setVisibility(View.VISIBLE);
            tag.rightPicture.setVisibility(View.GONE);
            if (item.getWriterPicture() != null && !item.getWriterPicture().isEmpty()) {
                int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
                Picasso.with(getContext()).load(item.getWriterPicture()).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(tag.leftPicture);
                Picasso.with(getContext()).load(item.getWriterPicture()).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(tag.rightPicture);
            } else {
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).into(tag.leftPicture);
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).into(tag.rightPicture);
            }
            // set name gravity
            tag.name.setLayoutParams(p);
            // set content
            tag.content.setText(item.getContent());
            tag.content.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_chat_other));
            tag.content.setTextColor(Color.WHITE);
        }
        // set name
        tag.name.setText(item.getWriterName());
        // rating
        Log.d("rate", item.getContent());
        if (item.getContent().contains(Constant.RATING)) {
            tag.ll_message.setVisibility(View.GONE);
            tag.ll_rating.setVisibility(View.VISIBLE);
        } else {
            tag.ll_message.setVisibility(View.VISIBLE);
            tag.ll_rating.setVisibility(View.GONE);
        }
        // listener
        tag.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(getContext());
                et.setText(((TextView) view).getText());
                new AlertDialog.Builder(getContext()).setView(et).show();
            }
        });
        tag.ll_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventChatActivity) getContext()).showRatingDialog();
            }
        });
        return v;
    }

    private static class ViewHolder {
        LinearLayout ll_main;
        LinearLayout ll_message;
        LinearLayout ll_rating;
        TextView name;
        CircleImageView leftPicture, rightPicture;
        TextView content;
        TextView leftTime, rightTime;

        ViewHolder(View v) {
            ll_main = (LinearLayout) v.findViewById(R.id.ll_item_list_event_main);
            ll_rating = (LinearLayout) v.findViewById(R.id.ll_item_list_event_rating);
            ll_message = (LinearLayout) v.findViewById(R.id.ll_item_list_event_message);
            name = (TextView) v.findViewById(R.id.tv_item_list_event_chat_name);
            content = (TextView) v.findViewById(R.id.tv_item_list_event_chat_content);
            leftPicture = (CircleImageView) v.findViewById(R.id.iv_item_list_event_chat_left_picture);
            rightPicture = (CircleImageView) v.findViewById(R.id.iv_item_list_event_chat_right_picture);
            leftTime = (TextView) v.findViewById(R.id.tv_item_list_event_chat_left_time);
            rightTime = (TextView) v.findViewById(R.id.tv_item_list_event_chat_right_time);
        }
    }
}
