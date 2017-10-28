package goldenbrother.gbmobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.Constant;
import goldenbrother.gbmobile.helper.LogHelper;
import goldenbrother.gbmobile.helper.QRCodeHelper;
import goldenbrother.gbmobile.helper.TimeHelper;
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
        this.am = getResources().getString(R.string.am);
        this.pm = getResources().getString(R.string.pm);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getUserID().equals(selfUserID) ? SELF : OTHER;
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
            String picturePath = item.getUserPicture();
            if (picturePath != null && !picturePath.isEmpty()) {
                Picasso.with(getContext())
                        .load(picturePath)
                        .into(h.picture);
            }
            h.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(item);
                }
            });
            setContent(item, h.date, h.content, h.qrCode, h.title);
        } else if (holder instanceof SelfViewHolder) {
            SelfViewHolder h = (SelfViewHolder) holder;
            setContent(item, h.date, h.content, h.qrCode);
        }
    }

    private void showProfile(ServiceChatModel item) {
        View v = getInflater().inflate(R.layout.dialog_profile, null);
        ImageView iv_picture = v.findViewById(R.id.iv_dialog_profile_picture);
        TextView tv_name = v.findViewById(R.id.tv_dialog_profile_name);
        TextView tv_worker_no = v.findViewById(R.id.tv_dialog_profile_worker_no);
        TextView tv_customer_no = v.findViewById(R.id.tv_dialog_profile_customer_no);

        String picturePath = item.getUserPicture();
        if (picturePath != null && !picturePath.isEmpty()) {
            Picasso.with(getContext())
                    .load(picturePath)
                    .into(iv_picture);
        }
        tv_name.setText(item.getUserName());
        tv_worker_no.setText(item.getWorkerNo());
        tv_customer_no.setText(item.getCustomerNo());

        new AlertDialog.Builder(getContext())
                .setView(v)
                .show();
    }

    private void displayPopupWindow(View anchorView, ServiceChatModel item) {
        PopupWindow popup = new PopupWindow(getContext());
        View v = getInflater().inflate(R.layout.dialog_pop_window, null);
        TextView tv = v.findViewById(R.id.tv_dialog_pop_window_name);
        tv.setText(item.getUserName());
        popup.setContentView(v);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);
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

    private void setContent(ServiceChatModel item, TextView date, final TextView content, ImageView qrCode, TextView title) {
        date.setText(TimeHelper.getTodayTime(item.getChatDate(), am, pm));
        content.setText(item.getContent());
        title.setText(item.getUserName());
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
        TextView title;

        OtherViewHolder(View v) {
            super(v);
            picture = v.findViewById(R.id.iv_item_rv_service_chat_other_picture);
            date = v.findViewById(R.id.tv_item_rv_service_chat_other_date);
            content = v.findViewById(R.id.tv_item_rv_service_chat_other_content);
            qrCode = v.findViewById(R.id.iv_item_rv_service_chat_other_qr_code);
            title = v.findViewById(R.id.tv_item_rv_service_chat_other_title);
        }
    }

    private class SelfViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView date;
        ImageView qrCode;

        SelfViewHolder(View v) {
            super(v);
            content = v.findViewById(R.id.tv_item_rv_service_chat_self_content);
            date = v.findViewById(R.id.tv_item_rv_service_chat_self_date);
            qrCode = v.findViewById(R.id.iv_item_rv_service_chat_self_qr_code);
        }
    }
}
