package kale.debug.log;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kale.debug.log.server.LogcatServer;
import kale.debug.log.ui.LogActivity;
import kale.debug.log.util.NetworkUtils;

/**
 * @author Kale
 * @date 2017/5/9
 */
public class Logcat {

    private static final String TAG = "Logcat";

    private static LogcatServer logcatServer;

    public static void startLogCatServer(Context context) {
        startLogCatServer(context, 8819);
    }

    public static void startLogCatServer(Context context, int port) {
        logcatServer = new LogcatServer(context, port);
        logcatServer.start();
        Log.d(TAG, NetworkUtils.getWebLogcatAddress(context, port));
    }

    public static void shutDownServer() {
        logcatServer.stop();
    }

    public static boolean isServerRunning() {
        return logcatServer != null && logcatServer.isRunning();
    }

    public static void jumpToLogcatActivity(Context context) {
        context.startActivity(new Intent(context, LogActivity.class));
    }
}
