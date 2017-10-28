package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.BloodType;

/**
 * Created by asus on 2016/11/16.
 */

public class MedicalBloodTypeListAdapter extends SampleBaseAdapter {

    private ArrayList<BloodType> list;

    public MedicalBloodTypeListAdapter(Context context) {
        super(context);
        list = new ArrayList<>();
        String[] codes = getResources().getStringArray(R.array.blood_type_code);
        String[] names = getResources().getStringArray(R.array.blood_type_name);
        for (int i = 0; i < codes.length; i++) {
            list.add(new BloodType(codes[i], names[i]));
        }
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
            v = getInflater().inflate(R.layout.item_list_medical_blood_type, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final BloodType item = (BloodType) getItem(position);
        tag.name.setText(item.getName());
        return v;
    }

    private static class ViewHolder {
        TextView name;

        ViewHolder(View v) {
            name = v.findViewById(R.id.tv_item_list_medical_blood_type_name);
        }
    }
}
