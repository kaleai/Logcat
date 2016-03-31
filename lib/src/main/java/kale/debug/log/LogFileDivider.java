package kale.debug.log;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Jack Tony
 * @date 2015/12/8
 */
public class LogFileDivider {

    private static final String LOG_FILE_END = ".log";

   /* 
    public static String saveFile(final String logData) {
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "log_temp.txt";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(logData.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }*/

    /**
     * Save the current logs to disk.
     */
    public static File saveFile(String logData) {
        File dir = getLogDir();
        if (dir == null) {
            return null;
        }
        
        FileWriter fileWriter = null;
        File output = null;
        try {
            output = new File(dir, getLogFileName());
            fileWriter = new FileWriter(output, true);
            fileWriter.write(logData);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return output;
    }

    public void cleanUp() {
        File dir = getLogDir();
        if (dir != null) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(LOG_FILE_END)) {
                        file.delete();
                    }
                }
            }
        }
    }

    private static File getLogDir() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static String getLogFileName() {
        String pattern = "%d%s";
        long currentDate = System.currentTimeMillis();

        return String.format(Locale.US, pattern, currentDate, LOG_FILE_END);
    }

    public static void sendEmail(final Activity activity, String address,final String title, final String subject, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] tos = {address};
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + filePath));
        intent.setType("image*//*");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "Choose Email Client");
        activity.startActivity(intent);
    }

    public static void shareFile(Activity activity,File file) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        activity.startActivity(sendIntent);
    }

}
