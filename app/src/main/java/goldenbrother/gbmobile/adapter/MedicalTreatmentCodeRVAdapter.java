package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.MedicalTreatmentCodeModel;

public class MedicalTreatmentCodeRVAdapter extends SampleRVAdapter {

    // data
    private ArrayList<MedicalTreatmentCodeModel> list;
    private HashSet<MedicalTreatmentCodeModel> set;

    public MedicalTreatmentCodeRVAdapter(Context context, ArrayList<MedicalTreatmentCodeModel> list) {
        super(context);
        this.list = list;
        this.set = new HashSet<>();
    }

    public HashSet<MedicalTreatmentCodeModel> getSelected() {
        return set;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_rv_medical_treatment_code, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final MedicalTreatmentCodeModel item = list.get(position);
            ViewHolder h = (ViewHolder) holder;
            h.name.setText(item.getValue());
            h.check.setImageResource(set.contains(item) ? R.drawable.ic_radio_button_checked_b : R.drawable.ic_radio_button_unchecked_b);
            h.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (set.contains(item)) {
                        set.remove(item);
                    } else {
                        set.add(item);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView check;
        TextView name;

        ViewHolder(View v) {
            super(v);
            check = (ImageView) v.findViewById(R.id.iv_item_list_medical_treatment_code_body_check);
            name = (TextView) v.findViewById(R.id.tv_item_list_medical_treatment_code_body_name);
        }
    }

}
