package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.ServiceChatModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/8.
 */
public class DAOServiceChat {
    // table name
    private static final String TABLENAME = "ServiceGroupChat";
    // pk
    private static final String COL_SGCNo = "SGCNo";
    // other column
    private static final String COL_ServiceGroupID = "ServiceGroupID";
    private static final String COL_WriterID = "WriterID";
    private static final String COL_WriterName = "WriterName";
    private static final String COL_WriterPicture = "WriterPicture";
    private static final String COL_Content = "Content";
    private static final String COL_ChatDate = "ChatDate";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLENAME + " ( ");
        sb.append(COL_SGCNo + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(COL_ServiceGroupID + " INTEGER NOT NULL, ");
        sb.append(COL_WriterID + " TEXT NOT NULL, ");
        sb.append(COL_WriterName + " TEXT NOT NULL, ");
        sb.append(COL_WriterPicture + " TEXT NOT NULL, ");
        sb.append(COL_Content + " TEXT NOT NULL, ");
        sb.append(COL_ChatDate + " TEXT NOT NULL) ");
        return sb.toString();
    }

    private SQLiteDatabase db;

    public DAOServiceChat(Context context) {
        db = SQLiteManager.getDatabase(context);
    }

    public boolean insert(ServiceChatModel gc) {
        if (getBySGCNo(gc.getSGCNo()) != null) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_SGCNo, gc.getSGCNo());
        cv.put(COL_ServiceGroupID, gc.getServiceGroupID());
        cv.put(COL_WriterID, gc.getWriterID());
        cv.put(COL_WriterName, gc.getWriterName());
        cv.put(COL_WriterPicture, gc.getWriterPicture());
        cv.put(COL_Content, gc.getContent());
        cv.put(COL_ChatDate, gc.getChatDate());
        return db.insert(TABLENAME, null, cv) > 0;
    }

    public boolean update(ServiceChatModel gc) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SGCNo, gc.getSGCNo());
        cv.put(COL_ServiceGroupID, gc.getServiceGroupID());
        cv.put(COL_WriterID, gc.getWriterID());
        cv.put(COL_WriterName, gc.getWriterName());
        cv.put(COL_WriterPicture, gc.getWriterPicture());
        cv.put(COL_Content, gc.getContent());
        cv.put(COL_ChatDate, gc.getChatDate());

        String where = COL_SGCNo + "=" + gc.getSGCNo();

        return db.update(TABLENAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {

        String where = COL_SGCNo + "=" + id;

        return db.delete(TABLENAME, where, null) > 0;
    }


    public List<ServiceChatModel> get(int ServiceGroupID) {
        List<ServiceChatModel> result = new ArrayList<>();
        String where = COL_ServiceGroupID + "=" + ServiceGroupID;
        Cursor cursor = db.query(TABLENAME, null, where, null, null, null, COL_ChatDate + " asc", null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public ServiceChatModel getBySGCNo(int SGCNo) {
        ServiceChatModel item = null;
        String where = COL_SGCNo + "=" + SGCNo;
        Cursor result = db.query(TABLENAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }


    private ServiceChatModel getRecord(Cursor cursor) {
        ServiceChatModel result = new ServiceChatModel();
        result.setSGCNo((int) cursor.getLong(0));
        result.setServiceGroupID(cursor.getInt(1));
        result.setWriterID(cursor.getString(2));
        result.setWriterName(cursor.getString(3));
        result.setWriterPicture(cursor.getString(4));
        result.setContent(cursor.getString(5));
        result.setChatDate(cursor.getString(6));
        return result;
    }

    public List<ServiceChatModel> getAllGroupBy() {
        List<ServiceChatModel> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
                + " GROUP BY " + COL_ServiceGroupID + " ORDER BY " + COL_ChatDate + " DESC", null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public int getCount(int ServiceGroupID) {
        int result = 0;
        String where = COL_ServiceGroupID + "=" + ServiceGroupID;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLENAME + " where " + where, null);
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
