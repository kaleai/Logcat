package kale.debug.logcat.db;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.Map;

import kale.debug.logcat.LogCat;
import kale.debug.logcat.P;
import kale.debug.logcat.model.LogcatModel;

/**
 * @author Kale
 * @date 2015/12/17
 */
public class LogcatDB {

    private static final String TAG = "LogcatDBHelper";

    public static final String TABLE_NAME = "android_log";

    private String level;

    private String message;

    private String tag;

    private final Gson mGson;

    private LogcatModel mModel;

    private final DatabaseHelper mHelper;

    private final Map<String, String> levMap;


    public LogcatDB(Context context) {
        mGson = new Gson();
        mModel = new LogcatModel();
        mHelper = new DatabaseHelper(context, "Logcat", 1);
        levMap = new ArrayMap<>();
        levMap.put("V", "verbose");
        levMap.put("D", "debug");
        levMap.put("I", "info");
        levMap.put("W", "warning");
        levMap.put("E", "error");
    }

    public void saveToDB(Activity activity) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading...");
        dialog.show();
        Process process = new LogCat().options("-d")
                .filter(null, "V")
                .commit();
//        mHelper.getDb().beginTransaction(); //手动设置开始事务
        LogCat.loadLog(process, new LogCat.LoadHandler() {
            @Nullable
            @Override
            public String handLine(String line) {
                return insertOneLine(line);
            }

            @Override
            public void onComplete(String str) {
                //mHelper.getDb().setTransactionSuccessful();
                //mHelper.getDb().endTransaction();
                dialog.dismiss();
            }
        });
    }

    /**
     * D/ddd     ( 7506): onCreate: debug
     *
     * @param line 每一行的log
     */
    private String insertOneLine(String line) {
        if (!line.substring(1, 2).equals("/")) {
            // 不符合规范的日志格式
            return null;
        }

        level = levMap.get(line.substring(0, 1));
        
        tag = line.substring(2, line.indexOf("("));
        message = line.substring(line.indexOf("):") + 2);

        if (message.contains(P.PREFIX)) {
            // custom log
            message = message.substring(P.PREFIX.length() + 1);
            System.out.println(message);
            mModel = mGson.fromJson(message, LogcatModel.class);
            message = mModel.msg;
        } else {
            mModel.category = P.Category.OTHER;
        }

        insertData(mModel.category,
                mModel.clsName,
                mModel.function,
                level,
                mModel.line,
                mModel.time,
                message,
                tag,
                mGson.toJson(mModel.extra));
        mModel.reSetData(); // 置空，下次复用
        return null;
    }

    public static final String CATEGORY = "category";

    public static final String CLASS_NAME = "class_name";

    public static final String FUNCTION = "function";

    public static final String LEVEL = "level";

    public static final String LINE = "line";

    public static final String LOG_TIME = "log_time";

    public static final String MESSAGE = "message";

    public static final String LOG_TAG = "tag";

    public static final String EXTRA = "extra";

    /**
     * 给数据库建立一个表
     */
    public void creatNewTable() {
        String createNewTable = "create table " + TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                CATEGORY + " TEXT," +
                CLASS_NAME + " TEXT," +
                FUNCTION + " TEXT," +
                LEVEL + " TEXT," +
                LINE + " INTEGER," +
                LOG_TIME + " INTEGER," +
                MESSAGE + " TEXT," +
                LOG_TAG + " TEXT," +
                EXTRA + " TEXT);";
        if (mHelper.executeSql(createNewTable)) {
            Log.i(TAG, "建表成功");
        } else {
            Log.e(TAG, "建表失败");
        }
    }

    /**
     * 新增数据
     */
    public void insertData(String category, String class_name, String function, String level, int line, long log_time,
            String message, String tag, String extra) {
        String sb = ("'" + category + "',") +
                "'" + class_name + "'," +
                "'" + function + "'," +
                "'" + level + "'," +
                line + "," +
                log_time + "," +
                "'" + message + "'," +
                "'" + tag + "'," +
                "'" + extra + "'";
        String sql = "insert into " + TABLE_NAME + " (category,class_name,function,level,line,log_time,message,tag,extra) "
                + "values(" + sb + ")";
        if (!mHelper.executeSql(sql)) {
            Log.e(tag, "插入失败");
        }
    }

    /**
     * 通过表名来删除一个表
     */
    public void deleteOldTable() {
        String sql = "drop table " + TABLE_NAME;
        if (mHelper.executeSql(sql)) {
            Log.i(TAG, "删除成功");
        } else {
            Log.e(TAG, "删除失败");
        }
    }

    public Cursor queryByColumn(String[] column) {
        StringBuilder sb = new StringBuilder();
        for (String c : column) {
            sb.append(",").append(c);
        }
        String sql = "select _id" + sb.toString() + " from " + TABLE_NAME;
        //得到一个Cursor，这个将要放入适配器中
        return mHelper.executeSql(sql, null);
    }

    public Cursor queryByColumn(String[] column, String where) {
        StringBuilder sb = new StringBuilder();
        for (String c : column) {
            sb.append(",").append(c);
        }
        String sql = "select _id" + sb.toString() + " from " + TABLE_NAME + " where " + where;
        //得到一个Cursor，这个将要放入适配器中
        return mHelper.executeSql(sql, null);
    }

    /**
     * 查询数据，？是占位符，用于和string数组搭配使用
     */
    public Cursor queryDataById(String id) {
        String sql = "select * from " + TABLE_NAME + " where _id=?";
        return mHelper.executeSql(sql, new String[]{id});
    }

    public void closeDB() {
        mHelper.closeDB();
    }

}
