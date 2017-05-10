package kale.debug.logcat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
                Logcat.jumpToLogcatActivity(MainActivity.this);
            }
        });

        Logcat.startLogCatServer(this);
        ((TextView) findViewById(R.id.ip_address_tv)).setText(NetworkUtils.getAddress(this));
    }

    private void printLog() {
        Log.v("ddd", "Just for test.");
        Log.d(TAG, "onCreate: kale");
        Log.i("kale", "Info message~");
        Log.w(TAG, "kale", new RuntimeException("exp"));
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
