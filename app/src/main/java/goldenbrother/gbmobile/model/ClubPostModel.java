package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ClubPostModel implements Parcelable{
    private int ClubPostID;
    private int ClubID;
    private String postUserPicture;
    private String postUserName;
    private String Content;
    private String CreateDate;
    private String ModeifyDate;
    private ArrayList<ClubPostMediaModel> list_media;
    private int messageCount;
    private ArrayList<ClubPostMessageModel> list_message;

    public ClubPostModel() {
        list_media = new ArrayList<>();
        list_message = new ArrayList<>();
    }

    protected ClubPostModel(Parcel in) {
        ClubPostID = in.readInt();
        ClubID = in.readInt();
        postUserPicture = in.readString();
        postUserName = in.readString();
        Content = in.readString();
        CreateDate = in.readString();
        ModeifyDate = in.readString();
        list_media = in.createTypedArrayList(ClubPostMediaModel.CREATOR);
        messageCount = in.readInt();
        list_message = in.createTypedArrayList(ClubPostMessageModel.CREATOR);
    }

    public static final Creator<ClubPostModel> CREATOR = new Creator<ClubPostModel>() {
        @Override
        public ClubPostModel createFromParcel(Parcel in) {
            return new ClubPostModel(in);
        }

        @Override
        public ClubPostModel[] newArray(int size) {
            return new ClubPostModel[size];
        }
    };

    public int getClubPostID() {
        return ClubPostID;
    }

    public void setClubPostID(int clubPostID) {
        ClubPostID = clubPostID;
    }

    public int getClubID() {
        return ClubID;
    }

    public void setClubID(int clubID) {
        ClubID = clubID;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    public String getPostUserPicture() {
        return postUserPicture;
    }

    public void setPostUserPicture(String postUserPicture) {
        this.postUserPicture = postUserPicture;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getModeifyDate() {
        return ModeifyDate;
    }

    public void setModeifyDate(String modeifyDate) {
        ModeifyDate = modeifyDate;
    }

    public ArrayList<ClubPostMediaModel> getMedias() {
        return list_media;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public ArrayList<ClubPostMessageModel> getMessages() {
        return list_message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ClubPostID);
        parcel.writeInt(ClubID);
        parcel.writeString(postUserPicture);
        parcel.writeString(postUserName);
        parcel.writeString(Content);
        parcel.writeString(CreateDate);
        parcel.writeString(ModeifyDate);
        parcel.writeTypedList(list_media);
        parcel.writeInt(messageCount);
        parcel.writeTypedList(list_message);
    }
}
