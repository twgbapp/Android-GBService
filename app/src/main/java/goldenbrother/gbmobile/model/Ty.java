package goldenbrother.gbmobile.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by haojun on 2017/8/10.
 */

public class Ty implements Parcelable{
    private File file;
    private Uri uri;

    protected Ty(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Ty> CREATOR = new Creator<Ty>() {
        @Override
        public Ty createFromParcel(Parcel in) {
            return new Ty(in);
        }

        @Override
        public Ty[] newArray(int size) {
            return new Ty[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
    }
}
