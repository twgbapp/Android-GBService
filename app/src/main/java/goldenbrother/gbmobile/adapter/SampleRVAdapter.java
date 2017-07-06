package goldenbrother.gbmobile.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

/**
 * Created by asus on 2017/1/21.
 */

public abstract class SampleRVAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final Resources resources;
    private final LayoutInflater inflater;

    protected SampleRVAdapter(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return resources;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
