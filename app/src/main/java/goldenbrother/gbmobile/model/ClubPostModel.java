package goldenbrother.gbmobile.model;

import java.util.ArrayList;

/**
 * Created by asus on 2017/1/21.
 */

public class ClubPostModel {
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
}
