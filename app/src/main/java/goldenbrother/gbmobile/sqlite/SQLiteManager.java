package goldenbrother.gbmobile.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gb.db";

    private static final int VERSION = 6;

    private static SQLiteDatabase database;

    private SQLiteManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
        db.execSQL(DAOServiceGroupMember.createTable());
        db.execSQL(DAOServiceChat.createTable());
        db.execSQL(DAOServiceTimePoint.createTable());
        db.execSQL(DAOEvent.createTable());
        db.execSQL(DAOEventChat.createTable());
        db.execSQL(DAOEventTimePoint.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DAOServiceGroupMember.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAOServiceChat.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAOServiceTimePoint.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAOEvent.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAOEventChat.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAOEventTimePoint.TABLENAME);
        onCreate(db);
    }
}
