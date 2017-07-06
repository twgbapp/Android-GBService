package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by asus on 2016/5/17.
 */
public abstract class SampleBaseAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final Resources resources;

    public SampleBaseAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.resources = context.getResources();
    }


    public Resources getResources() {
        return resources;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
