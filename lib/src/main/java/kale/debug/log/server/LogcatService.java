package kale.debug.log.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import kale.debug.log.Logcat;

/**
 * @author Kale
 * @date 2017/6/13
 */
public class LogcatService extends IntentService {

    private static final String TAG = "LogcatService";

    private static final String KEY_PORT = "key_port";

    private static int mPort = Logcat.LOGCAT_PORT;

    private static boolean mIsRunning;

    private ServerSocket mServerSocket;

    private RequestHandler mRequestHandler;

    public LogcatService() {
        this("LogcatService");
    }

    public LogcatService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIsRunning = true;
        mRequestHandler = new RequestHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mIsRunning = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            mPort = intent.getIntExtra(KEY_PORT, Logcat.LOGCAT_PORT);
        }

        try {
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                mRequestHandler.handle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    public static void start(Context context, int port) {
        Intent intent = new Intent(context, LogcatService.class);
        intent.putExtra(KEY_PORT, port);
        context.startService(intent);
    }

    public static void shutDown(Context context) {
        context.stopService(new Intent(context, LogcatService.class));
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    public static boolean isRunning() {
        return mIsRunning;
    }

    public static int getPort() {
        return mPort;
    }
}
