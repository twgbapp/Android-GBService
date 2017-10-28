package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;

/**
 * Created by haojun on 2017/7/20.
 */

public class CommonItemListAdapter extends SampleBaseAdapter {

    private String[] items;

    public CommonItemListAdapter(Context context, String[] items) {
        super(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        ViewHolder h;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list_common_item, null);
            h = new ViewHolder(v);
            v.setTag(h);
        } else {
            h = (ViewHolder) v.getTag();
        }
        final String item = (String) getItem(position);
        h.item.setText(item);
        h.divider.setVisibility(position == getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
        return v;
    }

    private static class ViewHolder {

        TextView item;
        View divider;

        ViewHolder(View v) {
            item = v.findViewById(R.id.tv_item_list_common_item_name);
            divider = v.findViewById(R.id.v_item_list_common_item_divider);
        }
    }
}
