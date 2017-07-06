package goldenbrother.gbmobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.activity.SatisfactionQuestionActivity;
import goldenbrother.gbmobile.model.SatisfactionQuestionModel;

import java.util.ArrayList;

/**
 * Created by asus on 2016/6/22.
 */
public class SatisfactionQuestionRVAdapter extends SampleRVAdapter {

    private ArrayList<SatisfactionQuestionModel> list;

    public SatisfactionQuestionRVAdapter(Context context, ArrayList<SatisfactionQuestionModel> list) {
        super(context);
        this.list = list;
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
            ViewHolder h = (ViewHolder) holder;
            // set name
            h.name.setText(item.getQuestion());
            // listener
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRatingDialog(item);
                }
            });
        }
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
                ((SatisfactionQuestionActivity) getContext()).addSatisfactionIssueRecord(item.getSiqNo(), (int) rb.getRating());
            }
        });
        b.setView(v);
        ad_rating = b.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tv_item_rv_satisfaction_question_name);
        }
    }


}
