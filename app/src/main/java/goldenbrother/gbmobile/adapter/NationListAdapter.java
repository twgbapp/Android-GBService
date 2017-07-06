package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;

/**
 * Created by asus on 2016/11/16.
 */

public class NationListAdapter extends SampleBaseAdapter {

    private String[] arr;

    public NationListAdapter(Context context, String[] arr) {
        super(context);
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.length;
    }

    @Override
    public Object getItem(int position) {
        return arr[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list_nation, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final String item = (String) getItem(position);
        tag.name.setText(item);
        return v;
    }

    private static class ViewHolder {

        TextView name;

        ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.item_list_nation_name);
        }
    }
}
