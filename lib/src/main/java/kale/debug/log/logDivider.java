package kale.debug.log;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jack Tony
 * @date 2015/12/8
 */
public class logDivider {

    /**
     * @return 保存文件的路径
     */
    public static String saveFile(final String json) {
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "log_temp.txt";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void sendEmail(final Activity activity, final String userName, final String subject, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] tos = {"developer_kale@foxmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, userName);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + filePath));
        intent.setType("image*//*");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "Choose Email Client");
        activity.startActivity(intent);
    }
    
}
