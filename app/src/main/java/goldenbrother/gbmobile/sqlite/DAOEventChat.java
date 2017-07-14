package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.EventChatModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOEventChat {
    // table name
    public static final String TABLENAME = "ServiceEventChat";
    // pk
    private static final String COL_SECNo = "SECNo";
    // other column
    private static final String COL_ServiceEventID = "ServiceEventID";
    private static final String COL_WriterID = "WriterID";
    private static final String COL_WriterName = "WriterName";
    private static final String COL_WriterPicture = "WriterPicture";
    private static final String COL_Content = "Content";
    private static final String COL_ChatDate = "ChatDate";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_SECNo + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_ServiceEventID + " INTEGER NOT NULL, ");
        sb.append(COL_WriterID + " TEXT NOT NULL, ");
        sb.append(COL_WriterName + " TEXT NOT NULL, ");
        sb.append(COL_WriterPicture + " TEXT NOT NULL, ");
        sb.append(COL_Content + " TEXT NOT NULL, ");
        sb.append(COL_ChatDate + " TEXT NOT NULL) ");
        return sb.toString();
    }

    private SQLiteDatabase db;

    public DAOEventChat(Context context) {
        db = SQLiteManager.getDatabase(context);
    }

    public boolean insert(EventChatModel ec) {
        if (getBySECNo(ec.getSECNo()) != null) {
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_SECNo, ec.getSECNo());
        cv.put(COL_ServiceEventID, ec.getServiceEventID());
        cv.put(COL_WriterID, ec.getWriterID());
        cv.put(COL_WriterName, ec.getWriterName());
        cv.put(COL_WriterPicture, ec.getWriterPicture());
        cv.put(COL_Content, ec.getContent());
        cv.put(COL_ChatDate, ec.getChatDate());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    public boolean update(EventChatModel gc) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SECNo, gc.getSECNo());
        cv.put(COL_ServiceEventID, gc.getServiceEventID());
        cv.put(COL_WriterID, gc.getWriterID());
        cv.put(COL_WriterName, gc.getWriterName());
        cv.put(COL_WriterPicture, gc.getWriterPicture());
        cv.put(COL_Content, gc.getContent());
        cv.put(COL_ChatDate, gc.getChatDate());

        String where = COL_SECNo + "=" + gc.getSECNo();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {

        String where = COL_SECNo + "=" + id;

        return db.delete(TABLENAME, where, null) > 0;
    }


    public List<EventChatModel> get(int ServiceGroupID) {
        List<EventChatModel> result = new ArrayList<>();
        String where = COL_ServiceEventID + "=" + ServiceGroupID;
        Cursor cursor = db.query(TABLENAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public EventChatModel getBySECNo(int SECNo) {
        EventChatModel item = null;
        String where = COL_SECNo + "=" + SECNo;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }

    private EventChatModel getRecord(Cursor cursor) {
        EventChatModel result = new EventChatModel();
        result.setSECNo((int) cursor.getLong(0));
        result.setServiceEventID(cursor.getInt(1));
        result.setWriterID(cursor.getString(2));
        result.setWriterName(cursor.getString(3));
        result.setWriterPicture(cursor.getString(4));
        result.setContent(cursor.getString(5));
        result.setChatDate(cursor.getString(6));
        return result;
    }

    public int getCount(int ServiceEventID){
        int result = 0;
        String where = COL_ServiceEventID + "="+ServiceEventID;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLENAME +" where "+where, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
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
