package goldenbrother.gbmobile.model;

/**
 * Created by asus on 2016/12/8.
 */

public class UserModel {
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
    private int roleID;

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

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }
}
