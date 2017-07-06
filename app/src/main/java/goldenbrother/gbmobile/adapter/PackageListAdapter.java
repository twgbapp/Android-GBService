package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.PackageModel;

import java.util.ArrayList;

/**
 * Created by asus on 2016/11/16.
 */

public class PackageListAdapter extends SampleBaseAdapter {

    private ArrayList<PackageModel> list;

    public PackageListAdapter(Context context, ArrayList<PackageModel> list) {
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
            v = getInflater().inflate(R.layout.item_list_package, null);
            tag = new ViewHolder(v);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final PackageModel item = (PackageModel) getItem(position);
        // set description
        tag.description.setText(item.getDescription());
        // set arriveDate
        tag.arriveDate.setText(item.getArriveDate());
        return v;
    }

    private static class ViewHolder {

        TextView description;
        TextView arriveDate;

        ViewHolder(View v) {
            description = (TextView) v.findViewById(R.id.tv_item_list_package_description);
            arriveDate = (TextView) v.findViewById(R.id.tv_item_list_package_arrivedate);
        }
    }
}
