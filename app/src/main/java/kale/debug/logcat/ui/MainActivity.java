package kale.debug.logcat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amitshekhar.DebugDB;

import kale.debug.log.Logcat;
import kale.debug.log.util.NetworkUtils;
import kale.debug.logcat.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        DebugDB.initialize(this);

        Log.e(TAG, "onCreate: first create");

        printLog();

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: click " + num);
                num++;
            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLog();
                int result = Log.d(TAG, "onClick: ");
                Logcat.jumpToLogcatActivity(MainActivity.this);
            }
        });

        String nullStr = null;
        try {
            nullStr.substring(0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.crash_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sss = null;

                sss.substring(0, 1);
            }
        });

        Logcat.startLogCatServer(this);
        ((TextView) findViewById(R.id.ip_address_tv)).setText(NetworkUtils.getWebLogcatAddress(this));
    }

    private void printLog() {
        Log.v("ddd", "Just for test.");
        Log.d(TAG, "onCreate: kale");
        Log.i("kale", "Info message~");
        Log.w(TAG, "kale", new RuntimeException("exp"));
        Log.w(TAG, "one\ntwo\nthree");
        Log.e(TAG, "Large Data:\n"
                + "Never give up,"
                + "Never lose hope."
                + "Always have faith,"
                + "It allows you to cope."
                + "Trying times will pass,"
                + "As they always do."
                + "Just have patience,"
                + "Your dreams will come true."
                + "So put on a smile,"
                + "You'll live through your pain."
                + "Know it will pass,"
                + "And strength you will gain.");
    }

}
