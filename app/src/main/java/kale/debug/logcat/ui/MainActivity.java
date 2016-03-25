package kale.debug.logcat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import kale.debug.log.ui.LogActivity;
import kale.debug.logcat.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        printLog();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLog();

                startActivity(new Intent(MainActivity.this, LogActivity.class));
            }
        });

        startActivity(new Intent(this, LogActivity.class));
    }

    private void printLog() {
        Log.v("ddd", "Just for test.");
        Log.d(TAG, "onCreate: kale");
        Log.i("kale", "Info message~");
        Log.w(TAG, "kale", new RuntimeException("exp"));
        Log.e(TAG, "Large Data:"
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
                + "And strength you will gain ");
    }

}
