package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.HospitalModel;

/**
 * Created by asus on 2016/11/16.
 */

public class MedicalHospitalListAdapter extends SampleBaseAdapter {

    private ArrayList<HospitalModel> list;

    public MedicalHospitalListAdapter(Context context, ArrayList<HospitalModel> list) {
        super(context);
        this.list = list;
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
            v = getInflater().inflate(R.layout.item_list_medical_hospital, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final HospitalModel item = (HospitalModel) getItem(position);
        tag.name.setText(item.getName());
        return v;
    }

    private static class ViewHolder {

        TextView name;

        ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.tv_item_list_medical_hospital_name);
        }
    }
}
