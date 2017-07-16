package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.EventChatActivity;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.QRCodeHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.EventChatModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.ServiceChatModel;

/**
 * Created by asus on 2017/1/21.
 */

public class EventChatRVAdapter extends SampleRVAdapter {

    // type
    private static final int OTHER = 0;
    private static final int SELF = 1;
    // data
    private ArrayList<EventChatModel> list;
    private String selfUserID;
    private String am, pm;

    public EventChatRVAdapter(Context context, ArrayList<EventChatModel> list) {
        super(context);
        this.list = list;
        this.selfUserID = RoleInfo.getInstance().getUserID();
        this.am = getResources().getString(R.string.am);
        this.pm = getResources().getString(R.string.pm);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getWriterID().equals(selfUserID) ? SELF : OTHER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == OTHER) {
            return new OtherViewHolder(getInflater().inflate(R.layout.item_rv_event_chat_other, parent, false));
        } else if (viewType == SELF) {
            return new SelfViewHolder(getInflater().inflate(R.layout.item_rv_event_chat_self, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EventChatModel item = list.get(position);
        if (holder instanceof OtherViewHolder) {
            final OtherViewHolder h = (OtherViewHolder) holder;
            Picasso.with(getContext()).load(item.getWriterPicture()).into(h.picture);
            setContent(item, h.date, h.content, h.rating);
        } else if (holder instanceof SelfViewHolder) {
            SelfViewHolder h = (SelfViewHolder) holder;
            setContent(item, h.date, h.content, h.rating);
        }
    }

    private void setContent(EventChatModel item, TextView date,final TextView content, View rating) {
        date.setText(TimeHelper.getTodayTime(item.getChatDate(), am, pm));
        content.setText(item.getContent());
        if (item.getContent().contains(Constant.RATING)) {
            rating.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        } else {
            rating.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventChatActivity) getContext()).showRatingDialog();
            }
        });
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipboard(content.getText().toString());
                Toast.makeText(getContext(), R.string.copy_to_clipboard, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private boolean copyToClipboard(String text) {
        Context context = getContext();
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("copy", text);
            clipboard.setPrimaryClip(clip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private class OtherViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView date;
        TextView content;
        View rating;

        OtherViewHolder(View v) {
            super(v);
            picture = (ImageView) v.findViewById(R.id.iv_item_rv_event_chat_other_picture);
            date = (TextView) v.findViewById(R.id.tv_item_rv_event_chat_other_date);
            content = (TextView) v.findViewById(R.id.tv_item_rv_event_chat_other_content);
            rating = v.findViewById(R.id.iv_item_rv_event_chat_other_rating);
        }
    }

    private class SelfViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView date;
        View rating;

        SelfViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.tv_item_rv_event_chat_self_content);
            date = (TextView) v.findViewById(R.id.tv_item_rv_event_chat_self_date);
            rating = v.findViewById(R.id.iv_item_rv_event_chat_self_rating);
        }
    }
}
