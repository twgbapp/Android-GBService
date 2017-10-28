package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.PersonalPickUpModel;

/**
 * Created by asus on 2016/11/16.
 */

public class MedicalPersonListAdapter extends SampleBaseAdapter {

    private ArrayList<PersonalPickUpModel> list;

    public MedicalPersonListAdapter(Context context, ArrayList<PersonalPickUpModel> list) {
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
            v = getInflater().inflate(R.layout.item_list_medical_person, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final PersonalPickUpModel item = (PersonalPickUpModel) getItem(position);
        tag.name.setText(item.getName());
        return v;
    }

    private static class ViewHolder {

        TextView name;

        ViewHolder(View v) {
            name = v.findViewById(R.id.tv_item_list_medical_person_name);
        }
    }
}
