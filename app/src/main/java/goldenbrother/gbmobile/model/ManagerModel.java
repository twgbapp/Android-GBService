package goldenbrother.gbmobile.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 2016/12/8.
 */

public class ManagerModel extends UserModel {
    private static ManagerModel managerModel;
    private String title;
    private String AreaNum;
    private String DeprtyID;
    private int OnCallStatus;
    private int ChatCount;
    private int EventCount;

    private ManagerModel() {

    }

    public static ManagerModel getInstance() {
        if (managerModel == null)
            managerModel = new ManagerModel();
        return managerModel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAreaNum() {
        return AreaNum;
    }

    public void setAreaNum(String areaNum) {
        AreaNum = areaNum;
    }

    public String getDeprtyID() {
        return DeprtyID;
    }

    public void setDeprtyID(String deprtyID) {
        DeprtyID = deprtyID;
    }

    public int getOnCallStatus() {
        return OnCallStatus;
    }

    public void setOnCallStatus(int onCallStatus) {
        OnCallStatus = onCallStatus;
    }

    public int getChatCount() {
        return ChatCount;
    }

    public void setChatCount(int chatCount) {
        ChatCount = chatCount;
    }

    public int getEventCount() {
        return EventCount;
    }

    public void setEventCount(int eventCount) {
        EventCount = eventCount;
    }


    public JSONObject getJSONObject() {

        try {
            JSONObject j = new JSONObject();
            j.put("userID", getUserID());
            j.put("roleID", getRoleID());
            j.put("userPicture", getUserPicture());
            j.put("userSex", getUserSex());
            j.put("userIDNumber", getUserIDNumber());
            j.put("userPhone", getUserPhone());
            j.put("userEmail", getUserEmail());
            j.put("userNationCode", getUserNationCode());
            j.put("userName", getUserName());
            j.put("userBirthday", getUserBirthday());
            j.put("title", getTitle());
            j.put("dormID", getDormID());
            j.put("centerID", getCenterID());
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
            setUserPicture(j.getString("userPicture"));
            setUserSex(j.getString("userSex"));
            setUserIDNumber(j.getString("userIDNumber"));
            setUserPhone(j.getString("userPhone"));
            setUserEmail(j.getString("userEmail"));
            setUserNationCode(j.getString("userNationCode"));
            setUserName(j.getString("userName"));
            setUserBirthday(j.getString("userBirthday"));
            setTitle(j.getString("title"));
            setDormID(j.getString("dormID"));
            setCenterID(j.getString("centerID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
