package kale.debug.logcat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Kale
 * @date 2015/12/17
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;
    
    public DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mDb = getReadableDatabase();
    }

    /* 
     * 初次使用时创建数据库表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //删除数据库
    public boolean deleteDatabase(Context context, String databaseName) {
        return context.deleteDatabase(databaseName);
    }

    /**
     * 建立表
     * SQLite内部只支持NULL,INTEGER,REAL(浮点),TEXT(文本),BLOB(大二进制)5种数据类型
     * 建立表成功了，返回true
     */
    public boolean executeSql(String sql) {
        try {
            mDb.execSQL(sql);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public Cursor executeSql(String sql, String[] args) {
        try {
            return mDb.rawQuery(sql, args);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void closeDB() {
        if (mDb != null) {
            close();
        }
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
}
