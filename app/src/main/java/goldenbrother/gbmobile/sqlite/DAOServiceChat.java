package goldenbrother.gbmobile.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import goldenbrother.gbmobile.model.ServiceChatModel;

import java.util.ArrayList;
import java.util.List;

public class DAOServiceChat {

    static final String TABLE_NAME = "ServiceGroupChat";
    // PK
    private static final String SGC_NO = "SGCNo";
    // COLUMN
    private static final String SERVICE_GROUP_ID = "ServiceGroupID";
    private static final String WRITER_ID = "WriterID";
    private static final String WRITER_NAME = "WriterName";
    private static final String WRITER_PICTURE = "WriterPicture";
    private static final String CONTENT = "Content";
    private static final String CHAT_DATE = "ChatDate";
    private static final String WORKER_NO = "WorkerNo";
    private static final String CUSTOMER_NO = "CustomerNo";

    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Create Table " + TABLE_NAME + " ( ");
        sb.append(SGC_NO + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
        sb.append(SERVICE_GROUP_ID + " INTEGER NOT NULL, ");
        sb.append(WRITER_ID + " TEXT NOT NULL, ");
        sb.append(WRITER_NAME + " TEXT NOT NULL, ");
        sb.append(WRITER_PICTURE + " TEXT NOT NULL, ");
        sb.append(CONTENT + " TEXT NOT NULL, ");
        sb.append(CHAT_DATE + " TEXT NOT NULL, ");
        sb.append(WORKER_NO + " TEXT NOT NULL, ");
        sb.append(CUSTOMER_NO + " TEXT NOT NULL) ");
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
        cv.put(SGC_NO, gc.getSGCNo());
        cv.put(SERVICE_GROUP_ID, gc.getServiceGroupID());
        cv.put(WRITER_ID, gc.getUserID());
        cv.put(WRITER_NAME, gc.getUserName());
        cv.put(WRITER_PICTURE, gc.getUserPicture());
        cv.put(CONTENT, gc.getContent());
        cv.put(CHAT_DATE, gc.getChatDate());
        cv.put(WORKER_NO, gc.getWorkerNo());
        cv.put(CUSTOMER_NO, gc.getCustomerNo());
        return db.insert(TABLE_NAME, null, cv) > 0;
    }

    public boolean update(ServiceChatModel gc) {
        ContentValues cv = new ContentValues();
        cv.put(SGC_NO, gc.getSGCNo());
        cv.put(SERVICE_GROUP_ID, gc.getServiceGroupID());
        cv.put(WRITER_ID, gc.getUserID());
        cv.put(WRITER_NAME, gc.getUserName());
        cv.put(WRITER_PICTURE, gc.getUserPicture());
        cv.put(CONTENT, gc.getContent());
        cv.put(CHAT_DATE, gc.getChatDate());
        cv.put(WORKER_NO, gc.getWorkerNo());
        cv.put(CUSTOMER_NO, gc.getCustomerNo());

        String where = SGC_NO + "=" + gc.getSGCNo();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }


    public boolean delete(int id) {
        String where = SGC_NO + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public boolean deleteAll() {
        String where = SGC_NO + ">" + 0;
        return db.delete(TABLE_NAME, where, null) > 0;
    }


    public List<ServiceChatModel> get(int ServiceGroupID) {
        List<ServiceChatModel> result = new ArrayList<>();
        String where = SERVICE_GROUP_ID + "=" + ServiceGroupID;
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, CHAT_DATE + " asc", null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public ServiceChatModel getBySGCNo(int SGCNo) {
        ServiceChatModel item = null;
        String where = SGC_NO + "=" + SGCNo;
        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);
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
        result.setUserID(cursor.getString(2));
        result.setUserName(cursor.getString(3));
        result.setUserPicture(cursor.getString(4));
        result.setContent(cursor.getString(5));
        result.setChatDate(cursor.getString(6));
        result.setWorkerNo(cursor.getString(7));
        result.setCustomerNo(cursor.getString(8));
        return result;
    }

    public List<ServiceChatModel> getLastChatList(List<Integer> serviceGroupIds) {
        List<ServiceChatModel> result = new ArrayList<>();
        if (!serviceGroupIds.isEmpty()) {
            StringBuilder args = new StringBuilder();
            for (Integer serviceGroupId : serviceGroupIds) {
                args.append((args.length() == 0) ? "" : ",");
                args.append(serviceGroupId);
            }
            String sql = "SELECT * FROM " + TABLE_NAME
                    + " WHERE " + SERVICE_GROUP_ID + " IN(" + args + ")"
                    + " GROUP BY " + SERVICE_GROUP_ID + " ORDER BY " + CHAT_DATE + " DESC";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                result.add(getRecord(cursor));
            }
            cursor.close();
        }

        return result;
    }

    public int getCount(int ServiceGroupID) {
        int result = 0;
        String where = SERVICE_GROUP_ID + "=" + ServiceGroupID;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " where " + where, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
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
