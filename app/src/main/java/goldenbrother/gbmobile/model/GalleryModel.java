package goldenbrother.gbmobile.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

/**
 * Created by asus on 2017/1/13.
 */

public class GalleryModel {
    private Bitmap bitmap;
    private Uri uri;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
