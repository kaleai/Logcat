package kale.debug.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import kale.debug.log.constant.Level;
import kale.debug.log.constant.Options;

/**
 * @author Jack Tony
 * @date 2015/12/4
 */
public class LogCatCmd {

    private static final List<String> DEFAULT_COMMAND = new ArrayList<>();

    static {
        DEFAULT_COMMAND.add("logcat");
    }

    private List<String> commandLine;

    private static LogCatCmd mInstance = null;

    public static LogCatCmd getInstance() {
        if (mInstance == null) {
            mInstance = new LogCatCmd();
        }
        return mInstance;
    }

    private LogCatCmd() {
        commandLine = new ArrayList<>(DEFAULT_COMMAND);
    }

    public LogCatCmd options(Options options) {
        commandLine.add(LogParser.parse(options));
        return this;
    }

    public LogCatCmd recentLines(@IntRange(from = 0) int lineSize) {
        commandLine.add("-t");
        commandLine.add(String.valueOf(lineSize));
        return this;
    }

    /**
     * @param tag log的tag
     */
    @CheckResult
    public LogCatCmd filter(@NonNull String tag) {
        return filter(tag, Level.VERBOSE); // 默认显示所有信息
    }

    /**
     * logcat Tag:I *:S
     *
     * @param tag log的tag 不输入代表仅仅通过lev做筛选
     * @param lev log的级别
     */
    @CheckResult
    public LogCatCmd filter(@Nullable String tag, Level lev) {
        String levStr = LogParser.parse(lev);

        if (!TextUtils.isEmpty(tag)) {
            commandLine.add(tag.trim() + ":" + levStr);
            commandLine.add("*:S");
        } else {
            commandLine.add("*:" + levStr);
        }
        return this;
    }

    @CheckResult
    public LogCatCmd withTime() {
        commandLine.add("-v");
        commandLine.add("time");
        return this;
    }

    @CheckResult
    public LogCatCmd clear() {
        commandLine.add("-c");
        return this;
    }

    public Process commit() {
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(commandLine.toArray(new String[this.commandLine.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            commandLine = new ArrayList<>(DEFAULT_COMMAND);
        }
        return exec;
    }

}
