package goldenbrother.gbmobile.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    private static UserModel user = new UserModel();
    private String UserID;
    private String UserPassword;
    private String UserNationCode;
    private String UserIDNumber;
    private String UserName;
    private String UserSex;
    private String UserBirthday;
    private String UserEmail;
    private String UserPhone;
    private String UserPicture;
    private String dormID;
    private String centerID;
    private int roleID;

    public static UserModel getInstance(){
        return user;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserNationCode() {
        return UserNationCode;
    }

    public void setUserNationCode(String userNationCode) {
        UserNationCode = userNationCode;
    }

    public String getUserIDNumber() {
        return UserIDNumber;
    }

    public void setUserIDNumber(String userIDNumber) {
        UserIDNumber = userIDNumber;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String userSex) {
        UserSex = userSex;
    }

    public String getUserBirthday() {
        return UserBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        UserBirthday = userBirthday;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserPicture() {
        return UserPicture;
    }

    public void setUserPicture(String userPicture) {
        UserPicture = userPicture;
    }

    public String getDormID() {
        return dormID;
    }

    public void setDormID(String dormID) {
        this.dormID = dormID;
    }

    public String getCenterID() {
        return centerID;
    }

    public void setCenterID(String centerID) {
        this.centerID = centerID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
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
            setDormID(j.getString("dormID"));
            setCenterID(j.getString("centerID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
