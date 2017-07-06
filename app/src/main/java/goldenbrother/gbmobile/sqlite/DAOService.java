package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.ServiceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOService {
    // table name
    private static final String TABLENAME = "ServiceGroup";
    // pk
    private static final String COL_ServiceGroupID = "ServiceGroupID";
    // other column
    private static final String COL_GroupName = "GroupName";
    private static final String COL_StaffID = "StaffID";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_ServiceGroupID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_GroupName + " TEXT NOT NULL, ");
        sb.append(COL_StaffID + " TEXT NOT NULL) ");
        return sb.toString();
    }

    private SQLiteDatabase db;
    private Context context;

    public DAOService(Context context) {
        this.context = context;
        this.db = SQLiteManager.getDatabase(context);
    }
    public boolean insertOrUpdate(ServiceModel g) {
        if (g == null) return false;
        // check exist
        if (get(g.getServiceGroupID()) == null) { // insert
            return insert(g);
        } else { // update
            return update(g);
        }
    }

    public boolean insert(ServiceModel g) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceGroupID, g.getServiceGroupID());
        cv.put(COL_GroupName, g.getServiceName());
        cv.put(COL_StaffID, g.getStaffID());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    public boolean update(ServiceModel g) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceGroupID, g.getServiceGroupID());
        cv.put(COL_GroupName, g.getServiceName());
        cv.put(COL_StaffID, g.getStaffID());

        String where = COL_ServiceGroupID + "=" + g.getServiceGroupID();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {

        String where = COL_ServiceGroupID + "=" + id;

        return db.delete(TABLENAME, where, null) > 0;
    }


    public List<ServiceModel> getAll() {
        List<ServiceModel> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLENAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public ServiceModel get(int ServiceGroupID) {
        ServiceModel item = null;
        String where = COL_ServiceGroupID + "=" + ServiceGroupID;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }

    public ServiceModel get(String StaffID) {
        ServiceModel item = null;
        String where = COL_StaffID + "='" + StaffID + "'";
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    private ServiceModel getRecord(Cursor cursor) {
        ServiceModel g = new ServiceModel();
        g.setServiceGroupID((int) cursor.getLong(0));
        g.setServiceName(cursor.getString(1));
        // get GroupChat
        DAOServiceChat daoGroupChat = new DAOServiceChat(context);
        g.getList_GroupChat().clear();
        g.getList_GroupChat().addAll(daoGroupChat.get(g.getServiceGroupID()));
        return g;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLENAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }
}
