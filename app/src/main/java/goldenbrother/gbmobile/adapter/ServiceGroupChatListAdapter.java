package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.QRCodeHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.ManagerModel;
import goldenbrother.gbmobile.model.RoleInfo;
import com.google.zxing.BarcodeFormat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 2016/11/16.
 */

public class ServiceGroupChatListAdapter extends SampleBaseAdapter {

    private ArrayList<ServiceChatModel> list;
    private String selfUserID;

    public ServiceGroupChatListAdapter(Context context, ArrayList<ServiceChatModel> list) {
        super(context);
        this.list = list;
        selfUserID = RoleInfo.getInstance().getUserID();
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
            v = getInflater().inflate(R.layout.item_list_service_chat, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final ServiceChatModel item = (ServiceChatModel) getItem(position);

        if (item.getWriterID().equals(SelfUserID)) { // self
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.RIGHT;
            tag.ll.setLayoutParams(p);
            // show left time
            tag.leftTime.setVisibility(View.VISIBLE);
            tag.rightTime.setVisibility(View.GONE);
            tag.leftTime.setText(TimeHelper.getTodayTime(item.getChatDate()));
            tag.rightTime.setText("");
            // show picture
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
        } else {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.LEFT;
            tag.ll.setLayoutParams(p);
            // show right time
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
        }
        // set name
        tag.name.setText(item.getWriterName());
        // set content or qrcode
        if (item.getContent().contains(Constant.QR)) {
            tag.qrcode.setVisibility(View.VISIBLE);
            tag.content.setVisibility(View.GONE);
            try {
                String code = item.getContent().substring(Constant.QR.length(), 13);
                int w = (int) (getResources().getDisplayMetrics().density * 200);
                Bitmap bmp = QRCodeHelper.encodeAsBitmap(code, BarcodeFormat.QR_CODE, w, w);
                tag.qrcode.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
                tag.qrcode.setImageResource(R.drawable.logo_gb1);
            }
        } else {
            tag.qrcode.setVisibility(View.GONE);
            tag.content.setVisibility(View.VISIBLE);
            tag.content.setText(item.getContent());
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
        return v;
    }

    private static class ViewHolder {
        LinearLayout ll;
        TextView name;
        CircleImageView leftPicture, rightPicture;
        ImageView qrcode;
        TextView content;
        TextView leftTime, rightTime;

        ViewHolder(View v) {
            ll = (LinearLayout) v.findViewById(R.id.ll_item_list_group_chat);
            name = (TextView) v.findViewById(R.id.tv_item_list_group_chat_name);
            leftPicture = (CircleImageView) v.findViewById(R.id.iv_item_list_group_chat_left_picture);
            rightPicture = (CircleImageView) v.findViewById(R.id.iv_item_list_group_chat_right_picture);
            qrcode = (ImageView) v.findViewById(R.id.iv_item_list_group_chat_qrcode);
            content = (TextView) v.findViewById(R.id.tv_item_list_group_chat_content);
            leftTime = (TextView) v.findViewById(R.id.tv_item_list_group_chat_left_time);
            rightTime = (TextView) v.findViewById(R.id.tv_item_list_group_chat_right_time);
        }
    }
}
