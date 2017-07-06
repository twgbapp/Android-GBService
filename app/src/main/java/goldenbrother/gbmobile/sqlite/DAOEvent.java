package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.EventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOEvent {
    // table name
    private static final String TABLENAME = "ServiceEvent";
    // pk
    private static final String COL_ServiceEventID = "ServiceEventID";
    // other column
    private static final String COL_UserPicture = "UserPicture";
    private static final String COL_EventScore = "EventScore";
    private static final String COL_EventDescription = "EventDescription";
    private static final String COL_UserName = "UserName";
    private static final String COL_UserID = "UserID";
    private static final String COL_RoleID = "RoleID";


    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_ServiceEventID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_UserPicture + " TEXT NOT NULL, ");
        sb.append(COL_EventScore + " INTEGER NOT NULL, ");
        sb.append(COL_EventDescription + " TEXT NOT NULL, ");
        sb.append(COL_UserName + " TEXT NOT NULL, ");
        sb.append(COL_UserID + " TEXT NOT NULL, ");
        sb.append(COL_RoleID + " INTEGER NOT NULL) ");

        return sb.toString();
    }

    private SQLiteDatabase db;
    private Context context;

    public DAOEvent(Context context) {
        this.context = context;
        this.db = SQLiteManager.getDatabase(context);
    }

    public boolean insertOrUpdate(EventModel e) {
        if (e == null) return false;
        // check exist
        if (get(e.getServiceEventID()) == null) { // insert
            return insert(e);
        } else { // update
            return update(e);
        }
    }

    private boolean insert(EventModel e) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceEventID, e.getServiceEventID());
        cv.put(COL_UserPicture, e.getUserPicture());
        cv.put(COL_EventScore, e.getEventScore());
        cv.put(COL_EventDescription, e.getEventDescription());
        cv.put(COL_UserName, e.getUserName());
        cv.put(COL_UserID, e.getUserID());
        cv.put(COL_RoleID, e.getRoleID());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    private boolean update(EventModel e) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ServiceEventID, e.getServiceEventID());
        cv.put(COL_UserPicture, e.getUserPicture());
        cv.put(COL_EventScore, e.getEventScore());
        cv.put(COL_EventDescription, e.getEventDescription());
        cv.put(COL_UserName, e.getUserName());
        cv.put(COL_UserID, e.getUserID());
        cv.put(COL_RoleID, e.getRoleID());

        String where = COL_ServiceEventID + "=" + e.getServiceEventID();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int ServiceEventID) {

        String where = COL_ServiceEventID + "=" + ServiceEventID;

        return db.delete(TABLENAME, where, null) > 0;
    }


    public List<EventModel> getAll() {
        List<EventModel> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLENAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public EventModel get(int ServiceEventID) {
        EventModel item = null;
        String where = COL_ServiceEventID + "=" + ServiceEventID;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    private EventModel getRecord(Cursor cursor) {
        EventModel e = new EventModel();
        e.setServiceEventID((int) cursor.getLong(0));
        e.setUserPicture(cursor.getString(1));
        e.setEventScore(cursor.getInt(2));
        e.setEventDescription(cursor.getString(3));
        e.setUserName(cursor.getString(4));
        e.setUserID(cursor.getString(5));
        e.setRoleID(cursor.getInt(6));
        return e;
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
