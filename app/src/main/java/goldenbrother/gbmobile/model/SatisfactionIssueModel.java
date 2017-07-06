package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by asus on 2017/3/14.
 */

public class SatisfactionIssueModel implements Parcelable{
    private int siNo;
    private String name;
    private int enable;
    private String createDate;
    private String createID;
    private ArrayList<SatisfactionQuestionModel> list_siq;

    public SatisfactionIssueModel() {
        list_siq = new ArrayList<>();
    }

    protected SatisfactionIssueModel(Parcel in) {
        siNo = in.readInt();
        name = in.readString();
        enable = in.readInt();
        createDate = in.readString();
        createID = in.readString();
        list_siq = in.createTypedArrayList(SatisfactionQuestionModel.CREATOR);
    }

    public static final Creator<SatisfactionIssueModel> CREATOR = new Creator<SatisfactionIssueModel>() {
        @Override
        public SatisfactionIssueModel createFromParcel(Parcel in) {
            return new SatisfactionIssueModel(in);
        }

        @Override
        public SatisfactionIssueModel[] newArray(int size) {
            return new SatisfactionIssueModel[size];
        }
    };

    public int getSiNo() {
        return siNo;
    }

    public void setSiNo(int siNo) {
        this.siNo = siNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
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

    public ArrayList<SatisfactionQuestionModel> getQuestions() {
        return list_siq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(siNo);
        dest.writeString(name);
        dest.writeInt(enable);
        dest.writeString(createDate);
        dest.writeString(createID);
        dest.writeTypedList(list_siq);
    }
}
