package goldenbrother.gbmobile.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 2016/5/8.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "golfforgaming.db";

    public static final int VERSION = 1;

    private static SQLiteDatabase database;

    public SQLiteManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new SQLiteManager(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DAOService.createTable());
        db.execSQL(DAOServiceChat.createTable());
        db.execSQL(DAOServiceTimePoint.createTable());
        db.execSQL(DAOEvent.createTable());
        db.execSQL(DAOEventChat.createTable());
        db.execSQL(DAOEventTimePoint.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DAOService.createTable());
        db.execSQL("DROP TABLE IF EXISTS " + DAOServiceChat.createTable());
        db.execSQL("DROP TABLE IF EXISTS " + DAOServiceTimePoint.createTable());
        db.execSQL("DROP TABLE IF EXISTS " + DAOEvent.createTable());
        db.execSQL("DROP TABLE IF EXISTS " + DAOEventChat.createTable());
        db.execSQL("DROP TABLE IF EXISTS " + DAOEventTimePoint.createTable());
        onCreate(db);
    }
}
