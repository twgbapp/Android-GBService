package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.AddClubPostActivity;
import goldenbrother.gbmobile.activity.AddClubPostMessageActivity;
import goldenbrother.gbmobile.activity.ClubPostActivity;
import goldenbrother.gbmobile.activity.ClubPostMediaActivity;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.RoleInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by asus on 2017/1/21.
 */

public class ClubPostRVAdapter extends SampleRVAdapter {
    // type
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_POST = 1;
    // pixel
    private final int widthPixel;
    private final int heightPixel;
    //
    private ClubModel club;
    private ArrayList<ClubPostModel> list;

    public ClubPostRVAdapter(Context context, ClubModel club, ArrayList<ClubPostModel> list) {
        super(context);
        this.widthPixel = getResources().getDisplayMetrics().widthPixels;
        this.heightPixel = (int) (getResources().getDisplayMetrics().density * 200);
        this.club = club;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return 1 + list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_POST;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeadViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_rv_club_post_head, parent, false));
        } else if (viewType == TYPE_POST) {
            return new BodyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_rv_club_post_body, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder h = (HeadViewHolder) holder;
            // set club picture
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int) getResources().getDimension(R.dimen.imageview_club_post_head_height);
            Picasso.with(getContext()).load(club.getClubPicture()).into(h.clubPicture);
            // set club name
            h.clubName.setText(club.getClubName());
            // set user picture
            int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
            String picture = RoleInfo.getInstance().getUserPicture();
            if (picture != null && !picture.isEmpty()){
                Picasso.with(getContext()).load(picture).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(h.userPicture);
            }else{
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(h.userPicture);
            }

            // set listener
            h.addPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddClubPostActivity.class);
                    intent.putExtra("clubID", club.getClubID());
                    ((ClubPostActivity) getContext()).startActivityForResult(intent, ClubPostActivity.REQUEST_ADD_POST);
                }
            });
        } else if (holder instanceof BodyViewHolder) {
            BodyViewHolder h = (BodyViewHolder) holder;
            final ClubPostModel item = list.get(position - 1);
            // set user picture
            if (item.getPostUserPicture() != null && !item.getPostUserPicture().isEmpty()) {
                int w = (int) getResources().getDimension(R.dimen.imageview_picture_in_list_width);
                Picasso.with(getContext()).load(item.getPostUserPicture()).placeholder(R.drawable.ic_person_replace).resize(w, w).centerCrop().into(h.userPicture);
            } else {
                Picasso.with(getContext()).load(R.drawable.ic_person_replace).into(h.userPicture);
            }
            // set user name
            h.userName.setText(item.getPostUserName());
            // set create date
            h.createDate.setText(item.getCreateDate());
            // set content
            h.content.setText(item.getContent());
            // set media
            final ArrayList<ClubPostMediaModel> list_media = item.getMedias();
            if (!list_media.isEmpty() && list_media.get(0).getThumbNailPath() != null && !list_media.get(0).getThumbNailPath().isEmpty()) {
                final ClubPostMediaModel m = list_media.get(0);
                final int type = m.getType();
                // show thumbnail pic
                h.thumbNailPicture.setVisibility(View.VISIBLE);
                // is video ?
                h.videoPlay.setVisibility(type == ClubPostMediaModel.VIDEO ? View.VISIBLE : View.GONE);
                Picasso.with(getContext()).load(m.getThumbNailPath()).resize(widthPixel, heightPixel).centerCrop().into(h.thumbNailPicture);
                h.thumbNailPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = m.getUrlPath();
                        if (type == ClubPostMediaModel.VIDEO) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            getContext().startActivity(intent);
                        } else if (type == ClubPostMediaModel.IMAGE) {
                            //showImageDialog(url);
                            Intent intent = new Intent();
                            intent.setClass(getContext(), ClubPostMediaActivity.class);
                            intent.putParcelableArrayListExtra("media", list_media);
                            getContext().startActivity(intent);
                        }
                    }
                });
            } else {
                h.videoPlay.setVisibility(View.GONE);
                h.thumbNailPicture.setVisibility(View.GONE);
            }
            // set comment listener
            h.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), AddClubPostMessageActivity.class);
                    intent.putExtra("clubPost",item);
                    ((ClubPostActivity) getContext()).startActivityForResult(intent, ClubPostActivity.REQUEST_ADD_POST_MESSAGE);
                }
            });
            // set message count
            h.messageCount.setText(String.format("%d", item.getMessageCount()));
        }
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        ImageView clubPicture;
        TextView clubName;
        ImageView userPicture;
        View addPost;

        HeadViewHolder(View v) {
            super(v);
            clubPicture = (ImageView) v.findViewById(R.id.iv_item_rv_club_post_head_club_picture);
            clubName = (TextView) v.findViewById(R.id.tv_item_rv_club_post_head_club_name);
            userPicture = (ImageView) v.findViewById(R.id.iv_item_rv_club_post_head_user_picture);
            addPost = v.findViewById(R.id.ll_item_rv_club_post_head_add_post);
        }
    }

    private class BodyViewHolder extends RecyclerView.ViewHolder {

        ImageView userPicture;
        TextView userName;
        TextView createDate;
        TextView content;
        ImageView thumbNailPicture;
        ImageView videoPlay;
        TextView messageCount;
        TextView comment;

        BodyViewHolder(View v) {
            super(v);
            userPicture = (ImageView) v.findViewById(R.id.iv_item_rv_club_post_body_user_picture);
            userName = (TextView) v.findViewById(R.id.tv_item_rv_club_post_body_user_name);
            createDate = (TextView) v.findViewById(R.id.tv_item_rv_club_post_body_create_date);
            content = (TextView) v.findViewById(R.id.tv_item_rv_club_post_body_content);
            thumbNailPicture = (ImageView) v.findViewById(R.id.iv_item_rv_club_post_body_thumbnail_pic);
            videoPlay = (ImageView) v.findViewById(R.id.iv_item_rv_club_post_body_video_play);
            messageCount = (TextView) v.findViewById(R.id.tv_item_rv_club_post_body_message_count);
            comment= (TextView) v.findViewById(R.id.tv_item_rv_club_post_body_comment);
        }
    }
}
