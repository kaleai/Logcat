package kale.debug.logcat.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import kale.debug.logcat.R;
import kale.debug.logcat.db.LogcatDB;

/**
 * @author Kale
 * @date 2015/12/18
 */
public class LogDetailActivity extends AppCompatActivity {

    public static final String ID = "key_id";

    public static Intent withIntent(Activity activity, String id) {
        return new Intent(activity, LogDetailActivity.class)
                .putExtra(ID, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_detail_activity);
        if (getIntent() == null) {
            return;
        }
        TextView textView = (TextView) findViewById(R.id.log_detail_tv);

        StringBuilder sb = new StringBuilder();
        String id = getIntent().getStringExtra(ID);
        LogcatDB db = new LogcatDB(this);
        Cursor cursor = db.queryDataById(id);
        if (cursor != null) {
            String[] names = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                for (String name : names) {
                    sb.append(name)
                            .append("：\n")
                            .append(cursor.getString(cursor.getColumnIndex(name)))
                            .append("\n\n");
                }
            }
            cursor.close();
        }
        textView.setText(sb);
    }
}
