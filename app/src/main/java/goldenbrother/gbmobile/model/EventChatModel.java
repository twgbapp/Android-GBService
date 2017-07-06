package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/9.
 */

public class EventChatModel {
    private int SECNo;
    private int ServiceEventID;
    private String WriterID;
    private String WriterName;
    private String WriterPicture;
    private String Content;
    private String ChatDate;

    public int getSECNo() {
        return SECNo;
    }

    public void setSECNo(int SECNo) {
        this.SECNo = SECNo;
    }

    public int getServiceEventID() {
        return ServiceEventID;
    }

    public void setServiceEventID(int serviceEventID) {
        ServiceEventID = serviceEventID;
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
