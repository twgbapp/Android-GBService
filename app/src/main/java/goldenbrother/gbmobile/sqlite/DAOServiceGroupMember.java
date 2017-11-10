package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.ServiceGroupMember;

import java.util.ArrayList;
import java.util.List;

public class DAOServiceGroupMember {

    private static final String TABLE_NAME = "ServiceGroupMembers";

    private static final String SERVICE_GROUP_ID = "ServiceGroupID";
    private static final String USER_ID = "UserID";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLE_NAME + " ( ");
        sb.append(SERVICE_GROUP_ID + " INTEGER NOT NULL , ");
        sb.append(USER_ID + " String NOT NULL ,");
        sb.append("PRIMARY KEY (" + SERVICE_GROUP_ID + "," + USER_ID + ")");
        sb.append(")");
        return sb.toString();
    }

    private SQLiteDatabase db;

    public DAOServiceGroupMember(Context context) {
        this.db = SQLiteManager.getDatabase(context);
    }

    public boolean insert(ServiceGroupMember item) {
        ContentValues cv = new ContentValues();
        cv.put(SERVICE_GROUP_ID, item.getServiceGroupID());
        cv.put(USER_ID, item.getUserID());
        return db.insert(TABLE_NAME, null, cv) > 0;
    }


    public boolean delete(ServiceGroupMember item) {
        String where = SERVICE_GROUP_ID + "=? and " + USER_ID + "=?";
        String[] args = {item.getServiceGroupID() + "", item.getUserID()};
        return db.delete(TABLE_NAME, where, args) > 0;
    }

    public boolean deleteAll() {
        String where = SERVICE_GROUP_ID + ">" + 0;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public ServiceGroupMember get(int serviceGroupID, String userId) {
        ServiceGroupMember item = null;
        String where = SERVICE_GROUP_ID + "=? and " + USER_ID + "=?";
        String[] args = {serviceGroupID + "", userId};
        Cursor result = db.query(TABLE_NAME, null, where, args, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    public List<ServiceGroupMember> getAll() {
        List<ServiceGroupMember> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public List<ServiceGroupMember> filterByServiceGroupId(int serviceGroupId) {
        List<ServiceGroupMember> result = new ArrayList<>();
        String where = SERVICE_GROUP_ID + "=" + serviceGroupId;
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public List<ServiceGroupMember> filterByUserId(String userId) {
        List<ServiceGroupMember> result = new ArrayList<>();
        String where = USER_ID + "=" + userId;
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    private ServiceGroupMember getRecord(Cursor cursor) {
        ServiceGroupMember item = new ServiceGroupMember();
        item.setServiceGroupID(cursor.getInt(0));
        item.setUserID(cursor.getString(1));
        return item;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }
}
