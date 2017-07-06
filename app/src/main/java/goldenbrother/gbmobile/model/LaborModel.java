package goldenbrother.gbmobile.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 2016/12/8.
 */

public class LaborModel extends UserModel {
    private static LaborModel laborModel;
    private int ServiceGroupID;
    private String CustomerNo;
    private String FlaborNo;
    private String WorkerNo;

    private LaborModel() {

    }

    public static LaborModel getInstance() {
        if (laborModel == null)
            laborModel = new LaborModel();
        return laborModel;
    }

    public int getServiceGroupID() {
        return ServiceGroupID;
    }

    public void setServiceGroupID(int serviceGroupID) {
        ServiceGroupID = serviceGroupID;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getFlaborNo() {
        return FlaborNo;
    }

    public void setFlaborNo(String flaborNo) {
        FlaborNo = flaborNo;
    }

    public String getWorkerNo() {
        return WorkerNo;
    }

    public void setWorkerNo(String workerNo) {
        WorkerNo = workerNo;
    }

    public JSONObject getJSONObject() {
        try {
            JSONObject j = new JSONObject();
            j.put("userID", getUserID());
            j.put("roleID", getRoleID());
            j.put("serviceGroupID", getServiceGroupID());
            j.put("userPicture", getUserPicture());
            j.put("userSex", getUserSex());
            j.put("userIDNumber", getUserIDNumber());
            j.put("userPhone", getUserPhone());
            j.put("userEmail", getUserEmail());
            j.put("userNationCode", getUserNationCode());
            j.put("userName", getUserName());
            j.put("userBirthday", getUserBirthday());
            j.put("flaborNo", getFlaborNo());
            j.put("customerNo", getCustomerNo());
            j.put("workerNo", getWorkerNo());
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setJSONObject(JSONObject j) {
        try {
            setUserID(j.getString("userID"));
            setRoleID(j.getInt("roleID"));
            setServiceGroupID(j.getInt("serviceGroupID"));
            setUserPicture(j.getString("userPicture"));
            setUserSex(j.getString("userSex"));
            setUserIDNumber(j.getString("userIDNumber"));
            setUserPhone(j.getString("userPhone"));
            setUserEmail(j.getString("userEmail"));
            setUserNationCode(j.getString("userNationCode"));
            setUserName(j.getString("userName"));
            setUserBirthday(j.getString("userBirthday"));
            setFlaborNo(j.getString("flaborNo"));
            setCustomerNo(j.getString("customerNo"));
            setWorkerNo(j.getString("workerNo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
