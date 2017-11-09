package goldenbrother.gbmobile.model;

import org.json.JSONObject;

/**
 * Created by asus on 2016/12/8.
 */

public class RoleInfo {

    private static RoleInfo info;

    private static final int USER = -1;
    private static final int LABOR = 0;
    private static final int MANAGER = 1;

    private int roleID;

    private RoleInfo() {

    }

    public static RoleInfo getInstance() {
        if (info == null)
            info = new RoleInfo();
        return info;
    }

    public int getRoleID() {
        return roleID;
    }

    public boolean isLabor() {
        return roleID == LABOR;
    }


    public void setRoleID(int roleID) {
        this.roleID = roleID;
        switch (roleID) {
            case LABOR:
                LaborModel.getInstance().setRoleID(roleID);
                break;
            case MANAGER:
                ManagerModel.getInstance().setRoleID(roleID);
                break;
        }
    }

    public String getUserID() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserID();
            case LABOR:
                return LaborModel.getInstance().getUserID();
            case MANAGER:
                return ManagerModel.getInstance().getUserID();
        }
        return null;
    }

    public String getUserPicture() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserPicture();
            case LABOR:
                return LaborModel.getInstance().getUserPicture();
            case MANAGER:
                return ManagerModel.getInstance().getUserPicture();
        }
        return null;
    }

    public void setUserPicture(String path) {
        switch (roleID) {
            case USER:
                UserModel.getInstance().setUserPicture(path);
                break;
            case LABOR:
                LaborModel.getInstance().setUserPicture(path);
                break;
            case MANAGER:
                ManagerModel.getInstance().setUserPicture(path);
                break;
        }
    }

    public String getUserName() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserName();
            case LABOR:
                return LaborModel.getInstance().getUserName();
            case MANAGER:
                return ManagerModel.getInstance().getUserName();
        }
        return null;
    }

    public String getUserSex() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserSex();
            case LABOR:
                return LaborModel.getInstance().getUserSex();
            case MANAGER:
                return ManagerModel.getInstance().getUserSex();
        }
        return null;
    }


    public String getUserBirthday() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserBirthday();
            case LABOR:
                return LaborModel.getInstance().getUserBirthday();
            case MANAGER:
                return ManagerModel.getInstance().getUserBirthday();
        }
        return null;
    }

    public String getUserEmail() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserEmail();
            case LABOR:
                return LaborModel.getInstance().getUserEmail();
            case MANAGER:
                return ManagerModel.getInstance().getUserEmail();
        }
        return null;
    }

    public String getUserNationCode() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getUserNationCode();
            case LABOR:
                return LaborModel.getInstance().getUserNationCode();
            case MANAGER:
                return ManagerModel.getInstance().getUserNationCode();
        }
        return null;
    }

    public String getDormID() {
        switch (roleID) {
            case LABOR:
                return LaborModel.getInstance().getDormID();
            case MANAGER:
                return ManagerModel.getInstance().getDormID();
        }
        return null;
    }

    public String getCenterID() {
        switch (roleID) {
            case LABOR:
                return LaborModel.getInstance().getCenterID();
            case MANAGER:
                return ManagerModel.getInstance().getCenterID();
        }
        return null;
    }

    public void setJSONObject(JSONObject j) {
        switch (roleID) {
            case USER:
                UserModel.getInstance().setJSONObject(j);
                break;
            case LABOR: // set Labor Object
                LaborModel.getInstance().setJSONObject(j);
                break;
            case MANAGER: // set Manager Object
                ManagerModel.getInstance().setJSONObject(j);
                break;
        }
    }

    public JSONObject getJSONObject() {
        switch (roleID) {
            case USER:
                return UserModel.getInstance().getJSONObject();
            case LABOR:
                return LaborModel.getInstance().getJSONObject();
            case MANAGER:
                return ManagerModel.getInstance().getJSONObject();
        }
        return null;
    }
}
