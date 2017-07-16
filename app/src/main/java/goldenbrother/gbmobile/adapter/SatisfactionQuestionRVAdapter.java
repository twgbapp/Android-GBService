package goldenbrother.gbmobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.SatisfactionQuestionActivity;
import goldenbrother.gbmobile.model.SatisfactionQuestionModel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by asus on 2016/6/22.
 */
public class SatisfactionQuestionRVAdapter extends SampleRVAdapter {

    // data
    private ArrayList<SatisfactionQuestionModel> list;
    private HashSet<SatisfactionQuestionModel> set;

    public SatisfactionQuestionRVAdapter(Context context, ArrayList<SatisfactionQuestionModel> list) {
        super(context);
        this.list = list;
        this.set = new HashSet<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_satisfaction_question, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SatisfactionQuestionModel item = list.get(position);
        if (holder instanceof ViewHolder) {
            final ViewHolder h = (ViewHolder) holder;
            h.name.setText(item.getQuestion());
            h.status.setText(set.contains(item) ? R.string.already_rating : R.string.not_rating);
            h.status.setTextColor(ContextCompat.getColor(getContext(), set.contains(item) ? R.color.main_light_yellow : R.color.main_light_gray));
            h.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        showConfirmDialog(item, (int) rating, h.getAdapterPosition());
                    }
                }
            });
            // listener
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRatingDialog(item);
                }
            });
        }
    }

    private void showConfirmDialog(final SatisfactionQuestionModel item, final int rating, final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.rating)
                .setMessage(R.string.confirm_to_rating)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SatisfactionQuestionActivity) getContext()).addSatisfactionIssueRecord(item.getSiqNo(), rating);
                        set.add(item);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private AlertDialog ad_rating;

    private void showRatingDialog(final SatisfactionQuestionModel item) {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        final View v = getInflater().inflate(R.layout.dialog_satisfaction_rating, null);
        final RatingBar rb = (RatingBar) v.findViewById(R.id.rb_dialog_satisfaction_rating);
        // init RatingBar
        rb.setRating(5);
        v.findViewById(R.id.tv_dialog_satisfaction_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_rating.dismiss();
            }
        });
        v.findViewById(R.id.tv_dialog_satisfaction_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_rating.dismiss();

            }
        });
        b.setView(v);
        ad_rating = b.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        RatingBar rating;

        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tv_item_rv_satisfaction_question_name);
            status = (TextView) v.findViewById(R.id.tv_item_rv_satisfaction_question_status);
            rating = (RatingBar) v.findViewById(R.id.rb_item_rv_satisfaction_question_rating);
        }
    }


}
