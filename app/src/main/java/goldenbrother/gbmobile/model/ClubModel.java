package goldenbrother.gbmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017/1/21.
 */

public class ClubModel implements Parcelable {
    private int ClubID;
    private String ClubName;
    private String ClubPicture;
    public ClubModel() {

    }

    public ClubModel(int clubID, String clubName, String clubPicture) {
        ClubID = clubID;
        ClubName = clubName;
        ClubPicture = clubPicture;
    }

    private ClubModel(Parcel in) {
        ClubID = in.readInt();
        ClubName = in.readString();
        ClubPicture = in.readString();
    }

    public static final Creator<ClubModel> CREATOR = new Creator<ClubModel>() {
        @Override
        public ClubModel createFromParcel(Parcel in) {
            return new ClubModel(in);
        }

        @Override
        public ClubModel[] newArray(int size) {
            return new ClubModel[size];
        }
    };

    public int getClubID() {
        return ClubID;
    }

    public void setClubID(int clubID) {
        ClubID = clubID;
    }

    public String getClubName() {
        return ClubName;
    }

    public void setClubName(String clubName) {
        ClubName = clubName;
    }

    public String getClubPicture() {
        return ClubPicture;
    }

    public void setClubPicture(String clubPicture) {
        ClubPicture = clubPicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ClubID);
        dest.writeString(ClubName);
        dest.writeString(ClubPicture);
    }
}
