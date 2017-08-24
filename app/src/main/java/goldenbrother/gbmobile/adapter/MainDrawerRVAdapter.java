package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.CropActivity;
import goldenbrother.gbmobile.activity.MainActivity;
import goldenbrother.gbmobile.helper.ApiResultHelper;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.GenericFileProvider;
import goldenbrother.gbmobile.helper.SPHelper;
import goldenbrother.gbmobile.model.DrawerItem;
import goldenbrother.gbmobile.model.RoleInfo;

import static android.app.Activity.RESULT_OK;

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
    // request
    public static final int REQUEST_FROM_GALLERY = 12;
    public static final int REQUEST_TAKE_PHOTO = 13;
    public static final int REQUEST_TAKE_CROP = 14;
    // ui
    private CircleImageView iv_picture;
    //private EditText et_name, et_email;
    // take picture
    private Uri uriTakePicture;

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
            RoleInfo r = RoleInfo.getInstance();
            h.name.setText(r.getUserName());
            h.email.setText(r.getUserEmail());
            // set picture
            String picturePath = r.getUserPicture();
            if (picturePath != null && !picturePath.isEmpty()) {
                int w = (int) getResources().getDimension(R.dimen.imageview_profile_picture_width);
                Picasso.with(getContext())
                        .load(picturePath)
                        .resize(w, w)
                        .centerCrop()
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(h.picture);
            }
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getContext()).openProfileActivity();
                    //choosePicture();
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

    /*
    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPicture(BitmapHelper.resize(bmp, 300, 300));
            }
        }, null);
    }

    private void uploadPicture(Bitmap bmp) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "uploadImg");
            j.put("fileName", RoleInfo.getInstance().getUserID());
            j.put("baseStr", BitmapHelper.bitmap2JPGBase64(bmp));
            j.put("url", URLHelper.HOST);
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("logStatus", true);
            new UploadImageTask(this, j, URLHelper.HOST).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageTask extends IAsyncTask {
        private HashMap<String, String> map;

        UploadImageTask(Context context, JSONObject json, String url) {
            super(context, json, url);
            map = new HashMap<>();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.EMPTY:
                    int result = ApiResultHelper.uploadPicture(response, map);
                    if (result == ApiResultHelper.SUCCESS) {
                        updatePicture(map.get("path"));
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void updatePicture(String path) {
        try {
            JSONObject j = new JSONObject();
            j.put("action", "updatePicture");
            j.put("userID", RoleInfo.getInstance().getUserID());
            j.put("path", path);
            j.put("logStatus", true);
            new UpdatePicture(this, j, URLHelper.HOST, path).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class UpdatePicture extends IAsyncTask {

        private String path;

        UpdatePicture(Context context, JSONObject json, String url, String path) {
            super(context, json, url);
            this.path = path;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            switch (getResult()) {
                case ApiResultHelper.SUCCESS:
                case ApiResultHelper.FAIL:
                    int result = ApiResultHelper.commonCreate(response);
                    if (result == ApiResultHelper.SUCCESS) {
                        // set picture
                        RoleInfo.getInstance().setUserPicture(path);
                        // save user info
                        SPHelper.getInstance(MainDrawerRVAdapter.this).setUserInfo(RoleInfo.getInstance().getJSONObject());
                        t(R.string.success);
                        // get role instances
                        RoleInfo r = RoleInfo.getInstance();
                        // set picture
                        String picturePath = r.getUserPicture();
                        if (picturePath != null && !picturePath.isEmpty()) {
                            int w = (int) getResources().getDimension(R.dimen.imageview_profile_picture_width);
                            Picasso.with(MainDrawerRVAdapter.this)
                                    .load(picturePath)
                                    .resize(w, w)
                                    .centerCrop()
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(iv_picture);
                        }
                        setResult(RESULT_OK);
                    } else {
                        t(R.string.fail);
                    }
                    break;
            }
        }
    }

    private void choosePicture() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setItems(R.array.choose_picture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_FROM_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriTakePicture = FileProvider.getUriForFile(MainDrawerRVAdapter.this, GenericFileProvider.AUTH, new File(FileHelper.getPicturesDir(MainDrawerRVAdapter.this) + "/take_picture.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePicture);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        b.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bundle b = new Bundle();
        switch (requestCode) {
            case REQUEST_FROM_GALLERY:
                b.putString("uri", data.getData().toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_PHOTO:
                b.putString("uri", uriTakePicture.toString());
                b.putInt("ratioX", 1);
                b.putInt("ratioY", 1);
                openActivityForResult(CropActivity.class, REQUEST_TAKE_CROP, b);
                break;
            case REQUEST_TAKE_CROP:
                showImage(BitmapHelper.file2Bitmap(new File(data.getStringExtra("path"))));
                break;
        }
    }
    */
}
