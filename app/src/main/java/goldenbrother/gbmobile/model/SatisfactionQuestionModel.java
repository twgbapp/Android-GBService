package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017/3/14.
 */

public class SatisfactionQuestionModel implements Parcelable{
    private int siqNo;
    private int siNo;
    private String question;
    private String createDate;
    private String createID;

    public SatisfactionQuestionModel(){

    }
    public SatisfactionQuestionModel(String q){
        question = q;
    }

    protected SatisfactionQuestionModel(Parcel in) {
        siqNo = in.readInt();
        siNo = in.readInt();
        question = in.readString();
        createDate = in.readString();
        createID = in.readString();
    }

    public static final Creator<SatisfactionQuestionModel> CREATOR = new Creator<SatisfactionQuestionModel>() {
        @Override
        public SatisfactionQuestionModel createFromParcel(Parcel in) {
            return new SatisfactionQuestionModel(in);
        }

        @Override
        public SatisfactionQuestionModel[] newArray(int size) {
            return new SatisfactionQuestionModel[size];
        }
    };

    public int getSiqNo() {
        return siqNo;
    }

    public void setSiqNo(int siqNo) {
        this.siqNo = siqNo;
    }

    public int getSiNo() {
        return siNo;
    }

    public void setSiNo(int siNo) {
        this.siNo = siNo;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(siqNo);
        dest.writeInt(siNo);
        dest.writeString(question);
        dest.writeString(createDate);
        dest.writeString(createID);
    }
}
