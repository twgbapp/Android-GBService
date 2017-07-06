package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.RepairKindModel;

import java.util.ArrayList;

/**
 * Created by asus on 2016/11/16.
 */

public class RepairKindListAdapter extends SampleBaseAdapter {

    private ArrayList<RepairKindModel> list;

    public RepairKindListAdapter(Context context, ArrayList<RepairKindModel> list) {
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
            v = getInflater().inflate(R.layout.item_list_event_kind, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final RepairKindModel item = (RepairKindModel) getItem(position);
        // set text
        tag.value.setText(item.getContent());
        return v;
    }

    private static class ViewHolder {

        TextView value;

        ViewHolder(View v) {
            value = (TextView) v.findViewById(R.id.item_list_event_kind_value);
        }
    }
}
