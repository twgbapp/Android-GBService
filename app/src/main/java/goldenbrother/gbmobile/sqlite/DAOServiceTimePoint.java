package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.ServiceTimePointModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOServiceTimePoint {
    // table name
    private static final String TABLENAME = "ServiceGroupTimePoint";
    // pk
    private static final String COL_ServiceGroupID = "ServiceGroupID";
    // other column
    private static final String COL_TimePoint = "TimePoint";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_ServiceGroupID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_TimePoint + " TEXT NOT NULL) ");
        return sb.toString();
    }

    private SQLiteDatabase db;
    private Context context;

    public DAOServiceTimePoint(Context context) {
        this.context = context;
        this.db = SQLiteManager.getDatabase(context);
    }


    public boolean insertOrUpdate(ServiceTimePointModel gtp) {
        if (gtp == null) return false;
        // check exist
        if (get(gtp.getServiceGroupID()) == null) { // insert
            return insert(gtp);
        } else { // update
            return update(gtp);
        }
    }

    private boolean insert(ServiceTimePointModel gtp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceGroupID, gtp.getServiceGroupID());
        cv.put(COL_TimePoint, gtp.getTimePoint());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    private boolean update(ServiceTimePointModel gtp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceGroupID, gtp.getServiceGroupID());
        cv.put(COL_TimePoint, gtp.getTimePoint());
        String where = COL_ServiceGroupID + "=" + gtp.getServiceGroupID();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {

        String where = COL_ServiceGroupID + "=" + id;

        return db.delete(TABLENAME, where, null) > 0;
    }


    public List<ServiceTimePointModel> getAll() {
        List<ServiceTimePointModel> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLENAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public ServiceTimePointModel get(int ServiceGroupID) {
        ServiceTimePointModel item = null;
        String where = COL_ServiceGroupID + "=" + ServiceGroupID;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    private ServiceTimePointModel getRecord(Cursor cursor) {
        ServiceTimePointModel gtp = new ServiceTimePointModel();
        gtp.setServiceGroupID((int) cursor.getLong(0));
        gtp.setTimePoint(cursor.getString(1));
        return gtp;
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
