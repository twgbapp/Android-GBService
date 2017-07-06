package goldenbrother.gbmobile.model;

import android.support.annotation.NonNull;

/**
 * Created by asus on 2016/12/8.
 */

public class ServiceChatModel{
    private int SGCNo;
    private int ServiceGroupID;
    private int chatCount;
    private String WriterID;
    private String WriterName;
    private String workerNo;
    private String WriterPicture;
    private String Content;
    private String ChatDate;

    public int getSGCNo() {
        return SGCNo;
    }

    public void setSGCNo(int SGCNo) {
        this.SGCNo = SGCNo;
    }

    public int getServiceGroupID() {
        return ServiceGroupID;
    }

    public void setServiceGroupID(int serviceGroupID) {
        ServiceGroupID = serviceGroupID;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public String getWriterID() {
        return WriterID;
    }

    public void setWriterID(String writerID) {
        WriterID = writerID;
    }

    public String getWriterName() {
        return WriterName;
    }

    public void setWriterName(String writerName) {
        WriterName = writerName;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getWriterPicture() {
        return WriterPicture;
    }

    public void setWriterPicture(String writerPicture) {
        WriterPicture = writerPicture;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getChatDate() {
        return ChatDate;
    }

    public void setChatDate(String chatDate) {
        ChatDate = chatDate;
    }

}
