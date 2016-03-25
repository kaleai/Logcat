package kale.debug.logcat;

import android.os.AsyncTask;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack Tony
 * @date 2015/12/4
 */
public class LogCat {

    @StringDef({"-s", "-f", "-r", "-n", "-v", "-c", "-d"})
    public @interface Options {
        String silent = "-s"; // -s              Set default filter to silent. Like specifying filterspec '*:s'
        String file = "-f"; // -f              <filename>   Log to file. Default to stdout
        String kbytes = "-r"; // -r              Rotate log every kbytes. (16 if unspecified). Requires -f 
        String count = "-n"; // -n              Sets max number of rotated logs to , default 
        String format = "-v"; // -v              Sets the log print format, where  is one of: brief process tag thread raw time threadtime long
        String clear = "-c"; // -c              clear (flush) the entire log and exit
        String dump = "-d"; // -d              dump the log and then exit (don't block) // 不会引起线程阻塞
    }
    
    @StringDef({"V", "D", "I", "W", "E", "F", "S"})
    public @interface Level {
        String Verbose = "V"; // Verbose (明细)  
        String Debug = "D"; // Debug (调试)
        String Info = "I"; // Info (信息)
        String Warn = "W"; // Warn (警告)
        String Error = "E"; // Error (错误)
        String Fatal = "F"; // Fatal (严重错误)
        String Assert= "S"; // Silent(Super all output) (最高的优先级, 前所未有的错误);  
    }

    ///////////////////////////////////////////////////////////////////////////
    // 好戏开场
    ///////////////////////////////////////////////////////////////////////////

    private final List<String> commandLine;
    
    public LogCat() {
        commandLine = new ArrayList<>();
        commandLine.add("logcat");
    }

    public LogCat options(@Options @NonNull String options) {
        commandLine.add(options);
        return this;
    }
    
    public LogCat recentLines(int lineSize) {
        commandLine.add("-t");
        commandLine.add(String.valueOf(lineSize));
        return this;
    }

    /**
     * @param tag log的tag
     */
    @CheckResult
    public LogCat filter(@NonNull String tag) {
        return filter(tag, "V"); // 默认显示所有信息
    }

    /**
     * logcat MyTag:I *:S 
     * @param tag    log的tag 不输入代表仅仅通过lev显示
     * @param lev    log的级别
     */
    @CheckResult
    public LogCat filter(@Nullable String tag, @Level @NonNull String lev) {
        if (!TextUtils.isEmpty(tag)) {
            commandLine.add(tag.trim() + ":" + lev);
            commandLine.add("*:S");
        } else {
            commandLine.add("*:" + lev);
        }
        return this;
    }

    @CheckResult
    public LogCat withTime() {
        commandLine.add("-v");
        commandLine.add("time");
        return this;
    }

    @CheckResult
    public LogCat clear() {
        commandLine.add("-c");
        return this;
    }

    public Process commit() {
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(commandLine.toArray(new String[this.commandLine.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        commandLine.clear();
        return exec;
    }

    public static void loadLog(final Process process, final LoadHandler handler) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                final StringBuilder builder = new StringBuilder();
                BufferedReader bufferedReader = null;    //将捕获内容转换为BufferedReader
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //  Runtime.runFinalizersOnExit(true);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        //清理日志，如果你这里做了sout，那么你输出的内容也会被记录，就会出现问题
                        line = handler.handLine(line);
                        if (!TextUtils.isEmpty(line)) {
                            builder.append(line);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return builder.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                handler.onComplete(s);
            }
        };
        task.execute();
    }

    public interface LoadHandler {

        @Nullable
        String handLine(String line);

        void onComplete(String str);
    }
    
}
