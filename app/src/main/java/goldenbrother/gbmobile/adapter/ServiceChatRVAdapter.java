package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AddClubPostActivity;
import goldenbrother.gbmobile.activity.AddClubPostMessageActivity;
import goldenbrother.gbmobile.activity.ClubPostActivity;
import goldenbrother.gbmobile.activity.ClubPostMediaActivity;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.QRCodeHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.ServiceChatModel;

/**
 * Created by asus on 2017/1/21.
 */

public class ServiceChatRVAdapter extends SampleRVAdapter {

    // type
    private static final int OTHER = 0;
    private static final int SELF = 1;
    // data
    private ArrayList<ServiceChatModel> list;
    private String selfUserID;
    private String am, pm;

    public ServiceChatRVAdapter(Context context, ArrayList<ServiceChatModel> list) {
        super(context);
        this.list = list;
        this.selfUserID = RoleInfo.getInstance().getUserID();
        this.am = getResources().getString(R.string.pm);
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
            return new OtherViewHolder(getInflater().inflate(R.layout.item_rv_service_chat_other, parent, false));
        } else if (viewType == SELF) {
            return new SelfViewHolder(getInflater().inflate(R.layout.item_rv_service_chat_self, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ServiceChatModel item = list.get(position);
        if (holder instanceof OtherViewHolder) {
            final OtherViewHolder h = (OtherViewHolder) holder;
            Picasso.with(getContext()).load(item.getWriterPicture()).into(h.picture);
            setContent(item, h.date, h.content, h.qrCode);
        } else if (holder instanceof SelfViewHolder) {
            SelfViewHolder h = (SelfViewHolder) holder;
            setContent(item, h.date, h.content, h.qrCode);
        }
    }

    private void setContent(ServiceChatModel item, TextView date, final TextView content, ImageView qrCode) {
        date.setText(TimeHelper.getTodayTime(item.getChatDate(), am, pm));
        content.setText(item.getContent());
        if (item.getContent().contains(Constant.QR)) {
            try {
                qrCode.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                String code = item.getContent().substring(Constant.QR.length(), 13);
                int w = (int) (getResources().getDisplayMetrics().density * 200);
                Bitmap bmp = QRCodeHelper.encodeAsBitmap(code, BarcodeFormat.QR_CODE, w, w);
                qrCode.setImageBitmap(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            qrCode.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
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
        ImageView qrCode;

        OtherViewHolder(View v) {
            super(v);
            picture = (ImageView) v.findViewById(R.id.iv_item_rv_service_chat_other_picture);
            date = (TextView) v.findViewById(R.id.tv_item_rv_service_chat_other_date);
            content = (TextView) v.findViewById(R.id.tv_item_rv_service_chat_other_content);
            qrCode = (ImageView) v.findViewById(R.id.iv_item_rv_service_chat_other_qr_code);
        }
    }

    private class SelfViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView date;
        ImageView qrCode;

        SelfViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.tv_item_rv_service_chat_self_content);
            date = (TextView) v.findViewById(R.id.tv_item_rv_service_chat_self_date);
            qrCode = (ImageView) v.findViewById(R.id.iv_item_rv_service_chat_self_qr_code);
        }
    }
}
