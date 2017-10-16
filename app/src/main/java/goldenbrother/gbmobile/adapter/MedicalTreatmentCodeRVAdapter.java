package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.MedicalSymptomModel;

public class MedicalTreatmentCodeRVAdapter extends SampleRVAdapter {

    // type
    private static final int GROUP = 0;
    private static final int CHILD = 1;
    // data
    private ArrayList<MedicalSymptomModel> list;
    private HashSet<MedicalSymptomModel> set;

    public MedicalTreatmentCodeRVAdapter(Context context, ArrayList<MedicalSymptomModel> list) {
        super(context);
        this.list = list;
        this.set = new HashSet<>();
    }

    public void setSelected(ArrayList<MedicalSymptomModel> list) {
        set.clear();
        set.addAll(list);
        notifyDataSetChanged();
    }

    public HashSet<MedicalSymptomModel> getSelected() {
        return set;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getCode().length() == 1 ? GROUP : CHILD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == GROUP) {
            return new GroupViewHolder(getInflater().inflate(R.layout.item_rv_medical_symptom_group, parent, false));
        } else if (viewType == CHILD) {
            return new ChildViewHolder(getInflater().inflate(R.layout.item_rv_medical_symptom_child, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MedicalSymptomModel item = list.get(position);
        if (holder instanceof GroupViewHolder) {
            final GroupViewHolder h = (GroupViewHolder) holder;
            h.name.setText(item.getValue());
            h.name.setOnClickListener(showTextListener);
        } else if (holder instanceof ChildViewHolder) {
            final ChildViewHolder h = (ChildViewHolder) holder;
            h.name.setText(item.getValue());
            h.check.setImageResource(set.contains(item) ? R.drawable.ic_radio_button_checked_w : R.drawable.ic_radio_button_unchecked_w);
//            h.check.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
////                    notifyDataSetChanged();
//                }
//            });
            h.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (set.contains(item)) {
                        set.remove(item);
                    } else {
                        set.add(item);
                    }
                    notifyItemChanged(h.getAdapterPosition());
                }
            });
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (set.contains(item)) {
                        set.remove(item);
                    } else {
                        set.add(item);
                    }
                    notifyItemChanged(h.getAdapterPosition());
                }
            });
        }
    }

    private View.OnClickListener showTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        GroupViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tv_item_list_medical_symptom_group_name);
        }
    }

    private static class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView check;
        TextView name;

        ChildViewHolder(View v) {
            super(v);
            check = (ImageView) v.findViewById(R.id.iv_item_list_medical_symptom_child_check);
            name = (TextView) v.findViewById(R.id.tv_item_list_medical_symptom_child_name);
        }
    }

}
