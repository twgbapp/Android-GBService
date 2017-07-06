package goldenbrother.gbmobile.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017/1/21.
 */

public class ClubPostMediaModel implements Parcelable {
    public static final int IMAGE = 0;
    public static final int VIDEO = 1;
    private int ClubPostMediaID;
    private int ClubPostID;
    private int Type;
    private int Position;
    // image
    private Uri uri;
    private String pic;
    private String name;
    // video
    private String ThumbNailPath;
    private String UrlPath;

    public ClubPostMediaModel() {

    }

    protected ClubPostMediaModel(Parcel in) {
        ClubPostMediaID = in.readInt();
        ClubPostID = in.readInt();
        Type = in.readInt();
        Position = in.readInt();
        uri = in.readParcelable(Uri.class.getClassLoader());
        pic = in.readString();
        name = in.readString();
        ThumbNailPath = in.readString();
        UrlPath = in.readString();
    }

    public static final Creator<ClubPostMediaModel> CREATOR = new Creator<ClubPostMediaModel>() {
        @Override
        public ClubPostMediaModel createFromParcel(Parcel in) {
            return new ClubPostMediaModel(in);
        }

        @Override
        public ClubPostMediaModel[] newArray(int size) {
            return new ClubPostMediaModel[size];
        }
    };

    public int getClubPostMediaID() {
        return ClubPostMediaID;
    }

    public void setClubPostMediaID(int clubPostMediaID) {
        ClubPostMediaID = clubPostMediaID;
    }

    public int getClubPostID() {
        return ClubPostID;
    }

    public void setClubPostID(int clubPostID) {
        ClubPostID = clubPostID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbNailPath() {
        return ThumbNailPath;
    }

    public void setThumbNailPath(String thumbNailPath) {
        ThumbNailPath = thumbNailPath;
    }

    public String getUrlPath() {
        return UrlPath;
    }

    public void setUrlPath(String urlPath) {
        UrlPath = urlPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ClubPostMediaID);
        parcel.writeInt(ClubPostID);
        parcel.writeInt(Type);
        parcel.writeInt(Position);
        parcel.writeParcelable(uri, i);
        parcel.writeString(pic);
        parcel.writeString(name);
        parcel.writeString(ThumbNailPath);
        parcel.writeString(UrlPath);
    }
}
