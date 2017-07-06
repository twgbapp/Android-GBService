package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017/1/21.
 */

public class ClubPostMessageModel implements Parcelable {
    private int ClubPostMessageID;
    private int ClubPostID;
    private String userName;
    private String userPicture;
    private String Message;
    private String CreateDate;

    public ClubPostMessageModel() {

    }

    protected ClubPostMessageModel(Parcel in) {
        ClubPostMessageID = in.readInt();
        ClubPostID = in.readInt();
        userName = in.readString();
        userPicture = in.readString();
        Message = in.readString();
        CreateDate = in.readString();
    }

    public static final Creator<ClubPostMessageModel> CREATOR = new Creator<ClubPostMessageModel>() {
        @Override
        public ClubPostMessageModel createFromParcel(Parcel in) {
            return new ClubPostMessageModel(in);
        }

        @Override
        public ClubPostMessageModel[] newArray(int size) {
            return new ClubPostMessageModel[size];
        }
    };

    public int getClubPostMessageID() {
        return ClubPostMessageID;
    }

    public void setClubPostMessageID(int clubPostMessageID) {
        ClubPostMessageID = clubPostMessageID;
    }

    public int getClubPostID() {
        return ClubPostID;
    }

    public void setClubPostID(int clubPostID) {
        ClubPostID = clubPostID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ClubPostMessageID);
        parcel.writeInt(ClubPostID);
        parcel.writeString(userName);
        parcel.writeString(userPicture);
        parcel.writeString(Message);
        parcel.writeString(CreateDate);
    }
}
