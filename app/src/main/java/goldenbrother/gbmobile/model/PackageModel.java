package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2017/2/13.
 */

public class PackageModel {
    private int packageID;
    private String description;
    private String pickNumber;
    private String arriveDate;
    private String baseStr;

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(String pickNumber) {
        this.pickNumber = pickNumber;
    }

    public String getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getBaseStr() {
        return baseStr;
    }

    public void setBaseStr(String baseStr) {
        this.baseStr = baseStr;
    }
}
