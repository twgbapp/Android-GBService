package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;

/**
 * Created by asus on 2017/1/24.
 */

public abstract class SamplePagerAdapter extends PagerAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private final Resources resources;

    public SamplePagerAdapter(Context context) {
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
