package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.EventTimePointModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOEventTimePoint {
    // table name
    public static final String TABLENAME = "ServiceEventTimePoint";
    // pk
    private static final String COL_ServiceEventID = "ServiceEventID";
    // other column
    private static final String COL_TimePoint = "TimePoint";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_ServiceEventID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_TimePoint + " TEXT NOT NULL) ");
        return sb.toString();
    }

    private SQLiteDatabase db;
    private Context context;

    public DAOEventTimePoint(Context context) {
        this.context = context;
        this.db = SQLiteManager.getDatabase(context);
    }


    public boolean insertOrUpdate(EventTimePointModel etp) {
        if (etp == null) return false;
        // check exist
        if (get(etp.getServiceEventID()) == null) { // insert
            return insert(etp);
        } else { // update
            return update(etp);
        }
    }

    private boolean insert(EventTimePointModel etp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceEventID, etp.getServiceEventID());
        cv.put(COL_TimePoint, etp.getTimePoint());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    private boolean update(EventTimePointModel etp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceEventID, etp.getServiceEventID());
        cv.put(COL_TimePoint, etp.getTimePoint());
        String where = COL_ServiceEventID + "=" + etp.getServiceEventID();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {
        String where = COL_ServiceEventID + "=" + id;
        return db.delete(TABLENAME, where, null) > 0;
    }
    public boolean deleteAll() {
        String where = COL_ServiceEventID + ">" + 0;
        return db.delete(TABLENAME, where, null) > 0;
    }

    public List<EventTimePointModel> getAll() {
        List<EventTimePointModel> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLENAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public EventTimePointModel get(int ServiceGroupID) {
        EventTimePointModel item = null;
        String where = COL_ServiceEventID + "=" + ServiceGroupID;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    private EventTimePointModel getRecord(Cursor cursor) {
        EventTimePointModel etp = new EventTimePointModel();
        etp.setServiceEventID((int) cursor.getLong(0));
        etp.setTimePoint(cursor.getString(1));
        return etp;
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
