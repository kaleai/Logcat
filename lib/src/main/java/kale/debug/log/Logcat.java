package kale.debug.log;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kale.debug.log.server.LogcatService;
import kale.debug.log.ui.LogActivity;
import kale.debug.log.util.NetworkUtils;

/**
 * @author Kale
 * @date 2017/5/9
 */
public class Logcat {

    private static final String TAG = "Logcat";

    public static final int LOGCAT_PORT = 8819;

    public static void startLogCatServer(Context context) {
        startLogCatServer(context, LOGCAT_PORT);
    }

    public static void startLogCatServer(Context context, int port) {
        LogcatService.start(context, port);
        Log.d(TAG, NetworkUtils.getWebLogcatAddress(context, port));
    }

    public static void shutDownServer(Context context) {
        LogcatService.shutDown(context);
    }

    public static boolean isServerRunning() {
        return LogcatService.isRunning();
    }

    public static void jumpToLogcatActivity(Context context) {
        context.startActivity(new Intent(context, LogActivity.class));
    }

}
