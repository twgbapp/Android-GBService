package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class ClubPostMediaModel implements Parcelable{
    public static final int IMAGE = 0;
    public static final int VIDEO = 1;
    private int clubPostMediaID;
    private int clubPostID;
    private int type;
    private int position;
    // media
    private File file; // image file
    private String pic; // image base64
    private String name; // image file name
    private String thumbNailPath; //
    private String urlPath; //

    public ClubPostMediaModel(){

    }

    protected ClubPostMediaModel(Parcel in) {
        clubPostMediaID = in.readInt();
        clubPostID = in.readInt();
        type = in.readInt();
        position = in.readInt();
        pic = in.readString();
        name = in.readString();
        thumbNailPath = in.readString();
        urlPath = in.readString();
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
        return clubPostMediaID;
    }

    public void setClubPostMediaID(int clubPostMediaID) {
        this.clubPostMediaID = clubPostMediaID;
    }

    public int getClubPostID() {
        return clubPostID;
    }

    public void setClubPostID(int clubPostID) {
        this.clubPostID = clubPostID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
        return thumbNailPath;
    }

    public void setThumbNailPath(String thumbNailPath) {
        this.thumbNailPath = thumbNailPath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(clubPostMediaID);
        dest.writeInt(clubPostID);
        dest.writeInt(type);
        dest.writeInt(position);
        dest.writeString(pic);
        dest.writeString(name);
        dest.writeString(thumbNailPath);
        dest.writeString(urlPath);
    }
}
